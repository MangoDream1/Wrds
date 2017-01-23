package nl.mprog.axel.wrds_programmeerproject.Algorithms;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 15-01-17.
 */

public class AnswerComparison {

    public static boolean checkCorrect(String wordA, String wordB) {
        return wordA.equals(wordB);
    }

    public static SpannableString underlineWrongPart(String wordA, String wordB) {
        SpannableString spanString = new SpannableString(wordA);

        // If the wordB is empty then return all underlined otherwise will crash later
        if (wordB.isEmpty()) {
            spanString.setSpan(new UnderlineSpan(), 0, wordA.length(), 0);
            return spanString;
        }

        List<String> similarities = findSimilarities(wordA, wordB);
        List<Integer> indexes = findIndexesOfSubstringsInString(wordA, similarities);

        for (int i = 0; i < indexes.size(); i = i + 2) {
            spanString.setSpan(new UnderlineSpan(), indexes.get(i), indexes.get(i+1), 0);
        }

        return spanString;
    }

    private static List<Integer> findIndexesOfSubstringsInString(String string,
                                                          List<String> substringList) {
        int wordALength = string.length();

        // Create index and add 0 for start
        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(0);

        // Loop through substringList and find all indexes in string
        for (String substring: substringList) {
            int start = string.indexOf(substring);

            if (start == -1) {
                continue;
            }

            indexes.add(start);
            indexes.add(start + substring.length());

            // Replace substring with an empty string the same length as the substring
            string = string.replaceFirst(substring, new String(new char[substring.length()]));
        }

        indexes.add(wordALength);

        return indexes;
    }

    private static List<String> createPartitions(String word, int size) {
        List<String> l = new ArrayList<>();

        // Create all possible substring of the given size by looping
        for (int i = 0; i < word.length() - size + 1; i++) {
            String substring = word.substring(i, i+ size);
            l.add(substring);
        }

        return l;
    }

    private static List<String> findSimilarities(String wordA, String wordB,
                                          int size, List<String> result) {

        // If size equals 1 then quit since single letter are not a good indication of correctness
        if (size == 1) {
            return result;
        }

        List<String> lstA = createPartitions(wordA, size);
        List<String> lstB = createPartitions(wordB, size);

        for (String bPartition: lstB) {
            if (lstA.contains(bPartition)) {
                result.add(bPartition);
                lstA.remove(bPartition);

                // Replace partition with nonsense so that the coming smaller partitions
                // are not found in this larger partition
                wordA = wordA.replaceFirst(bPartition, "\0");
                wordB = wordB.replaceFirst(bPartition, "\1");
            }
        }
        // Recursion to find all similarities regardless of size
        return findSimilarities(wordA, wordB, size-1, result);
    }

    private static List<String> findSimilarities(String wordA, String wordB) {
        return findSimilarities(wordA, wordB, wordB.length(), new ArrayList<String>());
    }
}
