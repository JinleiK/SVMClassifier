package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;


import struct.PageInfo;
import test.TestWebPageSort;
import train.IncreamentalTrain;
import file.ReadTrainingSet;
import global.GlobalVariable;
import global.svset;
import global.Evaluation;

public class SVMTrain extends JFrame{
	//��������е���ؿؼ�
	JPanel northJPanel=new JPanel();
	JPanel southJPanel =new JPanel();
	JPanel westJPanel=new JPanel();
	JPanel centerJPanel=new JPanel();
	
	JLabel defineJLabel=new JLabel("��ӭʹ��SVM����������");
	JLabel defineJLabel2=new JLabel("     ��ʼѵ��ʱ�䣺");
	JLabel defineJLabel3=new JLabel("                    ����ѵ��ʱ�䣺");
	JButton openTrainSetJB=new JButton("��ȡѵ����");
	JButton trainningJB=new JButton("ѵ��");
	JButton openSVMModelJB=new JButton("��ȡSVMģ��");
	JButton openTestSetJB=new JButton("��ȡ���Լ�");
	JButton showSortResultJB=new JButton("��ʾ������");
	JButton openIncrementalSetJB=new JButton("��ȡ��������");
	JButton incrementaltrainningJB=new JButton("����ѵ��");

	
	String sortName[]=new String[]{"������","������","ʱ����","�������"};
	JTree jTreeSort = new JTree(sortName);
	JTextArea first,second,first1,second1,first2,second2,first3,second3;
	JTextArea first4,second4,three,four;
	private Boolean bool=false;//��ʶ�ڼ�����ʾ������
	
	//����һЩ��ʱ����
	private Boolean isOpenTrainSet=false;//�Ƿ��ѵ����
	private Boolean isTraining =false;//�Ƿ��Ѿ�ѵ��
	private Boolean isOpenSVMModel=false;
	
	private File  fileSVMModel;//�洢ģ���ļ�
	private File fileTestingSet;//�洢���Լ��ļ�
	public SVMTrain(String title)
	{
		
		northJPanel.add(defineJLabel);
		GridLayout gridLayout1=new GridLayout(10,1,40,0);
		westJPanel.setLayout(gridLayout1);
		openTrainSetJB.addActionListener(new openTrainSetHandel());
		westJPanel.add(openTrainSetJB);
		trainningJB.addActionListener(new trainningHandel());
		westJPanel.add(trainningJB);
		openSVMModelJB.addActionListener(new openSVMModelHandel());
		westJPanel.add(openSVMModelJB);
		openTestSetJB.addActionListener(new openTestSetHandel());
		westJPanel.add(openTestSetJB);
		showSortResultJB.addActionListener(new showSortResultHandel());
		westJPanel.add(showSortResultJB);
		openIncrementalSetJB.addActionListener(new openIcrementalTrainSetHandel());
		westJPanel.add(openIncrementalSetJB);
		incrementaltrainningJB.addActionListener(new IncresmentaltrainningHandel());
		westJPanel.add(incrementaltrainningJB);
		
		jTreeSort.addTreeSelectionListener(new selectionHandler());
		GridLayout gridLayout2=new GridLayout(5,2);
		centerJPanel.setLayout(gridLayout2);
		first=new JTextArea();
		second=new JTextArea();
		first1=new JTextArea();
		second1=new JTextArea();
		first2=new JTextArea();
		second2=new JTextArea();
		first3=new JTextArea();
		second3=new JTextArea();
		first4=new JTextArea();
		second4=new JTextArea();
		global.Evaluation.three=new JTextArea();
		four=new JTextArea();
		Font f=new Font("����",Font.TYPE1_FONT, 18);
		first.setFont(f);
		first1.setAlignmentY((float) 10.0);
		first.append("\n         ����ǰ���Խ����\n");
		second.setFont(f);
		second.append("\n   ��������Խ����\n");
		centerJPanel.add(first);
		centerJPanel.add(second);
		centerJPanel.add(first1);
		centerJPanel.add(second1);
		centerJPanel.add(first2);
		centerJPanel.add(second2);
		centerJPanel.add(first3);
		centerJPanel.add(second3);
		centerJPanel.add(first4);
		centerJPanel.add(second4);
		
		
		southJPanel.add(defineJLabel2);
		southJPanel.add(global.Evaluation.three);
		southJPanel.add(defineJLabel3);
		southJPanel.add(four);
		
		
		Container contentPane=getContentPane();
		contentPane.add(northJPanel,BorderLayout.NORTH);
		contentPane.add(southJPanel,BorderLayout.SOUTH);
		contentPane.add(westJPanel,BorderLayout.WEST);
		contentPane.add(centerJPanel,BorderLayout.CENTER);
		
		
		//���ý���Ĵ�С��λ��
		setSize(680,500);
		setLocation(350,200);
		setVisible(true);
	}
	/**
	 * ����"��ȡ���Լ�"�¼�
	 */
	class openTestSetHandel  implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			JFileChooser jc=new JFileChooser();
			File file=new File("F:\\DeTreeSVM\\���Լ�");
			jc.setSelectedFile(file);
			jc.setFileFilter(new MyFileFilter());//�����ļ�������
			
