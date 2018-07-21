package de.nerdynet.lba.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.common.effect.EffectMundane;
import com.teamwizardry.refraction.init.ModBlocks;
import de.nerdynet.lba.ConfigValues;
import de.nerdynet.lba.LightBendingAdditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.awt.*;

@TileRegister("splitter")
public class TileAdvSplitter extends TileModTickable {

	@Nonnull
	public ItemStackHandler pane = new ItemStackHandler(1) {

		@Override
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	private ItemStack redPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
	private ItemStack greenPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 13);
	private ItemStack bluePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 11);
	private ItemStack lastPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);

	public boolean hasInvChanged() {
		ItemStack currentPane = pane.getStackInSlot(0);
		if (!Utils.simpleAreStacksEqual(lastPane, currentPane)){
			lastPane = pane.getStackInSlot(0);
			return true;
		}
		return false;
	}

	public boolean isValidPane(ItemStack input) {
		return
				Utils.simpleAreStacksEqual(redPane, input) ||
						Utils.simpleAreStacksEqual(greenPane, input) ||
						Utils.simpleAreStacksEqual(bluePane, input);
	}

	public ResourceLocation getHeadLocation() {
		if (Utils.simpleAreStacksEqual(redPane, pane.getStackInSlot(0)))
			return new ResourceLocation(LightBendingAdditions.MOD_ID, "blocks/mirror_splitter_red");
		if (Utils.simpleAreStacksEqual(greenPane, pane.getStackInSlot(0)))
			return new ResourceLocation(LightBendingAdditions.MOD_ID, "blocks/mirror_splitter_green");
		if (Utils.simpleAreStacksEqual(bluePane, pane.getStackInSlot(0)))
			return new ResourceLocation(LightBendingAdditions.MOD_ID, "blocks/mirror_splitter_blue");
		return new ResourceLocation(Constants.MOD_ID, "blocks/mirror_splitter");
	}

	/*public void setState() {
		if (Utils.simpleAreStacksEqual(redPane, pane.getStackInSlot(0)))
			world.setBlockState(pos, ModBlocks.SPLITTER.getDefaultState().withProperty(BlockAdvSplitter.COLORS, EnumSplitterColor.RED), 3);
		if (Utils.simpleAreStacksEqual(greenPane, pane.getStackInSlot(0)))
			world.setBlockState(pos, ModBlocks.SPLITTER.getDefaultState().withProperty(BlockAdvSplitter.COLORS, EnumSplitterColor.GREEN), 3);
		if (Utils.simpleAreStacksEqual(bluePane, pane.getStackInSlot(0)))
			world.setBlockState(pos, ModBlocks.SPLITTER.getDefaultState().withProperty(BlockAdvSplitter.COLORS, EnumSplitterColor.BLUE), 3);
		world.setBlockState(pos, ModBlocks.SPLITTER.getDefaultState().withProperty(BlockAdvSplitter.COLORS, EnumSplitterColor.WHITE), 3);
	}*/

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
				(T) pane : super.getCapability(capability, facing);
	}

	@Save
	public float rotXUnpowered, rotYUnpowered, rotXPowered = Float.NaN, rotYPowered = Float.NaN;
	@Save
	public float rotDestX, rotPrevX, rotDestY, rotPrevY;
	@Save
	public boolean transitionX = false, transitionY = false, powered = false;
	@Save
	public long worldTime = 0;

	public TileAdvSplitter() {
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public float getRotX() {
		return powered ? rotXPowered : rotXUnpowered;
	}

	public void setRotX(float rotX) {
		if (transitionX) return;
		if (rotX == rotDestX) return;
		rotPrevX = rotDestX;
		rotDestX = rotX;
		transitionX = true;
		worldTime = world.getTotalWorldTime();
		markDirty();
	}

	public float getRotY() {
		return powered ? rotYPowered : rotYUnpowered;
	}

	public void setRotY(float rotY) {
		if (transitionY) return;
		if (rotY == rotDestY) return;
		rotPrevY = rotDestY;
		rotDestY = rotY;
		transitionY = true;
		worldTime = world.getTotalWorldTime();
		markDirty();
	}

	public void handle(Beam beam) {
		float x, y;
		if (powered) {
			x = rotXPowered;
			y = rotYPowered;
		} else {
			x = rotXUnpowered;
			y = rotYUnpowered;
		}

		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(y), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(x), new Vec3d(1, 0, 0));

		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

		Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();
		Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

		fireBeams(incomingDir, outgoingDir, beam);
	}

	private void fireBeams(Vec3d incomingDir, Vec3d outgoingDir, Beam beam) {
		int newAlpha = beam.getColor().getAlpha() / 2;
		ItemStack currentPane = pane.getStackInSlot(0);
		if ( checkFireBeam(beam, currentPane, redPane)) {
			Effect incommingEffect= EffectTracker.getEffect(new Color(beam.getColor().getRed(), 0, 0, newAlpha));
			Effect outgoingEffect  = EffectTracker.getEffect(new Color(0, beam.getColor().getGreen(), beam.getColor().getBlue(), newAlpha));

			beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
			beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
		} else if ( checkFireBeam(beam, currentPane, greenPane)) {
			Effect incommingEffect = EffectTracker.getEffect(new Color(0, beam.getColor().getGreen(), 0, newAlpha));
			Effect outgoingEffect  = EffectTracker.getEffect(new Color(beam.getColor().getRed(), 0, beam.getColor().getBlue(), newAlpha));

			beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
			beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
		} else if ( checkFireBeam(beam, currentPane, bluePane)) {
			Effect incommingEffect = EffectTracker.getEffect(new Color(0, 0, beam.getColor().getBlue(), newAlpha));
			Effect outgoingEffect  = EffectTracker.getEffect(new Color(beam.getColor().getRed(), beam.getColor().getGreen(), 0, newAlpha));

			beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
			beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
		} else {
			Effect effect = EffectTracker.getEffect(new Color(beam.getColor().getRed(), beam.getColor().getGreen(), beam.getColor().getBlue(), newAlpha));

			beam.createSimilarBeam(outgoingDir).setEffect(effect).spawn();
			beam.createSimilarBeam(incomingDir).setEffect(effect).spawn();
		}
	}

	private boolean checkFireBeam(Beam beam, ItemStack currentPane, ItemStack paneToCheck) {
		return (!(beam.effect instanceof EffectMundane) || ConfigValues.ADV_SPLITTER_CAN_SPLIT_MUNDANE) &&
				Utils.simpleAreStacksEqual(currentPane, paneToCheck);
	}

	@Override
	public void tick() {
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((rotPrevX - rotDestX) / 2.0), 10)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((rotPrevY - rotDestY) / 2.0), 10));
		double worldTimeTransition = (world.getTotalWorldTime() - worldTime);

		float rotX, rotY;
		if (transitionX) {
			if (worldTimeTransition < transitionTimeMaxX) {
				if (Math.round(rotDestX) > Math.round(rotPrevX))
					rotX = -((rotDestX - rotPrevX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				else
					rotX = ((rotPrevX - rotDestX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
			} else {
				rotX = rotDestX;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
				transitionX = false;
			}
			markDirty();
		}
		if (transitionY) {
			if (worldTimeTransition < transitionTimeMaxY) {
				if (Math.round(rotDestY) > Math.round(rotPrevY))
					rotY = -((rotDestY - rotPrevY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				else
					rotY = ((rotPrevY - rotDestY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
			} else {
				rotY = rotDestY;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
				transitionY = false;
			}
			markDirty();
		}
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		if (!transitionX && !transitionY) {
			this.powered = powered;
			if (powered) {
				if (!Float.isNaN(rotXPowered) && rotDestX != rotXPowered) setRotX(rotXPowered);
				if (!Float.isNaN(rotYPowered) && rotDestY != rotYPowered) setRotY(rotYPowered);
			} else {
				if (!Float.isNaN(rotXUnpowered) && rotDestX != rotXUnpowered) setRotX(rotXUnpowered);
				if (!Float.isNaN(rotYUnpowered) && rotDestY != rotYUnpowered) setRotY(rotYUnpowered);
			}
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound cmp) {
		pane.deserializeNBT(cmp.getCompoundTag("items"));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
		cmp.setTag("items", pane.serializeNBT());
	}

	@Override
	public void readCustomBytes(ByteBuf buf) {
		pane.deserializeNBT(CommonUtilMethods.readTag(buf));
	}

	@Override
	public void writeCustomBytes(ByteBuf buf, boolean sync) {
		CommonUtilMethods.writeTag(buf, pane.serializeNBT());
	}
}
