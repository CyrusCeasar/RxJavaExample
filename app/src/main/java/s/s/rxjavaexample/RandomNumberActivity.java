package s.s.rxjavaexample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ChenLei on 2018/10/7 0007.
 */
public class RandomNumberActivity extends Activity {

    private static final String TAG = RandomNumberActivity.class.getSimpleName();

    private Random mRandom = new Random();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(150);
        setContentView(textView);


        Disposable subscribe = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                while (!emitter.isCancelled()) {
                    int num = mRandom.nextInt(100);
                    emitter.onNext(num);
                    Log.d(TAG, "generate number " + num);
                    Thread.sleep(mRandom.nextInt(1000));
                }
            }
        }, BackpressureStrategy.LATEST).throttleLast(2, TimeUnit.SECONDS).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer price) throws Exception {
                textView.setText(price + "");
            }
        });
        Disposable subscribe1 = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                insertDb();
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                Toast.makeText(RandomNumberActivity.this, "database insert success", Toast.LENGTH_LONG).show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, throwable.getMessage());
            }
        });


    }

    private void insertDb() {
    }

}
