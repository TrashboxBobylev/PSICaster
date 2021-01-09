package com.trashboxbobylev.psicaster;

import net.minecraftforge.common.config.Config;

@Config(modid = "psicaster")
public class CasterConfig {
    @Config.Name("Maximum Potency")
    @Config.Comment("Defines the limit of potency for auto-caster's spells.")
    @Config.RangeInt(min = 1)
    public static int maxSpellPotency = 500;
}
