package mekanism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.HUDElement;
import mekanism.common.content.gear.HUDElement.HUDColor;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class HUDRenderer {

    private static final ResourceLocation HEAD_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_helmet.png");
    private static final ResourceLocation CHEST_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_chest.png");
    private static final ResourceLocation LEGS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_leggings.png");
    private static final ResourceLocation BOOTS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_boots.png");

    private static final ResourceLocation COMPASS = MekanismUtils.getResource(ResourceType.GUI, "compass.png");

    private long lastTick = -1;

    private float prevRotationYaw;
    private float prevRotationPitch;

    private final Minecraft minecraft = Minecraft.getInstance();

    public void renderHUD(MatrixStack matrix, float partialTick) {
        update();
        int color = HUDColor.REGULAR.getColor();
        if (MekanismConfig.client.hudOpacity.get() < 0.05F) {
            return;
        }
        matrix.pushPose();
        float yawJitter = -absSqrt(minecraft.player.yHeadRot - prevRotationYaw);
        float pitchJitter = -absSqrt(minecraft.player.xRot - prevRotationPitch);
        matrix.translate(yawJitter, pitchJitter, 0);
        if (MekanismConfig.client.hudCompassEnabled.get()) {
            renderCompass(matrix, partialTick, color);
        }

        renderMekaSuitEnergyIcons(matrix, partialTick, color);
        renderMekaSuitModuleIcons(matrix, partialTick, color);

        matrix.popPose();
    }

    private void update() {
        // if we're just now rendering the HUD after a pause, reset the pitch/yaw trackers
        if (lastTick == -1 || minecraft.level.getGameTime() - lastTick > 1) {
            prevRotationYaw = minecraft.player.yRot;
            prevRotationPitch = minecraft.player.xRot;
        }
        lastTick = minecraft.level.getGameTime();
        float yawDiff = (minecraft.player.yHeadRot - prevRotationYaw);
        float pitchDiff = (minecraft.player.xRot - prevRotationPitch);
        prevRotationYaw += yawDiff / MekanismConfig.client.hudJitter.get();
        prevRotationPitch += pitchDiff / MekanismConfig.client.hudJitter.get();
    }

    private static float absSqrt(float val) {
        float ret = (float) Math.sqrt(Math.abs(val));
        return val < 0 ? -ret : ret;
    }

    private void renderMekaSuitEnergyIcons(MatrixStack matrix, float partialTick, int color) {
        matrix.pushPose();
        matrix.translate(10, 10, 0);
        int posX = 0;
        if (getStack(EquipmentSlotType.HEAD).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(HEAD_ICON, getStack(EquipmentSlotType.HEAD)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.CHEST).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(CHEST_ICON, getStack(EquipmentSlotType.CHEST)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.LEGS).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(LEGS_ICON, getStack(EquipmentSlotType.LEGS)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.FEET).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(BOOTS_ICON, getStack(EquipmentSlotType.FEET)), color, false);
        }
        matrix.popPose();
    }

    private void renderMekaSuitModuleIcons(MatrixStack matrix, float partialTick, int color) {
        // create list of all elements to render
        List<HUDElement> elements = new ArrayList<>();
        for (EquipmentSlotType type : EnumUtils.ARMOR_SLOTS) {
            ItemStack stack = getStack(type);
            if (stack.getItem() instanceof ItemMekaSuitArmor) {
                elements.addAll(((ItemMekaSuitArmor) stack.getItem()).getHUDElements(stack));
            }
        }

        int startX = minecraft.getWindow().getGuiScaledWidth() - 10;
        int curY = minecraft.getWindow().getGuiScaledHeight() - 10;

        matrix.pushPose();
        for (HUDElement element : elements) {
            int elementWidth = 24 + minecraft.font.width(element.getText());
            curY -= 18;
            renderHUDElement(matrix, startX - elementWidth, curY, element, color, true);
        }
        matrix.popPose();
    }

    private void renderHUDElement(MatrixStack matrix, int x, int y, HUDElement element, int color, boolean iconRight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        minecraft.getTextureManager().bind(element.getIcon());
        if (!iconRight) {
            AbstractGui.blit(matrix, x, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.font.draw(matrix, element.getText(), x + 18, y + 5, element.getColor());
        } else {
            AbstractGui.blit(matrix, x + minecraft.font.width(element.getText()) + 2, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.font.draw(matrix, element.getText(), x, y + 5, element.getColor());
        }
    }

    private void renderCompass(MatrixStack matrix, float partialTick, int color) {
        matrix.pushPose();
        int posX = 25;
        int posY = minecraft.getWindow().getGuiScaledHeight() - 100;
        matrix.translate(posX + 50, posY + 50, 0);
        matrix.pushPose();
        float angle = 180 - MathHelper.lerp(partialTick, minecraft.player.yHeadRotO, minecraft.player.yHeadRot);
        matrix.pushPose();
        matrix.scale(0.7F, 0.7F, 0.7F);
        ITextComponent coords = MekanismLang.GENERIC_BLOCK_POS.translate((int) minecraft.player.getX(), (int) minecraft.player.getY(), (int) minecraft.player.getZ());
        minecraft.font.draw(matrix, coords, -minecraft.font.width(coords) / 2F, -4, color);
        matrix.popPose();
        matrix.mulPose(Vector3f.XP.rotationDegrees(-60));
        matrix.mulPose(Vector3f.ZP.rotationDegrees(angle));
        minecraft.getTextureManager().bind(COMPASS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        AbstractGui.blit(matrix, -50, -50, 100, 100, 0, 0, 256, 256, 256, 256);
        rotateStr(matrix, MekanismLang.NORTH_SHORT, angle, 0, color);
        rotateStr(matrix, MekanismLang.EAST_SHORT, angle, 90, color);
        rotateStr(matrix, MekanismLang.SOUTH_SHORT, angle, 180, color);
        rotateStr(matrix, MekanismLang.WEST_SHORT, angle, 270, color);
        MekanismRenderer.resetColor();
        matrix.popPose();
        matrix.popPose();
    }

    private void rotateStr(MatrixStack matrix, ILangEntry langEntry, float rotation, float shift, int color) {
        matrix.pushPose();
        matrix.mulPose(Vector3f.ZP.rotationDegrees(shift));
        matrix.translate(0, -50, 0);
        matrix.mulPose(Vector3f.ZP.rotationDegrees(-rotation - shift));
        minecraft.font.draw(matrix, langEntry.translate(), -2.5F, -4, color);
        matrix.popPose();
    }

    private ItemStack getStack(EquipmentSlotType type) {
        return minecraft.player.getItemBySlot(type);
    }
}
