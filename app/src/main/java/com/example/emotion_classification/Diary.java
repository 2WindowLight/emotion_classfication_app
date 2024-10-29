package com.example.emotion_classification;

public class Diary {
    private String id; // 다이어리 ID
    private String date;
    private String title;
    private String content;
    private int emotionIcon;  // emotionIcon 필드 추가
    private int emotionIconResId;  // emotion 아이콘 리소스 ID 추가

    public Diary() {
        // 기본 생성자
    }

    public Diary(String id,String date, String title, String content, int emotionIcon) {  // emotionIcon 포함한 생성자 추가
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.emotionIcon = emotionIcon;
    }

    // Getter와 Setter 메서드 추가
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getEmotionIcon() {
        return emotionIcon;
    }

    public void setEmotionIcon(int emotionIcon) {
        this.emotionIcon = emotionIcon;
    }
    public int getEmotionIconResId() {
        return emotionIconResId;
    }
}