package com.serpenssolida.createtransmission.neoforge;

import com.serpenssolida.createtransmission.CTModels;
import com.serpenssolida.createtransmission.CreateTransmission;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = CreateTransmission.MOD_ID, dist = Dist.CLIENT)
public class CreateTransmissionClientForge
{
	public CreateTransmissionClientForge(IEventBus modEventBus)
	{
		modEventBus.addListener(CreateTransmissionClientForge::clientInit);
	}

	public static void clientInit(final FMLClientSetupEvent event)
	{
		CTModels.init();
	}

}
