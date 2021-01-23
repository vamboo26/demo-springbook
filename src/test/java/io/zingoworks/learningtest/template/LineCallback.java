package io.zingoworks.learningtest.template;

@FunctionalInterface
public interface LineCallback<T> {

    T doSomethingWithLine(String line, T value);
}
