package com.serpenssolida.createtransmission.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import com.serpenssolida.createtransmission.CreateTransmission;
import net.fabricmc.api.ModInitializer;

public class CreateTransmissionFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        CreateTransmission.init();

        CreateTransmission.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
        ), CreateTransmission.NAME);

        // on fabric, Registrates must be explicitly finalized and registered.
        CreateTransmission.REGISTRATE.register();
    }
}
