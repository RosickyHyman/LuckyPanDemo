package com.hyman.luckypan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LuckyPan mLuckyPan;
    private ImageView mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
       mLuckyPan = (LuckyPan) findViewById(R.id.id_luckyPan);
       mStartBtn= (ImageView) findViewById(R.id.id_start_btn);
        initListener();
    }

    private void initListener() {
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLuckyPan.isStart()){
                    mLuckyPan.luckyStart(getPresent(getRandom()));
                    mStartBtn.setImageResource(R.mipmap.stop);
                }else{
                    if (!mLuckyPan.isShouldEnd()){
                        mLuckyPan.luckyEnd();
                        mStartBtn.setImageResource(R.mipmap.start);
                    }
                }
            }
        });
    }


    public static int getRandom(){

        Random random =new Random();

        return (int) (random.nextFloat()*100);
    }

    public static int getPresent(int a){
        int result=2;
        if (a<3) {
            result=0;
        }else if (a<8) {
            result=1;
        }else if (a<15) {
            result=3;
        }else if (a<25) {
            result=4;
        }else {
            result=5;
        }

        return result;
    }


}
