package tradebooth.container;

import tradebooth.TradeBoothMod;
import tradebooth.TradeBoothSettings;
import tradebooth.block.BlockTradeBoothStorage;
import tradebooth.gui.SlotBoothCover;
import tradebooth.gui.SlotImmovable;
import tradebooth.tileentity.TileEntityTradeBoothStorage;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

public class ContainerTradeBoothTopNonOwner extends Container{

	protected TileEntityTradeBoothTop tileEntity;
	public static final int WOOL_SLOT_INDEX = 16;
	
	public ContainerTradeBoothTopNonOwner( InventoryPlayer inventoryPlayer, TileEntityTradeBoothTop tileEntity ){
		this.tileEntity = tileEntity;
		
		//Added in 1.7.10.2.
		//With consideration to servers already running this mod, the order in which slots are referenced is
		//convoluted, since in this version more slots were added so that price could be represented by two slots
		//instead of one.		
		
		//Set the original 16 Trade Booth Slots 
		for( int i = 0; i < 16; i++ ){
			int row = i / 4;
			int col = i % 4;
			if( col == 0 ){
				this.addSlotToContainer( new Slot( this.tileEntity, i, 8, 22 + row * 21 ) );
			}
			else if( col == 1 ){
				this.addSlotToContainer( new Slot( this.tileEntity, i, 62, 22 + row * 21 ) );
			}
			else if( col == 2 ){
				this.addSlotToContainer( new Slot( this.tileEntity, i, 98, 22 + row * 21 ) );
			}
			else{
				this.addSlotToContainer( new Slot( this.tileEntity, i, 152, 22 + row * 21 ) );
			}
		}
		//Slots 16-51 for player inventory
		this.bindPlayerInventory( inventoryPlayer );

		//Wool slot 52
		if( this instanceof ContainerTradeBoothTopOwner ){
			this.addSlotToContainer( new SlotBoothCover( this.tileEntity, 16, 8, -14 ) );
		}
		//Extended price slots introduced in 1.7.10.2
		for( int i = 17; i <= 24; i++ ){
			int extendedRow = ( i - 17 ) / 2;
			int extendedColumn = ( i - 1 ) % 2;
			this.addSlotToContainer( new Slot( this.tileEntity, i, 26 + extendedColumn * 90, 22 + extendedRow * 21 ) );
		}
	}
	
	@Override
	public boolean canInteractWith( EntityPlayer player ){
		return tileEntity.isUseableByPlayer( player );
	}
	
