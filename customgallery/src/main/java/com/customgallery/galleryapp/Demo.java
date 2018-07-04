package com.customgallery.galleryapp;

public class Demo {
    public static void main(String[] args) {
        int array1[] = {203, 204, 205, 206, 207, 208, 203, 204, 205, 206};
        int array2[] = {203, 204, 204, 205, 206, 207, 205, 208, 203, 206, 205, 206, 204};
        int count;

        for (int i = 0; i < array1.length; i++) {
            for (int j = i + 1; j < array1.length; j++) {
                if (array1[i] > array1[j]) {
                    int temp = array1[i];
                    array1[i] = array1[j];
                    array1[j] = temp;
                }
            }
        }

        for (int i = 0; i < array2.length; i++) {
            for (int j = i + 1; j < array2.length; j++) {
                if (array2[i] > array2[j]) {
                    int temp = array2[i];
                    array2[i] = array2[j];
                    array2[j] = temp;
                }
            }
        }

        for (int i : array1) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i : array2) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < array1.length; i++) {
            count = 1;
            for (int j = i + 1; j < array1.length; j++) {
                if (array1[i] == array1[j]) {
                    count++;
                }
            }
            System.out.println("Count: " + count);
        }
        System.out.println("\n\n");

        for (int i = 0; i < array2.length; i++) {
            count = 1;
            for (int j = i + 1; j < array2.length; j++) {
                if (array2[i] == array2[j]) {
                    count++;
                }
            }
            System.out.println("Count: " + count);
        }
    }
}
