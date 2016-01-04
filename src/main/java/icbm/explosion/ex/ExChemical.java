package icbm.explosion.ex;

import icbm.core.ICBMCore;
import icbm.explosion.explosive.blast.BlastChemical;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExChemical extends Explosion
{
    public ExChemical(String mingZi, int tier)
    {
        super(mingZi, tier);
        //chemical
        if (this.getTier() == 1)
        {
            this.modelName = "missile_chemical.tcn";
        }//contagious
        else if (this.getTier() == 2)
        {
            this.modelName = "missile_contagious.tcn";
        }
    }

    @Override
    public void init()
    {
        if (this.getTier() == 1)
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', ICBMCore.itemPoisonPowder, '?', debilitation.getItemStack() }));
        }
        else if (this.getTier() == 2)
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Items.rotten_flesh, '@', chemical.getItemStack() }));
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
