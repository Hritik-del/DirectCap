package com.example.project_upjao;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraOpen extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    Button cameraBtn;
    String currentPhotoPath;
    String currentPhotoPathwebp;
    StorageReference storageReference;
    String person_name;
    String cropType;
    Context context;
    Uri photoURI;
    File photoFile;
    File storageDirSucc;
    Long num = 0L;
    Uri photoURIwebp;
    File photoFilewebp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_open);

        Intent intent = getIntent();
        person_name = intent.getStringExtra("person_name");
        cropType = intent.getStringExtra("cropType");
        Log.v("cropType", cropType);
        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.cameraBtn);

        context=selectedImage.getContext();

        storageReference = FirebaseStorage.getInstance().getReference();

        cameraBtn.setOnClickListener(v -> askCameraPermissions());

    }

    public void askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();
            //Do_SOme_Operation();
        }else{
            requestStoragePermission();
        }
    }
    public void requestStoragePermission(){
        ActivityCompat.requestPermissions(this
                ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},1234);
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Not able to create image file on disk :(", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //increasing the count of number of tobeuploaded Pdf files.
                try {
                    FileReader fis = new FileReader(MainActivity.tobeUploaded);
                    BufferedReader br =
                            new BufferedReader(fis);
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        num = Long.parseLong(strLine);
                    }
                    Log.v("filepdf", Long.toString(num));
                    num++;
                    fis.close();
                } catch (IOException e) {
                    Log.v("filepdf", e.getMessage());
                    e.printStackTrace();
                }
                try {
                    FileWriter fos = new FileWriter(MainActivity.tobeUploaded);
                    fos.write(Long.toString(num));
                    fos.close();
                } catch (IOException e) {
                    Log.v("filepdf", e.getMessage());
                }

                photoURI = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
            /*if (photoFilewebp != null) {
                photoURIwebp = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".provider", photoFilewebp);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURIwebp);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }*/
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd$HHmmss").format(new Date());
        String imageFileName = person_name+"$" + timeStamp + "_";

        //creating file in internal storage for png image
        File storageDir = getExternalFilesDir(Environment.getRootDirectory().getAbsolutePath()+"/To Be Uploaded");
        storageDirSucc = getExternalFilesDir(Environment.getRootDirectory().getAbsolutePath()+"/Successfully Uploaded");
        Log.v("direc",storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */

        );

        /*File imagewebp = File.createTempFile(
                imageFileName,
                ".webp",
                storageDir
        );
        currentPhotoPathwebp = imagewebp.getAbsolutePath();
        photoFilewebp = imagewebp;*/

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoURI));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(imageBitmap);

                /*File f = new File(currentPhotoPath);

                Log.e("pathbitmap12", f.getPath());
                Picasso.get().load(Uri.fromFile(f))
                        .resize(300, 0)
                        .centerInside()
                        .into(selectedImage);


                Log.v("path", "ABsolute Url of Image is png " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);

                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);*/

                /*File fwebp = new File(currentPhotoPathwebp);
                Log.d("path", "ABsolute Url of Image is webp" + Uri.fromFile(fwebp));
                Uri contentUriwebp = Uri.fromFile(f);
                try  {
                    FileOutputStream out = new FileOutputStream(fwebp);

                    myBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out); // bmp is your Bitmap instance
                    out.close();
                } catch (IOException e) {
                    Log.v("bitmap", e.getMessage());
                }*/

                uploadPlaceDataInBackground1(photoFile.getName(), photoURI);
                Log.v("uricontent", photoURI.toString());
                //uploadPlaceDataInBackground(fwebp.getName(), contentUriwebp);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1234:if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                // Do_SOme_Operation();
            }

            default:super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    private void uploadPlaceDataInBackground(String name, Uri contentUri) {

        // TESTING WORKMANAGER FOR UPLOADING IMAGES TO FIREBASE STORAGE
        // Create a Constraints object that defines when the task should run
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Passing data to the worker class
        Data.Builder uploadBuilder = new Data.Builder();
        uploadBuilder.putString("image_uri", contentUri.toString());
        uploadBuilder.putString("file_name", name);
        uploadBuilder.putString("successful_uploaded_dir", storageDirSucc.getAbsolutePath());
        uploadBuilder.putString("cropType", cropType);
        Data ImageUriInputData = uploadBuilder.build();

        // ...then create a OneTimeWorkRequest that uses those constraints
        OneTimeWorkRequest uploadToFirebase = new OneTimeWorkRequest
                .Builder(UploadImageWorker.class)
                .setConstraints(constraints)
                .setInputData(ImageUriInputData)
                .build();

        Data.Builder uploadBuilder1 = new Data.Builder();
        uploadBuilder1.putString("image_uri", currentPhotoPath);
        Data ImageUriInputData1 = uploadBuilder1.build();

        OneTimeWorkRequest compress = new OneTimeWorkRequest
                .Builder(Compress.class)
                .setInputData(ImageUriInputData1)
                .build();

        // Execute and Manage the background service
        WorkManager workManager = WorkManager.getInstance(selectedImage.getContext());
        workManager.beginWith(compress)
                .then(uploadToFirebase)
                .enqueue();
    }

    private void uploadPlaceDataInBackground1(String name, Uri contentUri) {

        // TESTING WORKMANAGER FOR UPLOADING IMAGES TO FIREBASE STORAGE
        // Create a Constraints object that defines when the task should run
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Passing data to the worker class
        Data.Builder uploadBuilder = new Data.Builder();
        uploadBuilder.putString("image_uri", contentUri.toString());
        uploadBuilder.putString("file_name", name);
        uploadBuilder.putString("successful_uploaded_dir", storageDirSucc.getAbsolutePath());
        uploadBuilder.putString("cropType", cropType);
        Data ImageUriInputData = uploadBuilder.build();

        // ...then create a OneTimeWorkRequest that uses those constraints
        OneTimeWorkRequest uploadToFirebase = new OneTimeWorkRequest
                .Builder(UploadImageWorker.class)
                .setConstraints(constraints)
                .setInputData(ImageUriInputData)
                .build();

        Data.Builder uploadBuilder1 = new Data.Builder();
        uploadBuilder1.putString("image_uri", currentPhotoPath);
        Data ImageUriInputData1 = uploadBuilder1.build();

        OneTimeWorkRequest compress = new OneTimeWorkRequest
                .Builder(Compress.class)
                .setInputData(ImageUriInputData1)
                .build();

        // Execute and Manage the background service
        WorkManager workManager = WorkManager.getInstance(selectedImage.getContext());
        workManager.beginWith(compress)
                .then(uploadToFirebase)
                .enqueue();
    }




}
