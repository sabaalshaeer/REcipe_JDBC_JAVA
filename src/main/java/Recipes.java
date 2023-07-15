import recipes.exception.DbException;
import recipes.service.RecipeService;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

import entity.Ingredient;
import entity.Recipe;
import entity.Unit;
import entity.Step;
import entity.Category;


public class Recipes {

	private RecipeService recipeService = new RecipeService();
	private Recipe curRecipe;

	// print operations menu
	public static void main(String[] args) {
		new Recipes().displayMenu();

	}

	private List<String> operations = List.of("1) Create and Populate all Tables", "2) Add a recipe", "3) List recipes",
			"4) Select working recipe", "5) Add ingredient to current Recipe", "6) Add step to current Recipe",
			"7) Add Category to current Recipe", "8) Modify Step In Current Recipe", "9) Delete Recipe"

	);

	// add input device(Scanner) which represent console input
	Scanner scanner = new Scanner(System.in);

	// this method to display the menu selections
	private void displayMenu() {
		boolean done = false;
		while (!done) {
			try {
				int operation = getOperation();
				switch (operation) {
				case -1:
					done = exitMenu();
					break;

				// create table
				case 1:
					createTables();
					break;

				// collect recipe details and save them in the recipe table
				case 2:
					addRecipe();
					break;

				// list recipes
				case 3:
					listRecipes();
					break;

				// Select working recipe
				case 4:
					setCurrentRecipe();
					break;

				// Add ingredient to current Recipe
				case 5:
					addIngredientToCurrentRecipe();
					break;

				// Add step to current Recipe
				case 6:
					addStepToCurrentRecipe();
					break;

				// Add Category to current Recipe
				case 7:
					addCategoryToCurrentRecipe();
					break;
					
					// MOdify step in current Recipe
				case 8:
					modifyStepInCurrentREcipe();
					break;
					
					//delete recipe
				case 9:
					deleteRcipe();
					break;

				default:
					System.out.println("\n" + operation + " is not a valid selection. Try again.");

				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.getMessage() + " Try again. ");
				e.printStackTrace();
			}
		}

	}

	private void deleteRcipe() {
		//call listRecipe method
		listRecipes();
		
		Integer recipe_id =getIntInput("Enter the Id of recipe to delete");
		
		if(Objects.nonNull(recipe_id)) {
			recipeService.deleteRecipe(recipe_id);
		}
		
		System.out.println("you have deleted recipe with Id "+ recipe_id);
		System.out.println("you still have these recipes: ");
		//re-call listRecipe method
				listRecipes();
				
		//if current recipe is not null and recipe id of the current recipe == to recipe id that been pass then set the current recipe to null
		if(Objects.nonNull(curRecipe) && curRecipe.getRecipe_id().equals(recipe_id) ){
			curRecipe = null;
		}
		
		
				
		
		
	}

