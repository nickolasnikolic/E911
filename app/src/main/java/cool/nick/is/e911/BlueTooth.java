package cool.nick.is.e911;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.lang.*;


public class BlueTooth extends Thread {

    private String name = null;
    private UUID myUuid = null;
    private String mac = null;

    private BluetoothDevice device = null;
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final InputStream mmInStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };
    private final OutputStream mmOutStream = new OutputStream() {
        @Override
        public void write(int oneByte) throws IOException {

        }
    };


    public BlueTooth( String NAME, UUID MY_UUID, Context theContext ) {

        name = NAME;
        myUuid = MY_UUID;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Toast.makeText(theContext, device.getName() + "\n" + device.getAddress(), Toast.LENGTH_LONG).show();
            }
        }

        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                Toast.makeText(context, "" + device.getName() + "\n" + device.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private void manageConnectedSocket(BluetoothSocket mmSocket){
        ;

        mBluetoothAdapter.cancelDiscovery();
        byte[] message = new byte[1];
        int readThisToday;


        while(true){
            try{
                mmSocket.connect();

                // Read from the InputStream
                readThisToday = mmInStream.read(message);
                //if you have received the 1 byte message
                if(readThisToday == 0) {
                    // Send the obtained bytes to the UI activity
                    mmOutStream.write(message);
                }
            }catch (IOException e){}
        }

    }

    @Override
    public void run() {
        BluetoothSocket socket;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                }catch(IOException e){}
                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}