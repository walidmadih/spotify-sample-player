package spotifyplayer;

import java.util.ArrayList;

public class Album {
    private String artistName;
    private String albumName;
    private String imageURL;
    private ArrayList<Track> tracks;
    
    public Album(String artistName, String albumName, String imageURL, ArrayList<Track> tracks)
    {
        this.artistName = artistName;
        this.albumName = albumName;
        this.imageURL = imageURL;
        this.tracks = tracks;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
    
    public String toString()
    {
        String output = artistName+" \nTitle: "+ albumName + "\n\nTracks:\n";
        
        if (tracks != null && tracks.size() > 0)
        {
            for (int i=0; i<tracks.size(); ++i)
            {
                Track track = tracks.get(i);
                int duration = track.getDurationInSeconds();
                output += track.getTitle() + " [" + (duration / 60) + ":" + (duration % 60) + "]";
                
                if (track.getUrl().equals("") == false)
                {
                    output += " [preview available]\n";
                }
                else
                {
                    output += "\n";
                }
                
            }            
        }
        
        return output;
    }
}
