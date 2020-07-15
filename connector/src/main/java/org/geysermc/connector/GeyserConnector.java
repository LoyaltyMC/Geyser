/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.v407.Bedrock_v407;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.connector.common.AuthType;
import org.geysermc.connector.common.PlatformType;
import org.geysermc.connector.bootstrap.GeyserBootstrap;
import org.geysermc.connector.command.CommandManager;
import org.geysermc.connector.event.EventManager;
import org.geysermc.connector.configuration.GeyserConfiguration;
import org.geysermc.connector.event.events.geyser.GeyserStartEvent;
import org.geysermc.connector.event.events.network.BedrockCodecRegistryEvent;
import org.geysermc.connector.metrics.Metrics;
import org.geysermc.connector.network.ConnectorServerEventHandler;
import org.geysermc.connector.network.remote.RemoteServer;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.EntityIdentifierRegistry;
import org.geysermc.connector.network.translators.PacketTranslatorRegistry;
import org.geysermc.connector.network.translators.effect.EffectRegistry;
import org.geysermc.connector.network.translators.item.ItemRegistry;
import org.geysermc.connector.network.translators.item.ItemTranslator;
import org.geysermc.connector.network.translators.item.PotionMixRegistry;
import org.geysermc.connector.network.translators.sound.SoundRegistry;
import org.geysermc.connector.network.translators.sound.SoundRegistry;
import org.geysermc.connector.network.translators.world.WorldManager;
import org.geysermc.connector.utils.LanguageUtils;
import org.geysermc.connector.network.translators.world.block.BlockTranslator;
import org.geysermc.connector.network.translators.world.block.entity.BlockEntityTranslator;
import org.geysermc.connector.plugin.PluginManager;
import org.geysermc.connector.event.events.geyser.GeyserStopEvent;
import org.geysermc.connector.utils.DimensionUtils;
import org.geysermc.connector.utils.DockerCheck;
import org.geysermc.connector.utils.LocaleUtils;
import org.geysermc.connector.utils.SkinProvider;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class GeyserConnector {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

    public static final String NAME = "Geyser";
    public static final String VERSION = "DEV"; // A fallback for running in IDEs

    private final List<GeyserSession> players = new ArrayList<>();

    private static GeyserConnector instance;

    private RemoteServer remoteServer;
    @Setter
    private AuthType authType;

    private boolean shuttingDown = false;

    private final ScheduledExecutorService generalThreadPool;

    private BedrockServer bedrockServer;
    private PlatformType platformType;
    private GeyserBootstrap bootstrap;

    private final GeyserLogger logger;
    private final GeyserConfiguration config;

    private GeyserEdition edition;

    private final EventManager eventManager;
    private final PluginManager pluginManager;

    private final List<String> registeredPluginChannels = new ArrayList<>();

    private Metrics metrics;

    private GeyserConnector(PlatformType platformType, GeyserBootstrap bootstrap) throws GeyserConnectorException {
        long startupTime = System.currentTimeMillis();

        instance = this;

        this.bootstrap = bootstrap;

        logger = bootstrap.getGeyserLogger();
        config = bootstrap.getGeyserConfig();

        this.platformType = platformType;

        logger.info("******************************************");
        logger.info("");
        logger.info(LanguageUtils.getLocaleStringLog("geyser.core.load", NAME, VERSION));
        logger.info("");
        logger.info("******************************************");

        this.generalThreadPool = Executors.newScheduledThreadPool(config.getGeneralThreadPool());

        logger.setDebug(config.isDebugMode());

        // Register Editions
        GeyserEdition.registerEdition("bedrock", org.geysermc.connector.edition.mcpe.Edition.class);
        GeyserEdition.registerEdition("education",  org.geysermc.connector.edition.mcee.Edition.class);

        this.eventManager = new EventManager(this);
        this.pluginManager = new PluginManager(this, bootstrap.getConfigFolder().resolve("plugins").toFile());

        // Set Codec
//        BEDROCK_PACKET_CODEC = eventManager.triggerEvent(new BedrockCodecRegistryEvent(Bedrock_v407.V407_CODEC)).getEvent().getCodec();

        try {
            this.edition = GeyserEdition.create(this, config.getBedrock().getEdition());
        } catch (GeyserEdition.InvalidEditionException e) {
            throw new GeyserConnectorException(e.getMessage(), e.getCause());
        }

        // Initialize translators and registries
        BlockTranslator.init();
        EffectRegistry.init();
        EntityIdentifierRegistry.init();
        LocaleUtils.init();
        ItemRegistry.init();
        ItemTranslator.init();
        LocaleUtils.init();
        PotionMixRegistry.init();
        SoundRegistry.init();
        SkinProvider.init();

        if (platformType != PlatformType.STANDALONE) {
            DockerCheck.check(bootstrap);
        }

        remoteServer = new RemoteServer(config.getRemote().getAddress(), config.getRemote().getPort());
        authType = AuthType.getByName(config.getRemote().getAuthType());

        if (config.isAboveBedrockNetherBuilding())
            DimensionUtils.changeBedrockNetherId(); // Apply End dimension ID workaround to Nether

        bedrockServer = new BedrockServer(new InetSocketAddress(config.getBedrock().getAddress(), config.getBedrock().getPort()));
        bedrockServer.setHandler(new ConnectorServerEventHandler(this));
        bedrockServer.bind().whenComplete((avoid, throwable) -> {
            if (throwable == null) {
                logger.info(LanguageUtils.getLocaleStringLog("geyser.core.start", config.getBedrock().getAddress(), String.valueOf(config.getBedrock().getPort())));
            } else {
                logger.severe(LanguageUtils.getLocaleStringLog("geyser.core.fail", config.getBedrock().getAddress(), config.getBedrock().getPort()));
                throwable.printStackTrace();
            }
        }).join();

        if (config.getMetrics().isEnabled()) {
            metrics = new Metrics(this, "GeyserMC", config.getMetrics().getUniqueId(), false, java.util.logging.Logger.getLogger(""));
            metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> 1));
            metrics.addCustomChart(new Metrics.SingleLineChart("players", players::size));
            metrics.addCustomChart(new Metrics.SimplePie("authMode", authType.name()::toLowerCase));
            metrics.addCustomChart(new Metrics.SimplePie("platform", platformType::getPlatformName));
            metrics.addCustomChart(new Metrics.SimplePie("edition", () -> config.getBedrock().getEdition()));
        }

        // Enable Plugins
        pluginManager.enablePlugins();

        // Trigger GeyserStart Events
        eventManager.triggerEvent(new GeyserStartEvent());

        boolean isGui = false;
        // This will check if we are in standalone and get the 'useGui' variable from there
        if (platformType == PlatformType.STANDALONE) {
            try {
                Class<?> cls = Class.forName("org.geysermc.platform.standalone.GeyserStandaloneBootstrap");
                isGui = (boolean) cls.getMethod("isUseGui").invoke(cls.cast(bootstrap));
            } catch (Exception e) {
                logger.debug("Failed detecting if standalone is using a GUI; if this is a GeyserConnect instance this can be safely ignored.");
            }
        }

        // Enable Plugins
        pluginManager.enablePlugins();

        double completeTime = (System.currentTimeMillis() - startupTime) / 1000D;
        String message = LanguageUtils.getLocaleStringLog("geyser.core.finish.done", new DecimalFormat("#.###").format(completeTime)) + " ";
        if (isGui) {
            message += LanguageUtils.getLocaleStringLog("geyser.core.finish.gui");
        } else {
            message += LanguageUtils.getLocaleStringLog("geyser.core.finish.console");
        }
        logger.info(message);
    }

    public void shutdown() {
        bootstrap.getGeyserLogger().info(LanguageUtils.getLocaleStringLog("geyser.core.shutdown"));
        shuttingDown = true;

        // Trigger GeyserStop Events
        eventManager.triggerEvent(new GeyserStopEvent());

        // Disable Plugins
        pluginManager.disablePlugins();

        if (players.size() >= 1) {
            bootstrap.getGeyserLogger().info(LanguageUtils.getLocaleStringLog("geyser.core.shutdown.kick.log", players.size()));

            // Make a copy to prevent ConcurrentModificationException
            final List<GeyserSession> tmpPlayers = new ArrayList<>(players);
            for (GeyserSession playerSession : tmpPlayers) {
                playerSession.disconnect(LanguageUtils.getPlayerLocaleString("geyser.core.shutdown.kick.message", playerSession.getClientData().getLanguageCode()));
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    // Simulate a long-running Job
                    try {
                        while (true) {
                            if (players.size() == 0) {
                                return;
                            }

                            TimeUnit.MILLISECONDS.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
            });

            // Block and wait for the future to complete
            try {
                future.get();
                bootstrap.getGeyserLogger().info(LanguageUtils.getLocaleStringLog("geyser.core.shutdown.kick.done"));
            } catch (Exception e) {
                // Quietly fail
            }
        }

        generalThreadPool.shutdown();
        bedrockServer.close();
        players.clear();
        remoteServer = null;
        authType = null;
        this.getCommandManager().getCommands().clear();

        bootstrap.getGeyserLogger().info(LanguageUtils.getLocaleStringLog("geyser.core.shutdown.done"));
    }

    public void addPlayer(GeyserSession player) {
        players.add(player);
    }

    public void removePlayer(GeyserSession player) {
        players.remove(player);
    }

    public static GeyserConnector start(PlatformType platformType, GeyserBootstrap bootstrap) throws GeyserConnectorException {
        return new GeyserConnector(platformType, bootstrap);
    }

    public void reload() {
        shutdown();
        bootstrap.onEnable();
    }

    public GeyserLogger getLogger() {
        return bootstrap.getGeyserLogger();
    }

    public GeyserConfiguration getConfig() {
        return bootstrap.getGeyserConfig();
    }

    public CommandManager getCommandManager() {
        return bootstrap.getGeyserCommandManager();
    }

    public WorldManager getWorldManager() {
        return bootstrap.getWorldManager();
    }

    /**
     * Register a Plugin Channel
     *
     * This will maintain what channels are registered and ensure new connections and existing connections
     * are registered correctly
     */
    public void registerPluginChannel(String channel) {
        if (registeredPluginChannels.contains(channel)) {
            return;
        }

        registeredPluginChannels.add(channel);
        for ( GeyserSession session : getPlayers()) {
            session.registerPluginChannel(channel);
        }
    }

    /**
     * Unregister a Plugin Channel
     *
     * This will maintain what channels are registered and ensure new connections and existing connections
     * are registered correctly
     */
    public void unregisterPluginChannel(String channel) {
        if (!registeredPluginChannels.contains(channel)) {
            return;
        }

        registeredPluginChannels.remove(channel);
        for ( GeyserSession session : getPlayers()  ) {
            session.unregisterPluginChannel(channel);
        }
    }

    public static GeyserConnector getInstance() {
        return instance;
    }

    public static class GeyserConnectorException extends Exception {
        public GeyserConnectorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
