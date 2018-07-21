package de.nerdynet.lba.common.core;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public enum EnumSplitterColor implements IStringSerializable {
	WHITE(0, "white"),
	RED(1, "red"),
	GREEN(2, "green"),
	BLUE(3, "blue");

	private final int index;
	private final String name;
	public static final EnumSplitterColor[] VALUES = new EnumSplitterColor[4];

	EnumSplitterColor(int index, String name) {
		this.index = index;
		this.name = name;
	}

	@Override
	public @Nonnull	String getName() {
		return this.name;
	}

	/**
	 * Gets the SplitterColor corresponding to the given index (0-3). Out of bounds values are wrapped around. The order is
	 * W-R-G-B.
	 */
	public static EnumSplitterColor getColor(int index) {
		return VALUES[MathHelper.abs(index % VALUES.length)];
	}

	static
	{
		for (EnumSplitterColor enumColor : values()) {
			VALUES[enumColor.index] = enumColor;
		}
	}

	/**
	 * Get the Index of this Color (0-3). The order is W-R-G-B
	 */
	public int getIndex() {
		return this.index;
	}
}
