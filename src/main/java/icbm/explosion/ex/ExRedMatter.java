package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastRedmatter;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExRedMatter extends Explosion
{
    public ExRedMatter()
    {
        super("redMatter", 4);
        this.modelName = "missile_redmatter.tcn";
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', antimatter.getItemStack(), 'A', "strangeMatter" }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRedmatter(world, entity, x, y, z, 35).explode();
    }

}
