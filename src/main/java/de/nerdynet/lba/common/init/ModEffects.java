package de.nerdynet.lba.common.init;

import com.teamwizardry.refraction.api.beam.EffectTracker;
import de.nerdynet.lba.common.effect.EffectPureRedstone;

public class ModEffects {
	public static void init() {
		EffectTracker.registerEffect(new EffectPureRedstone());
	}
}
