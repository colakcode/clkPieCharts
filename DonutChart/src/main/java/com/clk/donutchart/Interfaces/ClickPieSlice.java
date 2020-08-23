package com.clk.donutchart.Interfaces;

import com.clk.donutchart.models.PieObject;

public interface ClickPieSlice {
    void getPieSliceInfo(PieObject pieObject, int position, double percentage);
}
