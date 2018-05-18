package pl.kolis.mobilevoter.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Question.class,
        parentColumns = "id",
        childColumns = "question_id"))
public class Answer {

    @PrimaryKey
    private int answerId;

    private String text;

    @ColumnInfo(name = "question_id")
    private int questionId;
}
