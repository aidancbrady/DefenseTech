package defense.core;

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
import defense.Reference;
import defense.Settings;
import defense.api.FrequencyGrid;
import defense.core.blocks.BlockSulfurOre;
import defense.core.blocks.OreGeneratorSulfur;
import defense.core.entity.EntityFlyingBlock;
import defense.core.entity.EntityFragments;
import defense.core.items.ItemAntidote;
import defense.core.items.ItemPoisonPowder;
import defense.core.items.ItemSignalDisrupter;
import defense.core.items.ItemSulfurDust;
import defense.core.items.ItemTracker;
import defense.core.network.PacketItem;
import defense.core.network.PacketItem.ItemMessage;
import defense.explosion.potion.PotionUtility;

@Mod(modid = Reference.NAME, name = Reference.NAME, version = Reference.VERSION, dependencies = "required-after:Mekanism")
public final class CoreModule
{
    @Instance(Reference.NAME)
    public static CoreModule INSTANCE;

    @Metadata(Reference.NAME)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = Reference.DOMAIN + ".core.ClientProxy", serverSide = Reference.DOMAIN + ".core.CommonProxy")
    public static CommonProxy proxy;

    // Items
    public static Item itemAntidote;
    public static Item itemSignalDisrupter;
    public static Item itemTracker;

    public static Block blockSulfurOre, blockRadioactive;

    public static Item itemSulfurDust, itemPoisonPowder;

    public static OreGeneratorSulfur sulfurGenerator;

    public static final Logger LOGGER = Logger.getLogger(Reference.NAME);
    
    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);

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
        
        GameRegistry.registerItem(itemPoisonPowder, "poisonPowder");
        GameRegistry.registerItem(itemSulfurDust, "sulfur");
        GameRegistry.registerItem(itemAntidote, "antidote");
        GameRegistry.registerItem(itemSignalDisrupter, "signalDisrupter");
        GameRegistry.registerItem(itemTracker, "tracker");
        
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
        GameRegistry.registerWorldGenerator(sulfurGenerator, 0);

        EntityRegistry.registerGlobalEntityID(EntityFlyingBlock.class, Reference.ENTITY_PREFIX + "GravityBlock", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityFragments.class, Reference.ENTITY_PREFIX + "Fragments", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityFlyingBlock.class, Reference.ENTITY_PREFIX + "GravityBlock", 0, this, 50, 15, true);
        EntityRegistry.registerModEntity(EntityFragments.class, Reference.ENTITY_PREFIX + "Fragments", 1, this, 40, 8, true);
        
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