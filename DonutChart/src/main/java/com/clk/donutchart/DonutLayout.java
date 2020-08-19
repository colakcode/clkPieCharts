package com.clk.donutchart;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by CLK on 06.01.2019.
 */

public class DonutLayout extends FrameLayout {

    private LayoutInflater mInflater;
    private RelativeLayout layoutGraph;
    private Activity activity;

    public DonutLayout(Activity activity) {
        super(activity);
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        init();
    }

    public DonutLayout(Activity context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public DonutLayout(Activity context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    private View view;
    public void init() {
        view = mInflater.inflate(R.layout.donut_layout, this, true);

    }

    public void run(Activity activity, List<DonutObject> donutObjects, String middle_text){
        layoutGraph = (RelativeLayout) view.findViewById(R.id.layoutGraph);
        DonutChart donutChart = new DonutChart(activity, layoutGraph, middle_text);
        donutChart.drawGraphs(donutObjects);
        layoutGraph.addView(donutChart);
    }
}
