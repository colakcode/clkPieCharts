package com.clk.clkpieanddonut;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.clk.clkpieanddonut.Interfaces.ClkChartsInterface;
import com.clk.clkpieanddonut.models.PieObject;
import com.clk.clkpieanddonut.models.TextBoxPoints;
import com.clk.clkpieanddonut.utils.AmountAccetable;
import com.clk.clkpieanddonut.utils.OptimizeName;
import com.clk.clkpieanddonut.utils.ResourcesColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PieChart extends View {

    private static final String TAG = "clk_PieChart";
    private Activity activity;
    private List<PieObject> pieObjects;
    private RelativeLayout layoutGraph;
    private Paint paintBody, paintObjects, paintLines,
            paintTextBox, paintTextBorer, paintTextFill;
    private int line_stroke = getContext().getResources().getInteger(R.integer.pie_line_stroke);
    private int bodyColor;
    private int middleTextColor = getContext().getResources().getColor(R.color.grey_text);
    private int paintTextSize = getContext().getResources().getInteger(R.integer.cake_graph_text_size);
    private Typeface textFont;
    private ResourcesColor resourcesColor;
    private int gapStart = getContext().getResources().getInteger(R.integer.cake_graph_gap_start);
    private int gapStop = getContext().getResources().getInteger(R.integer.cake_graph_gap_stop);
    private boolean isGraphicFinished = false;
    private List<Float> pie_x, pie_y, qua_x, qua_y, tb_x, tb_y;
    private float min_angle=30;

    public PieChart(Activity activity, List<PieObject> pieObjects, RelativeLayout layoutGraph) {
        super(activity);
        this.activity = activity;
        this.pieObjects = pieObjects;
        this.layoutGraph = layoutGraph;

        pie_x = new ArrayList<>();
        pie_y = new ArrayList<>();
        qua_x = new ArrayList<>();
        qua_y = new ArrayList<>();
        tb_x = new ArrayList<>();
        tb_y = new ArrayList<>();

        min_angle = 20;
        if(min_angle > 25) min_angle = 0;

        bodyColor = activity.getResources().getColor(R.color.white);
        textFont = Typeface.createFromAsset(activity.getAssets(), "fonts/poppins_bold.ttf");
        resourcesColor = new ResourcesColor(activity);

        paintBody = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBody.setColor(bodyColor);

        paintObjects = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintObjects.setTextSize(paintTextSize);
        paintObjects.setColor(bodyColor);

        paintLines = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLines.setTextSize(paintTextSize);
        paintLines.setColor(bodyColor);
        paintLines.setStrokeWidth(line_stroke);
        paintLines.setStyle(Paint.Style.STROKE);

        paintTextBox = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextBox.setColor(activity.getResources().getColor(R.color.grey_text));
        paintTextBox.setDither(true);
        paintTextBox.setTypeface(textFont);
        paintTextBox.setTextAlign(Paint.Align.LEFT);

        paintTextFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextFill.setStyle(Paint.Style.FILL);
        paintTextFill.setColor(bodyColor);
        paintTextBorer = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextBorer.setStyle(Paint.Style.STROKE);
        paintTextBorer.setColor(middleTextColor);

    }

    private Canvas canvas;
    private float canvasWidth, canvasHeight, centerX, centerY,
            marginEdge, leftEdge, rightEdge, topEdge, bottomEdge,
            pieLeft, pieRight, pieTop, pieBottom;
    private float stroke_width = 0;
    private RectF pieRect;
    private float pie_radius;
    private float middle_circle_radius, fold_gap, textBoxR, tbWidth, tbHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.drawColor(bodyColor);
        paintTextFill.setColor(bodyColor);
        paintBody.setColor(bodyColor);
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        stroke_width = getWidth() / 200;

        centerX = canvasWidth / 2;
        centerY = canvasHeight / 2;
        marginEdge = canvasWidth / 10;
        leftEdge = marginEdge;
        rightEdge = canvasWidth - marginEdge;
        topEdge = marginEdge;
        bottomEdge = canvasHeight - (marginEdge + marginEdge / 2);

        pieLeft = 2 * marginEdge + gapStart;
        pieRight = canvasWidth - 2 * marginEdge - gapStart;
        pieTop = 2 * marginEdge + gapStart;
        pieBottom = canvasHeight - 2 * marginEdge - gapStart;
        pie_radius = (pieRight - pieLeft) / 2;
        middle_circle_radius = pie_radius / 2;
        fold_gap = canvasWidth / 14;
        textBoxR = canvasWidth / 5;

        //2- TextBox (tb) width and height
        tbWidth = (int) (getWidth() / 10);
        tbHeight = (int) (getHeight() / 17.33);

        paintLines.setStrokeWidth(stroke_width);
        paintLines.setTextSize(canvasWidth / 40);
        paintLines.setTextAlign(Paint.Align.CENTER);

        pieRect = new RectF(pieLeft, pieTop, pieRight, pieBottom);
        setParams();
        //Log.d(TAG, "onDraw: ");
    }

    private int sweepWhite = 360;
    private List<Float> start_angles;
    private List<Float> sweep_angles;
    private List<Float> real_angles;
    private List<Double> percentage_value;
    private int touched_object = 0;
    private float seperate_raduis = 0;
    private float non_touched_radius = 0;
    public int DRAW_TEXT_RQS = -9999;

    private void setParams() {
        start_angles = new ArrayList<>();
        sweep_angles = new ArrayList<>();
        real_angles = new ArrayList<>();
        percentage_value = new ArrayList<>();
        start_angles.clear();
        sweep_angles.clear();
        real_angles.clear();
        percentage_value.clear();

        getTotalValue();
        float start_angle = 0;
        for (int i = 0; i < pieObjects.size(); i++) {
            PieObject pieObject = pieObjects.get(i);
            float sweep_angle = getSweepAngle(pieObject.getValue());
            float real_angle = start_angle + sweep_angle / 2;
            int color = pieObject.getColor() == 0 ? resourcesColor.getColor(i) : pieObject.getColor();
            paintObjects.setColor(color);
            pieObjects.get(i).setColor(color);
            float obj_radius;
            if (i == touched_object) obj_radius = seperate_raduis;
            else obj_radius = non_touched_radius;

            pieRect.set(rectOffsetX(real_angle, pieLeft, obj_radius), rectOffsetY(real_angle, pieTop, obj_radius),
                    rectOffsetX(real_angle, pieRight, obj_radius), rectOffsetY(real_angle, pieBottom, obj_radius));
            canvas.drawArc(pieRect, start_angle, sweep_angle, true, paintObjects);

            start_angles.add(start_angle);
            sweep_angles.add(sweep_angle);
            real_angles.add(real_angle);
            percentage_value.add(AmountAccetable.floorDouble((100 * pieObjects.get(i).getValue()) / total_value));
            start_angle = start_angle + sweep_angle;
        }

        // top layer swipe
        RectF ovalWhiteMask = new RectF();
        ovalWhiteMask.set(0, 0, getRight(), getBottom());
        canvas.drawArc(ovalWhiteMask, 0, sweepWhite, true, paintBody);

        if (!isChangeAngleCalled) {
            changeAngle();
        }

        if (isGraphicFinished) {
                drawTextBoxes();
        }
    }

    public List<Float> angularPoints(float real_angle, float gap) {
        float radius = pie_radius;
        float centerX1 = getWidth() / 2;
        float pointX = (float) (centerX1 + (radius + gap) * Math.cos(Math.toRadians(real_angle)));

        float centerY = getHeight() / 2;
        float pointY = (float) (centerY + (radius + gap) * Math.sin(Math.toRadians(real_angle)));

        List<Float> points = new ArrayList<>();
        points.add(pointX);
        points.add(pointY);

        return points;
    }

    private double total_value = 0;

    private void getTotalValue() {
        total_value = 0;
        for (PieObject donObj : pieObjects) {
            total_value = total_value + donObj.getValue();
        }
    }

    private float getSweepAngle(double value) {
        return (float) ((value * 360) / total_value);
    }

    private Float rectOffsetX(float real_angle, float x, float radius) {

        float pointX = (float) (x + (radius) * Math.cos(Math.toRadians(real_angle)));
        return pointX;
    }

    private Float rectOffsetY(float real_angle, float y, float radius) {

        float pointY = (float) (y + (radius) * Math.sin(Math.toRadians(real_angle)));
        return pointY;
    }

    private boolean isChangeAngleCalled = false;
    private List<TextBoxPoints> tbPoints;

    public void drawTextBoxes() {
        tbPoints = new ArrayList<>();
        tbPoints.clear();
        for (int i = 0; i < pieObjects.size(); i++) {

            if(DRAW_TEXT_RQS==-9999 || DRAW_TEXT_RQS ==i){

                if(sweep_angles.get(i)<min_angle && DRAW_TEXT_RQS == -9999) continue;

                float real_angle = real_angles.get(i);

                if (real_angle > 315 || real_angle < 45) textBoxR = canvasWidth / 12;
                else textBoxR = canvasWidth / 5;
                float x = angularPoints(real_angle, textBoxR).get(0);
                float y = angularPoints(real_angle, textBoxR).get(1);

                float tbW = canvasWidth/6;
                float tbH = canvasHeight/16;
                float rounded = canvasWidth/70;

                if(y+tbHeight+5>canvasHeight) y = y - tbH;
                if(x+tbW+5>canvasWidth) x = x - tbW/4-20;

                tbPoints.add(new TextBoxPoints(x,y,i, pieObjects.get(i)));

                int col = pieObjects.get(i).getColor();
                if (col != 0) paintLines.setColor(col);
                else paintLines.setColor(resourcesColor.getColor(i));
                final Path pathf = new Path();
                pathf.moveTo(angularPoints(real_angle, 0).get(0), angularPoints(real_angle, 0).get(1));
                float step = -5;
                pathf.quadTo(angularPoints(real_angle, fold_gap).get(0), angularPoints(real_angle, fold_gap).get(1),
                        x+tbW/2, y+tbH/2);

                canvas.drawPath(pathf, paintLines);

                paintTextBorer.setStrokeWidth(canvasWidth / 300);
                canvas.drawRoundRect(x, y, x+tbW, y+tbH, rounded, rounded, paintTextFill);
                canvas.drawRoundRect(x, y, x+tbW, y+tbH, rounded, rounded, paintTextBorer);
                paintTextBox.setTextSize(canvasWidth / 40);
                canvas.drawText(OptimizeName.get(pieObjects.get(i).getName()), x+5, y+tbH/2, paintTextBox);
                canvas.drawText("%"+percentage_value.get(i), x+5, y+tbH-5, paintTextBox);
            }
        }
    }

    public void selectObject(int position){
        DRAW_TEXT_RQS = position;
        touched_object = position;
        changeRadius(position);
    }

    public void setBackgroundColor(int color){
        bodyColor = color;
       // drawAgain();
    }

    private void drawAgain() {
        isChangeAngleCalled = true;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 10l, 5l);
    }

    public void resetGraph(){
        resetRadius();
        postInvalidate();
    }

    private void changeAngle() {
        isChangeAngleCalled = true;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sweepWhite -= 3;
                gapStart -= 5;
                if (gapStart <= gapStop) gapStart = gapStop;

                if (sweepWhite <= 0) {
                    cancel();
                    gapStart = 0;
                    isGraphicFinished = true;
                    ClkChartsInterface clkChartsInterface = (ClkChartsInterface) activity;
                    clkChartsInterface.getPercentage(percentage_value);
                    return;
                }
                postInvalidate();
            }
        }, 500l, 5l);
    }

    private void changeRadius(final int position) {
        //isGraphicFinished = false;

        seperate_raduis = 0;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                seperate_raduis += 3;
                if (!isTouched)
                    non_touched_radius -= 0.1f;

                if (seperate_raduis >= 60) {
                    cancel();
                    isTouched = true;
                    return;
                }
                postInvalidate();
            }
        }, 5l, 5l);
    }

    private void resetRadius() {
        if (seperate_raduis > 0) {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    seperate_raduis -= 3;

                    if (seperate_raduis >= 0) {
                        non_touched_radius = 0;
                        seperate_raduis = 0;
                        cancel();
                        isTouched = false;
                        DRAW_TEXT_RQS = -9999;
                        return;
                    }
                    postInvalidate();
                }
            }, 1l, 5l);
        }
    }

    private boolean isTouched = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();
                boolean check_in_pie = false;

                float diffX = centerX - x;
                float diffY = centerY - y;
                double hypo = Math.pow(diffX, 2) + Math.pow(diffY, 2);

                hypo = Math.sqrt(hypo);
                if (hypo < pie_radius && hypo > middle_circle_radius) {
                    check_in_pie = true;
                }

                if (check_in_pie) {
                    double alfa_angle = diffY / hypo;
                    alfa_angle = Math.acos(alfa_angle);
                    double alfaDeg = Math.toDegrees(alfa_angle);

                    if (x < canvasWidth / 2) {
                        alfaDeg = 360 - alfaDeg;
                    }

                    alfaDeg = alfaDeg - 90;
                    if (alfaDeg < 0) alfaDeg = alfaDeg + 360;

                    for (int i = 0; i < pieObjects.size(); i++) {
                        if (alfaDeg > start_angles.get(i) && alfaDeg < start_angles.get(i) + sweep_angles.get(i)) {
                            try {
                                ClkChartsInterface clkChartsInterface = (ClkChartsInterface) activity;
                                clkChartsInterface.onClickPieSlice(pieObjects.get(i), i, percentage_value.get(i));
                                touched_object = i;
                                DRAW_TEXT_RQS = i;
                                changeRadius(i);
                            } catch (Exception e) {
                                Log.d(TAG, "onTouchEvent: " + e.getMessage());
                            }
                        }
                    }
                } else {
                    float tbW = canvasWidth/6;
                    float tbH = canvasHeight/16;
                    TextBoxPoints selected_point = new TextBoxPoints();
                    boolean check_touch_box = false;
                    for(int i=0 ; i<tbPoints.size(); i++){
                        TextBoxPoints tbp = tbPoints.get(i);
                        if(x>tbp.getX() && x<tbp.getX()+tbW){
                            if(y>tbp.getY() && y<tbp.getY()+tbH){
                                check_touch_box = true;
                                selected_point = tbp;
                                break;
                            }
                        }
                    }
                    if(check_touch_box){
                        int pos = selected_point.getPosition();

                        ClkChartsInterface clkChartsInterface = (ClkChartsInterface) activity;
                        clkChartsInterface.onClickTextBox(selected_point.getPieObject(), pos, percentage_value.get(pos));

                        touched_object = pos;
                        DRAW_TEXT_RQS = pos;
                        changeRadius(pos);
                        Log.d(TAG, "onTouchEvent: \nx : "+selected_point.getX()+
                                "\ny : "+selected_point.getY()+
                                "\npos : "+selected_point.getPosition()+
                                "\nname : "+selected_point.getPieObject().getName());

                    }else{
                        resetRadius();
                    }
                }
            }
        }
        postInvalidate();
        return value;
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

