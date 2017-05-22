package tradebooth;

import tradebooth.ItemBlock.ItemBlockTradeBoothStorage;
import tradebooth.ItemBlock.ItemBlockTradeBoothTop;
import tradebooth.block.BlockTradeBoothStorage;
import tradebooth.block.BlockTradeBoothTop;
import tradebooth.event.PlayerJoinEvent;
import tradebooth.handler.GuiHandler;
import tradebooth.item.ItemTradeBoothTop;
import tradebooth.manager.RecipeManager;
import tradebooth.packet.Packet0SetPlayerName;
import tradebooth.packet.Packet1SetEnableCrafting;
import tradebooth.packet.Packet2SetRequireItemStack;
import tradebooth.packet.Packet3RequestTileEntityData;
import tradebooth.packet.Packet4SetWoolSlot;
import tradebooth.packet.Packet5RequestWoolSlot;
import tradebooth.tileentity.TileEntityTradeBoothStorage;
import tradebooth.tileentity.TileEntityTradeBoothTop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod( modid=TradeBoothMod.MODID, version=TradeBoothMod.VERSION )

public class TradeBoothMod {
	
	public static final String MODID = "tradeboothmod";
	public static final String VERSION = "1.7.10.2";
	
	public static FMLEventChannel Channel;
	
	public static SimpleNetworkWrapper network;
	
	@Instance( "TradeBoothMod" )
	public static TradeBoothMod instance;

	public static Block blockTradeBoothStorage;
	public static Block blockTradeBoothTop;
	
	public static Item itemTradeBoothTop;
	
	public static int BoothTopRenderID;
	
	@SidedProxy( clientSide= "tradebooth.ClientProxy", serverSide = "tradebooth.CommonProxy" )
	public static CommonProxy commonProxy;
	
	@EventHandler
	public void preInit( FMLPreInitializationEvent event ){
		Configuration config = new Configuration( event.getSuggestedConfigurationFile() );
		TradeBoothSettings.config( config );
		config.save();
		TradeBoothMod.network = NetworkRegistry.INSTANCE.newSimpleChannel("TradeBoothChan");
		TradeBoothMod.network.registerMessage( Packet0SetPlayerName.Handler.class, Packet0SetPlayerName.class, 0, Side.CLIENT );
		TradeBoothMod.network.registerMessage( Packet1SetEnableCrafting.Handler.class, Packet1SetEnableCrafting.class, 1, Side.CLIENT );
		TradeBoothMod.network.registerMessage( Packet2SetRequireItemStack.Handler.class, Packet2SetRequireItemStack.class, 2, Side.CLIENT );
		TradeBoothMod.network.registerMessage( Packet3RequestTileEntityData.Handler.class, Packet3RequestTileEntityData.class, 3, Side.SERVER );
		TradeBoothMod.network.registerMessage( Packet4SetWoolSlot.Handler.class, Packet4SetWoolSlot.class, 4, Side.CLIENT );
		TradeBoothMod.network.registerMessage( Packet5RequestWoolSlot.Handler.class, Packet5RequestWoolSlot.class, 5, Side.SERVER );
	}
		
	@EventHandler
	public void init( FMLInitializationEvent event ){
		TradeBoothMod.instance = this;
		
		blockTradeBoothStorage = new BlockTradeBoothStorage();
		blockTradeBoothTop = new BlockTradeBoothTop();
		itemTradeBoothTop = new ItemTradeBoothTop();
		
		GameRegistry.registerBlock( blockTradeBoothStorage, ItemBlockTradeBoothStorage.class, blockTradeBoothStorage.getUnlocalizedName().substring( 5 ) );
		GameRegistry.registerBlock( blockTradeBoothTop, ItemBlockTradeBoothTop.class, blockTradeBoothTop.getUnlocalizedName().substring( 5 ) );
		GameRegistry.registerTileEntity( TileEntityTradeBoothStorage.class, "tileEntityTradeBoothStorage" );
		GameRegistry.registerTileEntity( TileEntityTradeBoothTop.class, "tileEntityTradeBoothTop" );
		
		GameRegistry.registerItem( itemTradeBoothTop, "itemTradeBoothTop" );
		
		NetworkRegistry.INSTANCE.registerGuiHandler( this, new GuiHandler() );
		TradeBoothMod.Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel( "tradeboothmod" );
		
		blockTradeBoothStorage.setHarvestLevel( "axe", 0 );
		blockTradeBoothTop.setHarvestLevel( "axe", 0 );
		
		this.commonProxy.load();
		
		if( !TradeBoothSettings.disableCraftingRecipes ){
			RecipeManager.registerRecipes();
		}
		FMLCommonHandler.instance().bus().register( new PlayerJoinEvent() );
	}
}