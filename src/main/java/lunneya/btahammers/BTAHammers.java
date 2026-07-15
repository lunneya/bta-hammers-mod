package lunneya.btahammers;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.item.material.ToolMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

import lunneya.btahammers.item.ItemHammer;
import net.minecraft.core.item.Item;

import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;

import turniplabs.halplibe.helper.RecipeBuilder;
import net.minecraft.core.item.Items;

import net.minecraft.core.data.registry.Registries;

public class BTAHammers implements ModInitializer {

	public static Item IRON_HAMMER;


	public static final String MOD_ID = HalpLibe.registerMod("btahammers", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommonEvents.BEFORE_GAME_START.listen(
			Key.of(MOD_ID),
			this::beforeGameStart
		);

		CommonEvents.AFTER_GAME_START.listen(
			Key.of(MOD_ID),
			this::afterGameStart
		);

		LOGGER.info("[BTA Hammers] Mod initialized!");
	}

	private void afterGameStart() {
		LOGGER.info("[BTA Hammers] Registering hammer model...");

		ItemModelDispatcher.getInstance().addDispatch(
			new ItemModelStandard(IRON_HAMMER)
				.setDisplayPos(
					"firstperson_righthand",
					ItemModelDispatcher.HANDHELD_FIRST_PERSON_RIGHT_HAND
				)
				.setDisplayPos(
					"firstperson_lefthand",
					ItemModelDispatcher.HANDHELD_FIRST_PERSON_LEFT_HAND
				)
				.setDisplayPos(
					"thirdperson_righthand",
					ItemModelDispatcher.HANDHELD_THIRD_PERSON_RIGHT_HAND
				)
				.setDisplayPos(
					"thirdperson_lefthand",
					ItemModelDispatcher.HANDHELD_THIRD_PERSON_LEFT_HAND
				)
		);

		LOGGER.info("[BTA Hammers] Hammer model registered!");

		// Регистрируем рецепт молота
		RecipeBuilder.Shaped(MOD_ID)
			.setShape(
				"III",
				"ISI",
				" S "
			)
			.addInput('I', Items.INGOT_IRON)
			.addInput('S', Items.STICK)
			.create("iron_hammer", IRON_HAMMER.getDefaultStack());

		// Сбрасываем кэш рецептов, чтобы BTA увидела новый рецепт
		Registries.RECIPES.invalidateCaches();

		LOGGER.info("[BTA Hammers] Iron Hammer recipe registered!");
	}

	private void beforeGameStart() {
		LOGGER.info("[BTA Hammers] Registering hammer items...");

		IRON_HAMMER = new ItemHammer(
			"iron_hammer",
			MOD_ID + ":item/iron_hammer",
			20000,
			ToolMaterial.iron
		);

		LOGGER.info("[BTA Hammers] Iron Hammer registered!");
	}
}
