package icbm.core.gui;

import icbm.Reference;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.tile.TileEntityContainerBlock;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public abstract class GuiICBM extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

    protected int containerWidth;
    protected int containerHeight;

    private TileEntity tileEntity;

    public GuiICBM()
    {
        super(new ContainerNull());
    }

    public GuiICBM(TileEntityContainerBlock tileEntity)
    {
        super(new ContainerNull(tileEntity));
        this.tileEntity = tileEntity;
    }

    // TODO: Fix this.
    /** <pre>
     * &#064;Override
     * public void initGui()
     * {
     *     super.initGui();
     *     if (this.tileEntity != null)
     *         PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, -1, true));
     * }
     * 
     * &#064;Override
     * public void onGuiClosed()
     * {
     *     super.onGuiClosed();
     *     if (this.tileEntity != null)
     *         PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, -1, false));
     * }
     * </pre> */

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.containerWidth = (this.width - this.xSize) / 2;
        this.containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }

}
