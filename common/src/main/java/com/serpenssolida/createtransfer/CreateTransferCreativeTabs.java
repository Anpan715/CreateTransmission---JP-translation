package com.serpenssolida.createtransfer;

import com.jozufozu.flywheel.core.PartialModel;
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

	/**
	 * Initializes the class static fields.
	 */
	public static void init()
	{
		CreateTransfer.REGISTRATE.addRawLang("creativeTab." + CreateTransfer.MOD_ID + ".main", "Create: Transfer");
		CreateTransfer.LOGGER.info("Loading creative tabs for " + CreateTransfer.NAME);
	}

	/**
	 * Creates a new tab with the given id and registers it.
	 * @param id the id of the tab.
	 * @param sup the tab to register.
	 *
	 * @return the newly created tab.
	 */
	@ExpectPlatform
	public static Supplier<CreativeModeTab> registerTab(String id, Supplier<CreativeModeTab> sup)
	{
		throw new AssertionError();
	}

	/**
	 * Instantiates a new builder for a creative tab.
	 *
	 * @return the builder.
	 */
	@ExpectPlatform
	public static CreativeModeTab.Builder createBuilder()
	{
		throw new AssertionError();
	}

	/**
	 * Set the current creative tab of the registrate to the tab with the given key.
	 *
	 * @param key the key of the new tab.
	 */
	@ExpectPlatform
	public static void setTab(ResourceKey<CreativeModeTab> key)
	{
		throw new AssertionError();
	}
}
