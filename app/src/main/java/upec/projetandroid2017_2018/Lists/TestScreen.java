package upec.projetandroid2017_2018.Lists;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import upec.projetandroid2017_2018.DatabaseHandler.ConnectionHandler;
import upec.projetandroid2017_2018.Elements.ElementScreen;
import upec.projetandroid2017_2018.Adapters.MySpinnerAdapter;
import upec.projetandroid2017_2018.HistoData;
import upec.projetandroid2017_2018.R;
import upec.projetandroid2017_2018.Adapters.SwipeRecyclerViewAdapter;

public class TestScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    private TextView tvEmptyTextView;
    private RecyclerView mRecyclerView;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private ArrayList<ItemData> mDataSetCopy = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private String actuel_category,actuel_option;
    private boolean itemDecorationAdded = false;
    String username, password;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRecyclerViewAdapter rAdapter;
    private Spinner mySpinner;
    private MySpinnerAdapter myAdapter;

    private final int REQ_VOICE_RECOGNITION_CODE = 142;
    private final int ADD_METHOD = 1;
    private final int UPDATE_METHOD = 2;
    private EditText tempEditText;
    private TextView tempTextView;
    private Calendar dateTime = Calendar.getInstance();
    private LinearLayout timeMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screen);

        Intent intent = getIntent();
        Log.i("BIPBIP connexion ", " username -> "+intent.getStringExtra("username")+" mdp -> "+intent.getStringExtra("password"));
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        setTitle("");
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        //////////////////////////////////////////////////////////////////////////////////////////// Spinner

        mySpinner = (Spinner) findViewById(R.id.dashspinner);
        mySpinner.setOnItemSelectedListener(this);
        actuel_category ="all";
        actuel_option = "mnotes";
        categories = new ArrayList<>();/*
        categories.add(actuel_category);
        myAdapter = new MySpinnerAdapter(this, categories);
        mySpinner.setAdapter(myAdapter);*/

        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(TestScreen.this, android.R.layout.simple_list_item_1, testStrings);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);*/

        ////////////////////////////////////////////////////////////////////////////////////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestScreen.this, ListHandler.class);
                intent.putExtra("username", username);
                intent.putExtra("method", "ADD");
                intent.putStringArrayListExtra("categories",categories);
                startActivityForResult(intent, ADD_METHOD);
                //addList();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v =navigationView.getHeaderView(0);
        //View v = getLayoutInflater().inflate(R.id.drawer_layout);
        ((TextView) v.findViewById(R.id.userNameNav)).setText(username);
        ((TextView) v.findViewById(R.id.userEmailNav)).setText(username+"@gmail.com");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        tvEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ////////////////////////////////////////////////////////////////////////////////////////////
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new showListOfList().execute();
                new categorySpinnerFill().execute();
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////// Placement d'un adapter vide pour le remplacer après : pour éviter le message warning dans le log : E/RecyclerView﹕ No adapter attached; skipping layout
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {return null;}
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}
            @Override
            public int getItemCount() {return 0;}
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
        new showListOfList().execute();
        new categorySpinnerFill().execute();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_actions);
        SearchView searchView = (SearchView) menuItem.getActionView();//(SearchView) MenuItemCompat.getActionView(menuItem); DEPRECATED
        searchView.setOnQueryTextListener(this);
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// Recherche < implement
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length()==0){
            new showListOfList().execute();
            return true;
        }
        newText = newText.toLowerCase();
        Log.i("Query ", newText);
        ArrayList<ItemData> newList = new ArrayList<>();
        for(ItemData item : mDataSet){
            Log.d("contenu ", item.getName());
            String list = item.getName().toLowerCase();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_his) {
            new showhisto().execute();
            //new showhistorique().execute();
            //Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myNotes) {
            // Handle the camera action
            actuel_option = "mnotes";
            new showListOfList().execute();
        } else if (id == R.id.sharedNotes) {
            actuel_option = "snotes";
            new showListOfList().execute();
        } else if (id == R.id.publicNotes) {
            actuel_option = "pnotes";
            new showListOfList().execute();
        } else if (id == R.id.favoritesNotes) {
            actuel_option = "fnotes";
            new showListOfList().execute();
        } else if (id == R.id.doneNotes) {
            actuel_option = "dnotes";
            new showListOfList().execute();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            StringBuilder text = new StringBuilder();
            text.append("[Collaborative ToDo List Project]");
            text.append("\nFor 3rd Year Computer Science");
            text.append("\nAuthors n°1 : Sasikumar Govindarajalou");
            text.append("\nAuthors n°2 : Yersenia Racerlyn");
            text.append("\nHave a wonderful day :)");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
            startActivity(shareIntent);
        } else if (id == R.id.nav_send) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            StringBuilder text = new StringBuilder();
            text.append(actuel_category + " Listes from "+ actuel_option + " notes");
            for(ItemData it : mDataSet){
                text.append("\n-> "+it.getName());
            }
            shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
            startActivity(shareIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////// Spinner Listener
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String catName = adapterView.getItemAtPosition(i).toString();
        actuel_category = catName;
        //Toast.makeText(this, catName, Toast.LENGTH_LONG).show();
        new showListOfList().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //affichage des listes existant dans la base de donnée________________________________________________________________________________________________________________________________________

    public class showListOfList extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("liste_liste", "list");
                data.put("category", actuel_category);
                data.put("option", actuel_option);
                data.put("value", username);
                /*if(!actuel_category.equals("all")){
                    data.put("category", actuel_category);
                }*/
                return ConnectionHandler.sendRequestToArray(data);//sendShowListRequest(data);
            } catch(Exception e){ e.printStackTrace();return null; }
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

        //mDataSet = new ArrayList<>();//String[] lists = new String[result.length()];
        mDataSet.clear();
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            //Log.i("EXPERIENCE ", item.toString());
            String listName = item.getString("list");
            String username = item.getString("username");
            int icon = item.getInt("icon");
            String catName = item.getString("category");
            String visibility = item.getString("visibility");
            int listColor = Integer.parseInt(item.get("color").toString());
            int favoris = Integer.parseInt(item.get("favoris").toString());
            int pin = Integer.parseInt(item.get("pin").toString());
            int fait = Integer.parseInt(item.get("fait").toString());
            int reminder = Integer.parseInt(item.get("reminder").toString());
            long reminderTime = Long.parseLong(item.get("reminderTime").toString());

            mDataSet.add(new ItemData(listName, username, catName, visibility, listColor, icon, favoris, pin, fait, reminder, reminderTime));
            Log.i("Item "+i, " ->  : "+ item.toString());
        }

        mDataSetCopy = mDataSet; // pour la fonction de recherche

        if(mDataSet.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyTextView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyTextView.setVisibility(View.GONE);
        }

        rAdapter = new SwipeRecyclerViewAdapter(this, mDataSet){
        //SwipeRecyclerViewAdapter mAdapter = new SwipeRecyclerViewAdapter(this, mDataSet){
            @Override
            public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
                super.onBindViewHolder(viewHolder, position);
                final ItemData item = mDataSet.get(position);
                viewHolder.Name.setText(item.getName());
                viewHolder.Info.setText("Created by : " + item.getInfo()+ " - Visibility : "+item.getVisibility());
                viewHolder.swipeLayout.setBackgroundColor(item.getColor());
                viewHolder.listIcon.setImageResource(item.getListIcon());

                if(item.getFavoris()==0)viewHolder.favorite.setVisibility(View.GONE);
                else viewHolder.favorite.setVisibility(View.VISIBLE);

                if(item.getPin()==0)viewHolder.pinned.setVisibility(View.GONE);
                else viewHolder.pinned.setVisibility(View.VISIBLE);

                if(item.getReminder()==1){
                    long timeNow = System.currentTimeMillis();
                    Log.i(".............TimeNow", timeNow+"");
                    Log.i("..............RTime", item.getReminderTime()+"");
                    if(item.getReminderTime() > timeNow){
                        Intent myIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                        myIntent.putExtra("Title",item.getName());
                        myIntent.putExtra("Mode",item.getVisibility());
                        myIntent.putExtra("Category",item.getCategory());
                        myIntent.putExtra("Icon",item.getListIcon());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,item.getReminderTime(),pendingIntent);
                        Log.i("NOTIFICATION", "FAIT");
                        //Toast.makeText(getApplicationContext(), "Notification définie pour "+ item.getName(),Toast.LENGTH_SHORT).show();
                    }
                }

                /*int height = viewHolder.mainSLayout.getLayoutParams().height;
                viewHolder.extrasLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height));
                */
                //viewHolder.swipeLayout.setBackgroundColor(Color.argb(50,50,200,7));
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
                        Log.i("CHECKKKKKKKKKKKK", "username : " +username + " ListAutohr : "+item.getInfo());
                        if(username.toLowerCase().equals(item.getInfo().toLowerCase()) || item.getVisibility().toLowerCase().equals("public")){
                            goToElementActivity(item.getName(), item.getColor(), item.getListIcon(), item.getFavoris(), item.getPin());
                        }else if (item.getVisibility().toLowerCase().equals("shared")){
                            askPassword(item.getInfo(),item.getName(), position);
                        }
                        //Toast.makeText(v.getContext(), " Click ouiiiiiiii : " + item.getName() + " \n" + item.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(((CheckBox) view).isChecked()) new checklist(viewHolder.Name.getText().toString(), 1).execute();//viewHolder.Name.getPaint().setStrikeThruText(true);
                        else new checklist(viewHolder.Name.getText().toString(), 0).execute();//viewHolder.Name.getPaint().setStrikeThruText(false);
                        //viewHolder.Name.invalidate();
                    }
                });

                viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder text = new StringBuilder();
                        text.append("Title : "+mDataSet.get(position).getName());
                        text.append("\nCategory : "+mDataSet.get(position).getCategory());
                        text.append("\nVisibility : "+mDataSet.get(position).getVisibility());
                        text.append("\nCreated by : "+mDataSet.get(position).getInfo());
                        text.append("\nReminder : "+((mDataSet.get(position).getReminder()==0)?"Not":"Yes"));
                        text.append("\nDone : "+((mDataSet.get(position).getFait()==0)?"Not":"Yes"));
                        Toast.makeText(v.getContext(), text.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.Share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new getElementsList(viewHolder.Name.getText().toString()).execute();
                        Toast.makeText(view.getContext(), "Clicked on Share " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //modifyList(viewHolder.Name.getText().toString());
                        //Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(TestScreen.this, ListHandler.class);
                        intent.putExtra("username", username);
                        intent.putExtra("method", "UPDATE");
                        intent.putExtra("fav", mDataSet.get(position).getFavoris());
                        intent.putExtra("pin", mDataSet.get(position).getPin());
                        intent.putExtra("list", viewHolder.Name.getText().toString());
                        intent.putExtra("icon", mDataSet.get(position).getListIcon());
                        intent.putStringArrayListExtra("categories",categories);
                        intent.putExtra("category", mDataSet.get(position).getCategory());
                        intent.putExtra("visibility", mDataSet.get(position).getVisibility());
                        intent.putExtra("color", mDataSet.get(position).getColor());
                        intent.putExtra("reminder", mDataSet.get(position).getReminder());
                        intent.putExtra("reminderTime", mDataSet.get(position).getReminderTime());
                        //intent.putExtra("reminder", mDataSet.get(position).);
                        //intent.putExtra("reminderTime", mDataSet.get(position).);
                        startActivityForResult(intent, UPDATE_METHOD);
                    }
                });

                viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new supprimerList(viewHolder.Name.getText().toString()).execute();
                        mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                        mDataSet.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mDataSet.size());
                        mItemManger.closeAllItems();
                        Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                mItemManger.bindView(viewHolder.itemView, position);
            }
        };

        if(!itemDecorationAdded) { mRecyclerView.addItemDecoration(new DividerItemDecoration( mRecyclerView.getContext(), new LinearLayoutManager(this).getOrientation())); itemDecorationAdded = true;}
        ((SwipeRecyclerViewAdapter) rAdapter).setMode(Attributes.Mode.Single);
        mRecyclerView.setAdapter(rAdapter);
