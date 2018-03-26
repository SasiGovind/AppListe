package upec.projetandroid2017_2018.Lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import upec.projetandroid2017_2018.Adapters.MySpinnerAdapter;
import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.HistoData;
import upec.projetandroid2017_2018.R;

public class ListHandler extends AppCompatActivity {

    private final int REQ_VOICE_RECOGNITION_CODE = 142;
    private EditText tempEditText;
    private TextView tempTextView;
    private Calendar dateTime = Calendar.getInstance();
    private LinearLayout timeMainLayout;

    private Intent parent;

    private TextInputEditText newList;
    private Switch aSwitch;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button colorPicker;
    private ImageView mic;
    private String username;
    private String method;
    private int rappel;
    private long rappelTime;
    private String lastListName;
    private AlertDialog iconDialog;
    private ImageView listIcon;
    private ArrayList<String> categories;
    private Spinner mySpinner;
    private MySpinnerAdapter myAdapter;
    private LinearLayout dateLayout;
    private LinearLayout timeLayout;


    private boolean visibilitySet = false;

    private JSONObject jb;

    public ListHandler() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_handler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        parent = getIntent();
        username = parent.getStringExtra("username");
        method = parent.getStringExtra("method");
        newList = (TextInputEditText) findViewById(R.id.newList);
        listIcon = (ImageView) findViewById(R.id.listIcon);
        colorPicker = (Button) findViewById(R.id.buttonColor);
        aSwitch = (Switch) findViewById(R.id.switch1);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        timeMainLayout = (LinearLayout) findViewById(R.id.timeMainLayout);
        jb = new JSONObject();

