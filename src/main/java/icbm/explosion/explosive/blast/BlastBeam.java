package icbm.explosion.explosive.blast;

import icbm.core.entity.EntityFlyingBlock;
import icbm.explosion.entities.EntityLightBeam;
import icbm.explosion.explosive.thread.ThreadExplosion;
import icbm.explosion.explosive.thread.ThreadSky;

import java.util.HashSet;
import java.util.Set;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** Used by Exothermic and Endothermic explosions.
 * 
 * @author Calclavia */
public abstract class BlastBeam extends Blast
{
    protected ThreadExplosion thread;
    protected Set<EntityFlyingBlock> feiBlocks = new HashSet<EntityFlyingBlock>();
    protected EntityLightBeam lightBeam;
    protected float red, green, blue;
    /** Radius in which the uplighting of blocks takes place */
    protected int radius = 5;

    public BlastBeam(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    /** Called before an explosion happens */
    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.worldObj.createExplosion(this.exploder, position.xPos, position.yPos, position.zPos, 4F, true);

            this.lightBeam = new EntityLightBeam(this.worldObj, position, 20 * 20, this.red, this.green, this.blue);
            this.worldObj.spawnEntityInWorld(this.lightBeam);

            this.thread = new ThreadSky(worldObj, this.position, (int) this.getRadius(), 50, this.exploder);
            this.thread.start();
        }
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.callCount > 100 / this.proceduralInterval() && this.thread.isComplete)
            {
                this.controller.endExplosion();
            }

            if (this.canFocusBeam(this.worldObj, position))
            {
                Pos3D currentPos;
                Block block;
                int metadata;
                double dist;

                int r = radius;

                for (int x = -r; x < r; x++)
                {
                    for (int y = -r; y < r; y++)
                    {
                        for (int z = -r; z < r; z++)
                        {
                            dist = MathHelper.sqrt_double((x * x + y * y + z * z));

                            if (dist > r || dist < r - 3)
                            {
                                continue;
                            }
                            
                            currentPos = new Pos3D(position.xPos + x, position.yPos + y, position.zPos + z);
                            block = this.worldObj.getBlock((int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos);
                            
                            if (block == null || block.isAir(this.worldObj, x, y, z) || block.getBlockHardness(this.worldObj, x, y, x) < 0)
                            {
                                continue;
                            }

                            metadata = this.worldObj.getBlockMetadata((int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos);

                            if (this.worldObj.rand.nextInt(2) > 0)
                            {
                                this.worldObj.setBlockToAir((int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos);

                                currentPos.translate(0.5D, 0.5D, 0.5D);
                                EntityFlyingBlock entity = new EntityFlyingBlock(this.worldObj, currentPos, block, metadata);
                                this.worldObj.spawnEntityInWorld(entity);
                                this.feiBlocks.add(entity);
                                entity.pitchChange = 50 * this.worldObj.rand.nextFloat();
                            }
                        }
                    }
                }
            }
            else
            {
                this.controller.endExplosion();
            }

            for (EntityFlyingBlock entity : this.feiBlocks)
            {
                Pos3D entityPosition = new Pos3D(entity);
                Pos3D centeredPosition = entityPosition.clone().translate(this.position.scale(-1));
                centeredPosition.rotateYaw(2);
                Pos3D newPosition = this.position.clone().translate(centeredPosition);
                entity.motionX /= 3;
                entity.motionY /= 3;
                entity.motionZ /= 3;
                entity.addVelocity((newPosition.xPos - entityPosition.xPos) * 0.5 * this.proceduralInterval(), 0.09 * this.proceduralInterval(), (newPosition.zPos - entityPosition.zPos) * 0.5 * this.proceduralInterval());
                entity.yawChange += 3 * this.worldObj.rand.nextFloat();
            }
        }
    }

    @Override
    public void doPostExplode()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.lightBeam != null)
            {
                this.lightBeam.setDead();
                this.lightBeam = null;
            }
        }
    }

    public boolean canFocusBeam(World worldObj, Pos3D position)
    {
        return worldObj.canBlockSeeTheSky((int)position.xPos, (int)position.yPos + 1, (int)position.zPos);
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @param return - Return -1 if this explosive does not need proceudral calls */
    @Override
    public int proceduralInterval()
    {
        return 4;
    }

    @Override
    public long getEnergy()
    {
        return 10000;
    }

}
