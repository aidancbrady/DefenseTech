package defense.common.tile;

import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import defense.common.DefenseTech;
import defense.common.Reference;
import defense.common.base.IBlockActivate;

/** Missile Coordinator
 * 
 * @author Calclavia */
public class TileMissileCoordinator extends TileEntityElectricBlock implements IInventory, IBlockActivate
{
    protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

    public TileMissileCoordinator()
    {
    	super("MissileCoordinator", 0);
        inventory = new ItemStack[2];
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("gui.coordinator.name");
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "interface", 1, (float) (this.worldObj.rand.nextFloat() * 0.2 + 0.9F));
        entityPlayer.openGui(DefenseTech.INSTANCE, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }
}
