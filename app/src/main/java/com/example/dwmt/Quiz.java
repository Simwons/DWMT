package com.example.dwmt;


public class Quiz {
    private int id;
    private String question;
    private String[] answerArray;
    private int rightAnswer;


    public Quiz(int id, String question, String[] answerArray, int rightAnswer){
        this.id = id;
        this.question = question;
        this.answerArray = answerArray;
        this.rightAnswer = rightAnswer;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getanswerArray() { return answerArray; }

    public int getRightAnswer() { return rightAnswer; }

}
