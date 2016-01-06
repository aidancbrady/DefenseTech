package defense.explosion;

import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.core.CoreModule;
import defense.core.RenderUtils;
import defense.explosion.entities.EntityBombCart;
import defense.explosion.entities.EntityExplosion;
import defense.explosion.entities.EntityExplosive;
import defense.explosion.entities.EntityGrenade;
import defense.explosion.entities.EntityLightBeam;
import defense.explosion.entities.EntityMissile;
import defense.explosion.ex.missiles.MissileMovingSound;
import defense.explosion.explosive.TileExplosive;
import defense.explosion.fx.FXAntimatterPartical;
import defense.explosion.fx.FXElectricBolt;
import defense.explosion.fx.FXElectricBoltSpawner;
import defense.explosion.fx.FXEnderPortalPartical;
import defense.explosion.fx.FXShockWave;
import defense.explosion.fx.FXSmoke;
import defense.explosion.gui.GuiCruiseLauncher;
import defense.explosion.gui.GuiEMPTower;
import defense.explosion.gui.GuiLauncherScreen;
import defense.explosion.gui.GuiMissileCoordinator;
import defense.explosion.gui.GuiRadarStation;
import defense.explosion.machines.TileCruiseLauncher;
import defense.explosion.machines.TileEMPTower;
import defense.explosion.machines.TileMissileCoordinator;
import defense.explosion.machines.TileRadarStation;
import defense.explosion.machines.launcher.TileLauncherBase;
import defense.explosion.machines.launcher.TileLauncherFrame;
import defense.explosion.machines.launcher.TileLauncherScreen;
import defense.explosion.potion.PoisonFrostBite;
import defense.explosion.render.entity.RenderEntityExplosive;
import defense.explosion.render.entity.RenderExplosion;
import defense.explosion.render.entity.RenderGrenade;
import defense.explosion.render.entity.RenderLightBeam;
import defense.explosion.render.entity.RenderMissile;
import defense.explosion.render.item.RenderItemMachine;
import defense.explosion.render.item.RenderItemMissile;
import defense.explosion.render.item.RenderRocketLauncher;
import defense.explosion.render.tile.BlockRenderHandler;
import defense.explosion.render.tile.RenderBombBlock;
import defense.explosion.render.tile.RenderCruiseLauncher;
import defense.explosion.render.tile.RenderEmpTower;
import defense.explosion.render.tile.RenderLauncherBase;
import defense.explosion.render.tile.RenderLauncherFrame;
import defense.explosion.render.tile.RenderLauncherScreen;
import defense.explosion.render.tile.RenderMissileCoordinator;
import defense.explosion.render.tile.RenderRadarStation;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private boolean disableReflectionFX = false;

    @Override
    public void preInit()
    {
    	FMLCommonHandler.instance().bus().register(new TickHandler());
    }

    @Override
    public void init()
    {
        super.init();

        MinecraftForgeClient.registerItemRenderer(ExplosionModule.itemRocketLauncher, new RenderRocketLauncher());
        MinecraftForgeClient.registerItemRenderer(ExplosionModule.itemMissile, new RenderItemMissile());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ExplosionModule.blockMachine), new RenderItemMachine());

        RenderingRegistry.registerBlockHandler(new RenderBombBlock());
        RenderingRegistry.registerBlockHandler(new BlockRenderHandler());

        RenderingRegistry.registerEntityRenderingHandler(EntityExplosive.class, new RenderEntityExplosive());
        RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, new RenderMissile(0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityExplosion.class, new RenderExplosion());
        RenderingRegistry.registerEntityRenderingHandler(EntityLightBeam.class, new RenderLightBeam());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityBombCart.class, new RenderMinecart());

        ClientRegistry.bindTileEntitySpecialRenderer(TileCruiseLauncher.class, new RenderCruiseLauncher());
        ClientRegistry.bindTileEntitySpecialRenderer(TileLauncherBase.class, new RenderLauncherBase());
        ClientRegistry.bindTileEntitySpecialRenderer(TileLauncherScreen.class, new RenderLauncherScreen());
        ClientRegistry.bindTileEntitySpecialRenderer(TileLauncherFrame.class, new RenderLauncherFrame());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRadarStation.class, new RenderRadarStation());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEMPTower.class, new RenderEmpTower());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMissileCoordinator.class, new RenderMissileCoordinator());
        ClientRegistry.bindTileEntitySpecialRenderer(TileExplosive.class, new RenderBombBlock());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileCruiseLauncher)
        {
            return new GuiCruiseLauncher(entityPlayer.inventory, (TileCruiseLauncher) tileEntity);
        }
        else if (tileEntity instanceof TileLauncherScreen)
        {
            return new GuiLauncherScreen(((TileLauncherScreen) tileEntity));
        }
        else if (tileEntity instanceof TileRadarStation)
        {
            return new GuiRadarStation(((TileRadarStation) tileEntity));
        }
        else if (tileEntity instanceof TileEMPTower)
        {
            return new GuiEMPTower((TileEMPTower) tileEntity);
        }
        else if (tileEntity instanceof TileMissileCoordinator)
        {
            return new GuiMissileCoordinator(entityPlayer.inventory, (TileMissileCoordinator) tileEntity);
        }

        return null;
    }

    @Override
    public boolean isGaoQing()
    {
        return Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }

    @Override
    public int getParticleSetting()
    {
        return Minecraft.getMinecraft().gameSettings.particleSetting;
    }

    @Override
    public void spawnParticle(String name, World world, Pos3D position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
    {
        EntityFX fx = null;

        if (name.equals("smoke"))
        {
            fx = new FXSmoke(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("missile_smoke"))
        {
            fx = (new FXSmoke(world, position, red, green, blue, scale, distance)).setAge(100);
        }
        else if (name.equals("portal"))
        {
            fx = new FXEnderPortalPartical(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("antimatter"))
        {
            fx = new FXAntimatterPartical(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("digging"))
        {
            fx = new EntityDiggingFX(world, position.xPos, position.yPos, position.zPos, motionX, motionY, motionZ, Block.getBlockById((int)red), 0, (int) green);
            fx.multipleParticleScaleBy(blue);
        }
        else if (name.equals("shockwave"))
        {
            fx = new FXShockWave(world, position, red, green, blue, scale, distance);
        }

        if (fx != null)
        {
            fx.motionX = motionX;
            fx.motionY = motionY;
            fx.motionZ = motionZ;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

    @Override
    public void spawnShock(World world, Pos3D startVec, Pos3D targetVec)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXElectricBolt(world, startVec, targetVec, 0));
    }

    @Override
    public void spawnShock(World world, Pos3D startVec, Pos3D targetVec, int duration)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXElectricBoltSpawner(world, startVec, targetVec, 0, duration));
    }
    
    @Override
    public void playSound(EntityMissile missile)
    {
    	FMLClientHandler.instance().getClient().getSoundHandler().playSound(new MissileMovingSound(missile));
    }

    @Override
    public List<Entity> getEntityFXs()
    {
        if (!this.disableReflectionFX)
        {
            try
            {
                EffectRenderer renderer = Minecraft.getMinecraft().effectRenderer;
                List[] fxLayers = (List[]) ReflectionHelper.getPrivateValue(EffectRenderer.class, renderer, 2);

                return fxLayers[0];
            }
            catch (Exception e)
            {
                CoreModule.LOGGER.severe("Failed to use relfection on entity effects.");
                e.printStackTrace();
                this.disableReflectionFX = true;
            }
        }
        return null;
    }

    // TODO: Work on this!
    // @ForgeSubscribe
    public void renderingLivingEvent(Pre evt)
    {
        if (evt.entity instanceof EntityLivingBase)
        {
            if (evt.entity.getActivePotionEffect(PoisonFrostBite.INSTANCE) != null)
            {
                try
                {
                    ModelBase modelBase = (ModelBase) ReflectionHelper.getPrivateValue(RendererLivingEntity.class, evt.renderer, 2);

                    if (modelBase != null)
                    {
                        if (evt.entity.isInvisible())
                        {
                            GL11.glDepthMask(false);
                        }
                        else
                        {
                            GL11.glDepthMask(true);
                        }

                        float f1 = evt.entity.ticksExisted;
                        // this.bindTexture(evt.renderer.func_110829_a);
                        RenderUtils.setTerrainTexture();
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        float f2 = f1 * 0.01F;
                        float f3 = f1 * 0.01F;
                        GL11.glTranslatef(f2, f3, 0.0F);
                        GL11.glScalef(2, 2, 2);
                        evt.renderer.setRenderPassModel(modelBase);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_BLEND);
                        float f4 = 0.5F;
                        GL11.glColor4f(f4, f4, f4, 1.0F);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                        modelBase.render(evt.entity, (float) evt.entity.posX, (float) evt.entity.posY, (float) evt.entity.posZ, evt.entity.rotationPitch, evt.entity.rotationYaw, 0.0625F);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
                catch (Exception e)
                {
                    CoreModule.LOGGER.severe("Failed to render entity layer object");
                    e.printStackTrace();
                }
            }
        }
    }
}
