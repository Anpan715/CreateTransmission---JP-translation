package com.serpenssolida.createtransfer.forge;

import com.serpenssolida.createtransfer.CreateTransfer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CreateTransferCreativeTabsImpl
{
	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateTransfer.MOD_ID);
	private static final Map<ResourceKey<CreativeModeTab>, RegistryObject<CreativeModeTab>> TABS = new HashMap<>();

	public static void register(IEventBus modBus)
	{
		TAB_REGISTER.register(modBus);
	}

	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		RegistryObject<CreativeModeTab> tab = TAB_REGISTER.register(id, sup);
		TABS.put(ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateTransfer.asResource(id)), tab);
		return tab;
	}

	public static CreativeModeTab.Builder createBuilder()
	{
		return CreativeModeTab.builder();
	}

	public static void setTab(ResourceKey<CreativeModeTab> key)
	{
		CreateTransfer.LOGGER.warn("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		CreateTransfer.LOGGER.warn("tab: " + TABS.get(key));
		CreateTransfer.REGISTRATE.setCreativeTab(TABS.get(key));//.setCreativeTab(TABS.get(key));
	}
}
