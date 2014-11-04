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
 * 读取训练集
 * @author Administrator 
 */
public class ReadTrainingSet {
	/**
	 * 从训练集中读取网页相关信息
	 * @param fileTrainingSet 存取训练集的网页
	 * @return 将网页相关信息存取在list中
	 */
	@SuppressWarnings("unchecked")
	public List<PageInfo> getTrainingSet(File fileTrainingSet)
	{
		//获取网页相关信息
		StringBuffer strWebPageInfo=new StringBuffer();//将文件中的网页相关信息存放在strWebPageInfo中
		try {
			//获得BufferedReader流
			FileInputStream inputStream=new FileInputStream(fileTrainingSet);
			InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
			BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
			
			try {
				String strLine=bufferedReader.readLine();//读取一行数据
				while(strLine!=null)
				{
					strWebPageInfo.append(strLine.trim());
					strLine=bufferedReader.readLine();//读取下一行
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//定义相关的变量
		List<PageInfo>listPageInfo=new ArrayList<PageInfo>();
		int i=0,p=0,q=0,m=0,n=0;//临时变量
		StringBuffer type=new StringBuffer();//临时存储网页类别
		StringBuffer eigenvalue=new StringBuffer();//临时存储网页的特征值
		int maxVectorNumber=0;//存储网页特征向量最大维数
		int tempVectorNumber=0;//临时变量，存储网页特征向量维数
		
		//求网页特征向量最大的维数
		System.out.println("特征集总共长度");
		System.out.println(strWebPageInfo.length());
		while(p<strWebPageInfo.length())
		{
			
			//最大维数存储在)前面
				while(strWebPageInfo.charAt(p)==')')
				{
					//查找,:的位置
					for(q=p-2;q>p-30;q--)
					{
						if(strWebPageInfo.charAt(q)==':')
							n=q;
						if(strWebPageInfo.charAt(q)==',')
						{
							m=q;
							break;//结束此次查找
						}
					}
					tempVectorNumber=Integer.parseInt(strWebPageInfo.substring(m+1,n));
					//求网页特征向量的最大维数
					if(tempVectorNumber>maxVectorNumber)
						maxVectorNumber=tempVectorNumber;
					
					//跳出本次while
					p++;
					if(p==strWebPageInfo.length())
						break;
				}
				//查找下一个网页的维数
				p++;
			}
	
		//记录特征向量的维数
		int countEigenvalue=0;
		
		//定义临时变量
		int x=0, y=0,z=0;
		int tempEigenItem=0;
		int lack=0;
		int length=0;
		
		//将训练集中的网页特征向量放入list中
		while(i<strWebPageInfo.length())
		{
			PageInfo pageInfo=new PageInfo();//存储一个网页信息
			List listEigenvalue=new ArrayList();//存储一个网页信息的特征向量
			countEigenvalue=0;//当读取一个新网页时，特征向量的维数为1
			while(strWebPageInfo.charAt(i)!=')')//当等于)时，表示一个网页的特征向量已经读完
			{
				if(strWebPageInfo.charAt(i)=='(')//(后面存储的是网页的类别
				{
					//tempVectorNumber=0;//读取新的一个网页时，其特征向量维数为0
					type=new StringBuffer();//清空type里的内容
					while(strWebPageInfo.charAt(i+1)!=';')
					{
						type.append(strWebPageInfo.charAt(i+1));//获取网页的类别
						i++;
					}
					pageInfo.setType(Integer.parseInt(type.toString()));//将网页的类别存储到pageInfo中
				}
				if(strWebPageInfo.charAt(i)==':')//特征向量的值
				{
					countEigenvalue++;//特征向量出现的次数加1
					
					//取出是第几个特征值
					for(z=i-1;z>=i-6;z--)
					{
						if(strWebPageInfo.charAt(z)==','||strWebPageInfo.charAt(z)==';')//找到记录第几个特征值
						{
							x=z+1;
							y=i;
							break;//结束查找
						}
					}
					
					//看其特征项的值是否和countEigenvalue-1的值相等,若相等，说明特征项是全的，不相等，说明特征项少了一部分 (特征项是从0开始记的)
					tempEigenItem=Integer.parseInt(strWebPageInfo.substring(x,y));//取出特征项
					
					if(tempEigenItem==countEigenvalue-1)//特征项不缺少
					{
						//将这个特征值加入到listEigenvalue
						eigenvalue=new StringBuffer();//清空eigenvalue里的内容
						while(strWebPageInfo.charAt(i+1)!=',')
						{
							eigenvalue.append(strWebPageInfo.charAt(i+1));//获取网页的一个特征向量
							i++;
						}
						listEigenvalue.add(Double.parseDouble(eigenvalue.toString()));//将网页的特征值存取到list中
					}
					else//特征项缺少部分，补上缺少的部分
					{
						lack=tempEigenItem-countEigenvalue+1;
						for(int b=1;b<=lack;b++)
						{
							listEigenvalue.add(0.0);
						}
						//出现的次数加上lack
						countEigenvalue=countEigenvalue+lack;
						
						//将这个特征值加入到listEigenvalue
						eigenvalue=new StringBuffer();//清空eigenvalue里的内容
						while(strWebPageInfo.charAt(i+1)!=',')
						{
							eigenvalue.append(strWebPageInfo.charAt(i+1));//获取网页的一个特征向量
							i++;
						}
						listEigenvalue.add(Double.parseDouble(eigenvalue.toString()));//将网页的特征值存取到list中
					}
				}
				
				//找该网页的下一个特征值
				i++;
			}
			
			//若length小于maxVectorNumber+1,则该网页的最后一个特征项不是maxVecotrNumber，需要将剩下的补上
			length=listEigenvalue.size();
			while(length<=maxVectorNumber)
			{
				listEigenvalue.add(0.0);
				length++;
			}
			
			//将一个网页的相关信息加入到listPageInfo中
			pageInfo.setEigenvalue(listEigenvalue);
			listPageInfo.add(pageInfo);
			
			//下一个网页特征值的提取
			i++;
		}
		
		//记下网页特征向量的最大维数
		GlobalVariable.vectorNumber=maxVectorNumber+1;//特征向量的个数是从0开始记的
		GlobalVariable.webPageNumber=listPageInfo.size();//设置训练集中网页的个数
		/*System.out.println("向量维数");
		System.out.println(GlobalVariable.vectorNumber);*/
		return listPageInfo;
		
	}
	/**
	 *获取训练集中网页一共有几个类别
	 * @param listPageInfo 训练集
	 */
	public int getWebPageSort(List<PageInfo> listPageInfo)
	{
		int sort=1;//临时变量
		for(int i=0;i<listPageInfo.size()-1;i++)
		{
			if(listPageInfo.get(i).getType()!=listPageInfo.get(i+1).getType())//当前后两个网页的类别不相同时，则类别数加1
			{
				sort=sort+1;
			}
		}
		
		return sort;
	}
	/**
	 * 获得测试集中每个类别的网页数
	 * @param listPageInfo 测试集
	 * @return
	 */
	public int[] getEachSortCount(List<PageInfo> listPageInfo)
	{
		int count=0;//计数
		//定义一个数组，存储每个类别的网页数
		int eachSortNumber[]=new int[GlobalVariable.sortNumber];
		//统计每个类别的网页数
		for(int i=1;i<=GlobalVariable.sortNumber;i++)
		{
			count=0;//每个类别开始统计时，count清零
			for(int j=0;j<listPageInfo.size();j++)//对测试集中每个网页进行查找
			{
				if(listPageInfo.get(j).getType()==i)//类别相同
				{
					count++;
				}
			}
			//出现的次数赋给eachSortCount数组
			eachSortNumber[i-1]=count;
		}
		return eachSortNumber;
	}
}
