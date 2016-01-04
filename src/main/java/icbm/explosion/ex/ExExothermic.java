package icbm.explosion.ex;

import icbm.Settings;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.blast.BlastExothermic;
import mekanism.api.Pos3D;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExExothermic extends Explosion
{
    public boolean createNetherrack = true;

    public ExExothermic()
    {
        super("exothermic", 3);
        this.createNetherrack = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", createNetherrack).getBoolean(createNetherrack);
        this.modelName = "missile_endothermic.tcn";
    }

    @Override
    public void onDetonation(World worldObj, Pos3D position, int fuseTicks)
    {
        super.onDetonation(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.xPos, position.yPos + 0.5D, position.zPos, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "!!!", "!@!", "!!!", '@', Blocks.glass, '!', Explosive.incendiary.getItemStack() }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastExothermic(world, entity, x, y, z, 50).explode();
    }
}
