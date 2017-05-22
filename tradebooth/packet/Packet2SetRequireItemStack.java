package tradebooth.packet;

import tradebooth.TradeBoothSettings;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet2SetRequireItemStack implements IMessage{
	
	private boolean requireItemStack;

	public Packet2SetRequireItemStack(){
		
	}
	public Packet2SetRequireItemStack( boolean requireItemStack ){
		this.requireItemStack = requireItemStack;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		if( ByteBufUtils.readUTF8String( buf ).contentEquals( "TRUE" ) ){
			this.requireItemStack = true;
		}
		else{
			this.requireItemStack = false;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if( this.requireItemStack ){
			ByteBufUtils.writeUTF8String( buf, "TRUE" );
		}
		else{
			ByteBufUtils.writeUTF8String( buf,  "FALSE" );
		}
	}
	public static class Handler implements IMessageHandler<Packet2SetRequireItemStack, IMessage>{

		public Handler(){
			
		}
		
		@Override
		public IMessage onMessage( Packet2SetRequireItemStack message,MessageContext ctx ){
			if( message.requireItemStack ){
				TradeBoothSettings.requireItemStack = true;
			}
			else{
				TradeBoothSettings.requireItemStack = false;
			}
			return null;
		}
	}
}
