package com.github.militaermilitz.mcUtil;

import com.github.militaermilitz.util.FileUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Class is used to store data of ItemStacks which can stored with gson.
 */
public class ItemStackData implements IFileConstructor<ItemStack> {
    //All needed Data to define a ItemStack (Can saved with gson)
    private final Material material;
    private final int amount;

    /**
     * @param stack ItemStack where the needed data is extracted.
     */
    public ItemStackData(ItemStack stack){
        this.material = stack.getType();
        this.amount = stack.getAmount();
    }

    /**
     * This Class will be recursively saved by gson. Therefore is no saveToFile implementation needed.
     */
    @Override
    public void saveToFile() { }

    /**
     * @return Loads ItemStack with extracted data.
     */
    @Override
    public ItemStack load(){
        return new ItemStack(material, amount);
    }
}
