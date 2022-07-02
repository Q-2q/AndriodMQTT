package com.example.mqtttest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mqtttest.databinding.ActivityMainBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    private TextView mTextView;
    private Button btn;
    private static final String TAG = "MyTag";

    private MqttAndroidClient client;
    private String topic, clientID, MessageReceived;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
        init();
    }

    private void init(){
        btn = findViewById(R.id.btn_sub);
        clientID = "xxx";
        topic = "Test"; //Topic to Sub
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.50.1:1883",
                        clientID);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectX();
                Log.d(TAG,"Attempting to Connect...");
            }
        });
    }

    private void connectX(){
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub(){
        try{
            client.subscribe(topic,1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connectionLost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MessageReceived = new String(message.getPayload());
                    Log.d(TAG,"topic: " + topic);
                    Log.d(TAG,"message: " + MessageReceived);
                    mTextView.setText(MessageReceived);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //toast or log
                }
            });
        }catch (MqttException e){

        }
    }

   private void pub(){
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
   }