package com.trashboxbobylev.psicaster;

import io.github.phantamanta44.libnine.Virtue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "psicaster", name = PSICaster.NAME, version = PSICaster.VERSION)
public class PSICaster extends Virtue
{
    public static final String NAME = "PSICaster";
    public static final String VERSION = "1.0";

    @Mod.Instance("psicaster")
    public static PSICaster INSTANCE;

    public PSICaster() {
        super("psicaster");
    }


}
