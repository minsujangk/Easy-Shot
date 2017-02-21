package doortodoor.neat;

import io.realm.RealmObject;

/**
 * Created by noble on 2017-02-10.
 */

/*
* url 데이터를 담아두는 오브젝트
* */

public class LinkData extends RealmObject {
    private int id;
    private String url;
    private String folder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
