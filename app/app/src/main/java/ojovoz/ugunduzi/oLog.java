package ojovoz.ugunduzi;

import android.content.Context;

import java.util.Date;

/**
 * Created by Eugenio on 13/04/2018.
 */
public class oLog {

    public String farmName;
    public int userId;
    public String userName;

    public int plotId;
    public Date date;
    public oDataItem dataItem;
    public float value;
    public oUnit units;

    public String picture;
    public String sound;

    private Context context;

    oLog(){

    }

    oLog(Context c){
        context=c;
    }


}
