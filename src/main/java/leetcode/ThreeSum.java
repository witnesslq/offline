package leetcode;

import java.util.*;

/**
 * Created by fengwu on 15/4/6.
 * <p>
 * Given an array S of n integers, are there elements a, b, c in S such that a + b + c = 0?
 * Find all unique triplets in the array which gives the sum of zero.
 * <p>
 * Note:
 * Elements in a triplet (a,b,c) must be in non-descending order. (ie, a ≤ b ≤ c)
 * The  solution set must not contain duplicate triplets.
 * For example, given array S = {-1 0 1 2 -1 -4},
 * <p>
 * A solution set is:
 * (-1, 0, 1)
 * (-1, -1, 2)
 */
public class ThreeSum {

    private void makeTriple(ArrayList<List<Integer>> ans, final int a, final int b, final int c) {
        List<Integer> solution = new ArrayList<Integer>();
        solution.add(a);
        solution.add(b);
        solution.add(c);
        ans.add(solution);
    }

    private void twoSumMap(ArrayList<List<Integer>> ans, int[] num, int target, int begin) {
        Map<Integer, Integer> mapKey = new HashMap<Integer, Integer>();
        int n = num.length;
        for (int i = begin; i < n; i++) {
            if (mapKey.containsKey(num[i])) {
                int max = Math.max(num[i], num[mapKey.get(num[i])]);
                int min = Math.min(num[i], num[mapKey.get(num[i])]);
                makeTriple(ans, target, min, max);
            }
            mapKey.put(-target - num[i], i);
        }
    }


    /*
     * 不用返回数组索引
     */
    private void twoSum(ArrayList<List<Integer>> ans, int[] num, int target, int begin) {
        int i = begin;
        int j = num.length - 1;
        while (i < j) {
            int sum = num[i] + num[j];
            if (sum < target) {
                i++;
            } else if (sum > target) {
                j--;
            } else {
                makeTriple(ans, -target, num[i], num[j]);
                do {
                    i++;
                } while (i < j && num[i] == num[i - 1]);
                do {
                    j--;
                } while (i < j && num[j] == num[j + 1]);
            }
        }
    }


    public List<List<Integer>> threeSum(int[] num) {
        int length = num.length;
        HashMap<Integer, Integer> tuple = new HashMap<Integer, Integer>();
        ArrayList<List<Integer>> ans = new ArrayList<List<Integer>>();
        Arrays.sort(num);
        for (int i = 0; i < length - 2; i++) {
            if (i > 0 && num[i] == num[i - 1]) {
                continue;
            }
            //twoSumMap(ans, num, num[i], i + 1);
            twoSum(ans, num, -num[i], i + 1);

        }
        return ans;
    }
}
