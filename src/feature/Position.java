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
 *         Simple feature that analyzes the the positions of the tokens of the
 *         sentence. The featere value will be the the position of the current
 *         token in the given sentence.
 */
public class Position extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;
  private int distribution = 0;

  /**
   * Constructor with default 'false' isCaseSensitive, 'true' isCompact and
   * false 'addFalseFeatureValues' value.
   */
  public Position(String name, int windowSize, int distribution) {
    super(name, false, false, windowSize, false);

    this.distribution = distribution;

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  private int getDistributedValue(int sentenceLength, int index) {
    int distributedValue = index;

    if (this.distribution > 0) {
      distributedValue = (int) Math
              .round((((double) index / sentenceLength) * (this.distribution - 1)));
    }
    return distributedValue;
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      Set<String> values = new TreeSet<>();
      values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + getDistributedValue(sentence.length, i));
      features.add(values);
    }

    return features;
  }

  /**
   * @param params
   */
  public Position(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public Position(String[] params) {
    this(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    // case sens
    // cant be compact
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    // no false
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.distribution);
    return stringBuffer.toString();
  }
}
