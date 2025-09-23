package com.serpenssolida.createtransfer.fabric;

import com.serpenssolida.createtransfer.blocks.chain.AbstractTransmissionChainBlock;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.block.VariantBlockStateBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CreateTransferBuilderTransformersImpl
{
	public static <T extends Block> void transmissionChain(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String casing, boolean uvLock) {}

	public static <T extends Block> void noModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {}

	public static void handheldItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelProvider prov) {}
}
