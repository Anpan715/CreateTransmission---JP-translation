package com.serpenssolida.createtransfer.blocks.chain;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.serpenssolida.createtransfer.CreateTransfer;
import com.serpenssolida.createtransfer.CreateTransferBlockEntities;
import com.serpenssolida.createtransfer.CreateTransferBlocks;
import com.serpenssolida.createtransfer.CreateTransferShapes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.serpenssolida.createtransfer.CreateTransferShapes.*;

public abstract class AbstractTransmissionChainBlock extends KineticBlock implements IBE<TransmissionChainBlockEntity>, ProperWaterloggedBlock
{
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
	public static final EnumProperty<ConnectionType> CONNECTION_TOP = EnumProperty.create("top", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_RIGHT = EnumProperty.create("right", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_BOTTOM = EnumProperty.create("bottom", ConnectionType.class);
	public static final EnumProperty<ConnectionType> CONNECTION_LEFT = EnumProperty.create("left", ConnectionType.class);
	private static final Map<Direction, BiMap<ChainSide, Direction>> DIRECTION_NEIGHBOR;
	//private static final BiMap<ChainSide, EnumProperty<ConnectionType>> CONNECTION_MAP;

	static
	{
		DIRECTION_NEIGHBOR = new EnumMap<>(Direction.class);
		DIRECTION_NEIGHBOR.put(Direction.UP, ImmutableBiMap.of(
				ChainSide.TOP, Direction.SOUTH,
				ChainSide.RIGHT, Direction.EAST,
				ChainSide.BOTTOM, Direction.NORTH,
				ChainSide.LEFT, Direction.WEST
		));
		DIRECTION_NEIGHBOR.put(Direction.DOWN, ImmutableBiMap.of(
				ChainSide.TOP, Direction.NORTH,
				ChainSide.RIGHT, Direction.EAST,
				ChainSide.BOTTOM, Direction.SOUTH,
				ChainSide.LEFT, Direction.WEST
		));
		DIRECTION_NEIGHBOR.put(Direction.NORTH, ImmutableBiMap.of(
				ChainSide.TOP, Direction.UP,
				ChainSide.RIGHT, Direction.EAST,
				ChainSide.BOTTOM, Direction.DOWN,
				ChainSide.LEFT, Direction.WEST
		));
		DIRECTION_NEIGHBOR.put(Direction.EAST, ImmutableBiMap.of(
				ChainSide.TOP, Direction.UP,
				ChainSide.RIGHT, Direction.SOUTH,
				ChainSide.BOTTOM, Direction.DOWN,
				ChainSide.LEFT, Direction.NORTH
		));
		DIRECTION_NEIGHBOR.put(Direction.SOUTH, ImmutableBiMap.of(
				ChainSide.TOP, Direction.UP,
				ChainSide.RIGHT, Direction.WEST,
				ChainSide.BOTTOM, Direction.DOWN,
				ChainSide.LEFT, Direction.EAST
		));
		DIRECTION_NEIGHBOR.put(Direction.WEST, ImmutableBiMap.of(
				ChainSide.TOP, Direction.UP,
				ChainSide.RIGHT, Direction.NORTH,
				ChainSide.BOTTOM, Direction.DOWN,
				ChainSide.LEFT, Direction.SOUTH
		));

		//CONNECTION_MAP = ImmutableBiMap.of(ChainSide.TOP, CONNECTION_TOP, ChainSide.RIGHT, CONNECTION_RIGHT, ChainSide.BOTTOM, CONNECTION_BOTTOM, ChainSide.LEFT, CONNECTION_LEFT);
	}

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
		return List.of(new ItemStack(CreateTransferBlocks.TRANSMISSION_CHAIN.get()));
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

		return true;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context)
	{
		return InteractionResult.FAIL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockPos clickedPos = context.getClickedPos();
		Direction facing = context.getClickedFace().getOpposite();
		BlockState state = defaultBlockState()
				.setValue(FACING, facing);

		//Handle waterlogging.
		state = withWater(state, context);

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

		return state;
	}

	private BlockState updateState(BlockState state, BlockPos pos, LevelAccessor world)
	{
		Direction facing = state.getValue(FACING);

		boolean wasConnected = isConnected(state);

		for (ChainSide side : ChainSide.values())
		{
			BlockPos neighbourPos = pos.offset(AbstractTransmissionChainBlock.getDirectionFromSide(facing, side).getNormal());
			BlockEntity neighbourEntity = world.getBlockEntity(neighbourPos);
			ConnectionType connection = AbstractTransmissionChainBlock.getConnectionType(world, pos, state, side, neighbourEntity);

			if (!wasConnected && connection != ConnectionType.NONE)
			{
				state = state.setValue(side.property, connection);
				break;
			}

			if (wasConnected && connection == ConnectionType.NONE)
				state = state.setValue(side.property, connection);
		}

		return state;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext)
	{
		ChainSide side = getFirstConnectionSide(state);
		var connection = getConnection(state, side);
		final Direction facing = state.getValue(FACING);

		if (side == null)
			return CHAIN.get(facing);

		if (connection == ConnectionType.CHAIN)
			return switch (side)
			{
				case TOP -> CHAIN_CONNECTED_TOP.get(facing);
				case RIGHT -> CHAIN_CONNECTED_RIGHT.get(facing);
				case BOTTOM -> CHAIN_CONNECTED_BOTTOM.get(facing);
				case LEFT -> CHAIN_CONNECTED_LEFT.get(facing);
			};
		else if (connection == ConnectionType.BELT)
			return switch (side)
			{
				case TOP -> CHAIN_BELT_TOP.get(facing);
				case RIGHT -> CHAIN_BELT_RIGHT.get(facing);
				case BOTTOM -> CHAIN_BELT_BOTTOM.get(facing);
				case LEFT -> CHAIN_BELT_LEFT.get(facing);
			};

		return CHAIN.get(facing);
	}

	private static ConnectionType getConnectionType(LevelAccessor world, BlockPos pos, BlockState state, ChainSide side, BlockEntity otherEntity)
	{
		Direction facing = state.getValue(FACING);
		if (otherEntity == null)
			return ConnectionType.NONE;

		BlockState otherState = otherEntity.getBlockState();

		if (otherEntity instanceof TransmissionChainBlockEntity chainEntity)
		{
			ConnectionType otherConnection = otherState.getValue(ChainSide.opposite(side).property);
			if (otherState.getValue(FACING) == facing && ((otherConnection == ConnectionType.CHAIN && chainEntity.isConnected()) || (otherConnection == ConnectionType.NONE && !chainEntity.isConnected())))
				return ConnectionType.CHAIN;
		}
		else if (otherEntity instanceof BeltBlockEntity)
		{
			BeltBlock belt = (BeltBlock) otherState.getBlock();
			Direction toBelt = getDirectionFromSide(facing, side);
			boolean hasShaftTowards = belt.hasShaftTowards(world, pos.offset(toBelt.getNormal()), otherEntity.getBlockState(), toBelt.getOpposite());

			if (hasShaftTowards)
				return ConnectionType.BELT;
		}

		return ConnectionType.NONE;
	}

	public static Direction getDirectionFromSide(Direction facing, ChainSide side)
	{
		return DIRECTION_NEIGHBOR.get(facing).get(side);
	}

	public static ChainSide getSideFromDirection(Direction facing, Direction direction)
	{
		return DIRECTION_NEIGHBOR.get(facing).inverse().get(direction);
	}

	public static ConnectionType getConnection(BlockState state, ChainSide side)
	{
		if (!(state.getBlock() instanceof AbstractTransmissionChainBlock))
			return ConnectionType.NONE;

		if (side == null)
			return ConnectionType.NONE;

		return state.getValue(side.property);
	}

	public static ChainSide getFirstConnectionSide(BlockState state)
	{
		if (!isConnected(state))
			return null;

		for (ChainSide side : ChainSide.values())
		{
			if (AbstractTransmissionChainBlock.getConnection(state, side) != ConnectionType.NONE)
				return side;
		}

		return null;
	}

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
		return super.areStatesKineticallyEquivalent(oldState, newState) && getFirstConnectionSide(oldState) == getFirstConnectionSide(newState);
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
		Direction facing = state.getValue(FACING);

		if (facing == direction)
			return true;

		ChainSide side = AbstractTransmissionChainBlock.getSideFromDirection(facing, direction);
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
		return CreateTransferBlockEntities.TRANSMISSION_CHAIN.get();
	}

