package defense.explosion.ex;

import mekanism.api.Pos3D;
import mekanism.common.recipe.MekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.ModelMissileBase;
import defense.explosion.explosive.Explosive;
import defense.explosion.explosive.blast.BlastMine;
import defense.explosion.model.tiles.MDiLei;

public class ExSMine extends Explosive
{
    public ExSMine(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setFuseTime(20);
        this.hasGrenade = false;
        this.hasMinecart = false;
        this.hasMissile = false;
    }

    @Override
    public void onDetonation(World worldObj, Pos3D position, int fuseTicks) {}

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new MekanismRecipe(this.getItemStack(), new Object[] { "S", "L", "R", 'S', Explosive.fragmentation.getItemStack(), 'L', Explosive.attractive.getItemStack(), 'R', Explosive.replsive.getItemStack() }));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelMissileBase getBlockModel()
    {
        return MDiLei.INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getBlockResource()
    {
        return MDiLei.TEXTURE;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastMine(world, entity, x, y, z, 5).explode();
    }

}
