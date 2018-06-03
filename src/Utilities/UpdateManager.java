package Utilities;

import Series.Series;
import Settings.Settings;

import java.util.List;

//import from .txt
//update mit refresh list

public class UpdateManager implements Runnable {

    public Thread thread = new Thread(this);
    private List<Series> SeriesList;

    public UpdateManager(){

    }

    public void run(){
        for (Series s: SeriesList) {
            s.queuing = true;
        }

        int x = 0;

        for(int i = 0; i < SeriesList.size(); i++){
            SeriesList.get(i).queuing = false;
            SeriesList.get(i).update();
            x++;

            if(x == 5){
                for (Series s2: SeriesList) {
                    try {
                        s2.thread.join();
                    }catch (InterruptedException e){
                        Settings.log(e);
                    }
                }

                x = 0;
            }
        }

        for (Series s2: SeriesList) {
            try {
                s2.thread.join();
            }catch (InterruptedException e){
                Settings.log(e);
            }
        }

        Settings.log("Finished updating!");
    }

    //update mit refresh Knopf
    public void update(List<Series> list){
        if(thread.isAlive())
            return;

        SeriesList = list;
        thread.start();
    }

    //update mit .txt file
    public void update(){

    }

}
