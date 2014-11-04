package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import struct.HyperPlane;
import struct.PageInfo;
import train.Train;
import file.ReadSVMModel;
import file.ReadTrainingSet;
import global.Evaluation;
import global.GlobalVariable;
import global.svset;

/**
 * 测试网页所属类别
 * @author Administrator 
 */
public class TestWebPageSort {
	/**
	 * 计算点积
	 * @param listEigenValue 网页的特征向量
	 * @param vetricalVector 超平面的法向量
	 * @return
	 */
	public double calculateDotMetrix(List<Double>listEigenValue,double[]vetricalVector)
	{
		Double defineValue=0.0;
		for(int w=0;w<GlobalVariable.vectorNumber;w++)
		{
			defineValue+=listEigenValue.get(w)*vetricalVector[w];
		}
		return defineValue;
	}
	/**
	 * 计算被测试网页与一个超平面的决策函数值确定被测网页属于哪一个类别
	 * @param webPage 被测试的网页
	 * @param fileSVMModel SVM模型文件
	 * @return 属于哪一个类别的标号
	 */
	public int[] getWebPageSort(File fileWebPageSet,File fileSVMModel)
	{
		//获得被测网页的特征向量
		ReadTrainingSet readTestSet=new ReadTrainingSet();
		List<PageInfo> testListPageInfo=readTestSet.getTrainingSet(fileWebPageSet);
		
		//获得SVM模型
		ReadSVMModel readSVMModel=new ReadSVMModel();
		List<HyperPlane>listHyperPlane=readSVMModel.getSVMModel(fileSVMModel);
		
		//定义一个超平面,一个特征向量的list
		HyperPlane hyperPlane=new HyperPlane();
		List<Double>listEigenValue=new ArrayList<Double>();
		//定义数组，存储W
		double []vetricalVector=new double[GlobalVariable.vectorNumber];
		//定义两个临时变量，存储决策函数值
		double defineValue=0.0;
		double maxDefineValue=-1000000000.0;
	
		//定义一个类别数组，记录测试网页的类别
		int []sort=new int[testListPageInfo.size()];
		
		//进行测试
		//将一个网页与所有超平面计算，获得其决策函数的值，取函数值最大对应的类别为网页的类别
		for(int i=0;i<testListPageInfo.size();i++)
		{
			listEigenValue=testListPageInfo.get(i).getEigenvalue();	//获得该网页的特征向量值
			maxDefineValue=-1000000000.0;//当测试一个新的网页时，其最大决策函数值应该赋给一个很小的值
			
			for( int j=0;j<listHyperPlane.size();j++)
			{
				defineValue=0.0;//决策函数值清0
				//获得该超平面的法向量
				vetricalVector=listHyperPlane.get(j).getVerticalVector();
				//计算网页的特征值和法向量之间的点积
				defineValue=calculateDotMetrix(listEigenValue,vetricalVector);
				//加上b值
				defineValue+=listHyperPlane.get(j).getIntercept();
				//决策函数值最大所对应的那个类别为测试网页的类别
				if(defineValue>maxDefineValue)
				{
					maxDefineValue=defineValue;
					//将类别赋给sort数组
					sort[i]=j+1;
				}
			}
		}
			
		return sort;
	}
	public int[] getIncreamentalWebPageSort(List<PageInfo> testListPageInfo,File fileSVMModel)
	{
		//获得SVM模型
		ReadSVMModel readSVMModel=new ReadSVMModel();
		List<HyperPlane>listHyperPlane=readSVMModel.getSVMModel(fileSVMModel);
		
		//定义一个超平面,一个特征向量的list
		HyperPlane hyperPlane=new HyperPlane();
		List<Double>listEigenValue=new ArrayList<Double>();
		//定义数组，存储W
		double []vetricalVector=new double[GlobalVariable.vectorNumber];
		//定义两个临时变量，存储决策函数值
		double defineValue=0.0;
		double maxDefineValue=-1000000000.0;
	
		//定义一个类别数组，记录测试网页的类别
		int []sort=new int[testListPageInfo.size()];
		
		//进行测试
		//将一个网页与所有超平面计算，获得其决策函数的值，取函数值最大对应的类别为网页的类别
		for(int i=0;i<testListPageInfo.size();i++)
		{
			listEigenValue=testListPageInfo.get(i).getEigenvalue();	//获得该网页的特征向量值
			maxDefineValue=-1000000000.0;//当测试一个新的网页时，其最大决策函数值应该赋给一个很小的值
			
			for( int j=0;j<listHyperPlane.size();j++)
			{
				defineValue=0.0;//决策函数值清0
				//获得该超平面的法向量
				vetricalVector=listHyperPlane.get(j).getVerticalVector();
				//计算网页的特征值和法向量之间的点积
				defineValue=calculateDotMetrix(listEigenValue,vetricalVector);
				defineValue+=listHyperPlane.get(j).getIntercept();
				//决策函数值最大所对应的那个类别为测试网页的类别
				if(defineValue>maxDefineValue)
				{
					maxDefineValue=defineValue;
					//将类别赋给sort数组
					sort[i]=j+1;
				}
			}
		}
			
		return sort;
	}
	/**
	 * 计算查准率和查全率
	 * @param eachSortNumber 存储测试集中每个类别的网页数目，该数组的长度为GlobalVariable.sortNumber
	 * @param sortResult 分类结果
	 * @return 一个二维数组，行号表示类别数，第一列表示查准率，第二列表示查全率
	 */
	public double [][] calculatePrecisionAndRecall(int[] eachSortNumber,int[]sortResult,Boolean bool)
	{
		double [][]precisionAndRecall=new double [GlobalVariable.sortNumber][2];//存储准确率和查全率
		//定义临时变量
		int j=0;
		int factAndPrecision[]=new int[GlobalVariable.sortNumber];//事实属于此类且被分类正确的网页数
		int judgeSort[]=new int[GlobalVariable.sortNumber];//被判为其它类的网页数
		int count=0;//计数
		int countFactAndPrecision=0;//统计事实属于此类且被分类正确的网页数
		
		//计算factAndPrecision和judgeSort
		for(int i=0;i<eachSortNumber.length;i++)
		{
			count+=eachSortNumber[i];
			countFactAndPrecision=0;
			while(j<count)
			{
				if(sortResult[j]==i+1)//事实属于此类且被分类正确的网页数
				{
					countFactAndPrecision++;
				}
				else//属于此类但被判为其它类的网页数
				{
					judgeSort[sortResult[j]-1]++;
				}
				j++;
			}
			factAndPrecision[i]=countFactAndPrecision;
		}
		
		//计算被判为此类的文档数
		for(int i=0;i<eachSortNumber.length;i++)
		{
			judgeSort[i]+=eachSortNumber[i];
		}
		
		//计算查准率和查全率
		for(int i=0;i<eachSortNumber.length;i++)
		{
			//查准率
			precisionAndRecall[i][0]=(double)factAndPrecision[i]/(double)judgeSort[i];
			//查全率
			precisionAndRecall[i][1]=(double)factAndPrecision[i]/(double)eachSortNumber[i];
		}
		if(bool==false)//用于显示页面分类结果
	            Evaluation.firstValue=new double[precisionAndRecall[0].length][precisionAndRecall.length];
		else Evaluation.secondValue=new double[precisionAndRecall[0].length][precisionAndRecall.length];
		for(int i=0;i<precisionAndRecall.length;i++)
		{
			for(int k=0;k<precisionAndRecall[0].length;k++)
			{
				if(bool==false)
					Evaluation.firstValue[k][i]=precisionAndRecall[i][k];
				else 
					Evaluation.secondValue[k][i]=precisionAndRecall[i][k];
			}
		}
		return precisionAndRecall;
	}
public int IncramentalTrain(int[] eachSortNumber,int[]sortResult)
	{
		
		//定义临时变量
		int j=0;
		
		int count=0;//计数
		int eNum=0;//记录分类错误的网页数目
		
		//计算factAndPrecision和judgeSort
		for(int i=0;i<eachSortNumber.length;i++)
		{
			count+=eachSortNumber[i];
		
			while(j<count)
			{
				if(sortResult[j]==i+1)//事实属于此类且被分类正确的网页数
				{
					PageInfo page=GlobalVariable.listPageInfo.get(j);
					svset.nsvAndYpage.add(page);
				}
				else//属于此类但被判为其它类的网页数
				{
				
					PageInfo page=GlobalVariable.listPageInfo.get(j);
					svset.svAndEpage.add(page);
					eNum++;
				
				}
				j++;
			}
			
		}
		return eNum;
	}


public int IncramentalTrain1(int[] eachSortNumber,int[]sortResult)
{
	
	//定义临时变量
	int j=0;
	
	int count=0;//计数
	int eNum=0;//记录分类错误的网页数目
	
	//计算factAndPrecision和judgeSort
	for(int i=0;i<eachSortNumber.length;i++)
	{
		count+=eachSortNumber[i];
	
		while(j<count)
		{
			if(sortResult[j]==i+1)//事实属于此类且被分类正确的网页数
			{
				
			}
			else//属于此类但被判为其它类的网页数
			{
			
				PageInfo page=svset.nsvAndYpage.get(j);
				svset.svAndEpage.add(page);
				eNum++;
			
			}
			j++;
		}
		
	}
	return eNum;
}

}
