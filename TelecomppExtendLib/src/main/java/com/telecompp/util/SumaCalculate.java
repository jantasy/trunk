package com.telecompp.util;

import java.math.BigDecimal;

/**
 * 计算器模块
 *  
 * @author xiongdazhuang
 * 
 */
public class SumaCalculate implements SumaConstants
{
	private String CurrentNumber;

	private String Operators;

	/**
	 * 构造函数
	 */
	public SumaCalculate()
	{
		this.CurrentNumber = "0";
		this.Operators = "";
	}

	/**
	 * 添加数据
	 * 
	 * @param a
	 *            数据a
	 * @param b
	 *            数据b
	 * @return 返回结果
	 */
	public String add(double a, double b)
	{
		return "" + (a + b);
	}

	/**
	 * 进行计算
	 * 
	 * @param a
	 *            数a
	 * @param b
	 *            数b
	 * @param operator
	 *            运算符
	 * @return 输出结果
	 */
	public String calc(double a, double b, String operator)
	{
		if(operator == null)
		{
			return "0";//计算失败
		}
		String resultString = null;
		try
		{
			BigDecimal c = null;
			if (operator.equals("+"))
			{
				c = new BigDecimal(a + b);
			}
			if (operator.equals("-"))
			{
				c = new BigDecimal(a - b);
			}
			if (operator.equals("*"))
			{
				c = new BigDecimal(a * b);
			}
			if (operator.equals("/"))
			{
				c = new BigDecimal(a / b);
			}

			// 四舍五入
			if(c == null)
			{
				return "0";
			}
			
			double d = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			// //保留两位小数字
			// java.text.DecimalFormat df =new java.text.DecimalFormat("#.00");
			// df.format(d);
			//
			// String e = df.format(d);

			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);

			resultString = "" + nf.format(d);

			TerminalDataManager.printLog(SumaCalculate.DEBUG_LOGCAT_FILTER, ("" + a) + " " + operator + " " + ("" + b) + " = "
					+ resultString);
		}
		catch (Exception e)
		{
			return "0";
		}
		
		if(resultString.equals(""))
		{
			return "0";
		}
		
		return resultString;
	}

	/**
	 * 四舍五入
	 * 
	 * @param a
	 *            数据a
	 * @return 返回四舍五入结果
	 */
	public String RoundingAndFormat(double a)
	{
		BigDecimal c = new BigDecimal(a);
		double d = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);

		String resultString = "" + nf.format(d);

		return resultString;
	}

	/**
	 * 添加一个数
	 * 
	 * @param number
	 *            数字
	 * @return 添加的结果
	 */
	public String insert(String number)
	{
		if (Operators.equals(""))
		{
			CurrentNumber = CurrentNumber + number;
			return CurrentNumber;
		}
		else
		{
			return null;
		}
	}

	/**
	 * 清空数据
	 */
	public void Clear()
	{
		this.CurrentNumber = "0";
		this.Operators = "";
	}
}
