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

import java.io.Serializable;
import java.util.*;

public class Bracket extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;
  private String[] brackets;

  /**
   * @param name
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param addFalseFeatureValues
   * @param brackets              array of the bracket pairs for ex.: new String[] { "()", "[]" }
   */
  public Bracket(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean addFalseFeatureValues, String[] brackets) {
    super(name, isCaseSensitive, isCompact, windowSize, addFalseFeatureValues);

    this.brackets = brackets;

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  /**
   * Get the indices of the specified character in the string array.
   *
   * @param sentence array of the tokens
   * @param c        the character search for
   * @return array of the indices of the c
   */
  public static Integer[] getIndexes(String[] sentence, char c) {
    List<Integer> indices = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      if (sentence[i].equals(String.valueOf(c))) {
        indices.add(i);
      }
    }

    return indices.toArray(new Integer[indices.size()]);
  }

  /**
   * @param sentence
   * @return
   */
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      features.add(new TreeSet<>());
    }

    for (String bracket : brackets) {
      Integer[] beginIndexes = getIndexes(sentence, bracket.charAt(0));
      Integer[] endIndexes = getIndexes(sentence, bracket.charAt(1));

      String value = this.featureValue;

      if (!this.isCompact) {
        value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR + bracket;
      }

      if (beginIndexes.length == endIndexes.length) {
        for (int i = 0; i < beginIndexes.length; ++i) {
          int begin = beginIndexes[i] + 1;
          int end = endIndexes[endIndexes.length - 1 - i];

          for (int j = begin; j < end; ++j) {
            features.get(j).add(value);
          }
        }
      }
    }
    if (this.addFalseFeatureValues) {
      for (int i = 0; i < sentence.length; ++i) {
        if (features.get(i).isEmpty()) {
          features.get(i).add(this.falseFeatureValue);
        }
      }
    }

    return features;
  }


  /**
   * @param brackets
   * @return
   */
  private static String[] getBrackets(String brackets) {
    List<String> pairs = new LinkedList<>();

    char[] bracketChars = brackets.toCharArray();

    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < bracketChars.length; ++i) {

      stringBuffer.append(bracketChars[i]);
      // 1 3 5
      if (i % 2 == 1) {
        pairs.add(stringBuffer.toString());
        stringBuffer = new StringBuffer();
      }
    }

    return pairs.toArray(new String[pairs.size()]);
  }

  /**
   * @param params
   */
  public Bracket(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public Bracket(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Boolean.parseBoolean(params[4]), getBrackets(params[5]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR);
    for (String bracket : this.brackets){
      stringBuffer.append(bracket);
    }
    return stringBuffer.toString();
  }
}