//        this.rAdapter = mAdapter;

        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void askPassword(final String user, String list, final int position) {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TestScreen.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_shared_password, null);
        final TextView passList = (TextView) dialogview.findViewById(R.id.passList);
        passList.setText(list);
        final EditText passwordList = (EditText) dialogview.findViewById(R.id.passwordList);
        final Button enterButton = (Button) dialogview.findViewById(R.id.enterPass);


        final AlertDialog dialogMod = dialogbuilder.create();
        //dialogMod.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordList.getText().toString().isEmpty()){
                    String pwl = passwordList.getText().toString();
                    new passwordVerifier(user, pwl, position).execute();
                    dialogMod.dismiss();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogMod.setView(dialogview);
        dialogMod.show();
    }

    public class passwordVerifier extends AsyncTask<String, Void, JSONObject> {

        private String user;
        private String password;
        private int position;

        public passwordVerifier(String user, String password, int position) {
            this.user = user;
            this.password = password;
            this.position = position;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "Check liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("username", user);
                data.put("password", password);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    ItemData idx = mDataSet.get(position);
                    goToElementActivity(idx.getName(),  idx.getColor(), idx.getListIcon(), idx.getFavoris(), idx.getPin());
                    new addHisto(mDataSet.get(position).getName(), "", username, "List "+ mDataSet.get(position).getName() + " Verifier", "").execute();
                    //new addHistorique().execute("check list", list +" -> "+((checked==0)?"unchecked":"checked"), " ");
                }else Toast.makeText(getApplicationContext(), "Wrong Password!!!", Toast.LENGTH_LONG).show();
            } else Toast.makeText(getApplicationContext(), "Error Password Verifier!!!", Toast.LENGTH_LONG).show();
        }
    }

    public class categorySpinnerFill extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("category_list", "cat");
                return ConnectionHandler.sendRequestToArray(data);//sendShowListRequest(data);
            } catch(Exception e){ e.printStackTrace();return null; }
        }
        @Override
        protected void onPostExecute(JSONArray result) {
            if (result!=null) {
                Log.i("login ----->", "|" + result + "|");
                try {
                    setCatSpinner(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Après", "afficher liste");
            } else Toast.makeText(getApplicationContext(), "Error ShowofList!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void setCatSpinner(JSONArray result) throws JSONException {
        categories.clear();
        ArrayList<String> cats = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.getJSONObject(i);
            String catName = item.getString("category");
            cats.add(catName);
        }
        categories = cats;
        myAdapter = new MySpinnerAdapter(this, categories);
        mySpinner.setAdapter(myAdapter);

        ArrayAdapter myAdap = (ArrayAdapter) mySpinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(actuel_category);
        mySpinner.setSelection(spinnerPosition);
    }

    public class checklist extends AsyncTask<String, Void, JSONObject> {

        private String list;
        private int checked;

        public checklist( String list, int checked) {
            this.list = list;
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
                data.put("checkList", list);
                data.put("checked", checked);
                return ConnectionHandler.sendRequest(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    new addHisto(list, "", username, "List "+ list + " Terminer", "").execute();
                    //new addHistorique().execute("check list", list +" -> "+((checked==0)?"unchecked":"checked"), " ");
                    new showListOfList().execute();
                }
            } else Toast.makeText(getApplicationContext(), "Error CheckList!!!", Toast.LENGTH_LONG).show();
        }
    }

    /*private void modifyList(final String clicked){
        Log.i("modify liste", "onClick___________________________");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TestScreen.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_modify, null);
        final EditText newList = (EditText) dialogview.findViewById(R.id.newelement);
        newList.setText(clicked);
        final Button modList = (Button) dialogview.findViewById(R.id.modify);
        final AlertDialog dialogMod = dialogbuilder.create();
        dialogMod.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
    }*/

    /*spublic class modifyList extends AsyncTask<String, Void, JSONObject> {

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
                    //mRecyclerView.invalidate();
                    new showListOfList().execute();
                }
                Log.i("Après", "modif liste");
            } else Toast.makeText(getApplicationContext(), "Error ModifyList!!!", Toast.LENGTH_LONG).show();
        }
    }*/

    // voir les éléments de la liste
    private void goToElementActivity(String list, int ecolor, int listIcon, int fav, int pin){
        Intent intent = new Intent(TestScreen.this, ElementScreen.class);
        intent.putExtra("listName", list);
        intent.putExtra("ecolor", ecolor);
        intent.putExtra("fav", fav);
        intent.putExtra("pin", pin);
        intent.putExtra("listIcon", listIcon);
        intent.putExtra("username", username);
        startActivity (intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

/**    private void addList(){
        Log.i("add___j'ai cliqué", "onClick___________________________");

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TestScreen.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_addlist, null);
        final EditText newList = (EditText) dialogview.findViewById(R.id.newlist);

        ImageView mic = (ImageView) dialogview.findViewById(R.id.mic);
        /// /final Button addnewList = (Button) dialogview.findViewById(R.id.buttonadd);

        RadioGroup radioGroup =(RadioGroup) dialogview.findViewById(R.id.radiogroup);
        final Button colorPicker = (Button) dialogview.findViewById(R.id.buttonColor);
        colorPicker.setBackgroundColor(Color.WHITE);

        //////////////////////////////////////////////////////////////////////////////////////////// Date&Time Layout
        final Switch aSwitch = (Switch) dialogview.findViewById(R.id.switch1);

        final LinearLayout dateLayout = (LinearLayout) dialogview.findViewById(R.id.dateLayout);
        final TextView dateTextView = (TextView) dialogview.findViewById(R.id.dateTextView);

        timeMainLayout = (LinearLayout) dialogview.findViewById(R.id.timeMainLayout);
        final LinearLayout timeLayout = (LinearLayout) dialogview.findViewById(R.id.timeLayout);
        final TextView timeTextView = (TextView) dialogview.findViewById(R.id.timeTextView);
        //((LinearLayout) dialogview.findViewById(R.id.timeMainLayout)).setVisibility(View.VISIBLE);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dateLayout.setVisibility(View.VISIBLE);
                    //timeMainLayout.setVisibility(View.VISIBLE);
                }else{
                    dateLayout.setVisibility(View.INVISIBLE);
                    timeMainLayout.setVisibility(View.GONE);
                }
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aSwitch.isChecked()){
                    tempTextView = dateTextView;
                    updateDate();
                }
            }
        });
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aSwitch.isChecked()){
                    tempTextView = timeTextView;
                    updateTime();
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        final ArrayList<View> axe = radioGroup.getTouchables();
        final JSONObject jb = new JSONObject();
        try {
            jb.put("visibility", "public");
        } catch (JSONException e) {e.printStackTrace();}
        for(View v : axe) {
            RadioButton r = ((RadioButton) v);
            System.out.println(r.getId());
            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jb.remove("visibility");
                    try {
                        jb.put("visibility", ((RadioButton)view).getText().toString());
                    } catch (JSONException e) {e.printStackTrace();}
                    Toast.makeText(TestScreen.this, "RadioButton Clicked : " + axe.indexOf(view), Toast.LENGTH_SHORT).show();
                }
            });
        }**/
/*
        Intent intent = new Intent(getApplicationContext(),NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmManager.INTERVAL_DAY,pendingIntent);*/
/**
        dialogbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!newList.getText().toString().isEmpty()){
                    if(aSwitch.isChecked() && !dateTextView.getText().toString().equals("choose date") && !timeTextView.getText().toString().equals("choose time")){
                        Log.i("ALARM", "SETTING");**/
                        /*Intent intent = new Intent(getApplicationContext(),NotificationReciever.class);
                        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        //alarmManager.set(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),pendingIntent);
                        hgalarmManager.setRepeating(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),alarmManager.INTERVAL_DAY,pendingIntent);*/
                        /*Intent intent = new Intent(getApplicationContext(),NotificationReciever.class);
                       AlarmManager alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                       PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                       alarmMgr.setRepeating(AlarmManager.RTC, dateTime.getTimeInMillis()/1000,
                                AlarmManager.INTERVAL_DAY, alarmIntent);*/
                        /*Intent myIntent = new Intent(TestScreen.this, AlertReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);
*/
                        //calendar = Calendar.getInstance();
                        /*
                        dateTime.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                        dateTime.set(Calendar.MINUTE, alarmTimePicker.getMinute());*/
                        ///Log.i("CLOCK", "date: "+dateTime.get(Calendar.HOUR_OF_DAY) + "Heure : "+dateTime.get(Calendar.MINUTE));

                        /*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);*/
                        /**Intent myIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                        myIntent.putExtra("Title","UPEC");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),pendingIntent);
                        Log.i("NOTIFICATION", "FAIT");

                    }else Toast.makeText(getApplicationContext(), "Choose Date & Time", Toast.LENGTH_LONG).show();
                    String listToAdd = newList.getText().toString();
                    int listColor = ((ColorDrawable) colorPicker.getBackground()).getColor();
                    try {
                        jb.put("ajoutlist",listToAdd );
                        jb.put("username",username );
                        jb.put("listcolor",listColor);
                    } catch (JSONException e) {e.printStackTrace();}
                    new addNewList(jb).execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });
        dialogbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempEditText = newList;
                openVoiceRecognition();
            }
        });

        final AlertDialog dialog = dialogbuilder.create();**/
        /*addnewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newList.getText().toString().isEmpty()){
                    String listToAdd = newList.getText().toString();
                    int listColor = ((ColorDrawable) colorPicker.getBackground()).getColor();
                    try {
                        jb.put("ajoutlist",listToAdd );
                        jb.put("username",username );
                        jb.put("listcolor",listColor);
                    } catch (JSONException e) {e.printStackTrace();}
                    new addNewList(jb, dialog).execute();
                } else Toast.makeText(getApplicationContext(), "empty fields", Toast.LENGTH_LONG).show();
            }
        });*//**
        dialog.setView(dialogview);
        dialog.show();
    }**/

   /** public class addNewList extends AsyncTask<String, Void, JSONObject> {

        private JSONObject data;

        public addNewList(JSONObject data) {
            this.data = data;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "je vais ajouter une liste");
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                return ConnectionHandler.sendRequest(data);//sendAddListRequest(data);
            } catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            if (result!=null) {
                if (result.has("success")) {
                    Toast.makeText(getApplicationContext(), "la liste a été créé", Toast.LENGTH_LONG).show();
                    String list_name="";
                    try {
                        list_name = data.get("ajoutlist").toString();
                    } catch (JSONException e) {e.printStackTrace();}
                    new addHistorique().execute("create list", list_name, " ");//dialog.dismiss();
                    new showListOfList().execute();
                } else
                    Toast.makeText(getApplicationContext(), "Cette liste existe déjà", Toast.LENGTH_LONG).show();
                Log.i("Après", "je vais ajouter une liste");
            } else Toast.makeText(getApplicationContext(), "Error AddNewList!!!", Toast.LENGTH_LONG).show();
        }
    }**/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class supprimerList extends AsyncTask<String, Void, JSONObject> {

        private String clicked;

        public supprimerList(String clicked) {
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
                    new addHisto(clicked, "", username, "List " +clicked + " supprimer", "").execute();
                    //new addHistorique().execute("delete list", clicked, " ");//dialogChoix.dismiss();
                    new showListOfList().execute();
                }
                Log.i("Après", "supprimer la liste");
            } else Toast.makeText(getApplicationContext(), "Error SupprimerList!!!", Toast.LENGTH_LONG).show();
        }
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
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TestScreen.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_historique, null);
        ListView listView = (ListView)  dialogview.findViewById(R.id.listV);
        listView.setAdapter(customAdapter);
        dialogbuilder.setView(dialogview);
        dialogbuilder.create().show();
    }

    class CustomAdapter extends BaseAdapter{

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
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(TestScreen.this);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_historique, null);
        ListView listView = (ListView)  dialogview.findViewById(R.id.listV);
        listView.setAdapter(adapter);
        dialogbuilder.setView(dialogview);
        dialogbuilder.create().show();
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

    public class getElementsList extends AsyncTask<String, Void, JSONArray> {

        private String list;

        public getElementsList(String list) {
            this.list = list;
        }

        @Override
        protected void onPreExecute(){
            Log.i("Avant", "afficher liste des éléments");
        }
        @Override
        protected JSONArray doInBackground(String... arg0) {
            try {
                JSONObject data = new JSONObject();
                data.put("liste", list);
                Log.i("LIST ", list);
                return ConnectionHandler.sendRequestToArray(data); }
            catch(Exception e){ return null; }
        }
        @Override
        protected void onPostExecute(JSONArray result) {
            if (result!=null) {
                Log.i("RESULT ----->", "|" + result.toString() + "|");

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                StringBuilder text = new StringBuilder();
                text.append("Liste : "+list);
                //String text = jsonArray.toString();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = null;
                    try {
                        item = result.getJSONObject(i);
                        text.append("\n-> "+item.get("element"));
                        Log.i("item "+i, " ->  : "+ item.toString());
                    } catch (JSONException e) {e.printStackTrace();}
                }
                Log.i("SHARE TEXT", text.toString());
                shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
                startActivity(shareIntent);
                Log.i("Après", "afficher liste des éléments");
            } else Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_LONG).show();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////// Date&Time Picker
    /*DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
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
    }*/
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////// Voice Recognition
   /* private void openVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        try {
            startActivityForResult(intent, REQ_VOICE_RECOGNITION_CODE);
        }catch (ActivityNotFoundException e){
            Toast.makeText(TestScreen.this, "Voice Recognition Failed", Toast.LENGTH_SHORT).show();
        }
    }*/

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
            case ADD_METHOD:
            case UPDATE_METHOD:
                if (resultCode == RESULT_OK && data!=null) {
                    String response = data.getStringExtra("response");
                    if(response.equals("OK")) {
                        new showListOfList().execute();
                        //Toast.makeText(this, "Add success", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
/*
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
                        Toast.makeText(TestScreen.this, "color : " + selectedColor+ "\n onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_LONG).show();
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
    }*/

}
