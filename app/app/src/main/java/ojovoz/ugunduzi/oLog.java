package ojovoz.ugunduzi;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Eugenio on 13/04/2018.
 */
public class oLog {

    public String farmName;
    public int userId;

    public int plotId;
    public Date date;
    public oDataItem dataItem;
    public float value;
    public oUnit units;

    public oCrop crop;
    public oTreatment treatment;

    public String picture;
    public String sound;

    private Context context;

    private dateHelper dH;

    oLog(){
        dH = new dateHelper();
    }

    oLog(Context c){
        dH = new dateHelper();
        context=c;
    }

    public ArrayList<oLog> createLog(){
        ArrayList<oLog> ret = new ArrayList<>();
        csvFileManager log;

        log = new csvFileManager("log");
        List<String[]> logCSV = log.read(context);
        if(logCSV!=null) {
            Iterator<String[]> iterator = logCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                oLog l = new oLog();
                l.farmName = record[0];
                l.userId = Integer.parseInt(record[1]);
                l.plotId = Integer.parseInt(record[2]);
                l.date = dH.stringToDate(record[3]);
                oDataItem di = new oDataItem(context);
                l.dataItem = di.getDataItemFromId(Integer.parseInt(record[4]));
                l.value = Float.parseFloat(record[5]);
                oUnit u = new oUnit(context);
                l.units = u.getUnitFromId(Integer.parseInt(record[6]));
                oCrop c = new oCrop(context);
                l.crop = c.getCropFromId(Integer.parseInt(record[7]));
                oTreatment t = new oTreatment(context);
                l.treatment = t.getTreatmentFromId(Integer.parseInt(record[8]));
                l.picture = record[9];
                l.sound = record[10];
                ret.add(l);
            }
        }
        return ret;
    }

    public ArrayList<oLog> createLog(String fName, int userId){
        ArrayList<oLog> ret = new ArrayList<>();
        csvFileManager log;

        log = new csvFileManager("log");
        List<String[]> logCSV = log.read(context);
        if(logCSV!=null) {
            Iterator<String[]> iterator = logCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                if(record[0].equals(fName) && Integer.parseInt(record[1])==userId) {
                    oLog l = new oLog();
                    l.farmName = record[0];
                    l.userId = Integer.parseInt(record[1]);
                    l.plotId = Integer.parseInt(record[2]);
                    l.date = dH.stringToDate(record[3]);
                    oDataItem di = new oDataItem(context);
                    l.dataItem = di.getDataItemFromId(Integer.parseInt(record[4]));
                    l.value = Float.parseFloat(record[5]);
                    oUnit u = new oUnit(context);
                    l.units = u.getUnitFromId(Integer.parseInt(record[6]));
                    oCrop c = new oCrop(context);
                    l.crop = c.getCropFromId(Integer.parseInt(record[7]));
                    oTreatment t = new oTreatment(context);
                    l.treatment = t.getTreatmentFromId(Integer.parseInt(record[8]));
                    l.picture = record[9];
                    l.sound = record[10];
                    ret.add(l);
                }
            }
        }
        return ret;
    }

    public ArrayList<oLog> createLog(String fName, int userId, int plot){
        ArrayList<oLog> ret = new ArrayList<>();
        csvFileManager log;

        log = new csvFileManager("log");
        List<String[]> logCSV = log.read(context);
        if(logCSV!=null) {
            Iterator<String[]> iterator = logCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                if(record[0].equals(fName) && (Integer.parseInt(record[1])==userId) && (Integer.parseInt(record[2])==plot)) {
                    oLog l = new oLog();
                    l.farmName = record[0];
                    l.userId = Integer.parseInt(record[1]);
                    l.plotId = Integer.parseInt(record[2]);
                    l.date = dH.stringToDate(record[3]);
                    oDataItem di = new oDataItem(context);
                    l.dataItem = di.getDataItemFromId(Integer.parseInt(record[4]));
                    l.value = Float.parseFloat(record[5]);
                    oUnit u = new oUnit(context);
                    l.units = u.getUnitFromId(Integer.parseInt(record[6]));
                    oCrop c = new oCrop(context);
                    l.crop = c.getCropFromId(Integer.parseInt(record[7]));
                    oTreatment t = new oTreatment(context);
                    l.treatment = t.getTreatmentFromId(Integer.parseInt(record[8]));
                    l.picture = record[9];
                    l.sound = record[10];
                    ret.add(l);
                }
            }
        }
        return ret;
    }

    public void appendToLog(String farmName, int userId, int plot, Date date, oDataItem dataItem, float value, oUnit units, oCrop crop, oTreatment treatment, String picture, String sound){
        dateHelper dH = new dateHelper();

        csvFileManager log = new csvFileManager("log");
        String dataItemId;
        String unitsId;
        String cropId;
        String treatmentId;
        dataItemId = (dataItem == null) ? "0" : Integer.toString(dataItem.id);
        unitsId = (units == null) ? "0" : Integer.toString(units.id);
        cropId = (crop == null) ? "0" : Integer.toString(crop.id);
        treatmentId = (treatment == null) ? "0" : Integer.toString(treatment.id);
        String[] newLine = {farmName, Integer.toString(userId), Integer.toString(plot), dH.dateToString(date), dataItemId, Float.toString(value), unitsId, cropId, treatmentId, picture, sound};

        log.append(context, newLine);
    }

    public ArrayList<oLog> sortLogByDate(ArrayList<oLog> sortedLog, boolean reverse, int limit){
        Collections.sort(sortedLog, new Comparator<oLog>() {
            @Override
            public int compare(oLog l1, oLog l2) {
                return l1.date.compareTo(l2.date);
            }
        });

        if(reverse){
            Collections.reverse(sortedLog);
        }

        if(limit>0 && limit<sortedLog.size()){
            sortedLog.subList(0,limit);
        }

        return sortedLog;
    }

}
