package com.fl4ck.fightermod.item;

import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CriticalSwordItem extends SwordItem {
    public CriticalSwordItem() {
        super(Tiers.GOLD, new Item.Properties()
                .stacksTo(1) // Не стакается
                .durability(32) // Прочность
                .rarity(net.minecraft.world.item.Rarity.UNCOMMON)); // Редкость
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§6Меч критического урона"));
        tooltip.add(Component.literal("§7Пока надет амулет критического урона:"));
        tooltip.add(Component.literal("§a×2 урон + эффект крита"));
        tooltip.add(Component.literal("§8Прочность: " + (stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage()));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}