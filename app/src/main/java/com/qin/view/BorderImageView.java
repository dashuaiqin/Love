package com.qin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 自定义imageview,加入边框
 * @author Administrator
 *
 */
public class BorderImageView extends ImageView {
	private String namespace = "http://xmz.com";
	private int color;
	private Paint paint;

	public BorderImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor(attrs.getAttributeValue(namespace,
				"BorderColor"));
		paint= new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(1.0f);
		paint.setAntiAlias(true);//抗锯齿
		paint.setStyle(Paint.Style.STROKE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// 画边框 暂时去除小边框
//		Rect rec = canvas.getClipBounds();
////		Rect rec = new Rect(getLeft(),getTop(),getRight(),getBottom());
//		canvas.drawRect(rec, paint);
		Path path=new Path();
		path.moveTo(getLeft(), getTop());
		path.lineTo(getRight(), getTop());
		path.lineTo(getRight(), getBottom());
		path.lineTo(getLeft(), getBottom());
		path.lineTo(getLeft(), getTop());
		path.close();
		canvas.drawPath(path, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec+1, heightMeasureSpec+1);
	}
}
