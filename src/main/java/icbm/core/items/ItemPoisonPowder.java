package icbm.core.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;
import net.minecraft.client.renderer.texture.IIconRegister;

public class ItemPoisonPowder extends ItemICBMBase
{
    public ItemPoisonPowder()
    {
        super("poisonPowder");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "poisonPowder");
    }
}
