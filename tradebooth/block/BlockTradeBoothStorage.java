package tradebooth.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tradebooth.CommonProxy;
import tradebooth.TradeBoothMod;
import tradebooth.TradeBoothSettings;
import tradebooth.packet.Packet0SetPlayerName;
import tradebooth.tileentity.TileEntityTradeBoothStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTradeBoothStorage extends BlockContainer{

	public static final String[] woodType = new String[] {"Oak", "Spruce", "Birch", "Jungle", "Admin" };
    private IIcon[] iconArray = new IIcon[5];

	public BlockTradeBoothStorage(){
		super( Material.wood );
		this.setHardness( 5.0F );
		this.setResistance( TradeBoothSettings.explosionResistance );
		this.setBlockName( "blockTradeBoothStorage" );
		this.setCreativeTab( CreativeTabs.tabDecorations );
	}
	
	@Override
	public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, int var1, float var2, float var3, float var4 ){
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( tileEntity == null || player.isSneaking() ){
			return false;
		}
		else if( tileEntity instanceof TileEntityTradeBoothStorage ){
			TileEntityTradeBoothStorage tradeBoothStorage = (TileEntityTradeBoothStorage) tileEntity;
			String playerOwnerName = tradeBoothStorage.getPlayerOwner();
			if( playerOwnerName == null || playerOwnerName.isEmpty() ){ //If this TradeBooth is unowned
				tradeBoothStorage.setPlayerOwner( player.getDisplayName() );
				if( !world.isRemote ){ //Server					
					TradeBoothMod.network.sendToAllAround( new Packet0SetPlayerName( playerOwnerName, x, y, z ), new TargetPoint( player.dimension, x, y, z, 220 ) );
				}
			}
			if( tradeBoothStorage.getPlayerOwner().equals( player.getDisplayName() ) || player.capabilities.isCreativeMode ){
				world.playSoundAtEntity( player, "random.chestopen", 0.5F, 0.7F );
				player.openGui( TradeBoothMod.instance, 0, world, x, y, z);
				return true;
			}
			else{
				if( world.isRemote ){
					player.addChatComponentMessage( new ChatComponentTranslation( "This storage container does not belong to you." ) );
				}
				return true;
			}
		}
		return true;
	}
	@Override
	public void breakBlock( World world, int x, int y, int z, Block par5, int par6 ){
		this.dropItems( world, x, y, z );
		super.breakBlock( world, x, y, z, par5, par6 );
	}
	
	private void dropItems( World world, int x, int y, int z ){
		Random rand = new Random();
		
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( !( tileEntity instanceof IInventory ) ){
			return;
		}
		IInventory inventory = (IInventory) tileEntity;
		
		for( int i = 0; i < inventory.getSizeInventory(); i++ ){
			ItemStack itemStack = inventory.getStackInSlot( i );
			
			if( itemStack != null && itemStack.stackSize > 0 ){
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				
				EntityItem entityItem = new EntityItem( world, x + rx, y + ry, z + rz, new ItemStack( itemStack.getItem(), itemStack.stackSize, itemStack.getItemDamage() ) );
				if( itemStack.hasTagCompound() ){
					entityItem.getEntityItem().setTagCompound( (NBTTagCompound) itemStack.getTagCompound().copy() );
				}
				
				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld( entityItem );
				itemStack.stackSize = 0;
			}
		}
	}
	@Override
	public TileEntity createTileEntity( World var1, int meta ) {
	
		return new TileEntityTradeBoothStorage();
	}
	
	@Override
	public boolean hasTileEntity( int metadata ){
		return true;
	}
	@Override
	public float getPlayerRelativeBlockHardness( EntityPlayer player, World world, int x, int y, int z ){
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( tileEntity != null && tileEntity instanceof TileEntityTradeBoothStorage ){
			TileEntityTradeBoothStorage tradeBoothStorage = (TileEntityTradeBoothStorage) tileEntity;
			String ownerName = tradeBoothStorage.getPlayerOwner();
			if( ownerName == null || ownerName.isEmpty() ){ //If this is unclaimed
				return super.getPlayerRelativeBlockHardness( player, world, x, y, z );
			}
			else if( ownerName.equals( player.getDisplayName() ) ){ //If this block is claimed by the player breaking it
				return super.getPlayerRelativeBlockHardness( player, world, x, y, z );
			}
			else{ //If this block belongs to someone else
				return 0.0F;
			}
		}
		return super.getPlayerRelativeBlockHardness( player, world, x, y, z );
	}
	@Override
	public void registerBlockIcons( IIconRegister iconRegister ){
        this.iconArray[0] = iconRegister.registerIcon( "tradebooth:tradeboothstorage0" );
        this.iconArray[1] = iconRegister.registerIcon( "tradebooth:tradeboothstorage1" );
        this.iconArray[2] = iconRegister.registerIcon( "tradebooth:tradeboothstorage2" );
        this.iconArray[3] = iconRegister.registerIcon( "tradebooth:tradeboothstorage3" );
        this.iconArray[4] = iconRegister.registerIcon( "tradebooth:tradeboothstorage4" );
    }
	@Override
	public IIcon getIcon( int side, int meta ){
		return this.iconArray[meta];
	}
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int par2, int par3, int par4, int par5 ){
		return true;
	}
	@Override
	public int tickRate( World world ){
		return TradeBoothSettings.redstoneTransactionDuration;
	}
	@Override
	public int isProvidingWeakPower( IBlockAccess iBlockAccess, int par2, int par3, int par4, int par5 ){
		TileEntityTradeBoothStorage tileEntity = (TileEntityTradeBoothStorage) iBlockAccess.getTileEntity( par2, par3, par4 );
		if( tileEntity.providePower ){
			return 15;
		}
		else{
			return 0;
		}
	}
	@Override
	public int isProvidingStrongPower( IBlockAccess iBlockAccess, int par2, int par3, int par4, int par5 ){
		return 0;
	}
	@Override
	public boolean canProvidePower(){
        return true;
    }
	@Override
	public void updateTick( World world, int par2, int par3, int par4, Random random ){
		if( !world.isRemote ){
			TileEntityTradeBoothStorage tileEntity = (TileEntityTradeBoothStorage) world.getTileEntity( par2, par3, par4 );
			tileEntity.providePower = false;
			world.notifyBlockChange( par2, par3, par4, TradeBoothMod.blockTradeBoothStorage );
		}
	}
	@Override
	@SideOnly( Side.CLIENT )
	public void getSubBlocks( Item par1, CreativeTabs creativeTabs, List list){
		for( int i = 0; i < 5; i++ ){
			list.add( new ItemStack( this, 1, i ) );
		}
	}
	@Override
	public int damageDropped( int meta ){
        return meta;
    }

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		// TODO Auto-generated method stub
		return null;
	}
}
