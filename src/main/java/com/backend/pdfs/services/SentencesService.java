package com.backend.pdfs.services;

import com.backend.pdfs.entities.Sentences;
import com.backend.pdfs.errorHandling.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SentencesService {
    @Autowired
    SentencesRepository sentencesRepository;

    public void insert(Sentences sentences){
        sentencesRepository.insert(sentences);
    }

    public Optional<Sentences> findById(String id){
        return sentencesRepository.findById(id);
    }

    public void deleteById(String id){
        sentencesRepository.deleteById(id);
    }

    public List<Sentences> findBySentencesContaining(String keyword){
        return sentencesRepository.findBySentencesContaining(keyword);
    }

    public Map countOccurrences(String id, String keyword){
        Sentences sentences = sentencesRepository.countOccurrences(id, keyword);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("document_id", id);
        map.put("matched_word", keyword);
        map.put("occurrences", 0);

        int wordCount = 0;
        if (sentences != null){
            Pattern pattern = Pattern.compile("(?i)" + keyword);
            String text = String.join(" ", sentences.getSentences());
            Matcher matcher = pattern.matcher(text);

            while (matcher.find())
                wordCount++;

            map.put("sentences", sentences.getSentences());
        }

        map.put("occurrences", wordCount);

        return map;
    }

    public Map<String, Integer> getMostOccurringWords(String id) throws CustomException, IOException {
        // Load the stop words list
        List<String> stopWords = new ArrayList<>();
        InputStream inputStream = getClass().getResourceAsStream("/static/stopwords.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            stopWords.add(line);
        }

        Sentences sentences = findById(id).orElseThrow(() ->
                new CustomException("Sentences file not found in the database", HttpStatus.NOT_FOUND));

        // Combine the sentences into one string and keep alphabet and spaces only
        String text = String.join(" ", sentences.getSentences());
        text = text.replaceAll("[^a-zA-Z\\s]", "");
        String filtered = Arrays.stream(text.split(" ")).filter(word -> !stopWords.contains(word.toLowerCase()))
                        .collect(Collectors.joining(" "));

        // Use a priority queue to poll the entries in descending order
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue());

        // Split the string into words and count their occurrences
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : filtered.split("\\s+")) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

        // Add the entries to the priority queue
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            pq.offer(entry);
        }

        // Add the top 5 occurring words to the final Map
        Map<String, Integer> words = new LinkedHashMap<>();
        int count = 5;
        while (count-- > 0 && !pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            words.put(entry.getKey(), entry.getValue());
        }
        return words;
    }
}
