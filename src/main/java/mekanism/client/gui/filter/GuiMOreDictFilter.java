package mekanism.client.gui.filter;

import mekanism.api.Coord4D;
import mekanism.client.gui.button.GuiButtonDisableableImage;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.content.miner.MOreDictFilter;
import mekanism.common.network.PacketDigitalMinerGui;
import mekanism.common.network.PacketDigitalMinerGui.MinerGuiPacket;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMOreDictFilter extends GuiOreDictFilter<MOreDictFilter, TileEntityDigitalMiner> {

    public GuiMOreDictFilter(PlayerEntity player, TileEntityDigitalMiner tile, int index) {
        super(player, tile);
        origFilter = (MOreDictFilter) tileEntity.filters.get(index);
        filter = ((MOreDictFilter) tileEntity.filters.get(index)).clone();
        updateStackList(filter.getOreDictName());
    }

    public GuiMOreDictFilter(PlayerEntity player, TileEntityDigitalMiner tile) {
        super(player, tile);
        isNew = true;
        filter = new MOreDictFilter();
    }

    @Override
    protected void addButtons() {
        buttons.add(saveButton = new Button(0, guiLeft + 27, guiTop + 62, 60, 20, LangUtils.localize("gui.save")));
        buttons.add(deleteButton = new Button(1, guiLeft + 89, guiTop + 62, 60, 20, LangUtils.localize("gui.delete")));
        buttons.add(backButton = new GuiButtonDisableableImage(2, guiLeft + 5, guiTop + 5, 11, 11, 176, 11, -11, getGuiLocation()));
        buttons.add(replaceButton = new GuiButtonDisableableImage(3, guiLeft + 148, guiTop + 45, 14, 14, 199, 14, -14, getGuiLocation()));
        buttons.add(checkboxButton = new GuiButtonDisableableImage(4, guiLeft + 131, guiTop + 47, 12, 12, 187, 12, -12, getGuiLocation()));
    }

    @Override
    protected void sendPacketToServer(int guiID) {
        Mekanism.packetHandler.sendToServer(new PacketDigitalMinerGui(MinerGuiPacket.SERVER, Coord4D.get(tileEntity), guiID, 0, 0));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiMOreDictFilter.png");
    }

    @Override
    protected void updateStackList(String oreName) {
        iterStacks = OreDictCache.getOreDictStacks(oreName, true);
        stackSwitch = 0;
        stackIndex = -1;
    }
}