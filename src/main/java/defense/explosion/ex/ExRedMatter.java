package defense.explosion.ex;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.explosion.explosive.blast.BlastRedmatter;
import defense.explosion.model.missiles.ModelRedMatterMissile;

public class ExRedMatter extends Explosion
{
    public ExRedMatter()
    {
        super("redMatter", 4);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelRedMatterMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', antimatter.getItemStack(), 'A', "strangeMatter" }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRedmatter(world, entity, x, y, z, 35).explode();
    }

}
