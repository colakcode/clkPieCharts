package com.clk.clkpieanddonut.utils;

import android.app.Activity;
import android.content.Context;

import com.clk.clkpieanddonut.R;


/**
 * Created by CLK on 09.09.2018.
 */

public class ResourcesColor {

    private static final String TAG = "clk_ResourcesColor";
    private Context activity;

    int[] catColor = new int[15];
    public ResourcesColor(Activity activity) {
        this.activity = activity;
        int i = 0;
        catColor[i] = activity.getResources().getColor(R.color.pink_light);i++;
        catColor[i] = activity.getResources().getColor(R.color.pink);i++;
        catColor[i] = activity.getResources().getColor(R.color.pink_burnet);i++;
        catColor[i] = activity.getResources().getColor(R.color.red_vine);i++;
        catColor[i] = activity.getResources().getColor(R.color.red_maroon);i++;
        catColor[i] = activity.getResources().getColor(R.color.red_velvet);i++;
        catColor[i] = activity.getResources().getColor(R.color.purple_haze);i++;
        catColor[i] = activity.getResources().getColor(R.color.blue_steel);i++;
        catColor[i] = activity.getResources().getColor(R.color.blue);i++;
        catColor[i] = activity.getResources().getColor(R.color.blue_butterfly);i++;
        catColor[i] = activity.getResources().getColor(R.color.green_onion);i++;
        catColor[i] = activity.getResources().getColor(R.color.green);i++;
        catColor[i] = activity.getResources().getColor(R.color.green_avocado);i++;
        catColor[i] = activity.getResources().getColor(R.color.orange_cantaloupe);i++;
        catColor[i] = activity.getResources().getColor(R.color.yellow);i++;
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
}
