package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastEMP;
import mekanism.common.MekanismItems;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExEMP extends Explosion
{
    public ExEMP()
    {
        super("emp", 3);
        this.modelName = "missile_emp.tcn";
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastEMP(world, entity, x, y, z, 50).setEffectBlocks().setEffectEntities().explode();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "RBR", "BTB", "RBR", 'T', replsive.getItemStack(), 'R', Blocks.redstone_block, 'B', MekanismItems.EnergyTablet.getUnchargedItem() }));
    }
}
