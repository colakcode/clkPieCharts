package com.clk.donutchart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.clk.donutchart.Interfaces.ClickDonutSlice;
import com.clk.donutchart.models.DonutObject;
import com.clk.donutchart.utils.AmountAccetable;
import com.clk.donutchart.utils.ParseString;
import com.clk.donutchart.utils.ResourcesColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
* @colakcode on GitHub 18.08.2020
*/

public class DonutChart extends View {

    private static final String TAG = "clk_DonutChart";
    private int max_number = 36;
    private int angle_step = 360 / max_number;
    private int gapStart = getContext().getResources().getInteger(R.integer.cake_graph_gap_start);
    private int gapStop = getContext().getResources().getInteger(R.integer.cake_graph_gap_stop);
    private List<Integer> defaultTBx;
    private List<Integer> defaultTBy;
    private RelativeLayout layoutGraph;
    private ResourcesColor rColor;
    private Activity activity;
    private int bodyColor = getContext().getResources().getColor(R.color.white);
    private int middleTextColor = getContext().getResources().getColor(R.color.grey_text);
    private List<DonutObject> donutObjects;
    private String middle_text="";

    public DonutChart(Activity activity, RelativeLayout layoutGraph, String middle_text) {
        super(activity);
        this.activity=activity;
        this.layoutGraph = layoutGraph;
        this.middle_text = middle_text;
        init();
    }

    private Paint[] paint;
    private Paint[] paintLine;
    private Paint paintObject, paintLines;
    private Path[] path;
    private Region[] region;
    private RectF[] ovals;
    private int paintTextSize = getContext().getResources().getInteger(R.integer.cake_graph_text_size);
    private int textSizeWarn = getContext().getResources().getInteger(R.integer.cake_graph_text_size_warn);
    private Paint paintWhite, paintGraphLine, paintWarn, paintBody, paintMiddleText;
    private int line_stroke = getContext().getResources().getInteger(R.integer.pie_line_stroke);
    private Typeface textFont;

    private void init() {

        rColor = new ResourcesColor(activity);
        pie_x = new ArrayList<>();
        pie_y = new ArrayList<>();
        qua_x = new ArrayList<>();
        qua_y = new ArrayList<>();
        tb_x = new ArrayList<>();
        tb_y = new ArrayList<>();

        /* Sınıfın çağrılması ile birlikte initilization methodu çağrılır ve tüm ön işlemler gerlekleştirilir
         * 1- Default textBox noktaları angularPoints(açısal noktalar) defaultTBx_y dizelerine atılır
         * 2- Paint, path ve region öğeleri tanımlandı ve maximum kategori sayısı kadar oluşturuldu
         * 3- Beyaz, siyah ve uyarı öğerleri için paintler oluşturuldu
         * */

        defaultTBx = new ArrayList<>();
        defaultTBy = new ArrayList<>();

        paint = new Paint[max_number];
        paintLine = new Paint[max_number];
        path = new Path[max_number];
        region = new Region[max_number];
        ovals = new RectF[max_number];

        for (int t = 0; t < max_number; t++) {
            paint[t] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint[t].setTextSize(paintTextSize);
            ovals[t] = new RectF();
            paint[t].setColor(rColor.getColor(t));
            path[t] = new Path();
            region[t] = new Region();

            paintLine[t] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintLine[t].setTextSize(paintTextSize);
            paintLine[t].setColor(rColor.getColor(t));
            paintLine[t].setStrokeWidth(line_stroke);
            paintLine[t].setStyle(Paint.Style.STROKE);

        }

        paintObject = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintObject.setTextSize(paintTextSize);
        paintObject.setColor(bodyColor);

        paintLines = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLines.setTextSize(paintTextSize);
        paintLines.setColor(bodyColor);
        paintLines.setStrokeWidth(line_stroke);
        paintLines.setStyle(Paint.Style.STROKE);

        textFont = Typeface.createFromAsset(activity.getAssets(), "fonts/poppins_bold.ttf");
        paintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintWhite.setColor(getContext().getResources().getColor(R.color.white));
        paintWhite.setTextSize(paintTextSize);

        paintBody = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBody.setTextSize(paintTextSize);

        paintGraphLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGraphLine.setColor(middleTextColor);
        paintGraphLine.setTextSize(paintTextSize);
        paintGraphLine.setDither(true);
        paintGraphLine.setStyle(Paint.Style.STROKE);
        paintGraphLine.setStrokeWidth(6);

        paintMiddleText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMiddleText.setColor(activity.getResources().getColor(R.color.grey_text));
        paintMiddleText.setDither(true);
        paintMiddleText.setTypeface(textFont);
        paintMiddleText.setTextAlign(Paint.Align.RIGHT);

        paintWarn = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintWarn.setColor(getContext().getResources().getColor(R.color.grey_dark));
        paintWarn.setTextSize(textSizeWarn);
    }

