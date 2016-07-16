package defense.common.explosion.missile;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IMissile;
import defense.common.Reference;

public class MissileModule extends Missile
{
    public MissileModule()
    {
        super("missileModule", 1);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IModelCustom getMissileModel()
    {
    	return AdvancedModelLoader.loadModel(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_module.obj"));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getMissileResource()
    {
    	return new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_module.png");
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if(entity instanceof IMissile)
        {
            ((IMissile) entity).dropMissileAsItem();
        }
    }
}
