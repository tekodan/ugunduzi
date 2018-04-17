package ojovoz.ugunduzi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eugenio on 16/04/2018.
 */
public class enterData extends AppCompatActivity {

    public String user;
    public String userPass;
    public int userId;
    public String farmName;
    public int plot;

    public oCrop crop1;
    public oCrop crop2;
    public oTreatment treatment1;
    public oTreatment treatment2;

    public Date dataItemDate;

    public ArrayList<oLog> plotLog;

    boolean bChanges = false;
    private dateHelper dH;

    ArrayList<oDataItem> dataItemsList;
    public CharSequence dataItemsNamesArray[];
    public oDataItem chosenDataItem;

    ArrayList<oUnit> unitsList;
    public CharSequence unitsNamesArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        dH = new dateHelper();

        oCrop seed = new oCrop(this);
        oTreatment action = new oTreatment(this);
        oLog log = new oLog(this);

        user = getIntent().getExtras().getString("user");
        userPass = getIntent().getExtras().getString("userPass");
        userId = getIntent().getExtras().getInt("userId");
        farmName = getIntent().getExtras().getString("farmName");
        plot = getIntent().getExtras().getInt("plot");

        crop1 = (getIntent().getExtras().getInt("crop1") > 0) ? seed.getCropFromId(getIntent().getExtras().getInt("crop1")) : null;
        crop2 = (getIntent().getExtras().getInt("crop2") > 0) ? seed.getCropFromId(getIntent().getExtras().getInt("crop2")) : null;

        treatment1 = (getIntent().getExtras().getInt("treatment1") > 0) ? action.getTreatmentFromId(getIntent().getExtras().getInt("treatment1")) : null;
        treatment2 = (getIntent().getExtras().getInt("treatment2") > 0) ? action.getTreatmentFromId(getIntent().getExtras().getInt("treatment2")) : null;

        TextView tt = (TextView) findViewById(R.id.plotLabel);
        String title = "";

