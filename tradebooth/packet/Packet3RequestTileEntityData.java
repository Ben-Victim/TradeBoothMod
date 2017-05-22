package tradebooth.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import tradebooth.TradeBoothMod;
import tradebooth.tileentity.TileEntityTradeBoothStorage;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet3RequestTileEntityData implements IMessage{

	private int x, y, z;
	
	public Packet3RequestTileEntityData(){
		
	}
	public Packet3RequestTileEntityData( int x, int y, int z ){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = ByteBufUtils.readVarInt( buf, 5 );
		this.y = ByteBufUtils.readVarInt( buf, 5 );
		this.z = ByteBufUtils.readVarInt( buf, 5 );
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt( buf, this.x, 5 );
		ByteBufUtils.writeVarInt( buf, this.y, 5 );
		ByteBufUtils.writeVarInt( buf, this.z, 5 );
	}
	public static class Handler implements IMessageHandler<Packet3RequestTileEntityData, IMessage>{

		public Handler(){
			
		}
		@Override
		public IMessage onMessage( Packet3RequestTileEntityData message, MessageContext ctx ){
			TileEntity tileEntity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity( message.x, message.y,  message.z );
			if( tileEntity == null ){
				return null;
			}
			else if( tileEntity instanceof TileEntityTradeBoothStorage ){
				TileEntityTradeBoothStorage tileEntityStorage = (TileEntityTradeBoothStorage) tileEntity;
				TradeBoothMod.network.sendTo( new Packet0SetPlayerName( tileEntityStorage.getPlayerOwner(), message.x, message.y, message.z ), ctx.getServerHandler().playerEntity );
			}
			else if( tileEntity instanceof TileEntityTradeBoothTop ){
				TileEntityTradeBoothTop tileEntityTop = (TileEntityTradeBoothTop) tileEntity;
				TradeBoothMod.network.sendTo( new Packet0SetPlayerName( tileEntityTop.getPlayerOwner(), message.x, message.y, message.z ), ctx.getServerHandler().playerEntity );
			}
			return null;
		}
	}
}
