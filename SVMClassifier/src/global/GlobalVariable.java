package global;

import java.util.List;

import struct.PageInfo;

/**
 * 存取核函数参数和惩罚参数等相关的参数
 * @author Administrator 
 */ 
public class GlobalVariable {
	public static double delta;//核函数参数
	public static double punish;//惩罚参数
	public static int webPageNumber;//训练集中网页的数目
	public static double tolerance=0.001;//允许的误差
	public static int vectorNumber;//网页特征向量的维数(是所有网页特征向量维数中最大的那个)
	public static int sortNumber;//训练集中网页类别数
	public static List<PageInfo>listPageInfo;
	public static List<PageInfo>listPageInfo1;
}
