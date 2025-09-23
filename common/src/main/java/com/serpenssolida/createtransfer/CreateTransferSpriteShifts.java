package com.serpenssolida.createtransfer;

import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

public class CreateTransferSpriteShifts
{
	public static final SpriteShiftEntry BELT = get("block/chain", "block/chain_scroll");

	CreateTransferSpriteShifts(){}
	
	public static void init()
	{
		//Init static fields.
	}

	private static SpriteShiftEntry get(String originalLocation, String targetLocation)
	{
		return SpriteShifter.get(CreateTransfer.asResource(originalLocation), CreateTransfer.asResource(targetLocation));
	}
}
