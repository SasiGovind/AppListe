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
import upec.projetandroid2017_2018.Lists.ConnectedActivity;
import upec.projetandroid2017_2018.Lists.TestScreen;
import upec.projetandroid2017_2018.R;

public class InscriptionActivity extends AppCompatActivity {

    EditText username, password;
    Button inscription;

    //onCreate__________________________________________________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        Intent intent = getIntent();

        setTitle("Inscription");

        username = (EditText) findViewById(R.id.username);
        username.setText(intent.getStringExtra("username"));
        Log.i("BIPBIP inscription ", "onCreate: username -> "+intent.getStringExtra("username"));

        password = (EditText) findViewById(R.id.password);

        inscription = (Button) findViewById(R.id.inscription);

        inscription();
    }

    //inscription_________________________________________________________________________________________________________________________________
    private void inscription() {
        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vérifier si username et password n'existe pas encore dans la bd
                // si oui erreur rester la
                // sinon enregistrer username et password et passer avec dans la page de connection
                Log.i("bipbip enregistrer", "onClick");
                new tryInscription().execute();
            }
        });
    }

    public class tryInscription extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "inscription");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("inscrit", username.getText().toString());
                data.put("mdp", password.getText().toString());
                return ConnectionHandler.sendRequest(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if(result!=null){
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "Welcome " + username.getText().toString() + "! Vous êtes inscrit!", Toast.LENGTH_LONG).show();
                    goToConnectedActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Inscription impossible!!! Cet identifiant existe déjà!!!", Toast.LENGTH_LONG).show();
                }
                Log.i("Après", "inscription");
            }else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }
/*
    //envoie de la requete
    private JSONObject sendRequestInscription() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("inscrit", username.getText().toString());
        data.put("mdp", password.getText().toString());
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
                return jb;
            } else return null;
        }
        catch(Exception e) { return null; }
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
    //si on arrive à sa connecter on passe à la page suivante
    private void goToConnectedActivity(){
        Intent intent = new Intent(InscriptionActivity.this, TestScreen.class);//
        intent.putExtra("username", username.getText().toString());
        intent.putExtra("password", password.getText().toString());
        Log.i("BIPBIP", "onClick inscription: username -> "+username.getText()+"| password -> "+password.getText());
        startActivity (intent);
    }
}
