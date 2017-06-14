package in.ac.iiti.gymakhanaiiti.other;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by AnkitGaur on 1/3/2017.
 */
public class SharedPref {

private static String filePath = "mPrefs";

    public static void saveString(Context context, String key, String val){
        SharedPreferences.Editor editor = context.getSharedPreferences(filePath, Context.MODE_PRIVATE).edit();
        editor.putString(key, val);
        editor.apply();

    }

    public static String getString(Context context, String key){
        SharedPreferences mPrefs = context.getSharedPreferences(filePath, Context.MODE_PRIVATE);
        String json = mPrefs.getString(key, null);
        return json;
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                filePath, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                filePath, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                filePath, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
    public static double getDbl(Context context, String key, double defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                filePath, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(key, defaultValue);
        return sharedPreferences.getInt(key, (int) defaultValue);
    }
    public static void saveDbl(Context context, String key, double value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                filePath, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, (int) value);
        editor.commit();
    }
}
