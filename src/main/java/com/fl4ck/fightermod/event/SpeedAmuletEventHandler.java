package com.fl4ck.fightermod.event;

import com.fl4ck.fightermod.FighterMod;
import com.fl4ck.fightermod.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = FighterMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpeedAmuletEventHandler {

    private static final float HUNGER_COST_PERCENTAGE = 1; //отнимется голода
    private static final int EFFECT_DURATION = 100; //время эффекта в тиках
    private static final int SPEED_LEVEL = 1; //уровень скорости
    private static final int COOLDOWN_TICKS = 200; //задержка в тиках
    private static final int DURABILITY_COST = 1; //отнимается прочности

    private static final Map<UUID, Long> lastActivationTime = new HashMap<>();
    private static final Map<UUID, Boolean> wasMoving = new HashMap<>();
    private static final Map<UUID, Boolean> effectActive = new HashMap<>();
    private static final Map<UUID, Boolean> hadAmuletLastTick = new HashMap<>(); // Новый мап для отслеживания

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            UUID playerId = player.getUUID();

            // Проверяем, держит ли игрок амулет скорости в этой левой руке
            boolean hasAmuletNow = isHoldingSpeedAmulet(player);
            boolean hadAmuletLast = hadAmuletLastTick.getOrDefault(playerId, false);

            // Если раньше был амулет, а теперь нет - СРОЧНО СНИМАЕМ ЭФФЕКТ
            if (hadAmuletLast && !hasAmuletNow) {
                removeSpeedEffect(player);
                effectActive.remove(playerId);
                lastActivationTime.remove(playerId);
                wasMoving.remove(playerId);
            }

            // Обновляем состояние на следующий тик
            hadAmuletLastTick.put(playerId, hasAmuletNow);

            // Если сейчас есть амулет, обрабатываем эффект
            if (hasAmuletNow) {
                handleSpeedAmuletEffect(player);
            } else {
                // Если эффект все еще активен, снимаем его
                if (effectActive.getOrDefault(playerId, false)) {
                    removeSpeedEffect(player);
                    effectActive.remove(playerId);
                }
            }
        }
    }
    //если игрок прыгнет
    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Проверяем, что игрок держит именно амулет скорости
            if (isHoldingSpeedAmulet(player)) {
                checkAndApplyEffect(player);
            }
        }
    }
    //проверка на наличие амулета скорости
    private static boolean isHoldingSpeedAmulet(Player player) {
        ItemStack offhandItem = player.getOffhandItem();
        return !offhandItem.isEmpty() && offhandItem.getItem() == ModItems.SPEED_AMULET.get();
    }

    private static void handleSpeedAmuletEffect(Player player) {
        if (player.level().isClientSide()) return;

        ItemStack offhandItem = player.getOffhandItem();
        UUID playerId = player.getUUID();

        // Проверяем, что амулет все еще в руке (двойная проверка)
        if (offhandItem.getItem() != ModItems.SPEED_AMULET.get()) {
            removeSpeedEffect(player);
            effectActive.remove(playerId);
            return;
        }

        boolean isMoving = isPlayerMoving(player);
        wasMoving.put(playerId, isMoving);

        // Проверяем прочность амулета
        if (offhandItem.getDamageValue() >= offhandItem.getMaxDamage()) {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§cАмулет скорости сломан!"),
                        true
                );
            }
            removeSpeedEffect(player);
            effectActive.remove(playerId);
            return;
        }

        // Проверяем кулдаун
        long currentTime = player.level().getGameTime();
        Long lastTime = lastActivationTime.get(playerId);

        // Если игрок двигается и эффект еще не активен
        if (isMoving && !effectActive.getOrDefault(playerId, false)) {
            // Проверяем кулдаун только при попытке активации
            if (lastTime == null || currentTime - lastTime >= COOLDOWN_TICKS) {
                checkAndApplyEffect(player);
            }
        }

        // Если игрок перестал двигаться, снимаем эффект
        if (!isMoving && effectActive.getOrDefault(playerId, false)) {
            removeSpeedEffect(player);
            effectActive.put(playerId, false);
        }
    }
    //чек эффекта
    private static void checkAndApplyEffect(Player player) {
        ItemStack offhandItem = player.getOffhandItem();
        UUID playerId = player.getUUID();

        // Проверяем, что это действительно амулет скорости
        if (offhandItem.getItem() != ModItems.SPEED_AMULET.get()) {
            return;
        }

        int currentFood = player.getFoodData().getFoodLevel();
        int halfFood = (int) (HUNGER_COST_PERCENTAGE);

        if (currentFood >= halfFood || player.isCreative()) {

            if (!player.isCreative()) {
                int newFoodLevel = currentFood - halfFood;
                player.getFoodData().setFoodLevel(Math.max(0, newFoodLevel));

                float saturation = player.getFoodData().getSaturationLevel();
                player.getFoodData().setSaturation(Math.max(0, saturation - 5.0f));
            }

            if (!player.isCreative()) {
                if (player.level() instanceof ServerLevel serverLevel && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    offhandItem.hurtAndBreak(DURABILITY_COST, serverLevel, serverPlayer, (item) -> {
                        serverPlayer.onEquippedItemBroken(item, net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                    });
                }
            }

            applySpeedEffect(player);
            lastActivationTime.put(playerId, player.level().getGameTime());
            effectActive.put(playerId, true);

            spawnSpeedEffects(player.level(), player);

            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE,
                    SoundSource.PLAYERS,
                    0.5f,
                    1.5f
            );

            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH,
                    SoundSource.PLAYERS,
                    0.3f,
                    1.2f
            );
        } else {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§cНедостаточно голода для активации амулета скорости! (Нужно " + halfFood + ")"),
                        true
                );
            }
        }
    }
    //добавление эффекта
    private static void applySpeedEffect(Player player) {
        // Сначала снимаем старый эффект, если есть
        player.removeEffect(MobEffects.MOVEMENT_SPEED);

        // Добавляем новый
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                EFFECT_DURATION,
                SPEED_LEVEL,
                false,
                true,
                true
        ));
    }
    //удаление эффекта
    private static void removeSpeedEffect(Player player) {
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
    }

    private static boolean isPlayerMoving(Player player) {
        double deltaX = player.getX() - player.xOld;
        double deltaZ = player.getZ() - player.zOld;

        double horizontalSpeed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        boolean isMovingInput = player.zza != 0 || player.xxa != 0;
        boolean isSprinting = player.isSprinting();

        return horizontalSpeed > 0.001 || isMovingInput || isSprinting;
    }
    //эффекты
    private static void spawnSpeedEffects(net.minecraft.world.level.Level level, Player player) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 pos = player.position().add(0, player.getBbHeight() * 0.5, 0);

        for (int i = 0; i < 20; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 1.5;
            double dy = serverLevel.random.nextDouble() * 0.5;
            double dz = (serverLevel.random.nextDouble() - 0.5) * 1.5;

            serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    pos.x + dx,
                    pos.y + dy,
                    pos.z + dz,
                    1,
                    0, 0.1, 0,
                    0.05
            );
        }

        for (int i = 0; i < 10; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 1.0;
            double dz = (serverLevel.random.nextDouble() - 0.5) * 1.0;

            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x + dx,
                    pos.y + 0.1,
                    pos.z + dz,
                    1,
                    0, 0.1, 0,
                    0
            );
        }

        for (int i = 0; i < 15; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 1.2;
            double dz = (serverLevel.random.nextDouble() - 0.5) * 1.2;

            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    pos.x + dx,
                    pos.y,
                    pos.z + dz,
                    1,
                    0, 0.05, 0,
                    0.01
            );
        }
    }
}