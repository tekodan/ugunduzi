package ojovoz.ugunduzi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Eugenio on 27/03/2018.
 */
public class farmChooser extends AppCompatActivity {

    String user;
    String userPass;
    int userId;
    String server;

    preferenceManager prefs;

    ArrayList<CheckBox> checkboxes;
    ArrayList<String> farmsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_chooser);

        user = getIntent().getExtras().getString("user");
        userPass = getIntent().getExtras().getString("userPass");
        userId = getIntent().getExtras().getInt("userId");

        prefs = new preferenceManager(this);
        server = prefs.getPreference("server");

        prefs.savePreference("farm","");

        fillTable();

    }

    void fillTable() {

        TableLayout farmTable = (TableLayout)findViewById(R.id.chooserTable);
        checkboxes = new ArrayList<>();

        farmsList = prefs.getPreferenceAsArrayList(user+"_farms",";","-");
        if(farmsList!=null){
            int n=0;
            Iterator<String> farmIterator = farmsList.iterator();
            while (farmIterator.hasNext()) {
                String farmName = farmIterator.next();
                farmName.replaceAll("\\*","");
                final TableRow trow = new TableRow(farmChooser.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
                lp.setMargins(10, 10, 0, 10);
                String farmDate = prefs.getFarmDate(user+"_"+farmName.replaceAll(" ","_"),";");
                farmName = farmName + " (" + farmDate + ")";

                if (n % 2 == 0) {
                    trow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFillFaded));
                } else {
                    trow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                }
                CheckBox cb = new CheckBox(farmChooser.this);
                cb.setButtonDrawable(R.drawable.custom_checkbox);
                cb.setId(n);
                cb.setPadding(4, 4, 4, 4);
                cb.setChecked(false);
                checkboxes.add(cb);
                trow.addView(cb, lp);

                TextView tv = new TextView(farmChooser.this);
                tv.setId(n);
                tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);
                tv.setText(farmName);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tv.setPadding(0, 10, 0, 10);
                tv.setMaxWidth(350);
                tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goToFarm(v);
                    }

                });
                trow.addView(tv, lp);

                trow.setGravity(Gravity.CENTER_VERTICAL);
                farmTable.addView(trow, lp);

                n++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.opCreateNewFarm);
        menu.add(1, 1, 1, R.string.opDeleteSelectedFarms);
        menu.add(2, 2, 2, R.string.opSwitchUser);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                goToFarm(null);
                break;
            case 1:
                //TODO delete selected farms
                break;
            case 2:
                confirmExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmExit(){
        AlertDialog.Builder logoutDialog = new AlertDialog.Builder(this);

        logoutDialog.setMessage(getString(R.string.logoutConfirmMessage));
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

    public void goToFarm(View v){
        String fName="";
        boolean newFarm=false;
        if(v!=null) {
            TextView tv = (TextView) v;
            tv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
            int n = v.getId();
            fName = farmsList.get(n);
            prefs.savePreference("farm",fName);
        } else {
            newFarm=true;
        }
        final Context context = this;
        Intent i = new Intent(context, farmInterface.class);
        i.putExtra("user", user);
        i.putExtra("userId", userId);
        i.putExtra("userPass", userPass);
        i.putExtra("newFarm", newFarm);
        i.putExtra("firstFarm",false);
        i.putExtra("farmName", fName);
        startActivity(i);
        finish();
    }

}
