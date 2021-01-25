package com.example.gryffindor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Executor newExecutor = Executors.newSingleThreadExecutor();
        FragmentActivity activity = this;

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {
                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Fingerprint recognised successfully");

                TextView iptxt =  (TextView)  findViewById( R.id.edIP_address );
                sendMessage( iptxt.getText().toString(), "Smart Lock\n");


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Fingerprint not recognised");
            }


        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Scan your fingerprint to unlock.")
                .setDescription("Smart lock Arduino/ESP32 Project Unlock door with Fingerprint sensor from Android ")
                .setNegativeButtonText("Cancel")
                .build();

        findViewById(R.id.btnlaunch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "^((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}$";
                TextView  iptxt =  (TextView)  findViewById( R.id.edIP_address );
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Enter ESP32 IP address." );
                final AlertDialog.Builder ok = builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                {
                    DialogInterface dialog;
                    int id;
                    {
                        // Do something
                    }
                }


                if( iptxt.getText().toString().matches(regex)  ) {

                    myBiometricPrompt.authenticate(promptInfo);
                    Log.d(TAG, "Test send data to esp32");
                }else {
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }


            }
        });

    }

    private void sendMessage(final String ip , final String msg) {

        Runnable runSend = new Runnable() {
            public void run() {
                try {
                    Socket s = new Socket(ip
                            , 80);

                    BufferedWriter out = new BufferedWriter
                            (new OutputStreamWriter(s.getOutputStream()));
                    String outgoingMsg = msg;
                    out.write(outgoingMsg);
                    out.flush();
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run()
                        {
                            //txtStatus.setText("Message has been sent.");
                            //etxtMessage.setText("");
                        }
                    });
                    Log.i("Sender", outgoingMsg);
                    s.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    setText("No device on this IP address.");
                } catch (Exception e) {
                    e.printStackTrace();
                    setText("Connection failed. Please try again.");
                }
            }

            public void setText(String str) {
                final String string = str;
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run()
                    {
                        //txtStatus.setText(string);
                    }
                });
            }
        };
        new Thread(runSend).start();


    }


}


