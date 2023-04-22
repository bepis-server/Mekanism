package mekanism.generators.client.jei;

import mekanism.client.jei.MekanismJEI;
import mekanism.generators.common.GeneratorsBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

@JEIPlugin
public class GeneratorsJEI implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry registry) {
        for (Block block : GeneratorsBlocks.allGeneratorBlocks()) {
            registry.registerSubtypeInterpreter(Item.getItemFromBlock(block), MekanismJEI.NBT_INTERPRETER);
        }
    }
}