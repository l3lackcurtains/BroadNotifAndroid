package com.example.ravi.broadnotif;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {
    public static boolean isService = false;
    private static final String TAG = "MyActivity";
    TextView response;
    Button buttonConnect, buttonClear, buttonNotif;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);
        buttonNotif = (Button) findViewById(R.id.notif);
        buttonNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,BackgroundService.class);
                i.putExtra("context",String.valueOf(getApplicationContext()));
                startService(i);
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                isService = true;
            }
        });


        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    socket = IO.socket("http://192.168.100.28:3000");
                    socket.connect();
                    socket.emit("start", "android");
                    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "connected");
                        }

                    });
                    socket.on("newemail", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            JSONObject data = (JSONObject) args[0];
                            Log.d(TAG, "received email");
                            try {
                                String callFrom = data.getString("from");
                                Log.d(TAG, "Call from : " + callFrom);
                            } catch (JSONException e) {
                                Log.d(TAG, "friend call object cannot be parsed");
                            }
                        }
                    });
                    socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "Disconnected");
                        }

                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                response.setText("heyy");
            }
        });
        //alarm code
        /*Calendar t = Calendar.getInstance();

        t.add(Calendar.SECOND, 15);



        Intent i = new Intent(this, AlarmSound.class);

        PendingIntent pending = PendingIntent.getActivity(this,1235, i, PendingIntent.FLAG_CANCEL_CURRENT);



        AlarmManager alarm = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);

        alarm.set(AlarmManager.RTC_WAKEUP, t.getTimeInMillis(),pending);

        startActivity(i);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();stopService(new Intent(MainActivity.this,
                BackgroundService.class));
        if(isService)
        {
            TextView tv = (TextView) findViewById(R.id.textView1);
            tv.setText("Service Resumed");
            isService = false;
        }

    }
}
