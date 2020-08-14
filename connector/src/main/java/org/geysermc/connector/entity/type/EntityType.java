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

package org.geysermc.connector.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.geysermc.connector.entity.*;
import java.util.EnumMap;
import java.util.Map;

@Getter
public enum EntityType {

    CHICKEN,
    COW,
    PIG,
    SHEEP,
    WOLF,
    VILLAGER,
    MOOSHROOM,
    SQUID,
    RABBIT,
    BAT,
    IRON_GOLEM,
    SNOW_GOLEM,
    OCELOT,
    HORSE,
    DONKEY,
    MULE,
    SKELETON_HORSE,
    ZOMBIE_HORSE,
    POLAR_BEAR,
    LLAMA,
    TRADER_LLAMA,
    PARROT,
    DOLPHIN,
    ZOMBIE,
    GIANT,
    CREEPER,
    SKELETON,
    SPIDER,
    ZOMBIFIED_PIGLIN,
    SLIME,
    ENDERMAN,
    SILVERFISH,
    CAVE_SPIDER,
    GHAST,
    MAGMA_CUBE,
    BLAZE,
    ZOMBIE_VILLAGER,
    WITCH,
    STRAY,
    HUSK,
    WITHER_SKELETON,
    GUARDIAN,
    ELDER_GUARDIAN,
    NPC,
    WITHER,
    ENDER_DRAGON,
    SHULKER,
    ENDERMITE,
    AGENT,
    VINDICATOR,
    PILLAGER,
    WANDERING_TRADER,
    PHANTOM,
    RAVAGER,

