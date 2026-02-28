package com.fl4ck.fightermod.event;

import com.fl4ck.fightermod.FighterMod;
import com.fl4ck.fightermod.item.CriticalSwordItem;
import com.fl4ck.fightermod.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FighterMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CriticalAmuletEventHandler {

    private static final int HUNGER_COST = 2; // Стоимость в голоде за удар
    private static final int DURABILITY_COST = 1; // Стоимость в прочности за удар
    private static final int COOLDOWN_TICKS = 10; // Кулдаун в тиках

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            handleCriticalAmuletEffect(player, event.getEntity(), event);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            handleAncientAmuletDamage(player, event.getEntity(), event);
        }
    }

    private static void handleCriticalAmuletEffect(Player player, LivingEntity target, LivingHurtEvent event) {
        ItemStack offhandItem = player.getOffhandItem();

        // Проверяем, держит ли игрок древний амулет в левой руке
        if (isHoldingCriticalAmulet(player)) {
            ItemStack mainHandItem = player.getMainHandItem();

            // Проверяем, что в правой руке оружие
            if (!mainHandItem.isEmpty() &&
                    (mainHandItem.getItem() instanceof net.minecraft.world.item.SwordItem ||
                            mainHandItem.getItem() instanceof net.minecraft.world.item.AxeItem ||
                            mainHandItem.getItem() instanceof net.minecraft.world.item.TridentItem ||
                            mainHandItem.getItem() instanceof net.minecraft.world.item.BowItem)) {

                // Проверяем кулдаун
                if (player.getCooldowns().isOnCooldown(ModItems.CRITICAL_AMULET.get())) {
                    return;
                }

                // Проверяем прочность амулета
                if (offhandItem.getDamageValue() >= offhandItem.getMaxDamage()) {
                    if (!player.level().isClientSide()) {
                        player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§cАмулет сломан!"),
                                true
                        );
                    }
                    return;
                }

               //проверяем оружие в главной руке
                boolean isModWeapon = isHoldingCriticalSword(mainHandItem);

                // Проверяем, есть ли у игрока достаточно голода (учитываем модовое оружие)
                if (isModWeapon || player.getFoodData().getFoodLevel() >= HUNGER_COST || player.isCreative()) {

                    // Отнимаем голод только если это НЕ модовое оружие и игрок не в креативе
                    if (!player.isCreative() && !isModWeapon) {
                        player.getFoodData().setFoodLevel(
                                Math.max(0, player.getFoodData().getFoodLevel() - HUNGER_COST)
                        );
                    }

                    // Тратим прочность амулета
                    if (!player.isCreative()) {
                        if (player.level() instanceof ServerLevel serverLevel && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                            offhandItem.hurtAndBreak(DURABILITY_COST, serverLevel, serverPlayer, (item) -> {
                                serverPlayer.onEquippedItemBroken(item, net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                            });
                        }
                    }

                    // Увеличиваем урон в 2 раза
                    float originalDamage = event.getAmount();
                    float newDamage = originalDamage * 2.0f;

                    // Устанавливаем новый урон
                    event.setAmount(newDamage);

                    // Добавляем кулдаун
                    player.getCooldowns().addCooldown(ModItems.CRITICAL_AMULET.get(), COOLDOWN_TICKS);

                    // Визуальные эффекты
                    spawnCriticalEffects(player.level(), target);

                    // Звуковые эффекты
                    player.level().playSound(
                            null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT,
                            SoundSource.PLAYERS,
                            0.8f,
                            1.0f + (player.getRandom().nextFloat() * 0.4f - 0.2f)
                    );

                    player.level().playSound(
                            null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.AMETHYST_BLOCK_CHIME,
                            SoundSource.PLAYERS,
                            0.5f,
                            1.5f
                    );

                    // Эффект для игрока
                    if (!player.level().isClientSide()) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.DAMAGE_BOOST,
                                20, // 1 секунда
                                0,
                                false,
                                false,
                                true
                        ));

                        // Свечение цели
                        target.addEffect(new MobEffectInstance(
                                MobEffects.GLOWING,
                                40, // 2 секунды
                                0,
                                false,
                                false,
                                true
                        ));
                    }

                } else {
                    // Недостаточно голода
                    if (!player.level().isClientSide()) {
                        player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§cНедостаточно голода для активации амулета!"),
                                true
                        );
                    }
                }
            }
        }
    }

    private static void handleAncientAmuletDamage(Player player, LivingEntity target, LivingDamageEvent event) {
        ItemStack offhandItem = player.getOffhandItem();

        if (offhandItem.getItem() == ModItems.CRITICAL_AMULET.get()) {
            // Усиление крита - дополнительный визуальный эффект
            if (player.fallDistance > 0.0f && !player.onGround() && !player.isInWater()) {
                spawnEnhancedCriticalEffects(player.level(), target);
            }
        }
    }
    //эффекты
    private static void spawnCriticalEffects(net.minecraft.world.level.Level level, LivingEntity target) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 pos = target.position().add(0, target.getBbHeight() * 0.5, 0);

        // Частицы крита (оранжевые)
        for (int i = 0; i < 8; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 0.4;
            double dy = serverLevel.random.nextDouble() * target.getBbHeight();
            double dz = (serverLevel.random.nextDouble() - 0.5) * 0.4;

            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x + dx,
                    pos.y + dy,
                    pos.z + dz,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Частицы магии (красные)
        for (int i = 0; i < 3; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 0.3;
            double dy = serverLevel.random.nextDouble() * target.getBbHeight() * 0.5;
            double dz = (serverLevel.random.nextDouble() - 0.5) * 0.3;

            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    pos.x + dx,
                    pos.y + dy,
                    pos.z + dz,
                    1,
                    0, 0.1, 0,
                    0
            );
        }
    }
    //эффекты
    private static void spawnEnhancedCriticalEffects(net.minecraft.world.level.Level level, LivingEntity target) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        Vec3 pos = target.position().add(0, target.getBbHeight() * 0.5, 0);

        // Больше частиц крита
        for (int i = 0; i < 15; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 0.6;
            double dy = serverLevel.random.nextDouble() * target.getBbHeight();
            double dz = (serverLevel.random.nextDouble() - 0.5) * 0.6;

            serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    pos.x + dx,
                    pos.y + dy,
                    pos.z + dz,
                    1,
                    (serverLevel.random.nextDouble() - 0.5) * 0.1,
                    serverLevel.random.nextDouble() * 0.1,
                    (serverLevel.random.nextDouble() - 0.5) * 0.1,
                    0
            );
        }

        // Частицы взрыва
        serverLevel.sendParticles(
                ParticleTypes.POOF,
                pos.x,
                pos.y + target.getBbHeight() * 0.3,
                pos.z,
                5,
                0.2, 0.2, 0.2,
                0.05
        );
    }
    //проверяем наличие в руке критического амулета
    private static boolean isHoldingCriticalAmulet(Player player) {
        ItemStack offhandItem = player.getOffhandItem();
        return !offhandItem.isEmpty() && offhandItem.getItem() == ModItems.CRITICAL_AMULET.get();
    }
    //проверяем наличие в руке критического меча
    private static boolean isHoldingCriticalSword(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (stack.getItem() instanceof CriticalSwordItem) {
            return true;
        }

        if (stack.getItem() == ModItems.CRITICAL_SWORD.get()) {
            return true;
        }

        return false;
    }
}