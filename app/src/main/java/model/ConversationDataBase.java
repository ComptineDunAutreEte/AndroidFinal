package model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = { MsgInfo.class, Conversation.class }
        , version = 5, exportSchema = false)
public abstract class ConversationDataBase extends RoomDatabase {


    public abstract ConversationDAO getConDAO();

    public abstract MsgInfoDAO getMsgInfoDAO();

    private static ConversationDataBase INSTANCE;

    public static ConversationDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), ConversationDataBase.class, "conversation_db")
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
}
