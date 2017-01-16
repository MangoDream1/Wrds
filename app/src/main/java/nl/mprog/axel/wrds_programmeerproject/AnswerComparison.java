package nl.mprog.axel.wrds_programmeerproject;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 15-01-17.
 */

public class AnswerComparison {

    public boolean checkCorrect(String wordA, String wordB) {
        Log.d("test wordA", wordA);
        Log.d("test wordB", wordB);

        return wordA.equals(wordB);
    }

    public SpannableString createWrongUnderlinedString(String wordA, String wordB) {
        SpannableString spanString = new SpannableString(wordA);

        List<String> similarities = findSimilarities(wordA, wordB);

        ArrayList<Integer> indexes = new ArrayList<>();

        indexes.add(0);

        int offset = 0;
        int wordALength = wordA.length();

        for (String similarity: similarities) {
            Log.d("test similiarity", similarity);

            int start = wordA.indexOf(similarity);

            indexes.add(start + offset);
            indexes.add(start + similarity.length() + offset);

            offset = offset + similarity.length();
            wordA = wordA.replaceFirst(similarity, "");

            Log.d("test wordA", wordA);
            Log.d("test offset", String.valueOf(offset));

        }

        indexes.add(wordALength);

        Log.d("test ding", indexes.toString());

        for (int i = 0; i < indexes.size(); i = i + 2) {
            int start = indexes.get(i);
            int end = indexes.get(i+1);

            Log.d("test start", String.valueOf(start));
            Log.d("test end", String.valueOf(end));


            spanString.setSpan(new UnderlineSpan(), start, end, 0);
        }
//
//        //TODO reverse span. Now underline fault instead of correct
//        for (String similarity: similarities) {
//            int start = wordA.indexOf(similarity);
//            int end = start + similarity.length();
//
//            spanString.setSpan(new UnderlineSpan(), start, end, 0);
//        }

        return spanString;
    }

    private List<String> createPartitions(String word, int size) {
        List<String> l = new ArrayList<>();

        for (int i = 0; i < word.length() - size + 1; i++) {
            String substring = word.substring(i, i+ size);
            l.add(substring);
        }

        return l;
    }

    private List<String> findSimilarities(String wordA, String wordB,
                                          int size, List<String> result) {

        if (size == 1) {
            return result;
        }

        List<String> lstA = createPartitions(wordA, size);
        List<String> lstB = createPartitions(wordB, size);

        for (String bPartition: lstB) {
            if (lstA.contains(bPartition)) {
                result.add(bPartition);
                lstA.remove(bPartition);

                // TODO make it replace first but also fix issue with out of bounds span creation
                wordA = wordA.replace(bPartition, "_");

                Log.d("test partition", wordA);
            }

        }

        return findSimilarities(wordA, wordB, size-1, result);
    }

    private List<String> findSimilarities(String wordA, String wordB) {
        return findSimilarities(wordA, wordB, wordB.length(), new ArrayList<String>());
    }
}
