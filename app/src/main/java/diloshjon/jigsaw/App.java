package diloshjon.jigsaw;

import android.app.Application;
import android.os.Handler;

public class App extends Application {
    private static Handler sHandler = new Handler();
    public static Handler getMainHandler() {
        return sHandler;
    }
}
