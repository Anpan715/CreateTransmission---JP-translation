package com.serpenssolida.createtransmission.fabric;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CTBuilderTransformersImpl
{
	public static <T extends Block> void encasedTransmissionChain(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String casing) {}

	public static <T extends Block> void noModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {}

	public static void handheldItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelProvider prov) {}
}
