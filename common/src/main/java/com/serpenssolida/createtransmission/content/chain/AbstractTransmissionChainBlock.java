package com.serpenssolida.createtransmission.content.chain;

import com.serpenssolida.createtransmission.CTBlockEntities;
import com.serpenssolida.createtransmission.CTBlocks;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ChainConnection;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ChainDirection;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ChainSide;
import com.serpenssolida.createtransmission.content.chain.TransmissionChainHelpers.ConnectionType;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.serpenssolida.createtransmission.CTShapes.*;

public abstract class AbstractTransmissionChainBlock extends KineticBlock implements IBE<TransmissionChainBlockEntity>, ProperWaterloggedBlock
{
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
	public static final EnumProperty<ConnectionType> CONNECTION_TOP = EnumProperty.create("top", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_RIGHT = EnumProperty.create("right", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_BOTTOM = EnumProperty.create("bottom", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_LEFT = EnumProperty.create("left", ConnectionType.class);

	protected AbstractTransmissionChainBlock(Properties properties)
	{
		super(properties);

		this.registerDefaultState(this.getStateDefinition()
									  .any()
									  .setValue(FACING, Direction.NORTH)
									  .setValue(CONNECTION_TOP, ConnectionType.NONE)
									  .setValue(CONNECTION_RIGHT, ConnectionType.NONE)
									  .setValue(CONNECTION_BOTTOM, ConnectionType.NONE)
									  .setValue(CONNECTION_LEFT, ConnectionType.NONE)
									  .setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder.add(FACING, CONNECTION_TOP, CONNECTION_RIGHT, CONNECTION_BOTTOM, CONNECTION_LEFT, WATERLOGGED));
	}

	@Override
	public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder)
	{
		return List.of(new ItemStack(CTBlocks.TRANSMISSION_CHAIN.get()));
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		return InteractionResult.PASS;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		Direction facing = state.getValue(FACING);
		BlockPos otherPos = pos.offset(facing.getNormal());
		BlockEntity otherEntity = world.getBlockEntity(otherPos);

		if (otherEntity instanceof BeltBlockEntity beltEntity)
		{
			BeltBlock beltBlock = (BeltBlock) beltEntity.getBlockState().getBlock();
			return beltBlock.hasShaftTowards(world, otherPos, beltEntity.getBlockState(), facing.getOpposite());
		}

		return false;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context)
	{
		return InteractionResult.FAIL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
	{
		ChainConnection connection = getFirstConnection(state);
		Direction facing = state.getValue(FACING);

		if (connection.side() == null)
			return CHAIN.get(facing);

		if (connection.type() == ConnectionType.CHAIN)
			return switch (connection.side())
			{
				case TOP -> CHAIN_CONNECTED_TOP.get(facing);
				case RIGHT -> CHAIN_CONNECTED_RIGHT.get(facing);
				case BOTTOM -> CHAIN_CONNECTED_BOTTOM.get(facing);
				case LEFT -> CHAIN_CONNECTED_LEFT.get(facing);
			};
		else if (connection.type() == ConnectionType.BELT)
			return switch (connection.side())
			{
				case TOP -> CHAIN_BELT_TOP.get(facing);
				case RIGHT -> CHAIN_BELT_RIGHT.get(facing);
				case BOTTOM -> CHAIN_BELT_BOTTOM.get(facing);
				case LEFT -> CHAIN_BELT_LEFT.get(facing);
			};

		return CHAIN.get(facing);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		//Face the clicked block.
		BlockPos clickedPos = context.getClickedPos();
		Direction facing = context.getClickedFace().getOpposite();
		BlockState state = defaultBlockState().setValue(FACING, facing);

		//Handle waterlogging.
		state = withWater(state, context);

		//Update the state.
		return updateState(state, clickedPos, context.getLevel());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction face, BlockState neighbour, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
	{
		//Auto break chain if it is in an invalid state.
		if (!canSurvive(state, world, currentPos))
			return Blocks.AIR.defaultBlockState();

		//Handle state update and waterlogging.
		BlockState newState = updateState(state, currentPos, world);
		updateWater(world, newState, currentPos);

		return newState;
	}

	/**
	 * Updates the state according to surrounding blocks.
	 * @param state state that will get updated.
	 * @param pos location of the block.
	 * @param world the world the block is in.
	 *
	 * @return the new updated state.
	 */
	private BlockState updateState(BlockState state, BlockPos pos, LevelAccessor world)
	{
		boolean wasConnected = isConnected(state);

		if (wasConnected)
		{
			//Get the connection and update it if was removed.
			ChainConnection oldConnection = getFirstConnection(state);
			ConnectionType connection = queryWorldForConnection(world, pos, state, oldConnection.side());

			if (connection == ConnectionType.NONE)
				state = state.setValue(oldConnection.side().property, connection);

			return state;
		}

		//Try to find a new connection and update it if found.
		ChainConnection firstConnection = findFirstConnection(world, pos, state);

		if (firstConnection.side() != null && firstConnection.type() != ConnectionType.NONE)
			state = state.setValue(firstConnection.side().property, firstConnection.type());

		return state;
	}

	/**
	 * Finds the first connection available to the chain by querying the world for block.
	 * @param world the world the bloc is in.
	 * @param pos location of the block.
	 * @param state current state of the block.
	 *
	 * @return the connection if one is found.
	 */
	private static ChainConnection findFirstConnection(LevelAccessor world, BlockPos pos, BlockState state)
	{
		//Check all side for a connection.
		for (ChainSide side : ChainSide.values())
		{
			//Get connection type.
			ConnectionType connection = queryWorldForConnection(world, pos, state, side);

			//The block was connected and a type has been lost.
			if (connection != ConnectionType.NONE)
				return new ChainConnection(side, connection);
		}

		return new ChainConnection(null, ConnectionType.NONE);
	}

	/**
	 * Finds the connection type by querying the world for block on the given side.
	 * @param world the world the block is in.
	 * @param pos location of the block.
	 * @param state current state of the block.
	 * @param side the side of the connection.
	 *
	 * @return the connection type on the given side.
	 */
	private static ConnectionType queryWorldForConnection(LevelAccessor world, BlockPos pos, BlockState state, ChainSide side)
	{
		ChainDirection facing = ChainDirection.of(state.getValue(FACING));
		BlockPos neighbourPos = pos.offset(facing.getDirectionFromSide(side).getNormal());
		BlockEntity neighbourEntity = world.getBlockEntity(neighbourPos);

		if (neighbourEntity == null)
			return ConnectionType.NONE;

		BlockState neighbourState = neighbourEntity.getBlockState();

		if (neighbourEntity instanceof TransmissionChainBlockEntity chainEntity)
		{
			ConnectionType otherConnection = neighbourState.getValue(ChainSide.opposite(side).property);
			if (facing.direction == neighbourState.getValue(FACING) && ((otherConnection == ConnectionType.CHAIN && chainEntity.isConnected()) || (otherConnection == ConnectionType.NONE && !chainEntity.isConnected())))
				return ConnectionType.CHAIN;
		}
		else if (neighbourEntity instanceof BeltBlockEntity)
		{
			BeltBlock belt = (BeltBlock) neighbourState.getBlock();
			Direction directionBeltToChain = facing.getDirectionFromSide(side).getOpposite();

			boolean hasShaftTowards = belt.hasShaftTowards(world, neighbourPos, neighbourState, directionBeltToChain);

			if (hasShaftTowards)
				return ConnectionType.BELT;
		}

		return ConnectionType.NONE;
	}

	/**
	 * Gets the first available connection from the block state. It does not query the world for neighbour blocks.
	 * @param state the state of the block.
	 *
	 * @return a {@link ChainConnection} representing the first available connection. If none was found ChainConnection.side will be null.
	 */
	public static ChainConnection getFirstConnection(BlockState state)
	{
		if (!isConnected(state))
			return new ChainConnection(null, ConnectionType.NONE);

		for (ChainSide side : ChainSide.values())
		{
			ConnectionType connection = AbstractTransmissionChainBlock.getConnection(state, side);
			if (connection != ConnectionType.NONE)
				return new ChainConnection(side, connection);
		}

		return new ChainConnection(null, ConnectionType.NONE);
	}

	/**
	 * Gets the connection type on the given side from the block state. It does not query the world for neighbour blocks.
	 * @param state the state of the block.
	 * @param side the side of the block.
	 *
	 * @return a {@link ConnectionType} representing the connection type.
	 */
	public static ConnectionType getConnection(BlockState state, ChainSide side)
	{
		if (!(state.getBlock() instanceof AbstractTransmissionChainBlock))
			return ConnectionType.NONE;

		if (side == null)
			return ConnectionType.NONE;

		return state.getValue(side.property);
	}

	/**
	 * Checks if the given state represents a chain that is connected. It does not query the world for neighbour blocks.
	 * @param state state of the block.
	 *
	 * @return true if in the given state the chain is connected, false otherwise.
	 */
	public static boolean isConnected(BlockState state)
	{
		return state.getValue(AbstractTransmissionChainBlock.CONNECTION_TOP) != ConnectionType.NONE ||
				state.getValue(AbstractTransmissionChainBlock.CONNECTION_RIGHT) != ConnectionType.NONE ||
				state.getValue(AbstractTransmissionChainBlock.CONNECTION_BOTTOM) != ConnectionType.NONE ||
				state.getValue(AbstractTransmissionChainBlock.CONNECTION_LEFT) != ConnectionType.NONE;
	}

	@Override
	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState)
	{
		ChainConnection oldConnection = getFirstConnection(oldState);
		ChainConnection newConnection = getFirstConnection(newState);

		return super.areStatesKineticallyEquivalent(oldState, newState) && oldConnection.equals(newConnection);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		Direction facing = state.getValue(FACING);
		state = state.setValue(FACING, mirror.mirror(facing));
		state = mirrorSide(state, mirror);

		return super.mirror(state, mirror);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		Direction facing = state.getValue(FACING);

		if (rotation == Rotation.NONE)
			return state;

		if (facing.getAxis().isVertical())
			return rotateSides(state, rotation == Rotation.CLOCKWISE_180 ? rotation : rotation.getRotated(Rotation.CLOCKWISE_180));

		state = state.setValue(FACING, rotation.rotate(facing));

		return state;
	}

	/**
	 * Mirrors the sides inside the state by switching their values accordingly.
	 * @param state the state of the block.
	 * @param mirror the mirror operation.
	 *
	 * @return the state with mirrored sides.
	 */
	private BlockState mirrorSide(BlockState state, Mirror mirror)
	{
		Direction facing = state.getValue(FACING);

		if (mirror == Mirror.NONE)
			return state;

		if (facing.getAxis().isHorizontal())
		{
			ConnectionType right = state.getValue(CONNECTION_RIGHT);
			ConnectionType left = state.getValue(CONNECTION_LEFT);
			state = state.setValue(CONNECTION_LEFT, right);
			state = state.setValue(CONNECTION_RIGHT, left);
			return state;
		}

		if (mirror == Mirror.FRONT_BACK)
		{
			ConnectionType right = state.getValue(CONNECTION_RIGHT);
			ConnectionType left = state.getValue(CONNECTION_LEFT);
			state = state.setValue(CONNECTION_LEFT, right);
			state = state.setValue(CONNECTION_RIGHT, left);
		}
		else if (mirror == Mirror.LEFT_RIGHT)
		{
			ConnectionType top = state.getValue(CONNECTION_TOP);
			ConnectionType bottom = state.getValue(CONNECTION_BOTTOM);
			state = state.setValue(CONNECTION_BOTTOM, top);
			state = state.setValue(CONNECTION_TOP, bottom);
		}

		return state;
	}

	/**
	 * Rotates the sides inside the state by switching their values accordingly.
	 * @param state the state of the block.
	 * @param rotation the rotation operation.
	 *
	 * @return the state with rotated sides.
	 */
	private static BlockState rotateSides(BlockState state, Rotation rotation)
	{
		Direction facing = state.getValue(FACING);

		ConnectionType top = state.getValue(CONNECTION_TOP);
		ConnectionType right = state.getValue(CONNECTION_RIGHT);
		ConnectionType bottom = state.getValue(CONNECTION_BOTTOM);
		ConnectionType left = state.getValue(CONNECTION_LEFT);

		if (facing == Direction.DOWN && rotation != Rotation.CLOCKWISE_180)
			rotation = rotation == Rotation.CLOCKWISE_90 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;

		state = state.setValue(ChainSide.rotate(ChainSide.TOP, rotation).property, top);
		state = state.setValue(ChainSide.rotate(ChainSide.RIGHT, rotation).property, right);
		state = state.setValue(ChainSide.rotate(ChainSide.BOTTOM, rotation).property, bottom);
		state = state.setValue(ChainSide.rotate(ChainSide.LEFT, rotation).property, left);

		return state;
	}

	//ProperWaterloggedBlock
	@Override
	public FluidState getFluidState(BlockState blockState)
	{
		return fluidState(blockState);
	}

	//IRotate
	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction direction)
	{
		ChainDirection facing = ChainDirection.of(state.getValue(FACING));

		if (facing.direction == direction)
			return true;

		ChainSide side = facing.getSideFromDirection(direction);
		ConnectionType connection = AbstractTransmissionChainBlock.getConnection(state, side);

		return connection != ConnectionType.NONE;
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state)
	{
		return state.getValue(FACING).getAxis();
	}

	@Override
	public Class<TransmissionChainBlockEntity> getBlockEntityClass()
	{
		return TransmissionChainBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TransmissionChainBlockEntity> getBlockEntityType()
	{
		return CTBlockEntities.TRANSMISSION_CHAIN.get();
	}
}