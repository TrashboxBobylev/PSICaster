package com.trashboxbobylev.psicaster.tile;

import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;

@RegisterTile("psicaster")
public class TileCaster extends L9TileEntityTicking {
    public TileCaster() {
        this.markRequiresSync();
        this.setInitialized();
    }



    @Override
    protected void tick() {

    }

    @Override
    public void update() {

    }
}
