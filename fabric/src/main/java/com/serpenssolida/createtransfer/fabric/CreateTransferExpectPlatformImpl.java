package com.serpenssolida.createtransfer.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CreateTransferExpectPlatformImpl
{
	public static String platformName()
	{
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
