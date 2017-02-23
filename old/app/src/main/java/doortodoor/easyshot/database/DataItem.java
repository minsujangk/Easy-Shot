package doortodoor.easyshot.database;

import java.util.ArrayList;

/**
 * Created by noble on 2017-01-14.
 */


/*
* DataItem
* URL 데이터를 담아두는 객체
* ImageDatabaseManager에서 SQL과 함께 관리된다.
* id : item의 id (PRIMARY로 정해짐)
* location : item의 스크린샷의 sdcard 내의 저장소
* columnName : item의 이름 (현재는 시간 데이터로 만듦)
* columnFolder : item이 속한 folder의 index (Folder DB는 Folder 객체와 FolderDatabaseManager에 의해 관리)
* columnTag : 태그, ArrayList<String>의 array 표현 방식으로 들어감. 미구현
* columnUrl : item의 url
* */
public class DataItem {
    public long id;
    public String location;
    public String columnName;

    public int columnFolder;
    public ArrayList<String> columnTag;
    public String columnUrl;

    public DataItem(long id, String columnName, int columnFolder, String location, ArrayList<String> columnTag, String columnUrl) {
        this.id = id;
        this.location = location;
        this.columnName = columnName;
        this.columnFolder = columnFolder;
        this.columnTag = columnTag;
        this.columnUrl = columnUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


    public int getColumnFolder() {
        return columnFolder;
    }

    public void setColumnFolder(int columnFolder) {
        this.columnFolder = columnFolder;
    }

    public ArrayList<String> getColumnTag() {
        return columnTag;
    }

    public void setColumnTag(ArrayList<String> columnTag) {
        this.columnTag = columnTag;
    }

    public String getColumnUrl() {
        return columnUrl;
    }

    public void setColumnUrl(String columnUrl) {
        this.columnUrl = columnUrl;
    }
}
