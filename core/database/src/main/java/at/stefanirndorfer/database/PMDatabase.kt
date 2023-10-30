package at.stefanirndorfer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import at.stefanirndorfer.database.dao.MovieDao
import at.stefanirndorfer.database.model.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1
)
abstract class PMDatabase : RoomDatabase() {
    abstract val dao: MovieDao
}