package tradebooth.packet;

import tradebooth.container.ContainerTradeBoothTopOwner;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class Packet4SetWoolSlot implements IMessage{

	private ItemStack woolStack;
	private int x, y, z;
	
	public Packet4SetWoolSlot(){
		
	}
	public Packet4SetWoolSlot( ItemStack itemStack, int x, int y, int z ){
		this.woolStack = itemStack;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.woolStack = ByteBufUtils.readItemStack( buf );
		this.x = ByteBufUtils.readVarInt( buf, 5 );
		this.y = ByteBufUtils.readVarInt( buf, 5 );
		this.z = ByteBufUtils.readVarInt( buf, 5 );
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack( buf, this.woolStack );
		ByteBufUtils.writeVarInt( buf, this.x, 5 );
		ByteBufUtils.writeVarInt( buf, this.y, 5 );
		ByteBufUtils.writeVarInt( buf, this.z, 5 );
	}
	public static class Handler implements IMessageHandler<Packet4SetWoolSlot, IMessage>{

		public Handler(){
			
		}
		@Override
		public IMessage onMessage(Packet4SetWoolSlot message, MessageContext ctx) {
			TileEntity tileEntity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity( message.x, message.y, message.z );
			if( tileEntity == null ){
				return null;
			}
			else if( tileEntity instanceof TileEntityTradeBoothTop ){
				TileEntityTradeBoothTop tileEntityTop = (TileEntityTradeBoothTop) tileEntity;
				tileEntityTop.setInventorySlotContents( 16, message.woolStack );
				ItemStack setStack = tileEntityTop.getStackInSlot( ContainerTradeBoothTopOwner.WOOL_SLOT_INDEX );
				System.out.println( "Item in slot: " + setStack.getDisplayName() );
				tileEntityTop.getWorldObj().markBlockForUpdate( message.x, message.y, message.z );
			}
			return null;
		}
		
	}
}
