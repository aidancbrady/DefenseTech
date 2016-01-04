package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastAntiGravitational;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExAntiGravitational extends Explosion
{
    public ExAntiGravitational()
    {
        super("antiGravitational", 3);
        this.modelName = "missile_antigrav.tcn";
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', replsive.getItemStack(), 'E', Items.ender_eye }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntiGravitational(world, entity, x, y, z, 30).explode();
    }
}
