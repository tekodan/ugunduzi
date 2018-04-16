package ojovoz.ugunduzi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

    public ArrayList<oLog> plotLog;

    boolean bChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

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

    public void fillTable() {

    }
}
