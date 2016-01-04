package icbm.explosion.machines;

import icbm.Reference;
import icbm.api.RadarRegistry;
import icbm.core.IBlockActivate;
import icbm.core.IRedstoneReceptor;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.blast.BlastEMP;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.network.SimpleComponent;
import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEMPTower extends TileEntityElectricBlock implements IBoundingBlock, IRedstoneReceptor, IBlockActivate, SimpleComponent
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float rotation = 0;
    private float rotationDelta, prevXuanZhuanLu = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    private int cooldownTicks = 0;

    // The EMP explosion radius
    public int empRadius = 60;
    
    public double energyToUse = 0;

    public TileEMPTower()
    {
    	super("EMPTower", 10000000);
        RadarRegistry.register(this);
        updateCapacity();
    }

    @Override
    public void invalidate()
    {
        RadarRegistry.unregister(this);
        super.invalidate();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!isReady())
        {
            cooldownTicks--;
        }

        if (ticker % 20 == 0 && getEnergy() > 0)
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Reference.PREFIX + "machinehum", 0.5F, 0.85F * (float)getEnergy() / (float)getMaxEnergy());

        rotationDelta = (float) (Math.pow(getEnergy() / getMaxEnergy(), 2) * 0.5);
        rotation += rotationDelta;
        if (rotation > 360)
            rotation = 0;

        prevXuanZhuanLu = rotationDelta;
    }
    
    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
    	if(!worldObj.isRemote)
    	{
    		int packetType = dataStream.readInt();
    		
    		if(packetType == 1)
    		{
    			empRadius = dataStream.readInt();
                updateCapacity();
    		}
    		else if(packetType == 2)
    		{
    			empMode = dataStream.readByte();
    		}
	    	
	    	return;
    	}
    	
    	super.handlePacketData(dataStream);
    	
    	int prev = empRadius;
    	
    	empRadius = dataStream.readInt();
        empMode = dataStream.readByte();
        
        if(prev != empRadius)
        {
        	updateCapacity();
        }
    }
    
    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
    	super.getNetworkedData(data);
    	
    	data.add(empRadius);
    	data.add(empMode);
    	
    	return data;
    }

    @Callback
    public boolean isReady()
    {
        return getCooldown() <= 0;
    }

    @Callback
    public int getCooldown()
    {
        return cooldownTicks;
    }

    @Callback
    public int getMaxCooldown()
    {
        return 120;
    }

    private void updateCapacity()
    {
        maxEnergy = Math.max(3000000 * (this.empRadius / MAX_RADIUS), 10000000);
        energyToUse = getMaxEnergy()/0.9D;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.empRadius = par1NBTTagCompound.getInteger("banJing");
        this.empMode = par1NBTTagCompound.getByte("muoShi");
        
        updateCapacity();
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("banJing", this.empRadius);
        par1NBTTagCompound.setByte("muoShi", this.empMode);
    }

    @Callback(limit = 1)
    public boolean fire()
    {
        if (getEnergy() >= energyToUse)
        {
            if (isReady())
            {
                switch (this.empMode)
                {
                    default:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
                        break;
                    case 1:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
                        break;
                    case 2:
                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
                        break;
                }
                
                setEnergy(getEnergy()-energyToUse);
                this.cooldownTicks = getMaxCooldown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPowerOn()
    {
        fire();
    }

    @Override
    public void onPowerOff()
    {
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }
    
    @Override
	public void onPlace()
	{
		Coord4D pos = Coord4D.get(this);
		MekanismUtils.makeBoundingBlock(worldObj, pos.getFromSide(ForgeDirection.UP, 1), pos);
	}

	@Override
	public void onBreak()
	{
		worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public String getComponentName()
    {
        return "emptower";
    }

    @Callback
    public byte getEmpMode()
    {
        return empMode;
    }

    @Callback
    public void setEmpMode(byte empMode)
    {
        if (empMode >= 0 && empMode <= 2)
            this.empMode = empMode;
    }

    @Callback
    public void empMissiles()
    {
        this.empMode = 1;
    }

    @Callback
    public void empAll()
    {
        this.empMode = 0;
    }

    @Callback
    public void empElectronics()
    {
        this.empMode = 2;
    }

    @Callback
    public int getEmpRadius()
    {
        return empRadius;
    }

    @Callback
    public int getMaxEmpRadius()
    {
        return MAX_RADIUS;
    }

    @Callback
    public void setEmpRadius(int empRadius)
    {
        int prev = getEmpRadius();
        this.empRadius = Math.min(Math.max(empRadius, 0), MAX_RADIUS);
        if (prev != getEmpRadius())
            updateCapacity();
    }

    @Callback
    public static int getMaxRadius()
    {
        return MAX_RADIUS;
    }
}
