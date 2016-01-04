package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastBreech;
import icbm.explosion.model.missiles.ModelBreachingMissile;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExBreaching extends Explosion
{
    public ExBreaching()
    {
        super("breaching", 2);
        this.setFuseTime(40);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelBreachingMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(2), new Object[] { "GCG", "GCG", "GCG", 'C', Explosive.condensed.getItemStack(), 'G', Items.gunpowder }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastBreech(world, entity, x, y, z, 2.5f, 7).explode();
    }
}
