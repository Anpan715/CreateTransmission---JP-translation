package com.serpenssolida.createtransfer;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;

public class CreateTransferModels
{
	public static final PartialModel CHAIN_SHAFT = block("transmission_chain/chain_shaft");
	public static final PartialModel CHAIN = block("transmission_chain/chain");
	public static final PartialModel CHAIN_CONNECTED = block("transmission_chain/chain_connected");
	public static final PartialModel CHAIN_BELT = block("transmission_chain/chain_belt");

	CreateTransferModels(){}

	/**
	 * Initializes the class static fields.
	 */
	public static void init()
	{
		CreateTransfer.LOGGER.info("Loading partial models for " + CreateTransfer.NAME);
	}

	/**
	 * Creates a {@link PartialModel} with the given path.
	 */
	protected static PartialModel block(String path)
	{
		return new PartialModel(CreateTransfer.asResource("block/" + path));
	}

}
