package entity;

public class Step {
	private Integer step_id;
	private Integer recipe_id;
	private Integer stepOrder;
	private String stepText;
	
	

	public Integer getStep_id() {
		return step_id;
	}
	public void setStep_id(Integer step_id) {
		this.step_id = step_id;
	}
	public Integer getRecipe_id() {
		return recipe_id;
	}
	public void setRecipe_id(Integer recipe_id) {
		this.recipe_id = recipe_id;
	}
	public Integer getStepOrder() {
		return stepOrder;
	}
	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}
	public String getStepText() {
		return stepText;
	}
	public void setStepText(String stepText) {
		this.stepText = stepText;
	}
	
	@Override
	public String toString() {
		return "Id=" + step_id + ", stepText=" + stepText;
	}
	

}
