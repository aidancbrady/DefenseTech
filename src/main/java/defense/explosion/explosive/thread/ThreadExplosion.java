package defense.explosion.explosive.thread;

import java.util.HashSet;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** @author Calclavia */
public abstract class ThreadExplosion extends Thread
{
    public final Pos3D position;
    public World worldObj;
    public int radius;
    public float energy;
    public Entity source;

    public boolean isComplete = false;

    public final HashSet<Pos3D> deltaSet = new HashSet<Pos3D>();
    public final HashSet<Pos3D> results = new HashSet<Pos3D>();

    public ThreadExplosion(World world, Pos3D position, int radius, float energy, Entity source)
    {
    	this.worldObj = world;
        this.position = position;
        this.radius = radius;
        this.energy = energy;
        this.source = source;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run()
    {
        this.isComplete = true;
    }
}
