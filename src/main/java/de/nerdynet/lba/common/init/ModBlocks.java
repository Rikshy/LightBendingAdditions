package de.nerdynet.lba.common.init;

import de.nerdynet.lba.common.block.BlockAdvSplitter;
import de.nerdynet.lba.common.block.BlockRedstoneMiniLaser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

	public static BlockRedstoneMiniLaser REDMINILASER;
	public static BlockAdvSplitter ADVSPLITTER;

	public static void init() {
		ADVSPLITTER = new BlockAdvSplitter();
		REDMINILASER = new BlockRedstoneMiniLaser();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		ADVSPLITTER.initModel();;
	}
}
