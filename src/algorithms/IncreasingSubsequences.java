package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Part 2: Algorithms
 * Finds all strictly increasing contiguous subsequences in an array.
 */
public class IncreasingSubsequences {

    public static void main(String[] args) {
        // Example
        int[] input = {1, 2, 3, 1, 2};

        System.out.println("Input: " + java.util.Arrays.toString(input));
        System.out.println("Output:");

        List<List<Integer>> result = findIncreasingSubsequences(input);

        // Print results
        for (List<Integer> sub : result) {
            System.out.println(sub);
        }
    }

    /**
     * The Algorithm Function.
     * Iterates through the array and collects consecutive increasing numbers.
     * Time Complexity: O(N)
     * Space Complexity: O(N)
     */
    public static List<List<Integer>> findIncreasingSubsequences(int[] arr) {
        List<List<Integer>> allSubsequences = new ArrayList<>();

        if (arr == null || arr.length == 0) {
            return allSubsequences;
        }

        List<Integer> currentSub = new ArrayList<>();
        currentSub.add(arr[0]);

        for (int i = 1; i < arr.length; i++) {
            // If current number is greater than previous - continue the sequence
            if (arr[i] > arr[i - 1]) {
                currentSub.add(arr[i]);
            } else {
                // Sequence broken: save current sequence and start a new one
                allSubsequences.add(new ArrayList<>(currentSub));
                currentSub.clear();
                currentSub.add(arr[i]);
            }
        }

        // Add the final sequence remaining in the list
        if (!currentSub.isEmpty()) {
            allSubsequences.add(new ArrayList<>(currentSub));
        }

        return allSubsequences;
    }
}