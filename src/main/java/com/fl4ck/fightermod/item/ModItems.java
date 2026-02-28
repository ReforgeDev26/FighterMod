package com.fl4ck.fightermod.item;

import com.fl4ck.fightermod.FighterMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FighterMod.MODID);

    // Древний амулет для усиления урона
    public static final RegistryObject<Item> CRITICAL_AMULET = ITEMS.register("critical_amulet",
            () -> new CriticalAmuletItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                    .durability(100) // Прочность 100 использований
            ));

    //Меч критического урона
    public static final RegistryObject<Item> CRITICAL_SWORD = ITEMS.register("critical_sword",
            () -> new CriticalSwordItem());

    //Древний амулет скорости
    public static final RegistryObject<Item> SPEED_AMULET = ITEMS.register("speed_amulet",
            () -> new SpeedAmuletItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                    .durability(200) // Прочность 200 использований
            ));
}