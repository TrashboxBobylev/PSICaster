package com.trashboxbobylev.psicaster;

import io.github.phantamanta44.libnine.Virtue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "psicaster", name = PSICaster.NAME, version = PSICaster.VERSION)
public class PSICaster extends Virtue
{
    public static final String NAME = "PSICaster";
    public static final String VERSION = "1.0";
    public static Logger LOGGER;

    @Mod.Instance("psicaster")
    public static PSICaster INSTANCE;

    public PSICaster() {
        super("psicaster");
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
    }

}
