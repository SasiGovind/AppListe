package upec.projetandroid2017_2018.DatabaseHandler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Sasig on 09/03/2018.
 */

public class ConnectionHandler {

    static public String getResponseToString(JSONObject data){
        Log.i("DATA ------", ""+data);
        try {
            URL url = new URL("https://pw.lacl.fr/~u21408532/AppList/requete.php");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            wr.write(getPostData(data));
            wr.flush();
            wr.close();
            os.close();
            Log.i("DATA send ------", ""+getPostData(data));

            int response = urlConnection.getResponseCode();
            Log.i("Code response ------", ""+response);
            if (response== HttpURLConnection.HTTP_OK){
                String text = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                text = sb.toString();
                return text;
            } else return null;
        }
        catch(Exception e) { return null; }
    }

    //envoie de la requete
    static public JSONObject sendRequest(JSONObject data) throws UnsupportedEncodingException, JSONException {
        /*JSONObject data = new JSONObject();
        data.put("inscrit", username.getText().toString());
        data.put("mdp", password.getText().toString());*/
        String text = getResponseToString(data);
        if(text!=null){
            JSONObject jb = new JSONObject(text);
            Log.i("Response ", "------>|"+text+"|");
            return jb;
        }else return null;
    }

    static public JSONArray sendRequestToArray(JSONObject data) throws UnsupportedEncodingException, JSONException {
        /*JSONObject data = new JSONObject();
        data.put("inscrit", username.getText().toString());
        data.put("mdp", password.getText().toString());*/
        String text = getResponseToString(data);
        if(text!=null){
            JSONArray jb = new JSONArray(text);
            Log.i("Response ", "------>|"+text+"|");
            return jb;
        }else return null;
    }

    static private String getPostData(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}