    private int width;
    private int tbWidth;
    private int tbHeight;
    private int fold_gap;
    private List<Integer> pie_x;
    private List<Integer> pie_y;
    private List<Integer> qua_x;
    private List<Integer> qua_y;
    private List<Integer> tb_x;
    private List<Integer> tb_y;

    private int offset;
    private int x_start, x_stop, y_start, y_stop;
    private int center_x, center_y;
    private int radius;
    private int middle_circle_radius = getContext().getResources().getInteger(R.integer.middle_circle_radius);
    private int sweepWhite = 360;
    private Canvas canvas;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.drawColor(bodyColor);
        paintBody.setColor(bodyColor);
        /*
         * 1- Measurements are calculated
         * 2- TextBox witdh and height are calculated
         * 3- Circle points and fold points are calculated
         * 4- Lines are drawing from the circle edge to fold point and from the fold point to textBox point
         */

        //1 -
        center_x = getWidth() / 2;
        center_y = getHeight() / 2;
        if (getWidth() > getHeight()) gapStop = getHeight() / 6;
        else gapStop = getHeight() / 5;
        width = getHeight();
        offset = (getWidth() - getHeight()) / 2;
        x_start = gapStart + offset;
        x_stop = width - gapStart + offset;
        y_start = gapStart;
        y_stop = width - gapStart;
        radius = (width - gapStart * 2) / 2;
        middle_circle_radius = radius / 2;
        fold_gap = (int) getWidth() / 14;

        //2- TextBox (tb) width and height
        tbWidth = (int) (getWidth() / 10);
        tbHeight = (int) (getHeight() / 17.33);

        for (int i = 0; i < max_number; i++) {
            int gap_x = getWidth() / 4;
            int gap_y = getHeight() / 5;

            int x = angularPoints(i * angle_step, gap_x).get(0);
            int y = angularPoints(i * angle_step, gap_y).get(1);

            defaultTBx.add(x);
            defaultTBy.add(y);
        }

        RectF oval = new RectF();
        RectF oval2 = new RectF();
        oval.set(x_start, y_start, x_stop, y_stop);
        oval2.set(0, 0, getRight(), getBottom());

        //3- add the circle edge point(pie_x,y) and fold point(qua_x,y) eklendi
        for (int k = 0; k < donutObjects.size(); k++) {

            int real_angle = start_angle.get(k) + sweep_angle.get(k) / 2;

            int col = donutObjects.get(k).getColor();
            if(col!=0) paintObject.setColor(col);
            else paintObject.setColor(rColor.getColor(k));

            Log.d(TAG, "onDraw: "+k+" : "+col);

            canvas.drawArc(oval, (start_angle.get(k) - 90), sweep_angle.get(k), true,paintObject );
            pie_x.add(angularPoints(real_angle, 0).get(0));
            pie_y.add(angularPoints(real_angle, 0).get(1));
            qua_x.add(angularPoints(real_angle, fold_gap).get(0));
            qua_y.add(angularPoints(real_angle, fold_gap).get(1));
        }

        //4- draw lines from the circle edge to fold poin and from the fold point to textBox point
        // tb_x,y points are created in createTextBox
        if (control_text) {
            try {
                for (int i = 0; i < donutObjects.size(); i++) {
                    int col = donutObjects.get(i).getColor();
                    if(col!=0) paintLines.setColor(col);
                    else paintLines.setColor(rColor.getColor(i));

                    final Path pathf = new Path();
                    pathf.moveTo(pie_x.get(i), pie_y.get(i));
                    pathf.quadTo(qua_x.get(i), qua_y.get(i), tb_x.get(i), tb_y.get(i));
                    canvas.drawPath(pathf, paintLines);
                }
            } catch (Exception e) {
            }
        }

        // middle layer swipe
        canvas.drawCircle(center_x, center_y, middle_circle_radius, paintBody);
        // top layer swipe
        canvas.drawArc(oval2, 0, sweepWhite, true, paintBody);

