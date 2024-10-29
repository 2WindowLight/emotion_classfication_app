package com.example.emotion_classification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements DiaryAdapter.OnItemClickListener {

    private Button diaryButton, dashboardButton, btnEmotionTrend;;
    private CalendarView calendarView;
    private String selectedDate;
    private FirebaseFirestore firestore;

    private RecyclerView diaryRecyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();

    @Override
    public void onItemClick(Diary diary) {
        Intent intent = new Intent(MainPageActivity.this, EditDiaryActivity.class);
        intent.putExtra("diaryId", diary.getId()); // 수정할 다이어리 ID를 전달
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page); // XML 파일 연결
        firestore = FirebaseFirestore.getInstance();  // Firestore 초기화
        diaryButton = findViewById(R.id.diaryButton);
        dashboardButton = findViewById(R.id.dashboardButton);
        btnEmotionTrend = findViewById(R.id.btnEmotionTrend); // 새 버튼 연결
        calendarView = findViewById(R.id.calendarView);
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView);
        diaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        diaryAdapter = new DiaryAdapter(diaryList, this); // 두 번째 인자로 this를 전달
        diaryRecyclerView.setAdapter(diaryAdapter);

        // 캘린더 날짜 선택 시 해당 날짜의 일기 불러오기
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // 클래스 변수로 할당
            loadDiariesByDate(selectedDate);
        });
        //MainPageActivity에서 DiaryAdapter를 초기화할 때 클릭 리스너를 추가
        diaryAdapter = new DiaryAdapter(diaryList, diary -> {
            // 클릭한 아이템의 수정 화면으로 이동
            Intent intent = new Intent(MainPageActivity.this, EditDiaryActivity.class);
            intent.putExtra("diaryId", diary.getId()); // 일기의 고유 ID 전달
            startActivity(intent);
        });
        diaryRecyclerView.setAdapter(diaryAdapter);


        // 일기 작성 버튼 클릭 시 MainActivity로 이동, 선택된 날짜 전달
        diaryButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                intent.putExtra("selectedDate", selectedDate); // 선택된 날짜 전달
                startActivity(intent);
            } else {
                Toast.makeText(MainPageActivity.this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 대시보드 버튼 클릭 시 DashboardActivity로 이동
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainPageActivity.this, ResultsDashboardActivity.class);
            startActivity(intent);
        });
        // 감정 추세 보기 버튼 클릭 시 EmotionTrendActivity로 이동
        btnEmotionTrend.setOnClickListener(v -> {
            Intent intent = new Intent(MainPageActivity.this, EmotionTrendActivity.class);
            startActivity(intent);
        });
    }
    private void loadDiariesByDate(String date) {
        // Firestore에서 날짜별로 일기 불러오기
        firestore.collection("diaries").whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    diaryList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId(); // 문서 ID 가져오기
                        String title = doc.getString("title");
                        String content = doc.getString("content");
                        int emotionIcon = getEmotionIcon(doc.getString("emotion"));
                        diaryList.add(new Diary(id, date, title, content, emotionIcon));
                    }
                    diaryAdapter.notifyDataSetChanged();
                });
    }
    private int getEmotionIcon(String emotion) {
        if (emotion == null) {
            return R.drawable.ic_neutral; // 기본 아이콘 반환
        }
        switch (emotion) {
            case "행복":
                return R.drawable.ic_happy;
            case "슬픔":
                return R.drawable.ic_sad;
            case "분노":
                return R.drawable.ic_angry;
            case "혐오":
                return R.drawable.ic_disgust;
            case "중립":
                return R.drawable.ic_neutral;
            default:
                return R.drawable.ic_neutral;
        }
    }
}