package fr.unice.implicitintents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getAction().equals(SMS_RECEIVED)){
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            StringBuilder sb = new StringBuilder();

            if(bundle!=null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                messages = new SmsMessage[pdus.length];
                Intent in = new Intent();
                in.setAction("SMS_RECEIVED_ACTION");
                for(int i =0; i <messages.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    sb.append("Message From ");
                    sb.append(messages[i].getOriginatingAddress());
                    sb.append(": ");
                    sb.append(messages[i].getMessageBody().toString());
                    sb.append("\n");
                    in.putExtra("number",messages[i].getOriginatingAddress());
                    in.putExtra("message",messages[i].getMessageBody().toString());
                }
                context.sendBroadcast(in);
            }




        }

    }
}
