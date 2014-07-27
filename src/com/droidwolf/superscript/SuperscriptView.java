package com.droidwolf.superscript;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * License
 *This software is distributed under the MIT License: http://opensource.org/licenses/MIT
 * android中角标的实现 http://obacow.iteye.com/blog/1954631
 * @author droidwolf
 */
public class SuperscriptView extends TextView{
	public final static int Gravity_LEFT_TOP=1,Gravity_RIGHT_TOP=2;
	private float mOffsetX, mOffsetY;
	private float mOffsetDegress;
	private int mHeight, mWidth;
	private int mGravity;
	public SuperscriptView(Context context) {
		super(context);
	}

	public SuperscriptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SuperscriptView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	@Override
	public void setVisibility(int visibility) {
		clearAnimation();
		super.setVisibility(visibility);
		if(visibility==View.VISIBLE){
			startAnimation(mAnimation);
		}
	}

	private void init(Context context, AttributeSet attrs) {
		int leftEdge = 0,rightEdge=0, topEdge = 0,smallLeftEdge = 0,smallRightEdge=0, smallTopEdge = 0;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SuperscriptView);
		mGravity= ta.getInt( R.styleable.SuperscriptView_gravity, Gravity_LEFT_TOP);

		topEdge = ta.getDimensionPixelSize( R.styleable.SuperscriptView_topEdge, 0);
		smallTopEdge = ta.getDimensionPixelSize( R.styleable.SuperscriptView_smallTopEdge, 0);

		if( mGravity==Gravity_LEFT_TOP){
			leftEdge = ta .getDimensionPixelSize(R.styleable.SuperscriptView_leftEdge, 0);
			smallLeftEdge = ta .getDimensionPixelSize(R.styleable.SuperscriptView_smallLeftEdge, 0);

			if (smallLeftEdge > 0 && smallTopEdge > 0) {
				calcLeftTop(leftEdge, smallLeftEdge, topEdge, smallTopEdge);
			} else {
				calcLeftTopCover(leftEdge, topEdge);
			}
		}else if(mGravity==Gravity_RIGHT_TOP){
			rightEdge= ta .getDimensionPixelSize(R.styleable.SuperscriptView_rightEdge, 0);
			smallRightEdge = ta.getDimensionPixelSize( R.styleable.SuperscriptView_smallRightEdge, 0);
			
			if (smallRightEdge > 0 && smallTopEdge > 0) {
				calcRightTop(rightEdge, smallRightEdge, topEdge, smallTopEdge);
			} else {
				calcRightTopConver(rightEdge, topEdge);
			}
		}
		ta.recycle();
		
		mAnimation.setFillBefore(true);
		mAnimation.setFillAfter(true);
		mAnimation.setFillEnabled(true);
		startAnimation(mAnimation);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mHeight < 1 || mWidth < 1) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			setMeasuredDimension(mWidth, mHeight);
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (super.getVisibility() == View.VISIBLE) {
			startAnimation(mAnimation);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		super.clearAnimation();
	}
    
