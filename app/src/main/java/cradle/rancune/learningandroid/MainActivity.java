package cradle.rancune.learningandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cradle.rancune.learningandroid.aidl.client.BookManagerClientActivity;
import cradle.rancune.learningandroid.opengl.ui.OpenGL01Activity;
import cradle.rancune.learningandroid.opengl.ui.OpenGL02Activity;
import cradle.rancune.learningandroid.opengl.ui.OpenGL03Activity;
import cradle.rancune.learningandroid.opengl.ui.OpenGL04Activity;
import cradle.rancune.learningandroid.opengl.ui.OpenGL05Activity;

public class MainActivity extends BaseActivity {

    private Adapter mAdapter;
    private final List<Page> mPages = new ArrayList<>();

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        Page aidl = new Page();
        aidl.title = R.string.activity_aidl;
        aidl.target = BookManagerClientActivity.class;
        mPages.add(aidl);

        Page opengl01 = new Page();
        opengl01.title = R.string.activity_opengl01;
        opengl01.target = OpenGL01Activity.class;
        mPages.add(opengl01);

        Page opengl02 = new Page();
        opengl02.title = R.string.activity_opengl02;
        opengl02.target = OpenGL02Activity.class;
        mPages.add(opengl02);

        Page opengl03 = new Page();
        opengl03.title = R.string.activity_opengl03;
        opengl03.target = OpenGL03Activity.class;
        mPages.add(opengl03);

        Page opengl04 = new Page();
        opengl04.title = R.string.activity_opengl04;
        opengl04.target = OpenGL04Activity.class;
        mPages.add(opengl04);

        Page opengl05 = new Page();
        opengl05.title = R.string.activity_opengl05;
        opengl05.target = OpenGL05Activity.class;
        mPages.add(opengl05);

        mAdapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.reycle_item_main_page, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Page page = mPages.get(position);
            holder.bindView(page);
        }

        @Override
        public int getItemCount() {
            return mPages.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;
        Page mPage;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_page_name);
            itemView.setOnClickListener(this);
        }

        void bindView(Page page) {
            mTextView.setText(page.title);
            mPage = page;
        }

        @Override
        public void onClick(View v) {
            if (mPage != null) {
                Intent intent = new Intent(MainActivity.this, mPage.target);
                startActivity(intent);
            }
        }
    }

    private static final class Page {

        @StringRes
        int title;
        Class<? extends Activity> target;
        Intent intent;
    }
}
