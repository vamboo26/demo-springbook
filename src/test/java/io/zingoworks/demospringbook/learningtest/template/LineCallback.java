package io.zingoworks.demospringbook.learningtest.template;

@FunctionalInterface
public interface LineCallback<T> {

    T doSomethingWithLine(String line, T value);
}
