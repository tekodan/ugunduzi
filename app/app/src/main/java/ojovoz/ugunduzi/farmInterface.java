package ojovoz.ugunduzi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;

/**
 * Created by Eugenio on 13/03/2018.
 */
public class farmInterface extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Paint paint;
    View view;
    Bitmap bitmap;
    Canvas canvas;

    String user;
    int userId;
    boolean newFarm;
    String farmData;
    private promptDialog dlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_interface);

        relativeLayout = (RelativeLayout)findViewById(R.id.drawingCanvas);
        view = new SketchSheetView(farmInterface.this);
        paint = new Paint();
        relativeLayout.addView(view, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        paint.setDither(true);
        paint.setColor(ContextCompat.getColor(this, R.color.colorDraw));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2);

        user=getIntent().getExtras().getString("user");
        userId=getIntent().getExtras().getInt("userId");
        newFarm = getIntent().getExtras().getBoolean("newFarm");

        if(newFarm){
            this.setTitle(R.string.drawNewFarmTitle);
            defineFarmNameAcres();
        }
    }

    public void defineFarmNameAcres(){
        dlg = new promptDialog(this, 1, R.string.defineFarmNameAcresLabels, R.string.defineFarmNameAcresTitle, "", "") {
            @Override
            public boolean onOkClicked(String input) {
                farmData=input;
                return true;
            }
        };
        dlg.show();
    }

    class SketchSheetView extends View {

        public SketchSheetView(Context context) {
            super(context);
            bitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_4444);
            canvas = new Canvas(bitmap);
            this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        }

        private ArrayList<DrawingClass> DrawingClassArrayList = new ArrayList<DrawingClass>();

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            DrawingClass pathWithPaint = new DrawingClass();
            //canvas.drawPath(path2, paint);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                path2.moveTo(event.getX(), event.getY());
                path2.lineTo(event.getX(), event.getY());
                */
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                /*
                path2.lineTo(event.getX(), event.getY());
                pathWithPaint.setPath(path2);
                pathWithPaint.setPaint(paint);
                DrawingClassArrayList.add(pathWithPaint);
                */
            }

            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (DrawingClassArrayList.size() > 0) {
                canvas.drawPath(DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPath(),DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPaint());
            }
        }
    }

    public class DrawingClass {

        Path DrawingClassPath;
        Paint DrawingClassPaint;

        public Path getPath() {
            return DrawingClassPath;
        }

        public void setPath(Path path) {
            this.DrawingClassPath = path;
        }

        public Paint getPaint() {
            return DrawingClassPaint;
        }

        public void setPaint(Paint paint) {
            this.DrawingClassPaint = paint;
        }
    }
}
