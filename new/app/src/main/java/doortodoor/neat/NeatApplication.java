package doortodoor.neat;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by noble on 2017-02-10.
 */

/*
*  NeatApplication extends Application
*  기본 Realm DB 정보 입력
* */

public class NeatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        // The RealmConfiguration is created using the builder pattern.
// The Realm file will be located in Context.getFilesDir() with name "myrealm.realm"
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .schemaVersion(45)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }
}
