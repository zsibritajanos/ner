/**
 * Developed by Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 *
 * @see <a href="http://www.inf.u-szeged.hu/rgai/">Research Group on Artificial Intelligence of the Hungarian Academy of Sciences</a>
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * @see <a href="http://creativecommons.org/licenses/by-sa/3.0/legalcode">http://creativecommons.org/licenses/by-sa/3.0/legalcode</a>
 */
package feature;

import environmnet.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Feature that checks the case of the tokens in the sentence. If the
 *         token is lower cased, the token gets the 'lower' feature value, if
 *         the token is upper cased, it gets the 'upper' feature value, if the
 *         token is capitalized (only the first letter is upper cased), it gets
 *         the 'capitalized' feature.
 */

public class Case extends Feature {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with default 'true' isCaseSensitive, 'false' isCompact values.
     */
    public Case(String name, int windowSize, boolean addFalseFeatureValues) {
        super(name, true, false, windowSize, addFalseFeatureValues);

        if (Settings.IS_VERBOSE) {
            System.out.println(this);
        }
    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if all of the letters in the token are lower case
     */
    public static boolean isLowerCase(String token) {

        int lowerCounter = 0;
        for (char c : token.toCharArray()) {
            if (Character.isLowerCase(c)) {
                ++lowerCounter;
            }
        }

        if (lowerCounter == 0) {
            return false;
        }

        for (char character : token.toCharArray()) {
            if (!Character.isLowerCase(character) && !(character == '-')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if all of the letters in the token are upper case
     */
    public static boolean isCamelCase(String token) {
        char[] chars = token.toCharArray();

        // first letter is upcase
        if (!Character.isUpperCase(chars[0])) {
            return false;
        }

        for (int i = 1; i < chars.length; ++i) {
            if (Character.isUpperCase(chars[i]) && !Character.isLowerCase(chars[i - 1])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if all of the letters in the token are upper case
     */
    public static boolean isUpperCase(String token) {

        int upperCounter = 0;
        for (char c : token.toCharArray()) {
            if (Character.isUpperCase(c)) {
                ++upperCounter;
            }
        }

        if (upperCounter == 0) {
            return false;
        }


        for (char character : token.toCharArray()) {
            if (!Character.isUpperCase(character) && !(character == '-') && !(character == '.') && !(character == '&') && !(Character.isDigit(character))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if all of the letters in the token are upper case
     */
    public static boolean isHyphenCapitalized(String token) {
        String[] parts = token.split("-");
        if (parts.length < 2) {
            return false;
        }

        for (String part : parts) {
            if (!isCapizalized(part)) {
                return false;
            }
        }


        return true;
    }


    private static int getCaseIndex(String token) {
        char[] chars = token.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {

            if (Character.isUpperCase(chars[i]) && chars[i + 1] == '-') {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if all of the letters in the token are upper case
     */
    public static boolean isAbbrevCase(String token) {
        int caseIndex = getCaseIndex(token);
        if (caseIndex == -1) {
            return false;
        }

        String abbrevPart = token.substring(0, caseIndex);
        String suffixPart = token.substring(caseIndex);

        return isUpperCase(abbrevPart) && isLowerCase(suffixPart);

    }

    /**
     * Determines the case of the given token.
     *
     * @param token raw string
     * @return true if the first letter of the token is upper case and all of the
     * other letters are lower case
     */
    public static boolean isCapizalized(String token) {
        // first char is capitalized
        if (!Character.isUpperCase(token.charAt(0))) {
            return false;
        }

        // other cars
        for (int i = 1; i < token.toCharArray().length; ++i) {
            // if non letter but non hyphen
            if (!Character.isLetter(token.charAt(i)) && !(token.charAt(i) == '-') && !(token.charAt(i) == '/') && !(token.charAt(i) == '\'') && !(token.charAt(i) == '.') && !(Character.isDigit(token.charAt(i)))) {
                return false;
            }
            if (Character.isLetter(token.charAt(i))
                    && !Character.isLowerCase(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Set<String>> process(String[] sentence) {
        List<Set<String>> features = new ArrayList<>();

        String value = null;

        for (String token : sentence) {
            Set<String> values = new TreeSet<>();

            if (isLowerCase(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "lower";
            } else if (isUpperCase(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "upper";
            } else if (isCapizalized(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "capitalized";
            } else if (isAbbrevCase(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "abbrev";
            } else if (isHyphenCapitalized(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "hyphen-capitalized";
            } else if (isCamelCase(token)) {
                value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "camel";
            }

            if (value != null) {
                values.add(value);
                value = null;
            } else if (this.addFalseFeatureValues) {
                values.add(this.falseFeatureValue);
            }

            features.add(values);
        }

        return features;
    }

    /**
     * @param params
     */
    public Case(String params) {
        this(params.split(Settings.PARAM_SEPARATOR));
    }

    /**
     * @param params
     */
    public Case(String[] params) {
        this(params[0], Integer.parseInt(params[1]), Boolean.parseBoolean(params[2]));
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
        stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
        // always case sens
        stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
        stringBuffer.append(Settings.PARAM_SEPARATOR + this.addFalseFeatureValues);
        return stringBuffer.toString();
    }
}
