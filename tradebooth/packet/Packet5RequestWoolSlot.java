package tradebooth.packet;

import tradebooth.TradeBoothMod;
import tradebooth.container.ContainerTradeBoothTopOwner;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet5RequestWoolSlot implements IMessage{
	
	int x, y, z;
	
	public Packet5RequestWoolSlot(){
		
	}
	public Packet5RequestWoolSlot( int x, int y, int z ){
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
	public static class Handler implements IMessageHandler<Packet5RequestWoolSlot, IMessage>{

		public Handler(){
			
		}
		
		@Override
		public IMessage onMessage( Packet5RequestWoolSlot message, MessageContext ctx) {
			TileEntity tileEntity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity( message.x, message.y,  message.z );
			if( tileEntity == null ){
				return null;
			}
			else if( tileEntity instanceof TileEntityTradeBoothTop ){
				TileEntityTradeBoothTop tileEntityTop = (TileEntityTradeBoothTop) tileEntity;
				ItemStack itemStack = tileEntityTop.getStackInSlot( ContainerTradeBoothTopOwner.WOOL_SLOT_INDEX );
				if( itemStack == null ){
					return null;
				}
				TradeBoothMod.network.sendTo( new Packet4SetWoolSlot( itemStack, message.x, message.y, message.z ), ctx.getServerHandler().playerEntity );
			}
			return null;
		}
		
	}
}
