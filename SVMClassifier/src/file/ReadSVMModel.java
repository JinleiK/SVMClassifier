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
 * ��ȡSVMģ���ļ�
 * @author Administrator
 *
 */
public class ReadSVMModel {
	/**
	 * ת��
	 * @param tempStrLagrange
	 * @return
	 */
	public double[] transformLagrange(String[] tempStrLagrange)
	{
		svset.svAndEpage=new ArrayList<PageInfo>();
		svset.nsvAndYpage=new ArrayList<PageInfo>();
		
		//Lagrange����
		double[] tempLagrange=new double[GlobalVariable.webPageNumber];
		 for(int i=0;i<tempStrLagrange.length;i++)
			{
				tempLagrange[i]=Double.parseDouble(tempStrLagrange[i]);
			}
		 return tempLagrange;
	}
	/**
	 * ת��
	 * @param tempStrVerticalVector
	 * @return
	 */
	public double[] transformVerticalVector(String[] tempStrVerticalVector)
	{
		//��ƽ��ķ�����
	    double [] tempVerticalVector=new double[GlobalVariable.vectorNumber];
	    for(int i=0;i<tempStrVerticalVector.length;i++)
		{
			tempVerticalVector[i]=Double.parseDouble(tempStrVerticalVector[i]);
		}
	    return tempVerticalVector;
	}
	/**
	 * ��ȡSVMģ���ļ�
	 * @param fileSVMModel SVMģ���ļ�
	 * @return ��ƽ���б�  SVMģ���ļ�������˶����ƽ��
	 */
	public List<HyperPlane> getSVMModel(File fileSVMModel)
	{
		//������ʱ����
		//��ƽ��ķ�����
	     String [] tempStrVerticalVector=new String[GlobalVariable.vectorNumber];
		//Lagrange����
		 String[] tempStrLagrange=new String[GlobalVariable.webPageNumber];
		 double tempIntercept=0.0;//b
		 
		//������ʱ����,���ַ����͵ĳ�ƽ���Lagrange�ͷ�����ת��Ϊdouble�͵ĳ�ƽ���Lagrange�ͷ�����
		//��ƽ��ķ�����
		 double [] tempVerticalVector=new double[GlobalVariable.vectorNumber];
		//Lagrange����
	     double[] tempLagrange=new double[GlobalVariable.webPageNumber];
		 
		//����洢��ƽ���list
		 List<HyperPlane>listHyperPlane=new ArrayList<HyperPlane>();
		 
		 //��ȡSVMģ��
		FileInputStream inputStream;
		try {
			//���������
			inputStream = new FileInputStream(fileSVMModel);
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			
			String strLine;
			try {
				strLine = bufferedReader.readLine();//��ȡһ������
				while(strLine!=null)
				{
					//���Lagrange
					if(strLine.trim().equals("��ƽ���Lagrange"))
					{
						strLine=bufferedReader.readLine();
						tempStrLagrange=strLine.split(" ");
					}
					
					//��ó�ƽ�������
					if(strLine.trim().equals("��ƽ��ķ�����"))
					{
						strLine=bufferedReader.readLine();
						tempStrVerticalVector=strLine.split(" ");
					}
					
					//���bֵ
					if(strLine.trim().equals("��ƽ���bֵ"))
					{
						strLine=bufferedReader.readLine();
						tempIntercept=Double.parseDouble(strLine);
						
						//����һ����ƽ��
						//����ת��
						tempVerticalVector=transformVerticalVector(tempStrVerticalVector);
						tempLagrange=transformVerticalVector(tempStrLagrange);
						
						//����һ����ƽ�� ����¼��õĳ�ƽ��
						HyperPlane hyperPlane=new HyperPlane();
						hyperPlane.setIntercept(tempIntercept);
						hyperPlane.setLagrange(tempLagrange);
						hyperPlane.setVerticalVector(tempVerticalVector);
						
						//����ƽ����뵽list��
						listHyperPlane.add(hyperPlane);
					}
					strLine=bufferedReader.readLine();//��ȡ��һ��
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
