package io.zingoworks.learningtest.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {
	
	private Calculator calculator;
	private String numFilePath;
	
	@BeforeEach
	void setUp() {
		this.calculator = new Calculator();
		this.numFilePath = getClass().getResource("/numbers.txt").getPath();
	}
	
	@Test
	void sumOfNumbers() throws IOException {
		assertThat(this.calculator.sum(this.numFilePath)).isEqualTo(10);
	}
	
	@Test
	void multipleOfNumbers() throws IOException {
		assertThat(this.calculator.multiply(this.numFilePath)).isEqualTo(24);
	}

	@Test
	void concatenateOfNumbers() throws IOException {
		System.out.println("this.calculator.concatenate(numFilePath) = " + this.calculator.concatenate(numFilePath));
	}
}
