package leetcode;

import java.util.*;

public class TwoSum {

    // each input would have exactly one solution
    // target = arr[minIndex] + arr
    // 对于数组太大的时候 排序超时
    public static int[] twoSum(final int[] numbers, int target) {
        int n = numbers.length;
        List<Integer> pos = new ArrayList<Integer>();
        for (int i = 0; i < numbers.length; i++) {
            pos.add(i);
        }

        Collections.sort(pos, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return numbers[o1] - numbers[o2];
            }
        });

        for (int i = 0, j = n - 1; i < j; ) {
            if (numbers[pos.get(i)] + numbers[pos.get(j)] == target) {
                int indexMin = Math.min(pos.get(i) , pos.get(j)) + 1;
                int indexMax = Math.max(pos.get(i) , pos.get(j)) + 1;
                return new int[]{indexMin, indexMax};
            } else if (numbers[pos.get(i)] + numbers[pos.get(j)] < target) {
                i++;
            } else {
                j--;
            }
        }

        return new int[]{-1, -1}; //not found in array
    }

    public static int[] twoSumMap(final int[] numbers, int target, int begin){
        Map<Integer, Integer> mapKey = new HashMap<Integer, Integer>();
        int n  = numbers.length;
        for (int i = begin; i < n; i++){
            if (mapKey.containsKey(numbers[i])){
                int indexMin = Math.min(i, mapKey.get(numbers[i])) + 1;
                int indexMax = Math.max(i, mapKey.get(numbers[i])) + 1;
                return new int[]{indexMin, indexMax};
            }
            mapKey.put(target - numbers[i], i);
        }
        return new int[]{-1, -1}; //not found in array
    }


}
