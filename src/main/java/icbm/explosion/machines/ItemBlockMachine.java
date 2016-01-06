package icbm.explosion.machines;

import icbm.api.ITier;
import icbm.explosion.machines.BlockICBMMachine.MachineData;
import mekanism.common.tile.TileEntityBasicBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockMachine extends ItemBlock
{
    public ItemBlockMachine(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
    	MachineData type = MachineData.values()[itemstack.getItemDamage()];
    	String name = this.getUnlocalizedName() + "." + type.unlocalized;
    	
    	if(type.hasTier)
    	{
    		name += "." + getTier(itemstack);
    	}
    	
    	return name;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.machine";
    }

    public void setTier(ItemStack itemStack, int tier)
	{
		if(itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		itemStack.stackTagCompound.setInteger("tier", tier);
	}
	
	public int getTier(ItemStack itemStack)
	{
		if(itemStack.stackTagCompound == null)
		{
			return 0;
		}

		return itemStack.stackTagCompound.getInteger("tier");
	}
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        int direction = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        boolean place = BlockICBMMachine.canBePlacedAt(world, x, y, z, metadata, direction);

        if(place && super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
        	TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
        	
        	if(tileEntity instanceof ITier)
        	{
        		((ITier)tileEntity).setTier(getTier(stack));
        	}
        	
            return true;
        }

        return false;
    }
}
