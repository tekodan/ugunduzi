package ojovoz.ugunduzi;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.util.Iterator;

/**
 * Created by Eugenio on 13/03/2018.
 */
public class farmInterface extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Paint paint;
    View view;
    Bitmap bitmap;
    Canvas canvas;

    int displayWidth;
    int displayHeight;
    Bitmap iconMove;
    Bitmap iconResize;

    String user;
    int userId;
    boolean newFarm;

    oPlotMatrix plotMatrix;

    preferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_interface);

        user=getIntent().getExtras().getString("user");
        userId=getIntent().getExtras().getInt("userId");
        newFarm = getIntent().getExtras().getBoolean("newFarm");

        if(newFarm){
            this.setTitle(R.string.drawNewFarmTitle);
            defineFarmNameAcres(1);
        }

        plotMatrix = new oPlotMatrix();

        prefs = new preferenceManager(this);

        iconMove=BitmapFactory.decodeResource(this.getResources(),R.drawable.move);
        iconResize=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize);

        LinearLayout root = (LinearLayout) findViewById(R.id.mainRoot);
        root.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                Window win = getWindow();
                win.getDecorView().getWindowVisibleDisplayFrame(rect);
                int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                displayWidth = metrics.widthPixels;
                displayHeight = (int)(metrics.heightPixels-(contentViewTop*metrics.density));

                relativeLayout = (RelativeLayout)findViewById(R.id.drawingCanvas);
                view = new SketchSheetView(farmInterface.this,displayWidth,displayHeight);
                paint = new Paint();
                relativeLayout.addView(view, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                paint.setDither(true);
                paint.setColor(ContextCompat.getColor(farmInterface.this, R.color.colorDraw));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(4);

                plotMatrix.createMatrix(displayWidth,displayHeight);

                if(newFarm){
                    plotMatrix.addPlot();
                }
            }
        });
    }

    public void defineFarmNameAcres(int n){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_define_new_farm);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        EditText fName = (EditText)dialog.findViewById(R.id.newFarm);
        String defaultFarmName = getString(R.string.defaultFarmNamePrefix)+" "+String.valueOf(n);
        if(!newFarm){
            //TODO: add a number after default name
        }
        fName.setText(defaultFarmName);

        EditText fSize = (EditText)dialog.findViewById(R.id.acres);
        fSize.setText(Integer.toString(1));

        Button button = (Button)dialog.findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText fName = (EditText)dialog.findViewById(R.id.newFarm);
                String farmName = fName.getText().toString();
                if(farmName.isEmpty()){
                    Toast.makeText(view.getContext(), R.string.farmNameCannotBeEmptyMessage, Toast.LENGTH_SHORT).show();
                } else {
                    //TODO: check if farm name is repeated
                    EditText fSize = (EditText)dialog.findViewById(R.id.acres);
                    int farmSize = Integer.parseInt(fSize.getText().toString());
                    if(farmSize<=0){
                        Toast.makeText(view.getContext(), R.string.farmSizeMustBeAboveZero, Toast.LENGTH_SHORT).show();
                    } else {
                        updateFarmData(farmName,farmSize);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

    public void updateFarmData(String fName, int fSize){
        prefs.appendIfNewValue(user+"_farms",fName,",");
        //TODO: update log: create farm

        this.setTitle(this.getTitle()+ ": " + fName);
    }

    class SketchSheetView extends View {

        Context context;

        public SketchSheetView(Context c, int w, int h) {

            super(c);
            context=c;
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            canvas = new Canvas(bitmap);
            this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                plotMatrix.passEvent(event);
            }

            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Iterator<oPlot> iterator = plotMatrix.getPlots().iterator();
            while (iterator.hasNext()) {
                oPlot plot = iterator.next();
                drawPlot(canvas, plot.x, plot.y, plot.w, plot.h, ContextCompat.getColor(context, R.color.colorDraw), ContextCompat.getColor(context, R.color.colorFillDefault));
            }
        }

        private void drawPlot(Canvas canvas, int x, int y, float w, float h, int border, int fill){
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fill);
            canvas.drawRect(x,y,x+w,y+h,paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(border);
            canvas.drawRect(x,y,x+w,y+h,paint);
            canvas.drawBitmap(iconMove,(x+(w/2))-(iconMove.getWidth()/2),(y+(h/2)-iconMove.getHeight()/2),paint);
            canvas.drawBitmap(iconResize,(w+x)-iconResize.getWidth()-2,(h+y)-iconResize.getHeight()-2,paint);
        }
    }
}
