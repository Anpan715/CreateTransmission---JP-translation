package com.serpenssolida.createtransmission.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CTExpectPlatformImpl
{
	private CTExpectPlatformImpl() {}

	public static String platformName()
	{
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
