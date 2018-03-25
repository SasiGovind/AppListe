package upec.projetandroid2017_2018.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import upec.projetandroid2017_2018.CustomSeekBarLabels;
import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.HistoData;
import upec.projetandroid2017_2018.R;

public class OneElement extends AppCompatActivity {

    private TextView oneElement;
    private TextView oneDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_element);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Element");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        oneElement = (TextView) findViewById(R.id.oneElement);
        oneDescription = (TextView) findViewById(R.id.oneDescription);

        LinearLayout mSeekLin = (LinearLayout) findViewById(R.id.seekBarLayout2);
        CustomSeekBarLabels customSeekBarLabels = new CustomSeekBarLabels(this, 10, Color.DKGRAY);
        customSeekBarLabels.addSeekBar(mSeekLin);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekPrio2);
        seekBar.setEnabled(false);

        Intent parent = getIntent();
        String element = parent.getStringExtra("element");
        int prio = parent.getIntExtra("priority",5);
        String description = parent.getStringExtra("description");
        int ecolor = parent.getIntExtra("ecolor",Color.BLACK);

        AppBarLayout abl = (AppBarLayout) findViewById(R.id.oneAppBarLayout);
        abl.setBackgroundColor(ecolor);
/**
        mSeekLin.setBackgroundColor(ecolor);
        mSeekLin.getBackground().setAlpha(50);*/

        LinearLayout oneELayout = (LinearLayout) findViewById(R.id.oneELayout);
        oneELayout.setBackgroundColor(ecolor);
        oneELayout.getBackground().setAlpha(60);

        oneElement.setText(element);
        oneElement.setBackgroundColor(ecolor);
        oneElement.getBackground().setAlpha(90);
        oneDescription.setText(description);
        seekBar.setProgress(prio);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*Intent back = new Intent(this, ElementScreen.class);
        startActivity(back);*/
        Intent intent = new Intent();
        intent.putExtra("response", "OK");
        setResult(Activity.RESULT_OK, intent);
        finish();
        Log.i("hg", "onBackPressed: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.one_element, menu);
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
            case R.id.one_histo:
                new showhisto().execute();
                String response = "Error";
                break;
        }
        return super.onOptionsItemSelected(item);
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
                data.put("value", oneElement.getText().toString());
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
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(OneElement.this);
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