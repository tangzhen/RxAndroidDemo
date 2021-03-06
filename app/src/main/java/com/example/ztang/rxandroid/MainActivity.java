package com.example.ztang.rxandroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.util.Arrays.asList;

public class MainActivity extends RxAppCompatActivity {
    GithubService githubService;

    private static final String TAG = "RxLifecycle";

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");

        // Using automatic unsubscription, this should determine that the correct time to
        // unsubscribe is onStop (the opposite of onStart).
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "Unsubscribing subscription from onStart()");
                    }
                })
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Log.i(TAG, "Started in onStart(), running until in onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        // `this.<Long>` is necessary if you're compiling on JDK7 or below.
        //
        // If you're using JDK8+, then you can safely remove it.
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "Unsubscribing subscription from onResume()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Log.i(TAG, "Started in onResume(), running until in onDestroy(): " + num);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");
    }

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
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
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

        Button buttonTwo = (Button) findViewById(R.id.start_button_two);
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.
                query("Hello, world!")
                        .subscribe(new Action1<List<String>>() {
                            @Override
                            public void call(List<String> urls) {
                                for (String url : urls) {
                                    System.out.println(url);
                                }
                            }
                        });

                String[] urls = {"url1", "url2", "url3"};
                Observable.from(urls)
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String url) {
                                System.out.println(url);
                            }
                        });

                // 2.
                query("Hello, world!")
                        .flatMap(new Func1<List<String>, Observable<String>>() {
                            @Override
                            public Observable<String> call(List<String> urls) {
                                return Observable.from(urls);
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                System.out.println(s);
                            }
                        });

                // Java 8
                // query("Hello, world!")
                //       .flatMap(urls -> Observable.from(urls))
                //       .subscribe(url -> System.out.println(url));


                // 3.
                query("Hello, world!")
                        .flatMap(new Func1<List<String>, Observable<String>>() {
                            @Override
                            public Observable<String> call(List<String> urls) {
                                return Observable.from(urls);
                            }
                        })
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String url) {
                                return getTitle(url);
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                System.out.println(s);
                            }
                        });

                // 4.
                query("Hello, world!")
                        .flatMap(new Func1<List<String>, Observable<String>>() {
                            @Override
                            public Observable<String> call(List<String> urls) {
                                return Observable.from(urls);
                            }
                        })
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String url) {
                                return getTitle(url);
                            }
                        })
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String title) {
                                return title != null;
                            }
                        })
                        .take(2)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String title) {
                                saveTitle(title);
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                System.out.println(s);
                            }
                        });
            }
        });

        Button buttonThree = (Button) findViewById(R.id.start_button_three);
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.
                Subscription subscribe = Observable.just("Hello, world!")
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return potentialException(s);
                            }
                        })
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return anotherPotentialException(s);
                            }
                        })
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onNext(String s) {
                                System.out.println(s);
                            }

                            @Override
                            public void onCompleted() {
                                System.out.println("Completed!");
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println("Ouch!");
                            }
                        });

                // 2.
                // myObservableServices.retrieveImage(url)
                //        .subscribeOn(Schedulers.io())
                //        .observeOn(AndroidSchedulers.mainThread())
                //        .subscribe(bitmap -> myImageView.setImageBitmap(bitmap));

                subscribe.unsubscribe();
                System.out.println("Unsubscribed=" + subscribe.isUnsubscribed());
            }
        });

        final ImageView myImageView = (ImageView) findViewById(R.id.image_show);

        Button buttonAndroid = (Button)findViewById(R.id.start_button_android);
        buttonAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                githubService.getImage("")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                myImageView.setImageBitmap(bitmap);
                            }
                        });


            }
        });

        Log.d(TAG, "onCreate()");
        // Specifically bind this until onPause()
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "Unsubscribing subscription from onCreate()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Log.i(TAG, "Started in onCreate(), running until onPause(): " + num);
                    }
                });
    }

    private String anotherPotentialException(String s) {
        if (true) {
            throw new RuntimeException("big boom");
        } else {
            return "BBB";
        }
    }

    private String potentialException(String s) {
        return "ABC";
    }

    private void saveTitle(String title) {
        System.out.println("save title");
    }

    Observable<List<String>> query(String text) {
        return Observable.create(
                new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {
                        List<String> strings = asList("foo", "bar", "baz");

                        subscriber.onNext(strings);
                        subscriber.onCompleted();
                    }
                }
        );
    }

    Observable<String> getTitle(String URL) {
        return Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Title");
                        subscriber.onCompleted();
                    }
                }
        );
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
