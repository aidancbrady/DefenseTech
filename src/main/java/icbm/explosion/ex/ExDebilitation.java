package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastChemical;
import icbm.explosion.model.missiles.ModelDebilitationMissile;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDebilitation extends Explosion
{
    public ExDebilitation(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelDebilitationMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(3), new Object[] { "SSS", "WRW", "SSS", 'R', Explosive.replsive.getItemStack(), 'W', Items.water_bucket, 'S', "dustSulfur" }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastChemical(world, entity, x, y, z, 20, 20 * 30, false).setConfuse().explode();
    }
}
