package com.clk.clkpieanddonut.Interfaces;

import com.clk.clkpieanddonut.models.PieObject;

import java.util.List;

public interface ClkChartsInterface {
    void getPercentage(List<Double> percantage_values);
    void pieItemClick(PieObject pieObject, int position, double percentage);
}
