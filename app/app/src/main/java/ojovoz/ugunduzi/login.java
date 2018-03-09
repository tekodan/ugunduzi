package ojovoz.ugunduzi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class login extends AppCompatActivity implements httpConnection.AsyncResponse {

    public String server = "";
    public String user = "";
    private promptDialog dlg = null;
    private preferenceManager prefs;

    private String uAS;
    private String uPS;

    private ArrayList<String> dataItems;
    private int index;
    private ProgressDialog dialog;
    private int uploadIncrement = 1;
    private boolean bConnecting = false;
    private int connectionTask;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = new preferenceManager(this);
        server = prefs.getPreference("server");
        if (server.equals("")) {
            defineServer("");
        }

        user = prefs.getPreference("user");
        if (!user.equals("")) {
            //TODO start next activity
        } else {
            updateAutocomplete();
        }
    }

    public void defineServer(String current) {
        dlg = new promptDialog(this, R.string.emptyString, R.string.defineServerLabel, current) {
            @Override
            public boolean onOkClicked(String input) {
                if (!input.startsWith("http://")) {
                    input = "http://" + input;
                }
                login.this.server = input;
                prefs.savePreference("server", input);
                downloadData();
                return true;
            }
        };
        dlg.show();
    }

    private void createNewUser(String uAS, String uPS) {
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            CharSequence dialogTitle = getString(R.string.createNewUserLabel);

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
            doCreateNewUser(uAS, uPS);
        }
    }

    public void downloadData() {
        dataItems = new ArrayList<>();
        dataItems.add("users");

        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            index = 0;
            CharSequence dialogTitle = getString(R.string.downloadDataProgressDialogTitle) + " " + dataItems.get(index);

            dialog = new ProgressDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(dialogTitle);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(dataItems.size() - 1);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface d) {
                    bConnecting = false;
                }
            });
            doDownload();
        } else {
            Toast.makeText(this, R.string.pleaseConnectMessage, Toast.LENGTH_SHORT).show();
            bConnecting = false;
        }
    }

    private void doDownload() {
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            if (!bConnecting) {
                bConnecting = true;
                connectionTask = 0;
                http.execute(server + "/mobile/get_" + dataItems.get(index) + ".php", "csv");
            }
        } else {
            Toast.makeText(this, R.string.pleaseConnectMessage, Toast.LENGTH_SHORT).show();
            bConnecting = false;
        }
    }

    private void doCreateNewUser(String uAS, String uPS) {
        httpConnection http = new httpConnection(this, this);
        if (http.isOnline()) {
            if (!bConnecting) {
                bConnecting = true;
                connectionTask = 1;
                http.execute(server + "/mobile/create_new_user.php?alias=" + uAS + "&pass=" + uPS, "");
            }
        }
    }

    public void updateAutocomplete() {
        AutoCompleteTextView a = (AutoCompleteTextView) findViewById(R.id.userAlias);
        oUser u = new oUser(this);
        String userList[] = u.getAllUserNames().split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, userList);
        a.setAdapter(adapter);
        a.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    public void validateUser(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        EditText uA = (EditText) findViewById(R.id.userAlias);
        uAS = uA.getText().toString();
        EditText uP = (EditText) findViewById(R.id.userPassword);
        uPS = uP.getText().toString();
        if (!uAS.equals("") && !uPS.equals("")) {
            if (uAS.equals("admin") && uPS.equals("admin")) {
                defineServer(server);
            } else if (uAS.equals("reset") && uPS.equals("reset")) {
                downloadData();
            } else {
                oUser newUser = new oUser(this, uAS, uPS);
                userId = newUser.getUserIdFromAliasPass();
                if (userId > 0) {
                    // -1 = wrong password, 0 = new user, >0 known user
                    prefs.savePreference("user", uAS);
                    prefs.savePreferenceInt("user_id", userId);
                    //TODO download user data?
                    //TODO start next activity
                } else if (userId == 0) {
                    connectionTask = 1;
                    createNewUser(uAS, uPS);
                } else {
                    Toast.makeText(this, R.string.wrongPasswordLabel, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void processFinish(String output) {
        if (TextUtils.isEmpty(output)) {
            bConnecting = false;
            dialog.dismiss();
            Toast.makeText(this, R.string.incorrectServerURLMessage, Toast.LENGTH_SHORT).show();
        } else {
            switch (connectionTask) {
                case 0:
                    bConnecting = false;
                    String[] nextLine;
                    CSVReader reader = new CSVReader(new StringReader(output), ',', '"');
                    deleteCatalog(dataItems.get(index));
                    File file = new File(this.getFilesDir(), dataItems.get(index));
                    try {
                        FileWriter w = new FileWriter(file);
                        CSVWriter writer = new CSVWriter(w, ',', '"');
                        while ((nextLine = reader.readNext()) != null) {
                            writer.writeNext(nextLine);
                        }
                        writer.close();
                        reader.close();
                    } catch (IOException e) {

                    }
                    index++;
                    if (index < dataItems.size()) {
                        progressHandler.sendMessage(progressHandler.obtainMessage());
                        doDownload();
                    } else {
                        bConnecting = false;
                        dialog.dismiss();
                        updateAutocomplete();
                    }
                    break;
                case 1:
                    dialog.dismiss();
                    userId = Integer.parseInt(output);
                    if (userId > 0) {
                        prefs.savePreferenceInt("user_id", userId);
                    } else {
                        prefs.savePreferenceInt("user_id", 0);
                        prefs.savePreference("user_pass", uPS);
                    }
                    prefs.savePreference("user", uAS);
                    oUser newUser = new oUser(this);
                    newUser.addNewUser(userId, uAS, uPS);
                    //TODO start next activity
            }
        }
    }

    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.incrementProgressBy(uploadIncrement);
            CharSequence dialogTitle = getString(R.string.downloadDataProgressDialogTitle) + " " + dataItems.get(index);
            dialog.setMessage(dialogTitle);
            if (dialog.getProgress() == dialog.getMax()) {
                bConnecting = false;
                dialog.dismiss();
            }
        }
    };

    private void deleteCatalog(String filename) {
        this.deleteFile(filename);
    }

}
