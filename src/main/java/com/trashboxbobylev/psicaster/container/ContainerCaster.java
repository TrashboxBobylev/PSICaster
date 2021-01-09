package com.trashboxbobylev.psicaster.container;

import com.trashboxbobylev.psicaster.tile.TileCaster;
import io.github.phantamanta44.libnine.gui.L9Container;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCaster extends L9Container {
    protected TileCaster tile;

    public ContainerCaster(TileCaster tile, InventoryPlayer ipl) {
        super(ipl);
        this.tile = tile;
        addSlotToContainer(new SlotItemHandler(tile.inventory, 0, 62, 35));
        addSlotToContainer(new SlotItemHandler(tile.inventory, 1, 79, 35));
        addSlotToContainer(new SlotItemHandler(tile.inventory, 2, 96, 35));
    }
}
