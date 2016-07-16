package defense.common.explosion;

import mekanism.common.MekanismItems;
import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import defense.common.explosive.blast.BlastEMP;

public class ExEMP extends Explosion
{
    public ExEMP()
    {
        super("emp", 3);
    }
    
    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastEMP(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', repulsive.getItemStack(), 'R', Blocks.redstone_block, 'B', MekanismItems.EnergyTablet.getUnchargedItem() }));
    }
}
