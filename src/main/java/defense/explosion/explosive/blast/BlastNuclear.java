package defense.explosion.explosive.blast;

import defense.Reference;
import defense.Settings;
import defense.core.CoreModule;
import defense.explosion.ExplosionModule;
import defense.explosion.explosive.thread.ThreadLargeExplosion;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class BlastNuclear extends Blast
{
    public static boolean POLLUTIVE_NUCLEAR = true;

    static
    {
        Settings.CONFIGURATION.load();
        POLLUTIVE_NUCLEAR = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Pollutive Nuclear", POLLUTIVE_NUCLEAR).getBoolean(POLLUTIVE_NUCLEAR);
        Settings.CONFIGURATION.save();
    }

    private ThreadLargeExplosion thread;
    private float energy;
    private boolean spawnMoreParticles = false;
    private boolean isRadioactive = false;

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size, float energy)
    {
        this(world, entity, x, y, z, size);
        this.energy = energy;
    }

    public BlastNuclear setNuclear()
    {
        this.spawnMoreParticles = true;
        this.isRadioactive = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.thread = new ThreadLargeExplosion(worldObj, this.position, (int) this.getRadius(), this.energy, this.exploder);

            this.thread.start();

        }
        else if (this.spawnMoreParticles && ExplosionModule.proxy.isGaoQing())
        {
            // Spawn nuclear cloud.
            for (int y = 0; y < 26; y++)
            {
                int r = 4;

                if (y < 8)
                {
                    r = Math.max(Math.min((8 - y) * 2, 10), 4);
                }
                else if (y > 15)
                {
                    r = Math.max(Math.min((y - 15) * 2, 15), 5);
                }

                for (int x = -r; x < r; x++)
                {
                    for (int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if (r > distance && r - 3 < distance)
                        {
                            Pos3D spawnPosition = position.clone().translate(new Pos3D(x * 2, (y - 2) * 2, z * 2));
                            float xDiff = (float) (spawnPosition.xPos - position.xPos);
                            float zDiff = (float) (spawnPosition.zPos - position.zPos);
                            ExplosionModule.proxy.spawnParticle("smoke", worldObj, spawnPosition, xDiff * 0.3 * worldObj.rand.nextFloat(), -worldObj.rand.nextFloat(), zDiff * 0.3 * worldObj.rand.nextFloat(), (float) (distance / this.getRadius()) * worldObj.rand.nextFloat(), 0, 0, 8F, 1.2F);
                        }
                    }
                }
            }
        }

        this.doDamageEntities(this.getRadius(), this.energy * 1000);

        this.worldObj.playSoundEffect(this.position.xPos, this.position.yPos, this.position.zPos, Reference.PREFIX + "explosion", 7.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public void doExplode()
    {
        int r = this.callCount;

        if (this.worldObj.isRemote)
        {
            if (ExplosionModule.proxy.isGaoQing())
            {
                for (int x = -r; x < r; x++)
                {
                    for (int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if (distance < r && distance > r - 1)
                        {
                            Pos3D targetPosition = this.position.clone().translate(new Pos3D(x, 0, z));

                            if (this.worldObj.rand.nextFloat() < Math.max(0.001 * r, 0.05))
                            {
                                ExplosionModule.proxy.spawnParticle("smoke", this.worldObj, targetPosition, 5F, 1F);
                            }
                        }
                    }
                }
            }

        }
        else
        {
            if (this.thread != null)
            {
                if (this.thread.isComplete)
                {
                    this.controller.endExplosion();
                }
            }
            else
            {
                this.controller.endExplosion();
                CoreModule.LOGGER.severe("Something went wrong with multi-threading while detonating the nuclear explosive.");
            }
        }
    }

    @Override
    public void doPostExplode()
    {
        try
        {
            if (!this.worldObj.isRemote && this.thread.isComplete)
            {
                for (Pos3D p : this.thread.results)
                {
                    Block block = this.worldObj.getBlock((int)p.xPos, (int)p.yPos, (int)p.zPos);
                    if (!worldObj.isAirBlock((int)p.xPos, (int)p.yPos, (int)p.zPos))
                        block.onBlockExploded(this.worldObj, (int)p.xPos, (int)p.yPos, (int)p.zPos, this);

                }
            }
        }
        catch (Exception e)
        {
            CoreModule.LOGGER.severe("Nuclear-type detonation Failed!");
            e.printStackTrace();
        }

        this.doDamageEntities(this.getRadius(), this.energy * 1000);

        if (this.isRadioactive)
        {
            new BlastRot(worldObj, this.exploder, position.xPos, position.yPos, position.zPos, this.getRadius(), this.energy).explode();
            new BlastMutation(worldObj, this.exploder, position.xPos, position.yPos, position.zPos, this.getRadius()).explode();

            if (this.worldObj.rand.nextInt(3) == 0)
            {
            	worldObj.getWorldInfo().setRaining(true);
            }
        }

        this.worldObj.playSoundEffect(this.position.xPos, this.position.yPos, this.position.zPos, Reference.PREFIX + "explosion", 10.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @param return - Return -1 if this explosive does not need procedural calls */
    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return (long) (41840000 * this.energy);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.spawnMoreParticles = nbt.getBoolean("spawnMoreParticles");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("spawnMoreParticles", this.spawnMoreParticles);

    }
}
