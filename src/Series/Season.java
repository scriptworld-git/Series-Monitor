package Series;

import Settings.Settings;
import WebDriver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Season implements Runnable {
    public String BSname;

    public int SeasonNumber;
    public List<Episode> Episodes = new ArrayList<Episode>();

    private boolean loaded;
    private boolean error;

    public transient Thread thread;

    public Season(){
        thread = new Thread(this);
    }


    public Season(int number, String BSname){
        this();
        this.SeasonNumber = number;
        this.BSname = BSname;
        loaded = false;

        update();
    }

    @Override
    public String toString(){
        return "Season " + SeasonNumber;
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

    private void load(){
        Driver driver = new Driver();
        driver.get(Settings.urlBSTO + BSname + "/" + SeasonNumber);

        WebElement root = driver.findElement(By.id("root"));

        setError(false);

        try{
            root.findElement(By.className("serie"))
                    .findElement(By.className("messageBox error"));
            setError(true);
        }catch(NoSuchElementException e){
            setError(false);
        }

        if(getError()){
            Settings.log("Cant find given Season!");
            return;
        }

        WebElement temp = root.findElement(By.className("serie")).findElement(By.className("episodes"));

        List<WebElement> episodes = temp.findElements(By.tagName("tr"));

        for (WebElement e: episodes) {
            WebElement tempElement = e.findElement(By.tagName("a"));
            int number = Integer.parseInt(tempElement.getText());
            String name = tempElement.getAttribute("title");

            List<WebElement> tdElements = e.findElements(By.tagName("td"));

            for (WebElement td: tdElements) {
                
            }

            Episode tempEpisode = new Episode(number, name);
            Episodes.add(tempEpisode);
        }

        Settings.log(BSname + " Season " + SeasonNumber + " updated! - Episodes: " + Episodes.size());

        loaded = true;

    }

    public synchronized boolean getError(){
        return error;
    }

    private synchronized void setError(boolean state){
        this.error = state;
    }

    private void updateLatest(){
        Driver driver = new Driver();
        driver.get(Settings.urlBSTO + BSname + "/" + SeasonNumber);

        WebElement root = driver.findElement(By.id("root"));

        setError(false);

        try{
            root.findElement(By.className("serie"))
                    .findElement(By.className("messageBox error"));
            setError(true);
        }catch(NoSuchElementException e){
            setError(false);
        }

        if(error){
            Settings.log("Cant find given Season!");
            return;
        }

        WebElement temp = root.findElement(By.className("serie")).findElement(By.className("episodes"));

        List<WebElement> episodes = temp.findElements(By.tagName("tr"));

        int size = Episodes.size();

        for (WebElement e: episodes) {
            WebElement tempElement = e.findElement(By.tagName("a"));
            int number = Integer.parseInt(tempElement.getText());
            String name = tempElement.getAttribute("title");

            if(number > size){
                Episode tempEpisode = new Episode(number, name);
                Episodes.add(tempEpisode);
            }else{
                Episodes.get(number - 1).Name = name;
            }
        }
    }
}
