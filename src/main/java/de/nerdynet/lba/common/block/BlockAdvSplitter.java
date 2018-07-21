package de.nerdynet.lba.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IPrecision;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.init.ModItems;
import de.nerdynet.lba.client.render.RenderAdvSplitter;
import de.nerdynet.lba.common.core.EnumSplitterColor;
import de.nerdynet.lba.common.tile.TileAdvSplitter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Shyrik
 */
public class BlockAdvSplitter extends BlockModContainer implements ILaserTrace, IPrecision, ILightSink  {

	public static final PropertyEnum<EnumSplitterColor> COLORS = PropertyEnum.create("color", EnumSplitterColor.class);

	public BlockAdvSplitter() {
		super("splitter", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileAdvSplitter.class, new RenderAdvSplitter());
	}

	private TileAdvSplitter getTE(World world, BlockPos pos) {
		return (TileAdvSplitter) world.getTileEntity(pos);
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		getTE(world, pos).handle(beam);
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileAdvSplitter();
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {

		if (!worldIn.isRemote) {
			ItemStack heldItem = playerIn.getHeldItem(hand);
			TileAdvSplitter splitter = getTE(worldIn, pos);
			if (splitter.isValidPane(heldItem)) {
				ItemStack stack = heldItem.copy();
				stack.setCount(1);
				ItemStack insert = ItemHandlerHelper.insertItem(splitter.pane, stack, false);
				if (insert.isEmpty())
					heldItem.setCount(heldItem.getCount() - 1);
				playerIn.openContainer.detectAndSendChanges();
			} else if (playerIn.isSneaking() && CapsUtils.getOccupiedSlotCount(splitter.pane) > 0) {
				ItemHandlerHelper.giveItemToPlayer(playerIn, splitter.pane.extractItem(CapsUtils.getLastOccupiedSlot(splitter.pane), 1, false));
				playerIn.openContainer.detectAndSendChanges();
			} else if (heldItem.getItem() == ModItems.SCREW_DRIVER) {
				adjust(worldIn, pos, heldItem, false, facing);
			}
			splitter.markDirty();
		}
		return true;
	}

	@Override
	@Nonnull
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COLORS);
	}

	@Override
	@Nonnull
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(COLORS, EnumSplitterColor.getColor(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(COLORS).getIndex();
	}


	@Override
	public float getRotX(World worldIn, BlockPos pos) {
		return getTE(worldIn, pos).getRotX();
	}

	@Override
	public void setRotX(World worldIn, BlockPos pos, float x) {
		getTE(worldIn, pos).setRotX(x);
	}

	@Override
	public float getRotY(World worldIn, BlockPos pos) {
		return getTE(worldIn, pos).getRotY();
	}

	@Override
	public void setRotY(World worldIn, BlockPos pos, float y) {
		getTE(worldIn, pos).setRotY(y);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos neighbor) {
		TileAdvSplitter splitter = getTE((World) worldIn, pos);
		if (splitter == null) return;

		if (splitter.isPowered()) {
			if (!((World) worldIn).isBlockPowered(pos) || ((World) worldIn).isBlockIndirectlyGettingPowered(pos) == 0) {
				splitter.setPowered(false);
			}
		} else {
			if (((World) worldIn).isBlockPowered(pos) || ((World) worldIn).isBlockIndirectlyGettingPowered(pos) > 0)
				splitter.setPowered(true);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		TileAdvSplitter splitter = getTE(worldIn, pos);
		if (splitter == null) return;
		if (splitter.isPowered() && !worldIn.isBlockPowered(pos)) {
			splitter.setPowered(false);
		}
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult collisionRayTraceLaser(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw) {
		double pixels = 1.0 / 16.0;

		AxisAlignedBB aabb = new AxisAlignedBB(pixels, 0, pixels, 1 - pixels, pixels, 1 - pixels).offset(-0.5, -pixels / 2, -0.5);

		RayTraceResult superResult = super.collisionRayTrace(blockState, worldIn, pos, startRaw, endRaw);

		TileAdvSplitter tile = (TileAdvSplitter) worldIn.getTileEntity(pos);
		if (tile == null) return null;
		Vec3d start = startRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d end = endRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());

		start = start.subtract(0.5, 0.5, 0.5);
		end = end.subtract(0.5, 0.5, 0.5);

		Matrix4 matrix = new Matrix4();
		matrix.rotate(-Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		matrix.rotate(-Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));

		Matrix4 inverse = new Matrix4();
		inverse.rotate(Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		inverse.rotate(Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));

		start = matrix.apply(start);
		end = matrix.apply(end);
		RayTraceResult result = aabb.calculateIntercept(start, end);
		if (result == null) return null;
		Vec3d a = result.hitVec;

		a = inverse.apply(a);
		a = a.addVector(0.5, 0.5, 0.5);

		return new RayTraceResult(a.add(new Vec3d(pos)), superResult == null ? EnumFacing.UP : superResult.sideHit, pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = EnumFacing.getFacingFromVector((float) placer.getLook(0).x, (float) placer.getLook(0).y, (float) placer.getLook(0).z);
		TileAdvSplitter mirror = getTE(worldIn, pos);
		float x = 0, y = 0;

		if (facing.getHorizontalAngle() == 0) {
			x = 90;
			y = 0;
		} else if (facing.getHorizontalAngle() == 90) {
			x = 270;
			y = 90;
		} else if (facing.getHorizontalAngle() == 270) {
			x = -90;
			y = 270;
		} else if (facing.getHorizontalAngle() == 180) {
			x = 90;
			y = 180;
		}
		mirror.rotXPowered = x;
		mirror.rotYPowered = y;
		mirror.rotXUnpowered = x;
		mirror.rotYUnpowered = y;
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

}
