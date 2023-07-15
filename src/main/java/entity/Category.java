package entity;

public class Category {

	private Integer category_id;
	private String categoryName;
	
	public Category() {};
	
	
	public Category(Integer category_id, String categoryName) {
		this.category_id = category_id;
		this.categoryName = categoryName;
	}


	//getter and setter
	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	@Override
	public String toString() {
		return "Id= " + category_id + ", categoryName=" + categoryName ;
	}
	
	//toString method
	
}