        if (crop1 == null && crop2 == null) {
            title = getString(R.string.plotCropLabel) + "s: " + getString(R.string.textNone);
        } else {
            if (crop1 != null && crop2 == null) {
                title = getString(R.string.plotCropLabel) + ": " + crop1.name;
            } else if (crop1 != null && crop2 != null) {
                title = getString(R.string.plotCropLabel) + "s: " + crop1.name + ", " + crop2.name;
            }
        }
        title += "\n";
        if (treatment1 == null && treatment2 == null) {
            title += getString(R.string.plotTreatmentLabel) + "s: " + getString(R.string.textNone);
            tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillDefault));
        } else {
            if (treatment1 != null && treatment2 == null) {
                title += getString(R.string.plotTreatmentLabel) + ": " + treatment1.name;
                if (treatment1.category == 0) {
                    tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillPestControl));
                } else {
                    tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillSoilManagement));
                }
            } else if (treatment1 != null && treatment2 != null) {
                title += getString(R.string.plotTreatmentLabel) + "s: " + treatment1.name + ", " + treatment2.name;
                if (treatment1.category != treatment2.category) {
                    tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillSoilManagementAndPestControl));
                } else {
                    if (treatment1.category == 0) {
                        tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillPestControl));
                    } else {
                        tt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillSoilManagement));
                    }
                }
            }
        }
        tt.setText(title);

        dataItemDate = new Date();

        oDataItem d = new oDataItem(this);
        boolean bExcludeCropSpecific = (crop1==null && crop2==null) ? true : false;
        boolean bExcludeTreatmentSpecific = (treatment1==null && treatment1==null) ? true : false;
        dataItemsList = d.getDataItems(bExcludeCropSpecific, bExcludeTreatmentSpecific);
        dataItemsNamesArray = d.getDataItemNames(bExcludeCropSpecific, bExcludeTreatmentSpecific).toArray(new CharSequence[dataItemsList.size()]);

        plotLog = log.createLog(farmName, plot);
        if (plotLog.size() == 0) {
            TextView tv = (TextView) findViewById(R.id.previousDataItems);
            tv.setVisibility(View.GONE);
            TableLayout tl = (TableLayout) findViewById(R.id.dataItems);
            tl.setVisibility(View.GONE);
        } else {
            fillTable();
        }
    }

    @Override
    public void onBackPressed() {
        tryGoBack();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.opGoBack);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                tryGoBack();
        }
        return super.onOptionsItemSelected(item);
    }

    public void tryGoBack(){
        if(bChanges) {
            confirmExit();
        } else {
            goBack();
        }
    }

    public void confirmExit() {
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setMessage(R.string.dataNotSavedText);
        logoutDialog.setNegativeButton(R.string.noButtonText, null);
        logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                goBack();

            }
        });
        logoutDialog.create();
        logoutDialog.show();
    }

    public void goBack(){
        Intent i = new Intent(this, farmInterface.class);
        i.putExtra("user", user);
        i.putExtra("userId", userId);
        i.putExtra("userPass", userPass);
        i.putExtra("farmName", farmName);
        i.putExtra("newFarm", false);
        i.putExtra("firstFarm", false);
        startActivity(i);
        finish();
    }

    public void showDataItemsSelector(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ListAdapter adapter = new ArrayAdapter<>(this,R.layout.checked_list_template,dataItemsNamesArray);
        builder.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i>=0) {
                    String chosenDataItem = dataItemsNamesArray[i].toString();
                    Button b = (Button)findViewById(R.id.enterDataButton);
                    b.setText(chosenDataItem);
                    showFields(i);
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialogDataItems = builder.create();
        dialogDataItems.show();
    }

    public void showFields(int i){
        chosenDataItem = dataItemsList.get(i);
        Button bc = (Button)findViewById(R.id.enterCropButton);
        Button bt = (Button)findViewById(R.id.enterTreatmentButton);
        TextView td = (TextView)findViewById(R.id.enterDateText);
        Button bd = (Button)findViewById(R.id.dateButton);
        TextView tv = (TextView)findViewById(R.id.enterValueText);
        EditText ev = (EditText)findViewById(R.id.dataItemValue);
        TextView tu = (TextView)findViewById(R.id.enterUnitsText);
        Button bu = (Button)findViewById(R.id.dataItemUnits);
        Button bs = (Button)findViewById(R.id.saveButton);

        if(chosenDataItem.isCropSpecific && (crop1!=null && crop2!=null)){
            bc.setVisibility(View.VISIBLE);
        } else {
            bc.setVisibility(View.GONE);
        }

        if(chosenDataItem.isTreatmentSpecific && (treatment1!=null && treatment2!=null)){
            bt.setVisibility(View.VISIBLE);
        } else {
            bt.setVisibility(View.GONE);
        }

        td.setVisibility(View.VISIBLE);
        bd.setVisibility(View.VISIBLE);
        bd.setText(dH.dateToString(dataItemDate));

        switch(chosenDataItem.type){
            case 0:
                //number
                tv.setVisibility(View.VISIBLE);
                ev.setVisibility(View.VISIBLE);
                tu.setVisibility(View.VISIBLE);

                prepareUnits(bu,tu,tv);

                break;
            case 1:
                //date
                tv.setVisibility(View.GONE);
                ev.setVisibility(View.GONE);
                tu.setVisibility(View.GONE);
                bu.setVisibility(View.GONE);
                break;
            case 2:
                //cost
                tv.setVisibility(View.VISIBLE);
                ev.setVisibility(View.VISIBLE);
                tu.setVisibility(View.VISIBLE);
                bu.setVisibility(View.VISIBLE);

                prepareUnits(bu,tu,tv);
        }
        bs.setVisibility(View.VISIBLE);
    }

    public void prepareUnits(Button bu, TextView tu, TextView tv){
        oUnit u = new oUnit(this);
        unitsList = u.getUnits(chosenDataItem.type);
        if(unitsList.size()==1) {
            String units = unitsList.get(0).name;
            tv.setText(tu.getText()+" ("+units+")");
            tu.setVisibility(View.GONE);
            bu.setVisibility(View.GONE);
        } else {
            tu.setVisibility(View.VISIBLE);
            bu.setVisibility(View.VISIBLE);
            unitsNamesArray = u.getUnitNames(chosenDataItem.type).toArray(new CharSequence[unitsList.size()]);
            bu.setText(chosenDataItem.defaultUnits.name);
            tv.setText(R.string.valueLabel);
        }
    }

    public void displayDatePicker(View v){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_datepicker);

        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
        Calendar calActivity = Calendar.getInstance();
        calActivity.setTime(dataItemDate);
        dp.init(calActivity.get(Calendar.YEAR), calActivity.get(Calendar.MONTH), calActivity.get(Calendar.DAY_OF_MONTH), null);

        Calendar calMax = Calendar.getInstance();
        calMax.setTime(new Date());

        dp.setMaxDate(calMax.getTimeInMillis());

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                int day = dp.getDayOfMonth();
                int month = dp.getMonth();
                int year = dp.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                dataItemDate = calendar.getTime();

                Button cb = (Button) findViewById(R.id.dateButton);
                cb.setText(dH.dateToString(dataItemDate));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showUnitsSelector(View v){

        if(unitsList.size()>1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final ListAdapter adapter = new ArrayAdapter<>(this, R.layout.checked_list_template, unitsNamesArray);
            builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i >= 0) {
                        String chosenUnits = unitsNamesArray[i].toString();
                        Button b = (Button) findViewById(R.id.dataItemUnits);
                        b.setText(chosenUnits);
                    }
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialogUnits = builder.create();
            dialogUnits.show();
        }
    }

    public void showCropSelector(View v){
        ArrayList<String> cropNames = new ArrayList<>();
        cropNames.add(crop1.name);
        cropNames.add(crop2.name);
        CharSequence cropNamesArray[] = cropNames.toArray(new CharSequence[2]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ListAdapter adapter = new ArrayAdapter<>(this, R.layout.checked_list_template, cropNamesArray);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenCropName="";
                switch(i){
                    case 0:
                        chosenCropName=crop1.name;
                        break;
                    case 1:
                        chosenCropName=crop2.name;
                }
                Button b = (Button) findViewById(R.id.enterCropButton);
                b.setText(chosenCropName);

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialogCrops = builder.create();
        dialogCrops.show();
    }

    public void showTreatmentSelector(View v){
        ArrayList<String> treatmentNames = new ArrayList<>();
        treatmentNames.add(treatment1.name);
        treatmentNames.add(treatment2.name);
        CharSequence treatmentNamesArray[] = treatmentNames.toArray(new CharSequence[2]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancelButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ListAdapter adapter = new ArrayAdapter<>(this, R.layout.checked_list_template, treatmentNamesArray);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenTreatmentName="";
                switch(i){
                    case 0:
                        chosenTreatmentName=treatment1.name;
                        break;
                    case 1:
                        chosenTreatmentName=treatment2.name;
                }
                Button b = (Button) findViewById(R.id.enterTreatmentButton);
                b.setText(chosenTreatmentName);

                dialogInterface.dismiss();
            }
        });
        AlertDialog dialogTreatments = builder.create();
        dialogTreatments.show();
    }

    public void fillTable() {

    }

    public void saveData(View v){

    }
}