        if(control_text) drawMiddleText();

    }

    public List<Integer> angularPoints(int real_angle, int gap) {

        real_angle = real_angle - 90;
        int radius = (width - gapStop * 2) / 2;
        int centerX1 = getWidth() / 2;
        int pointX = (int) (centerX1 + (radius + gap) * Math.cos(Math.toRadians(real_angle)));

        int centerY = getHeight() / 2;
        int pointY = (int) (centerY + (radius + gap) * Math.sin(Math.toRadians(real_angle)));

        List<Integer> points = new ArrayList<>();
        points.add(pointX);
        points.add(pointY);

        return points;
    }

    private List<Integer> start_angle;
    private List<Integer> sweep_angle;
    private List<String> percentValue;
    public void setParams(List<DonutObject> donutObjects) {

        /*
        * THIS IS THE FIRS METHOD AFTER CONSTURACTOR IN ACTIVITY THAT CALLED THIS CLASS
        * 1- Tanımlamalar yapılır, dizeler temizlenir
        * 2- Total value is found to be able to rate per value
        * 3- Swipe angle is calculated using totalValue per value
             Created percenValue to shown in textBoxt
        */

        this.donutObjects = donutObjects;
        start_angle = new ArrayList<>();
        sweep_angle = new ArrayList<>();
        double totalValue = 0;
        percentValue = new ArrayList<>();
        int sweepAngle = 0;
        int startAngle = 0;

        //Total value is found to be able to rate per value
        for (int i = 0; i < donutObjects.size(); i++) {
            totalValue = totalValue + donutObjects.get(i).getValue();
        }

        // Swipe angle is calculated using totalValue per value
        // Created percenValue to shown in textBoxt
        int sumSweep = 0;
        for (int j = 0; j < donutObjects.size(); j++) {

            startAngle = sumSweep;

            sweepAngle = (int) ((360 * donutObjects.get(j).getValue()) / totalValue);

            percentValue.add(j, String.valueOf(
                    AmountAccetable.floorDouble((100 * donutObjects.get(j).getValue()) / totalValue)));

            sumSweep = sumSweep + sweepAngle;

            if (j == donutObjects.size() - 1) {
                sweepAngle = sweepAngle + (360 - sumSweep);
            }

            sweep_angle.add(sweepAngle); // for draw reverse clockwise graph
            start_angle.add(startAngle); // for draw reverse clockwise graph

            if (percentValue.get(j).equals("0")) percentValue.set(j, "");
            else percentValue.add(j, " " + percentValue.get(j) + " %");

        }

        setTextBoxes();
        postInvalidate();
    }

    public List<String> getPercentValue() {
        return percentValue;
    }

    private TextView[] textView;
    private boolean control_text = false;
    public void setTextBoxes() {
        /*
         * SECOND CALLED METHOD (IT IS CALLED IN SETPARAMS)
         * 1- 36 TextBoxes were created by default and "" was written in them.
         * 2- Object names written in textBoxes with 1000 ms delay, middle text is writen middle of graphic
         * */
        textView = new TextView[max_number];

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int k = 0; k < donutObjects.size(); k++) {
                    int real_angle = start_angle.get(k) + (sweep_angle.get(k) / 2);
                    createTextBox(optimizeName(donutObjects.get(k).getName()) + "\n" + percentValue.get(k),
                            real_angle, k, donutObjects.get(k).getId());
                }
                //setMiddleText();
                control_text = true;
            }

        }, 1000);
        changeAngle();
    }

    private int textSize = getContext().getResources().getInteger(R.integer.pie_text_size);
    private List<String> categoryInBox;

    public void createTextBox(String text, int real_angle, final int position,final  String id) {
        try {
            List<Integer> circlePoints = angularPoints(real_angle, 0);

            int k = 0;
            double control = getWidth();

            //textBoxes have already been created. The closest to the midpoint of the object angle is calculated
            for (int j = 0; j < max_number; j++) {
                int diffX = defaultTBx.get(j) - circlePoints.get(0);
                int diffY = defaultTBy.get(j) - circlePoints.get(1);
                double hipo = Math.pow(diffX, 2) + Math.pow(diffY, 2);
                hipo = Math.sqrt(hipo);
                if (hipo < control) {
                    control = hipo;
                    k = j;
                }
            }

            if (textView[k] != null) {
                for (int j = k; j < max_number; j++) {
                    if (textView[j] == null) {
                        k = j;
                        break;
                    } else if (j == max_number - 1 && textView[j] != null) {
                        j = 0;
                    }
                }
            }

            textView[k] = new TextView(activity);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textView[k].setText(" " + text);
            textView[k].setBackground(getResources().getDrawable(R.drawable.pie_text_rect));
            textView[k].setTextSize(textSize);
            textView[k].setTypeface(textView[k].getTypeface(), Typeface.BOLD);
            textView[k].setTextColor(Color.GRAY);
            textView[k].setId(position);

            int width = getWidth() - tbWidth;
            int height = getHeight() - tbHeight - 30;

            int x = defaultTBx.get(k);
            int y = defaultTBy.get(k);

            if (x < 0) x = 0;
            else if (x > width) x = width;

            if (y < 0) y = 0;
            else if (y > height) y = height;

            textView[k].setX(x);
            textView[k].setY(y);

            layoutGraph.addView(textView[k], params);

            textView[k].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickDonutSlice clickDonutSlice =(ClickDonutSlice) activity;
                    clickDonutSlice.getSliceInfo(donutObjects.get(position),position);
                }
            });

            categoryInBox = new ArrayList<>();
            boolean check = true;
            for (int g = 0; g < categoryInBox.size(); g++) {
                if (id.equals(categoryInBox.get(g))) {
                    check = false;
                    break;
                }
            }

            if (check || position == 0) {
                categoryInBox.add(id);
                tb_x.add(x + tbWidth / 2);
                tb_y.add(y + tbHeight / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMiddleText() {

        try {
            final TextView textYear = new TextView(activity);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            textYear.setBackgroundColor(getResources().getColor(R.color.transparent));
            textYear.setTextSize(30);
            textYear.setTypeface(textYear.getTypeface(), Typeface.BOLD);
            textYear.setTextColor(middleTextColor);
            textYear.setBackgroundColor(Color.TRANSPARENT);
            textYear.setText(middle_text);

            textYear.measure(0, 0);
            int w = textYear.getMeasuredWidth();
            int h = textYear.getMeasuredHeight();

            int x = getWidth() / 2 - w / 2;
            int y = getHeight() / 2 - h / 2;

            textYear.setX(x);
            textYear.setY(y);

            if (x < getWidth() / 2 && x > 0)
                layoutGraph.addView(textYear, params);

            textYear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            if (donutObjects.size() == 0) {
                textYear.setText("No Saves!");
            }

        } catch (Exception e) {
            Log.d(TAG, "setMiddleText: "+e.getMessage());
        }

    }

    private void drawMiddleText(){

        List<String> list = ParseString.get(middle_text," ");

        float ver_step = width/12;
        int size = list.size();

        if(size == 1){}
        if(size > 1){
            ver_step =width/15;
        }
        if(size > 3) ver_step = width/18;
        if(size > 4) ver_step = width/20;

        float y = center_y - ver_step * (size-1)/2;
        float x = center_x;

        float ts = ver_step;
        String longest_word = "";
        for(int i=0; i<list.size();i++){
            if(list.get(i).length() > longest_word.length()) longest_word = list.get(i);
        }

        ver_step = width/2;
        ver_step = ver_step - ver_step/8;
        ver_step = ver_step/(longest_word.length()+2);
        paintMiddleText.setTextSize(ver_step);
        paintMiddleText.setTextAlign(Paint.Align.CENTER);
        for(int i=0; i<list.size();i++){
            canvas.drawText(list.get(i),x,y+ver_step*i +(ver_step/(size+1)) ,paintMiddleText);
        }
    }

    public void setBodyColor(int color){
        bodyColor = color;
    }

    public void changeAngle() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sweepWhite -= 3;
                gapStart -= 5;
                if (gapStart <= gapStop) gapStart = gapStop;

                if (sweepWhite <= 0) {
                    cancel();
                    isGraphicFinished = true;
                    return;
                }
                postInvalidate();
            }
        }, 500l, 5l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();
                boolean check = false;

                float diffX = center_x - x;
                float diffY = center_y - y;
                double hipo = Math.pow(diffX, 2) + Math.pow(diffY, 2);
                hipo = Math.sqrt(hipo);

                if (hipo < radius && hipo > middle_circle_radius) {
                    check = true;
                }

                if (check) {
                    double alfa = diffY / hipo;
                    alfa = Math.acos(alfa);
                    double alfaDeg = Math.toDegrees(alfa);
                    if (x < getWidth() / 2) {
                        alfaDeg = 360 - alfaDeg;
                    }

                    for (int i = 0; i < donutObjects.size(); i++) {
                        if (alfaDeg > start_angle.get(i)
                                && alfaDeg < start_angle.get(i) + sweep_angle.get(i)) {
                            try {
                                ClickDonutSlice clickDonutSlice =(ClickDonutSlice)activity;
                                clickDonutSlice.getSliceInfo(donutObjects.get(i), i);
                            } catch (Exception e) {
                                Log.d(TAG, "onTouchEvent: "+e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    public static boolean isGraphicFinished = false;

    public static boolean isDrawingFinished() {
        return isGraphicFinished;
    }

    public static void setIsGraphicFinished(boolean check) {
        isGraphicFinished = check;
    }

    public String optimizeName(String category) {
        String new_name = "";

        if (category.length() > 10) {
            for (int i = 0; i < category.length(); i++) {
                if (i <= 6) {
                    char fchar = category.charAt(i);
                    new_name = new_name + fchar;

                } else if (i > 6 && i < 10) {
                    new_name = new_name + ".";

                } else if (i == 10) {
                    break;
                }
            }
        } else {
            new_name = category;
        }
        return new_name;
    }

    public DonutChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DonutChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DonutChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
