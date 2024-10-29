package com.example.emotion_classification;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmotionTrendActivity extends AppCompatActivity {

    private Button btnSelectDateRange;
    private LineChart emotionTrendChart;
    private TextView tvDiagnosisMessage;
    private FirebaseFirestore firestore;

    private void setupChartAppearance() {
        // 차트 설명 비활성화
        emotionTrendChart.getDescription().setEnabled(false);

        // X축 스타일 설정
        XAxis xAxis = emotionTrendChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY); // 그리드 라인 색상 설정
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f); // 텍스트 크기 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축 위치 설정

        // 왼쪽 Y축 스타일 설정
        YAxis leftAxis = emotionTrendChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f); // 텍스트 크기 설정
        leftAxis.setGridColor(Color.LTGRAY); // 그리드 라인 색상 설정

        // 오른쪽 Y축 비활성화
        YAxis rightAxis = emotionTrendChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 범례 스타일 설정
        Legend legend = emotionTrendChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(14f); // 범례 텍스트 크기
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_trend);

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


    }

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

    // 날짜 범위가 있는 loadEmotionTrendData 메서드 추가
    private void loadEmotionTrendData(String startDate, String endDate) {
        firestore.collection("diaries")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entry> angerEntries = new ArrayList<>();
                        List<Entry> anxietyEntries = new ArrayList<>();
                        List<Entry> happinessEntries = new ArrayList<>();
                        List<Entry> disgustEntries = new ArrayList<>();
                        List<Entry> neutralEntries = new ArrayList<>(); // 중립 감정 추가

                        int dayIndex = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float anger = document.getDouble("anger") != null ? document.getDouble("anger").floatValue() : 0.0f;
                            float anxiety = document.getDouble("anxiety") != null ? document.getDouble("anxiety").floatValue() : 0.0f;
                            float happiness = document.getDouble("happiness") != null ? document.getDouble("happiness").floatValue() : 0.0f;
                            float disgust = document.getDouble("disgust") != null ? document.getDouble("disgust").floatValue() : 0.0f;
                            float neutral = document.getDouble("neutral") != null ? document.getDouble("neutral").floatValue() : 0.0f;

                            angerEntries.add(new Entry(dayIndex, anger));
                            anxietyEntries.add(new Entry(dayIndex, anxiety));
                            happinessEntries.add(new Entry(dayIndex, happiness));
                            disgustEntries.add(new Entry(dayIndex, disgust));
                            neutralEntries.add(new Entry(dayIndex, neutral));
                            dayIndex++;
                        }

                        displayEmotionTrendChart(angerEntries, anxietyEntries, happinessEntries, disgustEntries, neutralEntries);
                        analyzeEmotionTrend(angerEntries, anxietyEntries, happinessEntries, disgustEntries, neutralEntries);
                    }
                });
    }

    private void displayEmotionTrendChart(List<Entry> angerEntries, List<Entry> anxietyEntries,
                                          List<Entry> happinessEntries, List<Entry> disgustEntries, List<Entry> neutralEntries) {
        LineDataSet angerDataSet = new LineDataSet(angerEntries, "분노");
        LineDataSet anxietyDataSet = new LineDataSet(anxietyEntries, "불안");
        LineDataSet happinessDataSet = new LineDataSet(happinessEntries, "행복");
        LineDataSet disgustDataSet = new LineDataSet(disgustEntries, "혐오");
        LineDataSet neutralDataSet = new LineDataSet(neutralEntries, "중립"); // 중립 데이터 추가

        angerDataSet.setColor(ContextCompat.getColor(this, R.color.red));
        anxietyDataSet.setColor(ContextCompat.getColor(this, R.color.blue));
        happinessDataSet.setColor(ContextCompat.getColor(this, R.color.green));
        disgustDataSet.setColor(ContextCompat.getColor(this, R.color.magenta));
        neutralDataSet.setColor(ContextCompat.getColor(this, R.color.gray));

        // 스타일 설정
        angerDataSet.setLineWidth(2f);
        angerDataSet.setCircleRadius(5f);
        angerDataSet.setCircleColor(ContextCompat.getColor(this, R.color.red));
        angerDataSet.setDrawValues(true); // 값 표시 활성화

        anxietyDataSet.setLineWidth(2f);
        anxietyDataSet.setCircleRadius(5f);
        anxietyDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blue));
        anxietyDataSet.setDrawValues(true);

        happinessDataSet.setLineWidth(2f);
        happinessDataSet.setCircleRadius(5f);
        happinessDataSet.setCircleColor(ContextCompat.getColor(this, R.color.green));
        happinessDataSet.setDrawValues(true);

        disgustDataSet.setLineWidth(2f);
        disgustDataSet.setCircleRadius(5f);
        disgustDataSet.setCircleColor(ContextCompat.getColor(this, R.color.magenta));
        disgustDataSet.setDrawValues(true);

        neutralDataSet.setLineWidth(2f);
        neutralDataSet.setCircleRadius(5f);
        neutralDataSet.setCircleColor(ContextCompat.getColor(this, R.color.gray));
        neutralDataSet.setDrawValues(true);

        // 데이터를 차트에 설정하고 갱신
        LineData lineData = new LineData(angerDataSet, anxietyDataSet, happinessDataSet, disgustDataSet, neutralDataSet);
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
        for (Entry entry : neutralEntries) neutralSum += entry.getY(); // 중립 감정 합산

        float angerAvg = angerSum / angerEntries.size();
        float anxietyAvg = anxietySum / anxietyEntries.size();
        float happinessAvg = happinessSum / happinessEntries.size();
        float disgustAvg = disgustSum / disgustEntries.size();
        float neutralAvg = neutralSum / neutralEntries.size(); // 중립 감정 평균
        float negativeAvg_NX = (angerSum + anxietySum + disgustSum) / 3; // 중립 없이 부정적 감정 평균
        float negativeAvg_NO = (angerSum + anxietySum + disgustSum + neutralAvg) / 4; // 중립 포함 부정적 감정 평균

        String diagnosisMessage;
        if (happinessAvg < 20 && ((angerAvg < 60 && angerAvg > 40) || (anxietyAvg > 40 && anxietyAvg < 60)
                || (disgustAvg > 40 && disgustAvg < 60 ) || (neutralAvg > 40  && neutralAvg < 60))) {
            diagnosisMessage = " 특정 부정적인 감정이 눈에 띕니다. 우울증 경향이 보이기 시작했어요! 아직 위험한 단계는 아니지만, ........";
        }
        else if (happinessAvg < 20 && ((angerAvg < 80 && angerAvg > 60) || (anxietyAvg > 60 && anxietyAvg < 80)
                || (disgustAvg > 60 && disgustAvg < 80 ) || (neutralAvg > 60  && neutralAvg < 80))) {
            diagnosisMessage = "우울증이 의심 됩니다. 어느 한 감정이 굉장히 눈에 띄고 있어요! 바쁘고 힘들더라도 휴식을 권장해요.";
        } else if (happinessAvg < 20 && (negativeAvg_NX > 40 &&  negativeAvg_NX < 60) ) {
            diagnosisMessage = "부정적인 요소들이 평균적으로 높은 편 이에요!, 우울증으로 발전 될 가능성이 높습니다. 최근 안좋았던 일들은 멀리 하시는게 어떨까요? ";
        }
        else if (happinessAvg < 20 && (negativeAvg_NX > 60 &&  negativeAvg_NX < 80) ) {
            diagnosisMessage = "최근 경향을 보면 우울증 같은 정신질환에 걸렸을 가능성이 매우 큽니다. 시급하게 전문가 또는 가까운 지인과의 상담이 필요해요!";
        }
        else if (happinessAvg < 20 && (negativeAvg_NX > 80) ) {
            diagnosisMessage = "매우 위험한 상태 입니다. 모든 활동을 멈추고, 전문 상담이 필수 입니다. 최대한 이성적인 사고를 유지 하세요.";
        }else if (happinessAvg < 30 &&  neutralAvg > 60 && negativeAvg_NX < neutralAvg) { // 행복이 낮고, 중립이 부정보다 높은 경우
            diagnosisMessage = "현재 우울증에 대한 가능성이 있어요! 최근에 무표정인 날이 많지 않았나요?";}
        else if (happinessAvg < negativeAvg_NX) {
            diagnosisMessage = "부정적인 감정들이 긍정적인 감정보다 다소 높은 경향을 보이고 있어요! 크게 걱정할 정도는 아니지만, 여가 활동을 즐겨보세요!";}
        else {
            System.out.println(angerAvg);
            System.out.println(anxietyAvg);
            System.out.println(happinessAvg);
            System.out.println(disgustAvg);
            System.out.println(neutralAvg);
            diagnosisMessage = "감정 상태가 비교적 안정적입니다. ";
        }
        tvDiagnosisMessage.setText(diagnosisMessage);
    }
}