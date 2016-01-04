package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.blast.BlastRepulsive;
import icbm.explosion.model.missiles.ModelCondensedMissile;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExCondensed extends Explosion
{
    public ExCondensed(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setFuseTime(1);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelCondensedMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(3), new Object[] { "@?@", '@', Blocks.tnt, '?', Items.redstone }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 2.5f).explode();
    }
}
