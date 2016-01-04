package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.blast.BlastEMP;
import icbm.explosion.model.missiles.ModelEMPMissile;
import mekanism.common.MekanismItems;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExEMP extends Explosion
{
    public ExEMP()
    {
        super("emp", 3);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	return new ModelEMPMissile();
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
