package Series;

import Settings.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ucSeries extends VBox {

    private Series series;

    @FXML private Label lbName;
    @FXML
    private Label lbStatus;

    public ucSeries(Series series){
        this.series = series;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ucSeries.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
        }catch (IOException e){
            Settings.log(e);
        }

        lbName.setText("is loading...");
    }

    public Series getSeries(){
        return series;
    }

    public void setLabel(String s){
        lbName.setText(s);
    }
    public void setStatus(String s){
        lbStatus.setText(s);
    }

    public String getLabel(){
        return lbName.getText();
    }

    public void update(){
        lbName.setText(series.Name);
    }
}
