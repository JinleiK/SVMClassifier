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
 * ��ѵ��������SVM����ѵ��,�õ�ģ��
 * @author Administrator 
 *
 */
public class Train {
	
	private double	delta_b=0;	//��ֵ����
	private double eps=0.001;   //Lagrange���ӵ���������
	private int RAND_MAX=300;
	private double alph[]=new double[GlobalVariable.webPageNumber];//Lagrange����
	private double eError[]=new double[GlobalVariable.webPageNumber];//���
	private int target[]=new int[GlobalVariable.webPageNumber];//��¼��ҳ�������ֻ��Ϊ1��-1��ͨ��SetSort������ʵ��
	private double b;//��ƽ����b 
	private List<PageInfo>listPageInfo=new ArrayList<PageInfo>();//ѵ��������ҳ����������
	/**
	 * ����listPageInfo 
	 * @param listPageInfo
	 */
	public void setListPageInfo(List<PageInfo> listPageInfo) {
		this.listPageInfo = listPageInfo;
	}
	/**
	 * ���캯������ʼ����صĲ���
	 */
	public Train()
	{
		for(int i=0;i<GlobalVariable.webPageNumber;i++)
		{
			alph[i]=0;//��ʼ��Lagrange ,Ϊ0
			eError[i]=0; //��ʼ����� ,Ϊ 0
			b=0;//��ʼ��b ,Ϊ 0
		}
	}
	/**
	 * ��������Ϊ��Ҫѵ���������Ϊ1������������Ϊ-1
	 * @param listPageInfo ѵ�����е���ҳ
	 * @param sort ѵ�������
	 */
	public void setSort(int sort)
	{
		//��Ҫѵ���������Ϊ1������������Ϊ-1
		System.out.println(listPageInfo.size());
		for(int i=0;i<listPageInfo.size();i++)
		{
			if(listPageInfo.get(i).getType()==sort)//��Ҫѵ�������
			{
			    //��Ҫѵ���������Ϊ1
				target[i]=1;
			}
			else//�������
			{
				target[i]=-1;
			}
		}
	}
	/**
	 * ��������������������
	 * @param vector1 ��������1
	 * @param vector2 ��������2
	 * @return 
	 */
	public double calculateDotMetrix(List<Double>vector1,List<Double>vector2)
	{
		double sumMetrix=0.0;//�洢���
	    //	����
		for(int i=0;i<vector1.size();i++)
		{
			sumMetrix+=vector1.get(i)*vector2.get(i);
		}
		return sumMetrix;
	}
	/**
	 * ����������������RBF�˺���
	 * @param suffix1 ��ѵ�����е�λ��
	 * @param suffix2 ��ѵ�����е�λ��
	 * @return
	 */
	public double calculateKernel(int suffix1,int suffix2)
	{
		//�����ҳ����������
		List<Double>vector1=listPageInfo.get(suffix1).getEigenvalue();
		List<Double>vector2=listPageInfo.get(suffix2).getEigenvalue();
		
		//����˺���
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
	 * ѵ��SVM
	 * @param sort Ҫѵ������ҳ���
	 */
	public void learning(int sort)
	{
		//Ӧ���и���0�Ĺ���,����Lagrange,w,b�ȶ�Ӧ�õ�����,��ͷ��ʼ
		//��һ ����ƽ�棬��Ϊÿ�����Ҫѵ���õ�һ����ƽ�棬����ÿ��ѧϰʱ�����ƽ�涼��Ҫ���¶���
		HyperPlane hyperPlane=new HyperPlane();//����һ����ƽ��
		
		//����Ǹ���ֵ
		//ȫ������ֵΪ0,�����Ժ����
		hyperPlane.setIntercept(0.0);
		b = 0;
		//����double[],��ʱ�洢w��Lagrange
		double tempLagrange[]=new double[GlobalVariable.webPageNumber];
		double tempVerticalVector[]=new double[GlobalVariable.vectorNumber];
		//ΪLagrange����ֵ
		for( int i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			tempLagrange[i]=0.0;
			alph[i] = 0.0;
		}
		hyperPlane.setLagrange(tempLagrange);
		
		//Ϊw����ֵ,w��Ϊ��ƽ�淨����
		for( int i = 0; i <GlobalVariable.vectorNumber ; i++ )
		{
			tempVerticalVector[i]=0.0;
		}
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//���ú��ʵ������,ֻ��Ϊ����,����͸���
		setSort(sort);

		int numChanged = 0;//ѵ�����ĸ�����ҳ��Lagrange�����Ƿ��иı��,"1"��ʾ"��","0"��ʾ"��"
		Boolean examineAll = true;//�Ƿ�Ҫ��ѯȫ����ѵ����ҳ
		int numTT = 0;//�����Ĵ���

		//�����е�ѵ����ҳ��Lagrange���Ӷ�δ�ı䲢���Ѿ���ѯ��ȫ����ѵ��
		//��ҳ֮����ߵ��������Ѿ�����150����,�����ѭ��
		while(( numChanged > 0 || examineAll ) && numTT < 150 )// 
		{
			numChanged = 0;//��Ϊ0,��ʾ���¿�ʼ,�����е�Lagrange���Ӷ�δ��

			if( examineAll )//��ѯȫ������������
			{
				for( int t = 0; t <GlobalVariable.webPageNumber ; t++ )
					numChanged += exameTrainingSet(t);//������ҳ��Lagrange����
													//�ı�,��numChanged��1,�Ըı���ֵ
			}
			else			//ֻ��ѯLagrangeֵ��Ϊ0�Ҳ�ΪdPunish����������,���Ǳ߽�����
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

			//��������examineAll��numChanged��ֵ,�Խ�����һ�ε�ѭ��
			if( examineAll == true )
			{
				examineAll = false;
				numTT++;//����������1
			}
			else if( numChanged == 0 )
			{
				examineAll = true;
				numTT++;//����������1
			}
		
		}

		//������õ���ֵ,������ƽ����Ӧ��ֵ
		for(int  i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			tempLagrange[i]=alph[i];
		}
		hyperPlane.setLagrange(tempLagrange);
		
		//��bֵ������ƽ��
		hyperPlane.setIntercept(b);
		
		//��ⳬƽ��ķ���������
		hyperPlane=getVerticalVector(hyperPlane);

		//����ƽ��д���ļ���
		tempLagrange=hyperPlane.getLagrange();//���Lagrange
		tempVerticalVector=hyperPlane.getVerticalVector();//���w
		//����������ʱ�������洢Lagrange VerticalVector
		StringBuffer strLagrange=new StringBuffer();
		StringBuffer strVerticalVector=new StringBuffer();
		
		//ΪstrLagrange strVerticalVector ��ֵ 
		for(int i=0;i<tempLagrange.length;i++)
		{
			strLagrange.append(tempLagrange[i]).append(" ");
		}
		for(int i=0;i<tempVerticalVector.length;i++)
		{
			strVerticalVector.append(tempVerticalVector[i]).append(" ");
		}
		
		File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt");
		//���������	
		
		try {
			FileOutputStream outputStream=new FileOutputStream(file,true);
			try {
				
				outputStream.write(("��"+sort+"���ĳ�ƽ��").getBytes());
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write("��ƽ���Lagrange".getBytes());
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write(strLagrange.toString().getBytes());//��Lagrangeд���ļ�
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write("��ƽ��ķ�����".getBytes());
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write(strVerticalVector.toString().getBytes());//��VerticalVectorд���ļ�
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write("��ƽ���bֵ".getBytes());
				outputStream.write("\r\n".getBytes());//д��һ�����з�
				outputStream.write(String.valueOf(b).getBytes());//��bд���ļ�
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
	 * ��ⳬƽ��ķ�����
	 * @param hyperPlane ��ƽ�棨��ʱ�ĳ�ƽ��ķ�����û���������ֻ�洢��������Lagrange
	 */
	public HyperPlane getVerticalVector(HyperPlane hyperPlane)
	{
		//������ʱ��������ʱ�洢��
		double tempVerticalVector[]=new double[GlobalVariable.vectorNumber];
		//����ֵΪ0,�Ա��������Ĳ���
		for( int i = 0; i <GlobalVariable.vectorNumber; i++ )
			tempVerticalVector[i] = 0;
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//���Lagrange 
		 double tempLagrange[]=hyperPlane.getLagrange();
		
		//������صĹ�ʽ,�������ѵ����ҳ�Ħ�
		for(int i = 0; i <GlobalVariable.webPageNumber ; i++ )
		{
			//��ø���ҳ����������
			List <Double> listEigenvalue=listPageInfo.get(i).getEigenvalue();
			for( int j = 0; j <GlobalVariable.vectorNumber ;j++ )
			{
				//��صĹ�ʽ
				tempVerticalVector[j] += tempLagrange[i] * target[i] *listEigenvalue.get(j) ;
			}
		}
		//���ؼ��뵽��ƽ����
		hyperPlane.setVerticalVector(tempVerticalVector);
		
		//���س�ƽ��
		return hyperPlane;
	}
	/**
	 * ����һ�������Ƿ�Υ��KKT���������������KKT��������Ѱ�ҵڶ���Ȩ����������takeStep��������������Ȩֵ
	 * @param i Lagrange�����±�
	 * @return �����Ȩ�����������£�����1���򷵻�0
	 */
	
	public int exameTrainingSet(int i1)
	{
		//������ر���,�Ա���в���
		int y1;			//��i1��ı��,Ϊ1��-1(ֻ������͸����������)
		double alph1;	//���Ż��ĵ�һ��Lagrange����
		double E1;		//������
		double r1;		//����y1*E1����ʱ����,�ж�KKT�����ı���

		//����y1,alph1�ĳ�ֵ
		y1 = target[i1];
		alph1 = alph[i1];

		//����E1�ĳ�ֵ
		if( alph1 > 0 && alph1 < GlobalVariable.punish )
			E1 = eError[i1];
		else
			E1 = func(i1) - y1;

		//����r1��ֵ
		r1 = y1 * E1;

		if(( r1 < - GlobalVariable.tolerance && alph1 < GlobalVariable.tolerance ) || ( r1 > GlobalVariable.tolerance && alph1 > 0 ))//������KKT����
		{
			//Ѱ�ҵڶ���Ȩֵ���£�������
			//Ѱ��|E1-E2|���ģ�
			{
				int k, i2;//k,ѭ������,i2,�ڶ������Ż���Lagrange����
				double tmax;//�������|E1-E2|��ֵ
				
				//����ֵ
				i2	 = -1;
				tmax =  0;

				//ѭ�����ҵ�Ҫ�Ż��ĵڶ���Lagrange���ӵ��±�
				for( k = 0; k <GlobalVariable.webPageNumber ; k++ )
					if( alph[k] > 0 && alph[k] <  GlobalVariable.punish  )
					{
						double E2;	//������
						double temp;//��ʱ����,������������ʵĲ�ľ���ֵ				

						//��ֵ
						E2 = eError[k];
						temp = Math.abs( E1 - E2 );

						//��������temp,
						if( temp > tmax )
						{
							tmax = temp;
							i2 = k;
						}
					}

				//��ʾ�ҵ��˵ڶ���Lagrange����
				if( i2 >= 0 )
				{
					if( takeStep( i1, i2 )==1 )//�Ż���i1,i2��Lagrange����
						return 1;
				}
			}

			//����Ĳ���û���ҵ�i2,��ִ�������
			//Ѱ�ҷǱ߽�����,�����λ�ÿ�ʼ,�����㷨ÿһ�ο�ʼ������̶��ķ���Ѱ��,
			//����,RAND_MAX = 0x7fff
			{
				int k;	//ѭ������,
				int k0;	//��������ķ���λ��
				int i2;	//��i2��Lagrange����

				//����k0
				k0 = (int)(( Math.random() / RAND_MAX ) * GlobalVariable.webPageNumber  );

				//ѭ������i2
				for( k = k0; k < GlobalVariable.webPageNumber + k0; k++ )
				{
					i2 = k % GlobalVariable.webPageNumber;
					if( alph[i2] > 0 && alph[i2] <GlobalVariable.punish )//�Ǳ߽�����������
					{
						if( takeStep(i1, i2)==1 )//�Ż���i1,i2��Lagrange����
							return 1;
					}
				}
			}

			//����Ĳ��軹û���ҵ�i2,��ִ�������
			//Ѱ���������������λ�ÿ�ʼ,�����㷨ÿһ�ο�ʼ������̶��ķ���Ѱ��,
			//����,RAND_MAX = 0x7fff
			{
				int k0;	//�漴�����Ĳ�ѯλ��
				int k;	//ѭ������
				int i2;	//��i2��Lagrange����

				//����k0
				k0 = (int)( ( Math.random()/ RAND_MAX ) *GlobalVariable.webPageNumber );

				//ѭ������i2
				for( k = k0; k <GlobalVariable.webPageNumber + k0; k++ )
				{
					i2 = k % GlobalVariable.webPageNumber ;
					if( takeStep( i1, i2 )==1 )//�ҵ�,�Ż���i1,i2��Lagrange����
						return 1;
				}
			}
		}
		return 0;
	}
	/**
	 * �Ż�����Lagrange����
	 * @param i1 ��һ������
	 * @param i2 �ڶ�������
	 * @return ���Ż��ɹ�������1�����ɹ�������-1
	 */
	public int takeStep(int i1,int i2)
	{
		//������ر���,�Ա���в���
		int y1, y2;	//��i1,i2��ı��,Ϊ1��-1(ֻ������͸����������)
		int s;		//��ʱ����,ֵ����y1*y2
		double alph1, alph2;//�ɵ�Lagrangeֵ
		double a1, a2;		//�µ�Lagrangeֵ
		double E1, E2;		//������
		double H, L;		//��i2��Lagrange���ӵ��ϡ�����
		double k11, k22, k12;//Ӧ�ú˺�����õ�ֵ
		double eta;			//��ʱ����,��ֵΪ2*k12-k11-k12
		double lobj, hobj;	//����i2��LagrangeֵΪL,Hʱ��Ŀ�꺯��(�б���)ֵ

		//�����Ż�������Lagrange����Ϊͬһ��ʱ,�򲻻���в���
		if( i1 == i2 )
			return 0;

		//��alph1,y1,E1����ֵ
		alph1 = alph[i1];
		y1 = target[i1];

		if( alph1 > 0 && alph1 <GlobalVariable.punish)
			E1 = eError[i1];
		else
			E1 = func(i1) - y1;

		//��alph2,y2,E2����ֵ
		alph2 = alph[i2];
		y2 = target[i2];

		if( alph2 > 0 && alph2 < GlobalVariable.punish )
			E2 = eError[i2];
		else
			E2 = func(i2) - y2;

		//����s�ĳ�ֵ
		s = y1 * y2;
		
		if( y1 == y2 )//�����Ż�������Lagrange���Ӷ�Ϊͬһ��(����������Ǹ���)ʱ
		{
			double dGamma = alph1 + alph2;//dGamma,��ʱ����

			//����dGamma��ֵ,����L,H��ֵ
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
		else		//�����Ż�������Lagrange���Ӳ�Ϊͬһ��ʱ
		{
			double dGamma = alph1 - alph2;//dGamma,��ʱ����

			//����dGamma��ֵ,����L,H��ֵ
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

		//��Lagrange���ӵ�������һ��ʱ,����Ҫ����,����,��Ϊ��������Lagrangeû�иı�Ŀռ�
		if( L == H )
			return 0;
		
		//��˺���,���ں������
		k11 = calculateKernel(i1,i1);
		k12 = calculateKernel(i1,i2);
		k22 = calculateKernel(i2,i2);

		//������ʱ����eta�ĳ�ʼֵ
		eta = 2 * k12 - k11 - k22;//����,ԭ����-k12

		//���ݺ˺�������ʽ,etaֻ����С�ڻ����������״̬
		if( eta < 0 )		//С��0
		{
			//�����Ƶ����a2,Ȼ�������������ȡ��a2
			a2 = alph2 + y2 * ( E2 - E1 ) / eta;
			if( a2 < L )
				a2 = L;
			else if( a2 > H )
				a2 = H;
		}
		else				//����0
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

		//����a2,���a1
		a1 = alph1 - s * ( a2 - alph2 );

		//a1Ҳ���ܳ����߽�
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

		//������ֵb 
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

		//���´����� 
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
	 * 	�������ܣ����ݵ�ǰģ��,�����k����ҳ���������ڵ�ǰƽ���ϵ�ʵ��ֵ,������Ŀ�꺯��ֵ
	 * @param k ��k����ҳ����������
	 * @return Ŀ�꺯��ֵ
	 */
	public double func(int k)
	{
		double s = 0;//����һ����ʱ����

		//���չ�ʽf(x) = ��yi*ai*K(i,j) - b ����f
		for( int i = 0; i < GlobalVariable.webPageNumber; i++ )
			if( alph[i] > 0 )
				s += alph[i] * target[i] *  calculateKernel(i,k);
		s -= b;
		
		return s;
	}
}
