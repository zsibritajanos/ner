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
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Match the given regular expression to the tokens of the sentence.
 */
public class RegExp extends Feature {

  private static final long serialVersionUID = 1L;
  private String regExp = null;
  private Pattern pattern = null;

  /**
   * Constructor with default 'true' isCompact values.
   */
  public RegExp(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean isAddNegativeFeatureValues, String regExp) {
    super(name, isCaseSensitive, isCompact, windowSize, isAddNegativeFeatureValues);

    this.regExp = regExp;

    if (this.isCaseSensitive) {
      this.pattern = Pattern.compile(this.regExp);
    } else {
      this.pattern = Pattern.compile(this.regExp, Pattern.CASE_INSENSITIVE);
    }

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new ArrayList<>();

    for (String token : sentence) {
      Set<String> values = new TreeSet<>();

      if (this.pattern.matcher(token).matches()) {
        String value = this.featureValue;
        // add the discrete value
        if (!this.isCompact) {
          value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR + token;
        }
        values.add(value);

        // negative feature values
      } else if (this.addFalseFeatureValues) {
        values.add(this.falseFeatureValue);
      }

      features.add(values);
    }

    return features;
  }

  /**
   * @param params
   * @param separator
   */
  public RegExp(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public RegExp(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public RegExp(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Boolean.parseBoolean(params[4]), params[5]);
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.pattern);
    return stringBuffer.toString();
  }
}
