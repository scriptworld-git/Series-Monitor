package Settings;

public class Settings {
    private void Settings(){}

    public static String urlBSTO;

    public static void loadSettings(){
        urlBSTO = "https://bs.to/serie/";
    }

    public static void log(Object var){
        System.out.print(var);
        System.out.print("\n");
    }
}