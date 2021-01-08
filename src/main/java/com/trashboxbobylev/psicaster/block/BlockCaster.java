package com.trashboxbobylev.psicaster.block;

import com.trashboxbobylev.psicaster.PSICaster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockCaster extends Block
{
    public BlockCaster()
    {
        super(Material.ROCK);
        this.setRegistryName("caster");
        this.setUnlocalizedName(PSICaster.MODID + ".caster");
    }
}
