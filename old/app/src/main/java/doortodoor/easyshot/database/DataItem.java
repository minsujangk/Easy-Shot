package doortodoor.easyshot.database;

import java.util.ArrayList;

/**
 * Created by noble on 2017-01-14.
 */

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
