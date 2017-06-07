package zairus.iextras.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
		
		List<String> bTooltip;
		
		this.buttonMode.curStep = e.getInventoryMode();
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Allow only input.");
		this.buttonMode.tooltip.add(bTooltip);
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Allow only output.");
		this.buttonMode.tooltip.add(bTooltip);
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Allow in/out.");
		this.buttonMode.tooltip.add(bTooltip);
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Block input/output.");
		this.buttonMode.tooltip.add(bTooltip);
		
		this.buttonAction.curStep = e.getUseAction();
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Left click.");
		this.buttonAction.tooltip.add(bTooltip);
		
		bTooltip = new ArrayList<String>();
		bTooltip.add("Right click.");
		this.buttonAction.tooltip.add(bTooltip);
		
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
		
		int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
		
		for (GuiButton b : this.buttonList)
		{
        	if (mouseX >= b.xPosition && mouseX <= (b.xPosition + b.width) && mouseY >= b.yPosition && mouseY <= (b.yPosition + b.height))
        	{
        		if (b instanceof GuiExecutorConfigureButton)
    			{
    				GuiExecutorConfigureButton eb = (GuiExecutorConfigureButton)b;
    				
    				if (eb.getStepTooltip() != null)
    					this.drawHoveringText(eb.getStepTooltip(), mouseX - i, mouseY - j);
    			}
        	}
		}
		
		if (mouseX >= (i + 156) && mouseX <= (i + 156 + 12) && mouseY >= (j + 6) && mouseY <= (j + 6 + 72))
        {
			TileEntityIExecutor e = (TileEntityIExecutor)this.inventory;
			
			List<String> energyTooltip = new ArrayList<String>();
			energyTooltip.add("RF: " + e.getEnergyStored());
			
        	this.drawHoveringText(energyTooltip, mouseX - i, mouseY - j + 10);
        }
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
        
        // Energy
        // Bar BG
        this.drawTexturedModalRect(i + 156, j + 6, 176, 0, 12, 72);
        
        // max: 75 - 68
        // min: 75 - 0
        // 34 steps
        
        TileEntityIExecutor e = (TileEntityIExecutor)this.inventory;
        int eBars = (int)(((float)e.getEnergyStored() / (float)e.getMaxEnergyStored()) * 34.0F);
        
        for (int ib = 0; ib <= eBars; ++ib)
        {
        	this.drawTexturedModalRect(i + 158, j + (75 - (ib * 2)), 188, 0, 8, 2);
        }
        
        // Bar Energy :: 68
        /*
        this.drawTexturedModalRect(i + 158, j + 75, 188, 0, 8, 2);
        this.drawTexturedModalRect(i + 158, j + 73, 188, 0, 8, 2);
        this.drawTexturedModalRect(i + 158, j + 71, 188, 0, 8, 2);
        this.drawTexturedModalRect(i + 158, j + 7, 188, 0, 8, 2);
        */
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
		
		public List<List<String>> tooltip = new ArrayList<List<String>>();
		
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
		
		@Nullable
		public List<String> getStepTooltip()
		{
			if (this.curStep < this.tooltip.size())
			{
				return this.tooltip.get(curStep);
			}
			
			return null;
		}
	}
}
