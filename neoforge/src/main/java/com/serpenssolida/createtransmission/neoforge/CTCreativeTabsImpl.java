package com.serpenssolida.createtransmission.neoforge;

import com.serpenssolida.createtransmission.CreateTransmission;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CTCreativeTabsImpl
{
	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateTransmission.MOD_ID);
	private static final Map<ResourceKey<CreativeModeTab>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TABS = new HashMap<>();

	public static void register(IEventBus modBus)
	{
		TAB_REGISTER.register(modBus);
	}

	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		DeferredHolder<CreativeModeTab, CreativeModeTab> tab = TAB_REGISTER.register(id, sup);
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
