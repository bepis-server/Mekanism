package mekanism.common.inventory.container.tile;

import javax.annotation.Nonnull;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.frequency.FrequencyType;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.qio.TileEntityQIODashboard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public class QIODashboardContainer extends QIOItemViewerContainer {

    private TileEntityQIODashboard tile;

    public QIODashboardContainer(int id, PlayerInventory inv, TileEntityQIODashboard tile) {
        super(MekanismContainerTypes.QIO_DASHBOARD, id, inv);
        this.tile = tile;
        if (tile != null) {
            tile.addContainerTrackers(this);
        }
        addSlotsAndOpen();
    }

    public QIODashboardContainer(int id, PlayerInventory inv, PacketBuffer buf) {
        this(id, inv, MekanismTileContainer.getTileFromBuf(buf, TileEntityQIODashboard.class));
    }

    @Override
    public QIODashboardContainer recreate() {
        QIODashboardContainer container = new QIODashboardContainer(windowId, inv, tile);
        sync(container);
        return container;
    }

    @Override
    public QIOFrequency getFrequency() {
        return tile.getFrequency(FrequencyType.QIO);
    }

    @Override
    protected void openInventory(@Nonnull PlayerInventory inv) {
        super.openInventory(inv);
        if (tile != null) {
            tile.open(inv.player);
        }
    }

    @Override
    protected void closeInventory(PlayerEntity player) {
        super.closeInventory(player);
        if (tile != null) {
            tile.close(player);
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        if (tile == null) {
            return true;
        }
        if (tile.hasGui() && !tile.isRemoved()) {
            //prevent Containers from remaining valid after the chunk has unloaded;
            World world = tile.getWorld();
            if (world == null) {
                return false;
            }
            return world.isBlockPresent(tile.getPos());
        }
        return false;
    }

    public TileEntityQIODashboard getTileEntity() {
        return tile;
    }
}