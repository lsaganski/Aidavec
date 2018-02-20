package br.com.aidavec.aidavec.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.aidavec.aidavec.base.BaseActivity;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Camera {

    public File file;

    private static Camera instance;

    private Context context;

    public byte[] resultBytes;
    public Bitmap resultBitmap;
    Uri imageUri;

    public static Camera getInstance() {
        if (instance == null)
            instance = new Camera();

        return instance;
    }

    private Handler handler;

    public void GetPicture(Context context, Handler handler) {

        this.context = context;
        this.handler = handler;

       // file = SDCardUtils.getPrivateFile(context, "foto.jpg", Environment.DIRECTORY_PICTURES);

        boolean res = Utils.getInstance().getYesNoConfirmWithExecutionStop("Selecione uma imagem", "Como você deseja selecionar a imagem ?", "Câmera", "Galeria", context);

        if (res)
            OpenCamera();
        else
            OpenGallery();

    }

    protected void OpenCamera(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            //Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // Uri.fromFile(file)
            ((BaseActivity)context).startActivityForResult(intent, BaseActivity.REQUEST_CODE_CAMERA);
        } else {
            checkForPermission(0);
        }
    }

    protected void OpenGallery() {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
            ((BaseActivity) context).startActivityForResult(Intent.createChooser(intent, "Select File"), BaseActivity.REQUEST_CODE_GALLERY);
        } else {
            checkForPermission(1);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission(int op) {
        if (op == 0) { // Camera
            int permissionCheckCamera = ((BaseActivity)context).checkSelfPermission(Manifest.permission.CAMERA);
            int permissionCheckWriteExt = ((BaseActivity)context).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED || permissionCheckWriteExt != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(((BaseActivity)context), Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(((BaseActivity)context), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Utils.Show("Não foi possível obter permissão para usar a câmera / salvar no sdcard", true);
                }

                String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                ActivityCompat.
                        requestPermissions(((BaseActivity)context), permissions, 1);
            } else {
                Log.d("SignUp", "Camera Granted");
            }

            OpenCamera();

        } else { // Gallery
            int permissionCheckReadExt = ((BaseActivity)context).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheckReadExt == PackageManager.PERMISSION_GRANTED) {
                Log.d("SignUp", "Write and Read Granted");
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(((BaseActivity)context), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Utils.Show("Não foi possível obter permissão para ler/escrever no sdcard", true);
                }
                ActivityCompat.
                        requestPermissions(((BaseActivity)context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }

            OpenGallery();

        }
    }

    public void HandleResult(int requestCode, Intent data) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (requestCode == BaseActivity.REQUEST_CODE_CAMERA) {
           //     Uri selectedImageUri = data.getData();
                //Bitmap thumbnail = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImageUri), null, null);

                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), imageUri);
                //imgView.setImageBitmap(thumbnail);
                //imageurl = getRealPathFromURI(selectedImageUri);


                //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                //Uri selectedImage = data.getData();

                int orientation = 0; // getOrientation(context, selectedImage);

                thumbnail = HandleBitmapPhoto(800, 800, thumbnail, orientation);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
                resultBytes = bytes.toByteArray();
                resultBitmap = thumbnail;
//                imgRounded.setImageBitmap(thumbnail);
            } else if (requestCode == BaseActivity.REQUEST_CODE_GALLERY) {
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                Uri selectedImage = data.getData();
                String selectedImagePath = getPath(selectedImage);
                if (selectedImagePath != null)
                    //ImageUtils.setPictureOnScreen(mTmpGalleryPicturePath, mImageView);
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                else {
                    InputStream is = context.getContentResolver().openInputStream(selectedImage);
                    bm = BitmapFactory.decodeStream(is);
                }

                int orientation = getOrientation(context, selectedImage);

                bm = HandleBitmapPhoto(300, 300, bm, orientation);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                resultBytes = bytes.toByteArray();
                resultBitmap = bm;
//                imgRounded.setImageBitmap(bm);
            }

            handler.sendEmptyMessage(1);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            handler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(0);
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        String path = null;
        try
        {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        }
        catch(NullPointerException e) {

        }
        return path;
    }

    public Bitmap HandleBitmapPhoto(int w, int h, Bitmap b, int orientation) {
        boolean portrait = true;
        double root = 1;
        int croppedSize = 0;

        if(orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                    b.getHeight(), matrix, true);
        }

        if (b.getWidth() > b.getHeight())
            portrait = false;

        if (portrait) {
            root = ((double)b.getHeight()) / ((double)b.getWidth());

            b = Bitmap.createScaledBitmap(b, w, ((int)(w*root)), false);

            if (b.getHeight() > h) {
                croppedSize = (b.getHeight() - h) / 2;

                b = Bitmap.createBitmap(b, 0, croppedSize, w, h);
            }
        } else {
            root = ((double)b.getWidth()) / ((double)b.getHeight());

            b = Bitmap.createScaledBitmap(b, ((int)(h*root)), h, false);

            if (b.getWidth() > w) {
                croppedSize = (b.getWidth() - w) / 2;

                b = Bitmap.createBitmap(b, croppedSize, 0, w, h);
            }
        }

        return b;
    }
}
