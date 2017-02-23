package doortodoor.easyshot.database;

/**
 * Created by noble on 2017-01-14.
 */


/*
* Folder
* 각 Folder 들의 id와 이름을 짝지어 놓은 객체. FolderDatabaseManager에 의해 다뤄진다.
* id : folder의 id, PRIMARY로 정해지고
* folderName : folder의 이름
* */

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
