package it.units.sim.yourtube.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

@Dao
public interface CategoryDAO {

    @Query("SELECT * FROM categories")
    ListenableFuture<List<Category>> getAll();

    @Query("SELECT * FROM categories WHERE name LIKE :name LIMIT 1")
    ListenableFuture<Category> findByName(String name, List<UserSubscription> channels);

    @Insert
    void insertAll(Category... categories);

    @Delete
    void delete(Category category);

    @Update
    void updateAll(Category... categories);

    // Optionally... @Insert, @Delete and @Update can return a number
    // https://developer.android.com/training/data-storage/room/accessing-data#java

}