package diloshjon.jigsaw.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import diloshjon.jigsaw.App;
import diloshjon.jigsaw.R;
import diloshjon.jigsaw.fragment.GameFragment;
import diloshjon.jigsaw.fragment.WinFragment;
import diloshjon.jigsaw.util.IOUtils;
import diloshjon.jigsaw.util.L;
import diloshjon.jigsaw.util.UIUtils;
import me.drakeet.mailotto.Mail;
import me.drakeet.mailotto.Mailbox;
import me.drakeet.mailotto.OnMailReceived;

import static diloshjon.jigsaw.R.id.level_spinner;
import static diloshjon.jigsaw.R.string.restart;

/**
 * Created by jon galletero on 2019.
 */
public class GameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private static final String TAG = "GameActivity";
    public static Spinner levelSpinner;
    public static int level = 1;
    public static int SPAN_COUNT_1 = 3;
    public static int SPAN_COUNT_2 = 4;
    public static int SPAN_COUNT_3 = 5;
    public static int SPAN_COUNT_4 = 6;
    public static int SPAN_COUNT_5 = 7;
    public static int SPAN_COUNT_6 = 8;
    public static int SPAN_COUNT_7 = 9;
    public static int BLANK_BRICK_1 = 8;
    public static int BLANK_BRICK_2 = 15;
    public static int BLANK_BRICK_3 = 24;
    public static int BLANK_BRICK_4 = 35;
    public static int BLANK_BRICK_5 = 48;
    public static int BLANK_BRICK_6 = 63;
    public static int BLANK_BRICK_7 = 80;

    public static final int[][] GOAL_STATUS_1 =
            {   {0, 1, 2},
                {3, 4, 5},
                {6, 7, BLANK_BRICK_1}
            };
    public static final int[][] GOAL_STATUS_2 =
            {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                { 8, 9, 10, 11},
                {12, 13, 14, BLANK_BRICK_2}
            };
    public static final int[][] GOAL_STATUS_3 =
            {
                {0, 1, 2, 3, 4},
                {5, 6, 7, 8, 9},
                {10, 11, 12, 13, 14},
                {15, 16, 17, 18, 19},
                {20, 21, 22, 23, BLANK_BRICK_3}
            };
    public static final int[][] GOAL_STATUS_4 =
            {
                    {0, 1, 2, 3, 4, 5},
                    {6, 7, 8, 9, 10, 11},
                    {12, 13, 14, 15, 16 ,17},
                    {18, 19, 20, 21, 22, 23},
                    {24, 25, 26, 27, 28, 29},
                    {30, 31, 32, 33, 34, BLANK_BRICK_4}
            };
    public static final int[][] GOAL_STATUS_5 =
            {
                    {0, 1, 2, 3, 4, 5, 6},
                    {7, 8, 9, 10, 11, 12, 13},
                    {14, 15, 16, 17, 18, 19, 20},
                    {21, 22, 23, 24, 25, 26, 27},
                    {28, 29, 30, 31, 32, 33, 34},
                    {35, 36, 37, 38, 39, 40, 41},
                    {42, 43, 44, 45, 46, 47, BLANK_BRICK_5}
            };
    public static final int[][] GOAL_STATUS_6 =
            {
                    {0, 1, 2, 3, 4, 5, 6, 7},
                    {8, 9, 10, 11, 12, 13, 14, 15},
                    {16, 17, 18, 19, 20, 21, 22, 23},
                    {24, 25, 26, 27, 28, 29, 30, 31},
                    {32, 33, 34, 35, 36, 37, 38, 39},
                    {40, 41, 42, 43, 44, 45, 46, 47},
                    {48, 49, 50, 51, 52, 53, 54, 55},
                    {56, 57, 58, 59, 60, 61, 62, BLANK_BRICK_6},
            };
    public static final int[][] GOAL_STATUS_7 =
            {
                    {0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {9, 10, 11, 12, 13, 14, 15, 16, 17},
                    {18, 19, 20, 21, 22, 23, 24, 25, 26},
                    {27, 28, 29, 30, 31, 32, 33, 34, 35},
                    {36, 37, 38, 39, 40, 41, 42, 43, 44},
                    {45, 46, 47, 48, 49, 50, 51, 52, 53},
                    {54, 55, 56, 57, 58, 59, 60, 61, 62},
                    {63, 64, 65, 66, 67, 68, 69, 70, 71},
                    {72, 73, 74, 75, 76, 77, 78, 79, BLANK_BRICK_7}
            };

    public static final int MAIL_GAME_STARTED = 100;
    public static final int MAIL_STEP_MOVED = 101;
    public static final int MAIL_GAME_WON = 102;
    public static final int REQUEST_CODE_CHOOSE_PICTURE = 200;

    private Bitmap mFullBitmap;
    private Bitmap[] mBitmapBricks;

    private Timer mTimer;
    private long mStartTime;
    private int mStepCount;

    private TextView mTvTime;
    private TextView mTvStep;
    private Button mBtnChooseAndStart;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //Toast.makeText(getBaseContext(), parent.getItemAtPosition(pos).toString(),Toast.LENGTH_LONG).show();
        switch (parent.getItemAtPosition(pos).toString()) {
            case "SuperEasy":
                    level = 1;
                break;
            case "Easy":
                    level = 2;
                break;
            case "Normal":
                    level = 3;
                break;
            case "Hard":
                    level = 4;
                break;
            case "SuperHard":
                    level = 5;
                break;
            case "God":
                    level = 6;
                break;
            case "Insane":
                    level = 7;
                break;
            default:
                Toast.makeText(getApplicationContext(), "cooming soon", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onStart() {
        super.onStart();
        final SharedPreferences settings = getSharedPreferences("localPreferences", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRun", true)) {
            new AlertDialog.Builder(this)
                    .setTitle("Cookies")
                    .setMessage("To give you the best possible experience, this APP uses cookies")
                    .setNegativeButton("Close message", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            settings.edit().putBoolean("isFirstRun", false).commit();
                        }
                    }).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            window.setEnterTransition(new Explode());
            window.setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_game);
        Mailbox.getInstance().atHome(this);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvStep = (TextView) findViewById(R.id.tv_step);
        mBtnChooseAndStart = (Button) findViewById(R.id.btn_choose_and_start);
        levelSpinner =  (Spinner) findViewById(level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this, R.array.level_array, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);
        levelSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mailbox.getInstance().leave(this);
    }

    @SuppressLint("RestrictedApi")
    private void startActivityForNewPicture() {
        Intent intent = new Intent(this, ChooseActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(
                    intent, REQUEST_CODE_CHOOSE_PICTURE,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            findViewById(R.id.btn_change_picture),
                            getString(R.string.bottom_button_trans_name)).toBundle());
        } else {
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    handleChooseResult(data.getData());
                }
                break;
        }
    }

    private void handleChooseResult(Uri uri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            IOUtils.deleteFile(uri.getPath());
            View container = findViewById(R.id.fl_board_container);
            assert container != null;
            int paddingHorizontal = container.getPaddingLeft() + container.getPaddingRight();
            int paddingVertical = container.getPaddingTop() + container.getPaddingBottom();
            mFullBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    container.getWidth() - paddingHorizontal,
                    container.getHeight() - paddingVertical,
                    false);
            switch(level) {
                case 1:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_1 * SPAN_COUNT_1];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_1 * SPAN_COUNT_1 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                break;
                case 2:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_2 * SPAN_COUNT_2];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_2 * SPAN_COUNT_2 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);

                    break;
                case 3:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_3 * SPAN_COUNT_3];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_3 * SPAN_COUNT_3 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                    break;
                case 4:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_4 * SPAN_COUNT_4];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_4* SPAN_COUNT_4 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                    break;
                case 5:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_5 * SPAN_COUNT_5];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_5 * SPAN_COUNT_5 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                    break;
                case 6:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_6 * SPAN_COUNT_6];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_6 * SPAN_COUNT_6 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                    break;
                case 7:
                    mBitmapBricks = new Bitmap[SPAN_COUNT_7 * SPAN_COUNT_7];
                    cutBitmapIntoPieces(level);
                    mBitmapBricks[SPAN_COUNT_7 * SPAN_COUNT_7 - 1] = BitmapFactory.decodeResource(getResources(), R.drawable.blank_brick);
                    break;
            }
            startNewGame();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void cutBitmapIntoPieces(int level) {
        int SPAN_COUNT = 0;
        switch(level){
            case 1:
                SPAN_COUNT = SPAN_COUNT_1;
            break;
            case 2:
                SPAN_COUNT = SPAN_COUNT_2;
                break;
            case 3:
                SPAN_COUNT = SPAN_COUNT_3;
                break;
            case 4:
                SPAN_COUNT = SPAN_COUNT_4;
                break;
            case 5:
                SPAN_COUNT = SPAN_COUNT_5;
                break;
            case 6:
                SPAN_COUNT = SPAN_COUNT_6;
                break;
            case 7:
                SPAN_COUNT = SPAN_COUNT_7;
                break;
        }
        int dividerWidth = (int) getResources().getDimension(R.dimen.brick_divider_width);
        int brickWidth = (mFullBitmap.getWidth() - dividerWidth * (SPAN_COUNT - 1)) / SPAN_COUNT;
        int brickHeight = (mFullBitmap.getHeight() - dividerWidth * (SPAN_COUNT - 1)) / SPAN_COUNT;
        for (int i = 0; i < SPAN_COUNT; i++) {
            for (int j = 0; j < SPAN_COUNT; j++) {
                mBitmapBricks[i * SPAN_COUNT + j] = Bitmap.createBitmap(
                        mFullBitmap,
                        j * (brickWidth + dividerWidth),
                        i * (brickHeight + dividerWidth),
                        brickWidth, brickHeight);
            }
        }
    }


    private void startNewGame() {
        mBtnChooseAndStart.setVisibility(View.GONE);
        switch (level) {
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_1))
                        .commit();
                break;
            case 2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_2))
                        .commit();
                break;
            case 3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_3))
                        .commit();
                break;
            case 4:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_4))
                        .commit();
                break;
            case 5:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_5))
                        .commit();
                break;
            case 6:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_6))
                        .commit();
                break;
            case 7:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, GameFragment.newInstance(mBitmapBricks, GOAL_STATUS_7))
                        .commit();
                break;

        }
    }

    @OnMailReceived
    public void onMailReceived(Mail mail) {
        if (mail.from == GameFragment.class) {
            switch ((int) mail.content) {
                case MAIL_GAME_STARTED:
                    L.d(TAG, "Game started");
                    onGameStarted();
                    break;
                case MAIL_STEP_MOVED:
                    L.d(TAG, "Moved");
                    onStepMoved();
                    break;
                case MAIL_GAME_WON:
                    L.d(TAG, "Game won");
                    onGameWon();
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void onGameStarted() {
        levelSpinner.setEnabled(false);
        mStepCount = 0;
        mTvStep.setText(String.valueOf(mStepCount));
        mTvTime.setText("00:00");
        mStartTime = System.currentTimeMillis();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long nowTime = System.currentTimeMillis();
                        Date span = new Date(nowTime - mStartTime);
                        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                        mTvTime.setText(format.format(span));
                    }
                });
            }
        }, 0, 1000);
    }

    private void onStepMoved() {
        mStepCount++;
        mTvStep.setText(String.valueOf(mStepCount));
    }

    private void onGameWon() {
        levelSpinner.setEnabled(true);
        mTimer.cancel();
        mTimer.purge();
        App.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_board_container, WinFragment.newInstance(mFullBitmap))
                        .commit();
                UIUtils.toast(
                        GameActivity.this,
                        String.format(getString(R.string.win_prompt_format), mTvTime.getText().toString(), mTvStep.getText().toString()),
                        true);
            }
        }, 500);
    }

    public void changePicture(View view) {
        startActivityForNewPicture();
    }

    public void restart(View view) {
        if (mFullBitmap == null) {
            // Not started, so cannot restart
            UIUtils.toast(this, getString(R.string.not_started));
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(restart))
                .setMessage(getString(R.string.confirm_restart_msg))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGame();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void lookUpOriginalPicture(View view) {
        if (mFullBitmap == null) {
            // Not started, so cannot restart
            UIUtils.toast(this, getString(R.string.not_started));
            return;
        }

        View alertView = View.inflate(this, R.layout.dialog_loop_up, null);
        ImageView imageView = (ImageView) alertView.findViewById(R.id.iv_image);
        imageView.setImageBitmap(mFullBitmap);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(alertView)
                .create();
        alertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                return true;
            }
        });
        dialog.show();
    }

    public void chooseAndStart(View view) {
        startActivityForNewPicture();
    }
}
