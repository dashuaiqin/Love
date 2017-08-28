package com.qin.wheelview;

import android.content.Context;
import android.view.View;

import com.qin.Utils.ScreenUtils;
import com.qin.love.R;

import java.util.Arrays;
import java.util.List;


public class WheelMain {

	private View view;
	private WheelTimeView wv_year;
	private WheelTimeView wv_month;
	private WheelTimeView wv_day;
	private int textSize = 50;// 字体大小
	private boolean hasSelectTime;
	private static int START_YEAR = 1990, END_YEAR = 2049;
	private Context context;
	private boolean isCalendar;//true,公历；false农历

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMain(Context context,View view,boolean isCalendar) {
		super();
		this.view = view;
		hasSelectTime = false;
		this.context=context;
		this.isCalendar=isCalendar;
		setView(view);
	}

	public WheelMain(View view, boolean hasSelectTime,boolean isCalendar) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		this.isCalendar=isCalendar;
		setView(view);
	}

	public void initDateTimePicker(int year, int month, int day) {
		this.initDateTimePicker(year, month, day, 0, 0);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker(int year, int month, int day, int h, int m) {
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DATE);
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
         // 农历数据

		final String chineseNumber[] = { "正", "二", "三", "四", "五", "六", "七",
				"八", "九", "十", "冬", "腊" };
		final String chineseNumber1[] = { "初一", "初二", "初三", "初四", "初五", "初六",
				"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
				"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
				"廿七", "廿八", "廿九", "卅十" };
		final String chineseNumber2[] = { "初一", "初二", "初三", "初四", "初五", "初六",
				"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
				"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
				"廿七", "廿八", "廿九" };


if (isCalendar) {
	setTextSize(18);
	// 年
	wv_year = (WheelTimeView) view.findViewById(R.id.wv_year);
	wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
	wv_year.setCyclic(true);// 可循环滚动
	wv_year.setLabel("年");// 添加文字
	wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
	    // 月
	wv_month = (WheelTimeView) view.findViewById(R.id.wv_month);
	wv_month.setAdapter(new NumericWheelAdapter(1, 12));
	wv_month.setCyclic(true);
	wv_month.setLabel("月");
	wv_month.setCurrentItem(month-1);

	// 日
	wv_day = (WheelTimeView) view.findViewById(R.id.wv_day);
	wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);


		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(View wheel, int oldValue, int newValue) {
				int oldDayNum=wv_day.getCurrentItem()+1;
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					if (oldDayNum==31) {
						wv_day.setCurrentItem(30-1, true);
					}

				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0){
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					if (oldDayNum==31||oldDayNum==30) {
						wv_day.setCurrentItem(29-1, true);
					}
					}
					else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						if (oldDayNum == 31 || oldDayNum == 30 || oldDayNum == 29) {
							wv_day.setCurrentItem(28 - 1, true);
						}
					}
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(View wheel, int oldValue, int newValue) {
				int oldDayNum=wv_day.getCurrentItem()+1;
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					if (oldDayNum==31) {
						wv_day.setCurrentItem(30-1, true);
					}
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0){
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					if (oldDayNum==31||oldDayNum==30) {
						wv_day.setCurrentItem(29-1, true);
					}
					}
					else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						if (oldDayNum == 31 || oldDayNum == 30 || oldDayNum == 29) {
							wv_day.setCurrentItem(28 - 1, true);
						}
					}
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
}
else {//农历
	setTextSize(15);
	// 年
	wv_year = (WheelTimeView) view.findViewById(R.id.wv_year);
	wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
	wv_year.setCyclic(true);// 可循环滚动
	wv_year.setLabel("年");// 添加文字
	wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
	// 月
	wv_month = (WheelTimeView) view.findViewById(R.id.wv_month);
	wv_month.setAdapter(new ArrayWheelAdapter<String>(context,
			runMonth(LunarCalendar.leapMonth(year))));
	wv_month.setCyclic(true);
	wv_month.setLabel(" 月");
	wv_month.setCurrentItem(month-1, true);
	// 日
	wv_day = (WheelTimeView) view.findViewById(R.id.wv_day);
	wv_day.setCyclic(true);
	// 判断大小月及是否闰年,用来确定"日"的数据
	if (getNum(wv_month.getAdapter().getItem(
			wv_month.getCurrentItem())) == 0) {
		System.out.println("r---");
		if (LunarCalendar.leapDays(year) == 29) {
			wv_day.setAdapter(new ArrayWheelAdapter<String>(context, chineseNumber2));
		} else {
			wv_day.setAdapter((WheelAdapter) new ArrayWheelAdapter<String>(context, chineseNumber1));
		}
	} else {
		if (LunarCalendar.daysInLunarMonth(year, getNum(wv_month
				.getAdapter().getItem(wv_month.getCurrentItem()))) == 29) {
			wv_day.setAdapter((WheelAdapter) new ArrayWheelAdapter<String>(context,
					chineseNumber2));
		} else {
			wv_day.setAdapter((WheelAdapter) new ArrayWheelAdapter<String>(context,
					chineseNumber1));
		}
	}
	// wv_day.setLabel("日");
	wv_day.setCurrentItem(day - 1,true);

	// 添加"年"监听
	OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
		public void onChanged(View wheel, int oldValue, int newValue) {
			int year_num = newValue + START_YEAR;
			int oldMonthPosition=wv_month.getCurrentItem();
			int oldDayPosition=wv_day.getCurrentItem();
			wv_month.setAdapter(new ArrayWheelAdapter<String>(context,
					runMonth(LunarCalendar.leapMonth(year_num))));
			if (oldMonthPosition==12){
				wv_month.setCurrentItem(11,true);
			}
			if (getNum(wv_month.getAdapter().getItem(
					wv_month.getCurrentItem())) == 0) {
				System.out.println("r---");
				if (LunarCalendar.leapDays(year_num) == 29) {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber2));
					if (oldDayPosition==29) {
						wv_day.setCurrentItem(29-1, true);
					}
				} else {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber1));
				}
			} else {
				if (LunarCalendar.daysInLunarMonth(year_num, getNum(wv_month
						.getAdapter().getItem(wv_month.getCurrentItem()))) == 29) {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber2));
					if (oldDayPosition==29) {
						wv_day.setCurrentItem(29-1, true);
					}
				} else {
					wv_day.setAdapter( new ArrayWheelAdapter<String>(context,
							chineseNumber1));
				}
			}
			// 判断大小月及是否闰年,用来确定"日"的数据
			// toArray(runMonth(DateUtil.leapMonth(year_num))).toString();
		}
	};
	// 添加"月"监听
	OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
		public void onChanged(View wheel, int oldValue, int newValue) {
			int month_num = newValue + 1;
			System.out.println("mmmmm--" + month_num);
			int oldDayPosition=wv_day.getCurrentItem();

			if (getNum(wv_month.getAdapter().getItem(
					wv_month.getCurrentItem())) == 0) {
				System.out.println("r---");
				if (LunarCalendar
						.leapDays(wv_year.getCurrentItem() + START_YEAR) == 29) {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber2));
					if (oldDayPosition==29) {
						wv_day.setCurrentItem(29-1, true);
					}
				} else {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber1));
				}
			} else {
				if (LunarCalendar.daysInLunarMonth(
						wv_year.getCurrentItem() + START_YEAR,
						getNum(wv_month.getAdapter().getItem(
								wv_month.getCurrentItem()))) == 29) {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber2));
					if (oldDayPosition==29) {
						wv_day.setCurrentItem(29-1, true);
					}
				} else {
					wv_day.setAdapter(new ArrayWheelAdapter<String>(context,
							chineseNumber1));
				}
			}
		}
	};
	wv_year.addChangingListener(wheelListener_year);
	wv_month.addChangingListener(wheelListener_month);
}
		if (hasSelectTime) {
			textSize += 1;
		}
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = ScreenUtils.sp2px(context,textSize);
	}

	/**
	 * 获取时间
	 * @return
	 */
	public String getTime() {
		StringBuffer sb = new StringBuffer();
		if (isCalendar) {//是公历
			if (!hasSelectTime)
				sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
						.append((wv_month.getCurrentItem() + 1)).append("-")
						.append((wv_day.getCurrentItem() + 1));
			else
				sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
						.append((wv_month.getCurrentItem() + 1)).append("-")
						.append((wv_day.getCurrentItem() + 1)).append(" ");
		} else{//农历
			sb.append((wv_year.getCurrentItem() + START_YEAR));
			if (getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem())) == 0){//如果是闰月
				sb.append("-")
						.append(wv_month.getCurrentItem()).append("-")
						.append(wv_day.getCurrentItem() + 1).append("-1");
			}else{//不是闰月
				sb.append("-")
						.append(getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem()))).append("-")
						.append((wv_day.getCurrentItem() + 1)).append("-0");
			}
		}
		return sb.toString();
	}/**
	 * 获取时间
	 * @return 数组
	 */
	public int[] getTimeArray() {
		int date[] = new int[4];
		if (isCalendar) {//是公历
			date[0]=(wv_year.getCurrentItem() + START_YEAR);
			date[1]=(wv_month.getCurrentItem() + 1);
			date[2]=(wv_day.getCurrentItem() + 1);
			date[3]=-1;//无用，仅用来占位，与农历保持一致
		} else{//农历
			date[0]=(wv_year.getCurrentItem() + START_YEAR);
			date[2]=(wv_day.getCurrentItem() + 1);
			if (getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem())) == 0){//如果是闰月
				date[1]=(wv_month.getCurrentItem());
				date[3]=1;
			}else{//不是闰月
				date[1]=getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem()));
				date[3]=0;
			}

		}
		return date;
	}

	/**
	 * 返回时间字符串，用于显示
	 * @return
	 */
    public String getTimeText(){
		if (isCalendar){//是公历
			return getTime();
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("年")
			        .append(wv_month.getAdapter().getItem(wv_month.getCurrentItem())).append("月")
					.append(wv_day.getAdapter().getItem(wv_day.getCurrentItem()));
			return sb.toString();
		}
	}
	/**
	 * 以下为阴历所需
	 * @param m
	 * @return
	 */
	private String[] runMonth(int m) {
		String[] list = new String[0];
		switch (m) {

			case 0:
				String chineseNumber0[] = { "正", "二", "三", "四", "五", "六", "七", "八",
						"九", "十", "冬", "腊" };
				list = chineseNumber0;
				break;
			case 1:
				String chineseNumber1[] = { "正", "闰一", "二", "三", "四", "五", "六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber1;
				break;
			case 2:
				String chineseNumber2[] = { "正", "二", "闰二", "三", "四", "五", "六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber2;
				break;
			case 3:
				String chineseNumber3[] = { "正", "二", "三", "闰三", "四", "五", "六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber3;
				break;
			case 4:
				String chineseNumber4[] = { "正", "二", "三", "四", "闰四", "五", "六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber4;
				break;
			case 5:
				String chineseNumber5[] = { "正", "二", "三", "四", "五", "闰五", "六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber5;

				break;
			case 6:
				String chineseNumber6[] = { "正", "二", "三", "四", "五", "六", "闰六", "七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber6;
				break;
			case 7:
				String chineseNumber7[] = { "正", "二", "三", "四", "五", "六", "七", "闰七",
						"八", "九", "十", "冬", "腊" };
				list = chineseNumber7;
				break;
			case 8:
				String chineseNumber8[] = { "正", "二", "三", "四", "五", "六", "七", "八",
						"闰八", "九", "十", "冬", "腊" };
				list = chineseNumber8;
				break;
			case 9:
				String chineseNumber9[] = { "正", "二", "三", "四", "五", "六", "七", "八",
						"九", "闰九", "十", "冬", "腊" };
				list = chineseNumber9;
				break;
			case 10:
				String chineseNumber10[] = { "正", "二", "三", "四", "五", "六", "七",
						"八", "九", "十", "闰十", "冬", "腊" };
				list = chineseNumber10;
				break;
			case 11:
				String chineseNumber11[] = { "正", "二", "三", "四", "五", "六", "七",
						"八", "九", "十", "冬", "闰冬", "腊" };
				list =chineseNumber11;
				break;
			case 12:
				String chineseNumber12[] = { "正", "二", "三", "四", "五", "六", "七",
						"八", "九", "十", "冬", "腊", "闰腊" };
				list = chineseNumber12;
				break;
		}
		return list;

	}

	private int getNum(String n) {
		int i = 0;
		if (n.equals("正")) {
			i = 1;
		} else if (n.equals("二")) {
			i = 2;
		} else if (n.equals("三")) {
			i = 3;
		} else if (n.equals("四")) {
			i = 4;
		} else if (n.equals("五")) {
			i = 5;
		} else if (n.equals("六")) {
			i = 6;
		} else if (n.equals("七")) {
			i = 7;
		} else if (n.equals("八")) {
			i = 8;
		} else if (n.equals("九")) {
			i = 9;
		} else if (n.equals("十")) {
			i = 10;
		} else if (n.equals("冬")) {
			i = 11;
		} else if (n.equals("腊")) {
			i = 12;
		} else if (n.equals("闰")) {
			i = 0;
		}
		return i;
	}
}
