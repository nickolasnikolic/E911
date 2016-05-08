package cool.nick.is.e911;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private UUID connectionUUID = java.util.UUID.fromString("00001523-1212-EFDE-1523-785FEABCD123");
    private UUID notifyUUID = java.util.UUID.fromString("00001524-1212-EFDE-1523-785FEABCD123");
    private UUID writeUUID = java.util.UUID.fromString("00001525-1212-EFDE-1523-785FEABCD123");

    private String phoneNumber;
    private Thread bt = null;

    public void callPhone(String number){
        //call phone number saved in class var
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        Toast.makeText(getApplicationContext(), "calling " + number,
                Toast.LENGTH_LONG).show();
        try {
            startActivity(callIntent);
        }catch(SecurityException e){
            Toast.makeText(getApplicationContext(), "NOT calling " + number,
                    Toast.LENGTH_LONG).show();
        }
    }

    //an early declaration of the save function used on the save button
    public String save( View v ){
        //upon save button press save phone text to class var
        EditText theView = (EditText) findViewById(R.id.phoneNumberText);
        phoneNumber = theView.getText().toString();
        return phoneNumber;
    }

    //an early declaration of the call function used on the call button
    public void call(View v){
        callPhone(phoneNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BlueTooth("CA-50i", connectionUUID, "FC-58-FA-14-60-b6", getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();

        if (bt == null) {
            // Device does not support Bluetooth

            // show alert if bt is disabled
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("No Bluetooth!").setTitle("Whoops!");

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();

        } else {
            //you have bluetooth, use it
            bt.run();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
