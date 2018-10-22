package spotifyparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;


public class SpotifyController{
    final static private String SPOTIFY_CLIENT_ID     = "a392f4c72f1b469cb2e38ce87aec6e7b";
    final static private String SPOTIFY_CLIENT_SECRET = "349660472b6c4aafa949bbb90e104e4c";
    
    public static String getArtistId(String artistNameQuery)
    {
        String artistId = "";
        
        try
        {
            // TODO - From an artist string, get the spotify ID
            // Recommended Endpoint: https://api.spotify.com/v1/search
            // Parse the JSON output to retrieve the ID
        
            String endpoint = "https://api.spotify.com/v1/search";
            String params = "type=artist&q=" + artistNameQuery;
            String jsonOutput = spotifyEndpointToJson(endpoint, params);
            
            JsonObject root = new JsonParser().parse(jsonOutput).getAsJsonObject();
            
            JsonObject artists = root.get("artists").getAsJsonObject();
            JsonArray items = artists.get("items").getAsJsonArray();
            
            if(items.size() > 0){
                JsonObject item = items.get(0).getAsJsonObject();
                artistId = item.get("id").getAsString();
                
            }
            
            // replace this string...
            artistId = "3WrFJ7ztbogyGnTHbHJFl2";
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return artistId;
    }
    
    public static ArrayList<String> getAlbumIdsFromArtist(String artistId)
    {
        ArrayList<String> albumIds = new ArrayList<>();
        
        // TODO - Retrieve album ids from an artist id
        // Recommended endpoint (id is the id of the artist): 
        //             https://api.spotify.com/v1/artists/{id}/albums
        //
        // Arguments - You can filter for the CA market, and limit search to 50 albums
        
        String endpoint = "https://api.spotify.com/v1/artists/" + artistId + "/albums";
        String params = "market=CA&limit=50";
        String jsonOutput = spotifyEndpointToJson(endpoint, params);
        
        JsonObject root = new JsonParser().parse(jsonOutput).getAsJsonObject();
        JsonArray items = root.get("items").getAsJsonArray();
        
        for(int x = 0; x < items.size(); x++){
            JsonObject item = items.get(x).getAsJsonObject();
            albumIds.add(item.get("id").getAsString());
            x++;
        }     
        
        return albumIds;
    }
    
    public static ArrayList<Album> getAlbumDataFromArtist(String artistId)
    {
        ArrayList<String> albumIds = getAlbumIdsFromArtist(artistId);
        ArrayList<Album> albums = new ArrayList<>();
        
        // TODO - Retrieve all album data from the list of album ids for an artist
        // 
        // You can have a look at the Album class included
        // 
        // Recommended endpoint (id is the id of the album): 
        //             https://api.spotify.com/v1/albums/{id}
        //
        // Arguments - You can filter for the CA market
        
        for(String albumId : albumIds)
        {
            String endpoint = "https://api.spotify.com/v1/albums/" + albumId;
            String params = "market=CA";
            String jsonOutput = spotifyEndpointToJson(endpoint, params);
            
            JsonObject root = new JsonParser().parse(jsonOutput).getAsJsonObject();
            JsonArray artistsArray = root.get("artists").getAsJsonArray();
            JsonObject artists = artistsArray.get(0).getAsJsonObject();
            JsonArray images = root.getAsJsonArray("images").getAsJsonArray();
            JsonObject tracks = root.get("tracks").getAsJsonObject();
            JsonArray items = tracks.get("items").getAsJsonArray();
            
            
            String artistName = artists.get("name").getAsString();
            String albumName = root.get("name").getAsString();
            String coverURL = images.get(0).getAsJsonObject().get("url").getAsString();;
            
            ArrayList<String> trackTitles = new ArrayList<>();
            ArrayList<Integer> trackLengths = new ArrayList<>();
            
            for(int x =0; x < items.size(); x++){
                JsonObject temporaryArtists = items.get(x).getAsJsonObject();
                trackTitles.add(temporaryArtists.getAsJsonObject().get("name").getAsString());
                trackLengths.add(Integer.parseInt(temporaryArtists.getAsJsonObject().get("duration_ms").getAsString())/1000);
            }
            
            albums.add(new Album(artistName, albumName, coverURL, trackTitles, trackLengths));                
        }
        
        return albums;
    }


    // This code will help you retrieve the JSON data from a spotify end point
    // It takes care of the complicated parts such as the authentication and 
    // connection to the Web API
    // 
    // You shouldn't have to modify any of the code...
    private static String spotifyEndpointToJson(String endpoint, String params)
    {
        params = params.replace(' ', '+');

        try
        {
            String fullURL = endpoint;
            if (params.isEmpty() == false)
            {
                fullURL += "?"+params;
            }
            
            URL requestURL = new URL(fullURL);
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String bearerAuth = "Bearer " + getSpotifyAccessToken();
            connection.setRequestProperty ("Authorization", bearerAuth);
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
                jsonOutput += inputLine;
            }
            in.close();
            
            return jsonOutput;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return "";
    }


    // This implements the Client Credentials Authorization Flows
    // Based on the Spotify API documentation
    // 
    // It retrieves the Access Token based on the client ID and client Secret  
    //
    // You shouldn't have to modify any of this code...          
    private static String getSpotifyAccessToken()
    {
        try
        {
            URL requestURL = new URL("https://accounts.spotify.com/api/token");
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String keys = SPOTIFY_CLIENT_ID+":"+SPOTIFY_CLIENT_SECRET;
            String postData = "grant_type=client_credentials";
            
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(keys.getBytes()));
            
            // Send header parameter
            connection.setRequestProperty ("Authorization", basicAuth);
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Send body parameters
            OutputStream os = connection.getOutputStream();
            os.write( postData.getBytes() );
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
                jsonOutput += inputLine;
            }
            in.close();
            
            JsonElement jelement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jelement.getAsJsonObject();
            String token = rootObject.get("access_token").getAsString();

            return token;
        }
        catch(Exception e)
        {
            System.out.println("Something wrong here... make sure you set your Client ID and Client Secret properly!");
            e.printStackTrace();
        }
        
        return "";
    }
}
