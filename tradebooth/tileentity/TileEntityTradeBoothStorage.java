package tradebooth.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import tradebooth.TradeBoothMod;
import tradebooth.packet.Packet0SetPlayerName;
import tradebooth.packet.Packet3RequestTileEntityData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityTradeBoothStorage extends TileEntity implements IInventory{
	
	private ItemStack[] inventory;
	private String playerOwner;
	public boolean providePower = false;

	public TileEntityTradeBoothStorage(){
		this.inventory = new ItemStack[36];
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
		return 	this.worldObj.getTileEntity( this.xCoord, this.yCoord, this.zCoord ) == this &&
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
	public boolean canAcceptItemStacks( ItemStack itemStack1, ItemStack itemStack2 ){
		//This method will handle null parameter stacks
		
		//Count how many stacks we need to accept
		int stacksToAccept = 0;
		if( itemStack1 != null ){ stacksToAccept++;	}
		if( itemStack2 != null ){ stacksToAccept++; }
		
		//Check if there are empty itemStacks in the inventory first
		int emptyStacks = 0;
		for( int i = 0; i < this.getSizeInventory() || emptyStacks == stacksToAccept; i++ ){
			ItemStack checkStack = this.getStackInSlot( i );
			if( checkStack == null ){
				emptyStacks++;
			}
		}
		if( emptyStacks >= stacksToAccept ){ //If the inventory has enough stacks, then we can accept the two stacks
			return true;
		}
		
		//At this point there aren't enough stacks to allow the desired stacks in
		//So check for space among half full stacks
		
		//Check to see if the itemStack items are identical
		boolean itemStacksAreIdentical = false;
		if( itemStack1 != null && itemStack1.isItemEqual( itemStack2 ) ){ //If both are not null and are the same item
			if( itemStack1.hasTagCompound() && itemStack2.hasTagCompound() ){ //If both have tag compounds
				if( itemStack1.getTagCompound().equals( itemStack2.getTagCompound() ) ){ //If both tag compounds are identical
					itemStacksAreIdentical = true;
				}	
			}
			else if( !itemStack1.hasTagCompound() && !itemStack2.hasTagCompound() ){ //If neither have tag compounds (then they are both the same item without tag compounds)
				itemStacksAreIdentical = true;
			}
		}
		if( itemStacksAreIdentical ){ //Then we're going to combine these items and count if the player has enough of them
			return canAcceptItemStack( itemStack1, itemStack1.stackSize + itemStack2.stackSize );
		}
		else if( itemStack1 == null || itemStack2 == null ){
			if( itemStack1 == null ){
				return canAcceptItemStack( itemStack2 );
			}
			else{
				return canAcceptItemStack( itemStack1 );
			}
		}
		else if( canAcceptItemStack( itemStack1 ) && canAcceptItemStack( itemStack2 ) ){
			return true;
		}
		
		return false;
	}
	public boolean canAcceptItemStack( ItemStack itemStack, int itemCount ){ //Special version of this method for itemstack sums greater than 64
		int count = 0;
		for( int i = 0; i < this.getSizeInventory(); i++ ){
			ItemStack inventoryStack = this.getStackInSlot( i );
			if( inventoryStack != null && inventoryStack.isItemEqual( itemStack ) ){ //If we found a stack with the same item as the priceStack
				if( inventoryStack.stackSize <= inventoryStack.getMaxStackSize() ){
					count += inventoryStack.getMaxStackSize() - inventoryStack.stackSize;
				}
			}
		}
		if( count >= itemCount ){
			return true;
		}
		return false;
	}
	public boolean canAcceptItemStack( ItemStack itemStack ){
		if( this.hasEmptyInventoryStack() ){ //If there is an empty slot
			return true;
		}
		else if( itemStack.hasTagCompound() ){ //Since there is no empty slot, and this item has tagCompound, it can't be combined with any other slot
			return false;
		}
		else{ //If there isn't an empty slot
			//Check for space among same-itemstacks with enough empty space
			int count = 0;
			for( int i = 0; i < this.getSizeInventory(); i++ ){
				ItemStack checkStack = this.getStackInSlot( i );
				if( checkStack != null && checkStack.isItemEqual( itemStack ) ){
					if( checkStack.stackSize <= checkStack.getItem().getItemStackLimit() ){
						//This previous if statement is in case the stack was forced to accept a stackSize large than its limit (creative mode can do this)
						count += checkStack.getItem().getItemStackLimit() - checkStack.stackSize;
					}
				}
			}
			if( count >= itemStack.stackSize ){ //If we counted enough empty spaces in all the itemstacks of the same type
				return true;
			}
		}
		return false;
	}
	public boolean canProvideItemStack( ItemStack itemStack ){
		int count = 0;
		for( int i = 0; i < this.getSizeInventory(); i++ ){
			ItemStack checkStack = this.getStackInSlot( i );
			if( checkStack != null && checkStack.isItemEqual( itemStack ) ){ //If we found a stack with the same item as the itemStack
				if( itemStack.hasTagCompound() ){//If the itemStack has a tagCompound
					if( checkStack.hasTagCompound() && checkStack.getTagCompound().equals( itemStack.getTagCompound() ) ){  //If the stacks have matching tagCompounds
						count += checkStack.stackSize;
					}
				}
				else if( !checkStack.hasTagCompound() ){ //Previous if statement says itemStack doesn't have a tagCompound, so if checkStack does this else if fails
					count += checkStack.stackSize;
				}
			}
		}
		if( count >= itemStack.stackSize ){
			return true;
		}
		return false;
	}
	public boolean hasEmptyInventoryStack(){
		//Searches the storage for a single empty storage stack.
		for( int i = 0; i < this.getSizeInventory(); i++ ){
			ItemStack itemStack = this.getStackInSlot( i );
			if( itemStack == null ){
				return true;
			}
		}
		return false;
	}
	public void addToStorage( ItemStack addStack ){
		int numberToAdd = addStack.stackSize;
		//Try to add the quantity in the addStack to existing itemStacks of the same kind
		for( int i = 0; i < this.getSizeInventory() && numberToAdd > 0; i++ ){
			ItemStack itemStack = this.getStackInSlot( i );
			if( itemStack != null && itemStack.isItemEqual( addStack ) ){
				if( itemStack.stackSize < itemStack.getItem().getItemStackLimit() ){
					if( itemStack.getItem().getItemStackLimit() - itemStack.stackSize >= numberToAdd ){ //If we have enough room in this stack to completely use up the numberToAdd
						itemStack.stackSize += numberToAdd;
						numberToAdd = 0;
						this.updateEntity();
					}
					else{
						numberToAdd -= itemStack.getItem().getItemStackLimit() - itemStack.stackSize;
						itemStack.stackSize = itemStack.getItem().getItemStackLimit();
						this.updateEntity();
					}
				}
			}
		}
		if( numberToAdd > 0 ){ //If there are still items to add after trying to fill same-type itemStacks
			for( int i = 0; i < this.getSizeInventory() && numberToAdd > 0; i++ ){
				ItemStack itemStack = this.getStackInSlot( i );
				if( itemStack == null ){ //we're looking for null/empty itemStacks
					ItemStack newItemStack = addStack.copy();
					newItemStack.stackSize = numberToAdd;
					this.setInventorySlotContents( i , newItemStack );
					numberToAdd = 0;
					this.updateEntity();
				}
			}
		}
	}
	public int countNullItemStacks(){
		int count = 0;
		for( int i = 0; i < this.getSizeInventory(); i++ ){
			if( this.getStackInSlot( i ) == null ){
				count++;
			}
		}
		return count;
	}

	public void removeStack( ItemStack removeStack ) {
		int numberToRemove = removeStack.stackSize;
		for( int i = 0; i < this.getSizeInventory(); i++ ){
			ItemStack checkStack = this.getStackInSlot( i );
			boolean isSuitableStack = false;
			if( checkStack != null && checkStack.isItemEqual( removeStack ) ){ //If both stacks have the same item
				if( removeStack.hasTagCompound() ){
					if( checkStack.hasTagCompound() && checkStack.getTagCompound().equals( removeStack.getTagCompound() ) ){
						isSuitableStack = true;
					}
				}
				else if( !checkStack.hasTagCompound() ){
					isSuitableStack = true;
				}
			}
			if( isSuitableStack ){
				if( checkStack.stackSize > numberToRemove ){ //If the player has more than enough in this stack to complete the removal
					checkStack.stackSize -= numberToRemove;
					numberToRemove = 0;
					this.updateEntity();
				}
				else if( checkStack.stackSize <= removeStack.stackSize ){ //If this checkStack has less than or just enough of the removeStack amount
					numberToRemove -= checkStack.stackSize; //Decrement the amount we're looking for
					this.setInventorySlotContents( i , null ); //And nullify this stack
					this.updateEntity();
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public void validate(){
		super.validate();
		if( this.worldObj.isRemote ){
			//Client requests extra tile entity data, which is just the trade booth block's owner's name
			TradeBoothMod.network.sendToServer( new Packet3RequestTileEntityData( this.xCoord, this.yCoord, this.zCoord ) );
		}
	}
}