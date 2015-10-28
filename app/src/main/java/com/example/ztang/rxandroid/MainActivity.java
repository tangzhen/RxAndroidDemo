package com.example.ztang.rxandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = (Button) findViewById(R.id.start_button_one);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.
                Observable<String> myObservable = Observable.create(
                        new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> sub) {
                                sub.onNext("Hello, world!");
                                sub.onCompleted();
                            }
                        }
                );

                Subscriber<String> mySubscriber = new Subscriber<String>() {
                    @Override
                    public void onNext(String s) { System.out.println(s); }

                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }
                };

                myObservable.subscribe(mySubscriber);

                // 2.
                Observable<String> secondObservable =
                        Observable.just("Second Hello, world!");

                Action1<String> onNextAction = new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                };

                secondObservable.subscribe(onNextAction);

                // 3.
                Observable.just("third Hello, world!")
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                System.out.println(s);
                            }
                        });

                // Java 8
                // Observable.just("Hello, world!").subscribe(s -> System.out.println(s));

                // 4.
                Observable.just("Hello, world!")
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return s + " -Dan";
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                System.out.println(s);
                            }
                        });
                // Java 8
                // Observable.just("Hello, world!")
                //      .map(s -> s + " -Dan")
                //      .subscribe(s -> System.out.println(s));

                // 5.
                Observable.just("Hello, world!")
                        .map(new Func1<String, Integer>() {
                            @Override
                            public Integer call(String s) {
                                return s.hashCode();
                            }
                        })
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer i) {
                                System.out.println(Integer.toString(i));
                            }
                        });
                // Java 8
                // Observable.just("Hello, world!")
                //      .map(s -> s.hashCode())
                //      .subscribe(i -> System.out.println(Integer.toString(i)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
