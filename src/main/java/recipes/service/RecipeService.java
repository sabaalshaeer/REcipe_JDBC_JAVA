package recipes.service;

import recipes.dao.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import entity.Category;
import entity.Ingredient;
import entity.Recipe;
import entity.Step;
import entity.Unit;
import recipes.exception.*;

public class RecipeService {

	// read the schema
	private static final String SCHEMA_FILE = "Recipe_schema.sql";
	private static final String DATA_FILE = "recipe_data.sql";

	// create instance variable of type RecipeDao
	private RecipeDao recipeDao = new RecipeDao();

	// access Dao from the service using a method called createAndPopulateTables
	public void createAndPopulateTables() {
		// load the schema by calling this method
		LoadFromFile(SCHEMA_FILE);
		LoadFromFile(DATA_FILE);

	}

	private void LoadFromFile(String fileName) {
		// load from file is going to return string
		String content = readFileContent(fileName);
		// then convert it to SQl statement
		List<String> sqlStatements = convertContentToSqlStatements(content);

		sqlStatements.forEach(line -> System.out.println(line));
		// call recipeDao executeBatch method
		recipeDao.executeBatch(sqlStatements);
	}

	// find file recipe schema.sql and load it up as a single string
	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get("src", "main", "resources", fileName);
//			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	private List<String> convertContentToSqlStatements(String content) {
		// convert each statement to sql statement
		content = removeComments(content);
		content = replaceWhiteSpaceSequencesWithSingleSpace(content);

		return extractLinesFromContent(content);
	}

	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();
		while (!content.isEmpty()) {
			int semicolon = content.indexOf(";");
			// test if it is -1
			if (semicolon == -1) {
				if (!content.isBlank()) {
					lines.add(content);
				}
				content = "";
			} else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
			}
		}
		// System.out.println(lines);
		return lines;
	}

	private String replaceWhiteSpaceSequencesWithSingleSpace(String content) {
		return content.replaceAll("\\s+", " "); // this sign //S means looks for space
	}

	private String removeComments(String content) {
		StringBuilder sb = new StringBuilder(content);

		// counter
		int commentPos = 0;
		while ((commentPos = sb.indexOf("-- ", commentPos)) != -1) {
			int endOfLinePos = sb.indexOf("\n", commentPos + 1);
			if (endOfLinePos == -1) {
				sb.replace(commentPos, sb.length(), "");
			} else {
				sb.replace(commentPos, endOfLinePos + 1, "");
			}
		}

		return sb.toString();
	}

	public Recipe addRecipe(Recipe recipe) {
		return recipeDao.insertRecipe(recipe);
	}

	//apply sorting with stream
	public List<Recipe> fetchRecipes() {
		// @formatter:off
		return recipeDao.fetchAllRecipes()
				.stream()     //creation method
				.sorted((r1, r2) -> r1.getRecipe_id() - r2.getRecipe_id())  //intermediate method
				.collect(Collectors.toList());  //termination method
		// @formatter:on

	}

	public Recipe fetchRecipeById(Integer recipe_id) {
		return recipeDao.fetchAllRecipeById(recipe_id)
				.orElseThrow(() -> new NoSuchElementException("Recipe with Id = " + recipe_id + "does not exist."));
	}

	public static void main(String[] args) {
		new RecipeService().createAndPopulateTables();
	}

	public List<Unit> fetchUnits() {
		return recipeDao.fetchAllUnits();
	}

	public void addIngredient(Ingredient ingredient) {
		recipeDao.addIngredientToRecipe(ingredient);
		
	}

	public void addStep(Step step) {
		recipeDao.addStepToRecipe(step);
		
	}

	public List<Category> fetchCategories() {
		return recipeDao.fetchAllCategories();
	}

	public void addCategoryToRecipe(Integer recipe_id, String category) {
		recipeDao.addCategorytoRecipe(recipe_id, category);
		
	}

	public List<Step> fetchSteps(Integer recipe_id) {
		return recipeDao.fetchStepsforRecipe(recipe_id);
	}

	public void modifyStep(Step step) {
		//if modification is not successful
		if(!recipeDao.modifyRecipeStep(step)) {
			throw new DbException("Step with Id=" + step.getStep_id()+ " does not exist.");
		}
		
	}

	public void deleteRecipe(Integer recipe_id) {
		if(!recipeDao.deleteRecipe(recipe_id)){
			throw new DbException("Recipe with Id=" + recipe_id+ " does not exist.");
		}
		
	}

}
