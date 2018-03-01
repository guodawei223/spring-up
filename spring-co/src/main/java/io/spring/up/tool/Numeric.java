package io.spring.up.tool;

import io.spring.up.tool.fn.Fn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Numeric {

    private static boolean isMatch(final String regex, final String original) {
        return Fn.getNull(false, () -> {
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(original);
            return matcher.matches();
        }, regex, original);
    }


    static boolean isPositive(final String original) {
        return isMatch("^\\+{0,1}[0-9]\\d*", original);
    }

    static boolean isNegative(final String original) {
        return isMatch("^-[0-9]\\d*", original);
    }

    static boolean isInteger(final String original) {
        return isMatch("[+-]{0,1}0", original) || isPositive(original) || isNegative(original);
    }

    static boolean isDecimal(final String original) {
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", original);
    }

    static boolean isReal(final String original) {
        return isInteger(original) || isDecimal(original);
    }

    static class Decimal {

        public static boolean isPositive(final String original) {
            return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", original);
        }

        public static boolean isNegative(final String original) {
            return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", original);
        }
    }
}
