package defense.explosion.ex;

import mekanism.api.Pos3D;
import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.Reference;
import defense.Settings;
import defense.explosion.explosive.Explosive;
import defense.explosion.explosive.blast.BlastAntimatter;
import defense.explosion.model.missiles.ModelAntimatterMissile;

public class ExAntimatter extends Explosion
{
    public ExAntimatter()
    {
        super("antimatter", 4);
        this.setFuseTime(300);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelAntimatterMissile();
    }

    /** Called when the explosive is on fuse and going to explode. Called only when the explosive is
     * in it's TNT form.
     * 
     * @param fuseTicks - The amount of ticks this explosive is on fuse */
    @Override
    public void onDetonation(World worldObj, Pos3D position, int fuseTicks)
    {
        super.onDetonation(worldObj, position, fuseTicks);

        if (fuseTicks % 25 == 0)
        {
            worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "alarm", 4F, 1F);
        }
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "AAA", "AEA", "AAA", 'E', Explosive.nuclear.getItemStack(), 'A', "antimatterGram" }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntimatter(world, entity, x, y, z, Settings.ANTIMATTER_SIZE, Settings.DESTROY_BEDROCK).explode();
    }
}
