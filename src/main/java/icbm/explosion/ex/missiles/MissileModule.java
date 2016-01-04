package icbm.explosion.ex.missiles;

import icbm.ModelICBM;
import icbm.api.IMissile;
import icbm.explosion.model.missiles.ModelCondensedMissile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MissileModule extends Missile
{
    public MissileModule()
    {
        super("missileModule", 1);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelCondensedMissile();
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (entity instanceof IMissile)
        {
            ((IMissile) entity).dropMissileAsItem();
        }
    }
}
