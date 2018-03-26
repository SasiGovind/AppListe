package upec.projetandroid2017_2018.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import upec.projetandroid2017_2018.CustomSeekBarLabels;
import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.HistoData;
import upec.projetandroid2017_2018.Lists.ListHandler;
import upec.projetandroid2017_2018.Lists.TestScreen;
import upec.projetandroid2017_2018.R;

public class ElementHandler extends AppCompatActivity {

    private Intent parent;
    private String username,method,list;
    private Button colorPicker;

    private final int REQ_VOICE_RECOGNITION_CODE = 142;
    private TextView tempEditText;

    private String lastElement1;

    private TextInputEditText newElement;
    private TextInputEditText newDescription;

    private SeekBar priority;

    private JSONObject jb;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_handler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        parent = getIntent();
        username = parent.getStringExtra("username");
        method = parent.getStringExtra("method");
        list = parent.getStringExtra("listName");

        LinearLayout mSeekLin = (LinearLayout) findViewById(R.id.linearLayout11);
        CustomSeekBarLabels customSeekBarLabels = new CustomSeekBarLabels(this, 10, Color.DKGRAY);
        customSeekBarLabels.addSeekBar(mSeekLin);

        newElement = (TextInputEditText) findViewById(R.id.element);
        newDescription = (TextInputEditText) findViewById(R.id.description);
        ////////////////////////////////////////////////////////////////////////////////////////////
        tempEditText = newElement;

        LinearLayout t1 = (LinearLayout) findViewById(R.id.linearLayout9);
        LinearLayout t2 = (LinearLayout) findViewById(R.id.linearLayout10);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempEditText = newElement;
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempEditText = newDescription;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        priority = (SeekBar) findViewById(R.id.seekPrio);
        colorPicker = (Button) findViewById(R.id.buttonColor);

        jb = new JSONObject();

