package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Ingredient extends EntityBase{

	private Integer ingredient_id;
	private Integer recipe_id;
	private Unit unit;
	private String ingredientName;
	private String instruction;
	private Integer ingredientOrder;
	private BigDecimal amount;
	
	//getter and setter
	public Integer getIngredient_id() {
		return ingredient_id;
	}
	public void setIngredient_id(Integer ingredient_id) {
		this.ingredient_id = ingredient_id;
	}
	public Integer getRecipe_id() {
		return recipe_id;
	}
	public void setRecipe_id(Integer recipe_id) {
		this.recipe_id = recipe_id;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public Integer getIngredientOrder() {
		return ingredientOrder;
	}
	public void setIngredientOrder(Integer ingredientOrder) {
		this.ingredientOrder = ingredientOrder;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	/**
	 * print like : Id =5: 1/4 cup carrots, thinly sliced.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Id").append(ingredient_id).append(": ");
		sb.append(toFraction(amount));
		
		if(Objects.nonNull(unit) && Objects.nonNull(unit.getUnit_id())) {
			String singular = unit.getUnitNameSingular();
			String plural = unit.getUnitNamePlural();
			
			String word = amount.compareTo(BigDecimal.ONE) > 0 ? plural : singular;
			
			sb.append(word).append(" ");
		}
		
		sb.append(ingredientName);
		if(Objects.nonNull(instruction)) {
			sb.append(", ").append(instruction);
		}
		
		return sb.toString();
	}
	
	
	

}
