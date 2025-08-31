package it.s7danyy.taserutil.client;

import it.s7danyy.taserutil.FriendManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class TaserUtilClient implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.taserutil.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.taserutil"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.execute(() -> client.setScreen(new FriendScreen()));
            }
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (!world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            if (entity instanceof PlayerEntity target) {
                ItemStack stack = player.getStackInHand(hand);
                boolean isTaser = stack.getItem() == Items.STICK && (
                        (stack.hasCustomName() && "Taser".equals(stack.getName().getString())) ||
                                (stack.hasNbt() && stack.getNbt().contains("CustomModelData")
                                        && stack.getNbt().getInt("CustomModelData") == 163)
                );

                if (isTaser && FriendManager.getFriends().contains(target.getName().getString())) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }
}
