package group.imagestudio9000;

public class FbfsImage {
    private String downloadUrl;
    private String imageUrl;
    private String filename;

    public FbfsImage() {
        // Default constructor required for Firestore
    }

    public FbfsImage(String downloadUrl, String imageUrl, String filename) {
        this.downloadUrl = downloadUrl;
        this.imageUrl = imageUrl;
        this.filename = filename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
