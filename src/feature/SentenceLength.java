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

import java.util.*;

public class SentenceLength extends Feature {

  private static final long serialVersionUID = 1L;

  public SentenceLength(String name) {
    super(name, false, true, 0, false);
    if (Settings.IS_VERBOSE) {
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new LinkedList<>();

    Set<String> lengths = getSentenceLength(sentence);

    for (int i = 0; i < sentence.length; ++i) {

      Set<String> values = new TreeSet<>();

      for (String length : lengths) {
        values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + length);
      }

      features.add(values);
    }

    return features;
  }

  /**
   * @param sentence
   * @return
   */
  private static Set<String> getSentenceLength(String[] sentence) {

    Set<String> values = new TreeSet<>();

    // 1 - 3
    if (sentence.length > 0 && sentence.length < 4) {
      values.add("1" + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "3");
    }

    // 1 - 5
    if (sentence.length > 0 && sentence.length < 6) {
      values.add("1" + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "5");
    }

    // 3 - 8
    if (sentence.length > 2 && sentence.length < 9) {
      values.add("3" + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "8");
    }

    // 8+
    if (sentence.length > 8) {
      values.add("8+");
    }

    return values;
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    // no case sens
    // cant be compact
    // no window size
    // no false
    return stringBuffer.toString();
  }
}
