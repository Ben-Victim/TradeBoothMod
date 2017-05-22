package tradebooth.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import tradebooth.TradeBoothMod;
import tradebooth.TradeBoothSettings;
import tradebooth.packet.Packet1SetEnableCrafting;
import tradebooth.packet.Packet2SetRequireItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerJoinEvent{
	
	@SubscribeEvent
	public void onPlayerLogin( PlayerEvent.PlayerLoggedInEvent event ){
		if( !event.player.worldObj.isRemote ){
			TradeBoothMod.network.sendTo( new Packet1SetEnableCrafting( !TradeBoothSettings.disableCraftingRecipes ), (EntityPlayerMP) event.player );
			TradeBoothMod.network.sendTo( new Packet2SetRequireItemStack( TradeBoothSettings.requireItemStack ), (EntityPlayerMP) event.player );
		}
	}
}
