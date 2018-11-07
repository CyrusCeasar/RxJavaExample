package s.s.rxjavaexample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by ChenLei on 2018/11/2 0002.
 */
public class LoginActivity extends Activity{
    int count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etAccount = findViewById(R.id.et_account);
        EditText etPwd = findViewById(R.id.et_pwd);
        EditText etSearh = findViewById(R.id.et_search);
        final Button btnLogin = findViewById(R.id.btn_login);
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        Observable accountObservable  = RxTextView.textChanges(etAccount);
        Observable pwdObservable = RxTextView.textChanges(etPwd);

        Observable.combineLatest(accountObservable, pwdObservable, new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) throws Exception {
                Log.d("test","handled");
                return !TextUtils.isEmpty(o.toString()) && !TextUtils.isEmpty(o2.toString());
            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                if((Boolean) o){
                    btnLogin.setBackgroundColor(Color.RED);
                    btnLogin.setEnabled(true);
                }else{
                    btnLogin.setBackgroundColor(Color.GRAY);
                    btnLogin.setEnabled(false);
                }

            }
        });

        RxView.clicks(btnLogin).throttleFirst(2,TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Toast.makeText(LoginActivity.this, "登录成功！"+count ,Toast.LENGTH_SHORT).show();
                count++;
            }
        });

        RxTextView.textChanges(etSearh).throttleLast(2,TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                Toast.makeText(LoginActivity.this, charSequence ,Toast.LENGTH_SHORT).show();
            }
        });


    }
}
