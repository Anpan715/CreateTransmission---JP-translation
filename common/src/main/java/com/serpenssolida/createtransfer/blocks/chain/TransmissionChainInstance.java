package com.serpenssolida.createtransfer.blocks.chain;

import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.Material;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.serpenssolida.createtransfer.CreateTransferModels;
import com.serpenssolida.createtransfer.CreateTransferSpriteShifts;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.BeltData;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;

public class TransmissionChainInstance extends KineticBlockEntityInstance<TransmissionChainBlockEntity>
{
	protected final EnumMap<Direction, RotatingData> keys = new EnumMap<>(Direction.class);
	protected BeltData chainInstance;
	protected Direction sourceFacing;

	public TransmissionChainInstance(MaterialManager materialManager, TransmissionChainBlockEntity blockEntity)
	{
		super(materialManager, blockEntity);

		updateSourceFacing();

		Direction facing = blockEntity.getBlockState().getValue(TransmissionChainBlock.FACING);
		TransmissionChainBlock.ChainSide side = getFirstConnectionSide();
		TransmissionChainBlock.ConnectionType connectionType = TransmissionChainBlock.ConnectionType.NONE;

		if (side != null)
			connectionType = blockEntity.getBlockState().getValue(side.property);

		//Shaft model instances.
		keys.put(facing, getFacingShaft());

		//Chain model instance
		Instancer<BeltData> chainKey = materialManager.defaultSolid()
													  .material(AllMaterialSpecs.BELTS)
													  .getModel(getChainModel(connectionType));

		chainInstance = getChain(chainKey.createInstance(), CreateTransferSpriteShifts.BELT);

		Direction direction = TransmissionChainBlock.getDirectionFromSide(facing, side);

		if (connectionType != TransmissionChainBlock.ConnectionType.BELT)
			return;

		//Belt shafts
		RotatingData key = getShaft(direction);
		keys.put(direction, key);

	}

	private PartialModel getChainModel(TransmissionChainBlock.ConnectionType connectionType)
	{
		if (connectionType == TransmissionChainBlock.ConnectionType.NONE)
			return CreateTransferModels.CHAIN;

		return connectionType == TransmissionChainBlock.ConnectionType.CHAIN ? CreateTransferModels.CHAIN_CONNECTED : CreateTransferModels.CHAIN_BELT;
	}

	protected void updateSourceFacing()
	{
		if (!blockEntity.hasSource())
		{
			sourceFacing = null;
			return;
		}

		BlockPos source = blockEntity.source.subtract(pos);
		sourceFacing = Direction.getNearest(source.getX(), source.getY(), source.getZ());
	}

	@Override
	public void update()
	{
		Direction facing = blockEntity.getBlockState().getValue(TransmissionChainBlock.FACING);

		updateSourceFacing();
		for (Map.Entry<Direction, RotatingData> key : keys.entrySet())
		{
			Direction direction = key.getKey();
			Direction.Axis axis = direction.getAxis();

			updateRotation(key.getValue(), axis, getSpeed(direction) * blockEntity.getRotationSpeedModifier(direction));
		}

		chainInstance.setScrollTexture(CreateTransferSpriteShifts.BELT)
		   .setColor(blockEntity)
		   .setRotationalSpeed(getChainRotationalSpeed(facing) * blockEntity.getRotationSpeedModifier(facing));
	}



	@Override
	public void updateLight()
	{
		keys.forEach((direction, rotatingData) -> relight(pos, rotatingData));

		if (chainInstance != null)
			relight(pos, chainInstance);
	}

	private RotatingData getFacingShaft()
	{
		Direction facing = blockEntity.getBlockState().getValue(TransmissionChainBlock.FACING);

		return getShaft(facing);
	}

	private RotatingData getShaft(Direction direction)
	{
		int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
		int skyLight = world.getBrightness(LightLayer.SKY, pos);

		Material<RotatingData> rotatingMaterial = getRotatingMaterial();
		Instancer<RotatingData> shaft = rotatingMaterial.getModel(CreateTransferModels.CHAIN_SHAFT, blockState, direction);

		Vector3f location = new Vector3f(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
		Vector3f offset = direction.step().mul(0.03f);

		RotatingData key = shaft.createInstance();
		key.setRotationAxis(direction.getAxis())
		   .setRotationalSpeed(getSpeed(direction) * blockEntity.getRotationSpeedModifier(direction))
		   .setRotationOffset(getRotationOffset(direction.getAxis()))
		   .setColor(blockEntity)
		   .setPosition(location.add(offset))
		   .setBlockLight(blockLight)
		   .setSkyLight(skyLight);

		return key;
	}

	private BeltData getChain(BeltData key, SpriteShiftEntry spriteShift)
	{
		Direction facing = blockEntity.getBlockState().getValue(TransmissionChainBlock.FACING);
		Quaternionf rotation = new Quaternionf();
		Direction up = Direction.UP;

		Vector3f vecFacing = facing.step().normalize();
		Vector3f vecUp = up.step().normalize();

		if (facing == Direction.UP)
			rotation = new Quaternionf().fromAxisAngleDeg(1, 0, 0, 90);
		else if (facing == Direction.DOWN)
			rotation = new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90);
		else
			rotation = rotation.lookAlong(vecFacing, vecUp);

		//Fix for wrong axis alignment (maybe check why).
		if (facing == Direction.WEST || facing == Direction.EAST)
			rotation = rotation.invert();

		if (blockEntity.isConnected())
		{
			TransmissionChainBlock.ChainSide side = getFirstConnectionSide();
			Quaternionf connectionRotation = new Quaternionf().fromAxisAngleRad(facing.getOpposite().step(), (float) side.rotationAngle);
			rotation = connectionRotation.mul(rotation);
		}

		key.setScrollTexture(spriteShift)
		   .setScrollMult(0.5f)
		   .setRotation(rotation)
		   .setRotationalSpeed(getChainRotationalSpeed(facing) * blockEntity.getRotationSpeedModifier(facing))
		   .setRotationOffset(0f)
		   .setColor(blockEntity)
		   .setPosition(getInstancePosition())
		   .setBlockLight(world.getBrightness(LightLayer.BLOCK, pos))
		   .setSkyLight(world.getBrightness(LightLayer.SKY, pos));

		return key;
	}

	private TransmissionChainBlock.ChainSide getFirstConnectionSide()
	{
		for (TransmissionChainBlock.ChainSide side : TransmissionChainBlock.ChainSide.values())
		{
			TransmissionChainBlock.ConnectionType connectionType = blockEntity.getBlockState().getValue(side.property);

			if (connectionType != TransmissionChainBlock.ConnectionType.NONE)
				return side;
		}

		return null;
	}

	private float getScrollSpeed()
	{
		return blockEntity.getSpeed();
	}

	private float getChainRotationalSpeed(Direction direction)
	{
		return direction.getAxisDirection().getStep() * -getScrollSpeed() * 0.415f;
	}

	private float getSpeed(Direction direction)
	{
		return blockEntity.getSpeed();
	}

	@Override
	protected void remove()
	{
		chainInstance.delete();
		keys.values().forEach(InstanceData::delete);
		keys.clear();
	}
}
