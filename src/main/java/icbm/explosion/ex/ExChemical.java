package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.core.ICBMCore;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastChemical;
import icbm.explosion.model.missiles.MMDuQi;
import icbm.explosion.model.missiles.MMGanRanDu;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExChemical extends Explosion
{
    public ExChemical(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	if(this.getTier() == 1)
    	{
    		return new MMDuQi();
    	}
    	else {
    		return new MMGanRanDu();
    	}
    }

    @Override
    public void init()
    {
        if (this.getTier() == 1)
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ICBMCore.itemPoisonPowder, '?', Explosive.debilitation.getItemStack() }));
        }
        else if (this.getTier() == 2)
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Items.rotten_flesh, '@', Explosive.chemical.getItemStack() }));
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 1)
        {
            new BlastChemical(world, entity, x, y, z, 20, 20 * 30, false).setPoison().setRGB(0.8f, 0.8f, 0).explode();
        }
        else if (this.getTier() == 2)
        {
            new BlastChemical(world, entity, x, y, z, 20, 20 * 30, false).setContagious().setRGB(0.3f, 0.8f, 0).explode();
        }

    }

}
