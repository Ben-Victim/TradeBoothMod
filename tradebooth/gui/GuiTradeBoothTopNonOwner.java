package tradebooth.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import tradebooth.CommonProxy;
import tradebooth.container.ContainerTradeBoothTopNonOwner;
import tradebooth.container.ContainerTradeBoothTopOwner;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTradeBoothTopNonOwner extends GuiContainer{

	private TileEntityTradeBoothTop tileEntity;
	
	public GuiTradeBoothTopNonOwner( InventoryPlayer inventoryPlayer, TileEntityTradeBoothTop tileEntity ){
		super( new ContainerTradeBoothTopNonOwner( inventoryPlayer, tileEntity ) );
		this.tileEntity = tileEntity;
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
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer( float par1, int par2, int par3 ){
		GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );
		this.mc.renderEngine.bindTexture( new ResourceLocation( "tradebooth", CommonProxy.GuiTradeBoothTopNonOwnerPNG ) );
		int x = ( width - xSize ) / 2;
		int y = ( height - ySize ) / 2;
		this.drawTexturedModalRect( x, y - 34, 0, 0, 175, 223 );
	}
}