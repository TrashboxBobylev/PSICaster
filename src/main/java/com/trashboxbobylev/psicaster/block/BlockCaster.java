package com.trashboxbobylev.psicaster.block;

import com.google.common.collect.Lists;
import com.trashboxbobylev.psicaster.PSICaster;
import com.trashboxbobylev.psicaster.client.GuiCaster;
import com.trashboxbobylev.psicaster.init.GuiInit;
import com.trashboxbobylev.psicaster.tile.TileCaster;
import io.github.phantamanta44.libnine.block.L9Block;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.psi.common.core.PsiCreativeTab;
import java.util.List;

import javax.annotation.Nullable;

public class BlockCaster extends L9Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.3D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.7D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.3D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.7D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3D, 1.0D);
    protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.7D, 0.0D, 1.0D, 1.0D, 1.0D);
    
    public BlockCaster()
    {
        super("caster", Material.ANVIL);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setUnlocalizedName("psicaster.caster");
        setCreativeTab(CreativeTabs.REDSTONE);
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
                tile.checkRedstone();
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
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileCaster tile = getTileEntity(world, pos);
        if (tile != null) {
        	for(int i = 0; i < tile.getInventory().getSlots() ; i++)
            WorldUtils.dropItem(world, pos, tile.getInventory().getStackInSlot(i));
        }
        super.breakBlock(world, pos, state);
    }

    private static List<AxisAlignedBB> getCollisionBoxList(IBlockState state)
    {
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        EnumFacing facing = (EnumFacing)state.getValue(FACING);
        
        if(facing != EnumFacing.UP)
            list.add(DOWN_AABB);
        if(facing != EnumFacing.DOWN)
        	list.add(UP_AABB);
        if(facing != EnumFacing.EAST)
        	list.add(WEST_AABB);
        if(facing != EnumFacing.WEST)
        	list.add(EAST_AABB);
        if(facing != EnumFacing.NORTH)
        	list.add(SOUTH_AABB);
        if(facing != EnumFacing.SOUTH)
        	list.add(NORTH_AABB);

        return list;
    }
    
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(blockState, worldIn, pos)))
        {
            list.add(this.rayTrace(pos, start, end, axisalignedbb));
        }

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for (RayTraceResult raytraceresult : list)
        {
            if (raytraceresult != null)
            {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }
}
