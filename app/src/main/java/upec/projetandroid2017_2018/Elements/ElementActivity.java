package upec.projetandroid2017_2018.Elements;

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
import upec.projetandroid2017_2018.R;

public class ElementActivity extends AppCompatActivity {

    String list;
    Button add;

    //onCreate_________________________________________________________________________________________________________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        Intent intent = getIntent();

        String listName = intent.getStringExtra("listName")+"";
        Log.i("BIPBIP list of element ", " listName -> "+listName);
        TextView textView = (TextView) findViewById(R.id.textView3);
        textView.setText(textView.getText().toString()+" "+listName);

        list = listName;

        add = (Button) findViewById(R.id.add);
        addElement();
        new showListOfElement().execute();

        //TODO possibilité de voir l'historique d'une liste
    }

    //afficher la liste des éléments de la liste ______________________________________________________________________________________________________________________________________________________

    public class showListOfElement extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste des éléments");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("liste", list);
                return ConnectionHandler.sendRequestToArray(data); }
            catch(Exception e){ return null; }
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
                Log.i("Après", "afficher liste des éléments");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //affichage
    private void showResult(JSONArray result) throws JSONException {

        String[] lists = new String[result.length()];
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            lists[i] = item.getString("element");
            Log.i("item "+i, " ->  : "+ lists[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("item was clicked", "->>>>>"+listView.getItemAtPosition(position));
                final String clicked = listView.getItemAtPosition(position)+"";
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_choix, null);
                final Button supprimer = (Button) dialogview.findViewById(R.id.supprimer);
                final Button modifier = (Button) dialogview.findViewById(R.id.modifier);
                final AlertDialog dialogChoix = dialogbuilder.create();
                TextView textView = (TextView)  dialogview.findViewById(R.id.element);
                textView.setText(clicked);
                supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new supprimerElement(dialogChoix, clicked).execute();
                    }
                });
                modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoix.dismiss();
                        modifyElement(clicked);
                    }
                });
                dialogChoix.setView(dialogview);
                dialogChoix.show();
            }
        });
    }

    //supprimer un élément de la liste ___________________________________________________________________________________________________________________________________

    public class supprimerElement extends AsyncTask<String, Void, JSONObject> {

        private AlertDialog dialogChoix;
        private String clicked;

        public supprimerElement(AlertDialog dialog, String clicked) {
            this.dialogChoix = dialog;
            this.clicked = clicked;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "supprimer élément de la liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("s", clicked );
                data.put("li", list );
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été supprimé", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("delete element", list, clicked);
                    dialogChoix.dismiss();
                    new showListOfElement().execute();
                }
                Log.i("Après", "supprimer élément à la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //modifier un élément de la liste ____________________________________________________________________________________________________________________________________

    private void modifyElement(final String clicked){
        Log.i("modify Element", "onClick___________________________");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementActivity.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_modify, null);
        final EditText newElement = (EditText) dialogview.findViewById(R.id.newelement);
        newElement.setText(clicked);
        final Button modElement = (Button) dialogview.findViewById(R.id.modify);
        final AlertDialog dialogModify = dialogbuilder.create();
        modElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newElement.getText().toString().isEmpty()){
                    String mod = newElement.getText().toString();
                    new modifyElement(dialogModify, clicked, mod).execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogModify.setView(dialogview);
        dialogModify.show();
    }

    public class modifyElement extends AsyncTask<String, Void, JSONObject> {

        private AlertDialog dialogModify;
        private String clicked;
        private String mod;


        public modifyElement(AlertDialog dialogModify, String clicked, String mod) {
            this.dialogModify = dialogModify;
            this.clicked = clicked;
            this.mod = mod;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "modif élément dans la liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("m", mod);
                data.put("lm", list );
                data.put("e",clicked );
                Log.i("MODIFY", "mod : "+mod+"list : "+list+"clicked :"+clicked);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été modifié", Toast.LENGTH_LONG).show();
                    new addHistorique().execute("modify element", list, clicked+" -> "+mod);
                    dialogModify.dismiss();
                    new showListOfElement().execute();
                }
                Log.i("Après", "modif élément dans la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
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
                final AlertDialog dialogAddElement = dialogbuilder.create();
                addnewElement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!newElement.getText().toString().isEmpty()){
                            String element = newElement.getText().toString();
                            new addNewElement(dialogAddElement, element).execute();
                        } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                    }
                });
                dialogAddElement.setView(dialogview);
                dialogAddElement.show();
            }
        });
    }

    public class addNewElement extends AsyncTask<String, Void, JSONObject> {

        private AlertDialog dialogAddElement;
        private String element;

        public addNewElement(AlertDialog dialogAddElement, String element) {
            this.dialogAddElement = dialogAddElement;
            this.element = element;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "ajout élément à la liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("ajoutelement",element );
                data.put("l", list );
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
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
                return ConnectionHandler.sendRequest(data);
            }
            catch(Exception e){ e.printStackTrace();return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                Log.i("Après", "Historiqueeeee");
            } else Toast.makeText(getApplicationContext(), "Error AddHistorique!!!", Toast.LENGTH_LONG).show();
        }
    }
}
