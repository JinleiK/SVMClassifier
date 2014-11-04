package file;

import global.GlobalVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import struct.PageInfo;

/**
 * ��ȡѵ����
 * @author Administrator 
 */
public class ReadTrainingSet {
	/**
	 * ��ѵ�����ж�ȡ��ҳ�����Ϣ
	 * @param fileTrainingSet ��ȡѵ��������ҳ
	 * @return ����ҳ�����Ϣ��ȡ��list��
	 */
	@SuppressWarnings("unchecked")
	public List<PageInfo> getTrainingSet(File fileTrainingSet)
	{
		//��ȡ��ҳ�����Ϣ
		StringBuffer strWebPageInfo=new StringBuffer();//���ļ��е���ҳ�����Ϣ�����strWebPageInfo��
		try {
			//���BufferedReader��
			FileInputStream inputStream=new FileInputStream(fileTrainingSet);
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			
			try {
				String strLine=bufferedReader.readLine();//��ȡһ������
				while(strLine!=null)
				{
					strWebPageInfo.append(strLine.trim());
					strLine=bufferedReader.readLine();//��ȡ��һ��
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//������صı���
		List<PageInfo>listPageInfo=new ArrayList<PageInfo>();
		int i=0,p=0,q=0,m=0,n=0;//��ʱ����
		StringBuffer type=new StringBuffer();//��ʱ�洢��ҳ���
		StringBuffer eigenvalue=new StringBuffer();//��ʱ�洢��ҳ������ֵ
		int maxVectorNumber=0;//�洢��ҳ�����������ά��
		int tempVectorNumber=0;//��ʱ�������洢��ҳ��������ά��
		
		//����ҳ������������ά��
		System.out.println("�������ܹ�����");
		System.out.println(strWebPageInfo.length());
		while(p<strWebPageInfo.length())
		{
			
			//���ά���洢��)ǰ��
				while(strWebPageInfo.charAt(p)==')')
				{
					//����,:��λ��
					for(q=p-2;q>p-30;q--)
					{
						if(strWebPageInfo.charAt(q)==':')
							n=q;
						if(strWebPageInfo.charAt(q)==',')
						{
							m=q;
							break;//�����˴β���
						}
					}
					tempVectorNumber=Integer.parseInt(strWebPageInfo.substring(m+1,n));
					//����ҳ�������������ά��
					if(tempVectorNumber>maxVectorNumber)
						maxVectorNumber=tempVectorNumber;
					
					//��������while
					p++;
					if(p==strWebPageInfo.length())
						break;
				}
				//������һ����ҳ��ά��
				p++;
			}
	
		//��¼����������ά��
		int countEigenvalue=0;
		
		//������ʱ����
		int x=0, y=0,z=0;
		int tempEigenItem=0;
		int lack=0;
		int length=0;
		
		//��ѵ�����е���ҳ������������list��
		while(i<strWebPageInfo.length())
		{
			PageInfo pageInfo=new PageInfo();//�洢һ����ҳ��Ϣ
			List listEigenvalue=new ArrayList();//�洢һ����ҳ��Ϣ����������
			countEigenvalue=0;//����ȡһ������ҳʱ������������ά��Ϊ1
			while(strWebPageInfo.charAt(i)!=')')//������)ʱ����ʾһ����ҳ�����������Ѿ�����
			{
				if(strWebPageInfo.charAt(i)=='(')//(����洢������ҳ�����
				{
					//tempVectorNumber=0;//��ȡ�µ�һ����ҳʱ������������ά��Ϊ0
					type=new StringBuffer();//���type�������
					while(strWebPageInfo.charAt(i+1)!=';')
					{
						type.append(strWebPageInfo.charAt(i+1));//��ȡ��ҳ�����
						i++;
					}
					pageInfo.setType(Integer.parseInt(type.toString()));//����ҳ�����洢��pageInfo��
				}
				if(strWebPageInfo.charAt(i)==':')//����������ֵ
				{
					countEigenvalue++;//�����������ֵĴ�����1
					
					//ȡ���ǵڼ�������ֵ
					for(z=i-1;z>=i-6;z--)
					{
						if(strWebPageInfo.charAt(z)==','||strWebPageInfo.charAt(z)==';')//�ҵ���¼�ڼ�������ֵ
						{
							x=z+1;
							y=i;
							break;//��������
						}
					}
					
					//�����������ֵ�Ƿ��countEigenvalue-1��ֵ���,����ȣ�˵����������ȫ�ģ�����ȣ�˵������������һ���� (�������Ǵ�0��ʼ�ǵ�)
					tempEigenItem=Integer.parseInt(strWebPageInfo.substring(x,y));//ȡ��������
					
					if(tempEigenItem==countEigenvalue-1)//�����ȱ��
					{
						//���������ֵ���뵽listEigenvalue
						eigenvalue=new StringBuffer();//���eigenvalue�������
						while(strWebPageInfo.charAt(i+1)!=',')
						{
							eigenvalue.append(strWebPageInfo.charAt(i+1));//��ȡ��ҳ��һ����������
							i++;
						}
						listEigenvalue.add(Double.parseDouble(eigenvalue.toString()));//����ҳ������ֵ��ȡ��list��
					}
					else//������ȱ�ٲ��֣�����ȱ�ٵĲ���
					{
						lack=tempEigenItem-countEigenvalue+1;
						for(int b=1;b<=lack;b++)
						{
							listEigenvalue.add(0.0);
						}
						//���ֵĴ�������lack
						countEigenvalue=countEigenvalue+lack;
						
						//���������ֵ���뵽listEigenvalue
						eigenvalue=new StringBuffer();//���eigenvalue�������
						while(strWebPageInfo.charAt(i+1)!=',')
						{
							eigenvalue.append(strWebPageInfo.charAt(i+1));//��ȡ��ҳ��һ����������
							i++;
						}
						listEigenvalue.add(Double.parseDouble(eigenvalue.toString()));//����ҳ������ֵ��ȡ��list��
					}
				}
				
				//�Ҹ���ҳ����һ������ֵ
				i++;
			}
			
			//��lengthС��maxVectorNumber+1,�����ҳ�����һ���������maxVecotrNumber����Ҫ��ʣ�µĲ���
			length=listEigenvalue.size();
			while(length<=maxVectorNumber)
			{
				listEigenvalue.add(0.0);
				length++;
			}
			
			//��һ����ҳ�������Ϣ���뵽listPageInfo��
			pageInfo.setEigenvalue(listEigenvalue);
			listPageInfo.add(pageInfo);
			
			//��һ����ҳ����ֵ����ȡ
			i++;
		}
		
		//������ҳ�������������ά��
		GlobalVariable.vectorNumber=maxVectorNumber+1;//���������ĸ����Ǵ�0��ʼ�ǵ�
		GlobalVariable.webPageNumber=listPageInfo.size();//����ѵ��������ҳ�ĸ���
		/*System.out.println("����ά��");
		System.out.println(GlobalVariable.vectorNumber);*/
		return listPageInfo;
		
	}
	/**
	 *��ȡѵ��������ҳһ���м������
	 * @param listPageInfo ѵ����
	 */
	public int getWebPageSort(List<PageInfo> listPageInfo)
	{
		int sort=1;//��ʱ����
		for(int i=0;i<listPageInfo.size()-1;i++)
		{
			if(listPageInfo.get(i).getType()!=listPageInfo.get(i+1).getType())//��ǰ��������ҳ�������ͬʱ�����������1
			{
				sort=sort+1;
			}
		}
		
		return sort;
	}
	/**
	 * ��ò��Լ���ÿ��������ҳ��
	 * @param listPageInfo ���Լ�
	 * @return
	 */
	public int[] getEachSortCount(List<PageInfo> listPageInfo)
	{
		int count=0;//����
		//����һ�����飬�洢ÿ��������ҳ��
		int eachSortNumber[]=new int[GlobalVariable.sortNumber];
		//ͳ��ÿ��������ҳ��
		for(int i=1;i<=GlobalVariable.sortNumber;i++)
		{
			count=0;//ÿ�����ʼͳ��ʱ��count����
			for(int j=0;j<listPageInfo.size();j++)//�Բ��Լ���ÿ����ҳ���в���
			{
				if(listPageInfo.get(j).getType()==i)//�����ͬ
				{
					count++;
				}
			}
			//���ֵĴ�������eachSortCount����
			eachSortNumber[i-1]=count;
		}
		return eachSortNumber;
	}
}
