package icbm.core.prefab.item;

import icbm.Reference;
import icbm.TabICBM;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

/** Prefab for ICBM items that sets the creative tab, texture name, and translation name
 * 
 * @author DarkGuardsman */
public class ItemICBMBase extends Item
{
    public ItemICBMBase(String name)
    {
        this.setUnlocalizedName(name);
        this.setCreativeTab(TabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + name);
    }
}
