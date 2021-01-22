package com.example.c196adamson.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.c196adamson.Entity.CourseMentor;

import java.util.List;

@Dao
public interface CourseMentorDao {
    @Query("SELECT * FROM course_mentor_table WHERE course_id_fk = :courseID ORDER BY mentor_id")
    List<CourseMentor> getMentorList(int courseID);

    @Query("SELECT * FROM  course_mentor_table WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    CourseMentor getMentor(int courseID, int mentorID);

    @Query("SELECT * FROM course_mentor_table")
    List<CourseMentor> getAllMentors();

    @Insert
    void insertMentor(CourseMentor courseMentor);

    @Insert
    void insertAll(CourseMentor... courseMentor);

    @Update
    void updateMentor(CourseMentor courseMentor);

    @Delete
    void deleteMentor(CourseMentor courseMentor);

}
