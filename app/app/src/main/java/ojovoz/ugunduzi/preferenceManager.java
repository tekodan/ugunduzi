package ojovoz.ugunduzi;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Eugenio on 08/03/2018.
 */
public class preferenceManager {

    Context context;

    preferenceManager(Context c){
        context=c;
    }

    public String getPreference(String keyName) {
        String value = "";
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        value = ugunduziPrefs.getString(keyName, "");
        return value;
    }

    public boolean preferenceExists(String keyName){
        boolean ret=false;
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        ret = ugunduziPrefs.contains(keyName);
        return ret;
    }

    public boolean getPreferenceBoolean(String keyName) {
        boolean value;
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        value = ugunduziPrefs.getBoolean(keyName, false);
        return value;
    }

    public void savePreference(String keyName, String keyValue) {
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putString(keyName, keyValue);
        prefEditor.commit();
    }

    public void savePreferenceBoolean(String keyName, boolean keyValue){
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putBoolean(keyName, keyValue);
        prefEditor.commit();
    }

    public void savePreferenceInt(String keyName, int keyValue){
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putInt(keyName, keyValue);
        prefEditor.commit();
    }

    public ArrayList<String> getArrayListPreference(String keyName){
        ArrayList<String> ret = new ArrayList<>();
        String allValues=getPreference(keyName);
        if(!allValues.isEmpty()) {
            String valuesArray[] = allValues.split("\\*");
            if(valuesArray.length>0){
                for(int i=0;i<valuesArray.length;i++){
                    ret.add(valuesArray[i]);
                }
            } else {
                ret.add(allValues);
            }
        }
        return ret;
    }

    public void appendIfNotExists(String keyName, String value){
        String previousValue = getPreference(keyName);
        if(!previousValue.isEmpty()) {
            String previousValues[] = previousValue.split("\\*");
            if(previousValues.length>0){
                boolean bFound=false;
                for(int i=0;i<previousValues.length;i++){
                    if(previousValues[i].equals(value)){
                        bFound=true;
                        break;
                    }
                }
                if(!bFound){
                    String newValue = previousValue+"*"+value;
                    savePreference(keyName,newValue);
                }
            } else {
                savePreference(keyName, value);
            }
        } else {
            savePreference(keyName, value);
        }
    }
}