	public enum ConnectionType implements StringRepresentable
	{
		NONE, CHAIN, BELT;

		@Override
		public String getSerializedName()
		{
			return Lang.asId(name());
		}
	}

	public enum ChainSide implements StringRepresentable
	{
		TOP(Math.PI * 0.5, CONNECTION_TOP), RIGHT(0, CONNECTION_RIGHT), BOTTOM(Math.PI * 1.5, CONNECTION_BOTTOM), LEFT(Math.PI, CONNECTION_LEFT);

		final double rotationAngle;
		final EnumProperty<ConnectionType> property;

		ChainSide(double rotationAngle, EnumProperty<ConnectionType> property)
		{
			this.rotationAngle = rotationAngle;
			this.property = property;
		}

		@Override
		public String getSerializedName()
		{
			return Lang.asId(name());

		}

		public static ChainSide opposite(ChainSide side)
		{
			return switch (side)
			{
				case TOP -> BOTTOM;
				case RIGHT -> LEFT;
				case BOTTOM -> TOP;
				case LEFT -> RIGHT;
			};
		}

		public static ChainSide rotate(ChainSide side, Rotation rotation)
		{
			if (rotation == Rotation.NONE)
				return side;
			else if (rotation == Rotation.COUNTERCLOCKWISE_90)
				return rotate(ChainSide.opposite(side), Rotation.CLOCKWISE_90);
			else if (rotation == Rotation.CLOCKWISE_180)
				return opposite(side);

			return switch (side)
			{
				case TOP -> RIGHT;
				case RIGHT -> BOTTOM;
				case BOTTOM -> LEFT;
				case LEFT -> TOP;
			};
		}
	}
}
