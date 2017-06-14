package in.ac.iiti.gymakhanaiiti.User;

import java.util.HashMap;

import in.ac.iiti.gymakhanaiiti.other.Vars;

/**
 * Created by ankit on 13/2/17.
 */

public class AuthenticationHeader {
    public static HashMap<String, String> getAuthenticationHeader(String autheKey)
    {
        HashMap<String,String > header = new HashMap<String,String>();
        header.put(Vars.KeyAuthenticationVar,autheKey);
        return header;
    }
}
