package io.zingoworks.demospringbook.learningtest.pointcut;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {

    @Test
    void methodSignaturePointcut() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
            "execution(public int io.zingoworks.demospringbook.learningtest.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

        assertThat(pointcut.getClassFilter().matches(Target.class) &&
            pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null))
            .isTrue();

    }
}
