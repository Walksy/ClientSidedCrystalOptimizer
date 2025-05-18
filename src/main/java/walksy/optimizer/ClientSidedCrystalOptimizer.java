package walksy.optimizer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;


public class ClientSidedCrystalOptimizer implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static void log(Object message) {
        if (false) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of((String) message));
        }
    }
}