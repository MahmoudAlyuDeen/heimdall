package com.afterapps.heimdall.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CollectionDao {

    @Query("select * from databasecollection")
    fun getCollections(): LiveData<List<DatabaseCollection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollections(collections: List<DatabaseCollection>)

}

@Dao
interface ImagesDao {

    @Query("select * from databaseimage where collectionId = :collectionId")
    fun getImages(collectionId: String): LiveData<List<DatabaseImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImages(images: List<DatabaseImage>)

}

@Database(entities = [DatabaseCollection::class, DatabaseImage::class], version = 1)
abstract class ShutterstockDatabase : RoomDatabase() {
    abstract val collectionDao: CollectionDao
    abstract val imagesDao: ImagesDao
}
