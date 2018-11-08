package spotifyplayer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TrackForTableView{
    private SimpleIntegerProperty trackNumber;
    private SimpleStringProperty trackTitle;
    private SimpleIntegerProperty trackDurationInSeconds;
    private SimpleStringProperty trackPreviewUrl;
    
    public TrackForTableView()
    {
        trackNumber = new SimpleIntegerProperty();
        trackTitle = new SimpleStringProperty();
        trackDurationInSeconds = new SimpleIntegerProperty();
        trackPreviewUrl = new SimpleStringProperty();
    }
    
    public int getTrackNumber() {
        return trackNumber.getValue();
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber.set(trackNumber);
    }

    public String getTrackTitle() {
        return trackTitle.getValue();
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle.set(trackTitle);
    }
    
    public int getTrackDurationInSeconds() {
        return trackDurationInSeconds.getValue();
    }

    public void setTrackDurationInSeconds(int trackNumber) {
        this.trackDurationInSeconds.set(trackNumber);
    }

    public String getTrackPreviewUrl()
    {
        return trackPreviewUrl.get();
    }
    
    public void setTrackPreviewUrl(String trackPreviewUrl)
    {
        this.trackPreviewUrl.set(trackPreviewUrl);
    }    
}
