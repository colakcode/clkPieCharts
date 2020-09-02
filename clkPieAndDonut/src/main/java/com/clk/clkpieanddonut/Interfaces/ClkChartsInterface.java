package com.clk.clkpieanddonut.Interfaces;

import com.clk.clkpieanddonut.models.PieObject;

import java.util.List;

public interface ClkChartsInterface {
    void getPercentage(List<Double> percantage_values);
    void onClickPieSlice(PieObject pieObject, int position, double percentage);
    void onClickTextBox(PieObject pieObject, int position, double percentage);
}
