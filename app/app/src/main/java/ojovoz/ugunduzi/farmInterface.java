package ojovoz.ugunduzi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Eugenio on 13/03/2018.
 */
public class farmInterface extends AppCompatActivity implements httpConnection.AsyncResponse {

    RelativeLayout relativeLayout;
    Paint paint;
    View canvasView;
    Bitmap bitmap;
    Canvas canvas;

    int displayWidth;
    int displayHeight;
    Bitmap iconMove;
    Bitmap iconMoveFaded;
    Bitmap iconMoveActive;
    Bitmap iconResize;
    Bitmap iconResizeFaded;
    Bitmap iconResizeActive;
    Bitmap iconContents;
    Bitmap iconContentsFaded;
    Bitmap iconContentsActive;

    String user;
    String userPass;
    int userId;
    boolean newFarm;
    boolean bFarmSaved;
    String farmName="";
    int farmSize;

    oPlotMatrix plotMatrix;

    ArrayList<oCrop> cropList;
    public CharSequence cropNamesArray[];
    int editingCrop;
    oCrop editCrop1;
    oCrop editCrop2;
    oTreatment editTreatment1;
    oTreatment editTreatment2;

    ArrayList<oTreatment> treatmentList;
    public CharSequence treatmentNamesArray[];
    int editingTreatment;

    preferenceManager prefs;

