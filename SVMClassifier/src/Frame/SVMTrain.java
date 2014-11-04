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
	//定义界面中的相关控件
	JPanel northJPanel=new JPanel();
	JPanel southJPanel =new JPanel();
	JPanel westJPanel=new JPanel();
	JPanel centerJPanel=new JPanel();
	
	JLabel defineJLabel=new JLabel("欢迎使用SVM增量分类器");
	JLabel defineJLabel2=new JLabel("     初始训练时间：");
	JLabel defineJLabel3=new JLabel("                    增量训练时间：");
	JButton openTrainSetJB=new JButton("读取训练集");
	JButton trainningJB=new JButton("训练");
	JButton openSVMModelJB=new JButton("读取SVM模型");
	JButton openTestSetJB=new JButton("读取测试集");
	JButton showSortResultJB=new JButton("显示分类结果");
	JButton openIncrementalSetJB=new JButton("读取增量样本");
	JButton incrementaltrainningJB=new JButton("增量训练");

	
	String sortName[]=new String[]{"教育类","娱乐类","时尚类","计算机类"};
	JTree jTreeSort = new JTree(sortName);
	JTextArea first,second,first1,second1,first2,second2,first3,second3;
	JTextArea first4,second4,three,four;
	private Boolean bool=false;//标识第几次显示分类结果
	
	//定义一些临时变量
	private Boolean isOpenTrainSet=false;//是否打开训练集
	private Boolean isTraining =false;//是否已经训练
	private Boolean isOpenSVMModel=false;
	
	private File  fileSVMModel;//存储模型文件
	private File fileTestingSet;//存储测试集文件
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
		Font f=new Font("宋体",Font.TYPE1_FONT, 18);
		first.setFont(f);
		first1.setAlignmentY((float) 10.0);
		first.append("\n         增量前测试结果：\n");
		second.setFont(f);
		second.append("\n   增量后测试结果：\n");
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
		
		
		//设置界面的大小和位置
		setSize(680,500);
		setLocation(350,200);
		setVisible(true);
	}
	/**
	 * 处理"读取测试集"事件
	 */
	class openTestSetHandel  implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			JFileChooser jc=new JFileChooser();
			File file=new File("F:\\DeTreeSVM\\测试集");
			jc.setSelectedFile(file);
			jc.setFileFilter(new MyFileFilter());//增加文件过滤器
			
			jc.setDialogTitle("选择测试集"); 
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
				//File dirDictionary=jc.getCurrentDirectory();//获得文件的目录
				fileTestingSet=jc.getSelectedFile();//获得模型文件	
		}
		}
		
	}
	/**
	 * 处理"打开模型"事件
	 */
	class openSVMModelHandel implements  ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			
			if(isTraining)
			{
				JOptionPane.showConfirmDialog(null, "您已经训练了，不需要在打开SVM模型了","提示",JOptionPane.CLOSED_OPTION);
			}
			else
			{
				isOpenSVMModel=true;
				JFileChooser jc=new JFileChooser();
				File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src");
				jc.setSelectedFile(file);
				jc.setFileFilter(new MyFileFilter());//增加文件过滤器
				jc.setDialogTitle("选择SVM模型");
				int rVal=jc.showOpenDialog(SVMTrain.this);
				if(rVal==JFileChooser.APPROVE_OPTION){
					 fileSVMModel=jc.getSelectedFile();//获得模型文件	
			}
		}
		
		}
	}
	/**
	 * 处理Tree事件
	 * @author Administrator
	 *
	 */
	class selectionHandler implements TreeSelectionListener
	{
	
		public void valueChanged(TreeSelectionEvent e) {
			System.out.println("树结点选取了");
			
			
		}
		
	}
	/**
	 * 处理"打开训练集"事件
	 */
	class openTrainSetHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			 isOpenTrainSet=true;//训练集已经打开
			 
			JFileChooser jc=new JFileChooser();//为用户选择文件提供了一种机制，File choosers provide a GUI for navigating the file system
			File file=new File("F:\\DeTreeSVM\\训练集");
			jc.setSelectedFile(file);
			
			jc.setFileFilter(new MyFileFilter());//增加文件过滤器
			jc.setDialogTitle("选择训练集");
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
				File fileTrainingSet=jc.getSelectedFile();//获得训练集文件
				
				//读取训练集信息
				ReadTrainingSet readTrainingSet=new ReadTrainingSet();
				//File fileTrainingSet=new File("D:\\毕业设计资料\\DeTreeSVM\\DeTreeSVM\\训练集\\PageVector.txt");
				List<PageInfo>listPageInfo=readTrainingSet.getTrainingSet(fileTrainingSet);
				
				System.out.println(listPageInfo.size());
				System.out.println("类别数");
				System.out.println(readTrainingSet.getWebPageSort(listPageInfo));
				GlobalVariable.sortNumber=readTrainingSet.getWebPageSort(listPageInfo);
				GlobalVariable.listPageInfo=listPageInfo;
				/*Train train=new Train();
				train.setListPageInfo(listPageInfo);*/
				
				GlobalVariable.tolerance=0.001;//设置误差容许范围
			}
		}
	}
	/**
	 *处理"训练"事件
	 */
	class  trainningHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			if(isOpenTrainSet)//训练集已打开
			{
				isTraining=true;
				new SelectParamters("参数选择");
			}
			else//训练集没打开
			{
				JOptionPane.showConfirmDialog(null, "请先打开训练集","提示",JOptionPane.CLOSED_OPTION);
			}
		}
		
	}
	/**
	 * 处理"显示结果"事件
	 */
	class showSortResultHandel implements ActionListener
	{
		
		    public void actionPerformed(ActionEvent e) {
			ReadTrainingSet readTrainingSet=new ReadTrainingSet();
			TestWebPageSort testWebPageSort=new TestWebPageSort();
			
			int sortResult[];//存储分类结果
			if(isOpenSVMModel)
			{
				sortResult=testWebPageSort.getWebPageSort(fileTestingSet, fileSVMModel);
			}
			else
			{
				 sortResult=testWebPageSort.getWebPageSort(fileTestingSet, new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt"));
				
			}
			System.out.println("测试结果");
			for(int i=0;i<sortResult.length;i++)
				System.out.println(sortResult[i]);
			
			System.out.println("每个类别的网页数");
			List<PageInfo>listTestPageInfo=readTrainingSet.getTrainingSet(fileTestingSet);
			 GlobalVariable.sortNumber=readTrainingSet.getWebPageSort(listTestPageInfo);
			System.out.println("测试集长度");
			System.out.println(listTestPageInfo.size());
			int[]eachSortNumber=readTrainingSet.getEachSortCount(listTestPageInfo);
			System.out.println(eachSortNumber.length);
			for(int i=0;i<eachSortNumber.length;i++)
			{
				System.out.println(eachSortNumber[i]);
			}
			System.out.println("查准率和查全率");
			double [][]precisionAndRecall=testWebPageSort.calculatePrecisionAndRecall(eachSortNumber, sortResult,bool);
			
			if(bool)
			{
				first1.append("     教育类:    "+"\n\t查全率:"+Evaluation.firstValue[0][0]+"\n\t查准率:"+Evaluation.firstValue[1][0]);
				first2.append("\n    娱乐类:    "+"\n\t查全率:"+Evaluation.firstValue[0][1]+"\n\t查准率:"+Evaluation.firstValue[1][1]);
				first3.append("\n    时尚类:    "+"\n\t查全率"+Evaluation.firstValue[0][2]+"\n\t查准率:"+Evaluation.firstValue[1][2]);
				first4.append("\n   计算机类:    "+"\n\t查全率:"+Evaluation.firstValue[0][3]+"\n\t查准率:"+Evaluation.firstValue[1][3]);
				second1.append("\n        查全率:"+Evaluation.secondValue[0][0]+"\n        查准率:"+Evaluation.secondValue[1][0]);
				second2.append("\n\n        查全率:"+Evaluation.secondValue[0][1]+"\n        查准率:"+Evaluation.secondValue[1][1]);
				second3.append("\n\n        查全率:"+Evaluation.secondValue[0][2]+"\n        查准率:"+Evaluation.secondValue[1][2]);
				second4.append("\n\n        查全率:"+Evaluation.secondValue[0][3]+"\n        查准率:"+Evaluation.secondValue[1][3]);
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
			
			//将precisionAndRecall行列互换存入到data中
			double[][] data=new double[precisionAndRecall[0].length][precisionAndRecall.length];
			
			for(int i=0;i<precisionAndRecall.length;i++)
			{
				for(int j=0;j<precisionAndRecall[0].length;j++)
				{
					data[j][i]=precisionAndRecall[i][j];
				}
			}
			
			//将结果用图表表示出来
			String[] rowKeys = {"查准率","查全率"};
			String[] columnKeys = {"教育","娱乐","时尚","计算"};
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);
			JFreeChart chart = ChartFactory.createBarChart3D("查准率与查全率",null,null,dataset,
			PlotOrientation.VERTICAL,true,false,false);
			chart.setBackgroundPaint(Color.WHITE);
			CategoryPlot plot = chart.getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
			
			plot.setDomainAxis(domainAxis);
			ValueAxis rangeAxis = plot.getRangeAxis();
			//设置最高的一个 Item 与图片顶端的距离
			rangeAxis.setUpperMargin(0.15);
			//设置最低的一个 Item 与图片底端的距离
			rangeAxis.setLowerMargin(0.15);
			plot.setRangeAxis(rangeAxis);
			BarRenderer3D renderer = new BarRenderer3D();
			renderer.setBaseOutlinePaint(Color.BLACK);
			//设置 Wall 的颜色<BR>
			renderer.setWallPaint(Color.gray);
			//设置每种水果代表的柱的颜色
			//renderer.setSeriesPaint(0, new Color(0, 0, 255));
			renderer.setSeriesPaint(0, new Color(0, 100, 255));
			renderer.setSeriesPaint(1, Color.GREEN);
			//设置每个地区所包含的平行柱的之间距离
			renderer.setItemMargin(0.1);
			//显示每个柱的数值，并修改该数值的字体属性<BR>
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setItemLabelsVisible(true);
			plot.setRenderer(renderer);
			//设置柱的透明度<BR>
			plot.setForegroundAlpha(0.6f);
			//设置类别、查准率与查全率的显示位置<BR>
			plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
			plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			ChartFrame cframe=new ChartFrame("分类的查准率与查全率",chart);
	        cframe.pack();
	        cframe.setLocation(400, 250);
	        cframe.setVisible(true);
		}
		
	}
	/**
	 * 处理"选择增量样本"事件
	 */
	class openIcrementalTrainSetHandel implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e) {
			JFileChooser jc=new JFileChooser();//为用户选择文件提供了一种机制，File choosers provide a GUI for navigating the file system
			File file=new File("F:\\DeTreeSVM\\训练集");
			jc.setSelectedFile(file);
			jc.setFileFilter(new MyFileFilter());//增加文件过滤器
			jc.setDialogTitle("选择增量样本");   
			int rVal=jc.showOpenDialog(SVMTrain.this);
			if(rVal==JFileChooser.APPROVE_OPTION){
			     fileTestingSet=jc.getSelectedFile();//获得新增训练集文件
				
		}
		}
		
	
	}
	
	/**
	 * 处理"增量训练"事件
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
		 //建立一个新的文件来存储训练模型
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
		   JOptionPane.showConfirmDialog(null, "增量训练结束","提示",JOptionPane.CLOSED_OPTION);
		}
	}
	
	
	
	
	
	
	
	/**
	 * 文件过滤器的设计，只读.txt文件
	 */
	class MyFileFilter extends FileFilter
	{
		//返回描述
		public String getDescription()
		{
			return "文本文件";
		}
		//根据file来判断是否显示
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
		new SVMTrain("svm网页分类器");
	}
	
}
