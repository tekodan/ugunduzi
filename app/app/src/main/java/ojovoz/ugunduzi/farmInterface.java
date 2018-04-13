package ojovoz.ugunduzi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Menu;
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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by Eugenio on 13/03/2018.
 */
public class farmInterface extends AppCompatActivity implements httpConnection.AsyncResponse {

    RelativeLayout relativeLayout;
    Paint paint;
    TextPaint textPaint;
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
    Bitmap iconActions;
    Bitmap iconActionsFaded;
    Bitmap iconActionsActive;

    String user;
    String userPass;
    int userId;
    boolean newFarm;
    boolean firstFarm;
    boolean bFarmSaved;
    String farmName="";
    String prevFarmName="";
    int farmSize;
    String farmDateString;

    int state; //0 = new farm; 1 = actions; 2 = edit farm


    oPlotMatrix plotMatrix;
    String sMatrix;

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
    ProgressDialog createFarmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_interface);

        final Context ctxt=this;

        user=getIntent().getExtras().getString("user");
        userPass=getIntent().getExtras().getString("userPass");
        userId=getIntent().getExtras().getInt("userId");
        newFarm = getIntent().getExtras().getBoolean("newFarm");
        firstFarm = getIntent().getExtras().getBoolean("firstFarm");

        prefs = new preferenceManager(this);
        server = prefs.getPreference("server");

        oCrop seed = new oCrop(this);
        cropList = seed.getCrops();
        cropNamesArray = seed.getCropNames(true).toArray(new CharSequence[cropList.size()]);

        oTreatment start = new oTreatment(this);
        treatmentList = start.getTreatments();
        treatmentNamesArray = start.getTreatmentNames(true).toArray(new CharSequence[treatmentList.size()]);

        if(newFarm){
            bFarmSaved=false;
            state=0;
            this.setTitle(R.string.drawNewFarmTitle);
            int n;
            if(firstFarm){
                n=1;
            } else {
                n=prefs.getNumberOfValueItems(user + "_farms", ";") + 1;
                String fName=getString(R.string.defaultFarmNamePrefix)+" "+String.valueOf(n);
                while(prefs.farmExists(user + "_farms",fName,";")){
                    n++;
                    fName=getString(R.string.defaultFarmNamePrefix)+" "+String.valueOf(n);
                }
            }
            defineFarmNameAcres(n,false);
        } else {
            state=1;
            farmName=getIntent().getExtras().getString("farmName");
            this.setTitle(farmName);
        }

        iconMove=BitmapFactory.decodeResource(this.getResources(),R.drawable.move);
        iconResize=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize);
        iconContents=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents);
        iconActions=BitmapFactory.decodeResource(this.getResources(),R.drawable.actions);
        iconMoveFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.move_faded);
        iconResizeFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize_faded);
        iconContentsFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents_faded);
        iconActionsFaded=BitmapFactory.decodeResource(this.getResources(),R.drawable.actions_faded);
        iconMoveActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.move_active);
        iconResizeActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.resize_active);
        iconContentsActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.contents_active);
        iconActionsActive=BitmapFactory.decodeResource(this.getResources(),R.drawable.actions_active);


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

                textPaint = new TextPaint();
                textPaint.setTextSize(26);
                textPaint.setTextAlign(Paint.Align.LEFT);
                textPaint.setColor(ContextCompat.getColor(ctxt, R.color.colorBlack));
                textPaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

                plotMatrix.createMatrix(displayWidth,displayHeight);

                if(newFarm){
                    plotMatrix.addPlot(iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight(), iconActions.getWidth(), iconActions.getHeight());
                } else if(state==1){
                    plotMatrix.fromString(ctxt,prefs.getPreference(user+"_"+farmName.replaceAll(" ","_")),";",iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight(), iconActions.getWidth(), iconActions.getHeight());
                }
            }
        });
    }

    @Override
    public void onBackPressed () {
        if(!bFarmSaved && state==0) {
            AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
            logoutDialog.setMessage(R.string.farmHasNotBeenSavedMessage);
            logoutDialog.setNegativeButton(R.string.noButtonText,null);
            logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            logoutDialog.create();
            logoutDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        if(state==0) {
            menu.add(0, 0, 0, R.string.opAddPlot);
            menu.add(1, 1, 1, R.string.opDeletePlot);
            menu.add(2, 2, 2, R.string.opEditFarmNameSize);
            menu.add(3, 3, 3, R.string.opSaveFarm);
            if(!firstFarm) {
                menu.add(4, 4, 4, R.string.opCancelNewFarm);
            }
        } else if(state==1){
            menu.add(0, 0, 0, R.string.opCreateNewFarm);
            if(prefs.getNumberOfValueItems(user + "_farms",";")>1) {
                menu.add(1, 1, 1, R.string.opGoToOtherFarm);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(state==0) {
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
                    int n = (newFarm) ? 1 : prefs.getNumberOfValueItems(user + "_farms", ";") + 1;
                    defineFarmNameAcres(n, true);
                    break;
                case 3:
                    saveFarm();
                    break;
                case 4:
                    cancelNewFarm();
            }
        } else if (state==1){
            switch(item.getItemId()){
                case 0:
                    createNewFarm();
                    break;
                case 1:
                    goToFarmChooser();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToFarmChooser(){
        final Context context = this;
        Intent i = new Intent(context, farmChooser.class);
        i.putExtra("user", user);
        i.putExtra("userId", userId);
        i.putExtra("userPass", userPass);
        startActivity(i);
        finish();
    }

    public void createNewFarm(){
        plotMatrix=new oPlotMatrix();
        plotMatrix.createMatrix(displayWidth,displayHeight);
        plotMatrix.addPlot(iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight(), iconActions.getWidth(), iconActions.getHeight());
        state=0;
        bFarmSaved=false;
        canvasView.invalidate();
        prevFarmName=farmName;
        int n = prefs.getNumberOfValueItems(user+"_farms",";");
        farmName = getString(R.string.defaultFarmNamePrefix)+" "+String.valueOf(n+1);
        while(prefs.farmExists(user+"_farms",farmName,";")){
            n++;
            farmName = getString(R.string.defaultFarmNamePrefix)+" "+String.valueOf(n+1);
        }
        defineFarmNameAcres(1,false);
    }

    public void cancelNewFarm(){
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setMessage(R.string.cancelNewFarmMessage);
        logoutDialog.setNegativeButton(R.string.noButtonText,null);
        logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doCancelNewFarm();
            }
        });
        logoutDialog.create();
        logoutDialog.show();
    }

    public void doCancelNewFarm(){
        if(prefs.preferenceExists("farm")){
            farmName=prefs.getPreference("farm");
            if(!farmName.isEmpty()){
                plotMatrix = new oPlotMatrix();
                plotMatrix.createMatrix(displayWidth,displayHeight);
                plotMatrix.fromString(this,prefs.getPreference(user+"_"+farmName.replaceAll(" ","_")),";",iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight(), iconActions.getWidth(), iconActions.getHeight());
                state=1;
                this.setTitle(farmName);
                canvasView.invalidate();
            } else {
                goToFarmChooser();
            }
        } else {
            goToFarmChooser();
        }
    }

    public void addPlot(){
        if(!plotMatrix.addPlot(iconMove.getWidth(), iconMove.getHeight(), iconResize.getWidth(), iconResize.getHeight(), iconContents.getWidth(), iconContents.getHeight(), iconActions.getWidth(), iconActions.getHeight())){
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
                    if(prefs.farmExists(user+"_farms",fName,";")){
                        Toast.makeText(view.getContext(), R.string.farmNameRepeated, Toast.LENGTH_SHORT).show();
                    } else {
                        EditText fSize = (EditText) dialog.findViewById(R.id.acres);
                        int farmSize = Integer.parseInt(fSize.getText().toString());
                        if (farmSize <= 0) {
                            Toast.makeText(view.getContext(), R.string.farmSizeMustBeAboveZero, Toast.LENGTH_SHORT).show();
                        } else {
                            updateFarmData(fName, farmSize);
                            dialog.dismiss();
                        }
                    }
                }
            }
        });

        dialog.show();
    }

    public void showActionChooser(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_action_chooser);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(displayWidth-50,550);

        TextView tvCrop = (TextView)dialog.findViewById(R.id.cropsLabel);
        if(plotMatrix.currentPlot.crop1==null && plotMatrix.currentPlot.crop2==null){
            tvCrop.setText(tvCrop.getText()+"s: "+getString(R.string.textNone));
        } else {
            if(plotMatrix.currentPlot.crop1!=null && plotMatrix.currentPlot.crop2==null){
                tvCrop.setText(tvCrop.getText()+": "+plotMatrix.currentPlot.crop1.name);
            } else if(plotMatrix.currentPlot.crop1!=null && plotMatrix.currentPlot.crop2!=null){
                tvCrop.setText(tvCrop.getText()+"s: "+plotMatrix.currentPlot.crop1.name+", "+plotMatrix.currentPlot.crop2.name);
            }
        }

        TextView tvTreatment = (TextView)dialog.findViewById(R.id.treatmentsLabel);
        if(plotMatrix.currentPlot.treatment1==null && plotMatrix.currentPlot.treatment2==null){
            tvTreatment.setText(tvTreatment.getText()+"s: "+getString(R.string.textNone));
        } else {
            if(plotMatrix.currentPlot.treatment1!=null && plotMatrix.currentPlot.treatment2==null){
                tvTreatment.setText(tvTreatment.getText()+": "+plotMatrix.currentPlot.treatment1.name);
            } else if(plotMatrix.currentPlot.treatment1!=null && plotMatrix.currentPlot.treatment2!=null){
                tvTreatment.setText(tvTreatment.getText()+"s: "+plotMatrix.currentPlot.treatment1.name+", "+plotMatrix.currentPlot.treatment2.name);
            }
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                plotMatrix.currentPlot.state=1;
                canvasView.invalidate();
            }
        });

        dialog.show();
    }

    public void updateFarmData(String fName, int fSize){
        fName = fName.replaceAll(";", " ");
        fName = fName.replaceAll("\\*", "");
        this.setTitle(getString(R.string.drawNewFarmTitle)+ ": " + fName);
        farmName = fName;
        farmSize = fSize;
    }

    public void saveFarm(){

        sMatrix = plotMatrix.toString();

        farmName = farmName.replaceAll("'", "");
        String fName = farmName.replaceAll(" ", "_");

        Date farmDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        farmDateString = sdf.format(farmDate);

        String saveString = user + ";" + userPass + ";" + fName + ";" + String.valueOf(farmSize) + ";" + farmDateString + ";" + sMatrix;
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            CharSequence dialogTitle = getString(R.string.createNewFarmLabel);

            createFarmDialog = new ProgressDialog(this);
            createFarmDialog.setCancelable(true);
            createFarmDialog.setCanceledOnTouchOutside(false);
            createFarmDialog.setMessage(dialogTitle);
            createFarmDialog.setIndeterminate(true);
            createFarmDialog.show();
            createFarmDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface d) {
                    bConnecting = false;
                    createFarmDialog.dismiss();
                }
            });
            doCreateNewFarm(saveString);
        } else {
            prefs.appendIfNewValue(user+"_farms","*"+fName,";");
            prefs.savePreference(user+"_"+fName,String.valueOf(farmSize)+";"+farmDateString+";"+sMatrix);
            prefs.savePreference("farm",farmName);
            Toast.makeText(this, R.string.farmSavedMessage, Toast.LENGTH_SHORT).show();
            state=1;
            canvasView.invalidate();
            firstFarm=false;
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
        bConnecting=false;
        createFarmDialog.dismiss();
        String fName = farmName.replaceAll(" ", "_");
        if(!output.equals("ko")){
            prefs.appendIfNewValue(user+"_farms",farmName,";");
        } else {
            prefs.appendIfNewValue(user+"_farms","*"+farmName,";");
        }
        prefs.savePreference(user+"_"+fName,String.valueOf(farmSize)+";"+farmDateString+";"+sMatrix);
        if(state==0){
            state=1;
            firstFarm=false;
            canvasView.invalidate();
        }
        Toast.makeText(this, R.string.farmSavedMessage, Toast.LENGTH_SHORT).show();
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
                boolean b = plotMatrix.passEvent(event,state);
                if(plotMatrix.currentPlot!=null) {
                    if (plotMatrix.currentPlot.state == 4 && state==0) {
                        definePlotContents();
                    } else if (plotMatrix.currentPlot.state == 5 && state==1){
                        showActionChooser();
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
            int borderColor;

            Bitmap iMove;
            Bitmap iResize;
            Bitmap iContents;
            Bitmap iActions;

            Iterator<oPlot> iterator = plotMatrix.getPlots().iterator();
            while (iterator.hasNext()) {
                oPlot plot = iterator.next();
                fillColor=getFillColor(plot,plot==plotMatrix.currentPlot);
                borderColor= (plot==plotMatrix.currentPlot) ? ContextCompat.getColor(context, R.color.colorDraw) : ContextCompat.getColor(context, R.color.colorDrawFaded);
                if(state==0) {
                    iMove = (plot == plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state == 2) ? iconMoveActive : iconMove : iconMoveFaded;
                    iResize = (plot == plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state == 3) ? iconResizeActive : iconResize : iconResizeFaded;
                    iContents = (plot == plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state == 4) ? iconContentsActive : iconContents : iconContentsFaded;
                    drawPlot(canvas, plot, borderColor, fillColor, iMove, iResize, iContents);
                } else if(state==1){
                    iActions = (plot == plotMatrix.currentPlot) ? (plotMatrix.currentPlot.state == 5) ? iconActionsActive : iconActions : iconActionsFaded;
                    drawPlot(canvas, plot, borderColor, fillColor, iActions);
                }
            }

            if(plotMatrix.ghostPlot !=null && state==0){
                drawGhostRectangle(canvas, plotMatrix.ghostPlot, ContextCompat.getColor(context, R.color.colorDrawGhostRectangle));
            }
        }

        public int getFillColor(oPlot p, boolean strong){
            int ret;
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

        private void drawPlot(Canvas canvas, oPlot p, int border, int fill, Bitmap iActions){
            Rect txtBounds1 = new Rect();
            Rect txtBounds2 = new Rect();
            float txtX;
            float txtY;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fill);
            canvas.drawRect(p.x,p.y,p.x+p.w,p.y+p.h,paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(border);
            canvas.drawRect(p.x,p.y,p.x+p.w,p.y+p.h,paint);
            canvas.drawBitmap(iActions,p.iActionsX,p.iActionsY,paint);

            if(p.crop1!=null) {
                textPaint.getTextBounds(p.crop1.name, 0, p.crop1.name.length(), txtBounds1);
                if (p.crop2 != null) {

                    textPaint.getTextBounds(p.crop2.name, 0, p.crop2.name.length(), txtBounds2);

                    txtX = ((p.w - txtBounds1.width()) / 2) + p.x;
                    txtY = p.y + txtBounds1.height() + ((p.h - ((txtBounds1.height()*2) + txtBounds2.height()))/2);
                    canvas.drawText(p.crop1.name, (int) txtX, (int) txtY, textPaint);

                    txtX = ((p.w - txtBounds2.width()) / 2) + p.x;
                    canvas.drawText(p.crop2.name, (int) txtX, (int) txtY + (txtBounds1.height()*1.5f), textPaint);

                } else {
                    txtX = ((p.w - txtBounds1.width()) / 2) + p.x;
                    txtY = ((p.h - (txtBounds1.height()/2)) / 2) + p.y + (txtBounds1.height()/2);
                    canvas.drawText(p.crop1.name, (int) txtX, (int) txtY, textPaint);
                }
            }
        }

        private void drawGhostRectangle(Canvas canvas, oPlot gR, int border){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(border);
            canvas.drawRect(gR.x,gR.y,gR.x+gR.w+1,gR.y+gR.h+1,paint);
        }
    }
}
