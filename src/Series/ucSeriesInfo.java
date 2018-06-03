package Series;

import Settings.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.io.IOException;

public class ucSeriesInfo extends VBox {

    @FXML
    private Label lbName;

    @FXML
    private Label lbNewest;

    @FXML
    private Button btSave;

    @FXML
    private Button btAll;

    @FXML
    private Button btOpenSeries;

    @FXML
    private Text lbDescription;

    @FXML
    private TextField tbProgressSeason;

    @FXML
    private TextField tbProgressEpisode;

    @FXML
    private ImageView imgCover;

    private Series series;

    public ucSeriesInfo(Series series){
        this.series = series;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ucSeriesInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException e){
            Settings.log(e);
        }

        btAll.setOnAction(e -> {
            tbProgressSeason.setText(Integer.toString(series.Seasons.size()));
            String temp = Integer.toString(series.Seasons.get(series.Seasons.size() - 1).Episodes.size());
            tbProgressEpisode.setText(temp);
        });

        btSave.setOnAction(e -> saveProgress());

    }

    private void saveProgress(){
        String Season = tbProgressSeason.getText();
        String Episode = tbProgressEpisode.getText();

        if(Season.isEmpty()){
            tbProgressSeason.clear();
            tbProgressEpisode.clear();
            return;
        }

        int iSea, iEp;

        try {
            iSea = Integer.parseInt(Season);

            if(!Episode.isEmpty())
                iEp = Integer.parseInt(Episode);
            else
                iEp = -1;

        }catch(NumberFormatException e){
            Settings.log(e);
            tbProgressSeason.clear();
            tbProgressEpisode.clear();
            return;
        }

        if(iEp < -1 || iSea <= 0){
            tbProgressEpisode.clear();
            tbProgressSeason.clear();
            return;
        }

        if(iSea > series.Seasons.size()){
            tbProgressSeason.clear();
            tbProgressEpisode.clear();
            return;
        }

        if(iEp > series.Seasons.get(iSea - 1).Episodes.size()){
            tbProgressEpisode.clear();
            tbProgressSeason.clear();
            return;
        }

        if(iEp == -1){
            iEp = series.Seasons.get(iSea - 1).Episodes.size();
            tbProgressEpisode.setText(Integer.toString(iEp));
        }

        series.progressEpisode = iEp;
        series.progressSeason = iSea;

        series.updateProgress();
    }

    public void update(){
        lbName.setText(series.Name);
        //lbDescription.setText(series.Description);
        imgCover.setImage(new Image(series.URLToImage));
        String newest = "Newest Season: " + series.Seasons.size() + " - Newest Episode: " + series.Seasons.get(series.Seasons.size() - 1).Episodes.size();
        lbNewest.setText(newest);

        if(series.progressSeason != 0)
            tbProgressSeason.setText(Integer.toString(series.progressSeason));

        if(series.progressEpisode != 0)
            tbProgressEpisode.setText(Integer.toString(series.progressEpisode));
    }
}

