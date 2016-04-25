package defense.explosion.machines.launcher;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import defense.api.IMissile;
import defense.api.ITier;
import defense.core.DefenseTech;
import defense.core.IBlockActivate;

/** This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherScreen extends TileLauncherPrefab implements IBlockActivate, ITier
{
    // The tier of this screen
    private int tier = 0;

    // The missile launcher base in which this
    // screen is connected with
    public TileLauncherBase laucherBase = null;

    public short gaoDu = 3;

    private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public TileLauncherScreen()
    {
    	super("LauncherScreen", 10000000);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.laucherBase == null)
        {
            for(ForgeDirection side : MekanismUtils.SIDE_DIRS)
            {
                Coord4D position = new Coord4D(this.xCoord, this.yCoord, this.zCoord);
                position.step(side);

                TileEntity tileEntity = position.getTileEntity(worldObj);

                if (tileEntity != null)
                {
                    if (tileEntity instanceof TileLauncherBase)
                    {
                        this.laucherBase = (TileLauncherBase) tileEntity;
                        setFacing((short)side.getOpposite().ordinal());
                    }
                }
            }
        }
        else
        {
            if (this.laucherBase.isInvalid())
            {
                this.laucherBase = null;
            }
        }

        if(!worldObj.isRemote)
        {
	        if (this.ticker % 40 == 0 && this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
	        {
	            this.launch();
	        }
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ticker % 3 == 0)
            {
                if (this.targetPos == null)
                {
                    this.targetPos = new Pos3D(this.xCoord, 0, this.zCoord);
                }
            }

            if (this.ticker % 600 == 0)
            {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }
    
    @Override
	public void handlePacketData(ByteBuf dataStream)
	{
    	if(!worldObj.isRemote)
    	{
    		int packetType = dataStream.readInt();
    		
    		if(packetType == 1)
    		{
    			this.setFrequency(dataStream.readInt());
    		}
    		else if(packetType == 2)
    		{
    			this.targetPos = new Pos3D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

                if (this.getTier() < 2)
                {
                    this.targetPos.yPos = 0;
                }
    		}
    		else if(packetType == 3)
    		{
    			this.gaoDu = (short) Math.max(Math.min(dataStream.readShort(), Short.MAX_VALUE), 3);
    		}
    		
    		return;
    	}
    	
		super.handlePacketData(dataStream);
		
        this.tier = dataStream.readInt();
        this.gaoDu = dataStream.readShort();
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(tier);
		data.add(gaoDu);
		
		return data;
	}

    @Override
    public void placeMissile(ItemStack itemStack)
    {
        if (this.laucherBase != null)
        {
            if (!this.laucherBase.isInvalid())
            {
                this.laucherBase.setInventorySlotContents(0, itemStack);
            }
        }
    }

    // Checks if the missile is launchable
    @Override
    public boolean canLaunch()
    {
        if (this.laucherBase != null && this.laucherBase.missile != null)
        {
            if (getEnergy() >= getLaunchCost())
            {
                return this.laucherBase.isInRange(this.targetPos);
            }
        }
        return false;
    }

    /** Calls the missile launcher base to launch it's missile towards a targeted location */
    @Override
    public void launch()
    {
        if (this.canLaunch())
        {
            setEnergy(getEnergy()-getLaunchCost());
            this.laucherBase.launchMissile(this.targetPos.clone(), this.gaoDu);
        }
    }

    /** Gets the display status of the missile launcher
     * 
     * @return The string to be displayed */
    @Override
    public String getStatus()
    {
        String color = "\u00a74";
        String status = LangUtils.localize("gui.misc.idle");

        if (this.laucherBase == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusMissing");
        }
        else if (getEnergy() < getLaunchCost())
        {
            status = LangUtils.localize("gui.launcherScreen.statusNoPower");
        }
        else if (this.laucherBase.missile == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusEmpty");
        }
        else if (this.targetPos == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusInvalid");
        }
        else if (this.laucherBase.shiTaiJin(this.targetPos))
        {
            status = LangUtils.localize("gui.launcherScreen.statusClose");
        }
        else if (this.laucherBase.shiTaiYuan(this.targetPos))
        {
            status = LangUtils.localize("gui.launcherScreen.statusFar");
        }
        else
        {
            color = "\u00a72";
            status = LangUtils.localize("gui.launcherScreen.statusReady");
        }

        return color + status;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.tier = par1NBTTagCompound.getInteger("tier");
        this.gaoDu = par1NBTTagCompound.getShort("gaoDu");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("tier", this.tier);
        par1NBTTagCompound.setShort("gaoDu", this.gaoDu);
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public int getTier()
    {
        return this.tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }

    public long getLaunchCost()
    {
        switch (this.getTier())
        {
            case 0:
                return 50000;
            case 1:
                return 80000;
        }

        return 100000;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        entityPlayer.openGui(DefenseTech.INSTANCE, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public LauncherType getLauncherType()
    {
        return LauncherType.TRADITIONAL;
    }

    @Override
    public IMissile getMissile()
    {
        if (this.laucherBase != null)
        {
            return this.laucherBase.missile;
        }

        return null;
    }
}
