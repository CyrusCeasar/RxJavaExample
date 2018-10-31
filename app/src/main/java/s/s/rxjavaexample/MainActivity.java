package s.s.rxjavaexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView rc = findViewById(R.id.rc);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rc.setLayoutManager(layoutManager);


        RxJavaPlugins.setOnObservableAssembly(new Function<Observable, Observable>() {
            @Override
            public Observable apply(Observable observable) throws Exception {
                logThreadInfo(getMethodName());
                Log.d(TAG, "observable assembled ");
                return observable;
            }
        });
        RxJavaPlugins.setOnObservableSubscribe(new BiFunction<Observable, Observer, Observer>() {
            @Override
            public Observer apply(Observable observable, Observer observer) throws Exception {
                logThreadInfo(getMethodName());
                Log.d(TAG, "observable subscribed");
                return observer;
            }
        });
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                logThreadInfo(getMethodName());
                Log.d(TAG, throwable.getMessage());
            }
        });
        RxJavaPlugins.setScheduleHandler(new Function<Runnable, Runnable>() {
            @Override
            public Runnable apply(Runnable runnable) throws Exception {
                logThreadInfo(getMethodName());
                Log.d(TAG, "runnable  handler");
                return runnable;
            }
        });


        final Disposable subscribe = Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                logThreadInfo(getMethodName());
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/file1/");
                File files[] = file.listFiles();
                if (files == null || files.length == 0) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Throwable("can't find any files, pls   check your file path"));
                    }
                    return;
                }
                for (File tmp : files) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(tmp);
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        }).filter(new Predicate<File>() {
            @Override
            public boolean test(File file) throws Exception {
                logThreadInfo(getMethodName());
                return file.getName().endsWith(".png");
            }
        }).map(new Function<File, Bitmap>() {
            @Override
            public Bitmap apply(File file) throws Exception {
                logThreadInfo(getMethodName());
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }).toList().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<List<Bitmap>>() {


            @Override
            public void accept(final List<Bitmap> bitmaps) throws Exception {
                logThreadInfo(getMethodName());
                rc.setAdapter(new RecyclerView.Adapter() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        ImageView imageView = new ImageView(MainActivity.this);
                        imageView.setLayoutParams(new RecyclerView.LayoutParams(-1, 500));
                        return new ImgViewHolder(imageView);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                        ((ImgViewHolder) viewHolder).mImageView.setImageBitmap(bitmaps.get(i));
                    }

                    @Override
                    public int getItemCount() {
                        return bitmaps.size();
                    }
                });
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                logThreadInfo(getMethodName());
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



    }

    static class ImgViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public ImgViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView;
        }
    }

    private void logThreadInfo(String tag) {
        Log.d(tag, Thread.currentThread().getName());
    }

    private String getMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[4];
        String methodName = e.getMethodName();
        return methodName;
    }

}
