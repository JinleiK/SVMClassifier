package struct;

import java.util.List;

/**
 * ��ȡ��ƽ������ݽṹ
 * @author Administrator 
 */
public class HyperPlane {
	//��ƽ��ķ�����
	private double [] verticalVector;
	//Lagrange����
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
