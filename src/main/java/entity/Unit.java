package entity;

public class Unit {

	private Integer unit_id;
	private String unitNameSingular;
	private String unitNamePlural;
	
	public Integer getUnit_id() {
		return unit_id;
	}
	public void setUnit_id(Integer unit_id) {
		this.unit_id = unit_id;
	}
	public String getUnitNameSingular() {
		return unitNameSingular;
	}
	public void setUnitNameSingular(String unitNameSingular) {
		this.unitNameSingular = unitNameSingular;
	}
	public String getUnitNamePlural() {
		return unitNamePlural;
	}
	public void setUnitNamePlural(String unitNamePlural) {
		this.unitNamePlural = unitNamePlural;
	}
	
	@Override
	public String toString() {
		return "Id=" + unit_id + ", unitNameSingular=" + unitNameSingular + ", unitNamePlural="
				+ unitNamePlural ;
	}

}