        listIcon.setTag(R.mipmap.ic_smiley);
        listIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseIcon();
            }
        });

        /******************************************************************************************/
        mySpinner = (Spinner) findViewById(R.id.listHandlerSpinner);
        //mySpinner.setOnItemSelectedListener(this);

        categories = parent.getStringArrayListExtra("categories");

        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(TestScreen.this, android.R.layout.simple_list_item_1, testStrings);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);*/
        myAdapter = new MySpinnerAdapter(this, categories);
        mySpinner.setAdapter(myAdapter);
        /******************************************************************************************/

        if (method.equals("ADD")) {
            setTitle("Add List");
            colorPicker.setBackgroundColor(Color.WHITE);
        } else if (method.equals("UPDATE")) {
            setTitle("Update List");
            updateFill();
        }

        setWindow();
    }

    private void updateFill() {
        lastListName = parent.getStringExtra("list");
        int icon = parent.getIntExtra("icon", R.mipmap.ic_smiley);
        listIcon.setImageResource(icon);

        String catName = parent.getStringExtra("category");
        ArrayAdapter myAdap = (ArrayAdapter) mySpinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(catName);
        mySpinner.setSelection(spinnerPosition);


        String vis = parent.getStringExtra("visibility");
        int col = parent.getIntExtra("color", 0);
        Log.i("COLORRRRR", vis);
        newList.setText(lastListName);
        if(!vis.equals("public")){
            ((RadioButton) findViewById(R.id.radioButton1)).setChecked(false);
            if (vis.equals("private")) {
                ((RadioButton) findViewById(R.id.radioButton)).setChecked(false);
                ((RadioButton) findViewById(R.id.radioButton2)).setChecked(true);
                try {
                    jb.put("visibility", "private");
                } catch (JSONException e) {}
            }else if(vis.equals("shared")){
                ((RadioButton) findViewById(R.id.radioButton)).setChecked(true);
                ((RadioButton) findViewById(R.id.radioButton2)).setChecked(false);
                try {
                    jb.put("visibility", "shared");
                } catch (JSONException e) {}
            }
            visibilitySet = true;
        }
        colorPicker.setBackgroundColor(col);
        int rem = parent.getIntExtra("reminder", 0);
        long remTime = parent.getLongExtra("reminderTime", 0);
        if (rem == 1) {
            aSwitch.setChecked(true);
            dateLayout.setVisibility(View.VISIBLE);
            timeMainLayout.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.VISIBLE);
            dateTime.setTimeInMillis(remTime);
            tempTextView = dateTextView;
            updateTextLabel("date");
            tempTextView = timeTextView;
            updateTextLabel("time");
        }
    }

    private void setWindow() {

        mic = (ImageView) findViewById(R.id.mic);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempEditText = newList;
                openVoiceRecognition();
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        //////////////////////////////////////////////////////////////////////////////////////////// Date&Time Layout

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dateLayout.setVisibility(View.VISIBLE);
                } else {
                    dateLayout.setVisibility(View.INVISIBLE);
                    timeMainLayout.setVisibility(View.GONE);
                }
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    tempTextView = dateTextView;
                    updateDate();
                }
            }
        });
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    tempTextView = timeTextView;
                    updateTime();
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        final ArrayList<View> axe = radioGroup.getTouchables();
        try {
            if (!visibilitySet) {
                jb.put("visibility", "public");
                ((RadioButton) findViewById(R.id.radioButton1)).setChecked(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (View v : axe) {
            RadioButton r = ((RadioButton) v);
            System.out.println(r.getId());
            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jb.remove("visibility");
                    try {
                        jb.put("visibility", ((RadioButton) view).getText().toString().toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ListHandler.this, "RadioButton Clicked : " + axe.indexOf(view), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent back = new Intent(this, TestScreen.class);
        back.putExtra("username", username);
        startActivity(back);
        Log.i("hg", "onBackPressed: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ok_item, menu);
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
            case R.id.ok_action:
                String response = "Error";
                if (!newList.getText().toString().isEmpty()) {
                    String newList1 = newList.getText().toString();
                    long timeCreation = System.currentTimeMillis();///1000;
                    int listColor = ((ColorDrawable) colorPicker.getBackground()).getColor();
                    String category_act = mySpinner.getSelectedItem().toString();
                    int icon = Integer.parseInt(listIcon.getTag().toString());
                    if (aSwitch.isChecked()) {
                        if (!dateTextView.getText().toString().equals("choose date") && !timeTextView.getText().toString().equals("choose time")) {

                            ///// if update cancel the last reminder /////

                            rappelTime = dateTime.getTimeInMillis();
                            rappel = 1;
                        } else Toast.makeText(this, "Choose timer", Toast.LENGTH_SHORT).show();
                    } else {

                        ///// if update cancel the last reminder /////

                        rappelTime = 0;
                        rappel = 0;
                    }
                    if (method.equals("ADD")) {
                        addList(newList1, icon, category_act, listColor, timeCreation);
                    } else {
                        long lastModified = System.currentTimeMillis();
                        modList(newList1, icon, category_act, listColor, lastModified);
                    }

                    /*if(aSwitch.isChecked() && !dateTextView.getText().toString().equals("choose date") && !timeTextView.getText().toString().equals("choose time")){
                        Log.i("CLOCK", "date: "+dateTime.get(Calendar.HOUR_OF_DAY) + "Heure : "+dateTime.get(Calendar.MINUTE));

                        Intent myIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                        myIntent.putExtra("Title","UPEC");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),pendingIntent);
                        Log.i("NOTIFICATION", "FAIT");
                    }else Toast.makeText(getApplicationContext(), "Choose Date & Time", Toast.LENGTH_LONG).show();*/

                } else
                    Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
                doneAndReturn("OK");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doneAndReturn(String response) {
        Intent intent = new Intent();
        intent.putExtra("response", response);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void addList(String newList1, int icon, String category, int listColor, long timeCreation) {
        try {
            jb.put("ajoutlist", newList1);
            jb.put("username", username);
            jb.put("icon", icon);
            jb.put("category", category);
            jb.put("listcolor", listColor);
            jb.put("creation", timeCreation);
            jb.put("modified", timeCreation);
            jb.put("reminder", rappel);
            jb.put("reminderTime", rappelTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new addNewList(jb).execute();
    }

    public class addNewList extends AsyncTask<String, Void, JSONObject> {

        private JSONObject data;

        public addNewList(JSONObject data) {
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Avant", "je vais ajouter une liste");
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                return ConnectionHandler.sendRequest(data);//sendAddListRequest(data);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été créé", Toast.LENGTH_LONG).show();
                    String list_name = "";
                    try {
                        list_name = data.get("ajoutlist").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new addHisto(list_name, "", username, "Nouvelle Liste creer","").execute();
                    //new addHistorique().execute("create list", list_name, " ");//dialog.dismiss();
                    //new TestScreen.showListOfList().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette liste existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "je vais ajouter une liste");
            } else
                Toast.makeText(getApplicationContext(), "Error AddNewList!!!", Toast.LENGTH_LONG).show();
        }
    }

    public void modList(String newList1, int icon, String category, int listColor, long lastModified) {
        try {
            jb.put("modList", newList1);//jb.put("username",username );
            jb.put("lastList", lastListName);
            jb.put("icon", icon);
            jb.put("category", category);
            jb.put("listcolor", listColor);
            jb.put("modified", lastModified);
            jb.put("reminder", rappel);
            jb.put("reminderTime", rappelTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new modifyList(jb).execute();
    }

    public class modifyList extends AsyncTask<String, Void, JSONObject> {

        private String newList;
        private String lastList;
        private JSONObject data;

        public modifyList(JSONObject data) {
            this.data = data;
            try {
                this.newList = data.get("modList").toString();
                this.lastList = data.get("listToMod").toString();
            } catch (JSONException e) {
            }
        }

        @Override
        protected void onPreExecute() {
            Log.i("Avant", "modif liste");
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                return ConnectionHandler.sendRequest(data);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "l'élément a été modifié", Toast.LENGTH_LONG).show();
                    new addHisto(newList, "", username, "List "+ lastList + " modifier en " +newList,"").execute();
                    //new addHistorique().execute("modify list", lastList + " -> " + newList, " ");
                    //new TestScreen.showListOfList().execute();
                }
                Log.i("Après", "modif liste");
            } else
                Toast.makeText(getApplicationContext(), "Error ModifyList!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void chooseIcon(){
    AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ListHandler.this);
    View dialogview = getLayoutInflater().inflate(R.layout.dialog_icons, null);

    setIcons(dialogview);
    iconDialog = dialogbuilder.create();
    iconDialog.setView(dialogview);
    iconDialog.show();
}

    private void setIcons(View dialogView) {
        ((ImageView) dialogView.findViewById(R.id.iconView1)).setTag(R.mipmap.ic_histo);
        ((ImageView) dialogView.findViewById(R.id.iconView2)).setTag(R.mipmap.ic_smiley);
        ((ImageView) dialogView.findViewById(R.id.iconView3)).setTag(R.mipmap.ic_fingerprint);
        ((ImageView) dialogView.findViewById(R.id.iconView4)).setTag(R.mipmap.ic_event);
        ((ImageView) dialogView.findViewById(R.id.iconView5)).setTag(R.mipmap.ic_holyday);
        ((ImageView) dialogView.findViewById(R.id.iconView6)).setTag(R.mipmap.ic_school);
        ((ImageView) dialogView.findViewById(R.id.iconView7)).setTag(R.mipmap.ic_gift);
        ((ImageView) dialogView.findViewById(R.id.iconView8)).setTag(R.mipmap.ic_music);
        ((ImageView) dialogView.findViewById(R.id.iconView9)).setTag(R.mipmap.ic_flower);
        ((ImageView) dialogView.findViewById(R.id.iconView10)).setTag(R.mipmap.ic_world);
        ((ImageView) dialogView.findViewById(R.id.iconView11)).setTag(R.mipmap.ic_cook);
        ((ImageView) dialogView.findViewById(R.id.iconView12)).setTag(R.mipmap.ic_urgent);
        ((ImageView) dialogView.findViewById(R.id.iconView13)).setTag(R.mipmap.ic_thumbdown);
        ((ImageView) dialogView.findViewById(R.id.iconView14)).setTag(R.mipmap.ic_pets);
        ((ImageView) dialogView.findViewById(R.id.iconView15)).setTag(R.mipmap.ic_birthday);
        ((ImageView) dialogView.findViewById(R.id.iconView16)).setTag(R.mipmap.ic_thumbup);
        ((ImageView) dialogView.findViewById(R.id.iconView17)).setTag(R.mipmap.ic_secured);
        ((ImageView) dialogView.findViewById(R.id.iconView18)).setTag(R.mipmap.ic_travel);
        ((ImageView) dialogView.findViewById(R.id.iconView19)).setTag(R.mipmap.ic_business);
        ((ImageView) dialogView.findViewById(R.id.iconView20)).setTag(R.mipmap.ic_movie);
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

    //////////////////////////////////////////////////////////////////////////////////////////////// Date&Time Picker
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel("date");

            View dialogview = getLayoutInflater().inflate(R.layout.dialog_addlist, null);
            TextView t = (TextView) dialogview.findViewById(R.id.dateTextView);
            t.invalidate();
            Log.i("date", "---"+t.getText().toString()+"---");
            timeMainLayout.setVisibility(View.VISIBLE);
            //((LinearLayout) dialogview.findViewById(R.id.timeMainLayout)).setVisibility(View.VISIBLE);

        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            dateTime.set(Calendar.SECOND, 0);
            updateTextLabel("time");
        }
    };

    private void updateDate(){
        new DatePickerDialog(this, d , dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime(){
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }

    private void updateTextLabel(String mode){
        String dTime="";
        if(mode.equals("date")) dTime = DateFormat.format("dd-MM-yyyy", dateTime).toString();
        else if(mode.equals("time")) dTime = DateFormat.format("HH:mm:ss", dateTime).toString();
        tempTextView.setText(dTime);
        tempTextView.invalidate();
        tempTextView=null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////// Voice Recognition
    private void openVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        try {
            startActivityForResult(intent, REQ_VOICE_RECOGNITION_CODE);
        }catch (ActivityNotFoundException e){
            Toast.makeText(ListHandler.this, "Voice Recognition Failed", Toast.LENGTH_SHORT).show();
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
                    tempEditText=null;
                }
                break;
        }
    }

    public void setListIcon(View view) {
        int iconId = Integer.parseInt(((ImageView) view).getTag().toString());
        listIcon.setImageResource(iconId);
        listIcon.setTag(iconId);
        iconDialog.dismiss();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// Color Picker

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
                        Toast.makeText(ListHandler.this, "color : " + selectedColor+ "\n onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_LONG).show();
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
        android.support.v7.app.AlertDialog.Builder dialogbuilder = new android.support.v7.app.AlertDialog.Builder(ListHandler.this);
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
