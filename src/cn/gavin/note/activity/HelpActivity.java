package cn.gavin.note.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

import cn.gavin.note.R;

/**
 * Created by luoyuan on 2016/11/5.
 */
public class HelpActivity extends Activity implements GestureDetector.OnGestureListener {
    private GestureDetector mGestureDetector;
    private ViewFlipper viewFlipper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);
        mGestureDetector = new GestureDetector(this);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        setTitle(getString(R.string.help_title));
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() > e2.getX()) {//move to left
            if (viewFlipper.getDisplayedChild() == 2) {
                finish();
            }
            viewFlipper.showNext();
        } else if (e1.getX() < e2.getX()) {
            viewFlipper.showPrevious();
        } else {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}