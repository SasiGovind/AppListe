package upec.projetandroid2017_2018.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import upec.projetandroid2017_2018.CustomSeekBarLabels;
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
                String response = "Error";
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}