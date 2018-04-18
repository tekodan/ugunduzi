package ojovoz.ugunduzi;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Eugenio on 08/03/2018.
 */
public class csvFileManager {

    public String filename;

    csvFileManager(String rFilename) {
        filename = rFilename;
    }

    public List<String[]> read(Context context) {
        List<String[]> ret = null;

        File file = new File(context.getFilesDir(), filename);
        if (file.exists()) {
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

    public void create(Context context, String[] newLine){
        String[] nextLine;
        CSVReader reader = new CSVReader(new StringReader(TextUtils.join(",",newLine)), ',', '"');
        File file = new File(context.getFilesDir(), filename);
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
    }

    public void append(Context context, String[] newLine) {

        List<String[]> currentCSV = read(context);

        if(currentCSV==null){
            create(context, newLine);
        } else {
            currentCSV.add(newLine);


            deleteCSVFile(context);

            File file = new File(context.getFilesDir(), filename);
            try {
                FileWriter w = new FileWriter(file);
                CSVWriter writer = new CSVWriter(w, ',', '"');

                Iterator<String[]> iterator = currentCSV.iterator();
                while (iterator.hasNext()) {
                    String[] thisLine = iterator.next();
                    writer.writeNext(thisLine);
                }
                writer.close();
            } catch (IOException e) {

            } finally {

            }
        }

    }

    public void deleteCSVFile(Context context) {
        context.deleteFile(filename);
    }
}
