# android-demo-realm_rxjava2_retrofit

show how to use rxjava2 + relm + retrofit


## Preview
---

    Observable
        .create(e -> {
            Realm.init(getApplicationContext());
            RealmConfiguration configuration = new RealmConfiguration.Builder().name("a").build();
            realm = Realm.getInstance(configuration);
            e.onComplete();
        }).subscribeOn(AndroidSchedulers.from(realmLooper));
