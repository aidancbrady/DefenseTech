package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastSky;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExEndothermic extends Explosion
{
    public ExEndothermic()
    {
        super("endothermic", 3);
        this.modelName = "missile_endothermic.tcn";
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "?!?", "!@!", "?!?", '@', attractive.getItemStack(), '?', Blocks.ice, '!', Blocks.snow }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastSky(world, entity, x, y, z, 50).explode();
    }
}
