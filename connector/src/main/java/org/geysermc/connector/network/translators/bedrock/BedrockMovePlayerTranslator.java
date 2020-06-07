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

package org.geysermc.connector.network.translators.bedrock;

import com.nukkitx.math.vector.Vector3d;
import org.geysermc.common.ChatColor;
import org.geysermc.connector.entity.Entity;
import org.geysermc.connector.entity.PlayerEntity;
import org.geysermc.connector.entity.type.EntityType;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.network.translators.PacketTranslator;
import org.geysermc.connector.network.translators.Translator;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;

import org.geysermc.connector.network.translators.world.collision.translators.*;
import org.geysermc.connector.utils.BoundingBox;

import org.geysermc.connector.network.translators.world.collision.CollisionTranslator;

@Translator(packet = MovePlayerPacket.class)
public class BedrockMovePlayerTranslator extends PacketTranslator<MovePlayerPacket> {

    @Override
    public void translate(MovePlayerPacket packet, GeyserSession session) {
        PlayerEntity entity = session.getPlayerEntity();
        if (entity == null || !session.isSpawned() || session.getPendingDimSwitches().get() > 0) return;

        if (!session.getUpstream().isInitialized()) {
            MoveEntityAbsolutePacket moveEntityBack = new MoveEntityAbsolutePacket();
            moveEntityBack.setRuntimeEntityId(entity.getGeyserId());
            moveEntityBack.setPosition(entity.getPosition());
            moveEntityBack.setRotation(entity.getBedrockRotation());
            moveEntityBack.setTeleported(true);
            moveEntityBack.setOnGround(true);
            session.sendUpstreamPacketImmediately(moveEntityBack);
            return;
        }

        // We need to parse the float as a string since casting a float to a double causes us to
        // lose precision and thus, causes players to get stuck when walking near walls
        double javaY = packet.getPosition().getY() - EntityType.PLAYER.getOffset();

        Vector3d position = Vector3d.from(Double.parseDouble(Float.toString(packet.getPosition().getX())), javaY,
                Double.parseDouble(Float.toString(packet.getPosition().getZ())));

        if (session.getConnector().getConfig().isCacheChunks()) {
            // With chunk caching, we can do some proper collision checks

            // For now we only check collision when the player is on the ground to improve performance
            // This will cause issues if the player flies into a stair in Creative (quite unlikely)

            if (true) { // packet.isOnGround()) {

                BoundingBox playerCollision = new BoundingBox(position.getX(), position.getY() + 0.9, position.getZ(), 0.8, 1.8, 0.8);

                // Loop through all blocks that could collide with the player
                int minCollisionX = (int) Math.floor(position.getX() - 0.4);
                int maxCollisionX = (int) Math.floor(position.getX() + 0.4);

                // Y extends 0.5 blocks down because of fence hitboxes
                int minCollisionY = (int) Math.floor(position.getY() - 0.5);

                // TODO: change comment
                // Hitbox height is currently set to 0.5 to improve performance, as only blocks below the player need checking
                // Any lower seems to cause issues
                int maxCollisionY = (int) Math.floor(position.getY() + 1.8);

                int minCollisionZ = (int) Math.floor(position.getZ() - 0.4);
                int maxCollisionZ = (int) Math.floor(position.getZ() + 0.4);

                BlockCollision blockCollision;

                for (int y = minCollisionY; y < maxCollisionY + 1; y++) {
                    for (int x = minCollisionX; x < maxCollisionX + 1; x++) {
                        for (int z = minCollisionZ; z < maxCollisionZ + 1; z++) {
                            blockCollision = CollisionTranslator.getCollision(
                                    session.getConnector().getWorldManager().getBlockAt(session, x, y, z),
                                    x, y, z
                            );

                            if (blockCollision != null) {
                                blockCollision.correctPosition(playerCollision);
                                playerCollision.translate(0, 0.1, 0); // Hack to not check y
                                if (blockCollision.checkIntersection(playerCollision)) {
                                    System.out.println("Collision!");
                                    System.out.println(playerCollision.getMiddleX());
                                    // return;
                                }
                                playerCollision.translate(0, -0.1, 0); // Hack to not check y
                            }
                        }
                    }
                }
                position = Vector3d.from(playerCollision.getMiddleX(), playerCollision.getMiddleY() - 0.9,
                        playerCollision.getMiddleZ());
            }
        } else {
            // When chunk caching is off, we have to rely on this
            // It rounds the Y position up to the nearest 0.5
            // This snaps players to snap to the top of stairs and slabs like on Java Edition
            // However, it causes issues such as the player floating on carpets
            if (packet.isOnGround()) javaY = Math.ceil(javaY * 2) / 2;
            position = position.up(javaY - position.getY());
        }

        if (!session.confirmTeleport(position)){
            return;
        }

        if (!isValidMove(session, packet.getMode(), entity.getPosition(), packet.getPosition())) {
            session.getConnector().getLogger().debug("Recalculating position...");
            recalculatePosition(session, entity, entity.getPosition());
            return;
        }

        ClientPlayerPositionRotationPacket playerPositionRotationPacket = new ClientPlayerPositionRotationPacket(
                packet.isOnGround(), position.getX(), position.getY(), position.getZ(), packet.getRotation().getY(), packet.getRotation().getX()
        );

        // head yaw, pitch, head yaw
        Vector3f rotation = Vector3f.from(packet.getRotation().getY(), packet.getRotation().getX(), packet.getRotation().getY());
        entity.setPosition(packet.getPosition().sub(0, EntityType.PLAYER.getOffset(), 0));
        entity.setRotation(rotation);
        entity.setOnGround(packet.isOnGround());

        /*
        boolean colliding = false;
        Position position = new Position((int) packet.getPosition().getX(),
                (int) Math.ceil(javaY * 2) / 2, (int) packet.getPosition().getZ());

        BlockEntry block = session.getChunkCache().getBlockAt(position);
        if (!block.getJavaIdentifier().contains("air"))
            colliding = true;

        if (!colliding)
         */
        session.sendDownstreamPacket(playerPositionRotationPacket);
    }

    public boolean isValidMove(GeyserSession session, MovePlayerPacket.Mode mode, Vector3f currentPosition, Vector3f newPosition) {
        if (mode != MovePlayerPacket.Mode.NORMAL)
            return true;

        double xRange = newPosition.getX() - currentPosition.getX();
        double yRange = newPosition.getY() - currentPosition.getY();
        double zRange = newPosition.getZ() - currentPosition.getZ();

        if (xRange < 0)
            xRange = -xRange;
        if (yRange < 0)
            yRange = -yRange;
        if (zRange < 0)
            zRange = -zRange;

        if ((xRange + yRange + zRange) > 100) {
            session.getConnector().getLogger().debug(ChatColor.RED + session.getName() + " moved too quickly." +
                    " current position: " + currentPosition + ", new position: " + newPosition);

            return false;
        }

        return true;
    }

    public void recalculatePosition(GeyserSession session, Entity entity, Vector3f currentPosition) {
        // Gravity might need to be reset...
        SetEntityDataPacket entityDataPacket = new SetEntityDataPacket();
        entityDataPacket.setRuntimeEntityId(entity.getGeyserId());
        entityDataPacket.getMetadata().putAll(entity.getMetadata());
        session.sendUpstreamPacket(entityDataPacket);

        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(entity.getGeyserId());
        movePlayerPacket.setPosition(entity.getPosition());
        movePlayerPacket.setRotation(entity.getBedrockRotation());
        movePlayerPacket.setMode(MovePlayerPacket.Mode.RESET);
        session.sendUpstreamPacket(movePlayerPacket);
    }
}
