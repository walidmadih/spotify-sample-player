package spotifyplayer;

public class Track {
    private int number;
    private String title;
    private int durationInSeconds;
    private String url;

    public Track(int number, String title, int durationInSeconds, String url)
    {
        this.number = number;
        this.title = title;
        this.durationInSeconds = durationInSeconds;
        this.url = url;
    }
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrlL(String url) {
        this.url = url;
    }

    
}
