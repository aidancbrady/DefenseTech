package defense.client.render.entity;

import java.util.HashMap;

import mekanism.common.Tier.BaseTier;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IExplosive;
import defense.common.Reference;
import defense.common.entity.EntityMissile;
import defense.common.entity.EntityMissile.MissileType;
import defense.common.explosion.Explosion;
import defense.common.explosive.Explosive;

@SideOnly(Side.CLIENT)
/** @author Calclavia */
public class RenderMissile extends Render
{
    public static final HashMap<Explosion, IModelCustom> cache = new HashMap<Explosion, IModelCustom>();

    public RenderMissile(float f)
    {
        shadowSize = f;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partial)
    {
        EntityMissile entityMissile = (EntityMissile) entity;
        IExplosive e = entityMissile.getExplosiveType();
        
        if(e instanceof Explosion)
        {
            Explosion missile = (Explosion)e;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -1.35F, 0.0F);
            GL11.glTranslated(x + (entityMissile.motionX * partial), y + (entityMissile.motionY * partial), z + (entityMissile.motionZ * partial));
            GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * partial - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * partial + 90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(0.25F, 0.25F, 0.25F);
            
            if(e.getTier() == 1 || e.getTier() == 4)
            {
            	GL11.glScalef(1.6F, 1.6F, 1.6F);
            }
            
            if(entityMissile.missileType == MissileType.CruiseMissile)
            {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
            }

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(getMissileResource(missile));

            synchronized(cache)
            {
            	GL11.glPushMatrix();
            	GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
                if(!RenderMissile.cache.containsKey(missile))
                {
                    RenderMissile.cache.put(missile, getMissileModel(missile));
                }
                
                RenderMissile.cache.get(missile).renderAll();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }
    
    public static IModelCustom getMissileModel(Explosive missile)
    {
    	if(missile.getMissileModel() != null)
    	{
    		return missile.getMissileModel();
    	}
    	
    	String tierString = BaseTier.values()[missile.getTier()-1].getName().toLowerCase();
    	return AdvancedModelLoader.loadModel(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_" + tierString + ".obj"));
    }
    
    public static ResourceLocation getMissileResource(Explosive missile)
    {
    	if(missile.getMissileResource() != null)
    	{
    		return missile.getMissileResource();
    	}
    	
    	String tierString = BaseTier.values()[missile.getTier()-1].getName().toLowerCase();
    	return new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_" + tierString + ".png");
    }
}