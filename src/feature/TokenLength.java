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
 *         Simple feature that analyzes the length of the given token.
 */
public class TokenLength extends Feature {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor with default 'false' isCaseSensitive, 'false' isCompact value
   * and false addFalseFeatureValues values.
   */
  public TokenLength(String name, int windowSize) {
    super(name, false, false, windowSize, false);

    if (Settings.IS_VERBOSE) {
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new ArrayList<>();

    for (String token : sentence) {
      Set<String> values = new TreeSet<>();
      values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + getTokenLength(token));
      features.add(values);
    }

    return features;
  }

  /**
   * @param token
   * @return
   */
  private static String getTokenLength(String token) {

    String length = null;

    // 1
    if (token.length() == 1) {
      length = "1";
    }

    // 2
    else if (token.length() == 2) {
      length = "2";
    }

    // 3
    else if (token.length() == 3) {
      length = "3";
    }

    // 4 - 6
    else if (token.length() > 3 && token.length() < 7) {
      length = "4" + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "6";
    }

    // 7 - 10
    else if (token.length() > 6 && token.length() < 11) {
      length = "7" + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + "10";
    }

    // 10+
    else if (token.length() > 10) {
      length = "10+";
    }

    return length;
  }

  /**
   * @param params
   */
  public TokenLength(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public TokenLength(String[] params) {
    this(params[0], Integer.parseInt(params[1]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    return stringBuffer.toString();
  }
}