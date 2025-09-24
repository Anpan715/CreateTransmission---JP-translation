package com.serpenssolida.createtransfer;

import com.serpenssolida.createtransfer.content.chain.TransmissionChainBlockEntity;
import com.serpenssolida.createtransfer.content.chain.TransmissionChainInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CreateTransferBlockEntities
{
	public static final BlockEntityEntry<TransmissionChainBlockEntity> TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.blockEntity("transmission_chain", TransmissionChainBlockEntity::new)
			.instance(() -> TransmissionChainInstance::new, true)
			.validBlocks(CreateTransferBlocks.TRANSMISSION_CHAIN, CreateTransferBlocks.ANDESITE_ENCASED_TRANSMISSION_CHAIN, CreateTransferBlocks.BRASS_ENCASED_TRANSMISSION_CHAIN)
			.register();

	private CreateTransferBlockEntities() {}

	/**
	 * Initializes the class static fields.
	 */
	public static void init()
	{
		CreateTransfer.LOGGER.info("Registering blocks entities for " + CreateTransfer.NAME);
	}
}
