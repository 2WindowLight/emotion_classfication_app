package com.example.emotion_classification.utils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SimpleTokenizer {
    private Map<String, Integer> vocab;

    public SimpleTokenizer(Context context) throws IOException {
        vocab = loadVocab(context);
    }

    private Map<String, Integer> loadVocab(Context context) throws IOException {
        Map<String, Integer> vocabMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("vocab.txt")))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                vocabMap.put(line.trim(), index++);
            }
        }
        // 만약 `[UNK]`가 포함되지 않았다면 기본값으로 0을 할당
        vocabMap.putIfAbsent("[UNK]", 0);
        return vocabMap;
    }
    public int getUnknownTokenId() {
        return vocab.getOrDefault("[UNK]", 0); // Returns the ID for "[UNK]" or a default of 0 if not found
    }

    public int[] tokenize(String text) {
        String[] words = text.split(" ");
        int[] tokens = new int[Math.min(words.length, 128)];  // 최대 128 길이만 허용
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = vocab.getOrDefault(words[i], vocab.get("[UNK]"));
        }
        return tokens;
    }

}