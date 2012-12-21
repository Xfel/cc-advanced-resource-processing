/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class AccessibleRecipe {

	public static AccessibleRecipe fromShapedRecipe(ShapedRecipes recipe) {
		ItemStack[] inputStacks = ReflectionHelper.getPrivateValue(
				ShapedRecipes.class, recipe, "recipeItems","d");
		int width = ReflectionHelper.getPrivateValue(ShapedRecipes.class,
				recipe, "width","b");

		ItemKey[] input = new ItemKey[9];

		int targetIndex = 0;
		for (int i = 0; i < inputStacks.length; i++) {
			if (inputStacks[i] != null) {
				ItemKey key = new ItemKey(inputStacks[i]);

				input[targetIndex] = key;
			}
			targetIndex++;
			if (targetIndex % 3 == width) {
				targetIndex = (i / width) * 3;
			}
		}

		return new AccessibleRecipe(RecipeType.CRAFTING, new ItemKey(
				recipe.getRecipeOutput()), recipe.getRecipeOutput().stackSize,
				input, false);

	}

	private RecipeType recipeType;

	private ItemKey output;

	private int outputAmount;

	private ItemKey[] input;

	private boolean shapeless;

	public AccessibleRecipe(RecipeType recipeType, ItemKey output,
			int outputAmount, ItemKey[] input, boolean shapeless) {
		super();
		this.recipeType = recipeType;
		this.output = output;
		this.outputAmount = outputAmount;
		this.input = input;
		this.shapeless = shapeless;
	}

	public RecipeType getRecipeType() {
		return recipeType;
	}

	public ItemKey getOutput() {
		return output;
	}

	public int getOutputAmount() {
		return outputAmount;
	}

	public ItemKey[] getInput() {
		return input;
	}

	public boolean isShapeless() {
		return shapeless;
	}

}
