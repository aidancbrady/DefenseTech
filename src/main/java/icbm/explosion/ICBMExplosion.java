package icbm.explosion;

import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import icbm.core.ICBMCore;
import icbm.core.implement.IChunkLoadHandler;
import icbm.explosion.entities.EntityBombCart;
import icbm.explosion.entities.EntityExplosion;
import icbm.explosion.entities.EntityExplosive;
import icbm.explosion.entities.EntityGrenade;
import icbm.explosion.entities.EntityLightBeam;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.BlockExplosive;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.ExplosiveHelper;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.explosive.ItemBlockExplosive;
import icbm.explosion.items.ItemBombCart;
import icbm.explosion.items.ItemDefuser;
import icbm.explosion.items.ItemGrenade;
import icbm.explosion.items.ItemLaserDesignator;
import icbm.explosion.items.ItemMissile;
import icbm.explosion.items.ItemRadarGun;
import icbm.explosion.items.ItemRemoteDetonator;
import icbm.explosion.items.ItemRocketLauncher;
import icbm.explosion.machines.BlockICBMMachine;
import icbm.explosion.machines.BlockICBMMachine.MachineData;
import icbm.explosion.machines.ItemBlockMachine;
import icbm.explosion.potion.ContagiousPoison;
import icbm.explosion.potion.PoisonContagion;
import icbm.explosion.potion.PoisonFrostBite;
import icbm.explosion.potion.PoisonToxin;
import icbm.explosion.potion.PotionUtility;

import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.Tier.EnergyCubeTier;
import mekanism.common.item.ItemEnergized;
import mekanism.common.recipe.MekanismRecipe;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ICBMExplosion.ID, name = ICBMExplosion.NAME, version = Reference.VERSION, dependencies = "required-after:ICBM")
public class ICBMExplosion
{
    public static final String NAME = Reference.NAME + " Explosion";
    public static final String ID = Reference.NAME + "|Explosion";
    public static final String CHANNEL = Reference.NAME + "|E";

    @Instance(ID)
    public static ICBMExplosion instance;

    @Metadata(ID)
    public static ModMetadata metadata;

    @SidedProxy(clientSide = "icbm.explosion.ClientProxy", serverSide = "icbm.explosion.CommonProxy")
    public static CommonProxy proxy;
    public static Item Du;
    public static final int ENTITY_ID_PREFIX = 50;

    /** Settings and Configurations */
    // Blocks
    public static Block blockExplosive;
    public static Block blockMachine;
    
    // Items
    public static Item itemMissile;

    public static Item itemDefuser;
    public static Item itemRadarGun;
    public static Item itemRemoteDetonator;
    public static Item itemLaserDesignator;
    public static Item itemRocketLauncher;

    public static Item itemGrenade;
    public static Item itemBombCart;

    public static final ContagiousPoison poisonous_potion = new ContagiousPoison("Chemical", 1, false);
    public static final ContagiousPoison contagios_potion = new ContagiousPoison("Contagious", 1, true);

