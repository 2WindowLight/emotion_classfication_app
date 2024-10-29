package com.example.emotion_classification;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultsDashboardActivity extends AppCompatActivity {

    private Button loadButton, analyzeButton;
    private PieChart emotionChart;
    private FirebaseFirestore firestore;
    private List<DocumentSnapshot> diaryList;
    private String selectedDiaryContent;
    private TextView highestEmotionText, lowestEmotionText;
    private TextView highestEmotionAdvice, lowestEmotionAdvice;
    private TextView diagnosisResultText; // 진단 결과 변수
    private OkHttpClient client = new OkHttpClient();
    private String selectedDiaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_dashboard);

        firestore = FirebaseFirestore.getInstance();  // Firestore 초기화

        loadButton = findViewById(R.id.loadButton);
        analyzeButton = findViewById(R.id.analyzeButton);
        emotionChart = findViewById(R.id.pieChartContainer);

        highestEmotionAdvice = findViewById(R.id.highestEmotionAdvice);
        lowestEmotionAdvice = findViewById(R.id.lowestEmotionAdvice);
        diagnosisResultText = findViewById(R.id.diagnosisResultText); // 진단결과 초기화

        setupPieChart();

        loadButton.setOnClickListener(v -> {
            loadDiaries();
            setupPieChart();
        });
        analyzeButton.setOnClickListener(v -> analyzeEmotion());
    }

    private void setupPieChart() {
        emotionChart.setUsePercentValues(true);
        emotionChart.getDescription().setEnabled(false);
        emotionChart.setDrawHoleEnabled(false);
        emotionChart.setTransparentCircleAlpha(0);
        emotionChart.setRotationEnabled(true);
        emotionChart.setHighlightPerTapEnabled(true);

        Legend legend = emotionChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
    }

    private void loadDiaries() {
        firestore.collection("diaries").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                diaryList = task.getResult().getDocuments();
                List<String> diaryTitlesDays = new ArrayList<>();
                for (DocumentSnapshot doc : diaryList) {
                   /* diaryTitles.add(doc.getString("title"));*/
                    String title = doc.getString("title");
                    String date = doc.getString("date"); // Firestore에 저장된 날짜 필드를 가져옴.

                    // 제목과 날짜를 함께 리스트에 추가
                    diaryTitlesDays.add(title + "\n" + date);

                }
                // 다이어리 선택 시 ID도 함께 저장하도록 수정
                new AlertDialog.Builder(ResultsDashboardActivity.this)
                        .setTitle("일기 목록")
                        .setItems(diaryTitlesDays.toArray(new String[0]), (dialog, which) -> {
                            selectedDiaryContent = diaryList.get(which).getString("content");
                            selectedDiaryId = diaryList.get(which).getId(); // 선택된 일기의 ID 저장
                            Toast.makeText(this, "선택한 일기: " + selectedDiaryContent, Toast.LENGTH_SHORT).show();
                        })
                        .show();
            }
        });
    }

    private void analyzeEmotion() {
        if (selectedDiaryContent == null) {
            Toast.makeText(this, "먼저 일기를 불러오세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendTextToServer(selectedDiaryContent);
    }

    private void sendTextToServer(String text) {
        String url = "http://10.0.2.2:5000/predict";


        // JSONObject를 사용하여 JSON 요청 본문을 안전하게 생성
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*String json = "{\"text\": \"" + text + "\"}";*/
        /*RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));*/
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ServerError", "서버 요청 실패: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Server Response", responseData);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (jsonResponse.has("probabilities")) {
                            JSONObject probabilities = jsonResponse.getJSONObject("probabilities");
                            // 퍼센트로 변환하고 소수점 한 자리까지 포맷
                            float anger = (float) (probabilities.getDouble("분노") * 100);
                            float anxiety = (float) (probabilities.getDouble("불안") * 100);
                            float neutral = (float) (probabilities.getDouble("중립") * 100);
                            float happiness = (float) (probabilities.getDouble("행복") * 100);
                            float disgust = (float) (probabilities.getDouble("혐오") * 100);

                            // Firestore에 숫자 형식으로 저장
                            Map<String, Object> emotionData = new HashMap<>();
                            emotionData.put("anger", anger);  // 이미 소수 값이므로 % 기호 없이 저장
                            emotionData.put("anxiety", anxiety);
                            emotionData.put("neutral", neutral);
                            emotionData.put("happiness", happiness);
                            emotionData.put("disgust", disgust);


                            // 선택한 일기 문서에 감정 데이터 업데이트
                            if (selectedDiaryId != null) {
                                firestore.collection("diaries").document(selectedDiaryId)
                                        .update(emotionData)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "감정 데이터 저장 성공"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "감정 데이터 저장 실패", e));
                            } else {
                                Log.e("Firestore", "선택된 일기 ID가 없습니다.");
                            }

                            float[] emotionPercentages = {anger, anxiety, neutral, happiness, disgust};
                            runOnUiThread(() -> {
                                displayEmotionChart(emotionPercentages);
                                displayEmotionAnalysis(emotionPercentages, new String[]{"분노", "불안", "중립", "행복", "혐오"});
                            });
                        } else {
                            Log.e("ServerResponseError", "probabilities 키가 없습니다.");
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", "JSON 파싱 에러: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServerError", "서버 응답 실패: " + response.message());
                }
            }
        });
    }

    private void displayEmotionChart(float[] emotionPercentages) {
        List<PieEntry> entries = new ArrayList<>();
        String[] emotionLabels = {"분노", "불안", "중립", "행복", "혐오"};

        for (int i = 0; i < emotionPercentages.length; i++) {
            // 소수점 한 자리로 포맷 후 차트에 추가
            entries.add(new PieEntry(Float.parseFloat(String.format("%.1f", emotionPercentages[i])), emotionLabels[i]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "감정 분석 결과");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.holo_blue_light));
        colors.add(Color.parseColor("#9C007F1F"));
        colors.add(Color.parseColor("#FFAF7251"));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        emotionChart.setData(data);
        emotionChart.invalidate();
    }

    private void displayEmotionAnalysis(float[] emotionPercentages, String[] emotionLabels) {
        float maxPercentage = findMaxPercentage(emotionPercentages);
        float minPercentage = findMinPercentage(emotionPercentages);

        int maxIndex = findMaxIndex(emotionPercentages, maxPercentage);
        int minIndex = findMinIndex(emotionPercentages, minPercentage);

        String highestEmotion = emotionLabels[maxIndex];
        String lowestEmotion = emotionLabels[minIndex];

        // 감정별로 조언을 제공하는 Map
        Map<String, String> emotionAdviceMap = new HashMap<>();
        emotionAdviceMap.put("분노", "분노를 다스리기 위해 명상이나 심호흡을 시도해보세요.");
        emotionAdviceMap.put("불안", "불안감을 줄이기 위해 산책을 해보세요.");
        emotionAdviceMap.put("중립", "감정을 확인하고, 균형을 유지하는 것이 좋습니다.");
        emotionAdviceMap.put("행복", "행복을 오래 유지하려면 감사하는 마음을 가져보세요.");
        emotionAdviceMap.put("혐오", "혐오를 느낄 때는 감정을 기록하고, 왜 그런지 생각해보세요.");

        // 소수 값을 퍼센트 값으로 변환하여 소수점 한 자리만 표시
        String highestEmotionAdviceText = String.format("%s가 %.1f%%로 가장 높습니다.\n%s", highestEmotion, maxPercentage , emotionAdviceMap.get(highestEmotion));

        String lowestEmotionAdviceText = String.format("%s가 %.1f%%로 가장 낮습니다.\n%s", lowestEmotion, minPercentage , emotionAdviceMap.get(lowestEmotion));

        // 우울증 진단 로직 추가
        float negativeEmotionSum = emotionPercentages[0] + emotionPercentages[1] + emotionPercentages[4]; // 분노, 불안, 혐오
        float neutralPercentage = emotionPercentages[2];
        float happinessPercentage = emotionPercentages[3];

        final String depressionDiagnosis;  // 이 변수는 이제 final로 선언합니다.


        if (negativeEmotionSum >= 0.7) {
            depressionDiagnosis = "우울증에 대한 위험도가 높습니다. 부정적 감정의 비율이 굉장히 높아요. 누군가에게 대화 또는 도움을 청해 보세요! 최소 2주간 감정 비율이 지금과 비슷한 경우 상담 또는 전문의 진료를 받아보세요!";
        } else if (happinessPercentage < 0.1 && neutralPercentage > 0.5) {
            depressionDiagnosis = "우울증이 의심됩니다. 긍정적인 표현의 비율이 낮고 무감각적인 모습을 보이고 있습니다. 크게 걱정할 부분은 아니지만, 우울증으로 진화할 가능성이 생길 수 있으니 조심해야 해요! 어떠한 것이든 동기 부여를 받아 보시는 것이 어떤가요?";
        }
        else if (negativeEmotionSum < 0.7 && negativeEmotionSum > 0.4) {
            depressionDiagnosis = "우울증이 의심되는 정도입니다. 부정적인 감정의 비율이 상당 부분 차지하고 있어요! 이러한 상태가 지속되면 더 악화 되는 단계로 진화할 수가 있어요. 산책을 하면서 안정을 취하는 것도 좋은 방법이에요! ";
        }else {
            depressionDiagnosis = "훌륭해요! 현재로써는 안정적인 정서를 보여주고 있습니다! 지금 상태를 꾸준히 유지하세요! 오늘 하루도 고생하셨어요 ";  // 진단이 없는 경우 빈 문자열로 설정
        }


        // UI 업데이트: 차트 아래에 표시
        runOnUiThread(() -> {
            highestEmotionAdvice.setText(highestEmotionAdviceText);
            lowestEmotionAdvice.setText(lowestEmotionAdviceText);
            if (!depressionDiagnosis.isEmpty()) {
                diagnosisResultText.setText(depressionDiagnosis);
                diagnosisResultText.setVisibility(View.VISIBLE);
            } else {
                diagnosisResultText.setVisibility(View.GONE); // 진단 결과가 없을 때 숨기기
            }

            // 텍스트뷰 보이도록 설정
            highestEmotionAdvice.setVisibility(View.VISIBLE);
            lowestEmotionAdvice.setVisibility(View.VISIBLE);
        });
    }



    private float findMaxPercentage(float[] emotionPercentages) {
        float max = emotionPercentages[0];
        for (float percentage : emotionPercentages) {
            if (percentage > max) {
                max = percentage;
            }
        }
        return max;
    }

    private float findMinPercentage(float[] emotionPercentages) {
        float min = emotionPercentages[0];
        for (float percentage : emotionPercentages) {
            if (percentage < min) {
                min = percentage;
            }
        }
        return min;
    }
    private int findMaxIndex(float[] emotionPercentages, float maxPercentage) {
        for (int i = 0; i < emotionPercentages.length; i++) {
            if (emotionPercentages[i] == maxPercentage) {
                return i;
            }
        }
        return -1;  // 값이 없을 경우 -1을 반환
    }
    private int findMinIndex(float[] emotionPercentages, float minPercentage) {
        for (int i = 0; i < emotionPercentages.length; i++) {
            if (emotionPercentages[i] == minPercentage) {
                return i;
            }
        }
        return -1;  // 혹시 minPercentage가 없을 경우를 대비해 -1 반환
    }
}