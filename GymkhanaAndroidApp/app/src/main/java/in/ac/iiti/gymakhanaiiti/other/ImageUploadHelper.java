package in.ac.iiti.gymakhanaiiti.other;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ankit on 18/2/17.
 */

public class ImageUploadHelper  {
    private String TAG = "ImageHelper";
    private String filePath; // local path of the file to be sent to server
    private String targetUrl;
    private File fileToBeUploaded;

    private OnResponseListener onResponseListener;

    public ImageUploadHelper(String filePath, String targetUrl,OnResponseListener onResponseListener)
    {
        this.onResponseListener = onResponseListener;
        this.targetUrl = targetUrl;
        this.filePath = filePath;
        this.fileToBeUploaded = new File(this.filePath);

    }
    public ImageUploadHelper(String fileName,File file,String targetUrl,OnResponseListener onResponseListener)
    {
        this.onResponseListener = onResponseListener;
        this.filePath = fileName;
        this.fileToBeUploaded  = file;
        this.targetUrl = targetUrl;
    }

    public void uploadImage()
    {
        new MyAsynTask().execute();
    }
    private class MyAsynTask extends AsyncTask<Void ,Void,String>{
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            onResponseListener.onResponse(response);
        }

        @Override
        protected String doInBackground(Void... params) {
           return sendFileToServer();
        }
    }


    public interface OnResponseListener{
        public abstract void onResponse(String response);
    }

    private String sendFileToServer() {
        if(fileToBeUploaded ==null) return "error";

        String response = "error";
        Log.d(TAG, "Image Uploading in class" + filePath);
        Log.d(TAG,"Image upload url" + targetUrl);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        String urlServer = targetUrl;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileToBeUploaded);

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String connstr = null;
            connstr = "Content-Disposition: form-data; name=\"image_file\";filePath=\""
                    + filePath + "\"" + lineEnd;
            Log.d("Connstr", connstr);

            outputStream.writeBytes(connstr);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            try {
                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        response = "outofmemoryerror";
                        return response;
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
                Log.d(Vars.globalTag, "in Image upload helper" + Log.getStackTraceString(e));
                return response;
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            //TODO Take json response from the server and return it;

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.d(TAG, "Server Response Code" + serverResponseCode);
            Log.d(TAG, "server response message" + serverResponseMessage);

            if (serverResponseCode == 200) {
                response = "true";
            }


            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception ex) {
            // Exception handling
            response = "error";
            Log.d(Vars.globalTag, ex.getMessage() + "error in android");
            ex.printStackTrace();
        }
        return response;
    }

}
