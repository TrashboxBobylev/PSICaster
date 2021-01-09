package com.trashboxbobylev.psicaster.block;

import com.trashboxbobylev.psicaster.PSICaster;
import com.trashboxbobylev.psicaster.client.GuiCaster;
import com.trashboxbobylev.psicaster.init.GuiInit;
import com.trashboxbobylev.psicaster.tile.TileCaster;
import io.github.phantamanta44.libnine.block.L9Block;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.psi.common.core.PsiCreativeTab;

import javax.annotation.Nullable;

public class BlockCaster extends L9Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public BlockCaster()
    {
        super("caster", Material.ANVIL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setUnlocalizedName("psicaster.caster");
        setCreativeTab(PsiCreativeTab.INSTANCE);
        this.setTileFactory((w, m) -> {
            return new TileCaster();
        });
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        state = this.getActualState(state, worldIn, pos);
        return state.getValue(FACING) != face.getOpposite()? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)));
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            PSICaster.INSTANCE.getGuiHandler().openGui(player, GuiInit.CASTER, new WorldBlockPos(world, pos));
        }

        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isRemote){
            TileCaster tile = getTileEntity(worldIn, pos);
            if (tile != null){

            }
        }
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        return i;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }


    }
