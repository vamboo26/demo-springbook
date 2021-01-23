package io.zingoworks.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	
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
		BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
			@Override
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer sum = 0;
				String line = null;
				while ((line = br.readLine()) != null) {
					sum += Integer.valueOf(line);
				}
				return sum;
			}
		};
		
		return fileReadTemplate(path, sumCallback);
	}
	
	public Integer multiply(String path) throws IOException {
		BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
			@Override
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer multiply = 1;
				String line = null;
				while ((line = br.readLine()) != null) {
					multiply *= Integer.valueOf(line);
				}
				return multiply;
			}
		};
		
		return fileReadTemplate(path, multiplyCallback);
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
