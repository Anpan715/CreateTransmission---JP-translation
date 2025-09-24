package com.serpenssolida.createtransfer;

import com.serpenssolida.createtransfer.content.chain.EncasedTransmissionChainBlock;
import com.serpenssolida.createtransfer.content.chain.TransmissionChainBlock;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.material.PushReaction;

import static com.serpenssolida.createtransfer.CreateTransferBuilderTransformers.encasedTransmissionChain;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CreateTransferBlocks
{
	static
	{
		CreateTransferCreativeTabs.setTab(CreateTransferCreativeTabs.CREATETRANSFER_TAB_KEY);
	}

	public static final BlockEntry<TransmissionChainBlock> TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("transmission_chain", TransmissionChainBlock::new)
			.lang("Transmission Chain")
			.properties(properties -> properties.pushReaction(PushReaction.DESTROY).noCollission())
			.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag, AllBlockTags.WRENCH_PICKUP.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.transform(axeOrPickaxe())
			.blockstate(CreateTransferBuilderTransformers::noModel)
			.item()
			.model(CreateTransferBuilderTransformers::handheldItem)
			.build()
			.register();

	public static final BlockEntry<EncasedTransmissionChainBlock> ANDESITE_ENCASED_TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("andesite_encased_transmission_chain", EncasedTransmissionChainBlock::getAndesite)
			.lang("Andesite Encased Transmission Chain")
			.properties(properties -> properties.pushReaction(PushReaction.DESTROY).noCollission())
			.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag, AllBlockTags.WRENCH_PICKUP.tag)
			.tag(AllBlockTags.BRITTLE.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.transform(axeOrPickaxe())
			.transform(EncasingRegistry.addVariantTo(TRANSMISSION_CHAIN))
			.blockstate((c, p) -> encasedTransmissionChain(c, p, "andesite"))
			.register();

	public static final BlockEntry<EncasedTransmissionChainBlock> BRASS_ENCASED_TRANSMISSION_CHAIN = CreateTransfer.REGISTRATE
			.block("brass_encased_transmission_chain", EncasedTransmissionChainBlock::getBrass)
			.lang("Brass Encased Transmission Chain")
			.properties(properties -> properties.pushReaction(PushReaction.DESTROY).noCollission())
			.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag, AllBlockTags.WRENCH_PICKUP.tag)
			.transform(BlockStressDefaults.setNoImpact())
			.transform(axeOrPickaxe())
			.transform(EncasingRegistry.addVariantTo(TRANSMISSION_CHAIN))
			.blockstate((c, p) -> encasedTransmissionChain(c, p, "brass"))
			.register();


	private CreateTransferBlocks() {}

	/**
	 * Initializes the class static fields.
	 */
	public static void init()
	{
		CreateTransfer.LOGGER.info("Registering blocks for " + CreateTransfer.NAME);
	}

}
