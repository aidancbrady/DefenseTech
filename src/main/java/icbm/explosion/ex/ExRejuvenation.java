package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.blast.BlastRegen;
import icbm.explosion.model.missiles.ModelRegenMissile;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExRejuvenation extends Explosion
{
    public ExRejuvenation()
    {
        super("rejuvenation", 2);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelRegenMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "ICI", "CDC", "ICI", 'D', Blocks.diamond_block, 'C', Items.clock, 'I', Blocks.iron_block }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRegen(world, entity, x, y, z, 16).doExplode();
    }
}
