package defense.explosion.machines;

import java.util.List;
import java.util.Random;

import mekanism.common.base.IBoundingBlock;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ITier;
import defense.core.DefenseTech;
import defense.core.DefenseTechBlocks;
import defense.core.DefenseTechItems;
import defense.core.IBlockActivate;
import defense.core.prefab.BlockBase;
import defense.explosion.machines.launcher.TileLauncherBase;
import defense.explosion.machines.launcher.TileLauncherFrame;
import defense.explosion.machines.launcher.TileLauncherScreen;
import defense.explosion.render.tile.BlockRenderHandler;

public class BlockMachine extends BlockBase
{
    public enum MachineData
    {
        LauncherBase("LauncherBase", TileLauncherBase.class, true),
        LauncherScreen("LauncherScreen", TileLauncherScreen.class, true),
        LauncherFrame("LauncherFrame", TileLauncherFrame.class, true),
        RadarStation("RadarStation", TileRadarStation.class, false),
        EmpTower("EmpTower", TileEMPTower.class, false),
        CruiseLauncher("CruiseLauncher", TileCruiseLauncher.class, false),
        MissileCoordinator("MissileCoordinator", TileMissileCoordinator.class, false);

        public String unlocalized;
        public Class<? extends TileEntity> tileEntity;
        public boolean hasTier;

        MachineData(String s, Class<? extends TileEntity> tileEntity, boolean tier)
        {
        	this.unlocalized = s;
            this.tileEntity = tileEntity;
            this.hasTier = tier;
        }

        public static MachineData get(int id)
        {
            if (id < MachineData.values().length && id >= 0)
            {
                return MachineData.values()[id];
            }

            return null;
        }
    }

    public BlockMachine()
    {
        super("machine", Material.iron);
    }

    /** Can this block provide power. Only wire currently seems to have this change based on its
     * state. */
    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		if(!world.isRemote)
		{
			if(tileEntity instanceof TileEntityBasicBlock)
			{
				((TileEntityBasicBlock)tileEntity).onAdded();
			}
		}
	}

    /** Called when the block is placed in the world. */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack)
    {
    	TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
		int side = MathHelper.floor_double((entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int height = Math.round(entityliving.rotationPitch);
		int change = 3;

		if(tileEntity == null)
		{
			return;
		}

		if(tileEntity.canSetFacing(0) && tileEntity.canSetFacing(1))
		{
			if(height >= 65)
			{
				change = 1;
			}
			else if(height <= -65)
			{
				change = 0;
			}
		}

		if(change != 0 && change != 1)
		{
			switch(side)
			{
				case 0: change = 2; break;
				case 1: change = 5; break;
				case 2: change = 3; break;
				case 3: change = 4; break;
			}
		}

		tileEntity.setFacing((short)change);
        
        if(tileEntity instanceof IBoundingBlock)
		{
			((IBoundingBlock)tileEntity).onPlace();
		}
    }

    /** Checks if the machine can be placed at the location */
    public static boolean canBePlacedAt(World world, int x, int y, int z, int m, int side)
    {
        ForgeDirection d = MekanismUtils.getRight(side);

        if (m == 0)
        {
            //Launcher Pad multi block placement check
            for (int yp = 0; yp <= 2; yp++)
            {
            	Block b = world.getBlock(x + d.offsetX, y + yp, z + d.offsetZ);
            	
                if (!b.isReplaceable(world, x + d.offsetX, y + yp, z + d.offsetZ))
                    return false;
                if (!b.isReplaceable(world, x - d.offsetX, y + yp, z - d.offsetZ))
                    return false;
            }
            
            return world.getBlock(x, y, z).isReplaceable(world, x, y, z);
        }
        else if (m == 2)
        {
            // Launcher Frame
            return world.getBlock(x, y - 1, z).getMaterial().isSolid() && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z);
        }
        else if (m == 4)
        {
            return world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
        }

        return world.getBlock(x, y - 1, z).getMaterial().isSolid();
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        int direction = 0;
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityBasicBlock)
        {
            direction = ((TileEntityBasicBlock) tileEntity).facing;
        }

        return canBePlacedAt(world, x, y, z, world.getBlockMetadata(x, y, z), direction);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if(tileEntity instanceof TileEntityBasicBlock)
			{
				((TileEntityBasicBlock)tileEntity).onNeighborChange(block);
			}
		}
    }

    /** Called when the block is right clicked by the player */
    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            if (player.inventory.getCurrentItem().getItem() == DefenseTechItems.itemLaserDesignator)
            {
                return false;
            }
            else if (player.inventory.getCurrentItem().getItem() == DefenseTechItems.itemRadarGun)
            {
                return false;
            }
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity != null)
        {
            if (tileEntity instanceof IBlockActivate)
            {
                return ((IBlockActivate) tileEntity).onActivated(player);
            }
        }

        return false;
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        return this.onMachineActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }
    
    @Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if(!player.capabilities.isCreativeMode && !world.isRemote && canHarvestBlock(player, world.getBlockMetadata(x, y, z)))
		{
			TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);

			float motion = 0.7F;
			double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

			EntityItem entityItem = new EntityItem(world, x + motionX, y + motionY, z + motionZ, getPickBlock(null, world, x, y, z, player));

			world.spawnEntityInWorld(entityItem);
		}

		return world.setBlockToAir(x, y, z);
	}

    /** If this block doesn't render as an ordinary block it will return False (examples: signs,
     * buttons, stairs, etc) */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity tileEntity = (TileEntity)world.getTileEntity(x, y, z);

		if(tileEntity instanceof IBoundingBlock)
		{
			((IBoundingBlock)tileEntity).onBreak();
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (MachineData.get(metadata) != null)
        {
            try
            {
                return MachineData.get(metadata).tileEntity.newInstance();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    /** Returns the quantity of items to drop on block destruction. */
    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /** The type of render function that is called for this block */
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return BlockRenderHandler.ID;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(MachineData data : MachineData.values())
        {
        	ItemStack stack = new ItemStack(this, 1, data.ordinal());
        	par3List.add(stack);
        	
        	if(data.hasTier)
        	{
        		ItemStack tier2 = stack.copy();
        		((ItemBlockMachine)tier2.getItem()).setTier(tier2, 1);
        		par3List.add(tier2);
        		
        		ItemStack tier3 = stack.copy();
        		((ItemBlockMachine)tier3.getItem()).setTier(tier3, 2);
        		par3List.add(tier3);
        	}
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, world.getBlockMetadata(x, y, z));
        
        if(tileEntity instanceof ITier)
        {
        	((ItemBlockMachine)stack.getItem()).setTier(stack, ((ITier)tileEntity).getTier());
        }
        
        return stack;
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    public static String getJiQiMing(TileEntity tileEntity)
    {
    	ItemStack stack = tileEntity.getBlockType().getPickBlock(null, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, null);
    	return stack.getDisplayName();
    }
}
