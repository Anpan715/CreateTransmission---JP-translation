package com.serpenssolida.createtransmission;

import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

public class CTShapes
{
	private CTShapes() {}

	//Base chain shapes.
	private static final VoxelShape SHAPE_CHAIN = box(new Vec3(5, 5, 0), new Vec3(11, 11, 2));
	private static final VoxelShape SHAPE_CHAIN_CONNECTED = box(new Vec3(5, 5, 0), new Vec3(16, 11, 2));
	private static final VoxelShape SHAPE_CHAIN_BELT = Shapes.or(SHAPE_CHAIN_CONNECTED, rotated(SHAPE_CHAIN_CONNECTED, new Vec3(180, 90, 0)));

	//Chain
	public static final VoxelShaper CHAIN = new Builder(SHAPE_CHAIN).forDirectional();

	//Chain connected to another chain.
	public static final VoxelShaper CHAIN_CONNECTED_RIGHT = new Builder(SHAPE_CHAIN_CONNECTED).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_BOTTOM = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 270))).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_LEFT = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 180))).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_TOP = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 90))).forDirectional();

	//Chain connected to a belt.
	public static final VoxelShaper CHAIN_BELT_RIGHT = new Builder(SHAPE_CHAIN_BELT).forDirectional();
	public static final VoxelShaper CHAIN_BELT_BOTTOM = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 270))).forDirectional();
	public static final VoxelShaper CHAIN_BELT_LEFT = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 180))).forDirectional();
	public static final VoxelShaper CHAIN_BELT_TOP = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 90))).forDirectional();


	/**
	 * Rotates the given {@link VoxelShape} around the given axes rotation.
	 *
	 * @param shape the shape to rotate.
	 * @param rotation angles of the rotation in degrees.
	 *
	 * @return the rotated shape.
	 */
	protected static VoxelShape rotated(VoxelShape shape, Vec3 rotation)
	{
		if (rotation.equals(Vec3.ZERO))
			return shape;

		MutableObject<VoxelShape> result = new MutableObject<>(Shapes.empty());
		Vec3 center = new Vec3(8, 8, 8);

		shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
		{
			Vec3 v1 = new Vec3(x1, y1, z1).scale(16)
										  .subtract(center);
			Vec3 v2 = new Vec3(x2, y2, z2).scale(16)
										  .subtract(center);

			v1 = VecHelper.rotate(v1, (float) rotation.x, Direction.Axis.X);
			v1 = VecHelper.rotate(v1, (float) rotation.y, Direction.Axis.Y);
			v1 = VecHelper.rotate(v1, (float) rotation.z, Direction.Axis.Z)
						  .add(center);

			v2 = VecHelper.rotate(v2, (float) rotation.x, Direction.Axis.X);
			v2 = VecHelper.rotate(v2, (float) rotation.y, Direction.Axis.Y);
			v2 = VecHelper.rotate(v2, (float) rotation.z, Direction.Axis.Z)
						  .add(center);

			VoxelShape rotated = box(v1, v2);
			result.setValue(Shapes.or(result.getValue(), rotated));
		});

		return result.getValue();
	}

	/**
	 * Wrapper for the function Block.box() that uses two Vec3 instead of triplets of floats.
	 * Creates a new box shape.
	 *
	 * @param v1 first corner of the box.
	 * @param v2 second corner of the box.
	 *
	 * @return a box defined by the given corners.
	 */
	protected static VoxelShape box(Vec3 v1, Vec3 v2)
	{
		return Block.box(
				Math.min(v1.x, v2.x),
				Math.min(v1.y, v2.y),
				Math.min(v1.z, v2.z),
				Math.max(v1.x, v2.x),
				Math.max(v1.y, v2.y),
				Math.max(v1.z, v2.z));
	}

	/**
	 * Builder used to easily create shape and variants for a given {@link VoxelShape}.
	 */
	public static class Builder
	{
		private VoxelShape shape;

		/**
		 * Creates a new builder with the given shape.
		 * @param shape the shape for the builder.
		 */
		public Builder(VoxelShape shape)
		{
			this.shape = shape;
		}

		/**
		 * Merges the given shape with the builder shape.
		 * @param shape the shape to merge
		 *
		 * @return the builder for method chaining.
		 */
		public Builder add(VoxelShape shape)
		{
			this.shape = Shapes.or(this.shape, shape);
			return this;
		}

		/**
		 * Finalizes shape building.
		 * @return the result of the builder.
		 */
		public VoxelShape build()
		{
			return shape;
		}

		/**
		 * Creates a {@link VoxelShaper} with directional rotation of the shape.
		 *
		 * @return a voxel shaper containing all directional variant of the shape.
		 */
		public VoxelShaper forDirectional()
		{
			return new VoxelShaper().withShape(shape, Direction.NORTH)
									.withShape(rotated(shape, new Vec3(0, 270, 0)), Direction.EAST)
									.withShape(rotated(shape, new Vec3(0, 180, 0)), Direction.SOUTH)
									.withShape(rotated(shape, new Vec3(0, 90, 0)), Direction.WEST)
									.withShape(rotated(shape, new Vec3(90, 0, 0)), Direction.UP)
									.withShape(rotated(shape, new Vec3(270, 0, 0)), Direction.DOWN);
		}
	}
}
