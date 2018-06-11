package cn.a527yxy.sideslipdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by rtyui on 2018/5/16.
 */

public class SideSlipLinearLayout extends LinearLayout {

    private int SIDE_WIDTH = 0;

    private boolean OUT = false;


    private float aX;
    private float bX;

    private float move;

    private final int SLIP_RATIO = 3;//滑动系数

    private OnSideStateChangeListener onSideStateChangeListener;

    private Context context = null;
    private float mPrevX;

    public SideSlipLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void addSide(View side, int width){
        addView(side, 0);
        SIDE_WIDTH = dp2px(width);
        setSide(-SIDE_WIDTH);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = wm.getDefaultDisplay().getWidth() + SIDE_WIDTH;
        this.setLayoutParams(layoutParams);
    }

    public void addMain(View main){
        addView(main, 1);
        setSide(-SIDE_WIDTH);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams layoutParams = main.getLayoutParams();
        layoutParams.width = wm.getDefaultDisplay().getWidth();
        main.setLayoutParams(layoutParams);
    }

    public void setOnSideStateChangeListener(OnSideStateChangeListener onSideStateChangeListener){
        this.onSideStateChangeListener = onSideStateChangeListener;
    }


    private void setSide(int dp){
        setPadding(dp, 0, 0,0);
    }

    /**
     * dp转为px
     * @param dp dp值
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void anAnim(int start, int end){
        if (end == 0){
            setStateOut();
        }else{
            setStateIn();
        }
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setSide((Integer) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (aX == 0)
                    aX = event.getX();
                else{
                    bX = event.getX();
                    move += (bX - aX);
                    aX = bX;
                    if (move > 0 && move - SIDE_WIDTH <= 0 && !OUT)
                        setSide((int) (move - SIDE_WIDTH));
                    if (move < 0 && SIDE_WIDTH + move >= 0 && OUT){
                        setSide((int) (move));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                aX = 0;
                bX = 0;
                if (move > 0 && move - SIDE_WIDTH <= 0 && !OUT){
                    if (move > SIDE_WIDTH / SLIP_RATIO){
                        anAnim((int) (move - SIDE_WIDTH), 0);
                    }else{
                        anAnim((int) (move - SIDE_WIDTH), -SIDE_WIDTH);
                    }
                }
                if (move < 0 && SIDE_WIDTH + move >= 0 && OUT){
                    if (-move < SIDE_WIDTH / SLIP_RATIO){
                        anAnim((int) (move), 0);
                    }else{
                        anAnim((int) (move), -SIDE_WIDTH);
                    }
                }
                move = 0;
                break;
        }
        return true;
    }*/

    public void slipIn(){
        if (!OUT)
            return;
        anAnim(0, -SIDE_WIDTH);
    }

    public void slipOut(){
        if (OUT)
            return;
        anAnim(-SIDE_WIDTH, 0);
    }


    public interface OnSideStateChangeListener{
        void stateOut();
        void stateIn();
    }


    private void setStateOut(){
        OUT = true;
        onSideStateChangeListener.stateOut();
    }

    private void setStateIn(){
        OUT = false;
        onSideStateChangeListener.stateIn();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
