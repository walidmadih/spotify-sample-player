/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifyplayer;

import com.sun.javafx.collections.ObservableListWrapper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author bergeron
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    ImageView albumImageView;

    @FXML
    TextField input;

    @FXML
    Label timeLabel;

    @FXML
    Label albumLabel;

    @FXML
    Label titleLabel;

    @FXML
    Button leftButton;

    @FXML
    Button rightButton;

    @FXML
    TableView tracksTableView;

    @FXML
    Slider trackSlider;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    Pane pane;

    @FXML
    MenuItem saveAlbumCovers;

    // Other Fields...
    ScheduledExecutorService sliderExecutor = null;
    MediaPlayer mediaPlayer = null;
    boolean isSliderAnimationActive = false;
    Button lastPlayButtonPressed = null;
    double currentTime;
    double maxTime;

    //Change to true to see progress indicator
    private BooleanProperty isLoading = new SimpleBooleanProperty(false);
    ArrayList<Album> albums = null;
    int currentAlbumIndex = 0;
    String artistName;

    private void startMusic(String url) {
        try {

            lastPlayButtonPressed.setText("Pause");
            trackSlider.setDisable(false);

            if (mediaPlayer != null) {
                stopMusic();
            }

            mediaPlayer = new MediaPlayer(new Media(url));

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                isSliderAnimationActive = true;
                trackSlider.setValue(0);
                trackSlider.setMax(30.0);
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.pause();
                mediaPlayer.seek(Duration.ZERO);
                isSliderAnimationActive = false;
                trackSlider.setValue(0);
            });
            trackSlider.setMax(maxTime);

        } catch (Exception e) {
            titleLabel.setText("ERROR");
            albumLabel.setText("Track preview might not be availible");
            trackSlider.setValue(0.0);
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    public void playPauseMusic() {
        try {
            if (lastPlayButtonPressed != null && lastPlayButtonPressed.getText().equals("Play")) {
                lastPlayButtonPressed.setText("Pause");

                if (mediaPlayer != null) {
                    mediaPlayer.play();
                }
                trackSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                isSliderAnimationActive = true;
            } else {
                lastPlayButtonPressed.setText("Play");
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                isSliderAnimationActive = false;
            }
        } catch (Exception e) {
        }
    }

    private void displayAlbum(int albumNumber) {
        // TODO - Display all the informations about the album
        //
        //        Artist Name 
        //        Album Name
        //        Album Cover Image
        //        Enable next/previous album buttons, if there is more than one album

        // Display Tracks for the album passed as parameter
        if (albumNumber >= 0 && albumNumber < albums.size()) {
            currentAlbumIndex = albumNumber;
            Album album = albums.get(albumNumber);

            //Must run later or else we get a not on same JavaFX thread exception
            //Took a while to fix, please don't change.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    albumLabel.setText(album.getAlbumName());
                    titleLabel.setText(album.getArtistName());
                }

            });
            String imageURL = album.getImageURL();
            Image coverImage = (imageURL != null) ? new Image(imageURL) : null;
            albumImageView.setImage(coverImage);
            // Set tracks
            ArrayList<TrackForTableView> tracks = new ArrayList<>();
            for (int i = 0; i < album.getTracks().size(); i++) {
                TrackForTableView trackForTable = new TrackForTableView();
                Track track = album.getTracks().get(i);
                trackForTable.setTrackNumber(track.getNumber());
                trackForTable.setTrackTitle(track.getTitle());
                trackForTable.setTrackPreviewUrl(track.getUrl());
                tracks.add(trackForTable);
            }
            tracksTableView.setItems(new ObservableListWrapper(tracks));
        }
    }

    private void searchAlbumsFromArtist(String artistName) {
        try {
            // TODO - Make sure this is not blocking the UI
            currentAlbumIndex = 0;
            String artistId = SpotifyController.getArtistId(artistName);
            albums = SpotifyController.getAlbumDataFromArtist(artistId);
        } catch (Exception ex) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            titleLabel.setText("ERROR");
                            albumLabel.setText("Artist not found");

                        }
                    });

                }
            });
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Setup Table View
        TableColumn<TrackForTableView, Number> trackNumberColumn = new TableColumn("#");
        trackNumberColumn.setCellValueFactory(new PropertyValueFactory("trackNumber"));

        TableColumn trackTitleColumn = new TableColumn("Title");
        trackTitleColumn.setCellValueFactory(new PropertyValueFactory("trackTitle"));
        trackTitleColumn.setPrefWidth(250);

        TableColumn playColumn = new TableColumn("Preview");
        playColumn.setCellValueFactory(new PropertyValueFactory("trackPreviewUrl"));
        Callback<TableColumn<TrackForTableView, String>, TableCell<TrackForTableView, String>> cellFactory = new Callback<TableColumn<TrackForTableView, String>, TableCell<TrackForTableView, String>>() {
            @Override
            public TableCell<TrackForTableView, String> call(TableColumn<TrackForTableView, String> param) {
                final TableCell<TrackForTableView, String> cell = new TableCell<TrackForTableView, String>() {
                    final Button playButton = new Button("Play");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null && item.equals("") == false) {
                            playButton.setOnAction(event -> {
                                if (playButton.getText().equals("Pause") || (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(item))) {
                                    playPauseMusic();

                                } else {
                                    if (lastPlayButtonPressed != null) {
                                        lastPlayButtonPressed.setText("Play");

                                    }
                                    lastPlayButtonPressed = playButton;
                                    startMusic(item);

                                }
                            });

                            setGraphic(playButton);
                        } else {
                            setGraphic(null);
                        }

                        setText(null);
                    }
                };

                return cell;
            }
        };
        playColumn.setCellFactory(cellFactory);
        tracksTableView.getColumns().setAll(trackNumberColumn, trackTitleColumn, playColumn);

        // When slider is released, we must seek in the song
        trackSlider.setOnMouseReleased(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(trackSlider.getValue()));
                }
            }
        });

        // Schedule the slider to move right every second
        // Set boolean flag to activate/deactivate the animation
        sliderExecutor = Executors.newSingleThreadScheduledExecutor();
        sliderExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // We can't update the GUI elements on a separate thread... 
                // Let's call Platform.runLater to do it in main thread!!
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Move slider
                        if (isSliderAnimationActive) {
                            trackSlider.setValue(trackSlider.getValue() + 0.016);
                        }
                    }
                });
            }
        }, 16, 16, TimeUnit.MILLISECONDS);

        //Setting the border of the pane
        pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Setting the look of the progress indicator
        progressIndicator.visibleProperty().bind(isLoading);

        //executorSearch will run the album search asynchronously
        //This code will be run whenever the user presses ENTER on the text area
        input.setOnKeyPressed(e -> {
            ScheduledExecutorService executorSearch = Executors.newSingleThreadScheduledExecutor();
            executorSearch.submit(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (e.getCode() == KeyCode.ENTER) {
                        if ("".compareTo(input.getText()) != 0) {
                            currentAlbumIndex = 0;

                            search(input.getText());
                            executorSearch.shutdownNow();
                        }

                    }
                    return null;
                }

            });

        });

        //Setting listeners for both album switch buttons (left and right)
        rightButton.setOnAction(e -> {
            try {
                if (currentAlbumIndex != albums.size() - 1 && !albums.isEmpty()) {
                    currentAlbumIndex++;
                }
                displayAlbum(currentAlbumIndex);
            } catch (Exception error) {
            }
        });

        leftButton.setOnAction(e -> {
            try {

                if (currentAlbumIndex != 0) {
                    currentAlbumIndex--;
                }

                displayAlbum(currentAlbumIndex);
            } catch (Exception error) {
            }
        });

        trackSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                maxTime = mediaPlayer.getTotalDuration().toMillis();
                currentTime = mediaPlayer.getCurrentTime().toMillis();
                int maxMinutes = (int) (maxTime / 60000);
                int maxSeconds = (int) (maxTime / 1000) - maxMinutes * 1000;

                int currentMinutes = (int) (currentTime / 60000);
                int currentSeconds = (int) (currentTime / 1000) - currentMinutes * 1000;
                if (currentSeconds < 10) {
                    timeLabel.setText(currentMinutes + ":0" + currentSeconds + " / " + maxMinutes + ":" + maxSeconds);
                } else {
                    timeLabel.setText(currentMinutes + ":" + currentSeconds + " / " + maxMinutes + ":" + maxSeconds);
                }
            }

        });
        artistName = "lil peep";
        searchAlbumsFromArtist(artistName);
        displayAlbum(0);

    }
    // This will get called automatically when window is closed
    // See spotifyPlayer.java for details about the setup!

    public void shutdownSlider() {
        if (sliderExecutor != null) {
            sliderExecutor.shutdown();
        }
        trackSlider.setValue(0);
    }

    public void search(String name) {
        isLoading.set(true);
        searchAlbumsFromArtist(name);
        displayAlbum(currentAlbumIndex);
        isLoading.set(false);

    }

    @FXML
    public void saveAlbumCovers(ActionEvent e) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String saveArtist = albums.get(0).getArtistName();
                    File directory = new File("./Images/" + saveArtist);
                    directory.mkdir();
                    for (Album album : albums) {

                        String saveAlbum = album.getAlbumName();
                        if (saveAlbum.contains("/")) {
                            saveAlbum = saveAlbum.replace("/", "");
                        }
                        if (saveAlbum.contains("\\")) {
                            saveAlbum = saveAlbum.replace("\\", "");
                        }
                        if (saveAlbum.contains(":")) {
                            saveAlbum = saveAlbum.replace(":", "");
                        }
                        if (saveAlbum.contains("*")) {
                            saveAlbum = saveAlbum.replace("*", "");
                        }
                        if (saveAlbum.contains("?")) {
                            saveAlbum = saveAlbum.replace("?", "");
                        }
                        if (saveAlbum.contains("\"")) {
                            saveAlbum = saveAlbum.replace("\"", "");
                        }
                        if (saveAlbum.contains("<")) {
                            saveAlbum = saveAlbum.replace("<", "");
                        }
                        if (saveAlbum.contains(">")) {
                            saveAlbum = saveAlbum.replace(">", "");
                        }
                        if (saveAlbum.contains("|")) {
                            saveAlbum = saveAlbum.replace("|", "");
                        }

                        Image cover = new Image(album.getImageURL());
                        BufferedImage coverImage = SwingFXUtils.fromFXImage(cover, null);
                        ImageIO.write(coverImage, "png", new File("images/" + saveArtist + "/"
                                + saveAlbum + ".png"));

                    }
                } catch (Exception error) {
                }
                return null;
            }
        });
    }

}
