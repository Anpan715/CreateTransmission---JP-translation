package com.serpenssolida.createtransfer;

import com.serpenssolida.createtransfer.blocks.chain.AbstractTransmissionChainBlock;
import com.serpenssolida.createtransfer.blocks.chain.EncasedTransmissionChainBlock;
import com.serpenssolida.createtransfer.blocks.chain.TransmissionChainBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.serpenssolida.createtransfer.CreateTransferBuilderTransformers.transmissionChain;

public class CreateTransferBlocks
{
	public static final BlockEntry<TransmissionChainBlock> TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("transmission_chain", TransmissionChainBlock::new)
			.lang("Transmission Chain")
			.initialProperties(() -> Blocks.REDSTONE_WIRE)
			.tag(AllTags.AllBlockTags.BRITTLE.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.blockstate(CreateTransferBuilderTransformers::noModel)
			.item()
			.model(CreateTransferBuilderTransformers::handheldItem)
			.build()
			.register();

	public static final BlockEntry<EncasedTransmissionChainBlock> ANDESITE_ENCASED_TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("andesite_encased_transmission_chain", EncasedTransmissionChainBlock::getAndesite)
			.lang("Andesite Encased Transmission Chain")
			.initialProperties(() -> Blocks.REDSTONE_WIRE)
			.tag(AllTags.AllBlockTags.BRITTLE.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.transform(EncasingRegistry.addVariantTo(TRANSMISSION_CHAIN))
			.blockstate((c, p) -> transmissionChain(c, p, "andesite", true))
			.register();

	public static final BlockEntry<EncasedTransmissionChainBlock> BRASS_ENCASED_TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("brass_encased_transmission_chain", EncasedTransmissionChainBlock::getBrass)
			.lang("Brass Encased Transmission Chain")
			.initialProperties(() -> Blocks.REDSTONE_WIRE)
			.tag(AllTags.AllBlockTags.BRITTLE.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.transform(EncasingRegistry.addVariantTo(TRANSMISSION_CHAIN))
			.blockstate((c, p) -> transmissionChain(c, p, "brass", true))
			.register();


	private CreateTransferBlocks() {}

	public static void init()
	{
		// load the class and register everything
		CreateTransfer.LOGGER.info("Registering blocks for " + CreateTransfer.NAME);
	}

}
