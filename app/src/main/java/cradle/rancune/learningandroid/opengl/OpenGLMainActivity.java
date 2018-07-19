package cradle.rancune.learningandroid.opengl;

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
import cradle.rancune.learningandroid.opengl.ui.OpenGLBasisActivity;
import cradle.rancune.learningandroid.opengl.ui.OpenGLCameraActivity;
import cradle.rancune.learningandroid.opengl.ui.OpenGLFboActivity;
import cradle.rancune.learningandroid.opengl.ui.OpenGLSaturationActivity;

public class OpenGLMainActivity extends BaseActivity {

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
        for (int i = 1; i < 8; i++) {
            Page page = new Page();
            String identifier = "activity_opengl" + i;
            page.title = mContext.getResources().getIdentifier(identifier, "string", mContext.getPackageName());
            page.intent = OpenGLBasisActivity.RendererOf(mContext, i);
            mPages.add(page);
        }

        Page page = new Page();
        page.title = R.string.activity_opengl8;
        page.intent = new Intent(mContext, OpenGLCameraActivity.class);
        mPages.add(page);

        Page saturation = new Page();
        saturation.title = R.string.activity_opengl9;
        saturation.intent = new Intent(mContext, OpenGLSaturationActivity.class);
        mPages.add(saturation);

        Page fbo = new Page();
        fbo.title = R.string.activity_opengl10;
        fbo.intent = new Intent(mContext, OpenGLFboActivity.class);
        mPages.add(fbo);

        mAdapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(OpenGLMainActivity.this).inflate(R.layout.reycle_item_main_page, parent, false);
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
}
