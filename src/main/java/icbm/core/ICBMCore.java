package icbm.core;

import icbm.Reference;
import icbm.Settings;
import icbm.api.FrequencyGrid;
import icbm.core.blocks.BlockSulfurOre;
import icbm.core.blocks.OreGeneratorSulfur;
import icbm.core.entity.EntityFlyingBlock;
import icbm.core.entity.EntityFragments;
import icbm.core.items.ItemAntidote;
import icbm.core.items.ItemComputer;
import icbm.core.items.ItemPoisonPowder;
import icbm.core.items.ItemSignalDisrupter;
import icbm.core.items.ItemSulfurDust;
import icbm.core.items.ItemTracker;
import icbm.core.network.PacketItem;
import icbm.core.network.PacketItem.ItemMessage;
import icbm.explosion.potion.PotionUtility;

import java.util.ArrayList;
import java.util.logging.Logger;

import mekanism.common.MekanismItems;
import mekanism.common.item.ItemEnergized;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

/** Main class for ICBM core to run on. The core will need to be initialized by each ICBM module.
 * 
 * @author Calclavia */
@Mod(modid = Reference.NAME, name = Reference.NAME, version = Reference.VERSION, dependencies = "required-after:Mekanism")
public final class ICBMCore
{
    @Instance(Reference.NAME)
    public static ICBMCore INSTANCE;

    @Metadata(Reference.NAME)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "icbm.core.ClientProxy", serverSide = "icbm.core.CommonProxy")
    public static CommonProxy proxy;

    // Items
    public static Item itemAntidote;
    public static Item itemSignalDisrupter;
    public static Item itemTracker;
    public static Item itemHackingComputer;

    public static Block blockSulfurOre, blockRadioactive;

    public static Item itemSulfurDust, itemPoisonPowder;

    public static OreGeneratorSulfur sulfurGenerator;

    public static final Logger LOGGER = Logger.getLogger(Reference.NAME);
    
    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("ICBM");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        MinecraftForge.EVENT_BUS.register(INSTANCE);

        Settings.CONFIGURATION.load();

        // Blocks       
        blockSulfurOre = new BlockSulfurOre();
        
        GameRegistry.registerBlock(blockSulfurOre, "oreSulfur");

        // ITEMS
        itemPoisonPowder = new ItemPoisonPowder();
        itemSulfurDust = new ItemSulfurDust();
        itemAntidote = new ItemAntidote();
        itemSignalDisrupter = new ItemSignalDisrupter();
        itemTracker = new ItemTracker();
        itemHackingComputer = new ItemComputer();
        
        GameRegistry.registerItem(itemPoisonPowder, "poisonPowder");
        GameRegistry.registerItem(itemSulfurDust, "sulfur");
        GameRegistry.registerItem(itemAntidote, "antidote");
        GameRegistry.registerItem(itemSignalDisrupter, "signalDisrupter");
        GameRegistry.registerItem(itemTracker, "tracker");
        GameRegistry.registerItem(itemHackingComputer, "hackingComputer");
        
        PotionUtility.resizePotionArray();

        sulfurGenerator = new OreGeneratorSulfur(new ItemStack(blockSulfurOre), Blocks.air, 0, 40, 20, 4);

        /** Check for existence of radioactive Blocks. If it does not exist, then create it. */
        if (OreDictionary.getOres("blockRadioactive").size() > 0)
        {
            blockRadioactive = Block.getBlockFromItem(OreDictionary.getOres("blockRadioactive").get(0).getItem());
            LOGGER.fine("Detected radioative block from another mod, utilizing it.");
        }
        else
        {
            blockRadioactive = Blocks.mycelium;
        }

        /** Decrease Obsidian Resistance */
        Blocks.obsidian.setResistance(Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Reduce Obsidian Resistance", 45).getInt(45));
        LOGGER.fine("Changed obsidian explosive resistance to: " + Blocks.obsidian.getExplosionResistance(null));

        OreDictionary.registerOre("dustSulfur", new ItemStack(itemSulfurDust, 1, 0));
        OreDictionary.registerOre("dustSaltpeter", new ItemStack(itemSulfurDust, 1, 1));
        OreDictionary.registerOre("oreSulfur", new ItemStack(blockSulfurOre));

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Settings.setModMetadata(Reference.NAME, Reference.NAME, metadata);
        
        GameRegistry.registerWorldGenerator(sulfurGenerator, 0);

        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, "ICBMGravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, "ICBMFragment", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityFlyingBlock.class, "ICBMGravityBlock", 0, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityFragments.class, "ICBMFragment", 1, this, 40, 8, true);
        
        netHandler.registerMessage(PacketItem.class, ItemMessage.class, 0, Side.SERVER);

        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        /** LOAD. */
        ArrayList dustCharcoal = OreDictionary.getOres("dustCharcoal");
        ArrayList dustCoal = OreDictionary.getOres("dustCoal");
        // Sulfur
        GameRegistry.addSmelting(blockSulfurOre, new ItemStack(itemSulfurDust, 4), 0.8f);
        GameRegistry.addSmelting(Items.reeds, new ItemStack(itemSulfurDust, 4, 1), 0f);
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", Items.coal }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", new ItemStack(Items.coal, 1, 1) }));

        if (dustCharcoal != null && dustCharcoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", "dustCharcoal" }));
        if (dustCoal != null && dustCoal.size() > 0)
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), new Object[] { "dustSulfur", "dustSaltpeter", "dustCoal" }));

        GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.tnt, new Object[] { "@@@", "@R@", "@@@", '@', Items.gunpowder, 'R', Items.redstone }));

        // Poison Powder
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemPoisonPowder, 3), new Object[] { Items.spider_eye, Items.rotten_flesh }));
        /** Add all Recipes */
        // Tracker
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)itemTracker).getUnchargedItem(), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', "circuitBasic", 'B', MekanismItems.EnergyTablet.getUnchargedItem(), 'S', Items.iron_ingot }));
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)itemTracker).getUnchargedItem(), new Object[] { " Z ", "SBS", "SCS", 'Z', Items.compass, 'C', "circuitBasic", 'B', Items.ender_pearl, 'S', Items.iron_ingot }));
        // Signal Disrupter
        GameRegistry.addRecipe(new ShapedOreRecipe(((ItemEnergized)itemSignalDisrupter).getUnchargedItem(), new Object[] { "WWW", "SCS", "SSS", 'S', Items.iron_ingot, 'C', "circuitBasic", 'W', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));

        // Antidote
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote, 6), new Object[] { "@@@", "@@@", "@@@", '@', Items.pumpkin_seeds }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAntidote), new Object[] { "@@@", "@@@", "@@@", '@', Items.wheat_seeds }));
    }

    @EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{
    	FrequencyGrid.reinitiate();
	}
}