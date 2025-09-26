package com.serpenssolida.createtransmission.content.chain;

import com.google.common.collect.ImmutableBiMap;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static com.serpenssolida.createtransmission.content.chain.AbstractTransmissionChainBlock.*;

public class TransmissionChainHelpers
{
	/**
	 * Enum representing the connection types of a transmission chain.
	 */
	public enum ConnectionType implements StringRepresentable
	{
		NONE, CHAIN, BELT;

		@Override
		public String getSerializedName()
		{
			return Lang.asId(name());
		}
	}

	/**
	 * Record representing a connection of a transmission chain block.
	 * @param side the side of the connection.
	 * @param type the type of the connection.
	 */
	public record ChainConnection(ChainSide side, ConnectionType type) {}

	/**
	 *  Enum representing the sides of all possible neighbours of the transmission chain.
	 */
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

	/**
	 *  Enum that helps to convert side values and direction value based on the direction the transmission chain is facing.
	 */
	public enum ChainDirection implements StringRepresentable
	{
		DOWN(Direction.DOWN, ImmutableBiMap.of(ChainSide.TOP, Direction.NORTH, ChainSide.RIGHT, Direction.EAST, ChainSide.BOTTOM, Direction.SOUTH, ChainSide.LEFT, Direction.WEST)),
		UP(Direction.UP, ImmutableBiMap.of(ChainSide.TOP, Direction.SOUTH, ChainSide.RIGHT, Direction.EAST, ChainSide.BOTTOM, Direction.NORTH, ChainSide.LEFT, Direction.WEST)),
		NORTH(Direction.NORTH, ImmutableBiMap.of(ChainSide.TOP, Direction.UP, ChainSide.RIGHT, Direction.EAST, ChainSide.BOTTOM, Direction.DOWN, ChainSide.LEFT, Direction.WEST)),
		SOUTH(Direction.SOUTH, ImmutableBiMap.of(ChainSide.TOP, Direction.UP, ChainSide.RIGHT, Direction.WEST, ChainSide.BOTTOM, Direction.DOWN, ChainSide.LEFT, Direction.EAST)),
		WEST(Direction.WEST, ImmutableBiMap.of(ChainSide.TOP, Direction.UP, ChainSide.RIGHT, Direction.NORTH, ChainSide.BOTTOM, Direction.DOWN, ChainSide.LEFT, Direction.SOUTH)),
		EAST(Direction.EAST, ImmutableBiMap.of(ChainSide.TOP, Direction.UP, ChainSide.RIGHT, Direction.SOUTH, ChainSide.BOTTOM, Direction.DOWN, ChainSide.LEFT, Direction.NORTH));

		public final Direction direction;
		private final ImmutableBiMap<ChainSide, Direction> directionMap;

		ChainDirection(Direction direction, ImmutableBiMap<ChainSide, Direction> directionMap)
		{
			this.direction = direction;
			this.directionMap = directionMap;
		}

		public static ChainDirection of(Direction direction)
		{
			return switch (direction)
			{
				case DOWN -> DOWN;
				case UP -> UP;
				case NORTH -> NORTH;
				case SOUTH -> SOUTH;
				case WEST -> WEST;
				case EAST -> EAST;
			};
		}

		public Direction.Axis getAxis()
		{
			return direction.getAxis();
		}

		public Direction getDirectionFromSide(ChainSide side)
		{
			return directionMap.get(side);
		}

		public ChainSide getSideFromDirection(Direction direction)
		{
			return directionMap.inverse().get(direction);
		}

		@Override
		public String getSerializedName()
		{
			return Lang.asId(name());
		}
	}
}
