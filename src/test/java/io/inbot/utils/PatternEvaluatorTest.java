package io.inbot.utils;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.testng.annotations.Test;

@Test
public class PatternEvaluatorTest {

    public void shouldEvalPatterns() {
        PatternEvaluator<Integer, String> evaluator = PatternEvaluator.evaluator(
            PatternEvaluator.equals(1,input->"one"),
            PatternEvaluator.equals(2,input->"two"),
            PatternEvaluator.equals(3,input->"three"),
            PatternEvaluator.matches(input -> input>=3,input->"big")
        );
        assertThat(evaluator.evaluate(2).get()).isEqualTo("two");
        assertThat(evaluator.evaluate(20).get()).isEqualTo("big");
        assertThat(evaluator.evaluate(-1).isPresent()).isFalse();
    }
}
