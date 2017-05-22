package tradebooth.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tradebooth.TradeBoothMod;
import tradebooth.block.BlockTradeBoothStorage;

public class ItemBlockTradeBoothTop extends ItemBlock{

	public ItemBlockTradeBoothTop( Block block ){
		super( block );
		this.setHasSubtypes( true );
	}
	@Override
	public int getMetadata( int meta ){
		return meta;
	}
	@Override
	public String getUnlocalizedName( ItemStack itemStack ){
		return TradeBoothMod.blockTradeBoothTop.getUnlocalizedName() + BlockTradeBoothStorage.woodType[itemStack.getItemDamage()];
	}
}
