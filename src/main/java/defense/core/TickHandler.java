package defense.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TickHandler
{
	@SubscribeEvent
	public void onTick(PlayerTickEvent event)
	{
        try {
            EntityPlayer player = (EntityPlayer)event.player;

            ItemStack currentItem = player.getCurrentEquippedItem();

            if(currentItem != null && (player != Minecraft.getMinecraft().renderViewEntity || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
            {
                if(currentItem.getItem() == DefenseTechItems.itemRocketLauncher)
                {
                    if(player.getItemInUseCount() <= 0)
                    {
                        player.setItemInUse(currentItem, Integer.MAX_VALUE);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to tick properly.");
            e.printStackTrace();
        }
    }
}
