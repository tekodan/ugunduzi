package ojovoz.ugunduzi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
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
    View canvasView;
    Bitmap bitmap;
    Canvas canvas;

    int displayWidth;
    int displayHeight;
    Bitmap iconMove;
    Bitmap iconMoveFaded;
    Bitmap iconResize;
    Bitmap iconResizeFaded;
    Bitmap iconContents;
    Bitmap iconContentsFaded;

    String user;
    int userId;
    boolean newFarm;
    boolean bFarmSaved;

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
            bFarmSaved=false;
            this.setTitle(R.string.drawNewFarmTitle);
            defineFarmNameAcres(1);
        }

        prefs = new preferenceManager(this);

        iconMove=BitmapFactory.decodeResource(this.getResources(),R.drawable.move);
        iconResize=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize);
        iconContents=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents);
        iconMoveFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.move_faded);
        iconResizeFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize_faded);
        iconContentsFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents_faded);


        plotMatrix = new oPlotMatrix();

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
                canvasView = new SketchSheetView(farmInterface.this,displayWidth,displayHeight);
                paint = new Paint();
                relativeLayout.addView(canvasView, new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                paint.setDither(true);
                paint.setColor(ContextCompat.getColor(farmInterface.this, R.color.colorDraw));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(4);

                plotMatrix.createMatrix(displayWidth,displayHeight);

                if(newFarm){
                    plotMatrix.addPlot(iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.opAddPlot);
        menu.add(1, 1, 1, R.string.opDeletePlot);
        menu.add(2, 2, 2, R.string.opSaveFarm);
        menu.add(3, 3, 3, R.string.opSwitchUser);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                addPlot();
                canvasView.invalidate();
                break;
            case 1:
                deleteSelectedPlot();
                canvasView.invalidate();
                break;
            case 2:
                //
                break;
            case 3:
                confirmExit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addPlot(){
        if(!plotMatrix.addPlot(iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight())){
            Toast.makeText(this, R.string.noSpaceForNewPlotMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSelectedPlot(){
        if(plotMatrix.currentPlot!=null){
            AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
            logoutDialog.setMessage(R.string.deletePlotConfirmMessage);
            logoutDialog.setNegativeButton(R.string.noButtonText,null);
            logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!plotMatrix.deletePlot()){
                        Toast.makeText(farmInterface.this, R.string.farmMustHavePlotMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        canvasView.invalidate();
                    }
                }
            });
            logoutDialog.create();
            logoutDialog.show();
        } else {
            Toast.makeText(this, R.string.selectPlotMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void confirmExit(){
        String msg;
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        if(!bFarmSaved){
            msg=getString(R.string.farmHasNotBeenSavedMessage) + " " + getString(R.string.logoutConfirmMessage);
        } else {
            msg=getString(R.string.logoutConfirmMessage);
        }
        logoutDialog.setMessage(msg);
        logoutDialog.setNegativeButton(R.string.noButtonText,null);
        logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToLogin();
            }
        });
        logoutDialog.create();
        logoutDialog.show();
    }

    public void goToLogin(){
        prefs.savePreference("user","");
        final Context context = this;
        Intent i = new Intent(context, login.class);
        startActivity(i);
        finish();
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

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE || event.getActionMasked() == MotionEvent.ACTION_UP) {
                invalidate();
                return plotMatrix.passEvent(event);
            } else {
                invalidate();
                return true;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Iterator<oPlot> iterator = plotMatrix.getPlots().iterator();
            while (iterator.hasNext()) {
                oPlot plot = iterator.next();
                if(plot==plotMatrix.currentPlot) {
                    drawPlot(canvas, plot, ContextCompat.getColor(context, R.color.colorDraw), ContextCompat.getColor(context, R.color.colorFillDefault), iconMove, iconResize, iconContents);
                } else {
                    drawPlot(canvas, plot, ContextCompat.getColor(context, R.color.colorDrawFaded), ContextCompat.getColor(context, R.color.colorFillFaded), iconMoveFaded, iconResizeFaded, iconContentsFaded);
                }
            }

            if(plotMatrix.ghostPlot !=null){
                drawGhostRectangle(canvas, plotMatrix.ghostPlot, ContextCompat.getColor(context, R.color.colorDrawGhostRectangle));
            }
        }

        private void drawPlot(Canvas canvas, oPlot p, int border, int fill, Bitmap iMove, Bitmap iResize, Bitmap iContents){
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fill);
            canvas.drawRect(p.x,p.y,p.x+p.w,p.y+p.h,paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(border);
            canvas.drawRect(p.x,p.y,p.x+p.w,p.y+p.h,paint);
            canvas.drawBitmap(iMove,p.iMoveX,p.iMoveY,paint);
            canvas.drawBitmap(iResize,p.iResizeX,p.iResizeY,paint);
            canvas.drawBitmap(iContents,p.iContentsX,p.iContentsY,paint);
        }

        private void drawGhostRectangle(Canvas canvas, oPlot gR, int border){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(border);
            canvas.drawRect(gR.x,gR.y,gR.x+gR.w+1,gR.y+gR.h+1,paint);
        }
    }
}
