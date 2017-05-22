package tradebooth.container;

import java.util.Iterator;

import tradebooth.TradeBoothSettings;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTradeBoothTopOwner extends ContainerTradeBoothTopNonOwner{
	
	public ContainerTradeBoothTopOwner( InventoryPlayer inventoryPlayer, TileEntityTradeBoothTop tileEntity ){
		super( inventoryPlayer, tileEntity );
	}
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int inventorySlotIndex ){
		//Method seems to be related only to shift-clicking items into inventory slots
		ItemStack itemStack = null;
		Slot slot = (Slot) this.inventorySlots.get( inventorySlotIndex );

		if( slot != null && slot.getHasStack() ){
			ItemStack itemStackInSlot = slot.getStack();
			itemStack = itemStackInSlot.copy();
			if( inventorySlotIndex < this.totalSlotsInContainer() ){
				if( !this.mergeItemStack( 	itemStackInSlot,
											this.totalSlotsInContainer(),
											this.totalSlotsInContainer() + this.totalSlotsInPlayer(),
											true ) ){
					return null;
				}
			}
			else if( !this.mergeItemStack( itemStackInSlot, 0, this.totalSlotsInContainer(), false ) ){
				//If all slots are full
				return null;
			}
			
			if( itemStackInSlot.stackSize == 0 ){
				slot.putStack( null );
			}
			else{
				slot.onSlotChanged();
			}
			
			if( itemStackInSlot.stackSize == itemStack.stackSize ){
				return null;
			}
			slot.onPickupFromSlot( player, itemStackInSlot );
		}
		return itemStack;
	}
	@Override
	public ItemStack slotClick( int slotIndex, int mouseButtonIndex, int keyIndex, EntityPlayer entityPlayer ){
		
		//System.out.println( slotIndex + ", " + mouseButtonIndex + ", " + keyIndex );
		
		if( !TradeBoothSettings.requireItemStack ){
			if( keyIndex == 4 ){
				keyIndex = 0;
			}
	        ItemStack itemstack = null;
	        InventoryPlayer inventoryPlayer = entityPlayer.inventory;
	        int l;
	        ItemStack itemstack1;
	
	        Slot slot2;
	        int k1;
	        ItemStack itemstack3;
	
	        if( ( keyIndex == 0 || keyIndex == 1 ) && ( mouseButtonIndex == 0 || mouseButtonIndex == 1 ) ){
	            if( slotIndex == -999 ){
	                if( inventoryPlayer.getItemStack() != null && slotIndex == -999 ){
	                    if (mouseButtonIndex == 0){
	                        entityPlayer.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack(), false );
	                        inventoryPlayer.setItemStack((ItemStack)null);
	                    }
	                    if (mouseButtonIndex == 1){
	                        entityPlayer.dropPlayerItemWithRandomChoice( inventoryPlayer.getItemStack().splitStack(1), false );
	
	                        if (inventoryPlayer.getItemStack().stackSize == 0)
	                        {
	                            inventoryPlayer.setItemStack((ItemStack)null);
	                        }
	                    }
	                }
	            }
	            else if( keyIndex == 1 ){
	            	//Removed: If shift click
	            }
	            else{
	                if( slotIndex < 0 ){
	                    return null;
	                }
	                else if( (slotIndex >= 0 && slotIndex < 16 ) || ( slotIndex >= 53 && slotIndex < 61 ) ){
	                	//If clicking a trade booth top slot
	                	Slot clickSlot = (Slot) this.inventorySlots.get( slotIndex );
	                	if( mouseButtonIndex == 0 ){ //Left click
	                		if( clickSlot.getStack() == null ){ //If clickSlot is empty
	                			if( inventoryPlayer.getItemStack() != null ){ //If player has item in use
	                				clickSlot.putStack( inventoryPlayer.getItemStack().copy() );
	                			}
	                		}
	                		else{ //If clickSlot is not empty
	                			if( inventoryPlayer.getItemStack() == null ){ //If player has NOTHING in use
	                				clickSlot.putStack( (ItemStack) null ); //Delete item in slot
	                			}
	                			else{ //If the player has item in use
	                				clickSlot.putStack( inventoryPlayer.getItemStack().copy() );
	                			}
	                		}
	                		clickSlot.onSlotChanged();
	                	}
	                	else if( mouseButtonIndex == 1 ){ //Right click
	                		if( clickSlot.getStack() == null ){ //If clickSlot is empty
	                			if( inventoryPlayer.getItemStack() != null ){ //If player has item in use
	                				clickSlot.putStack( inventoryPlayer.getItemStack().copy() );
	                				clickSlot.getStack().stackSize = 1;
	                				clickSlot.onSlotChanged();
	                			}
	                		}
	                		else{ //If clickSlot is occupied
	                			if( inventoryPlayer.getItemStack() == null ){ //If player has NO item in use
	                				clickSlot.putStack( (ItemStack) null ); //Delete item in slot
	                				clickSlot.onSlotChanged();
	                			}
	                			else{
	                				if( clickSlot.getStack().getItem() == inventoryPlayer.getItemStack().getItem() &&
	                					clickSlot.getStack().getItemDamage() == inventoryPlayer.getItemStack().getItemDamage() ){
	                					//If items are identical
	                					if( clickSlot.getStack().stackSize < clickSlot.getStack().getMaxStackSize() ){
	                						clickSlot.getStack().stackSize++;
	                						clickSlot.onSlotChanged();
	                					}
	                				}
	                				else{
	                					//If items are different
	                					clickSlot.putStack( inventoryPlayer.getItemStack().copy() );
	                    				clickSlot.getStack().stackSize = 1;
	                    				clickSlot.onSlotChanged();
	                				}
	                			}
	                		}
	                	}
	                }
	                else if( slotIndex == 52 ){ //Slot index 52 (the cover slot)
	                	entityPlayer.worldObj.markBlockForUpdate( this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord );
	                	return super.slotClick( slotIndex, mouseButtonIndex, keyIndex, entityPlayer );
	                }
	                else{
	                	//If clicking a player inventory slot
		                slot2 = (Slot)this.inventorySlots.get(slotIndex);
		
		                if( slot2 != null ){
		                    itemstack1 = slot2.getStack();
		                    ItemStack itemstack4 = inventoryPlayer.getItemStack();
		
		                    if (itemstack1 != null){
		                        itemstack = itemstack1.copy();
		                    }
		
		                    if (itemstack1 == null){
		                        if (itemstack4 != null && slot2.isItemValid(itemstack4)){
		                            k1 = mouseButtonIndex == 0 ? itemstack4.stackSize : 1;
		
		                            if (k1 > slot2.getSlotStackLimit()){
		                                k1 = slot2.getSlotStackLimit();
		                            }
		
		                            if (itemstack4.stackSize >= k1){
		                                slot2.putStack(itemstack4.splitStack(k1));
		                            }
		
		                            if (itemstack4.stackSize == 0){
		                                inventoryPlayer.setItemStack((ItemStack)null);
		                            }
		                        }
		                    }
		                    else if (slot2.canTakeStack(entityPlayer)){
		                        if (itemstack4 == null){
		                            k1 = mouseButtonIndex == 0 ? itemstack1.stackSize : (itemstack1.stackSize + 1) / 2;
		                            itemstack3 = slot2.decrStackSize(k1);
		                            inventoryPlayer.setItemStack(itemstack3);
		
		                            if (itemstack1.stackSize == 0)
		                            {
		                                slot2.putStack((ItemStack)null);
		                            }
		
		                            slot2.onPickupFromSlot(entityPlayer, inventoryPlayer.getItemStack());
		                        }
		                        else if (slot2.isItemValid(itemstack4)){
		                            if (itemstack1.getItem() == itemstack4.getItem() && itemstack1.getItemDamage() == itemstack4.getItemDamage() && ItemStack.areItemStackTagsEqual(itemstack1, itemstack4)){
		                                k1 = mouseButtonIndex == 0 ? itemstack4.stackSize : 1;
		
		                                if (k1 > slot2.getSlotStackLimit() - itemstack1.stackSize){
		                                    k1 = slot2.getSlotStackLimit() - itemstack1.stackSize;
		                                }
		
		                                if (k1 > itemstack4.getMaxStackSize() - itemstack1.stackSize){
		                                    k1 = itemstack4.getMaxStackSize() - itemstack1.stackSize;
		                                }
		
		                                itemstack4.splitStack(k1);
		
		                                if (itemstack4.stackSize == 0){
		                                    inventoryPlayer.setItemStack((ItemStack)null);
		                                }
		
		                                itemstack1.stackSize += k1;
		                            }
		                            else if (itemstack4.stackSize <= slot2.getSlotStackLimit()){
		                                slot2.putStack(itemstack4);
		                                inventoryPlayer.setItemStack(itemstack1);
		                            }
		                        }
		                        else if (itemstack1.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1 && (!itemstack1.getHasSubtypes() || itemstack1.getItemDamage() == itemstack4.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack1, itemstack4)){
		                            k1 = itemstack1.stackSize;
		
		                            if (k1 > 0 && k1 + itemstack4.stackSize <= itemstack4.getMaxStackSize()){
		                                itemstack4.stackSize += k1;
		                                itemstack1 = slot2.decrStackSize(k1);
		
		                                if (itemstack1.stackSize == 0){
		                                    slot2.putStack((ItemStack)null);
		                                }
		                                slot2.onPickupFromSlot(entityPlayer, inventoryPlayer.getItemStack());
		                            }
		                        }
		                    }
		                    slot2.onSlotChanged();
		                }
	                }
	            }
	        }
	        else if (keyIndex == 2 && mouseButtonIndex >= 0 && mouseButtonIndex < 9){
	            //Removed 
	        }
	        else if (keyIndex == 3 && entityPlayer.capabilities.isCreativeMode && inventoryPlayer.getItemStack() == null && slotIndex >= 0){
	            //Removed
	        }
	        else if (keyIndex == 4 && inventoryPlayer.getItemStack() == null && slotIndex >= 0){
	            //Removed
	        }
	        else if (keyIndex == 6 && slotIndex >= 0){
	            //Removed
	        }
        	return itemstack;
		}
		else{
			return super.slotClick( slotIndex, mouseButtonIndex, keyIndex, entityPlayer );
		}
    }
}