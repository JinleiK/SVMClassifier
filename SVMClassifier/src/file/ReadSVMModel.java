package file;

import global.GlobalVariable;
import global.svset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import struct.HyperPlane;
import struct.PageInfo;

/**
 * 读取SVM模型文件
 * @author Administrator
 *
 */
public class ReadSVMModel {
	/**
	 * 转化
	 * @param tempStrLagrange
	 * @return
	 */
	public double[] transformLagrange(String[] tempStrLagrange)
	{
		svset.svAndEpage=new ArrayList<PageInfo>();
		svset.nsvAndYpage=new ArrayList<PageInfo>();
		
		//Lagrange乘子
		double[] tempLagrange=new double[GlobalVariable.webPageNumber];
		 for(int i=0;i<tempStrLagrange.length;i++)
			{
				tempLagrange[i]=Double.parseDouble(tempStrLagrange[i]);
			}
		 return tempLagrange;
	}
	/**
	 * 转化
	 * @param tempStrVerticalVector
	 * @return
	 */
	public double[] transformVerticalVector(String[] tempStrVerticalVector)
	{
		//超平面的法向量
	    double [] tempVerticalVector=new double[GlobalVariable.vectorNumber];
	    for(int i=0;i<tempStrVerticalVector.length;i++)
		{
			tempVerticalVector[i]=Double.parseDouble(tempStrVerticalVector[i]);
		}
	    return tempVerticalVector;
	}
	/**
	 * 读取SVM模型文件
	 * @param fileSVMModel SVM模型文件
	 * @return 超平面列表  SVM模型文件里放入了多个超平面
	 */
	public List<HyperPlane> getSVMModel(File fileSVMModel)
	{
		//定义临时变量
		//超平面的法向量
	     String [] tempStrVerticalVector=new String[GlobalVariable.vectorNumber];
		//Lagrange乘子
		 String[] tempStrLagrange=new String[GlobalVariable.webPageNumber];
		 double tempIntercept=0.0;//b
		 
		//定义临时变量,将字符串型的超平面的Lagrange和法向量转化为double型的超平面的Lagrange和法向量
		//超平面的法向量
		 double [] tempVerticalVector=new double[GlobalVariable.vectorNumber];
		//Lagrange乘子
	     double[] tempLagrange=new double[GlobalVariable.webPageNumber];
		 
		//定义存储超平面的list
		 List<HyperPlane>listHyperPlane=new ArrayList<HyperPlane>();
		 
		 //读取SVM模型
		FileInputStream inputStream;
		try {
			//获得输入流
			inputStream = new FileInputStream(fileSVMModel);
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			
			String strLine;
			try {
				strLine = bufferedReader.readLine();//读取一行数据
				while(strLine!=null)
				{
					//获得Lagrange
					if(strLine.trim().equals("超平面的Lagrange"))
					{
						strLine=bufferedReader.readLine();
						tempStrLagrange=strLine.split(" ");
					}
					
					//获得超平面的向量
					if(strLine.trim().equals("超平面的法向量"))
					{
						strLine=bufferedReader.readLine();
						tempStrVerticalVector=strLine.split(" ");
					}
					
					//获得b值
					if(strLine.trim().equals("超平面的b值"))
					{
						strLine=bufferedReader.readLine();
						tempIntercept=Double.parseDouble(strLine);
						
						//读完一个超平面
						//进行转化
						tempVerticalVector=transformVerticalVector(tempStrVerticalVector);
						tempLagrange=transformVerticalVector(tempStrLagrange);
						
						//定义一个超平面 ，记录获得的超平面
						HyperPlane hyperPlane=new HyperPlane();
						hyperPlane.setIntercept(tempIntercept);
						hyperPlane.setLagrange(tempLagrange);
						hyperPlane.setVerticalVector(tempVerticalVector);
						
						//将超平面加入到list中
						listHyperPlane.add(hyperPlane);
					}
					strLine=bufferedReader.readLine();//读取下一行
				}
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listHyperPlane;
	}
}
