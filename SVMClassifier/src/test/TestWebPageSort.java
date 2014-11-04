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
 * ������ҳ�������
 * @author Administrator 
 */
public class TestWebPageSort {
	/**
	 * ������
	 * @param listEigenValue ��ҳ����������
	 * @param vetricalVector ��ƽ��ķ�����
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
	 * ���㱻������ҳ��һ����ƽ��ľ��ߺ���ֵȷ��������ҳ������һ�����
	 * @param webPage �����Ե���ҳ
	 * @param fileSVMModel SVMģ���ļ�
	 * @return ������һ�����ı��
	 */
	public int[] getWebPageSort(File fileWebPageSet,File fileSVMModel)
	{
		//��ñ�����ҳ����������
		ReadTrainingSet readTestSet=new ReadTrainingSet();
		List<PageInfo> testListPageInfo=readTestSet.getTrainingSet(fileWebPageSet);
		
		//���SVMģ��
		ReadSVMModel readSVMModel=new ReadSVMModel();
		List<HyperPlane>listHyperPlane=readSVMModel.getSVMModel(fileSVMModel);
		
		//����һ����ƽ��,һ������������list
		HyperPlane hyperPlane=new HyperPlane();
		List<Double>listEigenValue=new ArrayList<Double>();
		//�������飬�洢W
		double []vetricalVector=new double[GlobalVariable.vectorNumber];
		//����������ʱ�������洢���ߺ���ֵ
		double defineValue=0.0;
		double maxDefineValue=-1000000000.0;
	
		//����һ��������飬��¼������ҳ�����
		int []sort=new int[testListPageInfo.size()];
		
		//���в���
		//��һ����ҳ�����г�ƽ����㣬�������ߺ�����ֵ��ȡ����ֵ����Ӧ�����Ϊ��ҳ�����
		for(int i=0;i<testListPageInfo.size();i++)
		{
			listEigenValue=testListPageInfo.get(i).getEigenvalue();	//��ø���ҳ����������ֵ
			maxDefineValue=-1000000000.0;//������һ���µ���ҳʱ���������ߺ���ֵӦ�ø���һ����С��ֵ
			
			for( int j=0;j<listHyperPlane.size();j++)
			{
				defineValue=0.0;//���ߺ���ֵ��0
				//��øó�ƽ��ķ�����
				vetricalVector=listHyperPlane.get(j).getVerticalVector();
				//������ҳ������ֵ�ͷ�����֮��ĵ��
				defineValue=calculateDotMetrix(listEigenValue,vetricalVector);
				//����bֵ
				defineValue+=listHyperPlane.get(j).getIntercept();
				//���ߺ���ֵ�������Ӧ���Ǹ����Ϊ������ҳ�����
				if(defineValue>maxDefineValue)
				{
					maxDefineValue=defineValue;
					//����𸳸�sort����
					sort[i]=j+1;
				}
			}
		}
			
		return sort;
	}
	public int[] getIncreamentalWebPageSort(List<PageInfo> testListPageInfo,File fileSVMModel)
	{
		//���SVMģ��
		ReadSVMModel readSVMModel=new ReadSVMModel();
		List<HyperPlane>listHyperPlane=readSVMModel.getSVMModel(fileSVMModel);
		
		//����һ����ƽ��,һ������������list
		HyperPlane hyperPlane=new HyperPlane();
		List<Double>listEigenValue=new ArrayList<Double>();
		//�������飬�洢W
		double []vetricalVector=new double[GlobalVariable.vectorNumber];
		//����������ʱ�������洢���ߺ���ֵ
		double defineValue=0.0;
		double maxDefineValue=-1000000000.0;
	
		//����һ��������飬��¼������ҳ�����
		int []sort=new int[testListPageInfo.size()];
		
		//���в���
		//��һ����ҳ�����г�ƽ����㣬�������ߺ�����ֵ��ȡ����ֵ����Ӧ�����Ϊ��ҳ�����
		for(int i=0;i<testListPageInfo.size();i++)
		{
			listEigenValue=testListPageInfo.get(i).getEigenvalue();	//��ø���ҳ����������ֵ
			maxDefineValue=-1000000000.0;//������һ���µ���ҳʱ���������ߺ���ֵӦ�ø���һ����С��ֵ
			
			for( int j=0;j<listHyperPlane.size();j++)
			{
				defineValue=0.0;//���ߺ���ֵ��0
				//��øó�ƽ��ķ�����
				vetricalVector=listHyperPlane.get(j).getVerticalVector();
				//������ҳ������ֵ�ͷ�����֮��ĵ��
				defineValue=calculateDotMetrix(listEigenValue,vetricalVector);
				defineValue+=listHyperPlane.get(j).getIntercept();
				//���ߺ���ֵ�������Ӧ���Ǹ����Ϊ������ҳ�����
				if(defineValue>maxDefineValue)
				{
					maxDefineValue=defineValue;
					//����𸳸�sort����
					sort[i]=j+1;
				}
			}
		}
			
		return sort;
	}
	/**
	 * �����׼�ʺͲ�ȫ��
	 * @param eachSortNumber �洢���Լ���ÿ��������ҳ��Ŀ��������ĳ���ΪGlobalVariable.sortNumber
	 * @param sortResult ������
	 * @return һ����ά���飬�кű�ʾ���������һ�б�ʾ��׼�ʣ��ڶ��б�ʾ��ȫ��
	 */
	public double [][] calculatePrecisionAndRecall(int[] eachSortNumber,int[]sortResult,Boolean bool)
	{
		double [][]precisionAndRecall=new double [GlobalVariable.sortNumber][2];//�洢׼ȷ�ʺͲ�ȫ��
		//������ʱ����
		int j=0;
		int factAndPrecision[]=new int[GlobalVariable.sortNumber];//��ʵ���ڴ����ұ�������ȷ����ҳ��
		int judgeSort[]=new int[GlobalVariable.sortNumber];//����Ϊ���������ҳ��
		int count=0;//����
		int countFactAndPrecision=0;//ͳ����ʵ���ڴ����ұ�������ȷ����ҳ��
		
		//����factAndPrecision��judgeSort
		for(int i=0;i<eachSortNumber.length;i++)
		{
			count+=eachSortNumber[i];
			countFactAndPrecision=0;
			while(j<count)
			{
				if(sortResult[j]==i+1)//��ʵ���ڴ����ұ�������ȷ����ҳ��
				{
					countFactAndPrecision++;
				}
				else//���ڴ��൫����Ϊ���������ҳ��
				{
					judgeSort[sortResult[j]-1]++;
				}
				j++;
			}
			factAndPrecision[i]=countFactAndPrecision;
		}
		
		//���㱻��Ϊ������ĵ���
		for(int i=0;i<eachSortNumber.length;i++)
		{
			judgeSort[i]+=eachSortNumber[i];
		}
		
		//�����׼�ʺͲ�ȫ��
		for(int i=0;i<eachSortNumber.length;i++)
		{
			//��׼��
			precisionAndRecall[i][0]=(double)factAndPrecision[i]/(double)judgeSort[i];
			//��ȫ��
			precisionAndRecall[i][1]=(double)factAndPrecision[i]/(double)eachSortNumber[i];
		}
		if(bool==false)//������ʾҳ�������
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
		
		//������ʱ����
		int j=0;
		
		int count=0;//����
		int eNum=0;//��¼����������ҳ��Ŀ
		
		//����factAndPrecision��judgeSort
		for(int i=0;i<eachSortNumber.length;i++)
		{
			count+=eachSortNumber[i];
		
			while(j<count)
			{
				if(sortResult[j]==i+1)//��ʵ���ڴ����ұ�������ȷ����ҳ��
				{
					PageInfo page=GlobalVariable.listPageInfo.get(j);
					svset.nsvAndYpage.add(page);
				}
				else//���ڴ��൫����Ϊ���������ҳ��
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
	
	//������ʱ����
	int j=0;
	
	int count=0;//����
	int eNum=0;//��¼����������ҳ��Ŀ
	
	//����factAndPrecision��judgeSort
	for(int i=0;i<eachSortNumber.length;i++)
	{
		count+=eachSortNumber[i];
	
		while(j<count)
		{
			if(sortResult[j]==i+1)//��ʵ���ڴ����ұ�������ȷ����ҳ��
			{
				
			}
			else//���ڴ��൫����Ϊ���������ҳ��
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
