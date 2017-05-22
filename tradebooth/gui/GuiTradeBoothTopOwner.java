package tradebooth.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import tradebooth.CommonProxy;
import tradebooth.container.ContainerTradeBoothTopOwner;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTradeBoothTopOwner extends GuiContainer{

	private TileEntityTradeBoothTop tileEntity;
	private List woolSlotToolTipList = new ArrayList();
	
	public GuiTradeBoothTopOwner( InventoryPlayer inventoryPlayer, TileEntityTradeBoothTop tileEntity ){
		super( new ContainerTradeBoothTopOwner( inventoryPlayer, tileEntity ) );
		this.tileEntity = tileEntity;
		woolSlotToolTipList.add( "Wool Block Only" );
	}
	public String getOwnerName(){
		if( this.tileEntity != null ){
			return this.tileEntity.getPlayerOwner();
		}
		return "";
	}
	@Override
	protected void drawGuiContainerForegroundLayer( int par1, int par2 ){
		fontRendererObj.drawString( this.getOwnerName() + "'s Booth", 8, -26, 4210752 );
		fontRendererObj.drawString( "Price", 12, 12, 4210752 );
		fontRendererObj.drawString( "Price", 102, 12, 4210752 );
		int mouseXModulo = ( par1 - guiLeft - 25 ) % 72;
		int mouseYModulo = ( par2 - guiTop - 8 ) % 21;
		if( tileEntity.getStackInSlot( ContainerTradeBoothTopOwner.WOOL_SLOT_INDEX ) == null && par1 > ( 6 + this.guiLeft ) && par1 <= ( 24 + this.guiLeft ) &&
			par2 > ( -17 + this.guiTop ) && par2 <= ( 1 + this.guiTop ) ){
			this.drawHoveringText( woolSlotToolTipList, par1 - ( ( this.width - this.xSize ) / 2 ), par2 - ( ( this.height - this.ySize ) / 2 ) + 8, fontRendererObj );
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float par1, int par2, int par3 ){
		GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
		this.mc.renderEngine.bindTexture( new ResourceLocation( "tradebooth", CommonProxy.GuiTradeBoothTopOwnerPNG ) );
		int x = ( width - xSize ) / 2;
		int y = ( height - ySize ) / 2;
		this.drawTexturedModalRect( x, y - 34, 0, 0, 175, 223 );
	}
}