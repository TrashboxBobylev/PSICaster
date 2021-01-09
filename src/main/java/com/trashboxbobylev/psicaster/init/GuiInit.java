package com.trashboxbobylev.psicaster.init;

import com.trashboxbobylev.psicaster.client.GuiCaster;
import com.trashboxbobylev.psicaster.container.ContainerCaster;
import com.trashboxbobylev.psicaster.tile.TileCaster;
import io.github.phantamanta44.libnine.InitMe;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.Registrar;
import io.github.phantamanta44.libnine.gui.GuiIdentity;
import io.github.phantamanta44.libnine.tile.L9TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class GuiInit {
    public static final GuiIdentity<ContainerCaster, GuiCaster> CASTER = new GuiIdentity("caster", ContainerCaster.class);

    public GuiInit() {
    }

    @InitMe("psicaster")
    public static void init() {
        Registrar reg = LibNine.PROXY.getRegistrar();
        reg.queueGuiServerReg(CASTER, (p, w, x, y, z) -> {
            return new ContainerCaster(getTile(w, x, y, z), p.inventory);
        });
    }

    @SideOnly(Side.CLIENT)
    @InitMe(
            value = "psicaster",
            sides = {Side.CLIENT}
    )
    public static void initClient() {
        Registrar reg = LibNine.PROXY.getRegistrar();
        reg.queueGuiClientReg(CASTER, (c, p, w, x, y, z) -> {
            return new GuiCaster(c);
        });
    }

    public static <T extends L9TileEntity> T getTile(World world, int x, int y, int z) {
        return (T) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z)));
    }
}
