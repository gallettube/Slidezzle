package diloshjon.jigsaw.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

public class ResUtils {
    public static Uri getUriOfResource(@NonNull Context context, @AnyRes int resId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(resId)
                + '/' + context.getResources().getResourceTypeName(resId)
                + '/' + context.getResources().getResourceEntryName(resId));
    }
}
