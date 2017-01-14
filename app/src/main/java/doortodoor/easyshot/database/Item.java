package doortodoor.easyshot.database;

import java.util.ArrayList;

/**
 * Created by noble on 2017-01-14.
 */

public class Item {
    public int id;
    public String location;
    public String columnName;
    public int columnPrice;
    public ArrayList<String> columnTag;
    public String columnUrl;

    public Item(int id, String location, String columnName, int columnPrice, ArrayList<String> columnTag, String columnUrl) {
        this.id = id;
        this.location = location;
        this.columnName = columnName;
        this.columnPrice = columnPrice;
        this.columnTag = columnTag;
        this.columnUrl = columnUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getColumnPrice() {
        return columnPrice;
    }

    public void setColumnPrice(int columnPrice) {
        this.columnPrice = columnPrice;
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
