package io.zingoworks.learningtest.template;

import java.io.BufferedReader;
import java.io.IOException;

@FunctionalInterface
public interface BufferedReaderCallback {
	
	Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
