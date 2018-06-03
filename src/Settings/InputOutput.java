package Settings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
    private InputOutput(){}

    public static void CWCfile(String content, String path) throws Exception {
        File file1 = new File(path);

        if (!file1.exists())
            file1.createNewFile();

        BufferedWriter outStream;

        outStream = new BufferedWriter(new FileWriter(file1));
        outStream.write(content);
        outStream.close();

        Settings.log("Export Done.");
    }

    public static List<String> RLCfile(String filename) throws Exception {
        BufferedReader reader = null;
        List<String> lines = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(filename));
        }catch (IOException e){
            Settings.log(e);
        }

        String         line = null;

        try {
            while((line = reader.readLine()) != null) {
                lines.add(line);
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

        return lines;
    }
}
