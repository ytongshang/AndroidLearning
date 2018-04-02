package cradle.rancune.learningandroid;

import android.annotation.StringRes;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cradle.rancune.learningandroid.aidl.client.BookManagerClientActivity;
import cradle.rancune.learningandroid.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private final List<Page> mPages = new ArrayList<>();

    @Override
    public void initData() {
        Page aidl = new Page();
        aidl.title = R.string.activity_aidl;
        aidl.target = BookManagerClientActivity.class;
        mPages.add(aidl);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new Adapter());
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
