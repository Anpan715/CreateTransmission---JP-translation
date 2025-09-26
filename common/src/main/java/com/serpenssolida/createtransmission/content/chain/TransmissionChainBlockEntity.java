package com.serpenssolida.createtransmission.content.chain;

import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ChainDirection;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ChainSide;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ConnectionType;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionChainBlockEntity extends SplitShaftBlockEntity
{
	public TransmissionChainBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	protected boolean isNoisy() {
		return false;
	}

	public boolean isConnected()
	{
		return AbstractTransmissionChainBlock.isConnected(getBlockState());
	}

	@Override
	public float getRotationSpeedModifier(Direction face)
	{
		if (!hasSource())
			return 1;

		ChainDirection facing = ChainDirection.of(getBlockState().getValue(TransmissionChainBlock.FACING));

		//Edge case when connected to adjacent belt.
		if (isConnected() && facing.direction != face)
		{
			ChainSide side = facing.getSideFromDirection(face);
			ConnectionType connectionType = TransmissionChainBlock.getConnection(getBlockState(), side);

			if (connectionType == ConnectionType.BELT)
				return (facing.getAxis() == Direction.Axis.Z && side == ChainSide.LEFT) || (facing.getAxis() == Direction.Axis.X && side == ChainSide.RIGHT) ? 1 : -1;
		}

		return 1;
	}
}
