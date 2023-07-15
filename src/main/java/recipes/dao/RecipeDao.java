package recipes.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import entity.*;
import recipes.exception.DbException;
import util.DaoBase;

public class RecipeDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String Recipe_TABLE = "recipe";
	private static final String Recipe_CATEGORY_TABLE = "recipe_category";
	private static final String STEP_TABLE = "step";
	private static final String UNIT_TABLE = "unit";

	public Optional<Recipe> fetchAllRecipeById(Integer recipe_id) {
		String sql = "SELECT * FROM " + Recipe_TABLE + " WHERE recipe_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try {
				Recipe recipe = null;
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, recipe_id, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							recipe = extract(rs, Recipe.class);
						}
					}
				}
				if (Objects.nonNull(recipe)) {
					recipe.getIngredients().addAll(fetchRecipeIngredients(conn, recipe_id));
					recipe.getSteps().addAll(fetchRecipeSteps(conn, recipe_id));
					recipe.getCategories().addAll(fetchRecipeCategories(conn, recipe_id));
				}
				return Optional.ofNullable(recipe);
				
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Ingredient> fetchRecipeIngredients(Connection conn, Integer recipe_id) throws SQLException {
		// @formatter:off
		String sql =""
				+ "SELECT ingredient.*, u.unit_name_singular, u.unit_name_plural FROM " + INGREDIENT_TABLE
				+ " LEFT JOIN " + UNIT_TABLE + " u USING (unit_id) "
				+ "WHERE recipe_id = ? "
				+ "ORDER BY ingredient.ingredient_order";
		// @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, recipe_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Ingredient> ingredients = new LinkedList<>();

				while (rs.next()) {
					Ingredient ingredient = extract(rs, Ingredient.class);
					Unit unit = extract(rs, Unit.class);
					
					ingredient.setUnit(unit);
					ingredients.add(ingredient);
				}
				return ingredients;
			}
		}

	}
	
	private List<Step> fetchRecipeSteps(Connection conn, Integer recipe_id) throws SQLException {
		String sql = "SELECT * FROM "+STEP_TABLE +" WHERE recipe_id = ?" ;

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, recipe_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}

	}
	
	private List<Category> fetchRecipeCategories(Connection conn, Integer recipe_id) throws SQLException {
		// @formatter:off
				String sql =""
						+ "SELECT c.* "+ "FROM " + Recipe_CATEGORY_TABLE + " rc "
						+ "JOIN " + CATEGORY_TABLE + " c USING (category_id) "
						+ "WHERE recipe_id = ? "
						+ "ORDER BY c.category_name";
				// @formatter:on


		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, recipe_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}

	}

	public List<Recipe> fetchAllRecipes() {
		String sql = "SELECT * FROM " + Recipe_TABLE + " ORDER BY recipe_name";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Recipe> recipes = new LinkedList<>();

					while (rs.next()) {
						recipes.add(extract(rs, Recipe.class));
					}
					return recipes;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Recipe insertRecipe(Recipe recipe) {
		// write sql statement
		// @Formatter:off
		String sql = "" + "INSERT INTO " + Recipe_TABLE + " "
				+ "(recipe_name, notes, num_servings, prep_time, cook_time) " + "VALUES " + "(?, ?, ?, ?, ?)";
		// @Formatter:on

		// try-with-resource
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// set project details as parameters in the PreparedStatement
				setParameter(stmt, 1, recipe.getRecipe_name(), String.class);
				setParameter(stmt, 2, recipe.getNotes(), String.class);
				setParameter(stmt, 3, recipe.getNum_servings(), Integer.class);
				setParameter(stmt, 4, recipe.getPrep_time(), LocalTime.class);
				setParameter(stmt, 5, recipe.getCook_time(), LocalTime.class);

				// save the project details
				stmt.executeUpdate();

				// calling the convenience method to allows to us to generate the projectId
				Integer recipe_id = getLastInsertId(conn, Recipe_TABLE);
				commitTransaction(conn);

				// set projectId
				recipe.setRecipe_id(recipe_id);
				return recipe;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	public List<Unit> fetchAllUnits() {
		String sql ="SELECT * FROM " + UNIT_TABLE+ " ORDER BY unit_name_singular";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet rs = stmt.executeQuery()){
					List<Unit> units = new LinkedList<>();
				
					while(rs.next()) {
						units.add(extract(rs, Unit.class));
					}
					return units;
				}
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
	}

	public void addIngredientToRecipe(Ingredient ingredient) {
		String sql = "INSERT INTO "+ INGREDIENT_TABLE
				+ " (recipe_id , unit_id, ingredient_name, instruction, ingredient_order, amount)"
				+" VALUES (?, ?, ?, ?, ?, ?)";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try {//get ingredient order-getting the number of child rows and add 1 to it that is our next inserting position
				Integer order = getNextSequenceNumber(conn, ingredient.getRecipe_id(), INGREDIENT_TABLE, "recipe_id");
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, ingredient.getRecipe_id(), Integer.class);
					setParameter(stmt, 2, ingredient.getUnit().getUnit_id(), Integer.class);
					setParameter(stmt,3, ingredient.getIngredientName(), String.class);
					setParameter(stmt, 4, ingredient.getInstruction(), String.class);
					setParameter(stmt, 5, order , Integer.class);
					setParameter(stmt, 6, ingredient.getAmount(),BigDecimal.class);

					stmt.executeUpdate();
					commitTransaction(conn);


				}
			}catch(Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new DbException(e);

		}
		
	}
	
	public void addStepToRecipe(Step step) {
		String sql = "INSERT INTO "+ STEP_TABLE + " (recipe_id, step_order, step_text)"
				+" VALUES(?, ?, ?)";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			Integer order = getNextSequenceNumber(conn, step.getStep_id(), STEP_TABLE, "recipe_id");
			
			try(PreparedStatement stmt= conn.prepareStatement(sql)){
				setParameter(stmt, 1, step.getRecipe_id(), Integer.class);
				setParameter(stmt, 2, order, Integer.class);
				setParameter(stmt, 3, step.getStepText(), String.class);

				stmt.executeUpdate();
				commitTransaction(conn);
			}
			catch(Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}
		catch(SQLException e) {
			throw new DbException(e);			
			}
		
	}
	
	public List<Category> fetchAllCategories() {
		String sql = "SELECT * FROM "+ CATEGORY_TABLE+ " Order BY category_name";
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet rs = stmt.executeQuery()){
					List<Category> categories = new LinkedList<>();

					while(rs.next()) {
						Category category = new Category(
								rs.getInt("category_id"),
								rs.getString("category_name"));
						categories.add(category);
								
						//categories.add(extract(rs, Category.class));
					}
					return categories;
				}

				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}
	
	public void addCategorytoRecipe(Integer recipe_id, String category) {
		//subquery
		String subQuery = "(SELECT category_id FROM " + CATEGORY_TABLE + " Where category_name = ?)";
		//sql statement
		String sql ="Insert Into "+ Recipe_CATEGORY_TABLE + " (recipe_id, category_id) VALUES(? , " + subQuery + ")";
		
		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				//set parameter
				stmt.setInt(1, recipe_id);
				stmt.setString(2, category);
//				setParameter(stmt, 1, recipe_id, Integer.class);
//				setParameter(stmt, 2, category, String.class);
				
				stmt.executeUpdate();
				commitTransaction(conn);
				
			}catch (Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			
		}catch (SQLException e) {
			throw new DbException(e);
		}
		
	}
	
	public List<Step> fetchStepsforRecipe(Integer recipe_id) {
		try(Connection conn= DbConnection.getConnection()){
			startTransaction(conn);
			try{//we already have a method to fetch recipe's step, we can call it
				List<Step> steps = fetchRecipeSteps(conn, recipe_id);
				commitTransaction(conn);
				
				return steps;
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			throw new DbException(e);
		}
		
	}

	public boolean modifyRecipeStep(Step step) {
		String sql ="UPDATE "+ STEP_TABLE+ " SET step_text =? WHERE step_id =?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, step.getStepText(), String.class);
				setParameter(stmt, 2, step.getStep_id(), Integer.class);
				
				//if updating works successfully we get 1 
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updated;
				
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteRecipe(Integer recipe_id) {
		String sql ="DELETE FROM "+ Recipe_TABLE+ " WHERE recipe_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, recipe_id, Integer.class);
				
				//if updating works successfully we get 1 
				boolean deleted = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return deleted;
				
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}
	
	public void executeBatch(List<String> sqlBatch) {
		try (Connection conn = DbConnection.getConnection()) {
			// start transaction
			startTransaction(conn);

			// create statement
			try (Statement stmt = conn.createStatement()) {
				// loop through lines that we pass in sql batch lines and add them to a batch
				// statement
				for (String sql : sqlBatch) {
					// add each sql statement as a batch to the statement
					stmt.addBatch(sql);
				}
				// execute statement
				stmt.executeBatch();
				// commit transaction
				commitTransaction(conn);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	

	

	

	

	

	
	

}
