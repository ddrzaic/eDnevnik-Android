package com.example.kalkulatorocjenazae_dnevnik;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileIO {
    public static void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(
                            filename,
                            Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context,String filename) throws FileNotFoundException {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (IOException e) {
            throw new FileNotFoundException();
        }
        return ret;
    }

        public static void writeArrayListToFile(ArrayList<String> al, String filename, Context context){
            try{
                File directory = context.getFilesDir();
                File file = new File(directory, filename);

                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(al);
                oos.close();
            }catch(IOException f){
                Toast.makeText(context,"Write error!"+f.toString(),Toast.LENGTH_LONG).show();
            }
        }

        public static ArrayList<String> readArrayListFromFile(String filename, Context context) throws FileNotFoundException {
            ArrayList al=new ArrayList();
            try {
                File directory = context.getFilesDir();
                File file = new File(directory, filename);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                al= (ArrayList) ois.readObject();
                ois.close();
            }catch(IOException e){
                throw new FileNotFoundException(e.toString());
            }catch(ClassNotFoundException c){}//readObject()
            return al;
        }
}
