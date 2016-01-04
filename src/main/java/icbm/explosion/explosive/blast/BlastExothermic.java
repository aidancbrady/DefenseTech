package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.ex.ExExothermic;
import icbm.explosion.explosive.Explosive;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlastExothermic extends BlastBeam
{
    public BlastExothermic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.red = 0.7f;
        this.green = 0.3f;
        this.blue = 0;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "beamcharging", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.worldObj.isRemote)
        {
            this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "powerdown", 4.0F, 0.8F);

            if (this.canFocusBeam(this.worldObj, position) && this.thread.isComplete)
            {
                for (Pos3D targetPosition : this.thread.results)
                {
                    double distanceFromCenter = position.distance(targetPosition);

                    if (distanceFromCenter > this.getRadius())
                        continue;

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = this.getRadius() - (Math.random() * distanceFromCenter);

                    if (chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Check to see if the block is an air block and there is a block below it
                         * to support the fire.
                         */
                        Block block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                        if (block == Blocks.water || block == Blocks.flowing_water || block == Blocks.ice)
                        {
                            this.worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                        }

                        if ((worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) || block == Blocks.snow) && this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos).getMaterial().isSolid())
                        {
                            if (this.worldObj.rand.nextFloat() > 0.999)
                            {
                                this.worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.flowing_lava, 0, 2);
                            }
                            else
                            {
                                this.worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.fire, 0, 2);

                                block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos);

                                if (((ExExothermic) Explosive.exothermic).createNetherrack && (block == Blocks.stone || block == Blocks.grass || block == Blocks.dirt) && this.worldObj.rand.nextFloat() > 0.75)
                                {
                                    this.worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos, Blocks.netherrack, 0, 2);
                                }
                            }
                        }
                        else if (block == Blocks.ice)
                        {
                            this.worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                        }
                    }
                }

                this.worldObj.playSoundEffect(position.xPos + 0.5D, position.yPos + 0.5D, position.zPos + 0.5D, Reference.PREFIX + "explosionfire", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
            }

            this.worldObj.setWorldTime(18000);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Pos3D position)
    {
        long worldTime = worldObj.getWorldTime();

        while (worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime < 12000 && !worldObj.isRaining() && super.canFocusBeam(worldObj, position);
    }

}
