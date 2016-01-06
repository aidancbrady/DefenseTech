package defense.explosion.ex.missiles;

import defense.explosion.ExplosionModule;
import defense.explosion.ex.Explosion;
import net.minecraft.item.ItemStack;

/** Ex object that are only defined as missiles
 * 
 * @author Calclavia */
public abstract class Missile extends Explosion
{
    public Missile(String name, int tier)
    {
        super(name, tier);
        this.hasBlock = false;
        this.hasGrenade = false;
        this.hasMinecart = false;
    }

    @Override
    public ItemStack getItemStack()
    {
        return new ItemStack(ExplosionModule.itemMissile, 1, this.getID());
    }
}
