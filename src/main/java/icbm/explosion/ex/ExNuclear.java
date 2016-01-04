package icbm.explosion.ex;

import icbm.ModelICBM;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastNuclear;
import icbm.explosion.model.missiles.MMWenZha;
import icbm.explosion.model.missiles.MMYuanZi;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExNuclear extends Explosion
{
    public ExNuclear(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelICBM getMissileModel()
    {
    	if(getTier() == 3)
    	{
    		return new MMYuanZi();
    	}
    	else {
    		return new MMWenZha();
    	}
    }

    @Override
    public void init()
    {
        if (this.getTier() == 3)
        {
            if (OreDictionary.getOres("ingotUranium").size() > 0)
            {
                GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "UUU", "UEU", "UUU", 'E', thermobaric.getItemStack(), 'U', "ingotUranium" }));
            }
            else
            {
                GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "EEE", "EEE", "EEE", 'E', thermobaric.getItemStack() }));

            }
        }
        else
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', Explosive.replsive.getItemStack(), 'C', Explosive.chemical.getItemStack(), 'I', Explosive.incendiary.getItemStack() }));

        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 3)
        {
            new BlastNuclear(world, entity, x, y, z, 50, 80).setNuclear().explode();
        }
        else
        {
            new BlastNuclear(world, entity, x, y, z, 30, 45).explode();
        }
    }
}
