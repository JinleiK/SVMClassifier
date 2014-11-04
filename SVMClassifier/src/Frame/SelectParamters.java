package Frame;

import global.GlobalVariable;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import train.Train;

public class SelectParamters extends JFrame {
	//定义相关的控件
	private JLabel eductionJL=new JLabel("教育");
	private JLabel fashionJL=new JLabel("时尚");
	private JLabel entertainmentJL=new JLabel("娱乐");
	private JLabel computeJL=new JLabel("计算");
	
	
	private JLabel deltaJL1=new JLabel("δ=");
	private JLabel punishJL1=new JLabel("c=");
	private JLabel deltaJL2=new JLabel("δ=");
	private JLabel punishJL2=new JLabel("c=");
	private JLabel deltaJL3=new JLabel("δ=");
	private JLabel punishJL3=new JLabel("c=");
	private JLabel deltaJL4=new JLabel("δ=");
	private JLabel punishJL4=new JLabel("c=");
	
	
	private JPanel westJPanel=new JPanel();
	private JPanel centerJPanel=new JPanel();
	private JPanel northJPanel=new JPanel();
	
	private JButton OKJB=new JButton("确定");
	private JButton cancelJB=new JButton("取消");
	
	private JTextField punishEductionJF=new JTextField();
	private JTextField punishFashionJF=new JTextField();
	private JTextField punishEntertainmentJF=new JTextField();
	private JTextField punishComputeJF=new JTextField();
	private JTextField deltaEductionJF=new JTextField();
	private JTextField deltaFashionJF=new JTextField();
	private JTextField deltaEntertainmentJF=new JTextField();
	private JTextField deltaComputeJF =new JTextField();
	
	//构造选取参数界面
	public SelectParamters(String title)
	{
		GridLayout gridLayout1=new GridLayout(4,1);
		westJPanel.setLayout(gridLayout1);
		westJPanel.add(eductionJL);
		westJPanel.add(fashionJL);
		westJPanel.add(entertainmentJL);
		westJPanel.add(computeJL);
		
		//设置JTextField的大小
		punishEductionJF.setPreferredSize(new Dimension(110,25));
		deltaEductionJF.setPreferredSize(new Dimension(110,25));
		punishEntertainmentJF.setPreferredSize(new Dimension(110,25));
		deltaEntertainmentJF.setPreferredSize(new Dimension(110,25));
		punishFashionJF.setPreferredSize(new Dimension(110,25));
		deltaFashionJF.setPreferredSize(new Dimension(110,25));
		punishComputeJF.setPreferredSize(new Dimension(110,25));
		deltaComputeJF.setPreferredSize(new Dimension(110,25));
		//设置JTextField的初始值
		punishEductionJF.setText("10");
		deltaEductionJF.setText("10");
		punishEntertainmentJF.setText("10");
		deltaEntertainmentJF.setText("10");
		punishFashionJF.setText("10");
		deltaFashionJF.setText("10");
		punishComputeJF.setText("10");
		deltaComputeJF.setText("10");
		centerJPanel.add(punishJL1);
		centerJPanel.add(punishEductionJF);
		centerJPanel.add(deltaJL1);
		centerJPanel.add(deltaEductionJF);
		centerJPanel.add(punishJL2);
		centerJPanel.add(punishEntertainmentJF);
		centerJPanel.add(deltaJL2);
		centerJPanel.add(deltaEntertainmentJF);
		centerJPanel.add(punishJL3);
		centerJPanel.add(punishFashionJF);
		centerJPanel.add(deltaJL3);
		centerJPanel.add(deltaFashionJF);
		centerJPanel.add(punishJL4);
		centerJPanel.add(punishComputeJF);
		centerJPanel.add(deltaJL4);
		centerJPanel.add(deltaComputeJF);
	
		OKJB.addActionListener(new OKJBHandler());
		cancelJB.addActionListener(new cancelHandler());
		northJPanel.add(OKJB);
		northJPanel.add(cancelJB);
		
		
		Container contentPane=getContentPane();
		contentPane.add(westJPanel,BorderLayout.WEST);
		contentPane.add(centerJPanel,BorderLayout.CENTER);
		contentPane.add(northJPanel,BorderLayout.SOUTH);
		
		//设置界面的大小和位置
		setSize(300,200);
		setLocation(500,350);
		setResizable(false);
		setVisible(true);
	}
	
	
	
	/**
	 * 进行训练
	 */
	class OKJBHandler implements ActionListener
	{

		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			//建立一个新的文件来存储训练模型
			File file=new File("F:\\SVMClassifier\\SVMClassifier\\SVMClassifier\\src\\svmModel.txt");
			
			//检查是否存在训练模型文件，如果存在删除 
			if(file.exists())
			{
				file.delete();
			}
			long time =System.currentTimeMillis();
			Train train=new Train();//构造参数，初始化相关参数
			train.setListPageInfo(GlobalVariable.listPageInfo);
			GlobalVariable.delta=Double.parseDouble(deltaEductionJF.getText());//将所选择的参数存储于核函数中
			GlobalVariable.punish=Double.parseDouble(punishEductionJF.getText());
			train.learning(1);
			
			GlobalVariable.delta=Double.parseDouble(deltaEntertainmentJF.getText());
			GlobalVariable.punish=Double.parseDouble(punishEntertainmentJF.getText());
			train.learning(2);
			
			GlobalVariable.delta=Double.parseDouble(deltaFashionJF.getText());
			GlobalVariable.punish=Double.parseDouble(punishFashionJF.getText());
			train.learning(3);
			
			GlobalVariable.delta=Double.parseDouble(deltaComputeJF.getText());
			GlobalVariable.punish=Double.parseDouble(punishComputeJF.getText());
			train.learning(4);
			
			long time2=System.currentTimeMillis();
			long time3=time2-time;
			global.Evaluation.three.append(""+time3/1000);
			setVisible(false);
			JOptionPane.showConfirmDialog(null, "初始训练结束","提示",JOptionPane.CLOSED_OPTION);
		}
		
	}
	/**
	 * 取消训练
	 */
	class cancelHandler implements ActionListener
	{

		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			setVisible(false);//退出当前对话框
		}
		
	}
}

