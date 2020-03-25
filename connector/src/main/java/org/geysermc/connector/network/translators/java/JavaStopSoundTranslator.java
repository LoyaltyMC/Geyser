package org.geysermc.connector.network.translators.java;


import com.github.steveice10.mc.protocol.packet.ingame.server.ServerStopSoundPacket;
import com.nukkitx.protocol.bedrock.packet.StopSoundPacket;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.PacketTranslator;
import org.geysermc.connector.utils.SoundUtils;
import org.slf4j.LoggerFactory;

public class JavaStopSoundTranslator extends PacketTranslator<ServerStopSoundPacket> {

    @Override
    public void translate(ServerStopSoundPacket packet, GeyserSession session) {
        if(SoundUtils.hasIdentifier(packet.getSound().toString())){
            StopSoundPacket stopSoundPacket = new StopSoundPacket();

            stopSoundPacket.setSoundName(packet.getSound().toString());

            session.getUpstream().sendPacket(stopSoundPacket);
        }else {
            LoggerFactory.getLogger(this.getClass()).debug("No sound mapping for " + packet.getSound().toString());
        }
    }

}
