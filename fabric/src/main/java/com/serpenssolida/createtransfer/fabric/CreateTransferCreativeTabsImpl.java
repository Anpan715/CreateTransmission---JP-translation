package com.serpenssolida.createtransfer.fabric;

import com.serpenssolida.createtransfer.CreateTransfer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class CreateTransferCreativeTabsImpl
{
	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		CreativeModeTab tab = sup.get();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateTransfer.asResource(id)), tab);
		return sup;
	}

	public static CreativeModeTab.Builder createBuilder()
	{
		return FabricItemGroup.builder();
	}

	public static void setTab(ResourceKey<CreativeModeTab> key)
	{
		CreateTransfer.REGISTRATE.setCreativeTab(key);
	}
}