//	@Override
//	public void draw(Canvas canvas) {
////		canvas.save();
//		canvas.translate(mOffsetX,mOffsetY);
//		canvas.rotate(mOffsetDegress);
//		
////		canvas.translate(mOffsetX,0);
////		canvas.rotate(mOffsetDegress, mOffsetX,0);
//		
////		canvas.restore();
//		super.draw(canvas);
//	}
	
	/** 右上角-切完全覆盖的直角三角形
	 * @param rightEdge 右直角边
	 * @param topEdge 顶部直角边
	 */
	private void calcRightTopConver(int rightEdge, int topEdge) {
		final double be=topEdge,ec=rightEdge;
	    final double bc=Math.sqrt( Math.pow(be,2d)+Math.pow(ec,2d));
	    //sin(∠ebh)
		final double sinB =ec/bc;
		mOffsetDegress = (float) Math.toDegrees(Math.asin(sinB));

		// eh=sin(∠ebh)*be
		mHeight = Math.round((float) (sinB * be));

		// af=cos(∠baf)*ba=cos(∠ebh)*eh
		mOffsetY =- (float) (be/bc * mHeight);
		
		// bf=sin(∠baf)*ba=sin(∠ebh)*eh
		mOffsetX = (float) (sinB * mHeight+(bc-be));

		mWidth = Math.round((float) bc);
	}
	
	/**右上角-不完全覆盖
	 * @param rightEdge
	 * @param smallRightEdge
	 * @param topEdge
	 * @param smallTopEdge
	 */
	private void calcRightTop(int rightEdge,int smallRightEdge, int topEdge,int smallTopEdge) {
		final double ai=topEdge, bi=rightEdge;
		final double af=ai-smallTopEdge;
		final double ab = Math.sqrt( Math.pow(ai, 2d)+Math.pow(bi, 2d));
		final double sinB=bi/ab;
		
		//ad=sin(∠dfe)*af=sin(∠abh)*af
		final double ad=sinB*af;
		
		//ae=sin(∠ade)*ab=sin(∠abh)*ab
		final double ae= sinB*ad;
		
		//de=cos(∠ade)*ab=cos(∠abh)*ab
		mOffsetY=(float) -(ai/ab*ad);
		
		//X=ab-ai+ae
		mOffsetX=(float) (ab-ai+ae);
		
		mOffsetDegress=(float) Math.toDegrees( Math.asin(sinB));
		mWidth=Math.round((float)ab);
		mHeight=Math.round( (float)ad);
	}
	
	/** 左上角-切完全覆盖的直角三角形
	 * @param leftEdge 左直角边
	 * @param topEdge	顶部直角边
	 */
	private void calcLeftTopCover(int leftEdge, int topEdge) {
	    final double ab=Math.sqrt( Math.pow(topEdge,2d)+Math.pow(leftEdge,2d));
		final double sinB =leftEdge/ab;
		mOffsetDegress = -(float) Math.toDegrees(Math.asin(sinB));

		// ef=da=sin(∠ebf)*eb
		mHeight = Math.round((float) (sinB * topEdge));

		// de=sin(∠ead)*ea=sin(∠ebf)*ea
		final double de = sinB * leftEdge;

		// dg=cos(∠ead)*de=cos(∠ebf)*de
		mOffsetX = -(float) ((topEdge/ab) * de);

		// eg==sin(∠edg)*de=sin(∠ebf)*de
		mOffsetY = (float) (sinB * de);
		mWidth = Math.round((float) ab);
	}

	/** 左上角-切直角三角形，TextView和背景直角切出一个小直角三角形
	 * @param leftEdge  左-大直角边
	 * @param smallLeftEdge 左-小直角边
	 * @param topEdge 顶部-大直角边
	 * @param smallTopEdge  顶部-小直角边
	 */
	private void calcLeftTop(int leftEdge, int smallLeftEdge, int topEdge, int smallTopEdge) {
		final double ab = Math.sqrt(Math.pow(topEdge, 2d)
				+ Math.pow(leftEdge, 2d));
		// sin(∠ceb)=sin(∠gef)
		final double sinE = leftEdge / ab;
		final double eb = topEdge - smallTopEdge;
		final double fa = leftEdge - smallLeftEdge;
		// ∠ceb
		mOffsetDegress = -(float) Math.toDegrees(Math.asin(sinE));

		// cb=sin(∠ceb)*eb
		final double cb = sinE * eb;
		mHeight = Math.round((float) cb);

		// sin(∠daf)*fa=sin(∠ceb)*fa
		final double df = sinE * fa;

		// sin(∠fdh)*df=sin(∠ceb)*df
		final double fh = sinE * df;

		// dh=cos(∠fdh)*df=cos(∠ceb)*df=cos(∠gba)*df
		mOffsetX = -(float) (topEdge / ab * df);

		// gh=gf+fh
		mOffsetY = (float) (smallLeftEdge + fh);
		mWidth = Math.round((float) ab);
	}
	
	private Animation mAnimation = new Animation() {
		protected void applyTransformation(float interpolatedTime,Transformation t) {
			if (mHeight < 1 || mWidth < 1) {
				return;
			}
			Matrix tran = t.getMatrix();
			tran.setTranslate(mOffsetX,mOffsetY);
			tran.postRotate(mOffsetDegress, mOffsetX,mOffsetY);
		}
	};

	
}
