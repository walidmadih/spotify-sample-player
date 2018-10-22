package spotifyparser;

import java.util.ArrayList;

public class SpotifyParser {

    public static void main(String[] args) {
        String artistId = SpotifyController.getArtistId("Imagine Dragons");
        ArrayList<Album> albums = SpotifyController.getAlbumDataFromArtist(artistId);
        
        if (albums.size() > 0)
        {
            System.out.println(albums.get(0).toString());
        }
        
    }
    
}
