package defense.core;

import mekanism.common.inventory.container.ContainerNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import defense.Settings;
import defense.api.IItemFrequency;

public class CommonProxy implements IGuiHandler
{
    public void preInit()
    {
    }

    public void init()
    {
    }
    
    public void loadConfiguration()
    {
    	if(Settings.CONFIGURATION.hasChanged())
		{
			Settings.CONFIGURATION.save();
		}
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new ContainerNull(player, null);
        }

        return null;
    }
}
