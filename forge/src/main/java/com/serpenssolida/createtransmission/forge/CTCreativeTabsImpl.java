package com.serpenssolida.createtransmission.forge;

import com.serpenssolida.createtransmission.CreateTransmission;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CTCreativeTabsImpl
{
	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateTransmission.MOD_ID);
	private static final Map<ResourceKey<CreativeModeTab>, RegistryObject<CreativeModeTab>> TABS = new HashMap<>();

	public static void register(IEventBus modBus)
	{
		TAB_REGISTER.register(modBus);
	}

	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		RegistryObject<CreativeModeTab> tab = TAB_REGISTER.register(id, sup);
		TABS.put(ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateTransmission.asResource(id)), tab);
		return tab;
	}

	public static CreativeModeTab.Builder createBuilder()
	{
		return CreativeModeTab.builder();
	}

	public static void setTab(ResourceKey<CreativeModeTab> key)
	{
		CreateTransmission.REGISTRATE.setCreativeTab(TABS.get(key));
	}
}
