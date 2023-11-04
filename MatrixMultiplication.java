import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class MatrixMultiplication {

    // Function to perform matrix multiplication using a single thread
    public static int[][] matrixMultiplicationSingleThread(int[][] A, int[][] B) {
        int m = A.length;                      
        int n = A[0].length;                   
        int p = B[0].length;                    
        int[][] result = new int[m][p];        

        for (int i = 0; i < m; i++) {           
            for (int j = 0; j < p; j++) {        
                for (int k = 0; k < n; k++) {
                    result[i][j] += A[i][k] * B[k][j]; 
                }
            }
        }

        return result;
    }

    // Function to perform matrix multiplication using multiple threads (one thread per row)
    public static int[][] matrixMultiplicationMultiThreadRow(int[][] A, int[][] B) {
        int m = A.length;                      
        int n = A[0].length;                   
        int p = B[0].length;                    
        int[][] result = new int[m][p];        
        Thread[] threads = new Thread[m];       

        for (int i = 0; i < m; i++) {
            final int row = i;  
            threads[i] = new Thread(() -> {  
                for (int j = 0; j < p; j++) {        
                    for (int k = 0; k < n; k++) {    
                        result[row][j] += A[row][k] * B[k][j];  
                    }
                }
            });
            threads[i].start();  
        }

        try {
            for (Thread thread : threads) {
                thread.join();  
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Function to perform matrix multiplication using multiple threads (one thread per cell)
    public static int[][] matrixMultiplicationMultiThreadCell(int[][] A, int[][] B) {
        int m = A.length;                      
        int p = B[0].length;                    
        int[][] result = new int[m][p];        
        Thread[] threads = new Thread[m * p];  

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                final int row = i;
                final int col = j;
                threads[i * p + j] = new Thread(() -> {  
                    int cell_value = 0;
                    for (int k = 0; k < A[0].length; k++) {
                        cell_value += A[row][k] * B[k][col];
                    }
                    result[row][col] = cell_value;
                });
                threads[i * p + j].start();  
            }
        }

        try {
            for (Thread thread : threads) {
                thread.join();  
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Function to read a matrix from a file
    public static int[][] readMatrixFromFile(String fileName) {
        int[][] matrix = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            int rowCount = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                if (matrix == null) {
                    matrix = new int[values.length][values.length];
                }
                for (int i = 0; i < values.length; i++) {
                    matrix[rowCount][i] = Integer.parseInt(values[i]);
                }
                rowCount++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    public static void main(String[] args) {
        int[][] A = readMatrixFromFile("MatrxiA.txt");
        int[][] B = readMatrixFromFile("MatrixB.txt");

        long startTime, endTime;
        int[][] result;

        // Perform single-threaded matrix multiplication and measure time
        startTime = System.nanoTime();
        result = matrixMultiplicationSingleThread(A, B);
        endTime = System.nanoTime();
        long singleThreadTime = endTime - startTime;

        // Perform multi-threaded matrix multiplication (one thread per row) and measure time
        startTime = System.nanoTime();
        result = matrixMultiplicationMultiThreadRow(A, B);
        endTime = System.nanoTime();
        long multiThreadRowTime = endTime - startTime;

        // Perform multi-threaded matrix multiplication (one thread per cell) and measure time
        startTime = System.nanoTime();
        result = matrixMultiplicationMultiThreadCell(A, B);
        endTime = System.nanoTime();
        long multiThreadCellTime = endTime - startTime;

        System.out.println("Result of single-threaded multiplication:");
        printMatrix(result);

        System.out.println("Result of multi-threaded multiplication (one thread per row):");
        printMatrix(result);

        System.out.println("Result of multi-threaded multiplication (one thread per cell):");
        printMatrix(result);

        System.out.println("Single-threaded time: " + singleThreadTime + " ns");
        System.out.println("Multi-threaded (one thread per row) time: " + multiThreadRowTime + " ns");
        System.out.println("Multi-threaded (one thread per cell) time: " + multiThreadCellTime + " ns");
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
