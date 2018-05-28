package fr.unice.implicitintents;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Conversation;


public class ConversationView extends ArrayAdapter<Conversation> {
    private List<Conversation> msgs;
    private Activity context;




    public ConversationView(@NonNull Activity context, List<Conversation> conversations) {
        super(context, R.layout.text_view, conversations);
        this.context = context;
        if (conversations == null) {
            msgs = new ArrayList<>();
        } else {
            msgs = conversations;
        }
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ViewHolder vh = null;
        if (v == null) {
            LayoutInflater li = context.getLayoutInflater();
            v = li.inflate(R.layout.text_view, null, true);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        if (msgs != null) {
            final Conversation c = msgs.get(position);
            vh.tv.setText(c.getId());
            vh.deleteBut.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DecodeMessage.deleteConversation(c);
                    msgs.remove(position);
                    Intent in = new Intent();
                    in.setAction("UPDATE_ACTION");
                    context.sendBroadcast(in);
                }});
            vh.seeBut.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ReadSms.class);
                    intent.putExtra("number", c.getId());
                    //based on item add info to intent
                    context.startActivity(intent);
                }});
        }

        return v;
    }


    class ViewHolder {
        TextView tv;
        Button seeBut;
        Button deleteBut;

        ViewHolder(View v) {
            tv = v.findViewById(R.id.tv);

            seeBut = v.findViewById(R.id.seeBut);
            deleteBut = v.findViewById(R.id.deleteBut);
        }
    }
}
