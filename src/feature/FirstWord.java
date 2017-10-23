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
 * Feature that indicates whatever the token is the first element of a sequence
 * (sentence).
 */
public class FirstWord extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  public FirstWord(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean addFalseFeatureValues) {
    super(name, isCaseSensitive, isCompact, windowSize, addFalseFeatureValues);

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      String token = sentence[i];
      if (!this.isCaseSensitive) {
        token = token.toLowerCase();
      }

      Set<String> values = new TreeSet<>();

      if (i == 0) {
        String value = this.featureValue;
        if (!this.isCompact) {
          value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR + token;
        }
        values.add(value);
      } else {
        if (this.addFalseFeatureValues) {
          values.add(this.falseFeatureValue);
        }
      }
      features.add(values);
    }

    return features;
  }

  /**
   * @param params
   */
  public FirstWord(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public FirstWord(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Boolean.parseBoolean(params[4]));
  }
}