			jc.setDialogTitle("ѡ����Լ�"); 
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
				//File dirDictionary=jc.getCurrentDirectory();//����ļ���Ŀ¼
				fileTestingSet=jc.getSelectedFile();//���ģ���ļ�	
		}
		}
		
	}
	/**
	 * ����"��ģ��"�¼�
	 */
	class openSVMModelHandel implements  ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			
			if(isTraining)
			{
				JOptionPane.showConfirmDialog(null, "���Ѿ�ѵ���ˣ�����Ҫ�ڴ�SVMģ����","��ʾ",JOptionPane.CLOSED_OPTION);
			}
			else
			{
				isOpenSVMModel=true;
				JFileChooser jc=new JFileChooser();
				File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src");
				jc.setSelectedFile(file);
				jc.setFileFilter(new MyFileFilter());//�����ļ�������
				jc.setDialogTitle("ѡ��SVMģ��");
				int rVal=jc.showOpenDialog(SVMTrain.this);
				if(rVal==JFileChooser.APPROVE_OPTION){
					 fileSVMModel=jc.getSelectedFile();//���ģ���ļ�	
			}
		}
		
		}
	}
	/**
	 * ����Tree�¼�
	 * @author Administrator
	 *
	 */
	class selectionHandler implements TreeSelectionListener
	{
	
		public void valueChanged(TreeSelectionEvent e) {
			System.out.println("�����ѡȡ��");
			
			
		}
		
	}
	/**
	 * ����"��ѵ����"�¼�
	 */
	class openTrainSetHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			 isOpenTrainSet=true;//ѵ�����Ѿ���
			 
			JFileChooser jc=new JFileChooser();//Ϊ�û�ѡ���ļ��ṩ��һ�ֻ��ƣ�File choosers provide a GUI for navigating the file system
			File file=new File("F:\\DeTreeSVM\\ѵ����");
			jc.setSelectedFile(file);
			
			jc.setFileFilter(new MyFileFilter());//�����ļ�������
			jc.setDialogTitle("ѡ��ѵ����");
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
				File fileTrainingSet=jc.getSelectedFile();//���ѵ�����ļ�
				
				//��ȡѵ������Ϣ
				ReadTrainingSet readTrainingSet=new ReadTrainingSet();
				//File fileTrainingSet=new File("D:\\��ҵ�������\\DeTreeSVM\\DeTreeSVM\\ѵ����\\PageVector.txt");
				List<PageInfo>listPageInfo=readTrainingSet.getTrainingSet(fileTrainingSet);
				
				System.out.println(listPageInfo.size());
				System.out.println("�����");
				System.out.println(readTrainingSet.getWebPageSort(listPageInfo));
				GlobalVariable.sortNumber=readTrainingSet.getWebPageSort(listPageInfo);
				GlobalVariable.listPageInfo=listPageInfo;
				/*Train train=new Train();
				train.setListPageInfo(listPageInfo);*/
				
				GlobalVariable.tolerance=0.001;//�����������Χ
			}
		}
	}
	/**
	 *����"ѵ��"�¼�
	 */
	class  trainningHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			if(isOpenTrainSet)//ѵ�����Ѵ�
			{
				isTraining=true;
				new SelectParamters("����ѡ��");
			}
			else//ѵ����û��
			{
				JOptionPane.showConfirmDialog(null, "���ȴ�ѵ����","��ʾ",JOptionPane.CLOSED_OPTION);
			}
		}
		
	}
	/**
	 * ����"��ʾ���"�¼�
	 */
	class showSortResultHandel implements ActionListener
	{
		
		    public void actionPerformed(ActionEvent e) {
			ReadTrainingSet readTrainingSet=new ReadTrainingSet();
			TestWebPageSort testWebPageSort=new TestWebPageSort();
			
			int sortResult[];//�洢������
			if(isOpenSVMModel)
			{
				sortResult=testWebPageSort.getWebPageSort(fileTestingSet, fileSVMModel);
			}
			else
			{
				 sortResult=testWebPageSort.getWebPageSort(fileTestingSet, new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt"));
				
			}
			System.out.println("���Խ��");
			for(int i=0;i<sortResult.length;i++)
				System.out.println(sortResult[i]);
			
			System.out.println("ÿ��������ҳ��");
			List<PageInfo>listTestPageInfo=readTrainingSet.getTrainingSet(fileTestingSet);
			 GlobalVariable.sortNumber=readTrainingSet.getWebPageSort(listTestPageInfo);
			System.out.println("���Լ�����");
			System.out.println(listTestPageInfo.size());
			int[]eachSortNumber=readTrainingSet.getEachSortCount(listTestPageInfo);
			System.out.println(eachSortNumber.length);
			for(int i=0;i<eachSortNumber.length;i++)
			{
				System.out.println(eachSortNumber[i]);
			}
			System.out.println("��׼�ʺͲ�ȫ��");
			double [][]precisionAndRecall=testWebPageSort.calculatePrecisionAndRecall(eachSortNumber, sortResult,bool);
			
			if(bool)
			{
				first1.append("     ������:    "+"\n\t��ȫ��:"+Evaluation.firstValue[0][0]+"\n\t��׼��:"+Evaluation.firstValue[1][0]);
				first2.append("\n    ������:    "+"\n\t��ȫ��:"+Evaluation.firstValue[0][1]+"\n\t��׼��:"+Evaluation.firstValue[1][1]);
				first3.append("\n    ʱ����:    "+"\n\t��ȫ��"+Evaluation.firstValue[0][2]+"\n\t��׼��:"+Evaluation.firstValue[1][2]);
				first4.append("\n   �������:    "+"\n\t��ȫ��:"+Evaluation.firstValue[0][3]+"\n\t��׼��:"+Evaluation.firstValue[1][3]);
				second1.append("\n        ��ȫ��:"+Evaluation.secondValue[0][0]+"\n        ��׼��:"+Evaluation.secondValue[1][0]);
				second2.append("\n\n        ��ȫ��:"+Evaluation.secondValue[0][1]+"\n        ��׼��:"+Evaluation.secondValue[1][1]);
				second3.append("\n\n        ��ȫ��:"+Evaluation.secondValue[0][2]+"\n        ��׼��:"+Evaluation.secondValue[1][2]);
				second4.append("\n\n        ��ȫ��:"+Evaluation.secondValue[0][3]+"\n        ��׼��:"+Evaluation.secondValue[1][3]);
			}
			bool=true;
			for(int i=0;i<precisionAndRecall.length;i++)
			{
				for(int j=0;j<precisionAndRecall[0].length;j++)
				{
					System.out.print(precisionAndRecall[i][j]);
					System.out.print(" ");
				}
				System.out.println();
			}
			
			//��precisionAndRecall���л������뵽data��
			double[][] data=new double[precisionAndRecall[0].length][precisionAndRecall.length];
			
			for(int i=0;i<precisionAndRecall.length;i++)
			{
				for(int j=0;j<precisionAndRecall[0].length;j++)
				{
					data[j][i]=precisionAndRecall[i][j];
				}
			}
			
			//�������ͼ���ʾ����
			String[] rowKeys = {"��׼��","��ȫ��"};
			String[] columnKeys = {"����","����","ʱ��","����"};
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);
			JFreeChart chart = ChartFactory.createBarChart3D("��׼�����ȫ��",null,null,dataset,
			PlotOrientation.VERTICAL,true,false,false);
			chart.setBackgroundPaint(Color.WHITE);
			CategoryPlot plot = chart.getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
			
			plot.setDomainAxis(domainAxis);
			ValueAxis rangeAxis = plot.getRangeAxis();
			//������ߵ�һ�� Item ��ͼƬ���˵ľ���
			rangeAxis.setUpperMargin(0.15);
			//������͵�һ�� Item ��ͼƬ�׶˵ľ���
			rangeAxis.setLowerMargin(0.15);
			plot.setRangeAxis(rangeAxis);
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setBaseOutlinePaint(Color.BLACK);
			//���� Wall ����ɫ<BR>
			renderer.setWallPaint(Color.gray);
			//����ÿ��ˮ�������������ɫ
			//renderer.setSeriesPaint(0, new Color(0, 0, 255));
			renderer.setSeriesPaint(0, new Color(0, 100, 255));
			renderer.setSeriesPaint(1, Color.GREEN);
			//����ÿ��������������ƽ������֮�����
			renderer.setItemMargin(0.1);
			//��ʾÿ��������ֵ�����޸ĸ���ֵ����������<BR>
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setItemLabelsVisible(true);
			plot.setRenderer(renderer);
			//��������͸����<BR>
			plot.setForegroundAlpha(0.6f);
			//������𡢲�׼�����ȫ�ʵ���ʾλ��<BR>
			plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
			plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			ChartFrame cframe=new ChartFrame("����Ĳ�׼�����ȫ��",chart);
	        cframe.pack();
	        cframe.setLocation(400, 250);
	        cframe.setVisible(true);
		}
		
	}
	/**
	 * ����"ѡ����������"�¼�
	 */
	class openIcrementalTrainSetHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			JFileChooser jc=new JFileChooser();//Ϊ�û�ѡ���ļ��ṩ��һ�ֻ��ƣ�File choosers provide a GUI for navigating the file system
			File file=new File("F:\\DeTreeSVM\\ѵ����");
			jc.setSelectedFile(file);
			jc.setFileFilter(new MyFileFilter());//�����ļ�������
			jc.setDialogTitle("ѡ����������");   
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
			     fileTestingSet=jc.getSelectedFile();//�������ѵ�����ļ�
				
		}
		}
		
	
	}
	
	/**
	 * ����"����ѵ��"�¼�
	 */
	
	class IncresmentaltrainningHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			TestWebPageSort testWebPageSort=new TestWebPageSort();
			int sortResult[]=testWebPageSort.getWebPageSort(fileTestingSet, 
					new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt"));
			
			ReadTrainingSet readTrainingSet=new ReadTrainingSet();
		
			GlobalVariable.listPageInfo=readTrainingSet.getTrainingSet(fileTestingSet);
			int[]eachSortNumber=readTrainingSet.getEachSortCount(GlobalVariable.listPageInfo);
			
			
		   int enumber= testWebPageSort.IncramentalTrain(eachSortNumber, sortResult);
		   int time=0;
		 //����һ���µ��ļ����洢ѵ��ģ��
			File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt");
			if(file.exists() && file.isFile() )
			{file.delete();}
			long time1 =System.currentTimeMillis();
			
		   while (enumber!=0&&time<1)
		  {
			   IncreamentalTrain intrain=new IncreamentalTrain();
		    	intrain.setListPageInfo(svset.svAndEpage);
		    	GlobalVariable.delta=10.0;
				GlobalVariable.punish=10.0;
		
				if(file.exists() && file.isFile() )
				{file.delete();}
				
		    	intrain.learning(1);
		    	intrain.learning(2);
		    	intrain.learning(3);
		    	intrain.learning(4);
		    	
		    	int sortResult1[]=testWebPageSort.getIncreamentalWebPageSort(svset.nsvAndYpage,file);
				
			   readTrainingSet=new ReadTrainingSet();
			   GlobalVariable.listPageInfo=svset.nsvAndYpage;
				eachSortNumber=readTrainingSet.getEachSortCount(GlobalVariable.listPageInfo);
			   enumber= testWebPageSort.IncramentalTrain1(eachSortNumber, sortResult1);
		    	time++;
		    	//enumber=0;
		  
		   }
		  
		   long time2=System.currentTimeMillis();
		   long time3=time2-time1;
		   four.append(""+time3/1000);
		   JOptionPane.showConfirmDialog(null, "����ѵ������","��ʾ",JOptionPane.CLOSED_OPTION);
		}
	}
	
	
	
	
	
	
	
	/**
	 * �ļ�����������ƣ�ֻ��.txt�ļ�
	 */
	class MyFileFilter extends FileFilter
	{
		//��������
		public String getDescription()
		{
			return "�ı��ļ�";
		}
		//����file���ж��Ƿ���ʾ
		public boolean accept(File file)
		{
			String fileName = file.getName();
			if(fileName!=null&&(fileName.indexOf(".txt")>0||file.isDirectory()))
			{
				return true;
			}
			return false;
		}
	}
	public static void main(String[] arg){
		new SVMTrain("svm��ҳ������");
	}
	
}
