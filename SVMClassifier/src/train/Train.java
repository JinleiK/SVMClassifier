package train;

import global.GlobalVariable;
import global.svset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import struct.HyperPlane;
import struct.PageInfo;

/**
 * 用训练样本对SVM进行训练,得到模型
 * @author Administrator 
 *
 */
public class Train {
	
	private double	delta_b=0;	//阈值增量
	private double eps=0.001;   //Lagrange乘子的允许的误差
	private int RAND_MAX=300;
	private double alph[]=new double[GlobalVariable.webPageNumber];//Lagrange乘子
	private double eError[]=new double[GlobalVariable.webPageNumber];//误差
	private int target[]=new int[GlobalVariable.webPageNumber];//记录网页的类别，它只能为1和-1，通过SetSort函数来实现
	private double b;//超平面中b 
	private List<PageInfo>listPageInfo=new ArrayList<PageInfo>();//训练集中网页的特征向量
	/**
	 * 设置listPageInfo 
	 * @param listPageInfo
	 */
	public void setListPageInfo(List<PageInfo> listPageInfo) {
		this.listPageInfo = listPageInfo;
	}
	/**
	 * 构造函数，初始化相关的参数
	 */
	public Train()
	{
		for(int i=0;i<GlobalVariable.webPageNumber;i++)
		{
			alph[i]=0;//初始化Lagrange ,为0
			eError[i]=0; //初始化误差 ,为 0
			b=0;//初始化b ,为 0
		}
	}
	/**
	 * 函数功能为将要训练的类别设为1，其余的类别设为-1
	 * @param listPageInfo 训练集中的网页
	 * @param sort 训练的类别
	 */
	public void setSort(int sort)
	{
		//将要训练的类别设为1，其余的类别设为-1
		System.out.println(listPageInfo.size());
		for(int i=0;i<listPageInfo.size();i++)
		{
			if(listPageInfo.get(i).getType()==sort)//将要训练的类别
			{
			    //将要训练的类别设为1
				target[i]=1;
			}
			else//其它类别
			{
				target[i]=-1;
			}
		}
	}
	/**
	 * 对两个特征向量求其点积
	 * @param vector1 特征向量1
	 * @param vector2 特征向量2
	 * @return 
	 */
	public double calculateDotMetrix(List<Double>vector1,List<Double>vector2)
	{
		double sumMetrix=0.0;//存储点积
	    //	求点积
		for(int i=0;i<vector1.size();i++)
		{
			sumMetrix+=vector1.get(i)*vector2.get(i);
		}
		return sumMetrix;
	}
	/**
	 * 计算两个特征向量RBF核函数
	 * @param suffix1 在训练集中的位置
	 * @param suffix2 在训练集中的位置
	 * @return
	 */
	public double calculateKernel(int suffix1,int suffix2)
	{
		//获得网页的特征向量
		List<Double>vector1=listPageInfo.get(suffix1).getEigenvalue();
		List<Double>vector2=listPageInfo.get(suffix2).getEigenvalue();
		
		//计算核函数
		double temp=0.0;
		List<Double>tempSubstract=new ArrayList<Double>();
		for(int i=0;i<vector1.size();i++)
		{
			temp=vector1.get(i)-vector2.get(i);
			tempSubstract.add(temp);
		}
		
		return Math.exp(-(calculateDotMetrix(tempSubstract,tempSubstract)/(GlobalVariable.delta*GlobalVariable.delta)));
	}
	/**
	 * 训练SVM
	 * @param sort 要训练的网页类别
	 */
	public void learning(int sort)
	{
		//应该有个清0的工作,即将Lagrange,w,b等都应该等于零,从头开始
		//定一 个超平面，因为每个类别都要训练得到一个超平面，所有每次学习时这个超平面都需要重新定义
		HyperPlane hyperPlane=new HyperPlane();//定义一个超平面
		
		//这个是赋初值
		//全部赋初值为0,方便以后操作
		hyperPlane.setIntercept(0.0);
		b = 0;
		//定义double[],临时存储w和Lagrange
		double tempLagrange[]=new double[GlobalVariable.webPageNumber];
		double tempVerticalVector[]=new double[GlobalVariable.vectorNumber];
		//为Lagrange赋初值
		for( int i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			tempLagrange[i]=0.0;
			alph[i] = 0.0;
		}
		hyperPlane.setLagrange(tempLagrange);
		
		//为w赋初值,w即为超平面法向量
		for( int i = 0; i <GlobalVariable.vectorNumber ; i++ )
		{
			tempVerticalVector[i]=0.0;
		}
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//设置合适的类别标记,只分为两类,正类和负类
		setSort(sort);

		int numChanged = 0;//训练集的各个网页的Lagrange乘子是否有改变的,"1"表示"是","0"表示"否"
		Boolean examineAll = true;//是否要查询全部的训练网页
		int numTT = 0;//迭代的次数

		//当所有的训练网页的Lagrange乘子都未改变并且已经查询了全部的训练
		//网页之后或者迭代次数已经超过150次了,则结束循环
		while(( numChanged > 0 || examineAll ) && numTT < 150 )// 
		{
			numChanged = 0;//赋为0,表示重新开始,则所有的Lagrange乘子都未变

			if( examineAll )//查询全部的特征向量
			{
				for( int t = 0; t <GlobalVariable.webPageNumber ; t++ )
					numChanged += exameTrainingSet(t);//若有网页的Lagrange乘子
													//改变,则将numChanged加1,以改变其值
			}
			else			//只查询Lagrange值不为0且不为dPunish的特征向量,即非边界样本
			{
				svset.svAndEpage=new ArrayList<PageInfo>();
				svset.nsvAndYpage=new ArrayList<PageInfo>();
				for( int t = 0; t <GlobalVariable.webPageNumber ; t++ )
				{
				 if( alph[t] != 0  )
				 {
					PageInfo page=listPageInfo.get(t);
					svset.svAndEpage.add(page);
				}
				 else 
				 {
					PageInfo page=listPageInfo.get(t);
					svset.nsvAndYpage.add(page);
				 }
				if( alph[t] != 0 && alph[t] !=GlobalVariable.punish )
				   numChanged += exameTrainingSet(t);
				  }
			}

			//重新设置examineAll和numChanged的值,以进行下一次的循环
			if( examineAll == true )
			{
				examineAll = false;
				numTT++;//迭代次数增1
			}
			else if( numChanged == 0 )
			{
				examineAll = true;
				numTT++;//迭代次数增1
			}
		
		}

		//将计算得到的值,赋给超平面相应的值
		for(int  i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			tempLagrange[i]=alph[i];
		}
		hyperPlane.setLagrange(tempLagrange);
		
		//将b值赋给超平面
		hyperPlane.setIntercept(b);
		
		//求解超平面的法向向量ω
		hyperPlane=getVerticalVector(hyperPlane);

		//将超平面写到文件中
		tempLagrange=hyperPlane.getLagrange();//获得Lagrange
		tempVerticalVector=hyperPlane.getVerticalVector();//获得w
		//定义两个临时变量，存储Lagrange VerticalVector
		StringBuffer strLagrange=new StringBuffer();
		StringBuffer strVerticalVector=new StringBuffer();
		
		//为strLagrange strVerticalVector 赋值 
		for(int i=0;i<tempLagrange.length;i++)
		{
			strLagrange.append(tempLagrange[i]).append(" ");
		}
		for(int i=0;i<tempVerticalVector.length;i++)
		{
			strVerticalVector.append(tempVerticalVector[i]).append(" ");
		}
		
		File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt");
		//定义输出流	
		
		try {
			FileOutputStream outputStream=new FileOutputStream(file,true);
			try {
				
				outputStream.write(("第"+sort+"类别的超平面").getBytes());
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write("超平面的Lagrange".getBytes());
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write(strLagrange.toString().getBytes());//将Lagrange写入文件
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write("超平面的法向量".getBytes());
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write(strVerticalVector.toString().getBytes());//将VerticalVector写入文件
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write("超平面的b值".getBytes());
				outputStream.write("\r\n".getBytes());//写入一个换行符
				outputStream.write(String.valueOf(b).getBytes());//将b写入文件
				outputStream.write("\r\n".getBytes());
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 求解超平面的法向量
	 * @param hyperPlane 超平面（此时的超平面的法向量没有求出来，只存储向量机的Lagrange
	 */
	public HyperPlane getVerticalVector(HyperPlane hyperPlane)
	{
		//定义临时变量，临时存储ω
		double tempVerticalVector[]=new double[GlobalVariable.vectorNumber];
		//赋初值为0,以便进行下面的操作
		for( int i = 0; i <GlobalVariable.vectorNumber; i++ )
			tempVerticalVector[i] = 0;
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//获得Lagrange 
		 double tempLagrange[]=hyperPlane.getLagrange();
		
		//按照求ω的公式,求解所有训练网页的ω
		for(int i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			//获得该网页的特征向量
			List <Double> listEigenvalue=listPageInfo.get(i).getEigenvalue();
			for( int j = 0; j <GlobalVariable.vectorNumber ;j++ )
			{
				//求ω的公式
				tempVerticalVector[j] += tempLagrange[i] * target[i] *listEigenvalue.get(j) ;
			}
		}
		//将ω加入到超平面中
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//返回超平面
		return hyperPlane;
	}
	/**
	 * 检查第一个变量是否违背KKT条件，如果不满足KKT条件，则寻找第二个权向量，调用takeStep函数，更新两个权值
	 * @param i Lagrange乘子下标
	 * @return 如果有权重向量被更新，返回1否则返回0
	 */
	
	public int exameTrainingSet(int i1)
	{
		//定义相关变量,以便进行操作
		int y1;			//第i1类的标记,为1或-1(只有正类和负类的情形下)
		double alph1;	//待优化的第一个Lagrange乘子
		double E1;		//错误率
		double r1;		//等于y1*E1的临时变量,判断KKT条件的变量

		//设置y1,alph1的初值
		y1 = target[i1];
		alph1 = alph[i1];

		//设置E1的初值
		if( alph1 > 0 && alph1 < GlobalVariable.punish )
			E1 = eError[i1];
		else
			E1 = func(i1) - y1;

		//设置r1的值
		r1 = y1 * E1;

		if(( r1 < - GlobalVariable.tolerance && alph1 < GlobalVariable.tolerance ) || ( r1 > GlobalVariable.tolerance && alph1 > 0 ))//不满足KKT条件
		{
			//寻找第二个权值更新，并返回
			//寻找|E1-E2|最大的，
			{
				int k, i2;//k,循环变量,i2,第二个待优化的Lagrange乘子
				double tmax;//存放最大的|E1-E2|的值
				
				//赋初值
				i2	 = -1;
				tmax =  0;

				//循环以找到要优化的第二个Lagrange乘子的下标
				for( k = 0; k <GlobalVariable.webPageNumber ; k++ )
					if( alph[k] > 0 && alph[k] <  GlobalVariable.punish  )
					{
						double E2;	//错误率
						double temp;//临时变量,存放两个错误率的差的绝对值				

						//赋值
						E2 = eError[k];
						temp = Math.abs( E1 - E2 );

						//查找最大的temp,
						if( temp > tmax )
						{
							tmax = temp;
							i2 = k;
						}
					}

				//表示找到了第二个Lagrange乘子
				if( i2 >= 0 )
				{
					if( takeStep( i1, i2 )==1 )//优化第i1,i2个Lagrange乘子
						return 1;
				}
			}

			//上面的步骤没有找到i2,则执行下面的
			//寻找非边界样本,从随机位置开始,以免算法每一次开始都是向固定的方向寻找,
			//其中,RAND_MAX = 0x7fff
			{
				int k;	//循环变量,
				int k0;	//随机产生的访问位置
				int i2;	//第i2个Lagrange乘子

				//产生k0
				k0 = (int)(( Math.random() / RAND_MAX ) * GlobalVariable.webPageNumber  );

				//循环查找i2
				for( k = k0; k < GlobalVariable.webPageNumber + k0; k++ )
				{
					i2 = k % GlobalVariable.webPageNumber;
					if( alph[i2] > 0 && alph[i2] <GlobalVariable.punish )//非边界样本的条件
					{
						if( takeStep(i1, i2)==1 )//优化第i1,i2个Lagrange乘子
							return 1;
					}
				}
			}

			//上面的步骤还没有找到i2,则执行下面的
			//寻找整个样本从随机位置开始,以免算法每一次开始都是向固定的方向寻找,
			//其中,RAND_MAX = 0x7fff
			{
				int k0;	//随即产生的查询位置
				int k;	//循环变量
				int i2;	//第i2个Lagrange乘子

				//产生k0
				k0 = (int)( ( Math.random()/ RAND_MAX ) *GlobalVariable.webPageNumber );

				//循环查找i2
				for( k = k0; k <GlobalVariable.webPageNumber + k0; k++ )
				{
					i2 = k % GlobalVariable.webPageNumber ;
					if( takeStep( i1, i2 )==1 )//找到,优化第i1,i2个Lagrange乘子
						return 1;
				}
			}
		}
		return 0;
	}
	/**
	 * 优化两个Lagrange乘子
	 * @param i1 第一个乘子
	 * @param i2 第二个乘子
	 * @return 若优化成功，返回1，不成功，返回-1
	 */
	public int takeStep(int i1,int i2)
	{
		//定义相关变量,以便进行操作
		int y1, y2;	//第i1,i2类的标记,为1或-1(只有正类和负类的情形下)
		int s;		//临时变量,值等于y1*y2
		double alph1, alph2;//旧的Lagrange值
		double a1, a2;		//新的Lagrange值
		double E1, E2;		//错误率
		double H, L;		//第i2个Lagrange乘子的上、下限
		double k11, k22, k12;//应用核函数求得的值
		double eta;			//临时变量,其值为2*k12-k11-k12
		double lobj, hobj;	//当第i2个Lagrange值为L,H时的目标函数(判别函数)值

		//当待优化的两个Lagrange乘子为同一个时,则不会进行操作
		if( i1 == i2 )
			return 0;

		//将alph1,y1,E1赋初值
		alph1 = alph[i1];
		y1 = target[i1];

		if( alph1 > 0 && alph1 <GlobalVariable.punish)
			E1 = eError[i1];
		else
			E1 = func(i1) - y1;

		//将alph2,y2,E2赋初值
		alph2 = alph[i2];
		y2 = target[i2];

		if( alph2 > 0 && alph2 < GlobalVariable.punish )
			E2 = eError[i2];
		else
			E2 = func(i2) - y2;

		//设置s的初值
		s = y1 * y2;
		
		if( y1 == y2 )//当待优化的两个Lagrange乘子都为同一类(都是正类或都是负类)时
		{
			double dGamma = alph1 + alph2;//dGamma,临时变量

			//根据dGamma的值,设置L,H的值
			if( dGamma > GlobalVariable.punish )
			{
				L = dGamma - GlobalVariable.punish;
				H = GlobalVariable.punish;
			}
			else
			{
				L = 0;
				H = dGamma;
			}
		}
		else		//当待优化的两个Lagrange乘子不为同一类时
		{
			double dGamma = alph1 - alph2;//dGamma,临时变量

			//根据dGamma的值,设置L,H的值
			if( dGamma > 0 )
			{
				L = 0;
				H = GlobalVariable.punish - dGamma;
			}
			else
			{
				L = -dGamma;
				H = GlobalVariable.punish;
			}
		}

		//当Lagrange乘子的上下限一样时,不需要操作,返回,因为这样代表Lagrange没有改变的空间
		if( L == H )
			return 0;
		
		//求核函数,便于后面操作
		k11 = calculateKernel(i1,i1);
		k12 = calculateKernel(i1,i2);
		k22 = calculateKernel(i2,i2);

		//设置临时变量eta的初始值
		eta = 2 * k12 - k11 - k22;//改了,原先是-k12

		//依据核函数的形式,eta只能有小于或等于零两种状态
		if( eta < 0 )		//小于0
		{
			//依据推导求得a2,然后根据限制条件取的a2
			a2 = alph2 + y2 * ( E2 - E1 ) / eta;
			if( a2 < L )
				a2 = L;
			else if( a2 > H )
				a2 = H;
		}
		else				//等于0
		{
			double c1 = eta / 2;
			double c2 = y2 * ( E1 - E2 ) - eta * alph2;
			lobj = c1 * L * L + c2 * L;
			hobj = c1 * H * H + c2 * H;
		
			if( lobj > hobj + eps )
				a2 = L;
			else if( lobj < hobj - eps )
				a2 = H;
			else
				a2 = alph2;
		}

		if( Math.abs( a2 - alph2 ) < eps * ( a2 + alph2 + eps ) )
			return 0;

		//依据a2,求得a1
		a1 = alph1 - s * ( a2 - alph2 );

		//a1也不能超出边界
		if( a1 < 0 )
		{
			a2 += s * a1;
			a1 = 0;
		}
		else if( a1 >GlobalVariable.punish  )
		{
			double t = a1 - GlobalVariable.punish;
			a2 += s * t;
			a1 = GlobalVariable.punish;
		}

		//更新阈值b 
		{
			double b1,b2,bnew;
			if( a1 > 0 && a1 <GlobalVariable.punish )
				bnew = b + E1 + y1 * ( a1 - alph1 ) * k11 + y2 * ( a2 - alph2 ) * k12;
			else
			{
				if( a2 > 0 && a2 < GlobalVariable.punish )
					bnew = b + E2 + y1 * ( a1 - alph1 ) * k12 + y2 * ( a2 - alph2 ) * k22;
				else
				{
					b1 = b + E1 + y1 * ( a1 - alph1 ) * k11 + y2 * ( a2 - alph2 ) * k12;
					b2 = b + E2 + y1 * ( a1 - alph1 ) * k12 + y2 * ( a2 - alph2 ) * k22;
					bnew = ( b1 + b2 ) / 2;
				}
			}
			delta_b = bnew - b;
			b = bnew;
		}

		//更新错误率 
		{
			double t1 = y1 * ( a1 - alph1 );
			double t2 = y2 * ( a2 - alph2 );
			for( int i = 0; i < GlobalVariable.webPageNumber ; i++ )
				if( 0 < alph[i] && alph[i] < GlobalVariable.punish )
					eError[i] += t1 * calculateKernel(i1,i) + t2 * calculateKernel(i2,i) - delta_b;
			eError[i1] = 0;
			eError[i2] = 0;
		}
		alph[i1] = a1;
		alph[i2] = a2;

		return 1;
	}
	/**
	 * 	函数功能：依据当前模型,计算第k个网页特征向量在当前平面上的实际值,即计算目标函数值
	 * @param k 第k个网页的特征向量
	 * @return 目标函数值
	 */
	public double func(int k)
	{
		double s = 0;//定义一个临时变量

		//按照公式f(x) = ∑yi*ai*K(i,j) - b 计算f
		for( int i = 0; i < GlobalVariable.webPageNumber; i++ )
			if( alph[i] > 0 )
				s += alph[i] * target[i] *  calculateKernel(i,k);
		s -= b;
		
		return s;
	}
}
