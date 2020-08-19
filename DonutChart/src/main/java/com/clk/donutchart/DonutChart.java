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
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DonutChart extends View {

    private static final String TAG = "clkDonutChart";
    private int number_of_categories = 36;
    private int angle_step = 360 / number_of_categories;
    private int gapStart = getContext().getResources().getInteger(R.integer.cake_graph_gap_start);
    private int gapStop = getContext().getResources().getInteger(R.integer.cake_graph_gap_stop);
    private List<Integer> defaultTBx;//default TextBox X
    private List<Integer> defaultTBy;//default TextBox Y
    private RelativeLayout layoutGraph;
    private ResourcesColor rColor;
    private Activity activity;
    private int bodyColor = getContext().getResources().getColor(R.color.white);
    private int middleTextColor = getContext().getResources().getColor(R.color.grey_text);
    private List<DonutObject> donutObjects;
    private String middle_text;


    public DonutChart(Activity activity, RelativeLayout layoutGraph, String middle_text) {
        super(activity);
        this.activity=activity;
        this.layoutGraph = layoutGraph;
        this.middle_text = middle_text;

        init();
    }

    private Paint[] paint;
    private Paint[] paintLine;
    private Path[] path;
    private Region[] region;
    private RectF[] ovals;
    private int paintTextSize = getContext().getResources().getInteger(R.integer.cake_graph_text_size);
    private int textSizeWarn = getContext().getResources().getInteger(R.integer.cake_graph_text_size_warn);
    private Paint paintWhite, paintGraphLine, paintWarn, paintBody;
    private int line_stroke = getContext().getResources().getInteger(R.integer.pie_line_stroke);

    private void init() {

        rColor = new ResourcesColor(activity);
        pie_x = new ArrayList<>();
        pie_y = new ArrayList<>();
        qua_x = new ArrayList<>();
        qua_y = new ArrayList<>();
        tb_x = new ArrayList<>();
        tb_y = new ArrayList<>();

        /*Sınıfın çağrılması ile birlikte initilization methodu çağrılır ve tüm ön işlemler gerlekleştirilir
         * 1- Default textBox noktaları angularPoints(açısal noktalar) defaultTBx_y dizelerine atılır
         * 2- Paint, path ve region öğeleri tanımlandı ve maximum kategori sayısı kadar oluşturuldu
         * 3- Beyaz, siyah ve uyarı öğerleri için paintler oluşturuldu
         * */

        // Default textBox noktaları angularPoints(açısal noktalar) defaultTBx_y dizelerine atılır
        defaultTBx = new ArrayList<>();
        defaultTBy = new ArrayList<>();

        //Paint, path ve region öğeleri tanımlandı ve maximum kategori sayısı kadar oluşturuldu
        paint = new Paint[number_of_categories];
        paintLine = new Paint[number_of_categories];
        path = new Path[number_of_categories];
        region = new Region[number_of_categories];
        ovals = new RectF[number_of_categories];

        for (int t = 0; t < number_of_categories; t++) {
            paint[t] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint[t].setTextSize(paintTextSize);
            ovals[t] = new RectF();
            //paint[t].setTextScaleX(25);
            paint[t].setColor(rColor.getColor(t));
            path[t] = new Path();
            region[t] = new Region();


            paintLine[t] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintLine[t].setTextSize(paintTextSize);
            paintLine[t].setColor(rColor.getColor(t));
            paintLine[t].setStrokeWidth(line_stroke);
            paintLine[t].setStyle(Paint.Style.STROKE);

        }

        //Beyaz, siyah ve uyarı öğerleri için paintler oluşturuldu
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

        paintWarn = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintWarn.setColor(getContext().getResources().getColor(R.color.grey_dark));
        paintWarn.setTextSize(textSizeWarn);
    }

    private int width;
    private int tbWidth;//textBoxların genişliği
    private int tbHeight;
    private int fold_gap;
    private List<Integer> pie_x;// grafik çemberinin üzerinden çıkan çizgilerin başlangıç noktası
    private List<Integer> pie_y;
    private List<Integer> qua_x;// çemberden textBoxlara giden çizginin kıvrıldığı nokta
    private List<Integer> qua_y;
    private List<Integer> tb_x;// textBox (tb_x) 'ların noktaları alındı
    private List<Integer> tb_y;

    private int offset;
    private int x_start, x_stop, y_start, y_stop;
    private int center_x, center_y;
    private int radius;
    private int middle_circle_radius = getContext().getResources().getInteger(R.integer.middle_circle_radius);
    private int sweepWhite = 360;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bodyColor);
        paintBody.setColor(bodyColor);
        /*
         * 1- OnDraw tanımlamalar yapıldı. pie_ çember üzerindeki nokta, fold_ kıvrım noktası, tb_ textBox'ların orta noktası
         * 2- TextBox (tb) ların genişlik ve yükseklik değerleri atandı
         * 3- Çember üstü noktalar(pie_x,y) ve kıvrım noktaları(qua_x,y) eklendi
         * 4- Çember üstü --> ve kıvrım noktasına, kıvrım noktasında --> textBox noktalarına çizimler yapıldı
         * */

        //1- OnDraw tanımlamalar ve eşitlikler
        center_x = getWidth() / 2;
        center_y = getHeight() / 2;

        if (getWidth() > getHeight()) gapStop = getHeight() / 6;
        else gapStop = getHeight() / 5;

        //grafiğin başlangıç ve bitiş noktalarının belirlenmesi
        width = getHeight();
        offset = (getWidth() - getHeight()) / 2;
        x_start = gapStart + offset;
        x_stop = width - gapStart + offset;
        y_start = gapStart;
        y_stop = width - gapStart;
        radius = (width - gapStart * 2) / 2;
        middle_circle_radius = radius / 2;//orta beyaz(yıl)
        fold_gap = (int) getWidth() / 14; // çember üzeri noktadan kıvrım noktasını belirleyen boşluk

        //2- TextBox (tb) ların genişlik ve yükseklik değerleri atandı
        tbWidth = (int) (getWidth() / 10);
        tbHeight = (int) (getHeight() / 17.33);

        for (int i = 0; i < number_of_categories; i++) {
            int gap_x = getWidth() / 4;
            int gap_y = getHeight() / 5;

            //Log.d(TAG, "onDraw: gap_x:"+gap_x+" gap_y:"+gap_y);

            int x = angularPoints(i * angle_step, gap_x).get(0);
            int y = angularPoints(i * angle_step, gap_y).get(1);

            defaultTBx.add(x);
            defaultTBy.add(y);
            //Log.d(TAG, "onDraw: x:"+x+" y:"+y+"angle_step");
        }

        RectF oval = new RectF();
        RectF oval2 = new RectF();
        oval.set(x_start, y_start, x_stop, y_stop);
        oval2.set(0, 0, getRight(), getBottom());

        //3- Çember üstü noktalar(pie_x,y) ve kıvrım noktaları(qua_x,y) eklendi
        for (int k = 0; k < donutObjects.size(); k++) {

            int real_angle = start_angle.get(k) + sweep_angle.get(k) / 2;
            canvas.drawArc(oval, (start_angle.get(k) - 90), sweep_angle.get(k), true, paint[k]);
            pie_x.add(angularPoints(real_angle, 0).get(0));
            pie_y.add(angularPoints(real_angle, 0).get(1));
            qua_x.add(angularPoints(real_angle, fold_gap).get(0));
            qua_y.add(angularPoints(real_angle, fold_gap).get(1));

            //Log.d(TAG, "onDraw: real_angle:" + real_angle);
        }

        //4- Çember üstü --> ve kıvrım noktasına, kıvrım noktasında --> textBox noktalarına çizimler yapıldı
        // tb_x,y noktaları createTexBox metodu içerisinde yaratıldı
        if (control_text) {
            try {
                for (int i = 0; i < tb_x.size(); i++) {
                    final Path pathf = new Path();
                    pathf.moveTo(pie_x.get(i), pie_y.get(i));
                    pathf.quadTo(qua_x.get(i), qua_y.get(i), tb_x.get(i), tb_y.get(i));
                    canvas.drawPath(pathf, paintLine[i]);
                }
            } catch (Exception e) {
            }
        }

        canvas.drawCircle(center_x, center_y, middle_circle_radius, paintBody);// yıl bilgisinin olduğu orta nokta beyaza boyanır
        // en üst beyaz katman tamamen beyaza boyanır sweepWhite azaltılarak kaldırılır
        canvas.drawArc(oval2, 0, sweepWhite, true, paintBody);

    }

    public void setBodyColor(int color){
        bodyColor = color;
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

    public void drawGraphs(List<DonutObject> donutObjects) {

        /*
        * SINIF ÇAĞRILDIKTAN SONRA, İLK ÇAĞRILAN METHOD
        * 1- Tanımlamalar yapılır, dizeler temizlenir
        * 2- Kategori başına oranlama yapabilmek için toplam değer bulunur
        * 3- Toplam değer kullanılarak kategori başına tarama açısı hesaplanır,
             Kategori kutucuklarında görüntülemek üzere percentValue dizesine eklenir
        * */

        // Tanımlamalar
        this.donutObjects = donutObjects;
        start_angle = new ArrayList<>();
        sweep_angle = new ArrayList<>();
        double sumExpenses = 0;
        percentValue = new ArrayList<>();
        int sweepAngle = 0;
        int startAngle = 0;

        //Kategori başına oranlama yapabilmek için toplam değer bulunur
        for (int i = 0; i < donutObjects.size(); i++) {
            sumExpenses = sumExpenses + donutObjects.get(i).getValue();
        }

        // Toplam değer kullanılarak kategori başına tarama açısı hesaplanır,
        // Kategori kutucuklarında görüntülemek üzere percentValue dizesine eklenir
        int sumSweep = 0;
        for (int j = 0; j < donutObjects.size(); j++) {

            startAngle = sumSweep;

            sweepAngle = (int) ((360 * donutObjects.get(j).getValue()) / sumExpenses);

            percentValue.add(j, String.valueOf(
                    AmountAccetable.floorDouble((100 * donutObjects.get(j).getValue()) / sumExpenses)));

            sumSweep = sumSweep + sweepAngle;

            if (j == donutObjects.size() - 1) {
                sweepAngle = sweepAngle + (360 - sumSweep);
            }

            sweep_angle.add(sweepAngle); // for draw reverse clockwise graph
            start_angle.add(startAngle); // for draw reverse clockwise graph

            if (percentValue.get(j).equals("0")) percentValue.set(j, "");
            else percentValue.add(j, " " + percentValue.get(j) + " %");

            //Log.i("clkinfo", "start : " + "" + startAngle + "   sweep : " + sweepAngle);

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
         * IKINCI OLARAK OLARAK ÇAĞRILAN METHOD
         * 1- Default olarak 36 adet TextView oluşturuldu ve içerisine "" olarak dolduruldu
         * 2- 1000 sn gecikmeli olarak kullanılan kategoriler (categories_used) textView'lara yazıldı
         * yıl değeri grafik ortasına sabitlendi, control_text mantıksal değeri true olarak ayarlandı
         * */
        textView = new TextView[36];
//        for (int l = 0; l < number_of_categories; l++) { GEREK GÖRÜLMEDİĞİ İÇİN KALDIRILDI
//            if (textView[l] != null) {
//                textView[l].setText("");
//            }
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int k = 0; k < donutObjects.size(); k++) {
                    int real_angle = start_angle.get(k) + (sweep_angle.get(k) / 2);

                    //Kategori tarama payları ile belirlenen start ve sweep angle değerleriyle tek tek
                    // textBox lar çağrılıyor. Buradaki real_angle hangi textBox ın en yakın olacağı
                    //hesabında kullanılacak
                    createTextBox(optimizeName(donutObjects.get(k).getName()) + "\n" + percentValue.get(k),
                            real_angle, k, donutObjects.get(k).getId());
                }
                setMiddleText();
                control_text = true;
            }

        }, 1000);
        changeAngle();
    }

    private int textSize = getContext().getResources().getInteger(R.integer.pie_text_size);
    private List<String> categoryInBox;

    public void createTextBox(String text, int real_angle, final int position,final  String tag) {
        //textView = new TextView[number_of_categories];
        try {
            List<Integer> circlePoints = angularPoints(real_angle, 0);

            int k = 0;
            double control = getWidth();

            // Teker teker bakılıp en yakını bulunuyor (defaultTBX ve TBy önceden belirlendi
            // en yakın noktanın positionu bulunacak )
            for (int j = 0; j < number_of_categories; j++) {
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
                for (int j = k; j < number_of_categories; j++) {
                    if (textView[j] == null) {
                        k = j;
                        break;
                    } else if (j == number_of_categories - 1 && textView[j] != null) {
                        j = 0;
                    }
                }
            } else {
                k = k;
            }

            textView[k] = new TextView(activity);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textView[k].setText(" " + text);
            textView[k].setBackgroundDrawable(getResources().getDrawable(R.drawable.pie_text_rect));
            textView[k].setTextSize(textSize);
            textView[k].setTypeface(textView[k].getTypeface(), Typeface.BOLD);
            textView[k].setTextColor(Color.GRAY);
            textView[k].setId(position);

            int width = getWidth() - tbWidth;
            int height = getHeight() - tbHeight - 30;

            int x = defaultTBx.get(k);
            int y = defaultTBy.get(k);

            //Log.d(TAG, "createTextBox: x:"+x+" y:"+y);

            if (x < 0) x = 0;
            else if (x > width) x = width;

            if (y < 0) y = 0;
            else if (y > height) y = height;

            textView[k].setX(x);
            textView[k].setY(y);

            layoutGraph.addView(textView[k], params);


            /* BAKKKK???????????
            textView[k].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickPieInterface clickPieInterface =(ClickPieInterface)activity;
                    clickPieInterface.pickPieOfChart(position,tag);
                }
            });

            */

            categoryInBox = new ArrayList<>();
            boolean check = true;
            for (int g = 0; g < categoryInBox.size(); g++) {
                if (tag.equals(categoryInBox.get(g))) {
                    check = false;
                    break;
                }
            }

            if (check || position == 0) {
                categoryInBox.add(tag);
                tb_x.add(x + tbWidth / 2); // tb_x çizginin vardığı nokta, tbWidth textBox'ın genişliği
                tb_y.add(y + tbHeight / 2);
            }
        } catch (Exception e) {
            //Toast.makeText(context, "catch", Toast.LENGTH_LONG).show();
        }
    }

    public TextView[] getTextViews() {
        return textView;
    }

//    public List<String> categories_used() {
//        return categories_used;
//    }

    public List<String> getCategoryInBox() {
        return categoryInBox;
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
        }

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
                    //sharedPref.save(context, "check_statistic", "true");
                    check_statistic = true;
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
                    //Log.d(TAG, "onTouchEvent: cateSize :"+categoryInBox.size());

                    for (int i = 0; i < donutObjects.size(); i++) {

                        if (alfaDeg > start_angle.get(i)
                                && alfaDeg < start_angle.get(i) + sweep_angle.get(i)) {
                            try {

                                /*
                                ClickPieInterface clickPieInterface =(ClickPieInterface)activity;
                                clickPieInterface.pickPieOfChart(i, category_tags.get(i)); */

                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            case MotionEvent.ACTION_MOVE: {
            }
        }
        return value;
    }

    public static boolean check_statistic = false;

    public static boolean isCheck_statistic() {
        return check_statistic;
    }

    public static void setCheck_statistic(boolean check) {
        check_statistic = check;
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
