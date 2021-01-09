package com.trashboxbobylev.psicaster.tile;

import io.github.phantamanta44.libnine.capability.impl.L9AspectInventory;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.helper.OptUtils;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICADComponent;
import vazkii.psi.api.cad.ISocketableCapability;
import vazkii.psi.api.spell.ISpellContainer;
import vazkii.psi.common.item.component.ItemCADCore;
import xyz.phanta.psicosts.capability.PsiCell;
import xyz.phanta.psicosts.init.PsioCaps;

@RegisterTile("psicaster")
public class TileCaster extends L9TileEntityTicking {

    @AutoSerialize
    public boolean redstonePowered;

    public TileCaster() {
        this.markRequiresSync();
        this.setInitialized();
    }

    @AutoSerialize
    public final L9AspectInventory inventory = new L9AspectInventory.Observable(3,
            (s, o, n) -> setDirty())
            .withPredicate(0, s -> (Boolean)OptUtils.capability(s, PsioCaps.PSI_CELL).map(PsiCell::canReceiveCharge).orElse(false))
            .withPredicate(1, s -> s.getItem() instanceof ISpellContainer && ((ISpellContainer)s.getItem()).containsSpell(s))
            .withPredicate(2, s -> (s.getItem() instanceof ICADComponent) && ((ICADComponent)s.getItem()).getComponentType(s) == EnumCADComponent.CORE);

    @Override
    protected void tick() {

    }

    public void checkRedstone(){
        boolean isIndirectlyPowered = (getWorld().isBlockIndirectlyGettingPowered(pos) != 0);
        if (isIndirectlyPowered && !redstonePowered) {
            redstoneChanged(true);
        } else if (redstonePowered && !isIndirectlyPowered) {
            redstoneChanged(false);
        }
    }

    public void redstoneChanged(boolean value){
        redstonePowered = value;
        if (!world.isRemote && value){
            castStuff();
        }
    }

    public void castStuff(){

    }
}
