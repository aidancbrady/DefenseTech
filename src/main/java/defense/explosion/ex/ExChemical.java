package defense.explosion.ex;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.core.CoreModule;
import defense.explosion.explosive.Explosive;
import defense.explosion.explosive.blast.BlastChemical;
import defense.explosion.model.missiles.MMDuQi;
import defense.explosion.model.missiles.MMGanRanDu;

public class ExChemical extends Explosion
{
    public ExChemical(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
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
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', CoreModule.itemPoisonPowder, '?', Explosive.debilitation.getItemStack() }));
        }
        else if (this.getTier() == 2)
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Items.rotten_flesh, '@', Explosive.chemical.getItemStack() }));
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
