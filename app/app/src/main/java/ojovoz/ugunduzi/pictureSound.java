package ojovoz.ugunduzi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Eugenio on 19/04/2018.
 */
public class pictureSound extends AppCompatActivity {

    public String user;
    public String userPass;
    public int userId;
    public String farmName;
    public int plot;

    boolean bChanges=false;

    oCrop crop1;
    oCrop crop2;
    oTreatment treatment1;
    oTreatment treatment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_sound);

        crop1 = new oCrop(this);
        crop2 = new oCrop(this);
        treatment1 = new oTreatment(this);
        treatment2 = new oTreatment(this);

        user = getIntent().getExtras().getString("user");
        userPass = getIntent().getExtras().getString("userPass");
        userId = getIntent().getExtras().getInt("userId");
        farmName = getIntent().getExtras().getString("farmName");
        plot = getIntent().getExtras().getInt("plot");

        crop1 = (getIntent().getExtras().getInt("crop1") > 0) ? crop1.getCropFromId(getIntent().getExtras().getInt("crop1")) : null;
        crop2 = (getIntent().getExtras().getInt("crop2") > 0) ? crop2.getCropFromId(getIntent().getExtras().getInt("crop2")) : null;

        treatment1 = (getIntent().getExtras().getInt("treatment1") > 0) ? treatment1.getTreatmentFromId(getIntent().getExtras().getInt("treatment1")) : null;
        treatment2 = (getIntent().getExtras().getInt("treatment2") > 0) ? treatment2.getTreatmentFromId(getIntent().getExtras().getInt("treatment2")) : null;

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
    }

    @Override
    public void onBackPressed() {
        tryExit(2);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.opEnterData);
        menu.add(1, 1, 1, R.string.opManagePlotRecords);
        menu.add(2, 2, 2, R.string.opGoBack);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        tryExit(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public void tryExit(int exitAction){
        if(bChanges) {
            confirmExit(exitAction);
        } else {
            switch (exitAction) {
                case 0:
                    goToEnterData();
                    break;
                case 1:
                    goToManageRecords();
                    break;
                case 2:
                    goBack();
            }
        }
    }

    public void confirmExit(int e) {
        final int exitAction = e;
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setMessage(R.string.pictureSoundNotSavedText);
        logoutDialog.setNegativeButton(R.string.noButtonText, null);
        logoutDialog.setPositiveButton(R.string.yesButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (exitAction) {
                    case 0:
                        goToEnterData();
                        break;
                    case 1:
                        goToManageRecords();
                        break;
                    case 2:
                        goBack();
                }

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

    public void goToEnterData(){
        Intent i = new Intent(this, enterData.class);
        i.putExtra("user", user);
        i.putExtra("userId", userId);
        i.putExtra("userPass", userPass);
        i.putExtra("farmName", farmName);
        i.putExtra("plot", plot);
        if(crop1!=null) {
            i.putExtra("crop1", crop1.id);
        } else {
            i.putExtra("crop1", "-1");
        }
        if(crop2!=null) {
            i.putExtra("crop2", crop2.id);
        } else {
            i.putExtra("crop2", "-1");
        }
        if(treatment1!=null) {
            i.putExtra("treatment1", treatment1.id);
        } else {
            i.putExtra("treatment1", "-1");
        }
        if(treatment2!=null) {
            i.putExtra("treatment2", treatment2.id);
        } else {
            i.putExtra("treatment2", "-1");
        }
        startActivity(i);
        finish();
    }

    void goToManageRecords(){

    }

    public void startCamera(View v){

    }

    public void showRecorder(View v){

    }

    public void saveMessage(View v){

    }

}


