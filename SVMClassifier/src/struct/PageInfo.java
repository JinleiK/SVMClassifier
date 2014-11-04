package struct;

import java.util.List;

public class PageInfo {
	private int type;//网页所属的类别
	private List<Double> eigenvalue; //特征值
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Double> getEigenvalue() {
		return eigenvalue;
	}
	public void setEigenvalue(List<Double> eigenvalue) {
		this.eigenvalue = eigenvalue;
	}
	
	
	
	
}
