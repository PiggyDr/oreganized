package galena.oreganized.client.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import galena.oreganized.Oreganized;
import galena.oreganized.OreganizedConfig;
import galena.oreganized.content.effect.StunningEffect;
import galena.oreganized.index.OEffects;
import galena.oreganized.index.OFluids;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class OGui extends ForgeGui {

    protected static final ResourceLocation STUNNING_VIGNETTE_LOCATION = Oreganized.modLoc( "textures/misc/stunning_overlay.png");
    protected static final ResourceLocation STUNNED_HEARTS = Oreganized.modLoc( "textures/gui/stunned_hearts.png");

    private static final IntObjectHashMap<ResourceLocation> STUNNING_OVERLAY_LOCATIONS = new IntObjectHashMap<>();

    protected static ResourceLocation getStunningOutline(int amplifier) {
        return STUNNING_OVERLAY_LOCATIONS.computeIfAbsent(amplifier, i -> {
            return Oreganized.modLoc("textures/misc/brain_damage_outline_" + (i + 1) + ".png");
        });
    }

    public OGui(Minecraft mc) {
        super(mc);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float partialTick) {
        this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
        RenderSystem.enableBlend();

        var stunning = this.minecraft.player.getEffect(OEffects.STUNNING.get());
        if (stunning != null && OreganizedConfig.CLIENT.renderStunningOverlay.get()) {
            var opacity = stunning.getAmplifier() * 1F / StunningEffect.MAX_AMPLIFIER;
            this.renderTextureOverlay(guiGraphics, STUNNING_VIGNETTE_LOCATION, opacity);
            this.renderTextureOverlay(guiGraphics, getStunningOutline(stunning.getAmplifier()), 1F);
        }

        if (this.minecraft.player.isEyeInFluidType(OFluids.MOLTEN_LEAD_TYPE.get()))
            this.renderTextureOverlay(guiGraphics, STUNNING_VIGNETTE_LOCATION, 1F);
    }

    public static void renderStunnedHeart(GuiGraphics graphics, int u, int x, int y, int v) {
        graphics.blit(STUNNED_HEARTS, x, y, u, v, 9, 9, 72, 18);
    }
}
