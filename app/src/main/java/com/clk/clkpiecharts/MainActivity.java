package com.clk.clkpiecharts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.clk.donutchart.Interfaces.ClickDonutSlice;
import com.clk.donutchart.models.DonutObject;

public class MainActivity extends AppCompatActivity implements ClickDonutSlice {

    private static final String TAG = "clk_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void getSliceInfo(DonutObject donutObject, int position) {
        Log.d(TAG, "getSliceInfo: " +
                "position : "+position +
                "id : "+donutObject.getId() +
                "name : "+donutObject.getName() +
                "value : "+donutObject.getValue() +
                "color : "+donutObject.getColor());
    }
}