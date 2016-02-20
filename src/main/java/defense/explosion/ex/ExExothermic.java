package defense.explosion.ex;

import mekanism.api.Pos3D;
import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.Settings;
import defense.explosion.explosive.Explosive;
import defense.explosion.explosive.blast.BlastExothermic;
import defense.explosion.model.missiles.ModelExothermicMissile;

public class ExExothermic extends Explosion
{
    public boolean createNetherrack = true;

    public ExExothermic()
    {
        super("exothermic", 3);
        this.createNetherrack = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Exothermic Create Netherrack", createNetherrack).getBoolean(createNetherrack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelExothermicMissile();
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
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "!!!", "!@!", "!!!", '@', Blocks.glass, '!', Explosive.incendiary.getItemStack() }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastExothermic(world, entity, x, y, z, 50).explode();
    }
}
