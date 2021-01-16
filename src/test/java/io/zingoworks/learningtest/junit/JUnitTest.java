package io.zingoworks.learningtest.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/junit.xml")
public class JUnitTest {
	
	@Autowired
	private ApplicationContext context;
	
	private static Set<JUnitTest> testObjects = new HashSet<>();
	private static ApplicationContext contextObject = null;
	
	@Test
	void test1() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}
	
	@Test
	void test2() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}
	
	@Test
	void test3() {
		System.out.println("this = " + this);
		print();
		
		assertThat(this).isNotIn(testObjects);
		testObjects.add(this);
		
		assertThat( contextObject == null || contextObject == this.context).isTrue();
		contextObject = this.context;
	}
	
	private void print() {
		for (JUnitTest testObject : testObjects) {
			System.out.println("testObject = " + testObject);
		}
	}
}
