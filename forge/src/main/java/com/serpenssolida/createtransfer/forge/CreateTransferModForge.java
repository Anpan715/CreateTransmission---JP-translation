package com.serpenssolida.createtransfer.forge;

import com.serpenssolida.createtransfer.CreateTransfer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateTransfer.MOD_ID)
public class CreateTransferModForge
{
    public CreateTransferModForge()
    {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CreateTransfer.REGISTRATE.registerEventListeners(eventBus);
        CreateTransfer.init();
        CreateTransferCreativeTabsImpl.register(eventBus);
    }
}
