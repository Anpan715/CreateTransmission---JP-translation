package com.serpenssolida.createtransmission.content.chain;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		if (player.isShiftKeyDown() || !player.mayBuild())
			return InteractionResult.PASS;

		ItemStack heldItem = player.getItemInHand(hand);
		InteractionResult result = tryEncase(state, world, pos, heldItem, player, hand, ray);
		if (result.consumesAction())
			return result;

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult tryEncase(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray)
	{
		return EncasableBlock.super.tryEncase(state, level, pos, heldItem, player, hand, ray);
	}

	@Override
	public void playEncaseSound(Level level, BlockPos pos)
	{
		EncasableBlock.super.playEncaseSound(level, pos);
	}
}

