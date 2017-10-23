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
 *         Feature that checks the form of the given token.
 */
public class WordForm extends Feature {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor with default 'false' isCompact and 'false'
   * addFalseFeatureValues value.
   */
  public WordForm(String name, boolean isCaseSensitive, int windowSize) {
    super(name, isCaseSensitive, false, windowSize, false);

    if (Settings.IS_VERBOSE) {
      System.out.println(this);
    }

  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new ArrayList<>();

    for (String token : sentence) {
      Set<String> values = new TreeSet<>();
      token = token.trim();

      if (!this.isCaseSensitive) {
        token = token.toLowerCase().trim();
      }
      values.add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + token);
      features.add(values);
    }
    return features;
  }

  /**
   * @param params
   */
  public WordForm(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public WordForm(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Integer.parseInt(params[2]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.isCaseSensitive);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    return stringBuffer.toString();
  }
}
