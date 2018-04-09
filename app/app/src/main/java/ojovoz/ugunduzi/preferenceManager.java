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

    public int getPreferenceInt(String keyName) {
        int value = -1;
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        value = ugunduziPrefs.getInt(keyName, -1);
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

    public ArrayList<String> getPreferenceAsArrayList(String keyName, String separator, String prefixExcluded) {
        ArrayList<String> ret = new ArrayList<>();
        String list = getPreference(keyName);
        String valuesArray[] = list.split(separator);
        for(int i=0;i<valuesArray.length;i++) {
            if(!prefixExcluded.isEmpty()){
                if(prefixExcluded.charAt(0)!=valuesArray[i].charAt(0)){
                    ret.add(valuesArray[i]);
                }
            } else {
                ret.add(valuesArray[i]);
            }
        }
        return ret;
    }

    public String getFarmDate(String keyName, String separator){
        String ret="";
        ArrayList<String> farmList = getPreferenceAsArrayList(keyName,separator,"");
        if(farmList!=null){
            if(farmList.size()>1){
                ret=farmList.get(1);
            }
        }
        return ret;
    }

    public void savePreference(String keyName, String keyValue) {
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putString(keyName, keyValue);
        prefEditor.apply();
    }

    public void savePreferenceBoolean(String keyName, boolean keyValue){
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putBoolean(keyName, keyValue);
        prefEditor.apply();
    }

    public void savePreferenceInt(String keyName, int keyValue){
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.putInt(keyName, keyValue);
        prefEditor.apply();
    }

    public boolean valueExistsInList(String keyName, String value, String separator){
        boolean ret=false;
        String allValues=getPreference(keyName);
        if(!allValues.isEmpty()) {
            String valuesArray[] = allValues.split(separator);
            for(int i=0;i<valuesArray.length;i++) {
                if(valuesArray[i].equals(value)){
                    ret=true;
                    break;
                }
            }
        }
        return ret;
    }

    public void appendIfNewValue(String keyName, String value, String separator){
        String allValues=getPreference(keyName);
        if(!allValues.isEmpty()) {
            if(!valueExistsInList(keyName, value, separator)){
                String newValues = allValues + separator + value;
                savePreference(keyName, newValues);
            }
        } else {
            savePreference(keyName, value);
        }
    }

    public int getNumberOfValueItems(String keyName, String separator){
        int ret=0;
        String allValues=getPreference(keyName);
        if(!allValues.isEmpty()) {
            ret = allValues.split(separator).length;
        }
        return ret;
    }

    public boolean farmExists(String keyName, String value, String separator){
        boolean ret=false;
        if(valueExistsInList(keyName,value,separator) || valueExistsInList(keyName,"*"+value,separator)){
            ret=true;
        }
        return ret;
    }

    public void deletePreference(String keyName){
        SharedPreferences ugunduziPrefs = context.getSharedPreferences("ugunduziPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = ugunduziPrefs.edit();
        prefEditor.remove(keyName);
        prefEditor.apply();
    }

    public void markFarmsAsDeleted(String user, String farmsCSV, String separator){
        String[] deleteFarmList = farmsCSV.split(";");
        String[] currentFarmList = getPreference(user + "_farms").split(separator);
        String newFarms="";
        for(int i=0;i<currentFarmList.length;i++) {
            boolean bFound=false;
            for(int j=0;j<deleteFarmList.length;j++){
                String deleteFarm = deleteFarmList[j].replaceAll("_"," ");
                if(currentFarmList[i].equals(deleteFarm)){
                    bFound=true;
                    break;
                }
            }
            if(!bFound){
                newFarms = (newFarms.isEmpty()) ? currentFarmList[i] : newFarms + separator + currentFarmList[i];
            } else {
                newFarms = (newFarms.isEmpty()) ? "-" + currentFarmList[i] : newFarms + separator + "-" + currentFarmList[i];
                deletePreference(user + "_" + currentFarmList[i].replaceAll(" ","_"));
            }
        }
        savePreference(user + "_farms", newFarms);
    }

    public void deleteFarms(String user, String farmsCSV, String separator){
        String[] deleteFarmList = farmsCSV.split(";");
        String[] currentFarmList = getPreference(user + "_farms").split(separator);
        String newFarms="";
        for(int i=0;i<currentFarmList.length;i++) {
            boolean bFound=false;
            for(int j=0;j<deleteFarmList.length;j++){
                String deleteFarm = deleteFarmList[j].replaceAll("_"," ");
                if(currentFarmList[i].equals(deleteFarm)){
                    bFound=true;
                    break;
                }
            }
            if(!bFound){
                newFarms = (newFarms.isEmpty()) ? currentFarmList[i] : newFarms + separator + currentFarmList[i];
            } else {
                deletePreference(user + "_" + currentFarmList[i].replaceAll(" ","_"));
            }
        }
        savePreference(user + "_farms", newFarms);
    }
}