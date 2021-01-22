package com.example.c196adamson.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.c196adamson.Dao.AssessmentDao;
import com.example.c196adamson.Dao.CourseDao;
import com.example.c196adamson.Dao.CourseMentorDao;
import com.example.c196adamson.Dao.TermDao;
import com.example.c196adamson.Entity.Assessment;
import com.example.c196adamson.Entity.Course;
import com.example.c196adamson.Entity.CourseMentor;
import com.example.c196adamson.Entity.Term;

@Database(entities = {Term.class, Course.class, CourseMentor.class, Assessment.class}, exportSchema = false, version = 4)
@TypeConverters({Converters.class})
public abstract class RoomDB extends RoomDatabase {

    private static final String DB_Name = "room_db.db";
    private static RoomDB instance;

    public static synchronized RoomDB getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DB_Name).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract CourseMentorDao courseMentorDao();
    public abstract AssessmentDao assessmentDao();
}
