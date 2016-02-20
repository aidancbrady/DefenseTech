package defense.explosion.ex;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.explosion.explosive.Explosive;
import defense.explosion.explosive.blast.BlastRepulsive;
import defense.explosion.model.missiles.ModelAttractiveMissile;
import defense.explosion.model.missiles.ModelRepulsiveMissile;

public class ExRepulsive extends Explosion
{
    public ExRepulsive(String name, int tier)
    {
        super(name, tier);
        this.setFuseTime(120);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	if (getUnlocalizedName().equalsIgnoreCase("attractive"))
        {
            return new ModelAttractiveMissile();
        }
        else
        {
            return new ModelRepulsiveMissile();
        }
    }
    
    @Override
    public void init()
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "YY", 'Y', Explosive.condensed.getItemStack() }));
        }
        else
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "Y", "Y", 'Y', Explosive.condensed.getItemStack() }));
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getID() == Explosive.attractive.getID())
        {
            new BlastRepulsive(world, entity, x, y, z, 2f).setDestroyItems().setPushType(1).explode();
        }
        else
        {
            new BlastRepulsive(world, entity, x, y, z, 2f).setDestroyItems().setPushType(2).explode();

        }
    }
}
