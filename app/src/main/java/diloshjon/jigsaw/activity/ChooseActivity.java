package diloshjon.jigsaw.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImage;

import diloshjon.commonrecyclerviewadapter.CommonRecyclerViewAdapter;
import diloshjon.commonrecyclerviewadapter.ViewHolder;
import diloshjon.jigsaw.R;
import diloshjon.jigsaw.util.ResUtils;
import diloshjon.jigsaw.view.SquareGridSpacingItemDecoration;

public class ChooseActivity extends AppCompatActivity {
    private static final String TAG = "ChooseActivity";
    private static final int CHOOSER_SPAN_COUNT = 3;
    private final int[] mResIds = new int[]{
            R.mipmap.pic_1, R.mipmap.pic_2, R.mipmap.pic_3,
            R.mipmap.pic_4, R.mipmap.pic_5, R.mipmap.pic_6,
            R.mipmap.pic_7, R.mipmap.pic_8, R.mipmap.pic_9,
            R.mipmap.pic_10, R.mipmap.pic_11, R.mipmap.pic_12,
            R.mipmap.pic_13, R.mipmap.pic_14, R.mipmap.pic_15,
            R.mipmap.pic_16, /*R.mipmap.pic_17, R.mipmap.pic_18,
            R.mipmap.pic_19, R.mipmap.pic_20, R.mipmap.pic_21,
            R.mipmap.pic_22, R.mipmap.pic_23, R.mipmap.pic_24
            R.mipmap.pic_25, R.mipmap.pic_26, R.mipmap.pic_27,
            R.mipmap.pic_28, R.mipmap.pic_29, R.mipmap.pic_30*/
    };
    private Uri[] mUris = new Uri[mResIds.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        for (int i = 0; i < mResIds.length; i++) {
            mUris[i] = ResUtils.getUriOfResource(this, mResIds[i]);
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_pics);
        assert recyclerView != null;
        CommonRecyclerViewAdapter<Uri> adapter = new CommonRecyclerViewAdapter<Uri>(this, mUris, R.layout.choose_pic_item) {
            @Override
            public void onItemViewAppear(ViewHolder holder, Uri uri, int position) {
                holder.setViewImageResource(R.id.iv_image, mResIds[position]);
            }
        };
        adapter.setOnItemClickListener(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                returnUri(mUris[position]);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, CHOOSER_SPAN_COUNT));
        recyclerView.addItemDecoration(new SquareGridSpacingItemDecoration(this, R.dimen.brick_divider_width, CHOOSER_SPAN_COUNT));
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    public void chooseFromGallery(View view) {
           if(isStoragePermissionGranted()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    CropImage.activity(data.getData())
                            .setActivityTitle(getString(R.string.crop))
                            .setAspectRatio(1, 1)
                            .setFixAspectRatio(true)
                            .start(this);
                }
                break;
            }
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    returnUri(result.getUri());
                }
                break;
            }
        }
    }

    private void returnUri(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
