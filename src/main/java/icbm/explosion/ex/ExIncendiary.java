package icbm.explosion.ex;

import icbm.explosion.explosive.blast.BlastFire;
import mekanism.api.Pos3D;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExIncendiary extends Explosion
{
    public ExIncendiary(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.modelName = "missile_incendiary.tcn";
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', "dustSulfur", '?', replsive.getItemStack(), '!', Items.lava_bucket }));
    }

    @Override
    public void onDetonation(World worldObj, Pos3D position, int fuseTicks)
    {
        super.onDetonation(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.xPos, position.yPos + 0.5D, position.zPos, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastFire(world, entity, x, y, z, 14).explode();
    }
}
