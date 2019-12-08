package upec.projetandroid2017_2018;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class ConnectedActivity extends AppCompatActivity {
    String username, password, listToAdd, clicked, mod;
    ListView listView;
    Button add, historique;
    AlertDialog dialog, dialogChoix, dialogMod, dialogH;

    //onCreate______________________________________________________________________________________________________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Intent intent = getIntent();
        Log.i("BIPBIP connexion ", " username -> "+intent.getStringExtra("username")+" mdp -> "+intent.getStringExtra("password"));
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        listView = (ListView) findViewById(R.id.listView);
        add = (Button) findViewById(R.id.add);
        historique = (Button) findViewById(R.id.his);

        addList();

        showhistorique();

        new showListOfList().execute();

    }

    //voir historique _____________________________________________________________________________________________________________________________________________________________

    public void showhistorique(){

        historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new showhistorique().execute();
            }
        });
    }

    public class showhistorique extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher historique");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendShowHistoriqueRequest(); }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                try {
                    showHistoriqueResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher historique");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendShowHistoriqueRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("historique", "");
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception e) {
            return "Exception: "+e.getMessage();
        }
    }

    //affichage
    private void showHistoriqueResult(String result) throws JSONException {
        Log.i("showHistoriqueResult", "***************************************************************************");
        String re = result.split("\\}\\[")[1];
        Log.i("historiqueresult", " i : "+re);
        int taille = 0;
        for (int i=0; i < re.length(); i++) {
            if (re.charAt(i) == '{')
                taille++;
        }
        Log.i("historiqueresult", " taille : "+taille);
        String[] items = new String[taille];
        for(int i = 0; i<taille; i++){
            Log.i("popo", "__________________________________________");
            Log.i("re initial ", " ->  : "+re);
            String show = re.split("\\}")[0];
            Log.i("show", " ____ "+show);
            items[i] = extract(show);
            Log.i("_______________", " i : "+i+" result : "+items[i]);
            if (i+1!=taille){
                re = re.substring(re.split(",\\{")[0].length()+2);
                Log.i("nouveau re", " ->  : "+re);
            }
            Log.i("popo", "__________________________________________");
        }
        Log.i("showHistoriqueResult", "***************************************************************************");
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_historique, null);
        ListView listView = (ListView)  dialogview.findViewById(R.id.listV);
        listView.setAdapter(adapter);
        dialogbuilder.setView(dialogview);
        dialogH = dialogbuilder.create();
        dialogH.show();
    }

    private String extract (String s){
        String item = "|";
        Log.i("extract", s);
        item = item+s.split("action\":\"")[1].split("\"")[0]+" | ";
        //Log.i("extract", s.split("action\":\"")[1]);
        item = item+s.split("data\":\"")[1].split("\"")[0]+" | ";
        item = item+s.split("list\":\"")[1].split("\"")[0]+" | ";
        if(!s.split("element\":\"")[1].split("\"")[0].equals(" "))
            item = item+s.split("element\":\"")[1].split("\"")[0]+" | ";
        return item;
    }

    //ajout d'une nouvelle liste___________________________________________________________________________________________________________________________________________________

    private void addList(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("add___j'ai cliqué", "onClick___________________________");

                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_addlist, null);
                final EditText newList = (EditText) dialogview.findViewById(R.id.newlist);
                final Button addnewList = (Button) dialogview.findViewById(R.id.buttonadd);
                addnewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!newList.getText().toString().isEmpty()){
                            listToAdd = newList.getText().toString();
                            new addNewList().execute();
                        } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                    }
                });
                dialogbuilder.setView(dialogview);
                dialog = dialogbuilder.create();
                dialog.show();
            }
        });
    }

    public class addNewList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "je vais ajouter une liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendAddListRequest(); }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                String login = "";
                login = getResult(result);
                Log.i("login ----->", "|" + login + "|");
                if (login.equals("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été créé", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("create list", listToAdd, " ");
                    dialog.dismiss();
                    new showListOfList().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette liste existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "je vais ajouter une liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //parse le résultat de la requête
    private String getResult(String result){
        String re = result.split("\"")[1];
        return re;
    }

    //envoie de la requête
    private String sendAddListRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("ajoutlist",listToAdd );
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception e) {
            return "Exception: "+e.getMessage();
        }
    }

    //affichage des listes existant dans la base de donnée________________________________________________________________________________________________________________________________________

    public class showListOfList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendShowListRequest(); }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                try {
                    showResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendShowListRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("liste_liste", "list");
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception e) {
            return "Exception: "+e.getMessage();
        }
    }

    //affichage
    private void showResult(String result) throws JSONException {
        int taille = Integer.parseInt(result.split("array\\(")[1].split("\\)")[0]);
        String[] lists = new String[taille];
        Log.i("result of request : ", "tab["+taille);
        String re = result.substring(result.split("\\{")[0].length());
        Log.i("RE", "showResult: "+re);
        for (int i=0; i<taille; i++){
            lists[i] = re.split("\\["+i+"\\]")[1].split("\\}")[0].split("string\\(")[1].split("\"")[1].split("\"")[0];
            Log.i("split", "______________________"+i);
            Log.i("premier split", "showResult: "+re.split("\\["+i+"\\]")[1]);
            Log.i("deuxième split", "showResult: "+re.split("\\["+i+"\\]")[1].split("\\}")[0]);
            Log.i("deuxième split", "showResult: "+re.split("\\["+i+"\\]")[1].split("\\}")[0].split("string\\(")[1]);
            Log.i("deuxième split", "showResult: "+re.split("\\["+i+"\\]")[1].split("\\}")[0].split("string\\(")[1].split("\"")[1]);
            Log.i("deuxième split", "showResult: "+re.split("\\["+i+"\\]")[1].split("\\}")[0].split("string\\(")[1].split("\"")[1].split("\"")[0]);
            Log.i("BIPBIP je rempli la tab", "lists["+i+"] ==== "+lists[i]);
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("item was clicked", "->>>>>"+listView.getItemAtPosition(position));
                clicked = listView.getItemAtPosition(position)+"";
                //goToElementActivity(listView.getItemAtPosition(position)+"");
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_choix_list, null);
                final Button supprimer = (Button) dialogview.findViewById(R.id.supprimer);
                final Button modifier = (Button) dialogview.findViewById(R.id.modifier);
                final Button seeElement = (Button) dialogview.findViewById(R.id.seeElement);
                TextView textView = (TextView)  dialogview.findViewById(R.id.l);
                textView.setText(clicked);
                supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new supprimerList().execute();
                    }
                });
                modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoix.dismiss();
                        modifyList();
                    }
                });
                seeElement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoix.dismiss();
                        goToElementActivity(clicked);
                    }
                });
                dialogbuilder.setView(dialogview);
                dialogChoix = dialogbuilder.create();
                dialogChoix.show();
            }
        });
    }

    // voir les éléments de la liste
    private void goToElementActivity(String list){
        Intent intent = new Intent(ConnectedActivity.this, ElementActivity.class);
        intent.putExtra("listName", list);
        startActivity (intent);
    }

    //supprimer liste ______________________________________________________________________________________________________________________________

    public class supprimerList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "supprimer la liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendSuppListRequest(); }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                String login = "";
                login = getResult(result);
                Log.i("login ----->", "|" + login + "|");
                if (login.equals("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été supprimée", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("delete list", clicked, " ");
                    dialogChoix.dismiss();
                    new showListOfList().execute();
                }
                Log.i("Après", "supprimer la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendSuppListRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("listToSupp", clicked );
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception e) {
            return "Exception: "+e.getMessage();
        }
    }

    //modifier liste _______________________________________________________________________________________________________________________________

    private void modifyList(){
        Log.i("modify liste", "onClick___________________________");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_modify, null);
        final EditText newList = (EditText) dialogview.findViewById(R.id.newelement);
        newList.setText(clicked);
        final Button modList = (Button) dialogview.findViewById(R.id.modify);
        modList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newList.getText().toString().isEmpty()){
                    mod = newList.getText().toString();
                    new modifyList().execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogbuilder.setView(dialogview);
        dialogMod = dialogbuilder.create();
        dialogMod.show();
    }

    public class modifyList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "modif liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendModifyListRequest(); }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                String login = "";
                login = getResult(result);
                Log.i("login ----->", "|" + login + "|");
                if (login.equals("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été modifié", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("modify list", clicked+" -> "+mod, " ");
                    dialogMod.dismiss();
                    new showListOfList().execute();
                }
                Log.i("Après", "modif liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendModifyListRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("modList", mod);
        data.put("listToMod",clicked );
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception e) {
            return "Exception: "+e.getMessage();
        }
    }

    //______________________________________________________________________________________________________________________________________________________________________________________

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
       // new addHistorique().execute(params1, params2, params3);
    }

    //historique -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class addHistorique extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Historiqueeeee");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String h = arg0[0];
                String l = arg0[1];
                String e = arg0[2];
                Log.i("'historique", "type : "+h+" - liste : "+l+" - element : "+e);
                return sendHistoriqueRequest(h, l, e);
            }
            catch(Exception e){ return "Exception: "+e.getMessage(); }
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Log.i("login ----->", "|" + result + "|");
                String login = "";
                login = getResult(result);
                Log.i("login ----->", "|" + login + "|");
                Log.i("Après", "Historiqueeeee");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendHistoriqueRequest(String h, String l, String e) throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("h", h );
        data.put("lh", l );
        data.put("eh", e );
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
                Log.i("Response ", "------>|"+text+"|");
                return text;
            } else return "Error";
        }
        catch(Exception ex) {
            return "Exception: "+ex.getMessage();
        }
    }
}
