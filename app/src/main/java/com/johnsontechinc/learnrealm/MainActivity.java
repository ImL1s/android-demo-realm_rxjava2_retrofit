package com.johnsontechinc.learnrealm;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.johnsontechinc.learnrealm.model.UserRealm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testObservableSequence();
    }

    private void testObservableSequence() {
        Observable
                .create(e -> {
                    Log.d(this.getClass().getName(), "create");
                    e.onNext(new Object());
                    e.onComplete();
                })
                .doOnNext(o -> Log.d(this.getClass().getName(), "onNext"))
                .doOnComplete(() -> Log.d(this.getClass().getName(), "onComplete"))
                .subscribe(o -> Log.d(this.getClass().getName(), "subscribe"));

        // result: create -> onNext -> subscribe -> onComplete
    }
}
