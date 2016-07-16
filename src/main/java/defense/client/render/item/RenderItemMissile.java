package defense.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.render.entity.RenderMissile;
import defense.common.explosion.Explosion;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.item.ItemMissile;

@SideOnly(Side.CLIENT)
public class RenderItemMissile implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return shouldUseRenderHelper(type, item, null);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return item.getItem() instanceof ItemMissile;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if(shouldUseRenderHelper(type, item, null))
        {
            Explosion missile = (Explosion)ExplosiveRegistry.get(item.getItemDamage());

            GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
            
            float scale = 0.7f*0.25f;
			float right = -0.8f;

			if(type == ItemRenderType.INVENTORY)
			{
				scale = 0.4f*0.4f;

				if(missile.getTier() == 2)
				{
					scale = scale / 1.5f;
				}
				else if(missile.getTier() == 3)
				{
					scale = scale / 1.7f;
				}
				else if(missile.getTier() == 4)
				{
					scale = scale / 1.9f;
					right += 0.1;
				}
				else if(missile.getTier() == 1)
				{
					scale = scale * 1.5F;
					right += 0.2;
				}

				GL11.glTranslatef(right, 0.4f, 0f);
				GL11.glRotatef(-40, 0, 0, 1);
			}

			if(type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED)
			{
				GL11.glTranslatef(1.15f, 1f, 0.5f);
				GL11.glRotatef(180, 0, 0, 1f);
			}
			else {
				GL11.glRotatef(-90, 0, 0, 1f);
			}

			if(type == ItemRenderType.ENTITY)
			{
				scale = scale / 1.5f;
			}

			GL11.glScalef(scale, scale, scale);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissile.getMissileResource(missile));

            synchronized(RenderMissile.cache)
            {
                if(!RenderMissile.cache.containsKey(missile))
                {
                    RenderMissile.cache.put(missile, RenderMissile.getMissileModel(missile));
                }

                RenderMissile.cache.get(missile).renderAll();
            }
        }
    }
}
