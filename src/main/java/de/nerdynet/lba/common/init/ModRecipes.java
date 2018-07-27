package de.nerdynet.lba.common.init;

import net.minecraft.item.ItemStack;

import java.awt.*;

import static com.teamwizardry.refraction.api.recipe.AssemblyBehaviors.register;
import static com.teamwizardry.refraction.api.lib.LibOreDict.*;

public class ModRecipes {
	public static void init() {
		register("mini_red_laser", new ItemStack(ModBlocks.REDMINILASER),
				new Color(120, 0,0, 16), new Color(255, 0,0, 32),
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, LENS, OPTIC_FIBER, "dustRedstone");

		register("advanced_splitter", new ItemStack(ModBlocks.ADVSPLITTER),
				new Color(120, 0,120, 16), new Color(255, 0,255, 32),
				new ItemStack(com.teamwizardry.refraction.init.ModBlocks.SPLITTER),
				new ItemStack(com.teamwizardry.refraction.init.ModBlocks.MAGNIFIER),
				REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);
	}
}
