package io.zingoworks.learningtest.junit;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class JUnitTest {
	
	private static Set<JUnitTest> testObjects = new HashSet<>();
	
	@Test
	void test1() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
	}
	
	@Test
	void test2() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
	}
	
	@Test
	void test3() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
	}
	
	private void print() {
		for (JUnitTest testObject : testObjects) {
			System.out.println("testObject = " + testObject);
		}
	}
}
