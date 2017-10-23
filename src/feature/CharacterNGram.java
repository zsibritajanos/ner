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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Feature that finds all parts of the actual token with length between
 *         the specified interval. If the word part located at the beginning of
 *         the token it will be named like specifiedNGram-PREFIX, if the word
 *         part locdefated at the end of the token it will be named like
 *         specifiedNGram-SUFFIX and.
 */
public class CharacterNGram extends Feature {

  private static final long serialVersionUID = 1L;

  private static final String PREFIX_AFFIX = "PREFIX";
  private static final String SUFFIX_AFFIX = "SUFFIX";

  private int maxLength = 0;
  private int minLength = 0;

  /**
   * Constructor with default 'false' isCompact and 'false' addFalseFeatureValues value.
   */
  public CharacterNGram(String name, boolean isCaseSensitive, int windowSize,
                        int minLength, int maxLength) {
    super(name, isCaseSensitive, false, windowSize, false);

    this.minLength = minLength;
    this.maxLength = maxLength;

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  /**
   * Get the prefix of the given token with the specified length.
   *
   * @param token           raw String
   * @param length          the length of prefix
   * @param isCaseSensitive is the prefix case sensitive
   * @return prefix of the given token with the specified length, or null if it
   * is not possible
   */
  public static String getPrefix(String token, int length, boolean isCaseSensitive) {

    if (length < 1 || length > token.length()) {
      return "";
    }

    String prefix = token.substring(0, length);
    if (!isCaseSensitive) {
      prefix = prefix.toLowerCase();
    }

    return prefix;
  }

  /**
   * Extracts the character n-grams from the given token with the specified
   * length.
   *
   * @param token           the token
   * @param length          the length of the character grams
   * @param isCaseSensitive is the suffix case sensitive
   * @return Set of character n-grams from the given token, or null if it is not
   * possible
   */
  public static Set<String> getNGrams(String token, int length, boolean isCaseSensitive) {

    if (length < 1 || length > token.length()) {
      return new TreeSet<>();
    }

    Set<String> nGrams = new TreeSet<>();
    for (int i = 0; i <= token.length() - length; ++i) {
      String nGram = token.substring(i, i + length);
      if (!isCaseSensitive) {
        nGram = nGram.toLowerCase();
      }
      nGrams.add(nGram);
    }

    return nGrams;
  }

  /**
   * Get the suffix of the given token with the specified length.
   *
   * @param token           raw String
   * @param length          the length of suffix
   * @param isCaseSensitive is the suffix case sensitive
   * @return suffix of the given token with the specified length, or null if it
   * is not possible
   */
  public static String getSuffix(String token, int length,
                                 boolean isCaseSensitive) {

    if (length < 1 || length > token.length()) {
      return "";
    }

    String suffix = token.substring(token.length() - length);
    if (!isCaseSensitive) {
      suffix = suffix.toLowerCase();
    }

    return suffix;
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new LinkedList<>();

    for (String token : sentence) {
      Set<String> values = new TreeSet<>();

      // prefix
      for (int length = this.minLength; length <= this.maxLength; ++length) {
        if (token.length() >= length) {
          String value = getPrefix(token, length, this.isCaseSensitive);
          if (value != null) {
            values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + PREFIX_AFFIX + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + value);
          }
        }
      }

      // inside
      for (int length = this.minLength; length <= this.maxLength; ++length) {
        Set<String> nGrams = getNGrams(token, length, this.isCaseSensitive);
        if (nGrams != null) {
          for (String nGram : nGrams) {
            values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + nGram);
          }
        }
      }

      // suffix
      for (int length = this.minLength; length <= this.maxLength; ++length) {
        if (token.length() >= length) {
          String value = getSuffix(token, length, this.isCaseSensitive);
          if (value != null) {
            values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + SUFFIX_AFFIX + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + value);
          }
        }
      }

      features.add(values);
    }
    return features;
  }

  /**
   * @param params
   * @param separator
   */
  public CharacterNGram(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public CharacterNGram(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public CharacterNGram(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Integer.parseInt(params[2]),
            Integer.parseInt(params[3]),
            Integer.parseInt(params[4]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.isCaseSensitive);
    // cant be compact
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.minLength);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.maxLength);
    // cant add false
    return stringBuffer.toString();
  }
}