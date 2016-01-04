package icbm.explosion.items;

import icbm.Reference;
import icbm.TabICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.entities.EntityBombCart;
import icbm.explosion.entities.EntityExplosive;

import java.util.Random;

import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Explosive Defuser
public class ItemDefuser extends ItemEnergized
{
    private static final int ENERGY_USAGE = 2000;

    public ItemDefuser()
    {
        super(100000);
        
        this.setUnlocalizedName(Reference.PREFIX + "defuser");
        this.setCreativeTab(TabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + "defuser");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
       itemIcon = reg.registerIcon(getIconString());
    }

    /** Called when the player Left Clicks (attacks) an entity. Processed before damage is done, if
     * return value is true further processing is canceled and the entity is not attacked.
     * 
     * @param itemStack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction. */
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
    {
        System.out.println("stackCharge " + this.getEnergy(itemStack));
        if (this.getEnergy(itemStack) >= ENERGY_USAGE)
        {
            if (entity instanceof EntityExplosive)
            {
                if (!entity.worldObj.isRemote)
                {
                    EntityExplosive entityTNT = (EntityExplosive) entity;
                    EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ICBMExplosion.blockExplosive, 1, entityTNT.explosiveID));
                    float var13 = 0.05F;
                    Random random = new Random();
                    entityItem.motionX = ((float) random.nextGaussian() * var13);
                    entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
                    entityItem.motionZ = ((float) random.nextGaussian() * var13);
                    entity.worldObj.spawnEntityInWorld(entityItem);
                }
                entity.setDead();
            }
            else if (entity instanceof EntityTNTPrimed)
            {
                if (!entity.worldObj.isRemote)
                {
                    EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.tnt));
                    float var13 = 0.05F;
                    Random random = new Random();
                    entityItem.motionX = ((float) random.nextGaussian() * var13);
                    entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
                    entityItem.motionZ = ((float) random.nextGaussian() * var13);
                    entity.worldObj.spawnEntityInWorld(entityItem);
                }
                entity.setDead();
            }
            else if (entity instanceof EntityBombCart)
            {
                ((EntityBombCart) entity).killMinecart(DamageSource.generic);
            }

            this.setEnergy(itemStack, this.getEnergy(itemStack) - ENERGY_USAGE);
            return true;
        }
        else
        {
            player.addChatMessage(new ChatComponentText(LangUtils.localize("message.defuser.nopower")));
        }

        return false;
    }
}
