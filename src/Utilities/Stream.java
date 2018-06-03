package Utilities;

import Settings.Settings;


import java.io.*;

public class Stream {

    private String filePath;
    private File file;

    public Stream(String filePath){
        this.filePath = filePath;
        file = new File(this.filePath);
    }

    public boolean fileExists(){
        if(file.exists())
            return true;
        else
            return false;
    }

    public void createFile(){
        if(this.fileExists())
            return;

        try {
            file.createNewFile();
        }catch(IOException e){
            Settings.log(e);
        }
    }

    public String readAll(){
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
        }catch (IOException e){
            Settings.log(e);
        }

        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

        }catch(IOException e) {
            Settings.log(e);
        }
        finally
        {
            try {
                reader.close();
            }catch(IOException e){
                Settings.log(e);
            }
        }

        Settings.log("Reading File done: " + filePath);
        return stringBuilder.toString();
    }

    public void writeAll(String s){
        BufferedWriter outStream;

        try {
            outStream = new BufferedWriter( new FileWriter(file));
            outStream.write(s);
            outStream.close();
        }catch(IOException e){
            Settings.log(e);
        }

        Settings.log("Writing File done: " + filePath);
    }
}
