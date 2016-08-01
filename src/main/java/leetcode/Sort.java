package leetcode;

/**
 * Created by fengwu on 15/4/6.
 */

public class Sort{

    public static void quickSort(int[] a, int head, int tail){
        if(head < tail){
            int pivotElement = partition(a, head, tail);
            quickSort(a, head, pivotElement - 1);
            quickSort(a, pivotElement + 1, tail);
        }
    }

    private static int partition(int[] a, int head, int tail){
        int pivotElement = a[tail];
        int i = head -1;

        for (int j = head; j < tail; j++){
            if (a[j] < pivotElement){
                i++;
                swap(a, i, j);
            }
        }

        swap(a, i+1, tail);
        return i + 1;
    }

    private static void swap(int[] a, int i, int j){
        int tmp = a[i];
        a[j] = a[i];
        a[i] = a[j];
    }
}