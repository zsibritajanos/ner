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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Simple feature that analyzes the number of the words left is the
 *         sentence from the current token position.
 */
public class WordsLeft extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor with default 'false' isCaseSensitive, 'false' isCompact and
   * 'false' addFalseFeatureValues values.
   */
  public WordsLeft(String name, int windowSize) {
    super(name, false, false, windowSize, false);
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      Set<String> values = new TreeSet<>();
      values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + (sentence.length - i - 1));
      features.add(values);
    }

    return features;
  }

  /**
   * @param params
   */
  public WordsLeft(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public WordsLeft(String[] params) {
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