package zairus.iextras.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.IEConstants;
import zairus.iextras.inventory.ContainerIExecutor;
import zairus.iextras.tileentity.TileEntityIEBase;

@SideOnly(Side.CLIENT)
public class GuiIExecutor extends GuiContainer
{
	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(IEConstants.MODID, "textures/gui/container/iexecutor.png");
	
	private IInventory inventory;
	
	public GuiIExecutor(IInventory playerInv, IInventory inventorySlots, EntityPlayer player)
	{
		super(new ContainerIExecutor(playerInv, inventorySlots, player));
		this.inventory = inventorySlots;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRendererObj.drawString(((TileEntityIEBase)inventory).getGUIDisplayName(), 7, 4, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		this.ySize = 166;
		int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
