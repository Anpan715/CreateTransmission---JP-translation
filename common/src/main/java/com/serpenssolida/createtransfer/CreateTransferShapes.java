package com.serpenssolida.createtransfer;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.BiFunction;

import static net.minecraft.core.Direction.UP;

public class CreateTransferShapes
{
	private CreateTransferShapes(){}

//	private static final VoxelShape SHAPE_CHAIN = box(new Vec3(4, 14, 4), new Vec3(12, 16, 12));
	private static final VoxelShape SHAPE_CHAIN = box(new Vec3(5, 5, 0), new Vec3(11, 11, 2));
//	private static final VoxelShape SHAPE_CHAIN_CONNECTED = box(new Vec3(0, 14, 4), new Vec3(12, 16, 12));
	private static final VoxelShape SHAPE_CHAIN_CONNECTED = box(new Vec3(5, 5, 0), new Vec3(16, 11, 2));
	private static final VoxelShape SHAPE_CHAIN_BELT = Shapes.or(SHAPE_CHAIN_CONNECTED, rotated(SHAPE_CHAIN_CONNECTED, new Vec3(180, 90, 0)));

	public static final VoxelShaper CHAIN = new Builder(SHAPE_CHAIN).forDirectional();

	public static final VoxelShaper CHAIN_CONNECTED_RIGHT = new Builder(SHAPE_CHAIN_CONNECTED).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_BOTTOM = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 270))).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_LEFT = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 180))).forDirectional();
	public static final VoxelShaper CHAIN_CONNECTED_TOP = new Builder(rotated(SHAPE_CHAIN_CONNECTED, new Vec3(0, 0, 90))).forDirectional();

	public static final VoxelShaper CHAIN_BELT_RIGHT = new Builder(SHAPE_CHAIN_BELT).forDirectional();
	public static final VoxelShaper CHAIN_BELT_BOTTOM = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 270))).forDirectional();
	public static final VoxelShaper CHAIN_BELT_LEFT = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 180))).forDirectional();
	public static final VoxelShaper CHAIN_BELT_TOP = new Builder(rotated(SHAPE_CHAIN_BELT, new Vec3(0, 0, 90))).forDirectional();


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

	public static class Builder
	{
		private VoxelShape shape;

		public Builder(VoxelShape shape) {
			this.shape = shape;
		}

		public Builder add(VoxelShape shape) {
			this.shape = Shapes.or(this.shape, shape);
			return this;
		}

		/*public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
			return add(cuboid(x1, y1, z1, x2, y2, z2));
		}

		public Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
			this.shape = Shapes.join(shape, cuboid(x1, y1, z1, x2, y2, z2), BooleanOp.ONLY_FIRST);
			return this;
		}*/

		public VoxelShape build() {
			return shape;
		}

		public VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
			return factory.apply(shape, direction);
		}

		public VoxelShaper build(BiFunction<VoxelShape, Direction.Axis, VoxelShaper> factory, Direction.Axis axis) {
			return factory.apply(shape, axis);
		}

		public VoxelShaper forDirectional()
		{
			return new VoxelShaper().withShape(shape, Direction.NORTH)
									.withShape(rotated(shape, new Vec3(0, 270, 0)), Direction.EAST)
									.withShape(rotated(shape, new Vec3(0, 180, 0)), Direction.SOUTH)
									.withShape(rotated(shape, new Vec3(0, 90, 0)), Direction.WEST)
									.withShape(rotated(shape, new Vec3(90, 0, 0)), Direction.UP)
									.withShape(rotated(shape, new Vec3(270, 0, 0)), Direction.DOWN);
			//return build(VoxelShaper::forDirectional, direction);
		}

		/*public VoxelShaper forDirectional() {
			return forDirectional(UP);
		}*/

	}
}
