package ojovoz.ugunduzi;

import android.content.Context;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugenio on 08/03/2018.
 */
public class oUser {
    public String userAlias;
    public String userPassword;

    private csvFileReader userList;
    private Context context;

    oUser(Context rContext, String rUserAlias, String rUserPassword){
        userAlias=rUserAlias;
        userPassword=rUserPassword;

        context=rContext;

        userList = new csvFileReader("users");
    }

    oUser(Context rContext){
        context=rContext;
        userList = new csvFileReader("users");
    }

    public int getUserIdFromAliasPass(){
        int ret=-1;
        List<String[]> usersCSV = userList.read(context);
        if(usersCSV!=null) {
            Iterator<String[]> iterator = usersCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                if(userAlias.equals(record[1]) && userPassword.equals(record[1])){
                    ret=Integer.parseInt(record[0]);
                    break;
                }
            }
        }
        return ret;
    }

    public String getAllUserNames(){
        String ret="";
        List<String[]> usersCSV = userList.read(context);
        if(usersCSV!=null) {
            Iterator<String[]> iterator = usersCSV.iterator();
            while (iterator.hasNext()) {
                String[] record = iterator.next();
                if(ret.isEmpty()){
                    ret=record[1];
                } else {
                    ret+=","+record[1];
                }
            }
        }
        return ret;
    }

}
