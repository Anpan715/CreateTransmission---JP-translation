package com.serpenssolida.createtransfer;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CreateTransferBuilderTransformers
{
	private CreateTransferBuilderTransformers(){}

	@ExpectPlatform
	static <T extends Block> void noModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov)
	{
		throw new AssertionError();
	}

	@ExpectPlatform
	static <T extends Block> void transmissionChain(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String casing, boolean uvLock)
	{
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void handheldItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelProvider prov)
	{
		throw new AssertionError();
	}
}
