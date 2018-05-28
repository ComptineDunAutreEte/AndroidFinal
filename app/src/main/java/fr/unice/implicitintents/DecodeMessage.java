package fr.unice.implicitintents;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Conversation;
import model.ConversationDataBase;
import model.MsgInfo;

public final class DecodeMessage {

    public static ConversationDataBase database;

    public static int cle;
    private DecodeMessage() {
    }

    public static void init(ConversationDataBase db) {
        database = db;
        Random r = new Random();
        cle = 2 + r.nextInt(128);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Conversation> list = database.getConDAO().findAllConversations();
                if(list != null){

                }
            }});
    }

    public static void decode(final String msg, final String number, final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String[] lines = msg.split(":");
                if(lines[0].equals("5")){
                    Intent in = new Intent();
                    in.setAction("LOCATION_ACTION");
                    in.putExtra("location", lines[1]);
                    context.sendBroadcast(in);
                }else{
                    decode(lines, number);
                    Intent in = new Intent();
                    in.setAction("UPDATE_ACTION");
                    in.putExtra("number", number);
                    context.sendBroadcast(in);
                }


            }
        });

    }

    private static void decode(String[] lines, String number) {
        if (lines[0].equals("1")) { //1 envoi
            Conversation c = database.getConDAO().findNumber(number);

            MsgInfo m = new MsgInfo();
            m.setStatusLabel("Codé");
            m.setMessageLabel("Reçu");

            m.setContent(lines[1]);
            m.setUuid(lines[2]);
            m.setArBut(true);
            m.setKeyBut(false);
            m.setReadBut(true);

            if (c != null) {
                m.setNumber(c.getId());
                System.out.println("Jesuis LAAAAAAAAAA");
            } else {
                c = new Conversation();
                c.setId(number);

                m.setNumber(number);

            }
            insert(c, m);

        } else if (lines[0].equals("2")) {//sms recu
            MsgInfo m = database.getMsgInfoDAO().findMsgInfo(lines[1]);
            //System.out.println();
            m.setKeyBut(true);
            m.setMessageLabel("Envoyé");
            updateMsgInfo(m);
        } else if (lines[0].equals("3")) {//cle recu
            MsgInfo m = database.getMsgInfoDAO().findMsgInfo(lines[1]);
            //System.out.println(content);
            m.setCleLabel("Reçu");
            m.setStatusLabel("Décodé");
            m.setKeyBut(true);
            m.setContent(decrypt(m.getContent(), Integer.parseInt(lines[2])));
            updateMsgInfo(m);
        }else if (lines[0].equals("4")) {//cle recu
            MsgInfo m = database.getMsgInfoDAO().findMsgInfo(lines[1]);
            m.setCleLabel("Envoyé");
            updateMsgInfo(m);
        }
    }

    public static void insertConversation(final Conversation conversation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c != null) {
                    database.getConDAO().insert(conversation);
                }
            }
        });

    }

    public static void insert(final Conversation conversation, final MsgInfo msg) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c == null) {
                    database.getConDAO().insert(conversation);
                    database.getMsgInfoDAO().insert(msg);
                }else{
                    database.getMsgInfoDAO().insert(msg);
                }

            }
        });

    }

    public static void updateConversation(final Conversation conversation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Conversation c = database.getConDAO().findNumber(conversation.getId());
                if (c != null) {
                    database.getConDAO().update(conversation);
                }
            }
        });
    }

    public static void deleteConversation(Conversation conversation) {
        new AsyncTask<Conversation, Void, Void>() {
            @Override
            protected Void doInBackground(Conversation... con) {
                database.getConDAO().delete(con);
                return null;
            }
        }.execute(conversation);
    }

    public static void insertMsgInfo(final MsgInfo m) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database.getMsgInfoDAO().insert(m);
            }
        });

    }

    public static void updateMsgInfo(final MsgInfo m) {
        database.getMsgInfoDAO().update(m);

    }

    public static void deleteMsgInfo(MsgInfo m) {
        new AsyncTask<MsgInfo, Void, Void>() {
            @Override
            protected Void doInBackground(MsgInfo... msgs) {
                database.getMsgInfoDAO().delete(msgs);
                return null;
            }
        }.execute(m);
    }

    public static String encrypt(String s, int k) {
        char[] arr = s.toCharArray();
        for(int i = 0; i < arr.length; i++){
            arr[i] = (char) ((arr[i] + k) % 128);
        }
        return String.valueOf(arr);
    }
    public static String decrypt(String s, int k) {
        char[] arr = s.toCharArray();
        for(int i = 0; i < arr.length; i++){
            arr[i] = (char) ((arr[i] + 128 - k % 128) % 128);
            // + 128 - k % 128 because I don't want do deal with negative numbers.
        }
        return String.valueOf(arr);
    }



}
