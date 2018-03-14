package ojovoz.ugunduzi;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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

    preferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_interface);

        prefs = new preferenceManager(this);

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

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_define_new_farm);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        EditText fName = (EditText)dialog.findViewById(R.id.newFarm);
        String defaultFarmName = getString(R.string.defaultFarmNamePrefix)+" "+user;
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
