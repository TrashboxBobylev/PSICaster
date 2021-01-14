package com.trashboxbobylev.psicaster.tile;

import com.trashboxbobylev.psicaster.CasterConfig;
import com.trashboxbobylev.psicaster.PSICaster;
import com.trashboxbobylev.psicaster.block.BlockCaster;
import com.trashboxbobylev.psicaster.util.FakePlayerUtil;
import io.github.phantamanta44.libnine.capability.impl.L9AspectInventory;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBrokerDirPredicated;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.helper.OptUtils;
import io.github.phantamanta44.libnine.util.nbt.NBTUtils;
import io.github.phantamanta44.libnine.util.world.BlockSide;
import io.github.phantamanta44.libnine.util.world.IAllocableSides;
import io.github.phantamanta44.libnine.util.world.SideAlloc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.psi.api.cad.*;
import vazkii.psi.api.internal.DummyPlayerData;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.common.Psi;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.item.component.ItemCADAssembly;
import vazkii.psi.common.item.component.ItemCADCore;
import vazkii.psi.common.item.component.ItemCADSocket;
import xyz.phanta.psicosts.capability.PsiCell;
import xyz.phanta.psicosts.init.PsioCaps;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

@RegisterTile("psicaster")
public class TileCaster extends L9TileEntityTicking{

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
    protected CapabilityBroker initCapabilities() {
        return (new CapabilityBroker()).with(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventory);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("redstonePowered")) redstonePowered = compound.getBoolean("redstonePowered");
    }

    @Override
    protected void tick() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("redstonePowered", redstonePowered);
        return compound;
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

    public static float getYawFromFacing(EnumFacing currentFacing) {
        switch (currentFacing) {
            case DOWN:
            case UP:
            case SOUTH:
            default:
                return 0;
            case EAST:
                return 270F;
            case NORTH:
                return 180F;
            case WEST:
                return 90F;
        }
    }

    public void castStuff(){
        ItemStack bullet = inventory.getStackInSlot(1);
        ItemStack battery = inventory.getStackInSlot(0);
        ItemStack core = inventory.getStackInSlot(2);
        ItemStack assembly = new ItemStack(ModItems.cadAssembly, 1, 5);
        ItemStack socket = new ItemStack(ModItems.cadSocket, 1, 3);

        //create fake cad from our resources
        ItemStack cad = ItemCAD.makeCAD(Arrays.asList(assembly, core, socket));
        PSICaster.LOGGER.debug(cad);

        WeakReference<FakePlayer> player = FakePlayerUtil.initFakePlayer((WorldServer) getWorld(), UUID.randomUUID(), "caster");
        if (player == null){
            return;
        }
        player.get().rotationYaw = getYawFromFacing(getWorld().getBlockState(pos).getValue(BlockCaster.FACING));
        player.get().setPosition(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        player.get().setHeldItem(EnumHand.MAIN_HAND, cad);

        int charge = OptUtils.stackTag(battery).map((t) -> {
            return t.getInteger("PsioCharge");
        }).orElse(0);

        if (charge >= 0) {
            ISpellAcceptor spellContainer = ISpellAcceptor.acceptor(bullet);
            Spell spell = spellContainer.getSpell();
            SpellContext context = new SpellContext().setPlayer(player.get()).setSpell(spell);

            Consumer<SpellContext> predicate = (ctx -> ctx.castFrom = player.get().getActiveHand());
            predicate.accept(context);

            if (context.isValid()){
                int cost = context.cspell.metadata.stats.get(EnumSpellStat.COST);
                cost *= ISpellAcceptor.acceptor(bullet).getCostModifier();
                if (context.cspell.metadata.stats.get(EnumSpellStat.POTENCY) > CasterConfig.maxSpellPotency) {
                    if (!world.isRemote) {
                        player.get().sendMessage(new TextComponentTranslation("psimisc.weak_cad").setStyle(new Style().setColor(TextFormatting.RED)));
                    }
                }


                else if (context.cspell.metadata.evaluateAgainst(cad) && charge >= cost){

                    PreSpellCastEvent event = new PreSpellCastEvent(cost, 1.5F, 25, 1, spell, context, player.get(), new DummyPlayerData(), cad, bullet);
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        String cancelMessage = event.getCancellationMessage();
                        if (cancelMessage != null && !cancelMessage.isEmpty())
                            player.get().sendMessage(new TextComponentTranslation(cancelMessage).setStyle(new Style().setColor(TextFormatting.RED)));
                        return;
                    }

                    int cd = event.getCooldown();
                    int particles = event.getParticles();
                    float sound = event.getSound();
                    cost = event.getCost();

                    spell = event.getSpell();
                    context = event.getContext();

                    if (cost > 0) {
                        battery.getTagCompound().setInteger("PsioCharge", charge - cost);
                    }

                    if (!world.isRemote)
                        world.playSound(null, player.get().posX, player.get().posY, player.get().posZ, PsiSoundHandler.cadShoot, SoundCategory.PLAYERS, sound, (float) (0.5 + Math.random() * 0.5));
                    else {
                        int color = Psi.proxy.getColorForCAD(cad);
                        float r = PsiRenderHelper.r(color) / 255F;
                        float g = PsiRenderHelper.g(color) / 255F;
                        float b = PsiRenderHelper.b(color) / 255F;
                        for (int i = 0; i < particles; i++) {
                            double x = player.get().posX + (Math.random() - 0.5) * 2.1 * player.get().width;
                            double y = player.get().posY - player.get().getYOffset();
                            double z = player.get().posZ + (Math.random() - 0.5) * 2.1 * player.get().width;
                            float grav = -0.15F - (float) Math.random() * 0.03F;
                            Psi.proxy.sparkleFX(x, y, z, r, g, b, grav, 0.25F, 15);
                        }

                        double x = player.get().posX;
                        double y = player.get().posY + player.get().getEyeHeight() - 0.1;
                        double z = player.get().posZ;
                        Vector3 lookOrig = new Vector3(player.get().getLookVec());
                        for (int i = 0; i < 25; i++) {
                            Vector3 look = lookOrig.copy();
                            double spread = 0.25;
                            look.x += (Math.random() - 0.5) * spread;
                            look.y += (Math.random() - 0.5) * spread;
                            look.z += (Math.random() - 0.5) * spread;
                            look.normalize().multiply(0.15);

                            Psi.proxy.sparkleFX(x, y, z, r, g, b, (float) look.x, (float) look.y, (float) look.z, 0.3F, 5);
                        }
                    }

                    if (!world.isRemote)
                        spellContainer.castSpell(context);
                    MinecraftForge.EVENT_BUS.post(new SpellCastEvent(spell, context, player.get(), new DummyPlayerData(), cad, bullet));
                    return;
                } else if (!world.isRemote) {
                    player.get().sendMessage(new TextComponentTranslation("psimisc.weak_cad").setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
        }


    }
}
