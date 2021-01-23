package io.zingoworks.learningtest.template;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {
	
	@Test
	void sumOfNumbers() throws IOException {
		Calculator calculator = new Calculator();
		int sum = calculator.calcSum(getClass().getResource("/numbers.txt").getPath()); //todo "numbers.txt"는 실패, "/numbers.txt"는 성공
		
		assertThat(sum).isEqualTo(10);
	}
}
