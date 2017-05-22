package tradebooth.packet;

import tradebooth.tileentity.TileEntityTradeBoothStorage;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet0SetPlayerName implements IMessage{
	
	private int x, y, z;
	private String playerName;
	
	public Packet0SetPlayerName(){
		
	}
	
	public Packet0SetPlayerName( String playerName, int x, int y, int z ){
		this.playerName = playerName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes( ByteBuf buf ){
		this.playerName = ByteBufUtils.readUTF8String( buf );
		this.x = ByteBufUtils.readVarInt( buf, 5 );
		this.y = ByteBufUtils.readVarInt( buf, 5 );
		this.z = ByteBufUtils.readVarInt( buf, 5 );
	}

	@Override
	public void toBytes( ByteBuf buf ){
		ByteBufUtils.writeUTF8String( buf, this.playerName );
		ByteBufUtils.writeVarInt( buf, this.x, 5 );
		ByteBufUtils.writeVarInt( buf, this.y, 5 );
		ByteBufUtils.writeVarInt( buf, this.z, 5 );
	}

	public static class Handler implements IMessageHandler<Packet0SetPlayerName, IMessage>{

		public Handler(){
			
		}
		@Override
		public IMessage onMessage( Packet0SetPlayerName message, MessageContext ctx ){
			if( message.playerName == null || message.playerName.isEmpty() ){
				return null;
			}
			else{
				TileEntity tileEntity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity( message.x, message.y, message.z );
				if( tileEntity instanceof TileEntityTradeBoothTop ){
					TileEntityTradeBoothTop tileEntityTop = (TileEntityTradeBoothTop) tileEntity;
					tileEntityTop.setPlayerOwner( message.playerName);
				}
				else if( tileEntity instanceof TileEntityTradeBoothStorage ){
					TileEntityTradeBoothStorage tileEntityStorage = (TileEntityTradeBoothStorage) tileEntity;
					tileEntityStorage.setPlayerOwner( message.playerName );
				}
			}
			return null;
		}
	}
}
