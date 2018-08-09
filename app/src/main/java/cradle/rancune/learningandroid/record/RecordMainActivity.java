package cradle.rancune.learningandroid.record;

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

import cradle.rancune.learningandroid.BaseActivity;
import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.record.ui.AudioRecordActivity;
import cradle.rancune.learningandroid.record.ui.ScreenRecorderActivity;

public class RecordMainActivity extends BaseActivity {

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
        Page audio = new Page();
        audio.title = R.string.activity_record1;
        audio.intent = new Intent(mContext, AudioRecordActivity.class);
        mPages.add(audio);

        Page screen = new Page();
        screen.title = R.string.activity_record2;
        screen.intent = new Intent(mContext, ScreenRecorderActivity.class);
        mPages.add(screen);

        mAdapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(RecordMainActivity.this).inflate(R.layout.reycle_item_main_page, parent, false);
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
                startActivity(mPage.intent);
            }
        }
    }

    private static final class Page {

        @StringRes
        int title;
        Intent intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
