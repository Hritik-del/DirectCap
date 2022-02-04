package com.example.project_upjao;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.google.firebase.crashlytics.internal.common.AppData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjose on 8/20/2016.
 */

public class CloudStorage {

    static UploadImageWorker activity=null;
    private static final String TAG = "CloudStorage";

    public static void uploadFile(UploadImageWorker activity1, String bucketName, String name, File storageDir)throws Exception {
        activity = activity1;
        Storage storage = getStorage();
        StorageObject object = new StorageObject();
        object.setBucket(bucketName);
        File file = new File(storageDir+"/"+"To Be Uploaded"+"/", name);
        InputStream stream = new FileInputStream(file);

        try {
            String contentType = URLConnection.guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType,stream);

            Storage.Objects.Insert insert = storage.objects().insert(bucketName, null, content);
            insert.setName("abc"+"/"+name);
            StorageObject obj = insert.execute();
            Log.d(TAG, obj.getSelfLink());
        }catch (Exception e){
            Log.v("uploadcheckgcp", e.getMessage());
        }
        finally {
            stream.close();
        }
    }

    static Storage storage = null;
    private static Storage getStorage() throws Exception {

        Log.v("uploadcheckgcp", "2");
        if (storage == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId("myuser@analyticsupjao.iam.gserviceaccount.com") //Email
                    .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                    .setServiceAccountScopes(scopes).build();

            storage = new Storage.Builder(httpTransport, jsonFactory,
                    credential).setApplicationName("MyApp")
                    .build();
        }

        return storage;
    }

    private static File getTempPkc12File() throws IOException {
        // xxx.p12 export from google API console
        Context context = activity.getApplicationContext();
        AssetManager am = context.getAssets();
        InputStream pkc12Stream = am.open("analyticsupjao-adcf1e4faf6c.p12");
        File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }
}
