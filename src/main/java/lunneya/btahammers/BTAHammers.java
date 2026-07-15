package lunneya.btahammers;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BTAHammers implements ModInitializer {

	public static final String MOD_ID = "btahammers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[BTA Hammers] Mod loaded successfully!");
	}
}
