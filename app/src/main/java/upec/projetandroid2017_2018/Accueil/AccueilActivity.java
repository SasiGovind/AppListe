package upec.projetandroid2017_2018.Accueil;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.R;
import upec.projetandroid2017_2018.Lists.TestScreen;

public class AccueilActivity extends AppCompatActivity {

    EditText username, password;
    Button connexion, inscription;

    //onCreate______________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        setTitle("Network ToDo List");
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        connexion = (Button) findViewById(R.id.connexion);
        inscription = (Button) findViewById(R.id.inscription);

        connexion();
        inscription();
    }

    //inscription___________________________________________________________________________________
    private void inscription() {
        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //aller à la page d'inscription avec username
                goToInscriptionActivity();
            }
        });
    }

    //connexion_____________________________________________________________________________________
    private void connexion() {
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //envoyer username et password et vérifier s'ils trouvent dans la bd
            // si oui aller a la page de connection avec username et password
            // sinon rester
            new tryconnexion().execute();
            }
        });
    }

    public class tryconnexion extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant Test", "onClick");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("username", username.getText().toString());
                data.put("password", password.getText().toString());
                return ConnectionHandler.sendRequest(data);
            } catch(Exception e){ return null;}//"Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if(result!=null){
                if (result.has("success")){
                    Toast.makeText(getApplicationContext(), "Welcome " + username.getText().toString(), Toast.LENGTH_LONG).show();
                    goToConnectedActivity();
                } else
                    Toast.makeText(getApplicationContext(), "Username or password don't exist!!! you can register!!!!", Toast.LENGTH_LONG).show();
                Log.i("Après Test", "onClick");
            }else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }
/*
    //envoie de la requete
    private JSONObject sendRequestConnexion(JSONObject data) throws UnsupportedEncodingException, JSONException {
        Log.i("DATA ------", ""+data);
        try {
            URL url = new URL("https://pw.lacl.fr/~u21408532/AppList/config.php");

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
            if (response==HttpURLConnection.HTTP_OK){
                String text = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                text = sb.toString();
                JSONObject jb = new JSONObject(text);
                Log.i("Response ", "------>|"+text+"|");
                return jb;//text;
            } else return null;//"Error";
        }
        catch(Exception e) { return null;}
    }

    //modifie la forme des données à envoyer
    public String getPostData(JSONObject params) throws Exception {
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
*/

    private void goToInscriptionActivity(){
        Intent intent = new Intent(AccueilActivity.this, InscriptionActivity.class);
        intent.putExtra("username", username.getText().toString());
        Log.i("BIPBIP", "onClick inscription: username -> "+username.getText());
        startActivity (intent);
    }

    //si on arrive à sa connecter on passe à la page suivante
    private void goToConnectedActivity(){
        //Intent intent = new Intent(AccueilActivity.this, ConnectedActivity.class);
        Intent intent = new Intent(AccueilActivity.this, TestScreen.class);
        intent.putExtra("username", username.getText().toString());
        intent.putExtra("password", password.getText().toString());
        Log.i("BIPBIP", "onClick connexion: username -> "+username.getText()+"| password -> "+password.getText());
        startActivity (intent);
    }

    //______________________________________________________________________________________________
}
    /*private void showMessage(String title, String msg){
        Log.i(title, "message: "+msg);
        AlertDialog.Builder print = new AlertDialog.Builder(this);
        print.setCancelable(true);
        print.setTitle(title);
        print.setMessage(msg);
        print.show();
    }*/
    /*private void waitOneSecond(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
