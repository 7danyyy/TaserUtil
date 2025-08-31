package it.s7danyy.taserutil.client;

import com.mojang.blaze3d.systems.RenderSystem;
import it.s7danyy.taserutil.FriendManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Set;

public class FriendScreen extends Screen {
    private TextFieldWidget input;
    private static final Identifier DEFAULT_SKIN = new Identifier("textures/entity/steve.png");

    public FriendScreen() {
        super(new LiteralText("Taser Utils - Friends"));
    }

    @Override
    protected void init() {
        this.clearChildren();

        int w = this.width;
        int y = 20;

        input = new TextFieldWidget(
                this.textRenderer,
                w / 2 - 100, y, 200, 20,
                new LiteralText("Inserisci nick")
        );
        this.addDrawableChild(input);

        this.addDrawableChild(new ButtonWidget(
                w / 2 + 110, y, 60, 20,
                new LiteralText("Add"),
                btn -> {
                    String name = input.getText().trim();
                    if (!name.isEmpty() && FriendManager.addFriend(name)) {
                        this.client.setScreen(new FriendScreen());
                    }
                }
        ));

        y += 30;
        Set<String> friends = FriendManager.getFriends();
        for (String f : friends) {
            this.addDrawableChild(new ButtonWidget(
                    w / 2 + 110, y, 60, 20,
                    new LiteralText("Remove"),
                    btn -> {
                        FriendManager.removeFriend(f);
                        this.client.setScreen(new FriendScreen());
                    }
            ));
            y += 25;
        }

        this.addDrawableChild(new ButtonWidget(
                w / 2 - 40, this.height - 30, 80, 20,
                new LiteralText("Close"),
                btn -> this.client.setScreen(null)
        ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        String titleStr = this.title.getString();
        int tw = this.textRenderer.getWidth(titleStr);
        this.textRenderer.drawWithShadow(
                matrices, titleStr, (this.width - tw) / 2, 5, 0xFFFFFF
        );

        int y = 50;
        MinecraftClient mc = MinecraftClient.getInstance();
        for (String f : FriendManager.getFriends()) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(f);
            Identifier skin = (entry != null ? entry.getSkinTexture() : DEFAULT_SKIN);

            RenderSystem.setShaderTexture(0, skin);
            DrawableHelper.drawTexture(
                    matrices,
                    this.width / 2 - 120, y,
                    8, 8,
                    8, 8,
                    64, 64
            );
            DrawableHelper.drawTexture(
                    matrices,
                    this.width / 2 - 120, y,
                    40, 8,  // u2, v2
                    8, 8,
                    64, 64
            );

            this.textRenderer.drawWithShadow(
                    matrices, f, this.width / 2 - 100, y + 1, 0xFFFFFF
            );

            y += 25;
        }
    }
}
