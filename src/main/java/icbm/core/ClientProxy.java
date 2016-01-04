package icbm.core;

import icbm.api.IItemFrequency;
import icbm.core.entity.EntityFlyingBlock;
import icbm.core.entity.EntityFragments;
import icbm.core.entity.RenderEntityBlock;
import icbm.core.entity.RenderShrapnel;
import icbm.core.gui.GuiFrequency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
    }

    @Override
    public void init()
    {
        super.init();
        RenderingRegistry.registerEntityRenderingHandler(EntityFlyingBlock.class, new RenderEntityBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityFragments.class, new RenderShrapnel());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() instanceof IItemFrequency)
        {
            return new GuiFrequency(entityPlayer, entityPlayer.inventory.getCurrentItem());
        }

        return null;
    }
}
