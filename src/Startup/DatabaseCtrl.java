package Startup;

import Series.*;
import Settings.*;
import Utilities.Stream;
import Utilities.UpdateManager;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCtrl {

    private List<Series> SeriesList = new ArrayList<Series>();
    private ObservableList<ucSeries> ucSeries = FXCollections.observableArrayList();

    private ucSeriesInfo seriesInfoPane;
    private UpdateManager updateManager;

    @FXML
    private TextField tbSeries;

    @FXML
    private Button btAddSeries;

    @FXML
    private ListView<ucSeries> lbSeries;

    @FXML
    private Button btRefresh;

    @FXML
    private Button btSave;

    @FXML
    private HBox hboxMain;

    @FXML
    private void initialize(){
        lbSeries.itemsProperty().set(ucSeries);
        load();
        updatingUI();
    }

    @FXML
    public void onEnter(ActionEvent e){
        addClick();
    }

    @FXML
    private void btSaveClick(ActionEvent event){
        save();
    }

    @FXML
    private void btDeleteClick(ActionEvent event){
        if(lbSeries.getSelectionModel().getSelectedItem() == null)
            return;

        deletePane();
        ucSeries tempS = lbSeries.getSelectionModel().getSelectedItem();
        SeriesList.remove(tempS.getSeries());
        ucSeries.remove(tempS);
    }

    @FXML
    private void btAddClick(ActionEvent event) {
        addClick();
    }

    private void addClick(){
        String text = tbSeries.getText();
        tbSeries.clear();

        if(text.isEmpty())
            return;

        if(text.length() >= 6){
            if(text.substring(0, 6).equals("export")){
                exportSeries(text.substring(7));
                return;
            }else if(text.substring(0, 6).equals("import")){
                importSeries(text.substring(7));
                return;
            }
        }

        text = text.replaceAll(" ","-");
        text = text.replaceAll("'","-");

        Series series = new Series(text);
        SeriesList.add(series);
        ucSeries.add(series.getListviewSeries());
    }

    @FXML
    private void btRefreshClick(ActionEvent event) {
        if(updateManager != null)
            if(updateManager.thread.isAlive())
                return;

        deletePane();


        UpdateManager update = new UpdateManager();
        update.update(SeriesList);
    }

    @FXML
    private void lvClicked(){
        if(lbSeries.getSelectionModel().getSelectedItems().size() > 1)
            return;

        ucSeries tempS = lbSeries.getSelectionModel().getSelectedItem();

        if(tempS == null)
            return;

        if (tempS.getSeries().getError() || tempS.getSeries().thread.isAlive() || tempS.getSeries().queuing)
            return;

        createPane(tempS);
    }

    private void load(){
        Stream stream = new Stream("SeriesDatabase.sav");

        if (!stream.fileExists())
            return;

        String json = stream.readAll();

        Gson gson = new Gson();
        Series[] series = gson.fromJson(json, Series[].class);

        for (Series s: series) {
            SeriesList.add(s);
            ucSeries.add(s.getListviewSeries());
            s.getInfoSeries().update();
            s.getListviewSeries().update();
        }
    }

    private void save(){
        Gson gson = new Gson();
        String json = gson.toJson(SeriesList);
        Settings.log(json);

        Stream stream = new Stream("SeriesDatabase.sav");

        if(!stream.fileExists())
            stream.createFile();

        stream.writeAll(json);

        Settings.log("Database saved.");
    }

    private void createPane(ucSeries series){
        deletePane();
        Series temp = series.getSeries();

        if(temp.thread.isAlive() || temp.getError())
            return;

        seriesInfoPane = temp.getInfoSeries();
        seriesInfoPane.update();
        seriesInfoPane.setPrefHeight(Control.USE_COMPUTED_SIZE);
        seriesInfoPane.setPrefWidth(Control.USE_COMPUTED_SIZE);
        hboxMain.getChildren().add(seriesInfoPane);
        HBox.setHgrow(seriesInfoPane, Priority.ALWAYS);
        Main.getPrimaryStage().setWidth(Main.getPrimaryStage().getWidth() + 500);
    }

    private void deletePane(){
        if(seriesInfoPane == null)
            return;

        Main.getPrimaryStage().setWidth(Main.getPrimaryStage().getWidth() - 500);
        hboxMain.getChildren().remove(seriesInfoPane);
        seriesInfoPane = null;
    }

    private void updatingUI() {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            //Finished Updating Series
                            for (int i = 0; i < SeriesList.size(); i++){
                                if(SeriesList.get(i).flagUI){
                                    SeriesList.get(i).flagUI = false;
                                    SeriesList.get(i).getListviewSeries().update();
                                    SeriesList.get(i).getInfoSeries().update();
                                }else if(SeriesList.get(i).getError()){
                                    SeriesList.get(i).getListviewSeries().setLabel(SeriesList.get(i).BSname);
                                    SeriesList.get(i).getListviewSeries().setStatus("Error");
                                }else if (!SeriesList.get(i).newProgress && !SeriesList.get(i).thread.isAlive()){
                                    SeriesList.get(i).getListviewSeries().setStatus("");
                                }else if(SeriesList.get(i).thread.isAlive()){
                                    SeriesList.get(i).getListviewSeries().setStatus("updating");
                                }else if(SeriesList.get(i).queuing){
                                    SeriesList.get(i).getListviewSeries().setStatus("in Queue");
                                }else if(SeriesList.get(i).newProgress && !SeriesList.get(i).thread.isAlive()) {
                                    SeriesList.get(i).getListviewSeries().setStatus("New");
                                }
                            }
                        }
                    });
                    Thread.sleep(500);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void exportSeries(String name){
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        for (Series s: SeriesList) {
            stringBuilder.append(s.BSname + ";" + s.progressSeason + ";" + s.progressEpisode);
            stringBuilder.append(ls);
        }

        try {
            InputOutput.CWCfile(stringBuilder.toString(), name);
        }catch (IOException e){
            Settings.log(e);
        }catch (Exception e){
            Settings.log(e);
        }
    }

    private void importSeries(String name){
        List<String> sSeries = null;

        try{
            sSeries = InputOutput.RLCfile(name);
        }catch(IOException e){
            Settings.log(e);
            return;
        }catch(Exception e){
            Settings.log(e);
            return;
        }

        for (String s : sSeries) {
            int Season = 0;
            int Episode = 0;
            String nameS = null;

            String[] splits = s.split(";");

            nameS = splits[0];
            Season = Integer.parseInt(splits[1]);
            Episode = Integer.parseInt(splits[2]);

            Series series = new Series(nameS, Season, Episode);
            SeriesList.add(series);
            ucSeries.add(series.getListviewSeries());

        }
    }

}
