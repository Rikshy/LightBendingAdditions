package de.nerdynet.lba.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import de.nerdynet.lba.common.effect.EffectPureRedstone;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@TileRegister("mini_laser")
public class TileRedstoneMiniLaser extends TileModTickable {

	@Override
	public void tick() {
		World world = getWorld();
		if (world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0) return;
		Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		Vec3d vec = PosUtils.getVecFromFacing(face);
		new Beam(world, center, vec, (new EffectPureRedstone()).setPotency(20)).spawn();

	}
}
