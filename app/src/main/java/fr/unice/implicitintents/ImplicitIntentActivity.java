package fr.unice.implicitintents;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import model.Conversation;
import model.ConversationDataBase;
import model.MsgInfo;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ImplicitIntentActivity extends AppCompatActivity {

    private static final String TAG = "ImplicitIntent";
    private ConversationDataBase database;
    private MyBroadcastReceiver broadcast;

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            MsgInfo m = new MsgInfo();
            String mm = intent.getExtras().getString("message");
            String number = intent.getExtras().getString("number");
            DecodeMessage.decode(mm, number, getApplication());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(br, new IntentFilter("SMS_RECEIVED_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(br);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database = ConversationDataBase.getDatabase(getApplication());
                DecodeMessage.init(database);
            }
        });
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                BROADCAST);

        setContentView(R.layout.activity_main);
        //database = ConversationDataBase.getDatabase(this.getApplication());
        final EditText addrText = (EditText) findViewById(R.id.location);
        final Button mapButton = (Button) findViewById(R.id.mapButton);

        mapButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String address = addrText.getText().toString();
                    address = address.replace(' ', '+');
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));

                    if (geoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(geoIntent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });


        final Button contact = (Button) findViewById(R.id.contactBut);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactButtonClicked();
            }
        });

        final Button openSmsButton = (Button) findViewById(R.id.openSmsButton);
        openSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchReadSmsActivity();
                /*String content = addrText.getText().toString();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:5554"));
                smsIntent.putExtra("sms_body", content);

                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "INTENT NOT RESOLVED", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        final Button smsButton = (Button) findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                smsButtonClicked();
            }
        });

    }

    private void launchReadSmsActivity() {

        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }


    private void contactButtonClicked() {
        if ((ContextCompat.checkSelfPermission(ImplicitIntentActivity.this, READ_CONTACTS) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(ImplicitIntentActivity.this,
                    new String[]{READ_CONTACTS},
                    REQUEST_SEND_SMS);
        } else {
            pickContact();
        }
    }

    private void smsButtonClicked() {
        if ((ContextCompat.checkSelfPermission(ImplicitIntentActivity.this, SEND_SMS) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(ImplicitIntentActivity.this,
                    new String[]{SEND_SMS},
                    BROADCAST);
        } else {
            final EditText addrText = (EditText) findViewById(R.id.location);
            final EditText numberField = (EditText) findViewById(R.id.numeroField);
            MsgInfo m = new MsgInfo();
            m.setStatusLabel("Décodé");
            m.setCleLabel("Non Reçu");
            m.setMessageLabel("Non Reçu");
            m.setNumber(numberField.getText().toString());
            m.setUuid(UUID.randomUUID().toString());
            m.setKeyBut(false);
            m.setArBut(false);
            m.setReadBut(true);
            m.setContent(addrText.getText().toString());
            String encrypt = DecodeMessage.encrypt(m.getContent(), DecodeMessage.cle);
            String str = "1:" + encrypt + ":" + m.getUuid();

            Conversation c = new Conversation();
            c.setId(m.getNumber());
            DecodeMessage.insert(c, m);
            sendSMS(str, m.getNumber());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            try {
                ContentResolver cr = getContentResolver();
                Uri dataUri = data.getData();
                String[] projection = {ContactsContract.Contacts._ID};
                Cursor cursor = cr.query(dataUri, projection, null, null, null);

                if (null != cursor && cursor.moveToFirst()) {
                    String id = cursor
                            .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String number = getPhoneNumber(id);
                    if (number == null) {
                        Toast.makeText(getApplicationContext(), "No number in contact", Toast.LENGTH_SHORT).show();
                    } else {
                        final EditText numberField = (EditText) findViewById(R.id.numeroField);
                        numberField.setText(number);

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission denied " + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case REQUEST_SEND_SMS:
                pickContact();
                break;
            case BROADCAST:
                break;
            default:
                Toast.makeText(getApplicationContext(), "WRONG REQUEST CODE in Permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_SEND_SMS = 10;
    private static final int PICK_CONTACT_REQUEST = 20;
    private static final int BROADCAST = 30;

    private void pickContact() {
        Intent i = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }

    private String getPhoneNumber(String id) {
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id;
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        return null;
    }

    private void sendSMS(String content, String number) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, content, null, null);
    }
}