        if(method.equals("ADD")){
            setTitle("Add List");
            colorPicker.setBackgroundColor(Color.WHITE);
        }else if(method.equals("UPDATE")){
            setTitle("Update List");
            updateFill();
        }
    }

    private void updateFill() {
        lastElement1 = parent.getStringExtra("element");
        String lastDescription = parent.getStringExtra("description");
        int ecolor = parent.getIntExtra("ecolor",0);
        int prio = parent.getIntExtra("priority",4);
        newElement.setText(lastElement1);
        newDescription.setText(lastDescription);
        colorPicker.setBackgroundColor(ecolor);
        priority.setProgress(prio);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*Intent back = new Intent(this, ElementScreen.class);
        startActivity(back);*/
        Intent intent = new Intent();
        intent.putExtra("listName", list);
        intent.putExtra("username", username);
        intent.putExtra("response", "NON");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        Log.i("hg", "onBackPressed: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ok_item_mic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.ok_mic:
                openVoiceRecognition();
                break;
            case R.id.ok_action:
                String response = "Error";
                if (!newElement.getText().toString().isEmpty()){
                    String element = newElement.getText().toString();
                    String description = newDescription.getText().toString();
                    int listColor = ((ColorDrawable) colorPicker.getBackground()).getColor();
                    int prio = priority.getProgress()+1;
                    long time = System.currentTimeMillis();///1000;

                    if(method.equals("ADD")){
                        new addNewElement(element,description,listColor, prio, time).execute();
                    }else if(method.equals("UPDATE")){
                        new modifyElement(lastElement1, element, description, listColor, prio, time).execute();
                        //modList(newList1, listColor, lastModified);
                    }

                    //Notification
                    /*if(aSwitch.isChecked() && !dateTextView.getText().toString().equals("choose date") && !timeTextView.getText().toString().equals("choose time")){
                        Log.i("CLOCK", "date: "+dateTime.get(Calendar.HOUR_OF_DAY) + "Heure : "+dateTime.get(Calendar.MINUTE));

                        Intent myIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                        myIntent.putExtra("Title","UPEC");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),pendingIntent);
                        Log.i("NOTIFICATION", "FAIT");
                    }else Toast.makeText(getApplicationContext(), "Choose Date & Time", Toast.LENGTH_LONG).show();*/
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                doneAndReturn("OK");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doneAndReturn(String response) {
        Intent intent = new Intent();
        intent.putExtra("listName", list);
        intent.putExtra("username", username);
        intent.putExtra("response", response);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public class addNewElement extends AsyncTask<String, Void, JSONObject> {

        //private AlertDialog dialogAddElement;
        private String element;
        private String description;
        private int ecolor;
        private int prio;
        private long creation;

        public addNewElement(String element, String description, int ecolor, int prio, long creation) {
            this.element = element;
            this.description = description;
            this.ecolor = ecolor;
            this.prio = prio;
            this.creation = creation;
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
                data.put("username", username);
                data.put("description", description);
                data.put("ecolor", ecolor);
                data.put("priority", prio);
                data.put("creation", creation);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été créé", Toast.LENGTH_LONG).show();
                    new addHisto(list, element, username, "Element ajouter","").execute();
                    //new addHistorique().execute("create element", list, element);
                    //dialogAddElement.dismiss();
                    //new ElementActivity.showListOfElement().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette élément existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "ajout élément à la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    public class modifyElement extends AsyncTask<String, Void, JSONObject> {

        private String lastElement;
        private String newElement;
        private String description;
        private int ecolor;
        private int prio;
        private long modfification;

        public modifyElement(String lastElement, String newElement, String description, int ecolor, int prio, long modfification) {
            this.lastElement = lastElement;
            this.newElement = newElement;
            this.description = description;
            this.ecolor = ecolor;
            this.prio = prio;
            this.modfification = modfification;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "modif élément dans la liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("newElement", newElement);
                data.put("list", list);
                data.put("lastElement",lastElement);
                data.put("description",description);
                data.put("ecolor",ecolor);
                data.put("priority",prio);
                data.put("modified",modfification);
                //Log.i("MODIFY", "mod : "+mod+"list : "+list+"clicked :"+clicked);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été modifié", Toast.LENGTH_LONG).show();
                    new addHisto(list, newElement, username, "modify element","").execute();
                    //new ElementActivity.addHistorique().execute("modify element", list, clicked+" -> "+mod);
                    //new ElementActivity.showListOfElement().execute();
                }
                Log.i("Après", "modif élément dans la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// Voice Recognition
    private void openVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        try {
            startActivityForResult(intent, REQ_VOICE_RECOGNITION_CODE);
        }catch (ActivityNotFoundException e){
            Toast.makeText(ElementHandler.this, "Voice Recognition Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_VOICE_RECOGNITION_CODE:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> voiceText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tempEditText.setText(voiceText.get(0));
                    tempEditText= newDescription;
                }
                break;
        }
    }

    public void openColorPicker(final View view) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(ElementHandler.this, "color : " + selectedColor+ "\n onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_LONG).show();
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        ((Button) view).setBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class addHisto extends AsyncTask<String, Void, JSONObject> {

        private String liste;
        private String element;
        private String username;
        private String action;
        private String description;

        public addHisto(String liste, String element, String username, String action, String description) {
            this.liste = liste;
            this.element = element;
            this.username = username;
            this.action = action;
            this.description = description;
        }

        @Override
        protected void onPreExecute(){
            //Log.i("Avant", "Historiqueeeee");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("hisL", liste );
                data.put("hisE", element );
                data.put("hisU", username );
                data.put("hisA", action );
                data.put("hisD", description );
                return ConnectionHandler.sendRequest(data);//sendHistoriqueRequest(h, l, e);
            }catch(Exception e){ e.printStackTrace();return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            /*if (result!=null) {
                Log.i("login ----->", "|" + result.toString() + "|");
                Log.i("Après", "Historiqueeeee");
            } else Toast.makeText(getApplicationContext(), "Error addHistorique!!!", Toast.LENGTH_LONG).show();*/
        }
    }

    public class showhisto extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher historique");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("histo", "");
                return ConnectionHandler.sendRequestToArray(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONArray result) {
            if(result!=null){
                Log.i("login ----->", "|" + result + "|");
                try {
                    showHistoResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher historique");
            }else Toast.makeText(getApplicationContext(), "Error ShowHistorique!!!", Toast.LENGTH_LONG).show();
        }
    }

    //affichage
    private void showHistoResult(JSONArray result) throws JSONException {
        Log.i("showHistorique", "***************************************************************************");
        ArrayList<HistoData> hisData = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            hisData.add(new HistoData(item.getString("dateT"), item.getString("list"), item.getString("element"), item.getString("username"), item.getString("action"), item.getString("description")));
            //items[i] = item.getString("action")+"|"+item.getString("data")+"|"+item.getString("list")+"|"+item.getString("element");
            //Log.i("item "+i, " ->  : "+ items[i]);
        }
        Log.i("showHistorique", "***************************************************************************");

        CustomAdapter customAdapter = new CustomAdapter(hisData);

//        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementHandler.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_historique, null);
        ListView listView = (ListView)  dialogview.findViewById(R.id.listV);
        listView.setAdapter(customAdapter);
        dialogbuilder.setView(dialogview);
        dialogbuilder.create().show();
    }

    class CustomAdapter extends BaseAdapter {

        private ArrayList<HistoData> histoData;

        public CustomAdapter(ArrayList<HistoData> histoData) {
            this.histoData = histoData;
        }

        @Override
        public int getCount() {
            return histoData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.custom_histo, null);

            ((TextView)view.findViewById(R.id.hisTime)).setText(histoData.get(i).getTime());
            ((TextView)view.findViewById(R.id.hisUser)).setText(histoData.get(i).getUsername());
            ((TextView)view.findViewById(R.id.hisAction)).setText(histoData.get(i).getAction());
            ((TextView)view.findViewById(R.id.hisDesc)).setText(histoData.get(i).getDescrition());
            return view;
        }
    }
}
