package com.serpenssolida.createtransmission.content.chain;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TransmissionChainBlock extends AbstractTransmissionChainBlock implements EncasableBlock
{

	public TransmissionChainBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult ray)
	{
		if (player.isShiftKeyDown() || !player.mayBuild())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		ItemStack heldItem = player.getItemInHand(interactionHand);
		ItemInteractionResult result = tryEncase(state, world, pos, heldItem, player, interactionHand, ray);
		if (result.consumesAction())
			return result;

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public ItemInteractionResult tryEncase(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand interactionHand, BlockHitResult ray)
	{
		return EncasableBlock.super.tryEncase(state, level, pos, heldItem, player, interactionHand, ray);
	}

	@Override
	public void playEncaseSound(Level level, BlockPos pos)
	{
		EncasableBlock.super.playEncaseSound(level, pos);
	}
}

