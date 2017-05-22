package tradebooth.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tradebooth.CommonProxy;
import tradebooth.TradeBoothMod;
import tradebooth.TradeBoothSettings;
import tradebooth.packet.Packet0SetPlayerName;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTradeBoothTop extends BlockContainer{
	
	public static IIcon[] iconArray = new IIcon[5];
	
	public BlockTradeBoothTop(){
		super( Material.wood );
		this.setHardness( 5.0F );
		this.setResistance( TradeBoothSettings.explosionResistance );
		this.setBlockName( "blockTradeBoothTop" );
	}
	
	@Override
	public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, int var1, float var2, float var3, float var4 ){
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( tileEntity == null || player.isSneaking() ){
			return false;
		}
		else if( tileEntity instanceof TileEntityTradeBoothTop ){
			TileEntityTradeBoothTop tradeBoothTop = (TileEntityTradeBoothTop) tileEntity;
			String playerOwnerName = tradeBoothTop.getPlayerOwner();
			if( playerOwnerName == null || playerOwnerName.isEmpty() ){ //If this TradeBooth is unowned
				tradeBoothTop.setPlayerOwner( player.getDisplayName() );
				if( !world.isRemote ){ //Server
					TradeBoothMod.network.sendToAllAround( new Packet0SetPlayerName( playerOwnerName, x, y, z ), new TargetPoint( player.dimension, x, y, z, 220 ) );
				}
			}
			if( tradeBoothTop.getPlayerOwner().equals( player.getDisplayName() ) || player.capabilities.isCreativeMode ){
				player.openGui( TradeBoothMod.instance, 1, world, x, y, z );	//Owner's GUI
				return true;
			}
			else{
				player.openGui( TradeBoothMod.instance, 2, world, x, y, z ); //Non-owner's GUI
				return true;
			}
		}
		else{
			return true;
		}
	}
	
	@Override
	public void breakBlock( World world, int x, int y, int z, Block par5, int par6 ){
		
		this.dropItems( world, x, y, z );
		super.breakBlock( world, x, y, z, par5, par6 );
	}
	private void dropItems( World world, int x, int y, int z ){
		Random rand = new Random();
		
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( !( tileEntity instanceof TileEntityTradeBoothTop ) ){
			return;
		}
		TileEntityTradeBoothTop tradeBoothTop = (TileEntityTradeBoothTop) tileEntity;
		
		for( int i = 0; i < tradeBoothTop.getSizeInventory(); i++ ){
			if( TradeBoothSettings.requireItemStack || i == 16 ){ //16 is the cover slot
				ItemStack itemStack = tradeBoothTop.getStackInSlot( i );
				
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
	}

	@Override
	public TileEntity createTileEntity( World var1, int meta ) {
		return new TileEntityTradeBoothTop();
	}
	@Override
	public boolean hasTileEntity( int metadata ){
		return true;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness( EntityPlayer player, World world, int x, int y, int z ){
		TileEntity tileEntity = world.getTileEntity( x, y, z );
		if( tileEntity != null && tileEntity instanceof TileEntityTradeBoothTop ){
			TileEntityTradeBoothTop tradeBoothTop = (TileEntityTradeBoothTop) tileEntity;
			String ownerName = tradeBoothTop.getPlayerOwner();
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
        //this.blockIcon = iconRegister.registerIcon( "tradebooth:tradeboothtopside" );
		for( int i = 0; i < 5; i++ ){
			this.iconArray[i] = iconRegister.registerIcon( "tradebooth:tradeboothtop" + i );
		}
    }
	@Override
	public IIcon getIcon( int side, int meta ){
		return this.iconArray[meta];
	}
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	@Override 
	public boolean isOpaqueCube(){
		return false;
	}
	@Override
	public int getRenderType(){
		return TradeBoothMod.BoothTopRenderID;
	}
	@Override
    public ArrayList<ItemStack> getDrops( World world, int x, int y, int z, int metadata, int fortune ){
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        
        ret.add( new ItemStack( TradeBoothMod.itemTradeBoothTop, 1, metadata ) );
        
        return ret;
    }
	public void setBlockBoundsForItemRender(){
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
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