    boolean bConnecting=false;
    String server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_interface);

        user=getIntent().getExtras().getString("user");
        userPass=getIntent().getExtras().getString("userPass");
        userId=getIntent().getExtras().getInt("userId");
        newFarm = getIntent().getExtras().getBoolean("newFarm");

        oCrop seed = new oCrop(this);
        cropList = seed.getCrops();
        cropNamesArray = seed.getCropNames(true).toArray(new CharSequence[cropList.size()]);

        oTreatment start = new oTreatment(this);
        treatmentList = start.getTreatments();
        treatmentNamesArray = start.getTreatmentNames(true).toArray(new CharSequence[treatmentList.size()]);

        if(newFarm){
            bFarmSaved=false;
            this.setTitle(R.string.drawNewFarmTitle);
            int n=1; //TODO: if not single farm, get default farm number
            defineFarmNameAcres(n,false);
        }

        prefs = new preferenceManager(this);
        server = prefs.getPreference("server");

        iconMove=BitmapFactory.decodeResource(this.getResources(),R.drawable.move);
        iconResize=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize);
        iconContents=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents);
        iconMoveFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.move_faded);
        iconResizeFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize_faded);
        iconContentsFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents_faded);
        iconMoveActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.move_active);
        iconResizeActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize_active);
        iconContentsActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents_active);


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
        menu.add(2, 2, 2, R.string.opEditFarmNameSize);
        menu.add(3, 3, 3, R.string.opSaveFarm);
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
                int n=1; //TODO: if not single farm, get default farm number
                defineFarmNameAcres(n,true);
                break;
            case 3:
                saveFarm();
                break;
            case 4:
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

    public void definePlotContents(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_define_plot_contents);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(displayWidth-50,displayHeight-100);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                plotMatrix.currentPlot.state=1;
                editCrop1=null;
                editCrop2=null;
                editTreatment1=null;
                editTreatment2=null;
                dialog.dismiss();
                canvasView.invalidate();
            }
        });

        Button okButton = (Button)dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (v.getId()) {
                    case R.id.okButton:
                        if(editCrop1==null && editCrop2!=null){
                            editCrop1=editCrop2;
                            editCrop2=null;
                        }
                        plotMatrix.currentPlot.crop1 = editCrop1;
                        if(editCrop1==editCrop2){
                            plotMatrix.currentPlot.crop2 = null;
                        } else {
                            plotMatrix.currentPlot.crop2 = editCrop2;
                        }
                        if(editTreatment1==null && editTreatment2!=null){
                            editTreatment1=editTreatment2;
                            editTreatment2=null;
                        }
                        plotMatrix.currentPlot.treatment1=editTreatment1;
                        if(editTreatment1==editTreatment2) {
                            plotMatrix.currentPlot.treatment2 = null;
                        } else {
                            plotMatrix.currentPlot.treatment2 = editTreatment2;
                        }
                        editCrop1=null;
                        editCrop2=null;
                        editTreatment1=null;
                        editTreatment2=null;
                        plotMatrix.currentPlot.state=1;
                        dialog.dismiss();
                        canvasView.invalidate();
                        break;
                    default:
                        break;
                }
            }
        });

        Button crop1 = (Button)dialog.findViewById(R.id.crop1Button);
        if(plotMatrix.currentPlot.crop1!=null){
            crop1.setText(plotMatrix.currentPlot.crop1.name);
            crop1.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
            editCrop1=plotMatrix.currentPlot.crop1;
        }
        crop1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.crop1Button:
                        editingCrop=1;
                        showCropSelector(dialog);
                        break;
                    default:
                        break;
                }
            }
        });

        Button crop2 = (Button)dialog.findViewById(R.id.crop2Button);
        if(plotMatrix.currentPlot.crop2!=null){
            crop2.setText(plotMatrix.currentPlot.crop2.name);
            crop2.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
            editCrop2=plotMatrix.currentPlot.crop2;
        }
        crop2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.crop2Button:
                        editingCrop=2;
                        showCropSelector(dialog);
                        break;
                    default:
                        break;
                }
            }
        });

        Button treatment1 = (Button)dialog.findViewById(R.id.treatment1Button);
        if(plotMatrix.currentPlot.treatment1!=null){
            treatment1.setText(plotMatrix.currentPlot.treatment1.name);
            treatment1.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
            editTreatment1=plotMatrix.currentPlot.treatment1;
        }
        treatment1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.treatment1Button:
                        editingTreatment=1;
                        showTreatmentSelector(dialog);
                        break;
                    default:
                        break;
                }
            }
        });

        Button treatment2 = (Button)dialog.findViewById(R.id.treatment2Button);
        if(plotMatrix.currentPlot.treatment2!=null){
            treatment2.setText(plotMatrix.currentPlot.treatment2.name);
            treatment2.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
            editTreatment2=plotMatrix.currentPlot.treatment2;
        }
        treatment2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.treatment2Button:
                        editingTreatment=2;
                        showTreatmentSelector(dialog);
                        break;
                    default:
                        break;
                }
            }
        });

        dialog.show();
    }

    public void showCropSelector(Dialog d){
        final Dialog dialog = d;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ListAdapter adapter = new ArrayAdapter<>(this,R.layout.checked_list_template,cropNamesArray);
        builder.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i>=0) {
                    String chosenCrop=cropNamesArray[i].toString();
                    Button cropButton;
                    switch(editingCrop){
                        case 1:
                            cropButton = (Button)dialog.findViewById(R.id.crop1Button);
                            cropButton.setText(chosenCrop);
                            if(i>0) {
                                editCrop1 = cropList.get(i-1);
                                cropButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
                            } else {
                                editCrop1 = null;
                                cropButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorWhite));
                            }
                            break;
                        case 2:
                            cropButton = (Button)dialog.findViewById(R.id.crop2Button);
                            cropButton.setText(chosenCrop);
                            if(i>0) {
                                editCrop2 = cropList.get(i-1);
                                cropButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
                            } else {
                                editCrop2 = null;
                                cropButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorWhite));
                            }
                            break;
                    }

                }
                dialogInterface.dismiss();

            }
        });
        AlertDialog dialogCrops = builder.create();
        dialogCrops.show();
    }

    public void showTreatmentSelector(Dialog d){
        final Dialog dialog = d;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ListAdapter adapter = new ArrayAdapter<>(this,R.layout.checked_list_template,treatmentNamesArray);
        builder.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i>=0) {
                    String chosenTreatment=treatmentNamesArray[i].toString();
                    Button treatmentButton;
                    switch(editingTreatment){
                        case 1:
                            treatmentButton = (Button)dialog.findViewById(R.id.treatment1Button);
                            treatmentButton.setText(chosenTreatment);
                            if(i>0) {
                                editTreatment1 = treatmentList.get(i-1);
                                treatmentButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
                            } else {
                                editTreatment1 = null;
                                treatmentButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorWhite));
                            }
                            break;
                        case 2:
                            treatmentButton = (Button)dialog.findViewById(R.id.treatment2Button);
                            treatmentButton.setText(chosenTreatment);
                            if(i>0) {
                                editTreatment2 = treatmentList.get(i-1);
                                treatmentButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorBlack));
                            } else {
                                editTreatment2 = null;
                                treatmentButton.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.colorWhite));
                            }
                            break;
                    }

                }
                dialogInterface.dismiss();

            }
        });
        AlertDialog dialogTreatments = builder.create();
        dialogTreatments.show();
    }

    public void defineFarmNameAcres(int n, boolean cancellable){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_define_new_farm);
        dialog.setCanceledOnTouchOutside(cancellable);
        dialog.setCancelable(cancellable);

        EditText et = (EditText)dialog.findViewById(R.id.newFarm);
        String defaultFarmName="";
        if(!farmName.isEmpty()){
            defaultFarmName = farmName;
        } else {
            defaultFarmName = getString(R.string.defaultFarmNamePrefix) + " " + String.valueOf(n);
        }
        et.setText(defaultFarmName);

        EditText fSize = (EditText)dialog.findViewById(R.id.acres);
        if(farmSize>0){
            fSize.setText(Integer.toString(farmSize));
        } else {
            fSize.setText(Integer.toString(1));
        }

        Button button = (Button)dialog.findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText)dialog.findViewById(R.id.newFarm);
                String fName = et.getText().toString();
                if(fName.isEmpty()){
                    Toast.makeText(view.getContext(), R.string.farmNameCannotBeEmptyMessage, Toast.LENGTH_SHORT).show();
                } else {
                    //TODO: check if farm name is repeated
                    EditText fSize = (EditText)dialog.findViewById(R.id.acres);
                    int farmSize = Integer.parseInt(fSize.getText().toString());
                    if(farmSize<=0){
                        Toast.makeText(view.getContext(), R.string.farmSizeMustBeAboveZero, Toast.LENGTH_SHORT).show();
                    } else {
                        updateFarmData(fName,farmSize);
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

        this.setTitle(getString(R.string.drawNewFarmTitle)+ ": " + fName);
        farmName = fName;
        farmSize = fSize;
    }

    public void saveFarm(){
        ProgressDialog dialog;
        String fName = farmName.replaceAll(" ", "_");
        fName = fName.replaceAll(";", " ");

        String sMatrix = plotMatrix.toString();
        String saveString = user + ";" + userPass + ";" + fName + ";" + String.valueOf(farmSize) + ";" + sMatrix;
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            CharSequence dialogTitle = getString(R.string.createNewFarmLabel);

            dialog = new ProgressDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(dialogTitle);
            dialog.setIndeterminate(true);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface d) {
                    bConnecting = false;
                }
            });
            doCreateNewFarm(saveString);
        }
    }

    public void doCreateNewFarm(String s){
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            if (!bConnecting) {
                bConnecting = true;
                http.execute(server + "/mobile/create_new_farm.php?farm=" + s, "");
            }
        }
    }

    @Override
    public void processFinish(String output) {

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
                boolean b = plotMatrix.passEvent(event);
                if(plotMatrix.currentPlot!=null) {
                    if (plotMatrix.currentPlot.state == 4) {
                        definePlotContents();
                    }
                }
                return b;
            } else {
                invalidate();
                return true;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int fillColor;
            int borderColor=ContextCompat.getColor(context, R.color.colorDraw);

            Bitmap iMove;
            Bitmap iResize;
            Bitmap iContents;

            Iterator<oPlot> iterator = plotMatrix.getPlots().iterator();
            while (iterator.hasNext()) {
                oPlot plot = iterator.next();
                fillColor=getFillColor(plot,plot==plotMatrix.currentPlot);
                borderColor= (plot==plotMatrix.currentPlot) ? ContextCompat.getColor(context, R.color.colorDraw) : ContextCompat.getColor(context, R.color.colorDrawFaded);
                iMove = (plot==plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state==2) ? iconMoveActive : iconMove  : iconMoveFaded;
                iResize = (plot==plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state==3) ? iconResizeActive : iconResize : iconResizeFaded;
                iContents = (plot==plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state==4) ? iconContentsActive : iconContents : iconContentsFaded;
                drawPlot(canvas, plot, borderColor, fillColor, iMove, iResize, iContents);
            }

            if(plotMatrix.ghostPlot !=null){
                drawGhostRectangle(canvas, plotMatrix.ghostPlot, ContextCompat.getColor(context, R.color.colorDrawGhostRectangle));
            }
        }

        public int getFillColor(oPlot p, boolean strong){
            int ret=ContextCompat.getColor(context, R.color.colorFillDefault);
            if(!(p.treatment1==null) && !(p.treatment2==null)){
                if(p.treatment1.category!=p.treatment2.category){
                    ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillSoilManagementAndPestControl) : ContextCompat.getColor(context,R.color.colorFillSoilManagementAndPestControlFaded);
                } else {
                    if(p.treatment1.category==0){
                        ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillPestControl) : ContextCompat.getColor(context,R.color.colorFillPestControlFaded);
                    } else {
                        ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillSoilManagement) : ContextCompat.getColor(context,R.color.colorFillSoilManagementFaded);
                    }
                }
            } else if(!(p.treatment1==null)){
                if(p.treatment1.category==0){
                    ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillPestControl) : ContextCompat.getColor(context,R.color.colorFillPestControlFaded);
                } else {
                    ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillSoilManagement) : ContextCompat.getColor(context,R.color.colorFillSoilManagementFaded);
                }
            } else if(!(p.treatment2==null)){
                if(p.treatment2.category==0){
                    ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillPestControl) : ContextCompat.getColor(context,R.color.colorFillPestControlFaded);
                } else {
                    ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillSoilManagement) : ContextCompat.getColor(context,R.color.colorFillSoilManagementFaded);
                }
            } else {
                ret = (strong) ? ContextCompat.getColor(context,R.color.colorFillDefault) : ContextCompat.getColor(context,R.color.colorFillFaded);
            }
            return ret;
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
