package com.example.emotion_classification;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmotionTrendActivity extends AppCompatActivity {

    private Button btnSelectDateRange, btnSelectEmotions;
    private LineChart emotionTrendChart;
    private TextView tvDiagnosisMessage;
    private FirebaseFirestore firestore;
    private List<String> selectedEmotions = new ArrayList<>();

    // 기간 선택 후 로드된 데이터를 저장할 리스트 변수들
    private List<Entry> loadedAngerEntries = new ArrayList<>();
    private List<Entry> loadedAnxietyEntries = new ArrayList<>();
    private List<Entry> loadedHappinessEntries = new ArrayList<>();
    private List<Entry> loadedDisgustEntries = new ArrayList<>();
    private List<Entry> loadedNeutralEntries = new ArrayList<>();



    private void setupChartAppearance() {
        // 차트 설명 비활성화
        emotionTrendChart.getDescription().setEnabled(false);

        // X축 스타일 설정
        XAxis xAxis = emotionTrendChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // X축 간격 설정
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // X축에 날짜 형식 적용
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        // Y축 설정
        YAxis leftAxis = emotionTrendChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        leftAxis.setGridColor(Color.LTGRAY);
        YAxis rightAxis = emotionTrendChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 범례 스타일 설정
        Legend legend = emotionTrendChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(14f);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_trend);
        btnSelectEmotions = findViewById(R.id.btnSelectEmotions);
        btnSelectDateRange = findViewById(R.id.btnSelectDateRange);
        emotionTrendChart = findViewById(R.id.emotionTrendChart);
        tvDiagnosisMessage = findViewById(R.id.tvDiagnosisMessage);

        firestore = FirebaseFirestore.getInstance();
        // 버튼에 클릭 리스너 설정
        // 차트 초기 설정 적용
        setupChartAppearance();
        btnSelectDateRange.setOnClickListener(v -> {
            // 기간 선택에 대한 기능 구현
            showDateRangePicker();
        });
        // 감정 선택 버튼 리스너 추가
        btnSelectEmotions.setOnClickListener(v -> {
            showEmotionSelectionDialog();
        });


    }
    private void showEmotionSelectionDialog() {
        String[] emotionArray = {"분노", "불안", "행복", "혐오", "중립"};
        boolean[] checkedItems = new boolean[emotionArray.length];
        // 현재 선택된 감정을 표시하도록 설정
        for (int i = 0; i < emotionArray.length; i++) {
            checkedItems[i] = selectedEmotions.contains(emotionArray[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("감정을 선택하세요")
                .setMultiChoiceItems(emotionArray, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!selectedEmotions.contains(emotionArray[which])) {
                            selectedEmotions.add(emotionArray[which]);
                        }
                    } else {
                        selectedEmotions.remove(emotionArray[which]);
                    }
                })
                .setPositiveButton("확인", (dialog, id) -> {
                    // 중복 제거 및 차트 갱신
                    selectedEmotions = new ArrayList<>(new HashSet<>(selectedEmotions));
                    updateChartWithSelectedEmotions();
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }
    private void updateChartWithSelectedEmotions() {
        if (selectedEmotions.isEmpty()) {
            Toast.makeText(this, "표시할 감정을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        emotionTrendChart.clear();  //
    //
        // 선택된 감정에 따라 차트에 표시할 데이터 필터링
        List<Entry> angerEntries = selectedEmotions.contains("분노") ? loadedAngerEntries : new ArrayList<>();
        List<Entry> anxietyEntries = selectedEmotions.contains("불안") ? loadedAnxietyEntries : new ArrayList<>();
        List<Entry> happinessEntries = selectedEmotions.contains("행복") ? loadedHappinessEntries : new ArrayList<>();
        List<Entry> disgustEntries = selectedEmotions.contains("혐오") ? loadedDisgustEntries : new ArrayList<>();
        List<Entry> neutralEntries = selectedEmotions.contains("중립") ? loadedNeutralEntries : new ArrayList<>();

        displayEmotionTrendChart(angerEntries, anxietyEntries, happinessEntries, disgustEntries, neutralEntries);
    }// 행복 테스트 2

    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();

        // 시작 날짜 선택
        DatePickerDialog startDatePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String startDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            // 종료 날짜 선택
            DatePickerDialog endDatePicker = new DatePickerDialog(this, (view1, endYear, endMonth, endDayOfMonth) -> {
                String endDate = endYear + "-" + (endMonth + 1) + "-" + endDayOfMonth;

                // 선택한 날짜 범위를 이용해 데이터를 불러옵니다.
                loadEmotionTrendData(startDate, endDate);

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            endDatePicker.setMessage("종료 날짜 선택");
            endDatePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        startDatePicker.setMessage("시작 날짜 선택");
        startDatePicker.show();
    }

    // 데이터 로드 메서드
    private void loadEmotionTrendData(String startDate, String endDate) {
        firestore.collection("diaries")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 기존 데이터 초기화
                        loadedAngerEntries.clear();
                        loadedAnxietyEntries.clear();
                        loadedHappinessEntries.clear();
                        loadedDisgustEntries.clear();
                        loadedNeutralEntries.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateString = document.getString("date");
                            long timestamp = convertDateToTimestamp(dateString); // 날짜 문자열을 타임스탬프로 변환
                            float anger = document.getDouble("anger") != null ? document.getDouble("anger").floatValue() : 0.0f;
                            float anxiety = document.getDouble("anxiety") != null ? document.getDouble("anxiety").floatValue() : 0.0f;
                            float happiness = document.getDouble("happiness") != null ? document.getDouble("happiness").floatValue() : 0.0f;
                            float disgust = document.getDouble("disgust") != null ? document.getDouble("disgust").floatValue() : 0.0f;
                            float neutral = document.getDouble("neutral") != null ? document.getDouble("neutral").floatValue() : 0.0f;

                            float[] emotions = {anger, anxiety, happiness, disgust, neutral};
                            List<Entry>[] emotionEntries = new List[]{loadedAngerEntries, loadedAnxietyEntries, loadedHappinessEntries, loadedDisgustEntries, loadedNeutralEntries};
                            int maxIndex = 0;

                            for (int i = 1; i < emotions.length; i++) {
                                if (emotions[i] > emotions[maxIndex]) {
                                    maxIndex = i;
                                }
                            }

                            // 각 데이터에 대해 타임스탬프를 X축 값으로 사용
                            emotionEntries[maxIndex].add(new Entry(timestamp, emotions[maxIndex]));
                        }

                        // 처음 로드 시 전체 데이터로 차트 표시
                        displayEmotionTrendChart(loadedAngerEntries, loadedAnxietyEntries, loadedHappinessEntries, loadedDisgustEntries, loadedNeutralEntries);
                        analyzeEmotionTrend(loadedAngerEntries, loadedAnxietyEntries, loadedHappinessEntries, loadedDisgustEntries, loadedNeutralEntries);
                    }
                });
    }
    // 날짜를 타임스탬프로 변환하는 유틸리티 메서드
    private long convertDateToTimestamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void displayEmotionTrendChart(List<Entry> angerEntries, List<Entry> anxietyEntries,
                                          List<Entry> happinessEntries, List<Entry> disgustEntries, List<Entry> neutralEntries) {

        List<ILineDataSet> dataSets = new ArrayList<>();

        if (!angerEntries.isEmpty()) {
            LineDataSet angerDataSet = new LineDataSet(angerEntries, "분노");
            angerDataSet.setColor(ContextCompat.getColor(this, R.color.red));
            angerDataSet.setLineWidth(2f);
            angerDataSet.setCircleRadius(5f);
            angerDataSet.setCircleColor(ContextCompat.getColor(this, R.color.red));
            angerDataSet.setDrawValues(true);
            dataSets.add(angerDataSet);
        }

        if (!anxietyEntries.isEmpty()) {
            LineDataSet anxietyDataSet = new LineDataSet(anxietyEntries, "불안");
            anxietyDataSet.setColor(ContextCompat.getColor(this, R.color.blue));
            anxietyDataSet.setLineWidth(2f);
            anxietyDataSet.setCircleRadius(5f);
            anxietyDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blue));
            anxietyDataSet.setDrawValues(true);
            dataSets.add(anxietyDataSet);
        }

        if (!happinessEntries.isEmpty()) {
            LineDataSet happinessDataSet = new LineDataSet(happinessEntries, "행복");
            happinessDataSet.setColor(ContextCompat.getColor(this, R.color.green));
            happinessDataSet.setLineWidth(2f);
            happinessDataSet.setCircleRadius(5f);
            happinessDataSet.setCircleColor(ContextCompat.getColor(this, R.color.green));
            happinessDataSet.setDrawValues(true);
            dataSets.add(happinessDataSet);
        }

        if (!disgustEntries.isEmpty()) {
            LineDataSet disgustDataSet = new LineDataSet(disgustEntries, "혐오");
            disgustDataSet.setColor(ContextCompat.getColor(this, R.color.magenta));
            disgustDataSet.setLineWidth(2f);
            disgustDataSet.setCircleRadius(5f);
            disgustDataSet.setCircleColor(ContextCompat.getColor(this, R.color.magenta));
            disgustDataSet.setDrawValues(true);
            dataSets.add(disgustDataSet);
        }

        if (!neutralEntries.isEmpty()) {
            LineDataSet neutralDataSet = new LineDataSet(neutralEntries, "중립");
            neutralDataSet.setColor(ContextCompat.getColor(this, R.color.gray));
            neutralDataSet.setLineWidth(2f);
            neutralDataSet.setCircleRadius(5f);
            neutralDataSet.setCircleColor(ContextCompat.getColor(this, R.color.gray));
            neutralDataSet.setDrawValues(true);
            dataSets.add(neutralDataSet);
        }

        // 선택된 감정 데이터셋을 차트에 설정하고 갱신
        LineData lineData = new LineData(dataSets);
        emotionTrendChart.setData(lineData);
        emotionTrendChart.invalidate(); // 차트 갱신
    }

    private void analyzeEmotionTrend(List<Entry> angerEntries, List<Entry> anxietyEntries,
                                     List<Entry> happinessEntries, List<Entry> disgustEntries, List<Entry> neutralEntries) {
        float angerSum = 0, anxietySum = 0, happinessSum = 0, disgustSum = 0, neutralSum = 0;
        for (Entry entry : angerEntries) angerSum += entry.getY();
        for (Entry entry : anxietyEntries) anxietySum += entry.getY();
        for (Entry entry : happinessEntries) happinessSum += entry.getY();
        for (Entry entry : disgustEntries) disgustSum += entry.getY();
        for (Entry entry : neutralEntries) neutralSum += entry.getY();

        float totalEmotionSum = angerSum + anxietySum + happinessSum + disgustSum + neutralSum;
        float angerAvg = angerSum / Math.max(1, angerEntries.size());
        float anxietyAvg = anxietySum / Math.max(1, anxietyEntries.size());
        float happinessAvg = happinessSum / Math.max(1, happinessEntries.size());
        float disgustAvg = disgustSum / Math.max(1, disgustEntries.size());
        float neutralAvg = neutralSum / Math.max(1, neutralEntries.size());

        // 각 감정의 비율 계산
        float angerRatio = (angerSum / totalEmotionSum) * 100;
        float anxietyRatio = (anxietySum / totalEmotionSum) * 100;
        float happinessRatio = (happinessSum / totalEmotionSum) * 100;
        float disgustRatio = (disgustSum / totalEmotionSum) * 100;
        float neutralRatio = (neutralSum / totalEmotionSum) * 100;

        int angerCount = angerEntries.size();
        int anxietyCount = anxietyEntries.size();
        int happinessCount = happinessEntries.size();
        int disgustCount = disgustEntries.size();
        int neutralCount = neutralEntries.size();

        String diagnosisMessage;

        // 상황별 진단 메시지 생성
        if (happinessRatio > 70) {
            diagnosisMessage = "현재 매우 긍정적인 상태입니다! 이러한 기분을 오래 유지할 수 있도록 주의해보세요.";
        } else if (angerRatio > 50 && angerCount > 2) {
            diagnosisMessage = "분노가 주된 감정으로 나타나고 있습니다. 최근에 스트레스가 많은지 돌아보세요.";
        } else if (anxietyRatio > 50 && anxietyCount > 2) {
            diagnosisMessage = "불안이 주된 감정으로 나타나고 있습니다. 충분한 휴식이 필요할 수 있습니다.";
        } else if (disgustRatio > 50 && disgustCount > 2) {
            diagnosisMessage = "혐오가 주된 감정으로 나타나고 있습니다. 주변 환경을 점검해보는 것이 좋겠습니다.";
        } else if (neutralRatio > 50 && neutralCount > 3) {
            diagnosisMessage = "무감각한 상태가 계속되고 있습니다. 소소한 즐거움을 찾아보는 것도 좋을 것입니다.";
        } else if (angerRatio > 30 && anxietyRatio > 30) {
            diagnosisMessage = "분노와 불안이 함께 높은 상태입니다. 감정 조절에 유의하세요.";
        } else if (happinessRatio < 20 && (angerRatio + anxietyRatio + disgustRatio) > 70) {
            diagnosisMessage = "부정적 감정이 다수 감지됩니다. 심리적인 부담을 덜어낼 방법을 찾아보세요.";
        } else if (angerAvg > 80 && anxietyAvg > 80 && disgustAvg > 80) {
            diagnosisMessage = "모든 부정적 감정이 매우 높은 상태입니다. 심각한 스트레스 상태일 수 있으니 도움을 받는 것이 좋습니다.";
        } else if (neutralRatio > 40 && happinessRatio < 20) {
            diagnosisMessage = "무표정한 감정 상태가 주를 이루고 있으며, 즐거움을 찾기 어려운 상황일 수 있습니다.";
        } else if (happinessRatio > neutralRatio && happinessRatio > (angerRatio + anxietyRatio + disgustRatio)) {
            diagnosisMessage = "전체적으로 긍정적인 상태이며, 안정적인 감정 상태를 유지하고 있습니다.";
        } else {
            diagnosisMessage = "감정 상태가 비교적 안정적입니다.";
        }

        tvDiagnosisMessage.setText(diagnosisMessage);
    }
}