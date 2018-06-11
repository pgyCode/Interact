package cn.a527yxy.sideslipdemo;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private View side = null;
    private View main = null;
    private SideSlipLinearLayout root = null;

    private boolean OUT = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);

        side = LayoutInflater.from(this).inflate(R.layout.side, root, false);
        main = LayoutInflater.from(this).inflate(R.layout.main, root, false);
        root.addSide(side, 300);
        root.addMain(main);
        root.setOnSideStateChangeListener(new SideSlipLinearLayout.OnSideStateChangeListener() {
            @Override
            public void stateOut() {

            }

            @Override
            public void stateIn() {

            }
        });

        main.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.slipOut();
            }
        });

//        main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                root.slipIn();
//            }
//        });
    }
}
