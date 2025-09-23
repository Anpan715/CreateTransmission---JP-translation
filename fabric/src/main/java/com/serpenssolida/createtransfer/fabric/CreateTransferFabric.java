package com.serpenssolida.createtransfer.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import com.serpenssolida.createtransfer.CreateTransferBlocks;
import com.serpenssolida.createtransfer.CreateTransfer;
import net.fabricmc.api.ModInitializer;

public class CreateTransferFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        CreateTransfer.init();

        CreateTransfer.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
        ), CreateTransfer.NAME);

        // on fabric, Registrates must be explicitly finalized and registered.
        CreateTransfer.REGISTRATE.register();
    }
}
