package defense.common;

import mekanism.common.Tier.BaseTier;
import net.minecraft.item.ItemStack;
import defense.common.item.ItemBlockMachine;

public final class DefenseUtils 
{
	public static ItemStack getLauncherBase(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 0);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
	
	public static ItemStack getLauncherScreen(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 1);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
	
	public static ItemStack getLauncherFrame(BaseTier tier)
	{
		ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, 2);
		ItemBlockMachine itemMachine = (ItemBlockMachine)stack.getItem();
		itemMachine.setBaseTier(stack, tier);
		
		return stack;
	}
}
