package defense.core;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import defense.Reference;
import defense.core.items.ItemAntidote;
import defense.core.items.ItemPoisonPowder;
import defense.core.items.ItemSignalDisrupter;
import defense.core.items.ItemSulfurDust;
import defense.core.items.ItemTracker;
import defense.explosion.items.ItemBombCart;
import defense.explosion.items.ItemDefuser;
import defense.explosion.items.ItemGrenade;
import defense.explosion.items.ItemLaserDesignator;
import defense.explosion.items.ItemMissile;
import defense.explosion.items.ItemRadarGun;
import defense.explosion.items.ItemRemoteDetonator;
import defense.explosion.items.ItemRocketLauncher;

@ObjectHolder(Reference.NAME)
public class DefenseTechItems 
{
    public static Item itemAntidote = new ItemAntidote();
    public static Item itemSignalDisrupter = new ItemSignalDisrupter();
    public static Item itemTracker = new ItemTracker();

    public static Item itemSulfurDust = new ItemSulfurDust();
    public static Item itemPoisonPowder = new ItemPoisonPowder();
    
    public static Item itemMissile = new ItemMissile();
    public static Item itemDefuser = new ItemDefuser();
    public static Item itemRadarGun = new ItemRadarGun();
    public static Item itemRemoteDetonator = new ItemRemoteDetonator();
    public static Item itemLaserDesignator = new ItemLaserDesignator();
    public static Item itemRocketLauncher = new ItemRocketLauncher();
    public static Item itemGrenade = new ItemGrenade();
    public static Item itemBombCart = new ItemBombCart();
    
	public static void register()
	{
		GameRegistry.registerItem(itemPoisonPowder, "poisonPowder");
        GameRegistry.registerItem(itemSulfurDust, "sulfur");
        GameRegistry.registerItem(itemAntidote, "antidote");
        GameRegistry.registerItem(itemSignalDisrupter, "signalDisrupter");
        GameRegistry.registerItem(itemTracker, "tracker");
        
        GameRegistry.registerItem(itemMissile, "missile");
        GameRegistry.registerItem(itemDefuser, "defuser");
        GameRegistry.registerItem(itemRadarGun, "radarGun");
        GameRegistry.registerItem(itemRemoteDetonator, "remoteDetonator");
        GameRegistry.registerItem(itemLaserDesignator, "laserDesignator");
        GameRegistry.registerItem(itemRocketLauncher, "rocketLauncher");
        GameRegistry.registerItem(itemGrenade, "grenade");
        GameRegistry.registerItem(itemBombCart, "minecart");
	}
}
