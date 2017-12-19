package srx.awesome.code.security.dto;

public class FileInfo {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileInfo(String path) {
        this.path = path;
    }
}
