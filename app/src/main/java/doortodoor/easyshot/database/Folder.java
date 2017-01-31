package doortodoor.easyshot.database;

/**
 * Created by noble on 2017-01-14.
 */

public class Folder {
    public long id;
    public String folderName;

    public Folder(int id, String columnFolderName) {
        this.id = id;
        this.folderName = columnFolderName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
