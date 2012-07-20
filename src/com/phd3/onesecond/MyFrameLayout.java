package com.phd3.onesecond;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {
	public int height;
	public int width;
	
	public MyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public MyFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyFrameLayout(Context context) {
		super(context);
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		width = MeasureSpec.getSize(MeasureSpec.AT_MOST);
		width = MeasureSpec.getSize(MeasureSpec.EXACTLY);


		height = MeasureSpec.getSize(widthMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
