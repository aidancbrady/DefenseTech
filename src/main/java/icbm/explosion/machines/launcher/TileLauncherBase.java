package icbm.explosion.machines.launcher;

import icbm.Settings;
import icbm.api.ExplosionEvent.ExplosivePreDetonationEvent;
import icbm.api.ExplosiveType;
import icbm.api.ILauncherContainer;
import icbm.api.ILauncherController;
import icbm.api.IMissile;
import icbm.api.ITier;
import icbm.core.IBlockActivate;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.ex.Explosion;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.items.ItemMissile;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityContainerBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

/** This tile entity is for the base of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherBase extends TileEntityContainerBlock implements ILauncherContainer, ITier, IBoundingBlock, IBlockActivate
{
    // The missile that this launcher is holding
    public IMissile missile = null;

    // The connected missile launcher frame
    public TileLauncherFrame supportFrame = null;

    // The tier of this launcher base
    private int tier = 0;

    private boolean packetGengXin = true;
    
    public TileLauncherBase()
    {
    	super("LauncherBase");
    	inventory = new ItemStack[1];
    }

    /** Returns the name of the inventory. */
    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("gui.launcherBase.name");
    }

    /** Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner
     * uses this to count ticks and creates a new spawn inside its implementation. */
    @Override
    public void onUpdate()
    {
        if (this.supportFrame == null)
        {
            for (byte i = 2; i < 6; i++)
            {
                Coord4D position = Coord4D.get(this);
                position.step(ForgeDirection.getOrientation(i));

                TileEntity tileEntity = position.getTileEntity(worldObj);

                if (tileEntity instanceof TileLauncherFrame)
                {
                    this.supportFrame = (TileLauncherFrame) tileEntity;
                    this.supportFrame.setFacing((short)MekanismUtils.getBaseOrientation(ForgeDirection.NORTH.ordinal(), i));
                }
            }
        }
        else
        {
            if (this.supportFrame.isInvalid())
            {
                this.supportFrame = null;
            }
            else if (this.packetGengXin || this.ticker % (20 * 30) == 0 && this.supportFrame != null && !this.worldObj.isRemote)
            {
            	Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(supportFrame), supportFrame.getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(supportFrame)));
            }
        }

        if (!this.worldObj.isRemote)
        {
            this.setMissile();

            if (this.packetGengXin || this.ticker % (20 * 30) == 0)
            {
            	Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
                this.packetGengXin = false;
            }
        }
    }
    
    @Override
	public void handlePacketData(ByteBuf dataStream)
	{
    	super.handlePacketData(dataStream);
    	
    	tier = dataStream.readInt();
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(tier);
		
		return data;
	}

    public void setMissile()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getStackInSlot(0) != null)
            {
                if (this.getStackInSlot(0).getItem() instanceof ItemMissile)
                {
                    int explosiveID = this.getStackInSlot(0).getItemDamage();

                    if (ExplosiveRegistry.get(explosiveID) instanceof Explosion)
                    {
                        Explosion missile = (Explosion) ExplosiveRegistry.get(explosiveID);

                        ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
                        MinecraftForge.EVENT_BUS.post(evt);

                        if (!evt.isCanceled())
                        {
                            if (this.missile == null)
                            {
                                Pos3D startingPosition = new Pos3D((this.xCoord + 0.5f), (this.yCoord + 1.8f), (this.zCoord + 0.5f));
                                this.missile = new EntityMissile(this.worldObj, startingPosition, new Pos3D(this), explosiveID);
                                this.worldObj.spawnEntityInWorld((Entity) this.missile);
                                return;
                            }
                            else
                            {
                                if (this.missile.getExplosiveType().getID() == explosiveID)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (this.missile != null)
            {
                ((Entity) this.missile).setDead();
            }

            this.missile = null;
        }
    }

    /** Launches the missile
     * 
     * @param target - The target in which the missile will land in */
    public void launchMissile(Pos3D target, int gaoDu)
    {
        // Apply inaccuracy
        float inaccuracy;

        if (this.supportFrame != null)
        {
            inaccuracy = this.supportFrame.getInaccuracy();
        }
        else
        {
            inaccuracy = 30f;
        }

        inaccuracy *= (float) Math.random() * 2 - 1;

        target.xPos += inaccuracy;
        target.zPos += inaccuracy;

        this.decrStackSize(0, 1);
        this.missile.launch(target, gaoDu);
        this.missile = null;
    }

    // Checks if the missile target is in range
    public boolean isInRange(Pos3D target)
    {
        if (target != null)
            return !shiTaiYuan(target) && !shiTaiJin(target);

        return false;
    }

    /** Checks to see if the target is too close.
     * 
     * @param target
     * @return */
    public boolean shiTaiJin(Pos3D target)
    {
        // Check if it is greater than the minimum range
        if (new Pos3D(this.xCoord, 0, this.zCoord).distance(new Pos3D(target.xPos, 0, target.zPos)) < 10)
        {
            return true;
        }

        return false;
    }

    // Is the target too far?
    public boolean shiTaiYuan(Pos3D target)
    {
        // Checks if it is greater than the maximum range for the launcher base
        if (this.tier == 0)
        {
            if (new Pos3D(this.xCoord, 0, this.zCoord).distance(new Pos3D(target.xPos, 0, target.zPos)) < Settings.MAX_MISSILE_DISTANCE / 10)
            {
                return false;
            }
        }
        else if (this.tier == 1)
        {
            if (new Pos3D(this.xCoord, 0, this.zCoord).distance(new Pos3D(target.xPos, 0, target.zPos)) < Settings.MAX_MISSILE_DISTANCE / 5)
            {
                return false;
            }
        }
        else if (this.tier == 2)
        {
            if (new Pos3D(this.xCoord, 0, this.zCoord).distance(new Pos3D(target.xPos, 0, target.zPos)) < Settings.MAX_MISSILE_DISTANCE)
            {
                return false;
            }
        }

        return true;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.tier = nbt.getInteger("tier");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("tier", this.tier);
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

    @Override
    public boolean onActivated(EntityPlayer player)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            if (player.inventory.getCurrentItem().getItem() instanceof ItemMissile)
            {
                if (this.getStackInSlot(0) == null)
                {

                    this.setInventorySlotContents(0, player.inventory.getCurrentItem());
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else
                {
                    ItemStack player_held = player.inventory.getCurrentItem();
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
                    this.setInventorySlotContents(0, player_held);
                    return true;
                }
            }
        }
        else if (this.getStackInSlot(0) != null)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
            this.setInventorySlotContents(0, null);
            return true;
        }

        return true;
    }

    @Override
    public void invalidate()
    {
        if (this.missile != null)
        {
            ((Entity) this.missile).setDead();
        }

        super.invalidate();
    }
    
    @Override
	public void onPlace()
	{
		Coord4D pos = Coord4D.get(this);
		
		for(Coord4D coord : getMultiBlockVectors())
		{
			MekanismUtils.makeBoundingBlock(worldObj, pos.clone().translate(coord), pos);
		}
	}

	@Override
	public void onBreak()
	{
		for(Coord4D coord : getMultiBlockVectors())
		{
			Coord4D translated = Coord4D.get(this).translate(coord);
			worldObj.setBlockToAir(translated.xCoord, translated.yCoord, translated.zCoord);
		}

		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

    public Coord4D[] getMultiBlockVectors()
    {
    	ForgeDirection facingDirection = ForgeDirection.getOrientation(facing);
    	
        if (facingDirection == ForgeDirection.SOUTH || facingDirection == ForgeDirection.NORTH)
        {
            return new Coord4D[] { new Coord4D(1, 0, 0), new Coord4D(1, 1, 0), new Coord4D(1, 2, 0), new Coord4D(-1, 0, 0), new Coord4D(-1, 1, 0), new Coord4D(-1, 2, 0) };
        }
        else
        {
            return new Coord4D[] { new Coord4D(0, 0, 1), new Coord4D(0, 1, 1), new Coord4D(0, 2, 1), new Coord4D(0, 0, -1), new Coord4D(0, 1, -1), new Coord4D(0, 2, -1) };
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == 0 && stack.getItem() instanceof ItemMissile;
    }

    @Override
    public IMissile getContainingMissile()
    {
        return this.missile;
    }

    @Override
    public void setContainingMissile(IMissile missile)
    {
        this.missile = missile;
    }

    @Override
    public ILauncherController getController()
    {
        for (byte i = 2; i < 6; i++)
        {
            Coord4D position = Coord4D.get(this).step(ForgeDirection.getOrientation(i));

            TileEntity tileEntity = position.getTileEntity(this.worldObj);

            if (tileEntity instanceof ILauncherController)
            {
                return (ILauncherController) tileEntity;
            }
        }

        return null;
    }

    @Override
    public int[] getMissileSlots()
    {
        return new int[] { 0 };
    }
}
