package defense.explosion.ex;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.explosion.explosive.blast.BlastSky;
import defense.explosion.model.missiles.ModelEndothermicMissile;

public class ExEndothermic extends Explosion
{
    public ExEndothermic()
    {
        super("endothermic", 3);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelEndothermicMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "?!?", "!@!", "?!?", '@', attractive.getItemStack(), '?', Blocks.ice, '!', Blocks.snow }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastSky(world, entity, x, y, z, 50).explode();
    }
}
