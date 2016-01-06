package defense.explosion;

import java.util.List;

import mekanism.api.Pos3D;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.tile.TileEntityContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import defense.explosion.container.ContainerCruiseLauncher;
import defense.explosion.container.ContainerMissileCoordinator;
import defense.explosion.entities.EntityMissile;
import defense.explosion.explosive.TileExplosive;
import defense.explosion.machines.TileCruiseLauncher;
import defense.explosion.machines.TileEMPTower;
import defense.explosion.machines.TileMissileCoordinator;
import defense.explosion.machines.TileRadarStation;
import defense.explosion.machines.launcher.TileLauncherBase;
import defense.explosion.machines.launcher.TileLauncherFrame;
import defense.explosion.machines.launcher.TileLauncherScreen;

public class CommonProxy implements IGuiHandler
{
    public void preInit()
    {
    }

    public void init()
    {
        GameRegistry.registerTileEntity(TileCruiseLauncher.class, "CruiseLauncher");
        GameRegistry.registerTileEntity(TileLauncherBase.class, "LauncherBase");
        GameRegistry.registerTileEntity(TileLauncherScreen.class, "LauncherScreen");
        GameRegistry.registerTileEntity(TileLauncherFrame.class, "LauncherFrame");
        GameRegistry.registerTileEntity(TileRadarStation.class, "RadarStation");
        GameRegistry.registerTileEntity(TileEMPTower.class, "EMPTower");
        GameRegistry.registerTileEntity(TileMissileCoordinator.class, "MissileCoordinator");
        GameRegistry.registerTileEntity(TileExplosive.class, "Explosive");
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileCruiseLauncher)
        {
            return new ContainerCruiseLauncher(player.inventory, (TileCruiseLauncher) tileEntity);
        }
        else if (tileEntity instanceof TileMissileCoordinator)
        {
            return new ContainerMissileCoordinator(player.inventory, (TileMissileCoordinator) tileEntity);
        }
        else if (tileEntity instanceof TileLauncherScreen || tileEntity instanceof TileRadarStation || tileEntity instanceof TileEMPTower || tileEntity instanceof TileLauncherBase || tileEntity instanceof TileMissileCoordinator)
        {
            return new ContainerNull(player, (TileEntityContainerBlock)tileEntity);
        }

        return null;
    }

    public boolean isGaoQing()
    {
        return false;
    }

    public void spawnParticle(String name, World world, Pos3D position, float scale, double distance)
    {
        this.spawnParticle(name, world, position, 0, 0, 0, scale, distance);
    }

    public void spawnParticle(String name, World world, Pos3D position, double motionX, double motionY, double motionZ, float scale, double distance)
    {
        this.spawnParticle(name, world, position, motionX, motionY, motionZ, 1, 1, 1, scale, distance);
    }

    public void spawnParticle(String name, World world, Pos3D position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
    {

    }
    
    public void playSound(EntityMissile missile) {}

    public int getParticleSetting()
    {
        return -1;
    }

    public List<Entity> getEntityFXs()
    {
        return null;
    }

    public void spawnShock(World world, Pos3D position, Pos3D target)
    {

    }

    public void spawnShock(World world, Pos3D startVec, Pos3D targetVec, int duration)
    {
        // TODO Auto-generated method stub

    }

}
