<div align="center">


  <!-- markdownlint-disable-next-line -->
  # emotion_classfication_app

  BERT 모델을 활용한 우울증 관리 및 감정 분석 일기 어플리케이션

  ## 기술 스택

### 프론트엔드
<div>
  <!-- CSS -->
  <img src="https://img.shields.io/badge/CSS-1572B6?style=flat&logo=CSS3&logoColor=white"/>

  <!-- XML (레이아웃) -->
  <img src="https://img.shields.io/badge/XML-EBB8C3?style=flat&logo=files&logoColor=white" alt="XML Layout" />
  
  <!-- Jetpack Compose -->
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=android&logoColor=white" alt="Jetpack Compose" />
</div>

<br/>

### 백엔드
<div>
  <!-- Python -->
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=Python&logoColor=white"/>
  <!-- Flask -->
  <img src="https://img.shields.io/badge/Flask-000000?style=flat&logo=Flask&logoColor=white"/>
  <!-- Firebase -->
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=Firebase&logoColor=white"/>
  <!-- Java -->
  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white"/>
  <!-- MyBatis -->
  <img src="https://img.shields.io/badge/MyBatis-BE3632?style=flat&logo=MyBatis&logoColor=white"/>
</div>
<br/>
모바일
<div>
  <!-- Kotlin -->
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/>
  <!-- Android Studio -->
  <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat&logo=android-studio&logoColor=white"/>
</div>

### 데이터 분석 / 머신러닝
<div>
  <!-- Spyder (IDE) -->
  <img src="https://img.shields.io/badge/Spyder-FF0000?style=flat&logo=spyder%20ide&logoColor=white"/>
  <!-- Jupyter Notebook -->
  <img src="https://img.shields.io/badge/Jupyter-F37626?style=flat&logo=Jupyter&logoColor=white"/>
  <!-- Google BERT -->
  <img src="https://img.shields.io/badge/Google%20BERT-4285F4?style=flat&logo=google&logoColor=white"/>
</div>


<br/>

### 인프라
<div>
  <!-- MySQL -->
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white"/>
  <!-- Git -->
  <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/>
  <!-- SSH -->
  <img src="https://img.shields.io/badge/SSH-000000?style=flat&logo=SSH&logoColor=white"/>
</div>

</div>





---

