package in.ac.iiti.gymakhanaiiti.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;

/**
 * Created by Ankit Gaur on 1/18/2017.
 */

public class Methods {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    //parse html string to html string, as fromHtml is deprecated so adding version check
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    @SuppressWarnings("deprecation")
    public static String toHtml(Spannable s){
        String result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(s,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.toHtml(s);
        }
        return result;
    }

    public static Spannable stripUnderlines(String content) {
        //this method is used to strip underlines from a text content containing links;
        Spannable s = (Spannable) Methods.fromHtml(content);
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        return s;
    }


    public static String encodeToBase64(Bitmap bitmap)
    {
        String imgString ;
        if(bitmap != null) {
            int h = bitmap.getHeight();
            int w = bitmap.getHeight();
            int maxdimen = 400;
            int r = h/w;
            if(r>1&&h>400)
            {
                bitmap = Bitmap.createBitmap(400/r,400,bitmap.getConfig());
            }else if(r<1&&w>400){
                bitmap = Bitmap.createBitmap(400,400*r,bitmap.getConfig());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] profileImage = outputStream.toByteArray();

            imgString = Base64.encodeToString(profileImage,
                    Base64.NO_WRAP);
        }else{
            imgString = "";
        }

        return imgString;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }






    public static boolean isFileinCache(Context context,String fileName)
    {
        File f = new File(context.getCacheDir(), fileName);
        return f.exists();
    }
    public static File getFileFromBitmap(Context context, Bitmap bitmap,String name)
    {
        File f = new File(context.getCacheDir(), name);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,50, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        }catch (IOException e)
        {
            if(e!=null)
                Log.d("ankit",e.getMessage());
            return null;
        }
    }
    public static Bitmap getBitmapFromCache(Context context,String fileName)
    {
        File cacheFile = new File(context.getCacheDir(),fileName);
        try{
            FileInputStream fis = new FileInputStream(cacheFile);
            return BitmapFactory.decodeStream(fis);
        }catch (IOException e)
        {
            e.printStackTrace();
            return  null;
        }
    }
}
