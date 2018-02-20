package br.com.aidavec.aidavec.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

import br.com.aidavec.aidavec.helpers.Camera;
import br.com.aidavec.aidavec.helpers.Utils;
import livroandroid.lib.utils.ImageResizeUtils;

/**
 * Created by Leonardo Saganski on 20/11/16.
 */
public class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int REQUEST_CODE_GALLERY = 102;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {   // && data != null
           // Uri selectedImage = data.getData();
            Camera.getInstance().HandleResult(requestCode, data);
        }
    }
}


