package ojovoz.ugunduzi;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugenio on 13/04/2018.
 */
public class oUnit {

    public int id;
    public String name;

    private Context context;

    oUnit(){

    }

    oUnit(Context c){
        context=c;
    }

    public ArrayList<oUnit> getUnits(){
        ArrayList<oUnit> ret = new ArrayList<>();
        csvFileManager unitList;

        unitList = new csvFileManager("units");
        List<String[]> unitCSV = unitList.read(context);
        if(unitCSV!=null) {
            Iterator<String[]> iterator = unitCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                oUnit u = new oUnit();
                u.id = Integer.parseInt(record[0]);
                u.name = record[1];
                ret.add(u);
            }
        }
        return ret;
    }

    public ArrayList<String> getUnitNames(){
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<oUnit> unitList = getUnits();

        Iterator<oUnit> iterator = unitList.iterator();
        while(iterator.hasNext()){
            oUnit u = iterator.next();
            ret.add(u.name);
        }
        return ret;
    }

    public oUnit getUnitFromId(int id){
        oUnit ret = new oUnit();
        if(id==0){
            ret=null;
        } else {
            ArrayList<oUnit> unitList = getUnits();
            Iterator<oUnit> iterator = unitList.iterator();
            while (iterator.hasNext()) {
                oUnit u = iterator.next();
                if (u.id == id) {
                    ret = u;
                    break;
                }
            }
        }
        return ret;
    }
}