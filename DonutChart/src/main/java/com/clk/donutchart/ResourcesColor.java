package com.clk.donutchart;

import android.app.Activity;
import android.content.Context;

/**
 * Created by CLK on 09.09.2018.
 */

public class ResourcesColor {

    private static final String TAG = "clk_ResourcesColor";
    private Context activity;

    int[] catColor = new int[15];
    public ResourcesColor(Activity activity) {
        this.activity = activity;
        catColor[0] = activity.getResources().getColor(R.color.pink);
        catColor[1] = activity.getResources().getColor(R.color.colorAccent);
        catColor[2] = activity.getResources().getColor(R.color.red);
        catColor[3] = activity.getResources().getColor(R.color.orange);
        catColor[4] = activity.getResources().getColor(R.color.red_maroon);
        catColor[5] = activity.getResources().getColor(R.color.green_avocado);
        catColor[6] = activity.getResources().getColor(R.color.green);
        catColor[7] = activity.getResources().getColor(R.color.green_olive);
        catColor[8] = activity.getResources().getColor(R.color.blue_sea);
        catColor[9] = activity.getResources().getColor(R.color.blue);
        catColor[10] = activity.getResources().getColor(R.color.blue_steel);
        catColor[11] = activity.getResources().getColor(R.color.blue_dark);
        catColor[12] = activity.getResources().getColor(R.color.blue_navy);
        catColor[13] = activity.getResources().getColor(R.color.red_velvet);
        catColor[14] = activity.getResources().getColor(R.color.red_claret);
    }

    public int getColor(int k) {

        int picked_color = 0;
        if(k==0){
            picked_color=catColor[0];
        }else{
            k=k%15;
            picked_color=catColor[k];
        }
        return picked_color;
    }

//    public int getColor(int k) {
//        int[] catColor = new int[100];
//        for (int i = 0; i < 3; i++) {
//            int j = i * 15;
//            catColor[j + 0] = context.getResources().getColor(R.color.pink);
//            catColor[j + 1] = context.getResources().getColor(R.color.colorAccent);
//            catColor[j + 2] = context.getResources().getColor(R.color.red);
//            catColor[j + 3] = context.getResources().getColor(R.color.orange);
//            catColor[j + 4] = context.getResources().getColor(R.color.red_maroon);
//            catColor[j + 5] = context.getResources().getColor(R.color.green_avocado);
//            catColor[j + 6] = context.getResources().getColor(R.color.green);
//            catColor[j + 7] = context.getResources().getColor(R.color.green_olive);
//            catColor[j + 8] = context.getResources().getColor(R.color.blue_sea);
//            catColor[j + 9] = context.getResources().getColor(R.color.blue);
//            catColor[j + 10] = context.getResources().getColor(R.color.blue_steel);
//            catColor[j + 11] = context.getResources().getColor(R.color.blue_dark);
//            catColor[j + 12] = context.getResources().getColor(R.color.blue_navy);
//            catColor[j + 13] = context.getResources().getColor(R.color.red_velvet);
//            catColor[j + 14] = context.getResources().getColor(R.color.red_claret);
//        }
//        return catColor[k];
//    }
}
