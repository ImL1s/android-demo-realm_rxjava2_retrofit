package com.johnsontechinc.learnrealm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnsontechinc.learnrealm.model.UserRealm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmActivity extends AppCompatActivity {
    private Realm realm;
    private ListView listView;
    private List<UserRealm> userList = new ArrayList<>();
    private BaseAdapter adapter;
    private Button btn_load;
    private Button btn_clear;
    private Button btn_ed;
    private EditText ed_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm);
        initRealm();

        btn_load = (Button) findViewById(R.id.btn_load);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_ed = (Button) findViewById(R.id.btn_ed);
        listView = (ListView) findViewById(R.id.lv_display);
        ed_id = (EditText) findViewById(R.id.ed_id);

        adapter = new BaseAdapter() {
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
                    tv = new TextView(RealmActivity.this);
                else
                    tv = (TextView) convertView;
                UserRealm user = (UserRealm) getItem(position);
                tv.setText(String.format(Locale.CHINESE, "ID:%d Name:%s Age:%d", user.getId(), user.getName(), user.getAge()));

                return tv;
            }
        };

        listView.setAdapter(adapter);

        refreshMockData();

        btn_load.setOnClickListener(v -> loadDataFromRealm());

        btn_ed.setOnClickListener(v -> {
            int id = Integer.parseInt(ed_id.getText().toString().trim());
            for (UserRealm user : userList) {
                if (user.getId() == id) {
                    realm.beginTransaction();
                    user.setName("HelloWorld");
                    realm.commitTransaction();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(RealmActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        btn_clear.setOnClickListener(v -> clearData());

        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            clearData();
            refreshMockData();
        });

    }

    private void initRealm() {
        Realm.init(getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("a").build();
        realm = Realm.getInstance(configuration);
    }

    /**
     * init realm and inset some mock data.
     */
    private void refreshMockData() {

        realm.beginTransaction();
        realm.delete(UserRealm.class);

        for (int i = 0; i < 10000; i++) {
            UserRealm user = realm.createObject(UserRealm.class, i);
            UUID uuid = UUID.randomUUID();
            user.setName(uuid.toString().substring(0, 5));
            user.setAge(new Random().nextInt(100));
        }

        realm.commitTransaction();
    }


    /**
     * query from realm and display the query completed data to listView.
     */
    private void loadDataFromRealm() {
        userList.clear();
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
            userList.add(realmResults.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * clear memory data.
     */
    private void clearData() {
        userList.clear();
        adapter.notifyDataSetChanged();
    }
}
