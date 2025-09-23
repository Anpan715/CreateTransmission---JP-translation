package com.serpenssolida.createtransfer;

import com.jozufozu.flywheel.core.PartialModel;
import com.serpenssolida.createtransfer.blocks.chain.AbstractTransmissionChainBlock;
import com.serpenssolida.createtransfer.blocks.chain.EncasedTransmissionChainBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTransfer
{
    public static final String MOD_ID = "createtransfer";
    public static final String NAME = "Create: Transfer";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateTransfer.MOD_ID);

    public static void init()
    {
        CreateTransferBlocks.init();
        CreateTransferBlockEntities.init();
        CreateTransferModels.init();
        CreateTransferSpriteShifts.init();

        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) ->
				state.getBlock() instanceof AbstractTransmissionChainBlock chain && chain.hasShaftTowards(world, pos, state, direction) ? BlockMovementChecks.CheckResult.SUCCESS : BlockMovementChecks.CheckResult.FAIL);
    }

	public static ResourceLocation asResource(String path)
    {
        return new ResourceLocation(MOD_ID, path);
    }
}
