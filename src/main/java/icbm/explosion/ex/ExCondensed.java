package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastRepulsive;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExCondensed extends Explosion
{
    public ExCondensed(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setFuseTime(1);
        this.modelName = "missile_concussion.tcn";
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
