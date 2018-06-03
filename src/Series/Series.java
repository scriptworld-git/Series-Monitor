package Series;

import WebDriver.*;
import Settings.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Series implements Runnable {
    private transient ucSeries listviewSeries;
    private transient ucSeriesInfo infoSeries;

    public transient boolean flagUI;
    public transient boolean queuing;
    public transient Thread thread;

    public String Name;
    public String BSname;
    public String Description;
    public String URLToImage;

    public int progressSeason;
    public int progressEpisode;

    public boolean newProgress;

    private boolean loaded;
    private boolean error;

    public List<Season> Seasons = new ArrayList<Season>();

    public Series(){
        listviewSeries = new ucSeries(this);
        infoSeries = new ucSeriesInfo(this);
        thread = new Thread(this);
    }

    public Series(String name){
        this();
        this.BSname = name;
        this.loaded = false;

        update();
    }

    public Series(String name, int progressSeason, int progressEpisode){
        this();
        this.BSname = name;
        this.loaded = false;
        this.progressSeason = progressSeason;
        this.progressEpisode = progressEpisode;

        update();
    }

    @Override
    public String toString(){
        return Name;
    }

    public void run(){
        if(!loaded){
            load();
        }else{
            updateLatest();
        }
    }

    public void update(){
        //thread = new Thread(this);
        thread.start();
    }

    public void updateProgress(){
        int seasons = Seasons.size();

        if(seasons > progressSeason){
            newProgress = true;
        }else if(seasons < progressSeason){
            newProgress = false;
        }else if(seasons == progressSeason){
            int episode = Seasons.get(progressSeason - 1).Episodes.size();

            if(episode > progressEpisode){
                newProgress = true;
            }else if(episode <= progressEpisode){
                newProgress = false;
            }
        }
    }

    public synchronized boolean getError(){
        return this.error;
    }

    private synchronized void setError(boolean state){
        this.error = state;
    }

    private void load(){
        try {
            Driver driver = new Driver();

            driver.get(Settings.urlBSTO + BSname);

            WebElement root = driver.findElement(By.id("root"));

            setError(false);

            try {
                root.findElement(By.className("andere-serien"));
                setError(true);
            } catch (NoSuchElementException e) {
                setError(false);
            }

            if (getError()) {
                Settings.log("Couldn't find given Series!");
                return;
            }


            WebElement serie = root.findElement(By.className("serie"));

            Name = serie.findElement(By.className("top")).findElement(By.id("sp_left"))
                    .findElement(By.tagName("h2")).getText();

            Name = Name.substring(0, Name.indexOf("Staffel"));
            Name = Name.trim();

            Description = serie.findElement(By.className("top")).findElement(By.id("sp_left"))
                    .findElement(By.className("justify")).getText();
            URLToImage = serie.findElement(By.className("top")).findElement(By.id("sp_right"))
                    .findElement(By.cssSelector("img[alt='Cover']")).getAttribute("src");

            List<WebElement> elements = serie.findElement(By.className("seasons full")).findElement(By.id("seasons"))
                    .findElement(By.className("clearfix")).findElements(By.tagName("li"));

            for (WebElement e : elements) {
                if (!(e.getText().equals("Specials"))) {
                    Season tempS = new Season(Integer.parseInt(e.getText()), BSname);
                    Seasons.add(tempS);
                }

            }

            for (Season s: Seasons) {
                try{
                    s.thread.join();

                }catch(InterruptedException e){
                    Settings.log(e);
                }
            }

            this.loaded = true;
            this.flagUI = true;

            updateProgress();

            Settings.log(Name + " loaded! - Seasons: " + Seasons.size());

        }catch(NoSuchElementException e){
            Settings.log(e);
            setError(true);
        }
    }

    private void updateLatest(){
        try {
            Driver driver = new Driver();

            driver.get(Settings.urlBSTO + BSname);

            WebElement root = driver.findElement(By.id("root"));

            setError(false);

            try {
                root.findElement(By.className("andere-serien"));
                setError(true);
            } catch (NoSuchElementException e) {
                 setError(false);
            }

            if (getError()) {
                Settings.log("Couldn't find given Series!");
                return;
            }


            WebElement serie = root.findElement(By.className("serie"));
            List<WebElement> elements = serie.findElement(By.className("seasons full")).findElement(By.id("seasons"))
                    .findElement(By.className("clearfix")).findElements(By.tagName("li"));

            List<WebElement> seasonElemets = new ArrayList<>();

            for (WebElement e : elements) {
                if (!(e.getText().equals("Specials"))) {
                    seasonElemets.add(e);
                }
            }

            if (seasonElemets.size() < Seasons.size()) {
                error = true;
                return;
            } else if (seasonElemets.size() == Seasons.size()) {
                Seasons.get(seasonElemets.size() - 1).update();
            } else if (seasonElemets.size() > Seasons.size()) {
                Seasons.get(Seasons.size() - 1).update();

                for (int i = Seasons.size(); i < seasonElemets.size(); i++) {
                    Season tempS = new Season(Integer.parseInt(seasonElemets.get(i).getText()), BSname);
                    Seasons.add(tempS);
                }
            }

            for (Season s: Seasons) {
                try{
                    s.thread.join();
                }catch (InterruptedException e){
                    Settings.log(e);
                }
            }

            this.flagUI = true;

            Settings.log(Name + " updated!");
        }catch(NoSuchElementException e){
            Settings.log(e);
            setError(true);
        }
    }

    public ucSeries getListviewSeries(){
        return listviewSeries;
    }

    public ucSeriesInfo getInfoSeries(){
        return infoSeries;
    }
}
