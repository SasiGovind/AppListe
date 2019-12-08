package upec.projetandroid2017_2018;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class ElementActivity extends AppCompatActivity {

    String list, element, clicked, mod;
    ListView listView;
    Button add;
    AlertDialog dialogAddElement, dialogChoix, dialogModify;

    //onCreate_________________________________________________________________________________________________________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        Intent intent = getIntent();
        Log.i("BIPBIP list of element ", " listName -> "+intent.getStringExtra("listName"));
        String listName = intent.getStringExtra("listName")+"";

        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(textView.getText().toString()+" "+listName);

        list = listName;

        listView = (ListView) findViewById(R.id.listView);
        add = (Button) findViewById(R.id.add);

        addElement();

        new showListOfElement().execute();

        //TODO possibilité de voir l'historique d'une liste
    }

    //afficher la liste des éléments de la liste ______________________________________________________________________________________________________________________________________________________

    public class showListOfElement extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste des éléments");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendShowListOfElementRequest(); }
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
                Log.i("Après", "afficher liste des éléments");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendShowListOfElementRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("liste", list);
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
        String re = result.substring(result.split("\\{")[0].length()) ;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("item was clicked", "->>>>>"+listView.getItemAtPosition(position));
                clicked = listView.getItemAtPosition(position)+"";
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_choix, null);
                final Button supprimer = (Button) dialogview.findViewById(R.id.supprimer);
                final Button modifier = (Button) dialogview.findViewById(R.id.modifier);
                TextView textView = (TextView)  dialogview.findViewById(R.id.element);
                textView.setText(clicked);
                supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new supprimerElement().execute();
                    }
                });
                modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoix.dismiss();
                        modifyElement();
                    }
                });
                dialogbuilder.setView(dialogview);
                dialogChoix = dialogbuilder.create();
                dialogChoix.show();
            }
        });
    }

    //supprimer un élément de la liste ___________________________________________________________________________________________________________________________________

    public class supprimerElement extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "supprimer élément de la liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendSuppElementRequest(); }
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
                    Toast.makeText(getApplicationContext(), "l'élément a été supprimé", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("delete element", list, clicked);
                    dialogChoix.dismiss();
                    new showListOfElement().execute();
                }
                Log.i("Après", "supprimer élément à la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendSuppElementRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        /*data.put("suppelement", clicked );
        data.put("listSup", list );*/
        data.put("s", clicked );
        data.put("li", list );
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

    //modifier un élément de la liste ____________________________________________________________________________________________________________________________________

    private void modifyElement(){
        Log.i("modify Element", "onClick___________________________");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_modify, null);
        final EditText newElement = (EditText) dialogview.findViewById(R.id.newelement);
        newElement.setText(clicked);
        final Button modElement = (Button) dialogview.findViewById(R.id.modify);
        modElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newElement.getText().toString().isEmpty()){
                    mod = newElement.getText().toString();
                    new modifyElement().execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogbuilder.setView(dialogview);
        dialogModify = dialogbuilder.create();
        dialogModify.show();
    }

    public class modifyElement extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "modif élément dans la liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendModifyElementRequest(); }
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
                    new addHistorique().execute("modify element", list, clicked+" -> "+mod);
                    dialogModify.dismiss();
                    new showListOfElement().execute();
                }
                Log.i("Après", "modif élément dans la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendModifyElementRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("m", mod);
        data.put("lm", list );
        data.put("e",clicked );
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

    //ajouter un nouvel élément à la liste________________________________________________________________________________________________________________________________

    private void addElement(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("add Element_j'ai cliqué", "onClick___________________________");
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_addelement, null);
                final EditText newElement = (EditText) dialogview.findViewById(R.id.newelement);
                final Button addnewElement = (Button) dialogview.findViewById(R.id.buttonadd);
                addnewElement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!newElement.getText().toString().isEmpty()){
                            element = newElement.getText().toString();
                            new addNewElement().execute();
                        } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                    }
                });
                dialogbuilder.setView(dialogview);
                dialogAddElement = dialogbuilder.create();
                dialogAddElement.show();
            }
        });
    }

    public class addNewElement extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "ajout élément à la liste");
        }
        @Override
        protected String doInBackground(String... arg0) {
            try { return sendAddElementRequest(); }
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
                    Toast.makeText(getApplicationContext(), "l'élément a été créé", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("create element", list, element);
                    dialogAddElement.dismiss();
                    new showListOfElement().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette élément existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "ajout élément à la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //envoie de la requête
    private String sendAddElementRequest() throws UnsupportedEncodingException, JSONException {
        JSONObject data = new JSONObject();
        data.put("ajoutelement",element );
        data.put("l", list );
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
    }

    //parse le résultat de la requête
    private String getResult(String result){
        String re = result.split("\"")[1];
        return re;
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
/*try {
      Thread.sleep(1000000);
  } catch (InterruptedException e) {
       e.printStackTrace();
  }*/
