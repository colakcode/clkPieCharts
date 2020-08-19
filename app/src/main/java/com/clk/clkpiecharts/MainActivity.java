package com.clk.clkpiecharts;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.clk.donutchart.DonutChart;
import com.clk.donutchart.DonutObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DonutObject> donutObjects = new ArrayList<>();
        donutObjects.add(new DonutObject("jflskfdg","Yakıt",1000,0));
        donutObjects.add(new DonutObject("jfldddg","Mutfak",1000,0));
        donutObjects.add(new DonutObject("dda","Kira",2000,0));
        donutObjects.add(new DonutObject("dda","dddKira",2000,0));
        donutObjects.add(new DonutObject("dda","ddd",2000,0));


        RelativeLayout layoutGraph = findViewById(R.id.layoutGraph);

        DonutChart donutChart = new DonutChart(this,layoutGraph, "colak");
        //donutChart.setBodyColor(Color.BLUE);
        donutChart.drawGraphs(donutObjects);
        layoutGraph.addView(donutChart);

    }
}