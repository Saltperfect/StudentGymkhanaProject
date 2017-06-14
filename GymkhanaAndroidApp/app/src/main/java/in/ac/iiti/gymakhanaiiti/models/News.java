package in.ac.iiti.gymakhanaiiti.models;

/**
 * Created by Ankit Gaur on 1/18/2017.
 */

public class News {
    private String title,description,timestamp,imageUrl;
    public News(){

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
