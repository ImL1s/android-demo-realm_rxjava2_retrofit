package com.johnsontechinc.learnrealm;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnsontechinc.learnrealm.model.User;
import com.johnsontechinc.learnrealm.model.UserRealm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class RxRealmActivity extends AppCompatActivity {

    public static final String TAG = "RxRealmActivity";

    private Realm realm;
    private ListView listView;
    private BaseAdapter adapter;
    private Button btn_load;
    private Button btn_clear;
    private ProgressBar pb_bar;
    private Looper realmLooper;
    private List<UserRealm> realmUserList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_rx);

        initUI();

        // init realm,create mock db data.
        Observable
                .create(e -> {
                    new Thread(() -> { // prepare a realm looper,use this looper to operate realm DB.
                        Looper.prepare();
                        realmLooper = Looper.myLooper();
                        Looper.loop();
                    }).start();

                    while (realmLooper == null) { // waiting looper created.
                        Thread.sleep(100);
                    }
                    e.onComplete();
                    Log.d(TAG, "***** onCompleted");

                })
                .doOnComplete(() -> getInitRealmObservable().doOnComplete(() -> getRefreshMockDataObservable().subscribe()).subscribe())
                .subscribe();


        // ensure realmLooper is not null.
        Observable.timer(3000, TimeUnit.MILLISECONDS).subscribe(aLong -> Log.d(TAG, realmLooper.toString()));

    }

    /**
     * init UI ref, listen
     */
    private void initUI() {
        btn_load = (Button) findViewById(R.id.btn_load);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        listView = (ListView) findViewById(R.id.lv_display);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);

        adapter = new InnerAdapter();
        listView.setAdapter(adapter);

        btn_load.setOnClickListener(v -> loadDataFromRealm());

        btn_clear.setOnClickListener(v -> clearData());

        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            clearData();
            getRefreshMockDataObservable();
        });
    }

    private void setProgressBar(boolean enable) {
        pb_bar.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * init realm and inset some mock data.
     *
     * @return
     */
    private Observable getInitRealmObservable() {
        return
                Observable
                        .create(e -> {
                            Realm.init(getApplicationContext());
                            RealmConfiguration configuration = new RealmConfiguration.Builder().name("a").build();
                            realm = Realm.getInstance(configuration);
                            e.onComplete();
                        }).subscribeOn(AndroidSchedulers.from(realmLooper));
    }

    /**
     * create mock data.
     */
    private Observable getRefreshMockDataObservable() {

        return
                Observable
                        .create((ObservableOnSubscribe<Integer>) e -> {
                            realm.beginTransaction();
                            realm.delete(UserRealm.class);

                            if (e.isDisposed()) {
                                e.onComplete();
                                return;
                            }

                            for (int i = 0; i < 10000; i++) {
                                e.onNext(i);
                            }
                            e.onComplete();
                        })
                        .subscribeOn(AndroidSchedulers.from(realmLooper))
                        .observeOn(AndroidSchedulers.from(realmLooper))
                        .doOnComplete(() -> realm.commitTransaction())
                        .doOnNext(i -> {
                            UserRealm user = realm.createObject(UserRealm.class, i);
                            UUID uuid = UUID.randomUUID();
                            user.setName(uuid.toString().substring(0, 5));
                            user.setAge(new Random().nextInt(100));

                        });
    }


    /**
     * query from realm and display the query completed data to listView.
     */
    private void loadDataFromRealm() {

        setProgressBar(true);

        Observable.create(e -> {
            realmUserList.clear();
            RealmResults<UserRealm> realmResults = realm.where(UserRealm.class)
                    .greaterThan("age", 10)
                    .beginGroup()
                    .equalTo("age", 11)
                    .or()
                    .equalTo("age", 12)
                    .endGroup()
                    .findAll()
                    .sort("age", Sort.DESCENDING);

            for (int i = 0; i < realmResults.size(); i++) {
                if (e.isDisposed()) break;
                e.onNext(realmResults.get(i));
            }
            e.onComplete();
        })
                .subscribeOn(AndroidSchedulers.from(realmLooper))
                .observeOn(AndroidSchedulers.from(realmLooper))
                .doOnNext(realmUser -> {
                    realmUserList.add((UserRealm) realmUser);
                    userList.add(new User((UserRealm) realmUser));
                })
                .doOnComplete(() -> runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();

                }))
                .subscribe(x->runOnUiThread(()->setProgressBar(false)));
    }

    /**
     * clear memory data.
     */
    private void clearData() {
        userList.clear();
        adapter.notifyDataSetChanged();
    }

    class InnerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return userList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;

            if (convertView == null)
                tv = new TextView(RxRealmActivity.this);
            else
                tv = (TextView) convertView;
            User user = (User) getItem(position);
            tv.setText(String.format(Locale.CHINESE, "ID:%d Name:%s Age:%d", user.getId(), user.getName(), user.getAge()));

            return tv;
        }
    }
}
