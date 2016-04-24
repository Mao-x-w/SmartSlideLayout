package com.laomao.smartslidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;

public class SmartSlidingMenu extends FrameLayout {

	private ViewDragHelper viewDragHelper;

	private View mainView;
	private View menuView;
	private int mainViewWidth;
	private int mainViewHeight;
	private int menuViewWidth;
	private int menuViewHeight;

	private int dragRange;

	private FloatEvaluator floatEvaluator;

	// 定义几种表示开关的状态常量
	public static final int State_Open = 0;
	public static final int State_Close = 1;

	private int currentState = State_Close;

	public int getCurrentState() {
		return currentState;
	}

	private AnimState currentAnimaState = AnimState.SateScale;
	public void setAnimState(AnimState animState){
		currentAnimaState=animState;
//		if(currentAnimaState==AnimState.State3D){
//			ViewCompat.setScaleX(menuView,1f);
//			ViewCompat.setScaleY(menuView,1f);
//		}
	}

	public enum AnimState {
		SateScale(0),SmoothState(1), State3D(2);

		int value;

		AnimState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	public SmartSlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SmartSlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmartSlidingMenu(Context context) {
		this(context, null);
	}

	/*
	 * 用来初始化view
	 */
	private void initView() {
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
	}

	/**
	 * 在完成对xml文件的解析之后调用该方法,可以用来获取到子view对象
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mainView = getChildAt(1);
		menuView = getChildAt(0);
	}

	/**
	 * 当onMeasure方法执行完之后，调用该方法，所以可以用来测量子view的测量宽高
	 * 
	 * @param w
	 * @param h
	 * @param oldw
	 * @param oldh
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mainViewWidth = mainView.getMeasuredWidth();
		mainViewHeight = mainView.getMeasuredHeight();

		menuViewWidth = menuView.getMeasuredWidth();
		menuViewHeight = menuView.getMeasuredHeight();

		dragRange = (int) (mainViewWidth * 0.6);

	}

	/**
	 * 用来布局，此处默认是使用FrameLayout的layout，即两个布局叠加到一起
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	/**
	 * 让ViewDragHelper自己去决定是否拦截
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}

	/**
	 * 将触摸事件交给ViewDragHelper去处理，返会true表示，自己处理，即VDH处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	/**
	 * 回调接口，用于处理子控件一些动作
	 */
	ViewDragHelper.Callback callback = new Callback() {

		/**
		 * 用来判断子view是否可以被捕获
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == mainView || child == menuView;
		}

		/**
		 * 返会大于0的就表示可以水平滑动 它也会作为执行动画的事件
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return dragRange;
		}

		/**
		 * 确定子view可以滑动的位置
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// 如果是mainView的话，确定其水平可以滑动的距离
			if (child == mainView) {
				if (left < 0) {
					left = 0;
				} else if (left > dragRange) {
					left = dragRange;
				}
			}

			// if (child==menuView) {
			// left=0;
			// }
			return left;
		}

		/**
		 * 这个方法用于处理一些联动效果
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// 当改变menuView的位置的时候，让mainView实现联动效果
			if (changedView == menuView) {
				// 让menuView不可以被滑动
				menuView.layout(0, 0, menuViewWidth, menuView.getBottom());

				int newLeft = mainView.getLeft() + dx;
				if (newLeft > dragRange)
					newLeft = dragRange;
				if (newLeft < 0)
					newLeft = 0;

				mainView.layout(newLeft, mainView.getTop(), newLeft
						+ mainViewWidth, mainView.getBottom());
			}

			float fraction = mainView.getLeft() * 1f / dragRange;
			// 实现伴随动画
			executeAnim(fraction);

			// 在位置改变的过程中将接口暴露出去，让使用者实现想要实现的功能
			if (fraction == 1 && currentState != State_Open) {
				currentState = State_Open;
				if (slidingListener != null) {
					slidingListener.start();
				}

			} else if (fraction == 0 && currentState != State_Close) {
				currentState = State_Close;
				if (slidingListener != null) {
					slidingListener.end();
				}
			}

			if (slidingListener != null) {
				slidingListener.sliding(fraction);
			}

		}

		/**
		 * 当释放时可以让界面平滑的滑动到某处
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (mainView.getLeft() > dragRange / 2) {
				// 回到打开的位置,
				open();
			} else if (mainView.getLeft() <= dragRange / 2) {
				// 回到关闭的位置
				close();
			}

			if (xvel > 200) {
				open();
			}
			

		}

	};

	// 用于执行动画
	protected void executeAnim(float fraction) {
		// 1--0.6 (float) (1 + (0.8 - 1) * fraction)
		switch (currentAnimaState.getValue()) {
		case 0:
			ViewCompat.setScaleX(mainView,
					floatEvaluator.evaluate(fraction, 1.0f, 0.8f));
			ViewCompat.setScaleY(mainView,
					floatEvaluator.evaluate(fraction, 1.0f, 0.8f));

			ViewCompat.setScaleX(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));
			ViewCompat.setScaleY(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));

			ViewCompat.setTranslationX(menuView,
					floatEvaluator.evaluate(fraction, -menuViewWidth / 2, 0));
			ViewCompat.setAlpha(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));
			
			break;
			
		case 1:
			
			ViewCompat.setScaleX(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));
			ViewCompat.setScaleY(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));
			
			ViewCompat.setTranslationX(menuView,
					floatEvaluator.evaluate(fraction, -menuViewWidth / 2, 0));
			ViewCompat.setAlpha(menuView,
					floatEvaluator.evaluate(fraction, 0.3f, 1.0f));
			
			break;
		case 2:
			ViewCompat.setTranslationX(menuView,
					floatEvaluator.evaluate(fraction, -menuViewWidth / 2, 0));
			ViewCompat.setRotationY(menuView,
					floatEvaluator.evaluate(fraction, -90, 0));
			ViewCompat.setRotationY(mainView,
					floatEvaluator.evaluate(fraction, 0, 90));
			break;
		case 3:

			break;
		}
		

		// menuView.setTranslationX(floatEvaluator.evaluate(fraction,
		// -menuViewWidth/2, 0));

		if (getBackground() != null) {
			// ArgbEvaluator argbEvaluator=new ArgbEvaluator();
			// int color = (Integer) argbEvaluator.evaluate(fraction,
			// Color.BLACK, Color.TRANSPARENT);
			int color = (Integer) ColorUtil.evaluateColor(fraction,
					Color.BLACK, Color.TRANSPARENT);
			getBackground().setColorFilter(color, Mode.SRC_OVER);
		}
	}

	public void resetAnim() {
		ViewCompat.setScaleX(menuView,1f);
		ViewCompat.setScaleY(menuView,1f);
		ViewCompat.setRotationX(menuView,0);
		ViewCompat.setRotationY(menuView,0);
		ViewCompat.setTranslationX(menuView,-menuViewWidth / 2);
		ViewCompat.setAlpha(menuView,1f);
	}

	/**
	 * 让mainView处于打开位置
	 */
	public void open() {
		viewDragHelper
				.smoothSlideViewTo(mainView, dragRange, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * 让mainView处于关闭位置
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(mainView, 0, 0);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * 刷新的时候会调用该方法
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private OnSlidingStateChangedListener slidingListener;

	public void setOnSlidingStateChangedListener(
			OnSlidingStateChangedListener slidingListener) {
		this.slidingListener = slidingListener;
	}

	public interface OnSlidingStateChangedListener {
		public void start();

		public void end();

		public void sliding(float fraction);
	}

}