    @EventHandler
    // @Optional.Method(modid = ID)
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);

        blockExplosive = new BlockExplosive();
        blockMachine = new BlockICBMMachine();
        
        GameRegistry.registerBlock(blockExplosive, ItemBlockExplosive.class, "explosives");
        GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "machine");
        
        // ITEMS
        itemMissile = new ItemMissile();
        itemDefuser = new ItemDefuser();
        itemRadarGun = new ItemRadarGun();
        itemRemoteDetonator = new ItemRemoteDetonator();
        itemLaserDesignator = new ItemLaserDesignator();
        itemRocketLauncher = new ItemRocketLauncher();
        itemGrenade = new ItemGrenade();
        itemBombCart = new ItemBombCart();
        
        GameRegistry.registerItem(itemMissile, "missile");
        GameRegistry.registerItem(itemDefuser, "defuser");
        GameRegistry.registerItem(itemRadarGun, "radarGun");
        GameRegistry.registerItem(itemRemoteDetonator, "remoteDetonator");
        GameRegistry.registerItem(itemLaserDesignator, "laserDesignator");
        GameRegistry.registerItem(itemRocketLauncher, "rocketLauncher");
        GameRegistry.registerItem(itemGrenade, "grenade");
        GameRegistry.registerItem(itemBombCart, "minecart");

        /** Potion Effects */
        PoisonToxin.INSTANCE = new PoisonToxin(PotionUtility.getNextOptimalPotId(), true, 5149489, "toxin");
        PoisonContagion.INSTANCE = new PoisonContagion(PotionUtility.getNextOptimalPotId(), false, 5149489, "virus");
        PoisonFrostBite.INSTANCE = new PoisonFrostBite(PotionUtility.getNextOptimalPotId(), false, 5149489, "frostBite");
		if (!Loader.isModLoaded("ICBM|Sentry"))
			TabICBM.itemStack = new ItemStack(blockExplosive);

        /** Dispenser Handler */
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemGrenade, new IBehaviorDispenseItem()
        {
            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();
                    EnumFacing enumFacing = EnumFacing.getFront(blockSource.getBlockMetadata());

                    EntityGrenade entity = new EntityGrenade(world, new Pos3D(x, y, z), itemStack.getItemDamage());
                    entity.setThrowableHeading(enumFacing.getFrontOffsetX(), 0.1D, enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
                    world.spawnEntityInWorld(entity);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        BlockDispenser.dispenseBehaviorRegistry.putObject(itemBombCart, new IBehaviorDispenseItem()
        {
            private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
            {
                World world = blockSource.getWorld();

                if (!world.isRemote)
                {
                    int x = blockSource.getXInt();
                    int y = blockSource.getYInt();
                    int z = blockSource.getZInt();

                    EnumFacing var3 = EnumFacing.getFront(blockSource.getBlockMetadata());
                    World var4 = blockSource.getWorld();
                    double var5 = blockSource.getX() + var3.getFrontOffsetX() * 1.125F;
                    double var7 = blockSource.getY();
                    double var9 = blockSource.getZ() + var3.getFrontOffsetZ() * 1.125F;
                    int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
                    int var12 = blockSource.getYInt();
                    int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
                    Block var14 = var4.getBlock(var11, var12, var13);
                    double var15;

                    if (BlockRailBase.func_150051_a(var14))
                    {
                        var15 = 0.0D;
                    }
                    else
                    {
                        if (var14 != null || !BlockRailBase.func_150051_a(var4.getBlock(var11, var12 - 1, var13)))
                        {
                            return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack);
                        }

                        var15 = -1.0D;
                    }

                    EntityBombCart var22 = new EntityBombCart(world, var5, var7 + var15, var9, itemStack.getItemDamage());
                    world.spawnEntityInWorld(var22);
                    world.playAuxSFX(1000, x, y, z, 0);
                }

                itemStack.stackSize--;
                return itemStack;
            }
        });

        /** Chunk loading handler. */
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new LoadingCallback()
        {
            @Override
            public void ticketsLoaded(List<Ticket> tickets, World world)
            {
                for (Ticket ticket : tickets)
                {
                    if (ticket.getEntity() instanceof IChunkLoadHandler)
                    {
                        ((IChunkLoadHandler) ticket.getEntity()).chunkLoaderInit(ticket);
                    }
                    else
                    {
                        if (ticket.getModData() != null)
                        {
                            Coord4D position = Coord4D.read(ticket.getModData());

                            TileEntity tileEntity = position.getTileEntity(ticket.world);

                            if (tileEntity instanceof IChunkLoadHandler)
                            {
                                ((IChunkLoadHandler) tileEntity).chunkLoaderInit(ticket);
                            }
                        }
                    }
                }
            }
        });

        ExplosiveHelper.explosionManager = ExplosiveRegistry.class;

        proxy.preInit();
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void init(FMLInitializationEvent evt)
    {
        Settings.setModMetadata(ID, NAME, metadata, Reference.NAME);

        EntityRegistry.registerGlobalEntityID(EntityExplosive.class, "ICBMExplosive", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityMissile.class, "ICBMMissile", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityExplosion.class, "ICBMProceduralExplosion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityLightBeam.class, "ICBMLightBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "ICBMGrenade", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerGlobalEntityID(EntityBombCart.class, "ICBMChe", EntityRegistry.findGlobalUniqueEntityId());

        EntityRegistry.registerModEntity(EntityExplosive.class, "ICBMExplosive", ENTITY_ID_PREFIX, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityMissile.class, "ICBMMissile", ENTITY_ID_PREFIX + 1, this, 500, 1, true);
        EntityRegistry.registerModEntity(EntityExplosion.class, "ICBMProceduralExplosion", ENTITY_ID_PREFIX + 2, this, 100, 5, true);
        EntityRegistry.registerModEntity(EntityLightBeam.class, "ICBMLightBeam", ENTITY_ID_PREFIX + 4, this, 80, 5, true);
        EntityRegistry.registerModEntity(EntityGrenade.class, "ICBMGrenade", ENTITY_ID_PREFIX + 6, this, 50, 5, true);
        EntityRegistry.registerModEntity(EntityBombCart.class, "ICBMChe", ENTITY_ID_PREFIX + 8, this, 50, 4, true);

        proxy.init();
    }

    @EventHandler
    // @Optional.Method(modid = ID)
    public void postInit(FMLPostInitializationEvent event)
    {
        /** Add all Recipes */
        // Rocket Launcher
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(((ItemEnergized)itemRocketLauncher).getUnchargedItem(), new Object[] { "SCR", "SB ", 'R', ((ItemEnergized)itemRadarGun).getUnchargedItem(), 'C', new ItemStack(blockMachine, 1, MachineData.CruiseLauncher.ordinal() + 6), 'B', Blocks.stone_button, 'S', "ingotSteel" }));
        // Radar Gun
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(((ItemEnergized)itemRadarGun).getUnchargedItem(), new Object[] { "@#!", " $!", "  !", '@', "blockGlass", '!', "ingotSteel", '#', "circuitBasic", '$', Blocks.stone_button }));
        // Remote
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(((ItemEnergized)itemRemoteDetonator).getUnchargedItem(), new Object[] { "?@@", "@#$", "@@@", '@', "ingotSteel", '?', Items.redstone, '#', "circuitAdvanced", '$', Blocks.stone_button }));
        // Laser Designator
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(((ItemEnergized)itemLaserDesignator).getUnchargedItem(), new Object[] { "!  ", " ? ", "  @", '@', ((ItemEnergized)itemRemoteDetonator).getUnchargedItem(), '?', "circuitElite", '!', ((ItemEnergized)itemRadarGun).getUnchargedItem() }));
        // Defuser
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(((ItemEnergized)itemDefuser).getUnchargedItem(), new Object[] { "I  ", " W ", "  C", 'C', "circuitAdvanced", 'W', MekanismItems.Configurator.getUnchargedItem(), 'I', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        // Missile Launcher Platform
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 0), new Object[] { "! !", "!C!", "!!!", '!', "ingotBronze", 'C', "circuitBasic" }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 1), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 0), '!', "ingotSteel", 'C', "circuitAdvanced" }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 2), new Object[] { "! !", "!C!", "!@!", '@', new ItemStack(blockMachine, 1, 1), '!', "alloyAdvanced", 'C', "circuitElite" }));
        // Missile Launcher Panel
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 3), new Object[] { "!!!", "!#!", "!?!", '#', "circuitBasic", '!', "blockGlass", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 4), new Object[] { "!$!", "!#!", "!?!", '#', "circuitAdvanced", '!', "ingotSteel", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0), '$', new ItemStack(blockMachine, 1, 3) }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 5), new Object[] { "!$!", "!#!", "!?!", '#', "circuitElite", '!', "ingotGold", '?', new ItemStack(MekanismItems.PartTransmitter, 1, 0), '$', new ItemStack(blockMachine, 1, 4) }));
        // Missile Launcher Support Frame
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 6), new Object[] { "! !", "!!!", "! !", '!', "ingotBronze" }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 7), new Object[] { "! !", "!@!", "! !", '!', "ingotSteel", '@', new ItemStack(blockMachine, 1, 6) }));
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 8), new Object[] { "! !", "!@!", "! !", '!', "alloyAdvanced", '@', new ItemStack(blockMachine, 1, 7) }));
        // Radar Station
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 9), new Object[] { "?@?", " ! ", "!#!", '@', ((ItemEnergized)itemRadarGun).getUnchargedItem(), '!', "alloyAdvanced", '#', "circuitBasic", '?', "ingotGold" }));
        // EMP Tower
        GameRegistry.addRecipe(new MekanismRecipe(new ItemStack(blockMachine, 1, 10), new Object[] { "?W?", "@!@", "?#?", '?', "alloyAdvanced", '!', "circuitElite", '@', MekanismUtils.getEnergyCube(EnergyCubeTier.BASIC), '#', new ItemStack(MekanismBlocks.BasicBlock, 1, 8), 'W', new ItemStack(MekanismItems.PartTransmitter, 1, 0) }));
        // Cruise Launcher
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 11), new Object[] { "?! ", "@@@", '@', "alloyAdvanced", '!', new ItemStack(blockMachine, 1, 2), '?', new ItemStack(blockMachine, 1, 8) }));
        // Missile Coordinator
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(blockMachine, 1, 12), new Object[] { "R R", "SCS", "SSS", 'C', "circuitAdvanced", 'S', "alloyAdvanced", 'R', ((ItemEnergized)itemRemoteDetonator).getUnchargedItem() }));
        // Missile module
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new Object[] { " @ ", "@#@", "@?@", '@', "ingotSteel", '?', Items.flint_and_steel, '#', "circuitBasic" }));
        // Homing
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemMissile, 1, Explosive.homing.getID()), new Object[] { " B ", " C ", "BMB", 'M', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), 'C', "circuitBasic", 'B', "ingotBronze" }));
        // Anti-ballistic
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemMissile, 1, Explosive.antiBallistic.getID()), new Object[] { "!", "?", "@", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', new ItemStack(blockExplosive, 1, 0), '!', "circuitBasic" }));
        // Cluster
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemMissile, 1, Explosive.cluster.getID()), new Object[] { " ! ", " ? ", "!@!", '@', new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), '?', Explosive.fragmentation.getItemStack(), '!', new ItemStack(itemMissile, 1, 0) }));
        // Nuclear Cluster
        CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemMissile, 1, Explosive.nuclearCluster.getID()), new Object[] { " N ", "NCN", 'C', new ItemStack(itemMissile, 1, Explosive.cluster.getID()), 'N', Explosive.nuclear.getItemStack() }));

        // Add all explosive recipes.
        if (!Loader.isModLoaded("ResonantInduction|Atomic")) //TODO wither skull? what?
            OreDictionary.registerOre("strangeMatter", new ItemStack(Items.skull, 1, 1));

        for (Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            explosive.init();
            // Missile
            CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(new ItemStack(itemMissile, 1, explosive.getID()), new Object[] { new ItemStack(itemMissile, 1, Explosive.missileModule.getID()), new ItemStack(blockExplosive, 1, explosive.getID()) }));
            if (explosive.getTier() < 2)
            {
                // Grenade
            	CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemGrenade, 1, explosive.getID()), new Object[] { "?", "@", '@', new ItemStack(blockExplosive, 1, explosive.getID()), '?', Items.string }));
            }
            if (explosive.getTier() < 3)
            {
                // Minecart
            	CraftingManager.getInstance().getRecipeList().add(new MekanismRecipe(new ItemStack(itemBombCart, 1, explosive.getID()), new Object[] { "?", "@", '?', new ItemStack(blockExplosive, 1, explosive.getID()), '@', Items.minecart }));
            }
        }
    }

    @SubscribeEvent
    public void enteringChunk(EnteringChunk evt)
    {
        if (evt.entity instanceof EntityMissile)
        {
            ((EntityMissile) evt.entity).updateLoadChunk(evt.newChunkX, evt.newChunkZ);
        }
    }

    @SubscribeEvent
    public void creeperDeathEvent(LivingDeathEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            if (Settings.CREEPER_BLOW_UP_IN_FIRE)
            {
                if (evt.source == DamageSource.onFire || evt.source == DamageSource.inFire)
                {
                    evt.setCanceled(true);
                    boolean flag = evt.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

                    if (((EntityCreeper) evt.entityLiving).getPowered())
                    {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 6f, flag);
                    }
                    else
                    {
                        evt.entityLiving.worldObj.createExplosion(evt.entityLiving, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, 3f, flag);
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public void creeperDropEvent(LivingDropsEvent evt)
    {
        if (evt.entityLiving instanceof EntityCreeper)
        {
            if (Settings.CREEPER_DROP_SULFER)
            {
                evt.entityLiving.dropItem(ICBMCore.itemSulfurDust, 1 + evt.entityLiving.worldObj.rand.nextInt(6));
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandICBM());
    }
}
