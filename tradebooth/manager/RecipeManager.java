package tradebooth.manager;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import tradebooth.TradeBoothMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

public class RecipeManager {
	public static void registerRecipes(){
		for( int i = 0; i < 4; i++ ){
			GameRegistry.addRecipe( new ItemStack( TradeBoothMod.blockTradeBoothStorage, 1, i ), new Object[]{ "aaa", "aba", "aaa", Character.valueOf( 'a' ), new ItemStack( Blocks.planks, 1, i ), Character.valueOf( 'b' ), new ItemStack( Items.iron_ingot ) } );
			GameRegistry.addRecipe( new ItemStack( TradeBoothMod.itemTradeBoothTop, 1, i ), new Object[]{ "aaa", "a a", "aba", Character.valueOf( 'a' ), new ItemStack( Blocks.planks, 1, i ), Character.valueOf( 'b' ), new ItemStack( Items.gold_nugget ) } );
		}
	}
}
