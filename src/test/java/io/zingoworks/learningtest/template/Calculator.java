package io.zingoworks.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public String concatenate(String path) throws IOException {
        LineCallback<String> concatenateCallback = new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        };
        return lineReadTemplate(path, concatenateCallback, "");
    }

    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = initVal;
            String line;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                }
            }
        }
    }

    public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            int ret = callback.doSomethingWithReader(br);
            return ret;
        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                }
            }
        }
    }

    public int sum(String path) throws IOException {
        LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.parseInt(line);
            }
        };
        return lineReadTemplate(path, sumCallback, 0);
    }

    public Integer multiply(String path) throws IOException {
        LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.parseInt(line);
            }
        };
        return lineReadTemplate(path, multiplyCallback, 1);
    }

    public int sumLegacy(String path) throws IOException {
        BufferedReader br = null;

        try {
            new BufferedReader(new FileReader(path));
            Integer sum = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                sum += Integer.valueOf(line);
            }
            return sum;
        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                }
            }
        }
    }
}
