package tradebooth.packet;

import tradebooth.TradeBoothSettings;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet1SetEnableCrafting implements IMessage{
	
	private boolean serverSetting; //Is the server set to enable crafting or not?

	public Packet1SetEnableCrafting(){
		
	}
	public Packet1SetEnableCrafting( boolean serverSetting ){
		this.serverSetting = serverSetting;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		if( ByteBufUtils.readUTF8String( buf ).contentEquals( "TRUE" ) ){
			this.serverSetting = true;
		}
		else{
			this.serverSetting = false;
		}
	}
	@Override
	public void toBytes(ByteBuf buf) {
		if( this.serverSetting ){
			ByteBufUtils.writeUTF8String( buf, "TRUE" );
		}
		else{
			ByteBufUtils.writeUTF8String( buf,  "FALSE" );
		}
	}
	public static class Handler implements IMessageHandler<Packet1SetEnableCrafting, IMessage>{

		public Handler(){
			
		}
		
		@Override
		public IMessage onMessage( Packet1SetEnableCrafting message,MessageContext ctx ){
			if( message.serverSetting ){
				TradeBoothSettings.disableCraftingRecipes = false;
			}
			else{
				TradeBoothSettings.disableCraftingRecipes = true;
			}
			return null;
		}
	}
}
