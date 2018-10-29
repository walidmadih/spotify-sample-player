package spotifyparser;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpotifyParser extends Application{

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        try{
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ArrayList<Album> parsedAlbums = (ArrayList<Album>) executor.submit(new Task<ArrayList<Album>>() {
            @Override
            protected ArrayList<Album> call() throws Exception {
                String artistId = SpotifyController.getArtistId("Imagine Dragons");
                ArrayList<Album> albums = SpotifyController.getAlbumDataFromArtist(artistId);
                return albums;
            }
            
            protected void succeeded(){
                
            }
            
            protected void cancelled(){
                
            }
        }).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        launch(args);
    }
    
}
