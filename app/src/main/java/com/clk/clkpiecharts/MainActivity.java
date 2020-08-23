package com.clk.clkpiecharts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clk.donutchart.DonutChart;
import com.clk.donutchart.Interfaces.ClickDonutSlice;
import com.clk.donutchart.Interfaces.ClickPieSlice;
import com.clk.donutchart.PieChart;
import com.clk.donutchart.models.PieObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ClickPieSlice, ClickDonutSlice {

    private static final String TAG = "clk_MainActivity";
    private RelativeLayout layoutGraph;
    private List<PieObject> pieObjects;
    private DonutChart donutChart;
    private PieChart pieChart;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutGraph = findViewById(R.id.layoutGraph);
        tvInfo = findViewById(R.id.tvInfo);
        init();

        //callPieChart();
       callDonutChart();

    }

    public void init(){
        pieObjects = new ArrayList<>();
        pieObjects.add(new PieObject("023456", "School", 2000));
        pieObjects.add(new PieObject("123456", "Shoping", 750));
        pieObjects.add(new PieObject("234567", "House Rent", 2000));
        pieObjects.add(new PieObject("345678", "Transporting", 999));
        pieObjects.add(new PieObject("456789", "Entertainment", 4500));
        pieObjects.add(new PieObject("5456789", "Product", 1000));
        pieObjects.add(new PieObject("6456789", "Market", 800));
        pieObjects.add(new PieObject("7456789", "Car", 1600));
        pieObjects.add(new PieObject("87456789", "Fuel", 1000));
        pieObjects.add(new PieObject("97456789", "Dress", 400));
        pieObjects.add(new PieObject("#107456789", "Credit Card", 3500));
        pieObjects.add(new PieObject("#117456789", "Tickets", 1600));
        pieObjects.add(new PieObject("#127456789", "Holiday", 3000));
        pieObjects.add(new PieObject("#137456789", "Shoes", 650));
        pieObjects.add(new PieObject("#147456789", "Food", 600));

        Log.d(TAG, "init: pie_size : "+pieObjects.size());
    }

    public void callPieChart(){
        layoutGraph.removeAllViews();
        pieChart = new PieChart(this, pieObjects, layoutGraph);
        pieChart.setBackgroundColor(getResources().getColor(R.color.grey_light));
        layoutGraph.addView(pieChart);
    }

    public void callDonutChart(){
        layoutGraph.removeAllViews();
        donutChart = new DonutChart(this, pieObjects, layoutGraph);
        donutChart.setMiddleText("2020 Expenses");
        //donutChart.setBackgroundColor(getResources().getColor(R.color.grey_light));
        layoutGraph.addView(donutChart);
    }

    public void setMiddleText(View v) {
        if(donutChart!=null){
            Random r = new Random();
            donutChart.setMiddleText("Expenses");
        }
    }

    @Override
    public void getPieSliceInfo(PieObject pieObject, int position, double percentage) {
        Log.d(TAG, "getSliceInfo: " + "\n" +
                "position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "percentage : " + percentage);

        tvInfo.setText("position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "percentage : " + percentage);
    }

    @Override
    public void getDonutSliceInfo(PieObject pieObject, int position, double percentage) {
        Log.d(TAG, "getSliceInfo: " + "\n" +
                "position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "percentage : " + percentage);

        tvInfo.setText("position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "percentage : " + percentage);
    }
}