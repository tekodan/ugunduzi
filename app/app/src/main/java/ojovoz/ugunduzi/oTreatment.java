package ojovoz.ugunduzi;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugenio on 21/03/2018.
 */
public class oTreatment {

    public int id;
    public String name;
    public int category; //0=pest control; 1=soil management

    private Context context;

    oTreatment(){

    }

    oTreatment(Context c){
        context = c;
    }

    public ArrayList<oTreatment> getTreatments(){
        ArrayList<oTreatment> ret = new ArrayList<>();
        csvFileManager treatmentList;

        treatmentList = new csvFileManager("treatments");
        List<String[]> treatmentCSV = treatmentList.read(context);
        if(treatmentCSV!=null) {
            Iterator<String[]> iterator = treatmentCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                oTreatment t = new oTreatment();
                t.id = Integer.parseInt(record[0]);
                t.name = record[1];
                t.category = Integer.parseInt(record[2]);
                ret.add(t);
            }
        }
        return ret;
    }

    public ArrayList<String> getTreatmentNames(boolean bAddNone){
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<oTreatment> treatmentList = getTreatments();

        if(bAddNone){
            ret.add(context.getString(R.string.textNone));
        }

        Iterator<oTreatment> iterator = treatmentList.iterator();
        while(iterator.hasNext()){
            oTreatment t = iterator.next();
            ret.add(t.name);
        }
        return ret;
    }
}
