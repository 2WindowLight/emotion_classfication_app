package com.example.emotion_classification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditDiaryActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private FirebaseFirestore firestore;
    private String diaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        titleEditText = findViewById(R.id.etTitle);
        contentEditText = findViewById(R.id.etContent);
        saveButton = findViewById(R.id.saveButton);
        firestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String diaryId = intent.getStringExtra("diaryId");
        if (diaryId == null) {
            Log.e("EditDiaryActivity", "Document ID is null");
            finish(); // 에러가 발생하지 않도록 화면 종료
            return;
        }

        loadDiaryData(diaryId);

        saveButton.setOnClickListener(v -> updateDiary(diaryId));
    }

    private void loadDiaryData(String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("diaries").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String content = documentSnapshot.getString("content");
                        // 데이터 표시 코드 추가
                    }
                })
                .addOnFailureListener(e -> Log.e("EditDiaryActivity", "Error loading diary", e));
    }

    private void updateDiary(String diaryId) {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("diaries").document(diaryId)
                .update("title", title, "content", content)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "일기가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 수정 완료 후 화면 종료
                });
    }
}