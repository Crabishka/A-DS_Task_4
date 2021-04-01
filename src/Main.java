import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int N = 10;
        int[] arrayForRadix = new int[N];
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            arrayForRadix[i] = random.nextInt();
        }
        long start;
        long usedBytes;
        int[] arrayForSort = arrayForRadix.clone();


        start = System.currentTimeMillis();
        Arrays.sort(arrayForSort);
        System.out.println("Arrays.sort time - " + (System.currentTimeMillis() - start));
        usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used MegaBytes = " + usedBytes / 1048576);

        start = System.currentTimeMillis();
        radixSort(arrayForRadix, typeOfRadixSort.LSDByBytes);
        System.out.println("My Radix sort time - " + (System.currentTimeMillis() - start));
        usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Used MegaBytes = " + usedBytes / 1048576);


        for (int value : arrayForRadix) {
            System.out.println(value);
        }


    }

    enum typeOfRadixSort {
        LSDByBits, // LSD -  least significant digit
        LSDByBytes,
        MSDByBits, // MSD - most significant digit
        MSDByBytes;
    }

    public static void radixSort(int[] a) {
        radixSortLSDByBits(a);
    }

    public static void radixSort(int[] a, typeOfRadixSort type) {
        if (type == typeOfRadixSort.LSDByBits) radixSortLSDByBits(a);
        if (type == typeOfRadixSort.LSDByBytes) radixSortLSDByBytes(a);
    }

    public static void radixSortLSDByBits(int[] a) {

        // проходимся по массиву N раз, где N - максимальное количество разрядов (по дефолту 31)
        // создать массивы с количеством чисел под каждое состояние разряда
        // пройтись по этому массиву, и к каждому следующему элементу массива прибавить предыдущий
        // составить новый массив (можно прямо на старый (нельзя))
        // отдельно отсортировать отрицательные (есть идея сразу отсеять массив отрицательных и положительных чисел)
        // в принципе это тоже O(N)

        int length = a.length;
        int max = Integer.MIN_VALUE;
        int countOfNegative = 0;
        for (int value : a) {
            if (value > max) max = value;
            if (value < 0) countOfNegative++;
        }

        for (int i = 0; i < Math.log(max) / Math.log(2) + 1; i++) {   // проходимся

            int[] index = new int[2];  // создали
            int[] newArray = new int[length];

            for (int value : a) {  // O(N)
                index[Math.abs(value >> (i)) % 2]++;
            }

            int prev = 0;

            for (int j = 1; j < 2; j++) { // добавили O(N)
                index[j] += index[j - 1];
            }

            for (int j = length - 1; j >= 0; j--) {  // O(N)
                int tempIndex = Math.abs(a[j] >> (i)) % 2;
                index[tempIndex]--;
                newArray[index[tempIndex]] = a[j];

            }

            for (int k = 0; k < length; k++) {   // O(N)
                a[k] = newArray[k];              // как присвоить один массив другому...
            }

        }

        // тупая идея - создаем новый массив, зная количество отрицательных чисел
        int[] result = new int[length];
        for (int i = 0; i < countOfNegative; i++) {
            result[i] = a[length - countOfNegative + i];
        }
        for (int i = 0; i < length - countOfNegative; i++) { // O(N)
            result[i + countOfNegative] = a[i];
        }

        for (int k = 0; k < length; k++) {   // O(N)
            a[k] = result[k];
        }

    }

    public static void radixSortLSDByBytes(int[] a) {

        // разделить на положительный массив и отрицательный, потом слить

        int length = a.length;
        int max = Integer.MIN_VALUE;
        int countOfNegative = 0;
        for (int value : a) {
            if (value > max) max = value;
            if (value < 0) countOfNegative++;
        }

        for (int i = 0; i < 4; i++) {   // проходимся

            int[] index = new int[256];  // создали
            int[] newArray = new int[length];

            for (int value : a) {  // O(N)
                int temp = (value >> ((i) * 8)) % 256;
                if (temp < 0 && i == 3) index[(Math.abs(temp) + 127)]++ ;
                else index[(Math.abs(temp))]++;
            }

            int prev = 0;

            for (int j = 1; j < 256; j++) { // добавили O(N)
                index[j] += index[j - 1];
            }

            for (int j = length - 1; j >= 0; j--) {  // O(N)

                int tempIndex = (a[j] >> ((i) * 8)) % 256;
                if (tempIndex < 0 && i == 3) tempIndex = Math.abs(tempIndex) + 127;
                else tempIndex = Math.abs(tempIndex);
                index[tempIndex]--;
                newArray[index[tempIndex]] = a[j];

            }

            for (int k = 0; k < length; k++) {   // O(N)
                a[k] = newArray[k];              // как присвоить один массив другому...
            }

        }

        // существуют какие-то приколы с побитовыми сдвигами и int
        // тупая идея - создаем новый массив, зная количество отрицательных чисел
        int[] result = new int[length];

        for (int i = length - 1; i >= length - countOfNegative; i--) {
            result[length - i - 1] = a[i];
        }

        for (int i = 0; i < length - countOfNegative; i++) { // O(N)
            result[i + countOfNegative] = a[i];
        }

        for (int k = 0; k < length; k++) {   // O(N)
            a[k] = result[k];
        }

    }
}
