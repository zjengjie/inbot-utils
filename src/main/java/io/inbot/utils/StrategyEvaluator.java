package io.inbot.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class StrategyEvaluator {

    /**
     * Evaluate supplier stategies that produce Optionals until one returns something. This implements the strategy
     * pattern with a simple varargs of suppliers that produce an Optional T. You can use this to use several
     * strategies to calculate a T.
     *
     * @param suppliers
     *            the strategies that produce Optionals of the desired output type.
     * @return the optional provided by the first successful supplier or Optional.empty()
     */
    @SafeVarargs
    public static <T> Optional<T> evaluate(Supplier<Optional<T>>...suppliers) {
        for(Supplier<Optional<T>>s: suppliers) {
            Optional<T> maybe = s.get();
            if(maybe.isPresent()) {
                return maybe;
            }
        }
        return Optional.empty();
    }
}
