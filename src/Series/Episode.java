package Series;

public class Episode {
    public int EpisodeNumber;
    public String Name;
    public boolean German;

    public Episode(int number, String Name, boolean german){
        this.EpisodeNumber = number;
        this.Name = Name;
        this.German = german;
    }

    @Override
    public String toString() {
        return  EpisodeNumber + " - " + Name;
    }
}
