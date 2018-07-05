package cradle.rancune.learningandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Rancune@126.com 2018/4/1.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        initData();
    }

    public abstract void initView();

    public abstract void initData();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


}
