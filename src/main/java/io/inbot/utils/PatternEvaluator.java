package io.inbot.utils;

import java.util.Optional;
import java.util.function.Function;

/**
 * Simple Java lambda based implementation of Kotlin and Scala style pattern matching. This provides a nice alternative to the if else chaos that and a more
 * powerful alternative to switch statements. It's not perfect of course but it should allow for some limited verbosity when doing this kind of stuff.
 *
 * Inspired by https://kerflyn.wordpress.com/2012/05/09/towards-pattern-matching-in-java/.
 *
 * Because the evaluate returns an Optional, you can emulate the otherwise bit with a simple call to orElse(...).
 *
 * @param <MatchType>
 * @param <Output>
 */
public class PatternEvaluator<MatchType,Output> {

    private final Pattern<MatchType, Output>[] patterns;

    /**
     * Minimal interface for a pattern that takes an input and produces an output.
     * @param <I>
     * @param <O>
     */
    interface Pattern<I,O> {
        boolean matches(I input);
        Optional<O> apply(I input);
    }

    /**
     * Allows you to produce patterns that simply compare the input to the value and produce the output when they are equal.
     *
     * @param <I>
     * @param <O>
     */
    public static class EqualsPattern<I,O> implements Pattern<I,O> {
        private final I val;
        private final Function<I, O> function;

        public EqualsPattern(I val, Function<I,O> function) {
            this.val = val;
            this.function = function;
        }

        @Override
        public boolean matches(I input) {
            return val.equals(input);
        }

        @Override
        public Optional<O> apply(I input) {
            return Optional.of(function.apply(input));
        }
    }

    /**
     * If you want to match using a lambda function that produces a boolean this is what you use.
     *
     * @param <I>
     * @param <O>
     */
    public static class BoolExprPattern<I,O> implements Pattern<I,O> {
        private final Function<I, Boolean> evalFunction;
        private final Function<I, O> function;

        public BoolExprPattern(Function<I,Boolean> evalFunction, Function<I,O> function) {
            this.evalFunction = evalFunction;
            this.function = function;
        }

        @Override
        public boolean matches(I e) {
            return evalFunction.apply(e);
        }

        @Override
        public Optional<O> apply(I input) {
            return Optional.of(function.apply(input));
        }
    }

    @SafeVarargs
    public PatternEvaluator(Pattern<MatchType,Output>...ps) {
        this.patterns = ps;
    }

    public Optional<Output> evaluate(MatchType matchVal) {
        for(Pattern<MatchType, Output> p:patterns) {
            if(p.matches(matchVal)) {
                return p.apply(matchVal);
            }
        }
        return Optional.empty();
    }

    /**
     * Allows you use a lambda to match on an input.
     * @param expr
     * @param f
     * @return pattern that matches on the expression
     */
    public static <I,O> BoolExprPattern<I,O> matches(Function<I,Boolean> expr, Function<I,O> f) {
        return new BoolExprPattern<I, O>(expr, f);
    }

    public static <I,O> EqualsPattern<I,O> equals(I value, Function<I,O> f) {
        return new EqualsPattern<I, O>(value, f);
    }

    @SafeVarargs
    public static <I,O> PatternEvaluator<I,O> evaluator(Pattern<I,O>...patterns) {
        return new PatternEvaluator<>(patterns);
    }

    @SafeVarargs
    public static <I,O> Optional<O> evaluate(I input, Pattern<I,O>...patterns) {
        return new PatternEvaluator<>(patterns).evaluate(input);
    }
}
