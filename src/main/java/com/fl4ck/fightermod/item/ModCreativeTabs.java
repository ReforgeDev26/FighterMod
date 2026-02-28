package com.fl4ck.fightermod.item;

import com.fl4ck.fightermod.FighterMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FighterMod.MODID);

    public static final RegistryObject<CreativeModeTab> FIGHTERMOD_TAB = CREATIVE_MODE_TABS.register("fightermod_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fightermod"))
                    .icon(() -> new ItemStack(ModItems.CRITICAL_SWORD.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CRITICAL_SWORD.get());
                        output.accept(ModItems.CRITICAL_AMULET.get());
                        output.accept(ModItems.SPEED_AMULET.get());
                    })
                    .build()
    );
}