package mekanism.common.inventory.container.item;

import javax.annotation.Nonnull;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.frequency.Frequency.FrequencyIdentity;
import mekanism.common.frequency.FrequencyManager;
import mekanism.common.frequency.FrequencyType;
import mekanism.common.frequency.IFrequencyItem;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.inventory.container.slot.HotBarSlot;
import mekanism.common.item.ItemPortableItemDashboard;
import mekanism.common.registries.MekanismContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortableItemDashboardContainer extends QIOItemViewerContainer {

    protected Hand hand;
    protected ItemStack stack;

    public PortableItemDashboardContainer(int id, PlayerInventory inv, Hand hand, ItemStack stack) {
        super(MekanismContainerTypes.PORTABLE_ITEM_DASHBOARD, id, inv);
        this.hand = hand;
        this.stack = stack;
        addSlotsAndOpen();
    }

    public PortableItemDashboardContainer(int id, PlayerInventory inv, PacketBuffer buf) {
        this(id, inv, buf.readEnumValue(Hand.class), MekanismItemContainer.getStackFromBuffer(buf, ItemPortableItemDashboard.class));
    }

    public Hand getHand() {
        return hand;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public PortableItemDashboardContainer recreate() {
        PortableItemDashboardContainer container = new PortableItemDashboardContainer(windowId, inv, hand, stack);
        sync(container);
        return container;
    }

    @Override
    public QIOFrequency getFrequency() {
        if (!inv.player.world.isRemote()) {
            FrequencyIdentity identity = ((IFrequencyItem) stack.getItem()).getFrequency(stack);
            if (identity == null)
                return null;
            FrequencyManager<QIOFrequency> manager = identity.isPublic() ? FrequencyType.QIO.getManager(null) : FrequencyType.QIO.getManager(inv.player.getUniqueID());
            QIOFrequency freq = manager.getFrequency(identity.getKey());
            // if this frequency no longer exists, remove the reference from the stack
            if (freq == null) {
                ((IFrequencyItem) stack.getItem()).setFrequency(stack, null);
            }
            return freq;
        }
        return null;
    }

    @Override
    protected HotBarSlot createHotBarSlot(@Nonnull PlayerInventory inv, int index, int x, int y) {
        // special handling to prevent removing the dashboard from the player's inventory slot
        if (index == inv.currentItem) {
            return new HotBarSlot(inv, index, x, y) {
                @Override
                public boolean canTakeStack(PlayerEntity player) {
                    return false;
                }
                @Override
                @OnlyIn(Dist.CLIENT)
                public boolean isEnabled() {
                   return false;
                }
            };
        }
        return super.createHotBarSlot(inv, index, x, y);
    }
}
