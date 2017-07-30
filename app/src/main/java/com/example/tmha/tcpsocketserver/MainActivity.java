package com.example.tmha.tcpsocketserver;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private final int SERVER_PORT = 8080; //Define the server port
    private EditText mEdtServerIp, mEdtPort, mEdtMessage;
    private TextView mTxtChat;
    private ServerSocket mServerSocket;
    private  Socket mSocketClient;
    private String mStr, mMessage = "";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getDeviceIpAddress();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true){
//                        String s = "111";
//                        s = s + "gggg" ;
//                    }
//                }catch (Exception e){
//
//                }
//            }
//        }).start();



        mTxtChat.setText("Server host on: "+ mEdtServerIp.getText().toString());


//
//        Thread thread = new Thread(new serverThread());
//        thread.start();


        //New thread to listen
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to port
                    mServerSocket = new ServerSocket(SERVER_PORT);
                    //Create server side client socket reference
                    mSocketClient = null;
                    while (true){
                        //Accept the client connection and hand over communication to server side client socket

                        String s = "kfkfk";
                        mSocketClient = mServerSocket.accept();
                        //For each client new instance of server asynctask will be created
                        Server server = new Server("Hello client",new Server.CallBack() {
                            @Override
                            public void setMessage(String s) {
                                String chat = mTxtChat.getText().toString();
                                chat = chat + "\nClient: "+ s;
                                mTxtChat.setText(chat);

                            }
                        });
                        server.execute(mSocketClient);
                        String i = "kkkkkk";
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        mEdtServerIp = (EditText) findViewById(R.id.edt_server_ip);
        mEdtPort     = (EditText) findViewById(R.id.edt_port);
        mEdtMessage  = (EditText) findViewById(R.id.edt_messages);
        mTxtChat     = (TextView) findViewById(R.id.txt_chat);

        mEdtPort.setText(String.valueOf(SERVER_PORT));
    }

    public void getDeviceIpAddress(){
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements(); ){
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumeration1IpAddr = networkInterface
                        .getInetAddresses(); enumeration1IpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumeration1IpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4){
                        mEdtServerIp.setText(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(View view) {
        Thread sendThread = new Thread(new sentMessage());
        sendThread.start();
//
//        mStr = mEdtMessage.getText().toString();
//        String chat = mTxtChat.getText().toString();
//        chat = chat + "\n Server : " + mStr;
//        mTxtChat.setText(chat);
//        mEdtMessage.setText("");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
////                    //Create a server socket object and bind it to port
////                    mServerSocket = new ServerSocket(SERVER_PORT);
////                    //Create server side client socket reference
////                    Socket socketClient = null;
//
//                    //Accept the client connection and hand over communication to server side client socke
//                    //For each client new instance of server asynctask will be created
//
//                    Server server = new Server(mStr, new Server.CallBack() {
//                        @Override
//                        public void setMessage(String s) {
//                            String chat = mTxtChat.getText().toString();
//                            chat = chat + "\nClient: " + s;
//                            mTxtChat.setText(chat);
//                        }
//                    });
//                    server.execute(mSocketClient);
//
//
//
//            }
//        }).start();
    }

    public class serverThread implements Runnable {
        @Override
        public void run()
        {
            try
            {
                while(true)
                {
                    mServerSocket = new ServerSocket(SERVER_PORT);
                    Socket client = mServerSocket.accept();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTxtChat.setText("Connected");
                        } });
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    String line = null; while((line = in.readLine()) != null) {
                    mMessage = mMessage + "\n Client : " + line;
                    mHandler.post(new Runnable() {
                        @Override public void run()
                        { mTxtChat.setText(mMessage); } }); }
                    in.close();
                    client.close();
                    Thread.sleep(100);
                }
            } catch (Exception e) {
            }
        }
    }


    class sentMessage implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                Socket client = mServerSocket.accept();
                DataOutputStream os = new
                        DataOutputStream(client.getOutputStream());
                mStr = mEdtMessage.getText().toString();
                String chat = mTxtChat.getText().toString();
                chat = chat + "\n Server : " + mStr;
                final String finalChat = chat;
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTxtChat.setText(finalChat);
                    }
                });
                os.writeBytes(mStr);
                os.flush();
                os.close();
                client.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
