package com.fl4ck.fightermod;

import com.fl4ck.fightermod.item.ModCreativeTabs;
import com.fl4ck.fightermod.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FighterMod.MODID)
public class FighterMod {
    public static final String MODID = "fightermod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FighterMod() {
        LOGGER.info("Загрузка мода {}", MODID);

        // Получаем шину событий мода
        @SuppressWarnings("deprecation")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Сначала регистрируем предметы
        ModItems.ITEMS.register(modEventBus);  // Прямая регистрация DeferredRegister

        // Затем регистрируем вкладки
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);  // Прямая регистрация

        // Регистрируем слушатели жизненного цикла
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        LOGGER.info("Мод {} инициализирован", MODID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Настройка мода {}", MODID);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
   //креативные вкладки
        if (event.getTabKey() == ModCreativeTabs.FIGHTERMOD_TAB.getKey()) {
            event.accept(ModItems.CRITICAL_AMULET.get());
            event.accept(ModItems.CRITICAL_SWORD.get());
            event.accept(ModItems.SPEED_AMULET.get());
        }

        //стандартные вкладки
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.COMBAT) {
            event.accept(ModItems.CRITICAL_AMULET.get());
            event.accept(ModItems.CRITICAL_SWORD.get());
            event.accept(ModItems.SPEED_AMULET.get());
        }

        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.CRITICAL_AMULET.get());
            event.accept(ModItems.CRITICAL_SWORD.get());
            event.accept(ModItems.SPEED_AMULET.get());
        }
    }
}