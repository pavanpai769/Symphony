
package org.symphony.controller;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.symphony.service.SongService;
import org.symphony.models.*;


public class Controller implements Initializable {

    @FXML
    private Menu fileMenu;

    @FXML
    private ListView<String> songList;

    @FXML
    private MenuItem playNowFromSongList;

    @FXML
    private MenuItem playNextFromSongList;

    @FXML
    private MenuItem addToQueue;

    @FXML
    private ImageView albumArt;

    @FXML
    private Label songTitle;

    @FXML
    private Label songArtist;

    @FXML
    private ProgressBar songProgressBar;

    @FXML
    private ToggleButton playOrPauseButton;

    @FXML
    private ImageView playOrPauseButtonImage;

    @FXML
    private ComboBox<String> speedChangeBox;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ListView<String> queueList;

    @FXML
    private MenuItem playFromQueueList;

    @FXML
    private MenuItem removeItemFromQueueList;

    @FXML
    private MenuItem clearQueueList;

    private Media media;
    private MediaPlayer mediaPlayer;


    private final String[] speeds = {"0.25x", "0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x"};
    private Timer timer;

    private boolean running;
    private SongCollections songCollections;
    private SongService songService;
    private SongMetaData metaDataOfTheCurrentSong;

    private final Image play = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/symphony/assets/play.png")));
    private final Image pause = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/symphony/assets/pause.png")));
    private SongQueue songQueue;
    private SongMetaData defaultMetaData;

    private PlayedSongList playedSongList;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        Image albumArtDefaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/symphony/assets/default.png")));
        songArtist.setText("Artist");
        songTitle.setText("Title");
        defaultMetaData= new SongMetaData("Title","Artist","","","", albumArtDefaultImage);
        albumArt.setImage(albumArtDefaultImage);
        playedSongList=new PlayedSongList();
        songQueue = new SongQueue();
        metaDataOfTheCurrentSong =new SongMetaData();
        songService = new SongService();
        songCollections = new SongCollections();
        songList.setOnMouseClicked(this::showContextMenuForSongList);
        playNowFromSongList.setOnAction(this::playFromSongList);
        playNextFromSongList.setOnAction(this::setPlayNextFromSongList);
        addToQueue.setOnAction(this::createQueue);

        songList.setOnMouseClicked(this::playSongOnClick);

        playFromQueueList.setOnAction(this::playFromQueueList);
        removeItemFromQueueList.setOnAction(this::removeFromQueue);
        clearQueueList.setOnAction(this::clearQueue);

