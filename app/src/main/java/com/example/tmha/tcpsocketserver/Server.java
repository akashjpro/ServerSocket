package com.example.tmha.tcpsocketserver;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by tmha on 7/28/2017.
 */

public class Server extends AsyncTask<Socket, Void, String> {
    private  CallBack mCallBack;
    private String mMessage;

    public Server(String mMessage, CallBack mCallBack) {
        this.mCallBack = mCallBack;
        this.mMessage  = mMessage;
    }

    @Override
    protected String doInBackground(Socket... sockets) {
        String result = null;
        //Get the accepted socket object
        Socket socket = sockets[0];
        try {
            //Get the data input stream comming from the client
            InputStream is = socket.getInputStream();
            //Get the output stream to the client
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //write data to the data output stream
            out.println(mMessage);
            //Buffer the data input stream
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //Read content of the data buffer
            result = br.readLine();
            //Close the client connection
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallBack.setMessage(s);
    }

    public interface CallBack{
        void setMessage(String s);
    }
}
