package zairus.iextras.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.IEConstants;
import zairus.iextras.IExtras;
import zairus.iextras.inventory.ContainerIExecutor;
import zairus.iextras.tileentity.TileEntityIEBase;
import zairus.iextras.tileentity.TileEntityIExecutor;
import zairus.iextras.util.network.IExecutorPacket;

@SideOnly(Side.CLIENT)
public class GuiIExecutor extends GuiContainer
{
	public static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(IEConstants.MODID, "textures/gui/container/iexecutor.png");
	
	private IInventory inventory;
	
	private GuiExecutorConfigureButton buttonMode;
	private GuiExecutorConfigureButton buttonAction;
	
	public GuiIExecutor(IInventory playerInv, IInventory inventorySlots, EntityPlayer player)
	{
		super(new ContainerIExecutor(playerInv, inventorySlots, player));
		this.inventory = inventorySlots;
	}
	
	@Override
	public void initGui()
	{
		this.buttonList.add(buttonMode = new GuiExecutorConfigureButton(0, 0, 4, 10, 10, 14, 13, ""));
		this.buttonList.add(buttonAction = new GuiExecutorConfigureButton(1, 1, 2, 10, 25, 14, 13, ""));
		
		TileEntityIExecutor e = (TileEntityIExecutor)this.inventory;
		
		this.buttonMode.curStep = e.getInventoryMode();
		this.buttonAction.curStep = e.getUseAction();
		
		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button instanceof GuiExecutorConfigureButton)
		{
			GuiExecutorConfigureButton b = (GuiExecutorConfigureButton)button;
			
			if (b.enabled)
			{
				int mode = (b.curStep + 1) % b.maxStep;
				b.curStep = mode;
				
				updateButtons();
				syncTE();
			}
		}
	}
	
	private void updateButtons()
	{
		;
	}
	
	private void syncTE()
	{
		TileEntityIExecutor e = (TileEntityIExecutor)this.inventory;
		
		e.configureWork(this.buttonMode.curStep, this.buttonAction.curStep);
		IExtras.packetPipeline.sendToServer(new IExecutorPacket(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), this.buttonMode.curStep, this.buttonAction.curStep));
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
        
        this.setButtonPositions(i, j);
        
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
	
	private void setButtonPositions(int left, int top)
	{
		this.buttonMode.setScreenPos(left + 5, top + 15);
		this.buttonAction.setScreenPos(left + 5, top + 30);
	}
	
	public static class GuiExecutorConfigureButton extends GuiButton
	{
		private final int type;
		public final int maxStep;
		public int curStep = 0;
		
		public GuiExecutorConfigureButton(int buttonId, int buttonType, int maxStep, int x, int y, int width, int height, String buttonText)
		{
			super(buttonId, x, y, width, height, buttonText);
			this.type = buttonType;
			this.maxStep = maxStep;
		}
		
		@Override
		public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				minecraft.getTextureManager().bindTexture(GuiIExecutor.GUI_BACKGROUND);
				
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 + ((this.curStep % this.maxStep) * 14), 166 + (13 * this.type), 14, 13);
			}
		}
		
		public void setScreenPos(int x, int y)
		{
			this.xPosition = x;
			this.yPosition = y;
		}
	}
}
