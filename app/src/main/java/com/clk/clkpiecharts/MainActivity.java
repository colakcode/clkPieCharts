package com.clk.clkpiecharts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clk.clkpieanddonut.DonutChart;
import com.clk.clkpieanddonut.Interfaces.ClkChartsInterface;
import com.clk.clkpieanddonut.PieChart;
import com.clk.clkpieanddonut.models.PieObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ClkChartsInterface {

    private static final String TAG = "clk_MainActivity";
    private MainActivity activity = this;
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

        callPieChart();
        //callDonutChart();

    }

    public void init(){
        pieObjects = new ArrayList<>();
        pieObjects.add(new PieObject("023456", "School", 12000));
        pieObjects.add(new PieObject("123456", "Shoping", 6750));
        pieObjects.add(new PieObject("234567", "House Rent", 2000));
        pieObjects.add(new PieObject("345678", "Transporting", 4999));
        pieObjects.add(new PieObject("456789", "Entertainment", 4500));
        pieObjects.add(new PieObject("5456789", "Product", 6000));
        pieObjects.add(new PieObject("6456789", "Market", 800));
        pieObjects.add(new PieObject("7456789", "Car", 1600));
        pieObjects.add(new PieObject("87456789", "Fuel", 1000));
        pieObjects.add(new PieObject("97456789", "Dress", 400));
        pieObjects.add(new PieObject("#107456789", "Credit Card", 3500));
        pieObjects.add(new PieObject("#117456789", "Tickets", 1600));
        pieObjects.add(new PieObject("#127456789", "Holiday", 3000));
        pieObjects.add(new PieObject("#137456789", "Shoes", 650));
        pieObjects.add(new PieObject("#147456789", "Food", 600));
        pieObjects.add(new PieObject("023456", "School", 12000));
        pieObjects.add(new PieObject("123456", "Shoping", 6750));
        pieObjects.add(new PieObject("234567", "House Rent", 2000));
        pieObjects.add(new PieObject("345678", "Transporting", 4999));
        pieObjects.add(new PieObject("456789", "Entertainment", 4500));
//        pieObjects.add(new PieObject("5456789", "Product", 6000));
//        pieObjects.add(new PieObject("6456789", "Market", 800));
//        pieObjects.add(new PieObject("7456789", "Car", 1600));
//        pieObjects.add(new PieObject("87456789", "Fuel", 1000));
//        pieObjects.add(new PieObject("97456789", "Dress", 400));
//        pieObjects.add(new PieObject("#107456789", "Credit Card", 3500));
//        pieObjects.add(new PieObject("#117456789", "Tickets", 1600));
//        pieObjects.add(new PieObject("#127456789", "Holiday", 3000));
//        pieObjects.add(new PieObject("#137456789", "Shoes", 650));
//        pieObjects.add(new PieObject("#147456789", "Food", 600));

        Log.d(TAG, "init: pie_size : "+pieObjects.size());
    }

    public void callPieChart(){
        layoutGraph.removeAllViews();
        pieChart = new PieChart(this, pieObjects, layoutGraph);

        layoutGraph.addView(pieChart);
    }

    public void callDonutChart(){
        layoutGraph.removeAllViews();
        donutChart = new DonutChart(this, pieObjects, layoutGraph);
        donutChart.setMiddleText("2020 Expenses");
        layoutGraph.addView(donutChart);
    }

    public void setMiddleText(View v) {
        if(donutChart!=null){
            Random r = new Random();
            donutChart.setMiddleText("2020 Expenses");
        }
    }


    @Override
    public void getPercentage(List<Double> percantage_values) {
        for(Double value : percantage_values){
            Log.d(TAG, "getResultFromClkPieCharts: value : "+value);
        }
    }

    @Override
    public void pieItemClick(PieObject pieObject, int position, double percentage) {
        Log.d(TAG, "getSliceInfo: " + "\n" +
                "position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "color_code : " + pieObject.getColor() + "\n" +
                "percentage : " + percentage);

        tvInfo.setBackgroundColor(pieObject.getColor());
        tvInfo.setText("position : " + position + "\n" +
                "id : " + pieObject.getId() + "\n" +
                "name : " + pieObject.getName() + "\n" +
                "value : " + pieObject.getValue() + "\n" +
                "color : " + pieObject.getColor() + "\n" +
                "percentage : " + percentage);
    }
}