	protected void bindPlayerInventory( InventoryPlayer inventoryPlayer ){
		for( int y = 0; y < 3; y++ ){
			for( int x = 0; x < 9; x++ ){
				this.addSlotToContainer( new Slot( inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 108 + y * 18 ) );
			}
		}
		for( int i = 0; i < 9; i++ ){
			this.addSlotToContainer( new Slot( inventoryPlayer, i, 8 + i * 18, 166 ) );
		}
	}
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int inventorySlot ){
		ItemStack itemStack = null;
		Slot slot = (Slot) this.inventorySlots.get( inventorySlot );
		return itemStack;
	}
	@Override
	public ItemStack slotClick(int slotIndex, int mouseButtonID, int shiftClick, EntityPlayer entityPlayer){
		//mouseButtonID 0 = left click
		//mouseButtonID 1 = right click
		//mouseButtonID 2 = center/scroll wheel click, also makes shiftClick = 3

		if( slotIndex == 52 ){ //Allows TopOwner to call super.slotClick() and have the item actually transfer. (Wool slot)
			return super.slotClick( slotIndex, mouseButtonID, shiftClick, entityPlayer );
		}
		if( !entityPlayer.worldObj.isRemote ){ //Server
						
			if( mouseButtonID != 0 ){ //If not left click
				return null;
			}
			if( slotIndex % 2 == 1 && slotIndex < 16 ){ //If the left-click was on a buy slot. < 16 because buy slots are always less than 16.
				
				int priceSlot2Index = slotIndex - 1 + 52 - ( slotIndex / 2 );
				Slot buyingSlot = (Slot) this.inventorySlots.get( slotIndex );
				Slot priceSlot1 = (Slot) this.inventorySlots.get( slotIndex - 1 );
				Slot priceSlot2 = (Slot) this.inventorySlots.get( priceSlot2Index );
				
				ItemStack buyingStack = buyingSlot.getStack();
				ItemStack priceStack1 = priceSlot1.getStack();
				ItemStack priceStack2 = priceSlot2.getStack();
				
				//Count how many price stacks there are
				int priceStackCount = 0;
				if( priceStack1 != null ){ priceStackCount++; }
				if( priceStack2 != null ){ priceStackCount++; }
				
				if( buyingStack == null || priceStackCount == 0 ){ //If the buying stack is null or both price stacks are null
					return null;
				}
				
				if( !this.canPlayerAccept( buyingStack, entityPlayer.inventory ) ){	//Check if the player has room to buy something
					entityPlayer.addChatComponentMessage( new ChatComponentTranslation( "Your inventory is too full for this transaction." ) );
					return null;
				}
					
				//Check to see if the player has the price to complete this transaction
				if( !this.canPlayerProvide( priceStack1, priceStack2 ) ){ //If the player's inventory doesn't have enough to complete a purchase stack
					entityPlayer.addChatComponentMessage( new ChatComponentTranslation( "You do not have enough items for this transaction." ) );
					return null;
				}
				//Check to see if the TradeBoothTop is connected to a TradeBoothStorage that is owned by the same player
				if( !this.tileEntity.isConnectedToStorageAndSameOwner( entityPlayer.worldObj ) ){
					entityPlayer.addChatComponentMessage( new ChatComponentTranslation( "A valid storage block is not connected to booth top." ) );
					return null;
				}
					
				//Check to see if the TradeBoothStorage has room to buy the priceStack
				//Check to see if the TradeBoothStorage has enough of the buyingStack to sell to the player
				//Check to see if this is an adminTopBlock (meta4) then allow the transaction to occur regardless of the condition of the connected storage
				if( ( 	this.tileEntity.canConnectedStorageBuy( priceStack1, priceStack2, entityPlayer.worldObj ) &&
						this.tileEntity.canConnectedStorageSell( buyingStack, entityPlayer.worldObj ) ) || this.isAdminTopBlock() ){
					
					//Make the transaction!
					this.transact( entityPlayer, priceStack1, priceStack2, buyingStack );
					entityPlayer.worldObj.playSoundAtEntity( entityPlayer, "random.orb", 1.0F, 1.0F );
				}
				else{
					entityPlayer.addChatComponentMessage( new ChatComponentTranslation( "Storage is full or sold out." ) );
					return null;
				}
			}		
		}
		return null;
	}

	public int totalSlotsInContainer(){
		return 24;
	}
	public int totalSlotsInPlayer(){
		return 36;
	}
	public boolean canPlayerAccept( ItemStack itemStack, InventoryPlayer inventoryPlayer ){
		int emptySlotIndex = inventoryPlayer.getFirstEmptyStack();
		if( emptySlotIndex >= 0 ){
			return true;
		}
		else{
			int count = 0; //Count the amount of free space in the player's stacks of the same item type as the itemStack provided to this method
			for( int i = this.totalSlotsInContainer(); i < this.totalSlotsInContainer() + this.totalSlotsInPlayer(); i++ ){
				ItemStack playerStack = (ItemStack) this.inventoryItemStacks.get( i );
				if( playerStack != null && playerStack.getItem() == itemStack.getItem() ){
					if( playerStack.stackSize < playerStack.getItem().getItemStackLimit() ){
						//If the stacksize is less than the item's stacklimit
						//This check is done in case the stackSize was forced above its limit (can be done in creative mode)
						count += playerStack.getItem().getItemStackLimit() - playerStack.stackSize;
					}
				}
			}
			if( count >= itemStack.stackSize ){
				return true;
			}
		}
		return false;
	}
	public boolean canPlayerProvide( ItemStack priceStack1, ItemStack priceStack2 ){
		//Checks to see if the player inventory contains the desired priceStacks
		
		if( priceStack1 == null && priceStack2 == null ){ //If both stacks are empty
			return false;
		}
		if( priceStack1 == null || priceStack2 == null ){ //If either of the priceStacks is null
			if( priceStack1 == null ){
				return canPlayerProvide( priceStack2 );
			}
			return canPlayerProvide( priceStack1 );
		}
		//At this point we know that there are two itemstacks that arent null
		//Check to see if the priceStack items are identical
		
		boolean priceStacksAreIdentical = false;
		if( priceStack1 != null && priceStack1.isItemEqual( priceStack2 ) ){ //If both are not null and are the same item			
			if( priceStack1.hasTagCompound() && priceStack2.hasTagCompound() ){ //If both have tag compounds
				if( priceStack1.getTagCompound().equals( priceStack2.getTagCompound() ) ){ //If both tag compounds are identical
					priceStacksAreIdentical = true;
				}	
			}
			else if( !priceStack1.hasTagCompound() && !priceStack2.hasTagCompound() ){ //If neither have tag compounds (then they are both the same item without tag compounds)
				priceStacksAreIdentical = true;
			}
		}
		
		if( priceStacksAreIdentical ){ //Then we're going to combine these items and count if the player has enough of them
			return canPlayerProvide( priceStack1, priceStack1.stackSize + priceStack2.stackSize );
		} 
		else if( canPlayerProvide( priceStack1 ) && canPlayerProvide( priceStack2 ) ){ //If we have to search the player inventory for two different items
			return true;
		}		
		return false;
	}
	public boolean canPlayerProvide( ItemStack priceStack ){
		if( priceStack != null ){
			int count = 0;
			for( int i = 16; i < 52; i++ ){ //The range of player inventory slots
				Slot playerSlot = (Slot) this.inventorySlots.get( i );
				ItemStack playerStack = (ItemStack) playerSlot.getStack();
				if( playerStack != null && playerStack.isItemEqual( priceStack ) ){ //If we found a stack with the same item as the priceStack
					if( priceStack.hasTagCompound() ){//If the priceStack has a tagCompound
						if( priceStack.getTagCompound().equals( playerStack.getTagCompound() ) ){  //If the stacks have matching tagCompounds
							count += playerStack.stackSize;
						}
					}
					else if( !playerStack.hasTagCompound() ){ //Previous if statement says priceStack doesn't have a tagCompound, so if playerStack does this else if fails
						count += playerStack.stackSize;
					}
				}
			}
			if( count >= priceStack.stackSize ){
				return true;
			}
		}
		return false;
	}
	public boolean canPlayerProvide( ItemStack priceStack, int priceCount ){
		int count = 0;
		for( int i = this.totalSlotsInContainer(); i < this.totalSlotsInContainer() + this.totalSlotsInPlayer(); i++ ){
			Slot playerSlot = (Slot) this.inventorySlots.get( i );
			ItemStack playerStack = (ItemStack) playerSlot.getStack();
			if( playerStack != null && playerStack.isItemEqual( priceStack ) ){ //If we found a stack with the same item as the priceStack
				if( priceStack.hasTagCompound() ){
					if( priceStack.getTagCompound().equals( playerStack.getTagCompound() ) ){
						count += playerStack.stackSize;
					}
				}
				else if( !playerStack.hasTagCompound() ){
					count += playerStack.stackSize;
				}
			}
		}
		if( count >= priceCount ){
			return true;
		}
		return false;
	}
	public void removeFromPlayer( EntityPlayer entityPlayer, ItemStack removeStack ){
		int numberToRemove = removeStack.stackSize;
		for( int i = 0; i < entityPlayer.inventory.getSizeInventory() && numberToRemove > 0; i++ ){
			ItemStack checkStack = entityPlayer.inventory.getStackInSlot( i );
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
					entityPlayer.onUpdate();
				}
				else if( checkStack.stackSize <= removeStack.stackSize ){ //If this checkStack has less than or just enough of the removeStack amount
					numberToRemove -= checkStack.stackSize; //Decrement the amount we're looking for
					entityPlayer.inventory.setInventorySlotContents( i , null ); //And nullify this stack
					entityPlayer.onUpdate();
				}
			}
		}
	}
	public void removeFromStorage( TileEntityTradeBoothStorage storage, ItemStack removeStack ){
		storage.removeStack( removeStack );
	}
	public void addToStorage( TileEntityTradeBoothStorage storage, ItemStack addStack ){
		storage.addToStorage( addStack );
	}
	private void addToPlayer(EntityPlayer entityPlayer, ItemStack buyingStack) {
		int numberToAdd = buyingStack.stackSize;
		//Try to add the quantity in the addStack to existing itemStacks of the same kind
		for( int i = 0; i < entityPlayer.inventory.getSizeInventory() && numberToAdd > 0; i++ ){
			ItemStack itemStack = entityPlayer.inventory.getStackInSlot( i );
			if( itemStack != null && itemStack.isItemEqual( buyingStack ) ){
				if( itemStack.stackSize < itemStack.getItem().getItemStackLimit() ){
					if( itemStack.getItem().getItemStackLimit() - itemStack.stackSize >= numberToAdd ){ //If we have enough room in this stack to completely use up the numberToAdd
						itemStack.stackSize += numberToAdd;
						numberToAdd = 0;
						entityPlayer.onUpdate();
					}
					else{
						numberToAdd -= itemStack.getItem().getItemStackLimit() - itemStack.stackSize;
						itemStack.stackSize = itemStack.getItem().getItemStackLimit();
						entityPlayer.onUpdate();
					}
				}
			}
		}
		if( numberToAdd > 0 ){ //If there are still items to add after trying to fill same-type itemStacks
			for( int i = 0; i < entityPlayer.inventory.getSizeInventory() && numberToAdd > 0; i++ ){
				ItemStack itemStack = entityPlayer.inventory.getStackInSlot( i );
				if( itemStack == null ){ //we're looking for null/empty itemStacks
					ItemStack newItemStack = buyingStack.copy();
					newItemStack.stackSize = numberToAdd;
					entityPlayer.inventory.addItemStackToInventory( newItemStack );
					numberToAdd = 0;
					entityPlayer.onUpdate();
				}
			}
		}
	}
	public boolean isAdminTopBlock(){
		if( this.tileEntity.getWorldObj().getBlockMetadata( this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord ) == 4 ){
			return true;
		}
		return false;
	}
	public void transact( EntityPlayer entityPlayer, ItemStack priceStack1, ItemStack priceStack2, ItemStack buyingStack ){
		//Move the price item from player inventory to booth storage
		
		if( priceStack1 != null ){ this.removeFromPlayer( entityPlayer, priceStack1 ); }
		if( priceStack2 != null ){ this.removeFromPlayer( entityPlayer, priceStack2 ); }
		
		if( !this.isAdminTopBlock() ){
			if( priceStack1 != null ){ this.addToStorage( this.tileEntity.getConnectedTileEntityStorage( entityPlayer.worldObj ), priceStack1 ); }
			if( priceStack2 != null ){ this.addToStorage( this.tileEntity.getConnectedTileEntityStorage( entityPlayer.worldObj ), priceStack2 ); }
		
			//Move the buying item from booth storage to player inventory
			this.removeFromStorage( this.tileEntity.getConnectedTileEntityStorage( entityPlayer.worldObj ), buyingStack );
		}
		this.addToPlayer( entityPlayer, buyingStack );
		
		//Tell the storage block to send a redpower signal
		TileEntityTradeBoothStorage tileEntityStorage = this.tileEntity.getConnectedTileEntityStorage( entityPlayer.worldObj );
		tileEntityStorage.providePower = true;
		entityPlayer.worldObj.notifyBlockChange( tileEntityStorage.xCoord, tileEntityStorage.yCoord, tileEntityStorage.zCoord, TradeBoothMod.blockTradeBoothStorage );
		entityPlayer.worldObj.scheduleBlockUpdate( tileEntityStorage.xCoord, tileEntityStorage.yCoord, tileEntityStorage.zCoord, TradeBoothMod.blockTradeBoothStorage, TradeBoothMod.blockTradeBoothStorage.tickRate( entityPlayer.worldObj ) );
	}
}