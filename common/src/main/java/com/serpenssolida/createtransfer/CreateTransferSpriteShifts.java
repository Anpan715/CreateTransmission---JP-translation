package com.serpenssolida.createtransfer;

import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

public class CreateTransferSpriteShifts
{
	public static final SpriteShiftEntry CHAIN = get("block/chain", "block/chain_scroll");

	CreateTransferSpriteShifts(){}

	/**
	 * Initializes the class static fields.
	 */
	public static void init()
	{
		//Init static fields.
	}

	/**
	 * Initializes a {@link SpriteShifter} with the given textures and return its entry.
	 * @param originalLocation path of the original texture.
	 * @param targetLocation path of the target texture.
	 *
	 * @return the entry of the newly initialized sprite shift.
	 */
	private static SpriteShiftEntry get(String originalLocation, String targetLocation)
	{
		return SpriteShifter.get(CreateTransfer.asResource(originalLocation), CreateTransfer.asResource(targetLocation));
	}
}
