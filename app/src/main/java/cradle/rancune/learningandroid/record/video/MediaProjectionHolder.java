package cradle.rancune.learningandroid.record.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;

import cradle.rancune.commons.base.Preconditions;

/**
 * Created by Rancune@126.com 2018/8/9.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MediaProjectionHolder extends MediaProjection.Callback {
    private static final int REQUEST_CODE_CAPTURE = 10000;

    private static MediaProjectionHolder INSTANCE = new MediaProjectionHolder();

    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;

    public static MediaProjectionHolder getInstance() {
        return INSTANCE;
    }

    public MediaProjection getMediaProjection() {
        return mMediaProjection;
    }

    public boolean requestMediaProjection(Activity activity) {
        Preconditions.checkNotNull(activity);
        if (mProjectionManager == null) {
            mProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        if (mProjectionManager == null) {
            return false;
        }
        Intent intent = mProjectionManager.createScreenCaptureIntent();
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0) {
            activity.startActivityForResult(intent, REQUEST_CODE_CAPTURE);
            return true;
        }
        return false;
    }

    public boolean resolveMediaProjection(int resultCode, Intent data) {
        if (mProjectionManager == null) {
            return false;
        }
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        if (mMediaProjection == null) {
            return false;
        }
        setMediaProjection(mMediaProjection);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(this);
            mMediaProjection = null;
        }
    }

    private void setMediaProjection(MediaProjection mediaProjection) {
        Preconditions.checkNotNull(mediaProjection);
        mMediaProjection = mediaProjection;
        mMediaProjection.registerCallback(this, null);
    }
}
