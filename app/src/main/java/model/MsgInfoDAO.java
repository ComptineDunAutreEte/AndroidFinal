package model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MsgInfoDAO {

    @Insert
    void insert(MsgInfo... repos);

    @Update
    void update(MsgInfo... repos);

    @Delete
    void delete(MsgInfo... repos);
    @Query("SELECT * FROM msginfo")
    List<MsgInfo> getAllRepos();

    @Query("SELECT * FROM msginfo WHERE number=:number")
    List<MsgInfo> findRepositoriesForUser(final String number);

    @Query("SELECT * FROM msginfo WHERE uuid=:uuid")
    MsgInfo findMsgInfo(final String uuid);
}
