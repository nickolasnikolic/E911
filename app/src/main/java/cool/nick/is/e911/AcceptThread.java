package cool.nick.is.e911;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class AcceptThread extends Thread {
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


    public AcceptThread( String NAME, UUID MY_UUID) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    private void manageConnectedSocket(){
        byte[] message = new byte[1];
        int readThisToday;

        while(true){
            try{
                // Read from the InputStream
                readThisToday = mmInStream.read(message);
                // Send the obtained bytes to the UI activity
                mmOutStream.write(message);
            }catch (IOException e){}
        }

    }

    public void run() {
        BluetoothSocket socket = null;
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
                manageConnectedSocket();
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