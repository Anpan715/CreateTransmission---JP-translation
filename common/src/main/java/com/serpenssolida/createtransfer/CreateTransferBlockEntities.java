package com.serpenssolida.createtransfer;

import com.serpenssolida.createtransfer.blocks.chain.TransmissionChainBlockEntity;
import com.serpenssolida.createtransfer.blocks.chain.TransmissionChainInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CreateTransferBlockEntities
{
	public static final BlockEntityEntry<TransmissionChainBlockEntity> TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.blockEntity("transmission_chain", TransmissionChainBlockEntity::new)
			.instance(() -> TransmissionChainInstance::new, true)
			.validBlocks(CreateTransferBlocks.TRANSMISSION_CHAIN, CreateTransferBlocks.ANDESITE_ENCASED_TRANSMISSION_CHAIN, CreateTransferBlocks.BRASS_ENCASED_TRANSMISSION_CHAIN)
			//.renderer(() -> KineticBlockEntityRenderer::new)
			.register();

	private CreateTransferBlockEntities()
	{
	}

	public static void init()
	{
		// load the class and register everything
		CreateTransfer.LOGGER.info("Registering blocks for " + CreateTransfer.NAME);
	}
}
