package com.laomao.demo;

import java.util.Random;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartslidingmenu.R;
import com.example.smartslidingmenu.R.id;
import com.example.smartslidingmenu.R.layout;
import com.laomao.smartslidingmenu.Constant;
import com.laomao.smartslidingmenu.SmartSlidingMenu;
import com.laomao.smartslidingmenu.SmartSlidingMenu.AnimState;
import com.laomao.smartslidingmenu.SmartSlidingMenu.OnSlidingStateChangedListener;

public class Demo extends Activity implements OnClickListener {

	private SmartSlidingMenu mSlidingMenu;
	private ListView mMenuListview;
	private ListView mMainListview;
	private LinearLayout mMyLayout;
	private ImageView mIvHead;
	private View mButton1;
	private View mButton2;
	private View mButton3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initListener();
		initData();

	}

	private void initView() {
		mSlidingMenu = (SmartSlidingMenu) findViewById(R.id.sliding_menu);
		mMenuListview = (ListView) findViewById(R.id.menu_listview);
		mMainListview = (ListView) findViewById(R.id.main_listview);
		mMyLayout = (LinearLayout) findViewById(R.id.my_layout);
		mIvHead = (ImageView) findViewById(R.id.iv_head);

		mButton1 = findViewById(R.id.button1);
		mButton2 = findViewById(R.id.button2);
		mButton3 = findViewById(R.id.button3);

		mButton1.setOnClickListener(this);
		mButton2.setOnClickListener(this);
		mButton3.setOnClickListener(this);
	}

	private void initListener() {
		mSlidingMenu
				.setOnSlidingStateChangedListener(new OnSlidingStateChangedListener() {

					@Override
					public void start() {
						mMenuListview.smoothScrollToPosition(new Random()
								.nextInt(Constant.sCheeseStrings.length));
					}

					@Override
					public void end() {
						// ObjectAnimator animator = ObjectAnimator.ofFloat(
						// mIvHead, "translationX", 25);
						// animator.setDuration(300);
						// animator.setInterpolator(new CycleInterpolator(5));
						// animator.start();

						// ViewCompat.animate(mIvHead).translationX(25)
						// .setDuration(300)
						// .setInterpolator(new CycleInterpolator(5))
						// .start();

						// 高版本的动画，还可以使用nineoldandroids来实现
						mIvHead.animate().translationX(25).setDuration(300)
								.setInterpolator(new CycleInterpolator(5))
								.start();

					}

					@Override
					public void sliding(float fraction) {

					}
				});

	}

	private void initData() {

		mMenuListview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position,
						convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		mMainListview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.NAMES));
	}

	@Override
	public void onClick(View v) {
		mSlidingMenu.resetAnim();
		switch (v.getId()) {
		case R.id.button1:
			mSlidingMenu.setAnimState(AnimState.SateScale);
			break;

		case R.id.button2:
			mSlidingMenu.setAnimState(AnimState.SmoothState);
			break;
		case R.id.button3:
			mSlidingMenu.setAnimState(AnimState.State3D);
			break;
		}
	}

}
