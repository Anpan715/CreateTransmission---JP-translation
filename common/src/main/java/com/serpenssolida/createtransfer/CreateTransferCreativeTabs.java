package com.serpenssolida.createtransfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class CreateTransferCreativeTabs
{
	public static final ResourceKey<CreativeModeTab> CREATETRANSFER_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateTransfer.asResource("main"));

	public static final Supplier<CreativeModeTab> CREATETRANSFER_TAB = registerTab("main", () -> createBuilder()
			.icon(CreateTransferBlocks.TRANSMISSION_CHAIN::asStack)
			.title(Component.translatable("creativeTab." + CreateTransfer.MOD_ID + ".main"))
			.displayItems((param, output) -> output.accept(CreateTransferBlocks.TRANSMISSION_CHAIN.get()))
			.build());

	private CreateTransferCreativeTabs(){}

	@ExpectPlatform
	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		throw new AssertionError();
	}

	@ExpectPlatform
	public static CreativeModeTab.Builder createBuilder()
	{
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void setTab(ResourceKey<CreativeModeTab> key)
	{
		throw new AssertionError();
	}

	public static void init()
	{
		CreateTransfer.REGISTRATE.addRawLang("creativeTab." + CreateTransfer.MOD_ID + ".main", "Create: Transfer");
	}
}
