package upec.projetandroid2017_2018.Lists;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.Elements.ElementActivity;
import upec.projetandroid2017_2018.R;

public class ConnectedActivity extends AppCompatActivity {
    String username, password;
    Button add, historique;

    //onCreate______________________________________________________________________________________________________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Intent intent = getIntent();
        Log.i("BIPBIP connexion ", " username -> "+intent.getStringExtra("username")+" mdp -> "+intent.getStringExtra("password"));
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        //listView = (ListView) findViewById(R.id.listView);
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

    public class showhistorique extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher historique");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("historique", "");
                return ConnectionHandler.sendRequestToArray(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONArray result) {
            if(result!=null){
                Log.i("login ----->", "|" + result + "|");
                try {
                    showHistoriqueResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher historique");
            }else Toast.makeText(getApplicationContext(), "Error ShowHistorique!!!", Toast.LENGTH_LONG).show();
        }
    }

    //affichage
    private void showHistoriqueResult(JSONArray result) throws JSONException {
        Log.i("showHistoriqueResult", "***************************************************************************");
        String[] items = new String[result.length()];
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            items[i] = item.getString("action")+"|"+item.getString("data")+"|"+item.getString("list")+"|"+item.getString("element");
            Log.i("item "+i, " ->  : "+ items[i]);
        }
        Log.i("showHistoriqueResult", "***************************************************************************");

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_historique, null);
        ListView listView = (ListView)  dialogview.findViewById(R.id.listV);
        listView.setAdapter(adapter);
        dialogbuilder.setView(dialogview);
        dialogbuilder.create().show();
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
                final AlertDialog dialog = dialogbuilder.create();
                addnewList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!newList.getText().toString().isEmpty()){
                            String listToAdd = newList.getText().toString(); //TODO : enlever cette variable globale
                            new addNewList(listToAdd, dialog).execute();
                        } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setView(dialogview);
                dialog.show();
            }
        });
    }

    public class addNewList extends AsyncTask<String, Void, JSONObject> {

        private String listToAdd;
        private AlertDialog dialog;

        public addNewList(String list, AlertDialog dialog) {
            this.listToAdd = list;
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "je vais ajouter une liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("ajoutlist",listToAdd );
                return ConnectionHandler.sendRequest(data);//sendAddListRequest(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                /*Log.i("login ----->", "|" + result + "|");
                String login = "";
                login = getResult(result);
                Log.i("login ----->", "|" + login + "|");*/
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été créé", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("create list", listToAdd, " ");
                    dialog.dismiss();
                    new showListOfList().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette liste existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "je vais ajouter une liste");
            } else Toast.makeText(getApplicationContext(), "Error AddNewList!!!", Toast.LENGTH_LONG).show();
        }
    }

    //affichage des listes existant dans la base de donnée________________________________________________________________________________________________________________________________________

    public class showListOfList extends AsyncTask<String, Void, JSONArray> { //OK
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("liste_liste", "list");
                return ConnectionHandler.sendRequestToArray(data);//sendShowListRequest(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONArray result) {
            if (result!=null) {
                Log.i("login ----->", "|" + result + "|");
                try {
                    showResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher liste");
            } else Toast.makeText(getApplicationContext(), "Error ShowofList!!!", Toast.LENGTH_LONG).show();
        }
    }
    //affichage
    private void showResult(JSONArray result) throws JSONException {

        String[] lists = new String[result.length()];
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            lists[i] = item.getString("list");
            Log.i("item "+i, " ->  : "+ lists[i]);
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("item was clicked", "->>>>>"+listView.getItemAtPosition(position));
                final String clicked = listView.getItemAtPosition(position)+"";
                //goToElementActivity(listView.getItemAtPosition(position)+"");
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_choix_list, null);
                final Button supprimer = (Button) dialogview.findViewById(R.id.supprimer);
                final Button modifier = (Button) dialogview.findViewById(R.id.modifier);
                final Button seeElement = (Button) dialogview.findViewById(R.id.seeElement);
                final AlertDialog dialog = dialogbuilder.create();
                TextView textView = (TextView)  dialogview.findViewById(R.id.l);
                textView.setText(clicked);
                supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new supprimerList(dialog,clicked).execute();
                    }
                });
                modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        modifyList(clicked);
                    }
                });
                seeElement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        goToElementActivity(clicked);
                    }
                });
                dialog.setView(dialogview);
                dialog.show();
            }
        });
    }

    // voir les éléments de la liste
    private void goToElementActivity(String list){ //OK
        Intent intent = new Intent(ConnectedActivity.this, ElementActivity.class);
        intent.putExtra("listName", list);
        startActivity (intent);
    }

    //supprimer liste ______________________________________________________________________________________________________________________________

    public class supprimerList extends AsyncTask<String, Void, JSONObject> {

        private AlertDialog dialogChoix;
        private String clicked;

        public supprimerList(AlertDialog dialog, String clicked) {
            this.dialogChoix = dialog;
            this.clicked = clicked;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "supprimer la liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("listToSupp", clicked );
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été supprimée", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("delete list", clicked, " ");
                    dialogChoix.dismiss();
                    new showListOfList().execute();
                }
                Log.i("Après", "supprimer la liste");
            } else Toast.makeText(getApplicationContext(), "Error SupprimerList!!!", Toast.LENGTH_LONG).show();
        }
    }
    //modifier liste _______________________________________________________________________________________________________________________________

    private void modifyList(final String clicked){
        Log.i("modify liste", "onClick___________________________");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ConnectedActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_modify, null);
        final EditText newList = (EditText) dialogview.findViewById(R.id.newelement);
        newList.setText(clicked);
        final Button modList = (Button) dialogview.findViewById(R.id.modify);
        final AlertDialog dialogMod = dialogbuilder.create();
        modList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newList.getText().toString().isEmpty()){
                    String mod = newList.getText().toString();
                    new modifyList(dialogMod, mod, clicked).execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogMod.setView(dialogview);
        dialogMod.show();
    }

    public class modifyList extends AsyncTask<String, Void, JSONObject> {

        private AlertDialog dialogMod;
        private String mod;
        private String clicked;

        public modifyList(AlertDialog dialog, String mod, String clicked) {
            this.dialogMod = dialog;
            this.mod = mod;
            this.clicked = clicked;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "modif liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("modList", mod);
                data.put("listToMod",clicked );
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été modifié", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("modify list", clicked+" -> "+mod, " ");
                    dialogMod.dismiss();
                    new showListOfList().execute();
                }
                Log.i("Après", "modif liste");
            } else Toast.makeText(getApplicationContext(), "Error ModifyList!!!", Toast.LENGTH_LONG).show();
        }
    }

    //historique -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class addHistorique extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Historiqueeeee");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                String h = arg0[0];
                String l = arg0[1];
                String e = arg0[2];
                Log.i("'historique", "type : "+h+" - liste : "+l+" - element : "+e);
                JSONObject data = new JSONObject();
                data.put("h", h );
                data.put("lh", l );
                data.put("eh", e );
                return ConnectionHandler.sendRequest(data);//sendHistoriqueRequest(h, l, e);
            }catch(Exception e){ e.printStackTrace();return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                Log.i("login ----->", "|" + result.toString() + "|");
                Log.i("Après", "Historiqueeeee");
            } else Toast.makeText(getApplicationContext(), "Error addHistorique!!!", Toast.LENGTH_LONG).show();
        }
    }
}
