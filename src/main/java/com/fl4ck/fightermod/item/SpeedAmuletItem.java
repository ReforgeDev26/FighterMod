package com.fl4ck.fightermod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SpeedAmuletItem extends Item {
    public SpeedAmuletItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§6Амулет скорости"));
        tooltip.add(Component.literal("§7В левой руке:"));
        tooltip.add(Component.literal("§a×2 скорость"));
        tooltip.add(Component.literal("§c-2 голода за время эффекта"));
        tooltip.add(Component.literal("§8Прочность: " + (stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage()));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}