    ARMOR_STAND(ArmorStandEntity.class, 61, 1.975f, 0.5f),
    TRIPOD_CAMERA(Entity.class, 62, 0f),
    PLAYER(PlayerEntity.class, 63, 1.8f, 0.6f, 0.6f, 1.62f),
    ITEM(ItemEntity.class, 64, 0.25f, 0.25f),
    PRIMED_TNT(TNTEntity.class, 65, 0.98f, 0.98f, 0.98f, 0f, "minecraft:tnt"),
    FALLING_BLOCK(FallingBlockEntity.class, 66, 0.98f, 0.98f),
    MOVING_BLOCK(Entity.class, 67, 0f),
    THROWN_EXP_BOTTLE(ThrowableEntity.class, 68, 0.25f, 0.25f, 0f, 0f, "minecraft:xp_bottle"),
    EXPERIENCE_ORB(ExpOrbEntity.class, 69, 0f, 0f, 0f, 0f, "minecraft:xp_orb"),
    EYE_OF_ENDER(Entity.class, 70, 0.25f, 0.25f, 0f, 0f, "minecraft:eye_of_ender_signal"),
    END_CRYSTAL(EnderCrystalEntity.class, 71, 2.0f, 2.0f, 2.0f, 0f, "minecraft:ender_crystal"),
    FIREWORK_ROCKET(FireworkEntity.class, 72, 0.25f, 0.25f, 0.25f, 0f, "minecraft:fireworks_rocket"),
    TRIDENT(TridentEntity.class, 73, 0f, 0f, 0f, 0f, "minecraft:thrown_trident"),
    TURTLE(AnimalEntity.class, 74, 0.4f, 1.2f),
    CAT(CatEntity.class, 75, 0.35f, 0.3f),
    SHULKER_BULLET(Entity.class, 76, 0.3125f),
    FISHING_BOBBER(FishingHookEntity.class, 77, 0f, 0f, 0f, 0f, "minecraft:fishing_hook"),
    CHALKBOARD(Entity.class, 78, 0f),
    DRAGON_FIREBALL(ItemedFireballEntity.class, 79, 1.0f),
    ARROW(TippedArrowEntity.class, 80, 0.25f, 0.25f),
    SPECTRAL_ARROW(AbstractArrowEntity.class, 80, 0.25f, 0.25f, 0.25f, 0f, "minecraft:arrow"),
    SNOWBALL(ThrowableEntity.class, 81, 0.25f),
    THROWN_EGG(ThrowableEntity.class, 82, 0.25f, 0.25f, 0.25f, 0f, "minecraft:egg"),
    PAINTING(PaintingEntity.class, 83, 0f),
    MINECART(MinecartEntity.class, 84, 0.7f, 0.98f, 0.98f, 0.35f),
    FIREBALL(ItemedFireballEntity.class, 85, 1.0f),
    THROWN_POTION(ThrowableEntity.class, 86, 0.25f, 0.25f, 0.25f, 0f, "minecraft:splash_potion"),
    THROWN_ENDERPEARL(ThrowableEntity.class, 87, 0.25f, 0.25f, 0.25f, 0f, "minecraft:ender_pearl"),
    LEASH_KNOT(LeashKnotEntity.class, 88, 0.5f, 0.375f),
    WITHER_SKULL(Entity.class, 89, 0.3125f),
    BOAT(BoatEntity.class, 90, 0.7f, 1.6f, 1.6f, 0.35f),
    WITHER_SKULL_DANGEROUS(Entity.class, 91, 0f),
    LIGHTNING_BOLT(Entity.class, 93, 0f),
    SMALL_FIREBALL(ItemedFireballEntity.class, 94, 0.3125f),
    AREA_EFFECT_CLOUD(AreaEffectCloudEntity.class, 95, 0.5f, 1.0f),
    MINECART_HOPPER(MinecartEntity.class, 96, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:hopper_minecart"),
    MINECART_TNT(MinecartEntity.class, 97, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:tnt_minecart"),
    MINECART_CHEST(MinecartEntity.class, 98, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:chest_minecart"),
    MINECART_FURNACE(FurnaceMinecartEntity.class, 98, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:minecart"),
    MINECART_SPAWNER(SpawnerMinecartEntity.class, 98, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:minecart"),
    MINECART_COMMAND_BLOCK(MinecartEntity.class, 100, 0.7f, 0.98f, 0.98f, 0.35f, "minecraft:command_block_minecart"),
    LINGERING_POTION(ThrowableEntity.class, 101, 0f),
    LLAMA_SPIT(Entity.class, 102, 0.25f),
    EVOKER_FANGS(Entity.class, 103, 0.8f, 0.5f, 0.5f, 0f, "minecraft:evocation_fang"),
    EVOKER(SpellcasterIllagerEntity.class, 104, 1.95f, 0.6f, 0.6f, 0f, "minecraft:evocation_illager"),
    VEX(MonsterEntity.class, 105, 0.8f, 0.4f),
    ICE_BOMB(Entity.class, 106, 0f),
    BALLOON(Entity.class, 107, 0f), //TODO
    PUFFERFISH(PufferFishEntity.class, 108, 0.7f, 0.7f),
    SALMON(AbstractFishEntity.class, 109, 0.5f, 0.7f),
    DROWNED(ZombieEntity.class, 110, 1.95f, 0.6f),
    TROPICAL_FISH(TropicalFishEntity.class, 111, 0.6f, 0.6f, 0f, 0f, "minecraft:tropicalfish"),
    COD(AbstractFishEntity.class, 112, 0.25f, 0.5f),
    PANDA(PandaEntity.class, 113, 1.25f, 1.125f, 1.825f),
    FOX(FoxEntity.class, 121, 0.5f, 1.25f),
    BEE(BeeEntity.class, 122, 0.6f, 0.6f),
    STRIDER(StriderEntity.class, 125, 1.7f, 0.9f, 0f, 0f, "minecraft:strider"),
    HOGLIN(AnimalEntity.class, 124, 1.4f, 1.3965f, 1.3965f, 0f, "minecraft:hoglin"),
    ZOGLIN(ZoglinEntity.class, 126, 1.4f, 1.3965f, 1.3965f, 0f, "minecraft:zoglin"),
    PIGLIN(PiglinEntity.class, 123, 1.95f, 0.6f, 0.6f, 0f, "minecraft:piglin"),
    PIGLIN_BRUTE(BasePiglinEntity.class, 127, 1.95f, 0.6f, 0.6f, 0f, "minecraft:piglin_brute"),

    private static final Map<EntityType, Data> VALUES = new EnumMap<>(EntityType.class);
    public static Register REGISTER = new Register();
    
    public static class Register {
        public Register entityType(EntityType entityType, Class<? extends Entity> entityClass, int type, float height, float width,
                                   float length, float offset, String identifier) {
            VALUES.put(entityType, new Data(entityClass, type, height, width, length, offset+ 0.00001f, identifier));
            return this;
        }
        public Register entityType(EntityType entityType, Class<? extends Entity> entityClass, int type, float height, float width,
                                   float length, float offset) {
            return entityType(entityType, entityClass, type, height, width, length, offset, "minecraft:" + entityType.name().toLowerCase());
        }
        public Register entityType(EntityType entityType, Class<? extends Entity> entityClass, int type, float height, float width, float length) {
            return entityType(entityType, entityClass, type, height, width, length, 0f);
        }
        public Register entityType(EntityType entityType, Class<? extends Entity> entityClass, int type, float height, float width) {
            return entityType(entityType, entityClass, type, height, width, width);
        }
        public Register entityType(EntityType entityType, Class<? extends Entity> entityClass, int type, float height) {
            return entityType(entityType, entityClass, type, height, height);
        }
    }

    public Class<? extends Entity> getEntityClass() {
        return VALUES.get(this).getEntityClass();
    }

    public int getType() {
        return VALUES.get(this).getType();
    }

    public float getHeight() {
        return VALUES.get(this).getHeight();
    }

    public float getWidth() {
        return VALUES.get(this).getWidth();
    }

    public float getLength() {
        return VALUES.get(this).getLength();
    }

    public float getOffset() {
        return VALUES.get(this).getOffset();
    }

    public String getIdentifier() {
        return VALUES.get(this).getIdentifier();
    }

    public static EntityType getFromIdentifier(String identifier) {
        return VALUES.entrySet().stream()
                .filter(e -> e.getValue().getIdentifier().equals(identifier))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private final Class<? extends Entity> entityClass;
        private final int type;
        private final float height;
        private final float width;
        private final float length;
        private final float offset;
        private final String identifier;
    }

}
