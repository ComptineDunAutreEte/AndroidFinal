package fr.unice.implicitintents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import model.Conversation;
import model.ConversationDataBase;

public class ConversationActivity extends AppCompatActivity {
    private ListView listview;
    public  ConversationDataBase database;
    private ConversationView cc;
    private List<Conversation> msgs;

    private BroadcastReceiver br = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String m = intent.getExtras().getString("message");
            final String n = intent.getExtras().getString("number");
            DecodeMessage.decode(m, n, getApplication());
        }
    };

    private BroadcastReceiver update = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Conversation> list = database.getConDAO().findAllConversations();
                    if (list != null) {
                        msgs.clear();
                        msgs.addAll(list);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                listview.invalidateViews();

                            }
                        });

                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(br,new IntentFilter("SMS_RECEIVED_ACTION"));
        this.registerReceiver(update,new IntentFilter("UPDATE_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(br);
        this.unregisterReceiver(update);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        msgs = new ArrayList<>();
        cc = new ConversationView(this, msgs);
        listview = findViewById(R.id.conversationlv);
        listview.setAdapter(cc);
        database = ConversationDataBase.getDatabase(getApplication());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                 List<Conversation> tmp = database.getConDAO().findAllConversations();
                if(tmp != null){
                    msgs.addAll(tmp);
                    listview.invalidateViews();
                }
            }});


    }
}