## 사용된 데이터셋
- [Emotion_classification.csv](https://github.com/2WindowLight/app-that-analyzes-my-emotions-and-improves-team-cooperation/blob/main/dataSet/Emotion_classification.csv)

## 모델 생성 스크립트 (본 모델 생성 과정은 현재 결과의 달리 추가 파인 튜닝 이전으로 즉, 개선 전의 스크립트로 결과가 다를 수 있음)
본 개발자는 해당 모델 학습 과정을 MacBook M3 pro 로 진행하였으며, 
파인튜닝 미세 조정 및 데이터 클래스 분류 추가 작업 후의 학습은 GPU: RTX4070ti SUPER CPU: AMD 라이젠7-5세대 7800X3D RAM: 32GB 환경에서 진행하였음
모델 개선 전 스펙: MacBook M3 pro
모델 개선 후 스펙: GPU: RTX4070ti SUPER CPU: AMD 라이젠7-5세대 7800X3D RAM: 32GB
- [analyzes emotions.ipynb](https://github.com/2WindowLight/app-that-analyzes-my-emotions-and-improves-team-cooperation/blob/main/Bert_model/analyzes%20emotions.ipynb)

---

## 기획
최근에는 경제 불황에 의해 취업에 대한 어려움으로 **쉬었음 청년**, 즉 비경제활동인구 중 질병 ∙ 장애 등은 없지만 그냥 쉬었음을 나타내는 인구 수가 끊임 없이 증가하고 있습니다.  
우울증과 비슷한 정신적인 문제가 생겼을 경우, 자존감은 현저히 하락하게 됩니다. 이러한 상황이 장기화됨으로써 자택에 머무는 기간이 증가하게 되었습니다.

본 프로젝트에서는 학습된 **BERT 모델**을 기반으로 감정을 분석하고, 결과로 추론된 감정이 우울증과 관련이 있을 경우 이를 시각화하여 사용자가 자신의 상태를 체크하고 피드백을 제공받을 수 있도록 합니다.

---

## 데이터 분석 및 모델 학습 결과

### Flask 서버 동작 플로우 차트
<img width="371" alt="image" src="https://github.com/user-attachments/assets/346

<!-- markdownlint-disable-next-line -->
# Emotion Classification App

BERT 모델을 활용한 우울증 관리 및 감정 분석 어플리케이션

---

## 사용된 데이터셋
- [Emotion_classification.csv](https://github.com/2WindowLight/app-that-analyzes-my-emotions-and-improves-team-cooperation/blob/main/dataSet/Emotion_classification.csv)

## 모델 생성 스크립트
- [analyzes emotions.ipynb](https://github.com/2WindowLight/app-that-analyzes-my-emotions-and-improves-team-cooperation/blob/main/Bert_model/analyzes%20emotions.ipynb)

---

## 기획
최근에는 경제 불황에 의해 취업에 대한 어려움으로 **쉬었음 청년**, 즉 비경제활동인구 중 질병 ∙ 장애 등은 없지만 그냥 쉬었음을 나타내는 인구 수가 끊임 없이 증가 
우울증과 비슷한 정신적인 문제가 생겼을 경우, 자존감은 현저히 하락. 이러한 상황이 장기화됨으로써 자택에 머무는 기간이 증가

본 프로젝트에서는 학습된 **BERT 모델**을 기반으로 감정을 분석하고, 결과로 추론된 감정이 우울증과 관련이 있을 경우 이를 시각화하여 사용자가 자신의 상태를 체크하고 피드백을 제공

---

## 데이터 분석 및 모델 학습 결과

### Flask 서버 동작 플로우 차트
<img width="371" alt="image" src="https://github.com/user-attachments/assets/3465d7cd-5557-483d-8a76-4f5ff3268e6f" />

### Firebase 플로우 차트
<img width="222" alt="image" src="https://github.com/user-attachments/assets/8c1675e3-30ef-4beb-87a0-52a9ee49200d" />

### Firebase 클래스 다이어그램
<img width="190" alt="image" src="https://github.com/user-attachments/assets/1d09d51f-5eb5-4640-8e24-ccfa2dd11edd" />

---

### 병합 데이터셋
총 **218517** 개의 데이터와 **발화, 감정**이라는 2개의 속성으로 통합되었으며, 통합된 데이터셋을 BERT 모델에 맞게 전처리하고 불필요한 중복 데이터를 제거

<img width="348" alt="image" src="https://github.com/user-attachments/assets/818c7968-66b1-430a-96dd-4408293a5109" />

### 데이터셋 전처리 결과
초기 데이터 218517개에서 24772개가 줄었으며, 병합된 데이터셋은 총 **7개의 감정 클래스(슬픔, 중립, 분노, 행복, 불안, 당황, 혐오)**로 구성되었고, 감정 분류 모델 학습에 대한 클래스 분포를 제공

<img width="123" alt="image" src="https://github.com/user-attachments/assets/e41bb208-6e4f-4f31-827d-2a2879567bba" />

### 데이터 증강 기법
감정 데이터셋의 불균형 문제를 해결하기 위해 **동의어 증강(Synonym Augmentation)** 기법을 적용

<img width="452" alt="image" src="https://github.com/user-attachments/assets/f6af8ac6-8226-4a17-aea0-baeb6d775cb1" />

---

### [분노, 불안, 중립, 행복, 혐오] 총 5개 감정의 학습 결과
SynonymAug을 활용하여 일부 감정 레이블(행복, 불안, 혐오 등)의 텍스트를 증

<img width="279" alt="image" src="https://github.com/user-attachments/assets/02aba0db-1550-42c3-88fa-18c605199fdc" />

### Exponential LR 의 파라미터 값 적용
감정 분류 작업에서 **Exponential LR**을 적용하여, 학습 초기에는 빠른 수렴을 유도하고, 후반부에는 세밀한 조정을 통해 높은 정확도의 감정 분류 성능을 기대

![image](https://github.com/user-attachments/assets/242810be-3fb3-4f03-8ef4-a07c86e0c3b1)

---

### 모델 학습 성능

모델의 학습 결과는 아래 그래프에서 **학습 정확도**와 **검증 정확도**의 변화를 통해 확인
BERT 모델을 5개의 에포크 동안 학습한 결과, **학습 정확도**는 꾸준히 상승하여 최종적으로 약 **90%**에 도달하였습니다. 이는 모델이 훈련 데이터에 대해 거의 완벽하게 적합

반면, 검증 정확도는 에포크가 진행되면서 점진적으로 증가하였으나 **85-88%** 사이에서 수렴

<br/>

<img width="279" alt="image" src="https://github.com/user-attachments/assets/e5ed1cd3-c21c-4965-9dcc-83139391e50f" />

---

### 학습 손실도

검증 데이터에 대해 높은 정확도를 기록하였지만, 학습 데이터와 비교할 때 **과적합**의 가능성을 보여주는 결과
학습이 진행됨에 따라 모델이 훈련 데이터에 다소 적합되는 경향이 나타났으며, 새로운 데이터에서의 **일반화 성능**이 상대적으로 낮았다.

<br/>

<img width="297" alt="image" src="https://github.com/user-attachments/assets/58b2b4cc-87e4-4026-9c52-a2fd42eabb96" />

---

### 데이터 클래스별 분포도
<img width="261" alt="image" src="https://github.com/user-attachments/assets/f81c7c1f-d4ac-4e25-98aa-6cd494feee08" />

---

## 애플리케이션의 구성 및 기능

### 애플리케이션 구성도
<img width="288" alt="image" src="https://github.com/user-attachments/assets/ff87ae04-a54a-4463-8986-38fb6f100297" />

### 구상 전체 구조도
<img width="873" alt="image" src="https://github.com/user-attachments/assets/063985a5-707b-49fd-abc0-72a6d3c44feb" />

---

### 로그인 UI & 회원가입 UI
간단한 UI로 사용자에게 명확한 입력 안내를 제공하여 편리하게 접근할 수 있으며, 사용자의 정보는 Firebase에 전달되고 저장/읽기가 가능

<img width="175" alt="image" src="https://github.com/user-attachments/assets/e589f93d-aa10-42de-97f0-7dba95855c53" />
<img width="175" alt="image" src="https://github.com/user-attachments/assets/c1ebd427-ead9-48cc-8c35-adbc02292d43" />

---

### 메인 페이지 UI
상단의 캘린더에서 특정 날짜를 선택하면 해당 날짜에 작성된 일기를 하단에서 확인할 수 있으며, 이를 통해 일기 작성 및 수정이 가능
메인 화면에는 ‘일기 작성’, ‘결과 대시보드’, ‘감정 추세 보기’ 버튼이 있어 각 기능으로 빠르게 이동

<img width="179" alt="image" src="https://github.com/user-attachments/assets/47a32a80-e0e2-407a-a77c-994e635b9d9c" />

---

### 일기 작성/수정 UI
일기 작성 화면에서는 사용자가 하루 동안의 감정을 기록. 제목과 내용을 작성하고, 하단의 ‘저장’ 버튼을 클릭하여 일기를 저장
이 화면은 기존 일기를 선택하여 불러올 때 수정 모드로 사용되며, 기존 일기를 다시 작성하여 업데이트

<img width="246" alt="image" src="https://github.com/user-attachments/assets/bb68f819-328f-4dec-be0f-a54c86acb784" />

---

### 감정 대시보드 UI
이 화면은 사용자가 저장된 감정 데이터를 시각화하여 자신의 감정 변화를 이해하고, 감정 상태를 간단히 파악할 수 있도록 돕는다.

<img width="216" alt="image" src="https://github.com/user-attachments/assets/fe078413-26f4-423f-9073-41ce779f1a9e" />

#### 대시보드 분석 결과
사용자 감정 상태를 원형 차트로 시각화한 예시. 원형 차트는 사용자가 기록한 감정 중 어느 감정이 가장 빈번하게 나타났는지를 비율로 보여준다. 이를 통해 사용자는 자신의 주요 감정 상태를 한눈에 파악

<img width="191" alt="image" src="https://github.com/user-attachments/assets/8a6bd80a-5924-42b7-a054-13c0dcb319ba" />

---

### 감정 추세 통계 UI
이 차트는 사용자가 선택한 기간 동안 매일 기록한 감정의 변화를 보여준다. 각 감정의 변화 추이를 통해 감정이 어떤 패턴으로 나타나고 변동했는지 확인

<img width="184" alt="image" src="https://github.com/user-attachments/assets/75cbc702-09cb-411f-b175-edd8a7a2d7c1" />


