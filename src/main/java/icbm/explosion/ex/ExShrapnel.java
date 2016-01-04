package icbm.explosion.ex;

import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastShrapnel;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExShrapnel extends Explosion
{
    public ExShrapnel(String name, int tier)
    {
        super(name, tier);
        if (name.equalsIgnoreCase("shrapnel"))
        {
            this.modelName = "missile_shrapnel.tcn";
        }
        else if (name.equalsIgnoreCase("anvil"))
        {
            this.modelName = "missile_anvil.tcn";
        }
        else if (name.equalsIgnoreCase("fragmentation"))
        {
            this.modelName = "missile_fragment.tcn";
        }
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.shrapnel.getID())
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', replsive.getItemStack(), '?', Items.arrow }));
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Blocks.anvil, 'S', Explosive.shrapnel.getItemStack() }));
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', incendiary.getItemStack(), '@', shrapnel.getItemStack() }));
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 2)
        {
            new BlastShrapnel(world, entity, x, y, z, 15, true, true, false).explode();
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            new BlastShrapnel(world, entity, x, y, z, 25, false, false, true).explode();
        }
        else
        {
            new BlastShrapnel(world, entity, x, y, z, 30, true, false, false).explode();
        }
    }
}
