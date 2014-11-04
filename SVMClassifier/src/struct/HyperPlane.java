package struct;

import java.util.List;

/**
 * 存取超平面的数据结构
 * @author Administrator 
 */
public class HyperPlane {
	//超平面的法向量
	private double [] verticalVector;
	//Lagrange乘子
	private double[] lagrange;
	private double intercept;//b
	public double[] getVerticalVector() {
		return verticalVector;
	}
	public void setVerticalVector(double[] verticalVector) {
		this.verticalVector = verticalVector;
	}
	public double[] getLagrange() {
		return lagrange;
	}
	public void setLagrange(double[] lagrange) {
		this.lagrange = lagrange;
	}
	public double getIntercept() {
		return intercept;
	}
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}
	
	
}
