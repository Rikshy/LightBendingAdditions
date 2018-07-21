package de.nerdynet.lba.common.effect;

import com.teamwizardry.refraction.common.effect.EffectRedstone;
import com.teamwizardry.refraction.common.tile.TileInvisibleRedstone;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

public class EffectPureRedstone extends EffectRedstone {

	@Nonnull
	@Override
	protected Color getEffectColor() {
		return new Color(250,88,88, 20);
	}

	@Override
	public boolean doesTrigger(int potency) {
		return true;
	}

	@Override
	public void runFinalBlock(World world, BlockPos pos, int potency) {
		super.runFinalBlock(world, pos, 17);
		EnumFacing facing = beam.trace.sideHit;
		if (facing != null) {
			TileInvisibleRedstone te = (TileInvisibleRedstone) world.getTileEntity(pos.offset(facing));
			if (te != null) te.expiry = 20;
		}
	}
}
