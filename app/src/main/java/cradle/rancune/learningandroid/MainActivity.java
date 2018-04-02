package cradle.rancune.learningandroid;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cradle.rancune.learningandroid.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private static final class Page {
        int titleRes;
        Class<? extends Activity> target;
    }
}
