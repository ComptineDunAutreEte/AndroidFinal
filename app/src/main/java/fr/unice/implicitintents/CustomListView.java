package fr.unice.implicitintents;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.MsgInfo;

import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CustomListView extends ArrayAdapter<MsgInfo> {
    private List<MsgInfo> msgs;
    private Activity context;

    public CustomListView(@NonNull Activity context, List<MsgInfo> list) {
        super(context, R.layout.msgbox, list);
        this.context = context;
        if (list == null) {
            msgs = new ArrayList<>();
        } else {
            msgs = list;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        ViewHolder vh = null;
        if (v == null) {
            LayoutInflater li = context.getLayoutInflater();
            v = li.inflate(R.layout.msgbox, null, true);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        if (msgs != null) {
            final MsgInfo m = msgs.get(position);


            vh.statusLabel.setText("Status: " + m.getStatusLabel());
            vh.keyLabel.setText("Clé: " + m.getCleLabel());
            vh.messageLabel.setText("Message: " + m.getMessageLabel());

            vh.arBut.setEnabled(m.isArBut());
            vh.keyBut.setEnabled(m.isKeyBut());
            vh.readBut.setEnabled(m.isReadBut());

            vh.readBut.setOnClickListener(new Button.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Toast.makeText(context, m.getContent(), Toast.LENGTH_SHORT).show();
                }});
            if (m.getMessageLabel().equals("Reçu")) {
                vh.arBut.setText("Accusé De Réception");
                vh.keyBut.setText("Clé Reçu");
                vh.arBut.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = "2:"+m.getUuid();
                        sendSms(str,m.getNumber());
                    }
                });
                vh.keyBut.setText("Accusé Réception Clé");
                vh.keyBut.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = "4:"+m.getUuid();
                        sendSms(str,m.getNumber());
                    }
                });
            } else {
                vh.keyBut.setText("Envoyer La Clé");
                vh.keyBut.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = "3:"+m.getUuid()+":"+DecodeMessage.cle;
                        sendSms(str,m.getNumber());
                    }
                });
            }
        }

        return v;


    }


    private void sendSms(String content, String number) {
        if ((ContextCompat.checkSelfPermission(context, SEND_SMS) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(context,
                    new String[]{SEND_SMS},
                    30);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, content, null, null);
        }
    }


    class ViewHolder {
        TextView statusLabel;
        TextView keyLabel;
        TextView messageLabel;

        Button arBut;
        Button keyBut;
        Button readBut;

        ViewHolder(View v) {
            statusLabel = v.findViewById(R.id.statusLabel);
            keyLabel = v.findViewById(R.id.keyLabel);
            messageLabel = v.findViewById(R.id.messageLabel);

            arBut = v.findViewById(R.id.arBut);
            keyBut = v.findViewById(R.id.keyBut);
            readBut = v.findViewById(R.id.readBut);

        }
    }
}
