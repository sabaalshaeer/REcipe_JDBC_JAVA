package entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.*;

public class Recipe {
	private Integer recipe_id;
	private String recipe_name;
	private String notes;
	private Integer num_servings;
	private LocalTime prep_time;
	private LocalTime cook_time;
	private LocalDateTime create_at;
	
	private List<Ingredient> ingredients = new LinkedList<>();
	private List<Step> steps = new LinkedList<>();
	private List<Category> categories = new LinkedList<>();


	
	public Integer getRecipe_id() {
		return recipe_id;
	}
	public void setRecipe_id(Integer recipe_id) {
		this.recipe_id = recipe_id;
	}
	public String getRecipe_name() {
		return recipe_name;
	}
	public void setRecipe_name(String recipe_name) {
		this.recipe_name = recipe_name;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getNum_servings() {
		return num_servings;
	}
	public void setNum_servings(Integer num_servings) {
		this.num_servings = num_servings;
	}
	public LocalTime getPrep_time() {
		return prep_time;
	}
	public void setPrep_time(LocalTime prepTime) {
		this.prep_time = prepTime;
	}
	public LocalTime getCook_time() {
		return cook_time;
	}
	public void setCook_time(LocalTime cookTime) {
		this.cook_time = cookTime;
	}
	public LocalDateTime getCreate_at() {
		return create_at;
	}
	public void setCreate_at(LocalDateTime create_at) {
		this.create_at = create_at;
	}
	
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public List<Category> getCategories() {
		return categories;
	}
	
	
	@Override
	public String toString() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
		String createTime = Objects.nonNull(create_at) ? fmt.format(create_at) : "(null";
		
		String recipe = "";
		
		recipe += "\n   Id=" +recipe_id;
		recipe += "\n   recipe name=" + recipe_name;
		recipe += "\n   notes=" + notes;
		recipe += "\n   number of servings=" + num_servings;
		recipe += "\n   preper time=" + prep_time;
		recipe += "\n   cook time=" + cook_time;
		recipe += "\n   create at=" + createTime;
		
		recipe += "\n   Ingredients=" + ingredients;
		for(Ingredient ingredient : ingredients ) {
			recipe += "\n   " + ingredient;
		}
		
		recipe += "\n   steps=" + steps;
		for(Step step : steps ) {
			recipe += "\n   " + step;
		}
		
		recipe += "\n   Categories=" + categories;
		for(Category category : categories ) {
			recipe += "\n   " + category;
		}
		
		return recipe;
	}
	
	
	

}
