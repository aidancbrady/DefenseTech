package icbm.explosion.explosive.blast;

import ic2.api.item.ISpecialElectricItem;
import ic2.api.tile.IEnergyStorage;
import icbm.Reference;
import icbm.api.IEMPBlock;
import icbm.api.IEMPItem;
import icbm.api.IMissile;
import icbm.api.RadarRegistry;
import icbm.core.Vector2;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityExplosive;

import java.util.List;

import mekanism.api.Pos3D;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;

public class BlastEMP extends Blast
{
    private boolean effectEntities = false;
    private boolean effectBlocks = false;

    public BlastEMP(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastEMP setEffectBlocks()
    {
        this.effectBlocks = true;
        return this;
    }

    public BlastEMP setEffectEntities()
    {
        this.effectEntities = true;
        return this;
    }

    @Override
    public void doExplode()
    {
        if (this.effectBlocks)
        {
            for (int x = (int) -this.getRadius(); x < (int) this.getRadius(); x++)
            {
                for (int y = (int) -this.getRadius(); y < (int) this.getRadius(); y++)
                {
                    for (int z = (int) -this.getRadius(); z < (int) this.getRadius(); z++)
                    {
                        double dist = MathHelper.sqrt_double((x * x + y * y + z * z));

                        Pos3D searchPosition = position.clone().translate(new Pos3D(x, y, z));
                        if (dist > this.getRadius())
                            continue;

                        if (Math.round(position.xPos + y) == (int)position.yPos)
                        {
                            worldObj.spawnParticle("largesmoke", searchPosition.xPos, searchPosition.yPos, searchPosition.zPos, 0, 0, 0);
                        }

                        Block block = searchPosition.getCoord(worldObj.provider.dimensionId).getBlock(worldObj);
                        TileEntity tileEntity = searchPosition.getCoord(worldObj.provider.dimensionId).getTileEntity(worldObj);

                        if (!worldObj.isAirBlock((int)searchPosition.xPos, (int)searchPosition.yPos, (int)searchPosition.zPos))
                        {
                            if (block instanceof IEMPBlock)
                            {
                                ((IEMPBlock) block).onEMP(worldObj, (int)searchPosition.xPos, (int)searchPosition.yPos, (int)searchPosition.zPos, this);
                            }
                        }

                        if (tileEntity != null)
                        {
                            if (tileEntity instanceof IEnergyStorage)
                            {
                                ((IEnergyStorage) tileEntity).setStored(0);
                            }
                            else if (tileEntity instanceof IStrictEnergyStorage)
                            {
                            	((IStrictEnergyStorage) tileEntity).setEnergy(0);
                            }
                            else if (tileEntity instanceof IEnergyHandler)
                            {
                            	((IEnergyHandler) tileEntity).extractEnergy(ForgeDirection.UNKNOWN, ((IEnergyHandler)tileEntity).getEnergyStored(ForgeDirection.UNKNOWN), false);
                            }
                        }
                    }
                }
            }
        }

        if (this.effectEntities)
        {
            // Drop all missiles
            List<Entity> entitiesNearby = RadarRegistry.getEntitiesWithinRadius(new Vector2(position), (int) this.getRadius());

            for (Entity entity : entitiesNearby)
            {
                if (entity instanceof IMissile && !entity.isEntityEqual(this.controller))
                {
                    if (((IMissile) entity).getTicksInAir() > -1)
                    {
                        ((IMissile) entity).dropMissileAsItem();
                    }
                }
            }

            int maxFx = 10;
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.xPos - this.getRadius(), position.yPos - this.getRadius(), position.zPos - this.getRadius(), position.xPos + this.getRadius(), position.yPos + this.getRadius(), position.zPos + this.getRadius());
            List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

            for (Entity entity : entities)
            {
                if (entity instanceof EntityLivingBase)
                {
                    if (this.worldObj.isRemote && maxFx > 0)
                    {
                        ICBMExplosion.proxy.spawnShock(this.worldObj, this.position, new Pos3D(entity), 20);
                        maxFx--;
                    }

                    if (entity instanceof EntityCreeper)
                    {
                        if (!this.worldObj.isRemote)
                        {
                            try
                            {
                                ((EntityCreeper) entity).getDataWatcher().updateObject(17, (byte) 1);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (entity instanceof EntityPlayer)
                    {
                        IInventory inventory = ((EntityPlayer) entity).inventory;

                        for (int i = 0; i < inventory.getSizeInventory(); i++)
                        {
                            ItemStack itemStack = inventory.getStackInSlot(i);

                            if (itemStack != null)
                            {
                                if (itemStack.getItem() instanceof IEMPItem)
                                {
                                    ((IEMPItem) itemStack.getItem()).onEMP(itemStack, entity, this);
                                }
                                else if (itemStack.getItem() instanceof IEnergizedItem)
                                {
                                    ((IEnergizedItem) itemStack.getItem()).setEnergy(itemStack, 0);
                                }
                                else if (itemStack.getItem() instanceof IEnergyContainerItem)
                                {
                                	((IEnergyContainerItem) itemStack.getItem()).extractEnergy(itemStack, ((IEnergyContainerItem)itemStack.getItem()).getEnergyStored(itemStack), false);
                                }
                                else if (itemStack.getItem() instanceof ISpecialElectricItem)
                                {
                                    ((ISpecialElectricItem) itemStack.getItem()).getManager(itemStack).discharge(itemStack, ((ISpecialElectricItem) itemStack.getItem()).getMaxCharge(itemStack), 0, true, false, false);
                                }
                            }
                        }
                    }
                }
                else if (entity instanceof EntityExplosive)
                {
                    entity.setDead();
                }
            }
        }

        ICBMExplosion.proxy.spawnParticle("shockwave", worldObj, position, 0, 0, 0, 0, 0, 255, 10, 3);
        this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "emp", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public long getEnergy()
    {
        return 3000;
    }
}
