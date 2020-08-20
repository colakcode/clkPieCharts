package com.clk.clkpiecharts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clk.donutchart.DonutChart;
import com.clk.donutchart.Interfaces.ClickDonutSlice;
import com.clk.donutchart.models.DonutObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ClickDonutSlice {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DonutObject> donutObjects = new ArrayList<>();
//        donutObjects.add(new DonutObject("111111","Yakıt",1000,0));
//        donutObjects.add(new DonutObject("22222","Mutfak",1000,0));
//        donutObjects.add(new DonutObject("33333","Kira",2000,0));
//        donutObjects.add(new DonutObject("44444","dddKira",2000,0));
//        donutObjects.add(new DonutObject("55555","ddd",2000,0));

        for(int i=0; i<4;i++){
            double value = 101;
            if(i==2) value = 0;
            donutObjects.add(new DonutObject("id_"+i,"name_"+i,value,0));
        }

        RelativeLayout layoutGraph = findViewById(R.id.layoutGraph);

        DonutChart donutChart = new DonutChart(this,layoutGraph, "2020 Süresi Pandemi İstatistikleri");
        //donutChart.setBodyColor(Color.BLUE);
        donutChart.setParams(donutObjects);
        layoutGraph.addView(donutChart);

    }

    @Override
    public void getSliceInfo(DonutObject donutObject, int position) {
        Toast.makeText(this, "position :"+position+ " id :"+donutObject.getId(), Toast.LENGTH_SHORT).show();
    }
}