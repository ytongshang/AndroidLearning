package cradle.rancune.learningandroid.jni;

import android.widget.TextView;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.BaseActivity;

/**
 * Created by Rancune@126.com 2018/9/7.
 */
public class JniTestActivity extends BaseActivity {

    private TextView mTvStringFromNative;
    private TextView mTvNativeAddNum;
    private TextView mTvNativeSetNormalField;
    private TextView mTvNativeSetStaticField;
    private final JniUtils mJniUtils = new JniUtils();

    static {
        System.loadLibrary("jniTest");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_jni_test);
        mTvStringFromNative = findViewById(R.id.tv_string_from_native);
        mTvNativeAddNum = findViewById(R.id.tv_native_addNum);
        mTvNativeSetNormalField = findViewById(R.id.tv_native_set_normal_field);
        mTvNativeSetStaticField = findViewById(R.id.tv_native_set_static_field);
    }

    @Override
    public void initData() {
        // string from native
        mTvStringFromNative.setText(mContext.getString(R.string.jni_getStringFromNative, JniUtils.getStringFromNative()));

        // native addNum
        mJniUtils.num = mJniUtils.addNum();
        mTvNativeAddNum.setText(mContext.getString(R.string.jni_addNum, mJniUtils.num));

        // native set normal field
        mJniUtils.setNormalField();
        mTvNativeSetNormalField.setText(getString(R.string.jni_setNormalField, mJniUtils.str));

        // native set static field
        JniUtils.setStaticField();
        mTvNativeSetStaticField.setText(getString(R.string.jni_setStaticField, JniUtils.staticStr));
    }
}
