package me.choicore.common.distributedlock.aspect;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DistributedLockNameGenerator {

    private static final String DASH = "-";

    public static String generate(
            final String prefix,
            final String key,
            final String[] parameterNames,
            final Object[] args
    ) {
        return lockNameBuilder(prefix, SpringExpressionLanguageParser.parse(key, parameterNames, args));
    }

    private static String lockNameBuilder(final String prefix, final Object parse) {
        return prefix + DASH + parse;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class SpringExpressionLanguageParser {
        private static final ExpressionParser expressionParser = new SpelExpressionParser();

        public static Object parse(String key, String[] parameterNames, Object[] args) {
            if (parameterNames == null || parameterNames.length == 0) {
                throw new IllegalArgumentException("parameterNames is null or empty");
            }

            StandardEvaluationContext context = new StandardEvaluationContext();

            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            return expressionParser.parseExpression(key).getValue(context, Object.class);
        }
    }
}