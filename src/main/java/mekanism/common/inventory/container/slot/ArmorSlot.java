package mekanism.common.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ArmorSlot extends InsertableSlot {

    protected static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS,
                                                                                           PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
                                                                                           PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
                                                                                           PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};

    private final EquipmentSlotType slotType;

    public ArmorSlot(PlayerInventory inventory, int index, int x, int y, EquipmentSlotType slotType) {
        super(inventory, index, x, y);
        this.slotType = slotType;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.canEquip(slotType, ((PlayerInventory) container).player);
    }

    @Override
    public boolean mayPickup(@Nonnull PlayerEntity player) {
        ItemStack itemstack = getItem();
        if (!itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack)) {
            return false;
        }
        return super.mayPickup(player);
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slotType.getIndex()]);
    }
}
