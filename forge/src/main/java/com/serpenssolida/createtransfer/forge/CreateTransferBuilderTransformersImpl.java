package com.serpenssolida.createtransfer.forge;

import com.serpenssolida.createtransfer.blocks.chain.AbstractTransmissionChainBlock;
import com.serpenssolida.createtransfer.blocks.chain.TransmissionChainBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import org.openjdk.nashorn.internal.ir.CallNode;

public class CreateTransferBuilderTransformersImpl
{
	public static <T extends Block> void transmissionChain(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String casing, boolean uvLock)
	{
		String path = "block/transmission_chain/encased/";
		VariantBlockStateBuilder variantBuilder = prov.getVariantBuilder(ctx.getEntry());

		variantBuilder.forAllStatesExcept(state ->
		{
			Direction facing = state.getValue(AbstractTransmissionChainBlock.FACING);
			boolean isHorizontal = facing.getAxis().isHorizontal();
			int zRot = isHorizontal ? 0 : (facing == Direction.UP ? 270 : 90);
			AbstractTransmissionChainBlock.ChainSide side;
			String modelTypePath = "";
			String sidePath = "";

			if (AbstractTransmissionChainBlock.isConnected(state))
			{
				side = AbstractTransmissionChainBlock.getFirstConnectionSide(state);
				AbstractTransmissionChainBlock.ConnectionType connectionType = AbstractTransmissionChainBlock.getConnection(state, side);
				
				modelTypePath = connectionType == AbstractTransmissionChainBlock.ConnectionType.CHAIN ? "_connected" : "_belt";
				sidePath = "_" + side.toString().toLowerCase();
			}


			ModelFile.ExistingModelFile modelFile = prov.models().getExistingFile(prov.modLoc(path + casing + "_encased_chain" + modelTypePath + sidePath));

			return ConfiguredModel.builder()
								  .rotationY((int) (facing.toYRot() - 180) - (!isHorizontal ? 90 : 0))
								  .rotationX(zRot)
								  .modelFile(modelFile)
								  .build();

		}, AbstractTransmissionChainBlock.WATERLOGGED);
	}

	public static <T extends Block> void noModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov)
	{
		VariantBlockStateBuilder variantBuilder = prov.getVariantBuilder(ctx.getEntry());
		ModelFile.ExistingModelFile modelFile = prov.models().getExistingFile(new ResourceLocation(Create.ID, "block/belt/particle"));

		variantBuilder.forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(modelFile).build(), AbstractTransmissionChainBlock.WATERLOGGED);
	}

	public static void handheldItem(DataGenContext<Item, BlockItem> ctx, RegistrateItemModelProvider prov)
	{
		prov.handheld(ctx);
	}
}