        for(String speed : speeds) {
            speedChangeBox.getItems().add(speed);
        }
        speedChangeBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener((arg01, arg11, arg2) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        songProgressBar.setStyle("-fx-accent: rgb(40,77,194);");

        songProgressBar.setOnMouseClicked(event -> {
            if (mediaPlayer != null && media != null) {
                double mouseX = event.getX();
                double progressBarWidth = songProgressBar.getWidth();
                double seekTime;

                if (songProgressBar.getProgress() >= 1.0) {
                    seekTime = media.getDuration().toSeconds();
                } else {
                    seekTime = (mouseX / progressBarWidth) * media.getDuration().toSeconds();
                }
                mediaPlayer.seek(Duration.seconds(seekTime));
            }
        });

        songList.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.getWindow().setOnCloseRequest(event -> {
                    if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.stop();
                    }
                });
            }
        });

    }


    public void addFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selected = directoryChooser.showDialog(null);

        if (selected != null) {

            File[] list = selected.listFiles();

            if(list!=null) {
                for (File file : list) {
                    if (file.getName().endsWith(".mp3")) {

                        String pathOfTheSong = file.getAbsolutePath();
                        SongMetaData metaDataOfTheSong = songService.getSongMetadataByPath(pathOfTheSong);
                        String titleOfTheSong =  songService.getSongTitleByMetaData(metaDataOfTheSong);
                        if(!songService.contains(songCollections,titleOfTheSong)) {
                            songService.addSongToCollections(songCollections,new Song(pathOfTheSong,metaDataOfTheSong));
                            songService.addTitleToSongCollection(songCollections,titleOfTheSong);
                            songList.getItems().add(titleOfTheSong);
                        }
                    }
                }
            }
        }
    }


    public void closeFile() {
        fileMenu.getParentPopup().hide();
    }

    private void showContextMenuForSongList(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            songList.getContextMenu().show(songList, event.getScreenX(), event.getScreenY());
        }
    }

    public void clearSongList() {
        songService.clearSongList(songCollections);
        clearQueue(null);
        songList.getItems().clear();
    }

    private void playFromSongList(ActionEvent event) {
        String selectedSongTitle = songList.getSelectionModel().getSelectedItem();
        Song selectedSong = songService.getSong(songCollections,selectedSongTitle);

        if(selectedSong==null) return;

        if (mediaPlayer != null) {
            Song currentlyPlayingSong = songService.getSong(songCollections,metaDataOfTheCurrentSong.getTitle());
            if(currentlyPlayingSong==selectedSong) return;
            mediaPlayer.stop();
            Song currentSong = songService.getSong(songCollections,metaDataOfTheCurrentSong.getTitle());
            playedSongList.pushSong(currentSong);
        }


        metaDataOfTheCurrentSong= selectedSong.getMetaData();
        if(metaDataOfTheCurrentSong==null) metaDataOfTheCurrentSong=defaultMetaData;
        songQueue.addFirstToQueue(selectedSong);
        queueList.getItems().addFirst(selectedSongTitle);
        media = new Media(new File(selectedSong.getSongPath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        playOrPauseButton.setSelected(true);
        playMedia();
    }


    private void setPlayNextFromSongList(ActionEvent event) {
            String selectedSongTitle = songList.getSelectionModel().getSelectedItem();
            Song slectedSong = songService.getSong(songCollections,selectedSongTitle);

            if(slectedSong==null) return;

            String currentlyPlayingSongTitle =  metaDataOfTheCurrentSong.getTitle();
            Song currentlyPlayingSong = songService.getSong(songCollections,currentlyPlayingSongTitle);

            if(currentlyPlayingSong==null){
                songQueue.addToQueue(slectedSong);
                queueList.getItems().addFirst(selectedSongTitle);
            }else{
                songQueue.insertSongAfterCurrent(currentlyPlayingSong,slectedSong);
                queueList.getItems().add(1,selectedSongTitle);
            }
    }


    private void createQueue(ActionEvent event) {
        String selectedSongTitle = songList.getSelectionModel().getSelectedItem();
        Song selectedSong = songService.getSong(songCollections,selectedSongTitle);

        if(selectedSong==null) return;

        songQueue.addToQueue(selectedSong);
        queueList.getItems().add(selectedSongTitle);

    }

    private void playSongOnClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            playFromSongList(null);
        }
    }

    @FXML
    private void playOrPauseWithSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            playOrPauseMedia();
        }
    }



    private void playFromQueueList(ActionEvent event) {
        String selectedSongTitle = queueList.getSelectionModel().getSelectedItem();
        if (mediaPlayer != null) {
            Song currentlyPlayedSong = songService.getSong(songCollections,metaDataOfTheCurrentSong.getTitle());

            if(selectedSongTitle.equals(currentlyPlayedSong.getMetaData().getTitle())) return;


            mediaPlayer.stop();
            playedSongList.pushSong(currentlyPlayedSong);
        }
        Song selectedSong = songService.getSong(songCollections,selectedSongTitle);

        if(selectedSong==null) return;

        media = new Media(new File(selectedSong.getSongPath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        queueList.getItems().remove(selectedSongTitle);
        songQueue.removeSong(selectedSong);
        queueList.getItems().addFirst(selectedSongTitle);
        metaDataOfTheCurrentSong=selectedSong.getMetaData();
        if(metaDataOfTheCurrentSong==null) metaDataOfTheCurrentSong=defaultMetaData;
        songQueue.addFirstToQueue(selectedSong);
        playOrPauseButton.setSelected(true);
        playMedia();

    }


    private void removeFromQueue(ActionEvent event) {
        String selectedSongTitle = queueList.getSelectionModel().getSelectedItem();
        Song selectedSong = songService.getSong(songCollections,selectedSongTitle);

        if(selectedSong==null) return;

        if(queueList.getItems().size()==1){
            clearQueue(null);
        }
        songQueue.removeSong(selectedSong);
        queueList.getItems().remove(selectedSongTitle);


        if(mediaPlayer!=null && selectedSongTitle.equals(metaDataOfTheCurrentSong.getTitle())){
           mediaPlayer.stop();
           Song currentlyPlayedSong = songService.getSong(songCollections,metaDataOfTheCurrentSong.getTitle());
           playedSongList.pushSong(currentlyPlayedSong);
           nextMedia();
        }

    }

    public void clearQueue(ActionEvent event) {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer=null;
        }
        songQueue.removeAllSongs();
        queueList.getItems().clear();
        metaDataOfTheCurrentSong=defaultMetaData;
        metaInitialize();
        songProgressBar.setProgress(0.0);
    }

    public void metaInitialize() {
        if (metaDataOfTheCurrentSong != null) {
            albumArt.setImage(metaDataOfTheCurrentSong.getAlbumArt());
            songTitle.setText(metaDataOfTheCurrentSong.getTitle());
            songArtist.setText(metaDataOfTheCurrentSong.getArtist());
        }
    }


    public void playMedia() {


        if (mediaPlayer != null && media != null) {
            metaInitialize();
            beginTimer();
            changeSpeed(null);
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            playOrPauseButton.setSelected(true);
            playOrPauseButtonImage.setImage(pause);
            mediaPlayer.play();
            songProgressBar.setProgress(0);
        }
    }

    public void playOrPauseMedia() {
        if (playOrPauseButton.isSelected()) {
            if (mediaPlayer != null && media != null) {
                playMedia();
            }
        }

        else {
            if (mediaPlayer != null && media != null) {
            	playOrPauseButtonImage.setImage(play);
                mediaPlayer.pause();
            }
        }
    }


    public void resetMedia() {
        if (mediaPlayer != null && media != null) {
            songProgressBar.setProgress(0);
            mediaPlayer.seek(Duration.seconds(0));
        }
    }


    public void previousMedia() {
        if(playedSongList!=null && !playedSongList.isEmpty()){
            if(mediaPlayer!=null){
                mediaPlayer.stop();
            }
            Song previousSong = playedSongList.getSong();
            songQueue.addFirstToQueue(previousSong);
            metaDataOfTheCurrentSong=previousSong.getMetaData();
            queueList.getItems().addFirst(metaDataOfTheCurrentSong.getTitle());
            media=new Media(new File(previousSong.getSongPath()).toURI().toString());
            mediaPlayer=new MediaPlayer(media);
            playMedia();
        }

    }




    public void nextMedia() {
        try {
            Platform.runLater(() -> {
                if(mediaPlayer!=null && media!=null) {
                    Song currentSong = songService.getSong(songCollections,metaDataOfTheCurrentSong.getTitle());
                    Song nextSong = songQueue.getNextSong(currentSong);
                    if(nextSong!=null) {
                        mediaPlayer.stop();
                        playedSongList.pushSong(currentSong);
                        queueList.getItems().removeFirst();
                        songQueue.removeSong(currentSong);
                        media = new Media(new File(nextSong.getSongPath()).toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        metaDataOfTheCurrentSong= nextSong.getMetaData();
                        playMedia();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void changeSpeed(ActionEvent event) {
        if (speedChangeBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Double.parseDouble(speedChangeBox.getValue().substring(0, speedChangeBox.getValue().length() - 1)));
        }
    }


    public void beginTimer() {
        if (mediaPlayer != null && songQueue.getFirstSong() != null && media != null) {
            timer = new Timer();

            TimerTask task = new TimerTask() {
                public void run() {
                    running = true;
                    try {
                        if (mediaPlayer != null && media != null) {
                            double current = mediaPlayer.getCurrentTime().toSeconds();
                            double end = media.getDuration().toSeconds();
                            double progress = current / end;

                            Platform.runLater(() -> songProgressBar.setProgress(progress));

                            if (current / end == 1) {
                                cancelTimer();

                                Platform.runLater(() -> {
                                    if (songQueue.getFirstSong() != null && songQueue.getFirstSong().nextSong != null) {
                                        nextMedia();
                                    } else {
                                        metaDataOfTheCurrentSong = defaultMetaData;
                                        metaInitialize();
                                    }

                                    if (songQueue.getFirstSong().nextSong == null && queueList.getItems().size() == 1 && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                        playedSongList.pushSong(songQueue.getFirstSong().song);
                                        songQueue.removeFirstSong();
                                        Platform.runLater(() -> queueList.getItems().removeFirst());
                                    }
                                });

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 1000, 1000);

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!songProgressBar.isPressed() && media != null && mediaPlayer != null) {
                    Platform.runLater(() -> {
                        double current = mediaPlayer.getCurrentTime().toSeconds();
                        double end = media.getDuration().toSeconds();
                        double progress = current / end;
                        songProgressBar.setProgress(progress);
                    });
                }
            });
        }
    }


    public void cancelTimer() {
        running = false;
        timer.cancel();
    }


}



