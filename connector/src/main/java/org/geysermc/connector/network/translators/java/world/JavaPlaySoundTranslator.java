package org.geysermc.connector.network.translators.java.world;


import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlaySoundPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.PlaySoundPacket;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.PacketTranslator;
import org.geysermc.connector.utils.SoundUtils;
import org.slf4j.LoggerFactory;

public class JavaPlaySoundTranslator extends PacketTranslator<ServerPlaySoundPacket> {

    @Override
    public void translate(ServerPlaySoundPacket packet, GeyserSession session) {
        if(SoundUtils.hasIdentifier(packet.getSound().toString())){
            PlaySoundPacket soundPacket = new PlaySoundPacket();

            soundPacket.setSound(SoundUtils.getIdentifier(packet.getSound().toString()));
            soundPacket.setPitch(packet.getPitch());
            soundPacket.setVolume(packet.getVolume());
            soundPacket.setPosition(Vector3f.from(packet.getX(), packet.getY(), packet.getZ()));

            session.getUpstream().sendPacket(soundPacket);
        }else {
            LoggerFactory.getLogger(this.getClass()).debug("No sound mapping for " + packet.getSound().toString());
        }
    }

}
