package defense.core.blocks;

import java.util.Random;

import mekanism.api.Coord4D;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.IWorldGenerator;
import defense.Settings;

/** @author CovertJaguar, Modified by Calclavia */

public class OreGeneratorSulfur implements IWorldGenerator
{
	public int minGenerateLevel;
    public int maxGenerateLevel;
    public int amountPerChunk;
    public int amountPerBranch;
    public ItemStack oreStack;
    
    public Block oreBlock;
    public int oreMeta;
    
    public OreGeneratorSulfur(ItemStack stack, Block replaceBlock, int minGenerateLevel, int maxGenerateLevel, int amountPerChunk, int amountPerBranch)
    {
    	this.minGenerateLevel = minGenerateLevel;
        this.maxGenerateLevel = maxGenerateLevel;
        this.amountPerChunk = amountPerChunk;
        this.amountPerBranch = amountPerBranch;
        this.oreStack = stack;
        
        this.oreBlock = Block.getBlockFromItem(oreStack.getItem());
        this.oreMeta = oreStack.getItemDamage();
    }

    @Override
    public void generate(Random random, int varX, int varZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
    	if(Settings.GENERATE_SULFUR)
    	{
	        for (int y = this.minGenerateLevel; y < this.maxGenerateLevel; y++)
	        {
	            for (int x = 0; x < 16; x++)
	            {
	                for (int z = 0; z < 16; z++)
	                {
	                    this.generateReplace(world, random, varX + x, y, varZ + z);
	                }
	            }
	        }
    	}
    }

    public boolean generateReplace(World world, Random rand, int x, int y, int z)
    {
        if (nearLava(world, x, y, z))
        {
            placeOre(world, rand, x, y, z);
            return true;
        }

        return false;
    }

    private void placeOre(World world, Random rand, int x, int y, int z)
    {
        Coord4D position = new Coord4D(x, y, z);

        for (int amount = 0; amount < this.amountPerBranch; amount++)
        {
            Block block = world.getBlock(x, y, z);

            if (!world.isAirBlock(x, y, z) && block.isReplaceableOreGen(world, x, y, z, Blocks.stone))
            {
                world.setBlock(x, y, z, this.oreBlock, this.oreMeta, 2);
            }

            ForgeDirection dir = ForgeDirection.values()[rand.nextInt(6)];

            position.step(dir);
        }
    }

    private boolean nearLava(World world, int x, int y, int z)
    {
        for (int side = 2; side < 6; side++)
        {
            Coord4D position = new Coord4D(x, y, z);

            ForgeDirection s = ForgeDirection.values()[side];

            position.step(s);

            if (world.blockExists(position.xCoord, position.yCoord, position.zCoord))
            {
                Block block = position.getBlock(world);

                if (block == Blocks.lava || block == Blocks.flowing_lava)
                {
                    return true;
                }
            }
        }

        for (int j = 0; j < 4; j++)
        {
            Block block = world.getBlock(x, y - j, z);

            if (block == Blocks.lava || block == Blocks.flowing_lava)
            {
                return true;
            }
            else if (!world.isAirBlock(x, y - j, z))
            {
                return false;
            }
        }

        return false;
    }
}