	private void modifyStepInCurrentREcipe() {
		// if there is no recipe
				if (Objects.isNull(curRecipe)) {
					System.out.println("\nPlease select a recipe");
					return;
				}
				//get step
				List<Step> steps = recipeService.fetchSteps(curRecipe.getRecipe_id());
				
				//print a message
				System.out.println("\nSteps for current recipe");
				//print out the steps for current recipe
				steps.forEach(step-> System.out.println("   "+ step));
				
				//get user input
				Integer stepId = getIntInput("Enter step Id to modify");
				
				//check if step is not null
				if(Objects.nonNull(stepId)) {
					String stepText = getStringInput("Enter new step text");

					if(Objects.nonNull(stepText)) {
						Step step = new Step();
						//set the step id and step text
						step.setStep_id(stepId);
						step.setStepText(stepText);
						//modify step
					recipeService.modifyStep(step);
					curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipe_id());
					System.out.println(curRecipe);
				}
			}
		
	}

	private void addCategoryToCurrentRecipe() {
		// if there is no recipe
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
			return;
		}
		//get list of categories
		List<Category> categories = recipeService.fetchCategories();
		//print category's name
		categories.forEach(category -> System.out.println("  "+ category.getCategoryName()));
		//collect category name form user
		String category = getStringInput("Enter Category to add");
		
		if(Objects.nonNull(category)) {
			recipeService.addCategoryToRecipe(curRecipe.getRecipe_id(), category);
			curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipe_id());
			System.out.println(curRecipe);
		}
		
	}

	private void addStepToCurrentRecipe() {
		// if there is no recipe
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
			return;
		}
		// collect information for ingredient
		String stepText = getStringInput("Enter the step text");

		if (Objects.nonNull(stepText)) {
			Step step = new Step();

			step.setRecipe_id(curRecipe.getRecipe_id());
			step.setStepText(stepText);

			recipeService.addStep(step);
			curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipe_id());
			System.out.println(curRecipe);

		}

	}

	private void addIngredientToCurrentRecipe() {
		// if there is no recipe
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
			return;
		}
		// collect information for ingredient
		String name = getStringInput("Enter the ingredient name");
		String instruction = getStringInput("Enter an instruction");
		Double inputAmount = getDoubleInput("Enter an ingredient amount(Like .25)");
		List<Unit> units = recipeService.fetchUnits();

		// convert our double amount into BigDecimal amount
		BigDecimal amount = Objects.isNull(inputAmount) ? null : new BigDecimal(inputAmount).setScale(2);

		// print the units
		System.out.println("Units: ");
		units.forEach(unit -> System.out.println(
				"  " + unit.getUnit_id() + ": " + unit.getUnitNameSingular() + "(" + unit.getUnitNamePlural() + ")"));
		Integer unit_Id = getIntInput("Enter a unit id(press enter for none)");

		// create ingredient object
		Unit unit = new Unit();
		unit.setUnit_id(unit_Id);

		Ingredient ingredient = new Ingredient();
		ingredient.setRecipe_id(curRecipe.getRecipe_id());
		ingredient.setUnit(unit);
		ingredient.setIngredientName(name);
		ingredient.setInstruction(instruction);
		ingredient.setAmount(amount);

		recipeService.addIngredient(ingredient);
		curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipe_id());
		System.out.println(curRecipe);


	}

	private void setCurrentRecipe() {
		// print the list of recipes and return that list
		List<Recipe> recipes = listRecipes();

		// input the selected recipe Id
		Integer recipe_id = getIntInput("Select a recipe Id");

		curRecipe = null;

		for (Recipe recipe : recipes) {
			if (recipe.getRecipe_id().equals(recipe_id)) {
				curRecipe = recipeService.fetchRecipeById(recipe_id);
				System.out.println(curRecipe);
				break;
			}

		}
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe Id");
		}

	}

	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchRecipes();

		System.out.println("\nRecipes: ");

		recipes.forEach(recipe -> System.out.println("  " + recipe.getRecipe_id() + ": " + recipe.getRecipe_name()));

		return recipes;
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTable created and populated");

	}

	private int getOperation() {
		// call other method
		printOperations();
		Integer input = getIntInput("\nEnter a operation number");// pass a prompt
		return Objects.isNull(input) ? -1 : input;// the value -1 will signal the menu processing method to exit the
													// application
	}

	private void printOperations() {
		System.out.println();
		System.out.println("\nThere are the available selections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println("  " + line));

	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}

	private void addRecipe() {
		String name = getStringInput("Enter the name of the recipe");
		String notes = getStringInput("Enter the notes ");
		Integer numSreving = getIntInput("Enter the number of serving ");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time in minutes");

		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(cookMinutes);

		Recipe recipe = new Recipe();

		recipe.setRecipe_name(name);
		recipe.setNotes(notes);
		recipe.setNum_servings(numSreving);
		recipe.setPrep_time(prepTime);
		recipe.setCook_time(cookTime);

		// save the recipe
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You have successfully created recipe: \n" + dbRecipe);

		curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipe_id());
	}

	private LocalTime minutesToLocalTime(Integer numPrepTimeMin) {
		int min = Objects.isNull(numPrepTimeMin) ? 0 : numPrepTimeMin;
		int hours = min / 60;
		int minutes = min % 60;
		return LocalTime.of(hours, minutes);

	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
			throw new DbException(input + " not a valid number");
		}
	}

	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException nfe) {
			throw new DbException(input + " not a valid number");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt);
		String input = scanner.nextLine().trim();
		return input.isBlank() ? null : input.trim();
	}

}
