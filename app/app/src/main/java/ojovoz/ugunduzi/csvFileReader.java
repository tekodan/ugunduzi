package ojovoz.ugunduzi;

import android.content.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by Eugenio on 08/03/2018.
 */
public class csvFileReader {

    public String filename;

    csvFileReader(String rFilename){
        filename=rFilename;
    }

    public List<String[]> read(Context context){
        List<String[]> ret = null;

        File file = new File(context.getFilesDir(), filename);
        if(file.exists()) {
            try {
                FileReader r = new FileReader(file);
                CSVReader reader = new CSVReader(r, ',', '"');
                ret = reader.readAll();
            } catch (IOException e) {

            } finally {
                return ret;
            }
        }

        return ret;
    }
}
