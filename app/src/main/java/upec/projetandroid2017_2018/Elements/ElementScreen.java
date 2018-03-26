package upec.projetandroid2017_2018.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import upec.projetandroid2017_2018.Adapters.ElementRecyclerViewAdapter;
import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.Elements.ElementData;
import upec.projetandroid2017_2018.HistoData;
import upec.projetandroid2017_2018.Lists.ListHandler;
import upec.projetandroid2017_2018.Lists.TestScreen;
import upec.projetandroid2017_2018.R;

public class ElementScreen extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private String list,username;
    private int listIcon, fav, pin;
    private TextView tvEmptyTextView;
    private ImageView titleView;
    private RecyclerView mRecyclerView;
    private ElementRecyclerViewAdapter rAdapter;
    private boolean itemDecorationAdded = false;
    private ArrayList<ElementData> mDataSet = new ArrayList<>();
    private ArrayList<ElementData> mDataSetCopy = new ArrayList<>();
    private final int ADD_METHOD = 1;
    private final int UPDATE_METHOD = 2;
    private final int ONEELEMENT_METHOD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////////////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ////////////////////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();
        list = intent.getStringExtra("listName");
        username = intent.getStringExtra("username");
        listIcon = intent.getIntExtra("listIcon",R.mipmap.ic_home);
        int ecolor = intent.getIntExtra("ecolor",Color.WHITE);
        fav = intent.getIntExtra("fav",0);
        pin = intent.getIntExtra("pin",0);


        Log.i("COLOR", "onCreate: COOOOOOOOOOOOOOOOOOOOLOR"+ecolor);
        if(ecolor < -10000 || ecolor >10000){
            ////////////////////////////////////////////////////////////////////////////////////////////
            AppBarLayout abl = (AppBarLayout) findViewById(R.id.app_bar_layout);
            abl.setBackgroundColor(ecolor);
            ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout)).setContentScrimColor(ecolor);
            ////////////////////////////////////////////////////////////////////////////////////////////
        }
        setTitle(list);
        toolbar.setTitle(list);
        ////////////////////////////////////////////////////////////////////////////////////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.efab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ElementScreen.this, ElementHandler.class);
                intent.putExtra("listName", list);
                intent.putExtra("username", username);
                intent.putExtra("method", "ADD");
                startActivityForResult(intent, ADD_METHOD);
            }
        });

        FloatingActionButton eFav = (FloatingActionButton) findViewById(R.id.eFav);
        eFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setFavorite(list, ((fav==0)?1:0)).execute();
            }
        });

        FloatingActionButton ePin = (FloatingActionButton) findViewById(R.id.ePin);
        ePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setPin(list, ((pin==0)?1:0)).execute();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        titleView = (ImageView) findViewById(R.id.titleView);
        titleView.setImageResource(listIcon);
        tvEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {return null;}
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}
            @Override
            public int getItemCount() {return 0;}
        });

        new showListOfElement().execute();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /*Intent back = new Intent(this, ElementScreen.class);
        startActivity(back);*/
        Intent intent = new Intent();
        intent.putExtra("username", username);
        intent.putExtra("response", "NON");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        Log.i("hg", "onBackPressed: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainelement, menu);
        MenuItem menuItem = menu.findItem(R.id.action_actions);
        SearchView searchView = (SearchView) menuItem.getActionView();//(SearchView) MenuItemCompat.getActionView(menuItem); DEPRECATED
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_act) {
            new showListOfElement().execute();
            //new TestScreen.showhistorique().execute();
            //Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }else if (id == R.id.action_his) {
            new showhisto().execute();
            //new TestScreen.showhistorique().execute();
            //Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// Recherche < implement
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length()==0){
            new showListOfElement().execute();
            return true;
        }
        newText = newText.toLowerCase();
        Log.i("Query ", newText);
        ArrayList<ElementData> newList = new ArrayList<>();
        for(ElementData item : mDataSet){
            Log.d("contenu ", item.getElementName());
            String list = item.getElementName().toLowerCase();
            if(list.contains(newText)){
                Log.i("List : ", list);
                Log.i("newText : ", newText);
                newList.add(item);
            }
        }
        rAdapter.setFilters(newList);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
            } else Toast.makeText(getApplicationContext(), "Error ShowElements!!!", Toast.LENGTH_LONG).show();
        }
    }

    //affichage
    private void showResult(JSONArray result) throws JSONException {
/*
        String[] lists = new String[result.length()];
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            lists[i] = item.getString("element");
            Log.i("item "+i, " ->  : "+ lists[i]);
        }*/

        mDataSet.clear();
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            //Log.i("EXPERIENCE ", item.toString());
            String elementName = item.getString("element");
            String description = item.getString("description");
            String username = item.getString("username");
            int ecolor = item.getInt("ecolor");
            int priority = item.getInt("priority");
            int fait = item.getInt("fait");
            mDataSet.add(new ElementData(elementName, description, username, ecolor, priority, fait));
            Log.i("item "+i, " Element : "+elementName+" Description : "+description+ " username : "+username + " ecolor : "+ecolor + " priority : "+ priority + " fait : "+fait);
        }

        mDataSetCopy = mDataSet; // pour la fonction de recherche

        if(mDataSet.isEmpty()){
            Log.i("CONTENU", "showResult: EMPTY");
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            Log.i("CONTENU", "showResult: NOT-EMPTY");
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyTextView.setVisibility(View.GONE);
        }

        rAdapter = new ElementRecyclerViewAdapter(this, mDataSet){
            //SwipeRecyclerViewAdapter mAdapter = new SwipeRecyclerViewAdapter(this, mDataSet){

            @Override
            public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
                super.onBindViewHolder(viewHolder, position);
                final ElementData item = mDataSet.get(position);
                viewHolder.Name.setText(item.getElementName());
                Log.i("ELEMENT", "onBindViewHolder: ElementName : " + item.getElementName());
                viewHolder.Info.setText("Created by : " + item.getUsername());
                viewHolder.swipeLayout.setBackgroundColor(item.getEcolor());
                viewHolder.Priority.setText(String.valueOf(item.getPriority()));

                ////////////////////////////////////////////////////////////////////////////////////
                int fait = item.getFait();
                if(fait==0) {
                    viewHolder.Name.getPaint().setStrikeThruText(false);
                    viewHolder.checkBox.setChecked(false);
                } else {
                    viewHolder.Name.getPaint().setStrikeThruText(true);
                    viewHolder.checkBox.setChecked(true);
                    viewHolder.swipeLayout.setBackgroundColor(Color.argb(50,10,10,10));
                }
                //viewHolder.Name.invalidate();
                ////////////////////////////////////////////////////////////////////////////////////

                viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
                viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));

                viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToOneElementActivity(mDataSet.get(position).getElementName(), mDataSet.get(position).getPriority(), mDataSet.get(position).getEcolor(), mDataSet.get(position).getDescription());
                        //Toast.makeText(v.getContext(), " Click ouiiiiiiii : " + item.getName() + " \n" + item.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.Name.getPaint().setStrikeThruText(true);
                        if(((CheckBox) view).isChecked()) new checklist(viewHolder.Name.getText().toString(), 1).execute();//viewHolder.Name.getPaint().setStrikeThruText(true);
                        else new checklist(viewHolder.Name.getText().toString(), 0).execute();//viewHolder.Name.getPaint().setStrikeThruText(false);
                        //viewHolder.Name.invalidate();
                    }
                });

                viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder text = new StringBuilder();
                        text.append("Title : "+mDataSet.get(position).getElementName());
                        text.append("\nDescription : "+mDataSet.get(position).getDescription());
                        text.append("\nPriority : "+mDataSet.get(position).getPriority());
                        text.append("\nCreated by : "+mDataSet.get(position).getUsername());
                        text.append("\nDone : "+((mDataSet.get(position).getFait()==0)?"Not":"Yes"));
                        Toast.makeText(v.getContext(), text.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.Share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        StringBuilder text = new StringBuilder();
                        text.append("Liste : "+list);
                        text.append("\n->Element : " + mDataSet.get(position).getElementName());
                        text.append("\n->Description : " + mDataSet.get(position).getDescription());
                        text.append("\n->Priority : " + mDataSet.get(position).getPriority() +"/10");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
                        startActivity(shareIntent);
                    }
                });

                viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //modifyList(viewHolder.Name.getText().toString());
                        //Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ElementScreen.this, ElementHandler.class);
                        intent.putExtra("username", username);
                        intent.putExtra("method", "UPDATE");
                        intent.putExtra("listName", list);
                        intent.putExtra("element", mDataSet.get(position).getElementName());
                        intent.putExtra("description", mDataSet.get(position).getDescription());
                        intent.putExtra("ecolor", mDataSet.get(position).getEcolor());
                        intent.putExtra("priority", mDataSet.get(position).getPriority()-1);
                        startActivityForResult(intent, UPDATE_METHOD);
                    }
                });

                viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new supprimerElement(mDataSet.get(position).getElementName(), list).execute();
 //                       new supprimerList(viewHolder.Name.getText().toString()).execute();
                        mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                        mDataSet.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mDataSet.size());
                        mItemManger.closeAllItems();
                        Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                Log.i("OVERRIDE", "TESTTTTT");
                mItemManger.bindView(viewHolder.itemView, position);
            }
        };
        if(!itemDecorationAdded) { mRecyclerView.addItemDecoration(new DividerItemDecoration( mRecyclerView.getContext(), new LinearLayoutManager(this).getOrientation())); itemDecorationAdded = true;}
        ((ElementRecyclerViewAdapter) rAdapter).setMode(Attributes.Mode.Single);
        mRecyclerView.setAdapter(rAdapter);
        Log.i("OVERRIDE", "TESTTTTT2222");

        /*
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
                        //new supprimerElement(dialogChoix, clicked).execute();
                    }
                });
                modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoix.dismiss();
                        //modifyElement(clicked);
                    }
                });
                dialogChoix.setView(dialogview);
                dialogChoix.show();
            }
        });*/
    }

    public class setFavorite extends AsyncTask<String, Void, JSONObject> {

        private String list;
        private int fav;

        public setFavorite(String list, int fav) {
            this.list = list;
            this.fav = fav;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Check liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("favList", list);
                data.put("heart", fav);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    String tag = "";
                    if(fav==0) tag = "UnFavorite";
                    else tag = "Favorite";
                    new addHisto(list, "", username, list + " favoris","").execute();
                    //new TestScreen.addHistorique().execute("check list", list +" -> "+((checked==0)?"unchecked":"checked"), " ");
                    Toast.makeText(getApplicationContext(), "List : "+list+" "+tag, Toast.LENGTH_LONG).show();
                }
            } else Toast.makeText(getApplicationContext(), "Error FavList!!!", Toast.LENGTH_LONG).show();
        }
    }

    public class setPin extends AsyncTask<String, Void, JSONObject> {

        private String list;
        private int pin;

        public setPin(String list, int pin) {
            this.list = list;
            this.pin = pin;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Check liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("pinList", list);
                data.put("pin", pin);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    String tag = "";
                    if(fav==0) tag = "UnPinned";
                    else tag = "Pinned";
                    new addHisto(list, "", username, list + " pinned","").execute();
                    //new TestScreen.addHistorique().execute("check list", list +" -> "+((checked==0)?"unchecked":"checked"), " ");
                    Toast.makeText(getApplicationContext(), "List : "+list+" "+tag, Toast.LENGTH_LONG).show();
                }
            } else Toast.makeText(getApplicationContext(), "Error PinList!!!", Toast.LENGTH_LONG).show();
        }
    }

    // voir un élément de la liste
    private void goToOneElementActivity(String element, int prio, int ecolor, String desscription){
        Intent intent = new Intent(ElementScreen.this, OneElement.class);
        intent.putExtra("element", element);
        intent.putExtra("priority", prio);
        intent.putExtra("ecolor", ecolor);
        intent.putExtra("description", desscription);
        startActivityForResult(intent, ONEELEMENT_METHOD);
    }

    public class checklist extends AsyncTask<String, Void, JSONObject> {

        private String element;
        private int checked;

        public checklist( String element, int checked) {
            this.element = element;
            this.checked = checked;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Check liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("checkElement", element);
                data.put("checked", checked);
                data.put("listName", list);
                Log.i("CHECKED", "doInBackground: element " + element +" checked : "+checked);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    new addHisto(list, element, username, "Element "+ element + " terminer","").execute();
                    //new TestScreen.addHistorique().execute("check list", list +" -> "+((checked==0)?"unchecked":"checked"), " ");
                    new showListOfElement().execute();
                }
            } else Toast.makeText(getApplicationContext(), "Error CheckList!!!", Toast.LENGTH_LONG).show();
        }
    }

    //supprimer un élément de la liste ___________________________________________________________________________________________________________________________________

    public class supprimerElement extends AsyncTask<String, Void, JSONObject> {

        private String clicked;
        private String list;

        public supprimerElement(String clicked, String list) {
            this.clicked = clicked;
            this.list = list;
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
                    new addHisto(list, clicked, username, "Supprimer "+ clicked,"").execute();
                    //new ElementActivity.addHistorique().execute("delete element", list, clicked);
                    new showListOfElement().execute();
                }
                Log.i("Après", "supprimer élément à la liste");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            /*case REQ_VOICE_RECOGNITION_CODE:
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> voiceText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tempEditText.setText(voiceText.get(0));
                    tempEditText=null;
                }
                break;*/
            case ADD_METHOD:
            case UPDATE_METHOD:
            case ONEELEMENT_METHOD:
                if (resultCode == RESULT_OK && data!=null) {
                    String response = data.getStringExtra("response");
                    if(response.equals("OK")) {
                        Log.i("BOP", "onActivityResult: success");
                       new showListOfElement().execute();
                        //Toast.makeText(this, "Add success", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
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
                data.put("histoES", "");
                data.put("value", list);

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
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(ElementScreen.this);
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
