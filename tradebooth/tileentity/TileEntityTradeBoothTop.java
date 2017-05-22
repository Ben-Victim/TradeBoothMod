package tradebooth.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import tradebooth.TradeBoothMod;
import tradebooth.container.ContainerTradeBoothTopOwner;
import tradebooth.packet.Packet3RequestTileEntityData;
import tradebooth.packet.Packet4SetWoolSlot;
import tradebooth.packet.Packet5RequestWoolSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileEntityTradeBoothTop extends TileEntity implements IInventory, ISidedInventory{

	private ItemStack[] inventory;
	private String playerOwner;
	private static int ticksPerCycle = 60;
	private int tickCount = 0;
	
	public TileEntityTradeBoothTop(){
		this.inventory = new ItemStack[25];
		this.playerOwner = "";
	}
	
	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	@Override
	public ItemStack getStackInSlot( int inventorySlot ) {
		return this.inventory[inventorySlot];
	}

	@Override
	public ItemStack decrStackSize( int inventorySlot, int amount ) {
		ItemStack itemStack = this.getStackInSlot( inventorySlot );
		if( itemStack != null ){
			if( itemStack.stackSize <= amount ){
				this.setInventorySlotContents( inventorySlot, null );
			}
			else{
				itemStack = itemStack.splitStack( amount );
				if( itemStack.stackSize == 0 ){
					this.setInventorySlotContents( inventorySlot, null );
				}
			}
		}
		return itemStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int inventorySlot ) {
		ItemStack itemStack = getStackInSlot( inventorySlot );
		if( itemStack != null ){
			this.setInventorySlotContents( inventorySlot, null );
		}
		return itemStack;
	}

	@Override
	public void setInventorySlotContents( int inventorySlot, ItemStack itemStack ) {
		this.inventory[inventorySlot] = itemStack;
		if( itemStack != null && itemStack.stackSize > this.getInventoryStackLimit() ){
			itemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player ) {
		return this.worldObj.getTileEntity( this.xCoord, this.yCoord, this.zCoord ) == this &&
				player.getDistanceSq( xCoord + 0.5, yCoord + 0.5, zCoord + 0.5 ) < 64;
	}
	
	@Override
	public void readFromNBT( NBTTagCompound tagCompound ){
		super.readFromNBT( tagCompound );
		
		NBTTagList tagList = tagCompound.getTagList( "Inventory", Constants.NBT.TAG_COMPOUND );
		for( int i = 0; i < tagList.tagCount(); i++ ){
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt( i );
			byte slot = tag.getByte( "Slot" );
			if( slot >= 0 && slot < this.inventory.length ){
				this.inventory[slot] = ItemStack.loadItemStackFromNBT( tag );
			}
		}
		this.playerOwner = tagCompound.getString( "PlayerOwner" );
	}

	@Override
	public void writeToNBT( NBTTagCompound tagCompound ){
		super.writeToNBT( tagCompound );
		
		NBTTagList tagList = new NBTTagList();
		for( int i = 0; i < this.inventory.length; i++ ){
			ItemStack itemStack = this.inventory[i];
			if( itemStack != null ){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte( "Slot", (byte) i );
				itemStack.writeToNBT( tag );
				tagList.appendTag( tag );
			}
		}
		tagCompound.setTag( "Inventory", tagList );
		tagCompound.setString( "PlayerOwner", this.playerOwner );
	}
	
	public String getPlayerOwner(){
		return this.playerOwner;
	}
	
	public void setPlayerOwner( String newOwner ){
		this.playerOwner = newOwner;
	}
	public boolean isConnectedToStorageAndSameOwner( World world ){
		if( this.yCoord > 1 ){ //Do not allow a TradeBoothTop to attempt a connection when placed below y: 2
			TileEntity checkEntity = world.getTileEntity( this.xCoord, this.yCoord - 1, this.zCoord );
			if( checkEntity != null && checkEntity instanceof TileEntityTradeBoothStorage ){
				TileEntityTradeBoothStorage storageEntity = (TileEntityTradeBoothStorage) checkEntity;
				String topOwner = this.getPlayerOwner();
				String storageOwner = storageEntity.getPlayerOwner();
				if( !topOwner.equals( "" ) && topOwner.equals( storageOwner ) ){ //If at least one of them isn't empty and they are both the same
					return true;
				}
			}
		}
		return false;
	}
	public TileEntityTradeBoothStorage getConnectedTileEntityStorage( World world ){
		if( this.isConnectedToStorageAndSameOwner( world ) ){
			return (TileEntityTradeBoothStorage) world.getTileEntity( this.xCoord, this.yCoord - 1, this.zCoord );
		}
		return null;
	}
	public boolean canConnectedStorageBuy( ItemStack buyStack1, ItemStack buyStack2, World world ){
		TileEntity checkEntity = world.getTileEntity( this.xCoord, this.yCoord - 1, this.zCoord );
		if( checkEntity != null && checkEntity instanceof TileEntityTradeBoothStorage ){
			TileEntityTradeBoothStorage storageEntity = (TileEntityTradeBoothStorage) checkEntity;
			return storageEntity.canAcceptItemStacks( buyStack1, buyStack2 );
		}
		return false;
	}
	public boolean canConnectedStorageSell( ItemStack sellStack, World world ){
		TileEntity checkEntity = world.getTileEntity( this.xCoord, this.yCoord - 1, this.zCoord );
		if( checkEntity != null && checkEntity instanceof TileEntityTradeBoothStorage ){
			TileEntityTradeBoothStorage storageEntity = (TileEntityTradeBoothStorage) checkEntity;
			return storageEntity.canProvideItemStack( sellStack );
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void validate(){
		super.validate();
		if( this.worldObj.isRemote ){
			//Client requests extra tile entity data, which is just the trade booth block's owner's name
			TradeBoothMod.network.sendToServer( new Packet3RequestTileEntityData( this.xCoord, this.yCoord, this.zCoord ) );
			TradeBoothMod.network.sendToServer( new Packet5RequestWoolSlot( this.xCoord, this.yCoord, this.zCoord ) );
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		// TODO Auto-generated method stub
		return new int[ 0 ];
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		// TODO Auto-generated method stub
		return false;
	}
}