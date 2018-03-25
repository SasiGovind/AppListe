package upec.projetandroid2017_2018;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import upec.projetandroid2017_2018.Accueil.AccueilActivity;

public class Blabla extends AppCompatActivity {

    DataBase db;

    EditText username, password;
    Button connexion, inscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        db = new DataBase(this);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);


        connexion = (Button) findViewById(R.id.connexion);
        inscription = (Button) findViewById(R.id.inscription);


    }

    private void addUser(){
        boolean isInsered = db.addUser(username.getText().toString(), password.getText().toString());
        if (isInsered) Toast.makeText(Blabla.this, "Data Insered", Toast.LENGTH_LONG).show();
        else Toast.makeText(Blabla.this, "Data NOT Insered", Toast.LENGTH_LONG).show();
    }

    private void deleteUser(){
        boolean b = db.deleteUser(username.getText().toString(), password.getText().toString());
        if (b) Toast.makeText(Blabla.this, "Deleted", Toast.LENGTH_LONG).show();
        else  Toast.makeText(Blabla.this, "NOT Deleted", Toast.LENGTH_LONG).show();
    }

    private void showUser(){
                Cursor users = db.getAllUsers();
                if (users.getCount()==0){
                    showMessage("Error", "NOT FOUND");
                    return;
                }
                StringBuffer info = new StringBuffer();
                while (users.moveToNext()){
                    info.append("Id : "+users.getString(0)+"\n");
                    info.append("UserName : /"+users.getString(1)+"/\n");
                    info.append("Password : /"+users.getString(2)+"/\n");
                }
                showMessage("usersList", info.toString());
    }

    private void showMessage(String title, String msg){
        Log.i(title, "message: "+msg);
        AlertDialog.Builder print = new AlertDialog.Builder(this);
        print.setCancelable(true);
        print.setTitle(title);
        print.setMessage(msg);
        print.show();
    }
}
