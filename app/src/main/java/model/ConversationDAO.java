package model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ConversationDAO {
    @Insert
    void insert(Conversation... con);

    @Update
    void update(Conversation... con);
    @Delete
    void delete(Conversation... con);

    @Query("SELECT * FROM conversation WHERE id=:id")
    Conversation findNumber(final String id);

    @Query("SELECT * FROM conversation")
    List<Conversation> findAllConversations();
}
