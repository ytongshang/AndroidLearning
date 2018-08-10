package cradle.rancune.learningandroid.record.video;

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
public class MediaProjectionHelper extends MediaProjection.Callback {

    public static Intent requestMediaProjection(Context context) {
        Preconditions.checkNotNull(context);
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager == null) {
            return null;
        }
        Intent intent = manager.createScreenCaptureIntent();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0) {
            return intent;
        }
        return null;
    }

    public static MediaProjection resolveMediaProjection(Context context, int resultCode, Intent data) {
        Preconditions.checkNotNull(context);
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager == null) {
            return null;
        }
        return manager.getMediaProjection(resultCode, data);
    }
}
