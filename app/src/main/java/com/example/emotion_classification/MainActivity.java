package com.example.emotion_classification;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton, loadButton, analyzeButton;
    private TextView emotionResultTextView;
    private FirebaseFirestore firestore;
    private Interpreter tflite;
    private List<DocumentSnapshot> diaryList;
    private String selectedDate;


    // OkHttp 클라이언트 생성
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.etTitle);
        contentEditText = findViewById(R.id.etContent);
        saveButton = findViewById(R.id.saveButton);

        firestore = FirebaseFirestore.getInstance();

        selectedDate = getIntent().getStringExtra("selectedDate");

        if (selectedDate == null) {
            Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            finish(); // 날짜가 없으면 Activity 종료
        }

        saveButton.setOnClickListener(v -> saveDiary());
    }

    // 일기 저장 함수
    private void saveDiary() {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.isEmpty() || content.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "제목, 내용, 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> diary = new HashMap<>();
        diary.put("title", title);
        diary.put("content", content);
        diary.put("date", selectedDate);  // 날짜도 함께 저장

        firestore.collection("diaries").add(diary)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    // 일기 불러오기 함수
    /*private void loadDiaries() {
        CollectionReference diariesRef = firestore.collection("diaries");

        diariesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    diaryList = querySnapshot.getDocuments();
                    StringBuilder loadedData = new StringBuilder();
                    for (DocumentSnapshot document : diaryList) {
                        String title = document.getString("title");
                        String content = document.getString("content");
                        loadedData.append("제목: ").append(title).append(", 내용: ").append(content).append("\n");

                        // 불러온 데이터를 Log로 출력 (원하는 경우)
                        Log.d("Loaded Data", "제목: " + title + ", 내용: " + content);
                    }

                    // 불러온 데이터를 텍스트로 표시
                    emotionResultTextView.setText(loadedData.toString());

                    Toast.makeText(this, "데이터가 불러와졌습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "일기를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    // 서버로 텍스트 전송하는 메서드
    public void sendTextToServer(String text) {
        String url = "http://10.0.2.2:5000/predict";  // 서버 주소

        // JSON 요청 본문 생성
        String json = "{\"text\": \"" + text + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        // POST 요청 생성
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 서버로 요청 전송
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    final String decodedResponse = URLDecoder.decode(responseData, "UTF-8");

                    runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(decodedResponse);

                            // 감정별 확률 데이터를 받는 부분 수정
                            JSONObject probabilities = json.getJSONObject("probabilities");

                            // 각 감정의 확률을 소수점 둘째 자리까지 포맷팅하여 표시
                            StringBuilder emotionResults = new StringBuilder();
                            emotionResults.append("분노: ").append(String.format("%.2f", probabilities.getDouble("분노") * 100)).append("% 확률\n")
                                    .append("불안: ").append(String.format("%.2f", probabilities.getDouble("불안") * 100)).append("% 확률\n")
                                    .append("중립: ").append(String.format("%.2f", probabilities.getDouble("중립") * 100)).append("% 확률\n")
                                    .append("행복: ").append(String.format("%.2f", probabilities.getDouble("행복") * 100)).append("% 확률\n")
                                    .append("혐오: ").append(String.format("%.2f", probabilities.getDouble("혐오") * 100)).append("% 확률\n");

                            emotionResultTextView.setText(emotionResults.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    // 감정 분석 함수에서 텍스트를 서버로 전송
    private void analyzeEmotion() {
        String textToAnalyze = diaryList.get(0).getString("content"); // 분석할 텍스트 가져오기
        if (!textToAnalyze.isEmpty()) {
            sendTextToServer(textToAnalyze);  // 서버로 텍스트 전송
        } else {
            Toast.makeText(MainActivity.this, "분석할 텍스트를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}