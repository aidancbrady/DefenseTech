package defense.common.explosive.blast;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import defense.common.Reference;
import defense.common.entity.EntityExplosion;

public class BlastAntimatter extends Blast
{
    private boolean destroyBedrock;

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size, boolean destroyBedrock)
    {
        this(world, entity, x, y, z, size);
        this.destroyBedrock = destroyBedrock;
    }

    /** Called before an explosion happens */
    @Override
    public void doPreExplode()
    {
        super.doPreExplode();
        this.worldObj.playSoundEffect(this.position.xPos, this.position.yPos, this.position.zPos, Reference.PREFIX + "antimatter", 7F, (float) (this.worldObj.rand.nextFloat() * 0.1 + 0.9F));
        this.doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            for (int x = (int) -this.getRadius(); x < this.getRadius(); x++)
            {
                for (int y = (int) -this.getRadius(); y < this.getRadius(); y++)
                {
                    for (int z = (int) -this.getRadius(); z < this.getRadius(); z++)
                    {
                        Pos3D targetPosition = this.position.clone().translate(new Pos3D(x, y, z));

                        double dist = position.distance(targetPosition);

                        if (dist < this.getRadius())
                        {
                        	Coord4D coord = targetPosition.getCoord(worldObj.provider.dimensionId);
                        	
                            Block block = coord.getBlock(worldObj);

                            if (!block.isAir(this.worldObj, x, y, x))
                            {
                                if (!this.destroyBedrock && block.getBlockHardness(this.worldObj, x, y, x) < 0)
                                {
                                    continue;
                                }

                                if (dist < this.getRadius() - 1 || worldObj.rand.nextFloat() > 0.7)
                                {
                                	worldObj.setBlockToAir(coord.xCoord, coord.yCoord, coord.zCoord);
                                }
                            }
                        }
                    }

                }
            }
        }

        // TODO: Render antimatter shockwave
        /*
         * else if (ZhuYao.proxy.isGaoQing()) { for (int x = -this.getRadius(); x <
         * this.getRadius(); x++) { for (int y = -this.getRadius(); y < this.getRadius(); y++) { for
         * (int z = -this.getRadius(); z < this.getRadius(); z++) { Vector3 targetPosition =
         * Vector3.add(position, new Vector3(x, y, z)); double distance =
         * position.distanceTo(targetPosition);
         * if (targetPosition.getBlockID(worldObj) == 0) { if (distance < this.getRadius() &&
         * distance > this.getRadius() - 1 && worldObj.rand.nextFloat() > 0.5) {
         * ParticleSpawner.spawnParticle("antimatter", worldObj, targetPosition); } } } } } }
         */
    }

    @Override
    public void doPostExplode()
    {
        doDamageEntities(this.getRadius() * 2, Integer.MAX_VALUE);
    }

    @Override
    protected boolean onDamageEntity(Entity entity)
    {
        if(entity instanceof EntityExplosion)
        {
            if(((EntityExplosion)entity).blast instanceof BlastRedmatter)
            {
                entity.setDead();
                return true;
            }
        }

        return false;
    }

    @Override
    public long getEnergy()
    {
        return 30000000;
    }
}
