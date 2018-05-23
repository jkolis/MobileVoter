package pl.kolis.mobilevoter.database.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {

    private String questionId;

    private String text;

    private String user;

    private List<String> answers;

    private long duration;

    private HashMap<String, Integer> votes;

    Map<String, Integer> voters;


    public Question() {
    }

    public Question(String questionId, String text, String user, List<String> answers, long duration,
                    Map<String, Integer> voters, HashMap<String, Integer> votes) {
        this.questionId = questionId;
        this.text = text;
        this.user = user;
        this.answers = answers;
        this.duration = duration;
        this.voters = voters;
        this.votes = votes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Map<String, Integer> getVoters() {
        return voters;
    }

    public void setVoters(Map<String, Integer> voters) {
        this.voters = voters;
    }

    public HashMap<String, Integer> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, Integer> votes) {
        this.votes = votes;
    }
}
