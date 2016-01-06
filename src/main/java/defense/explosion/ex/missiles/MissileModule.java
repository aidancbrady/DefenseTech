package defense.explosion.ex.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.api.IMissile;
import defense.explosion.model.missiles.ModelCondensedMissile;

public class MissileModule extends Missile
{
    public MissileModule()
    {
        super("missileModule", 1);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
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
