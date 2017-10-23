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
 *         Simple feature, that examines if the current token contains the
 *         specified String.
 */
public class Contains extends Feature {

  private static final long serialVersionUID = 1L;
  private String sequence;

  /**
   * Constructor with default 'true' isCompact value.
   */
  public Contains(String name, boolean isCaseSensitive, int windowSize, boolean addFalseFeatureValues, String sequence) {
    super(name, isCaseSensitive, true, windowSize, addFalseFeatureValues);

    this.sequence = sequence;

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new LinkedList<>();

    Set<String> values;
    boolean isContains;

    for (String token : sentence) {
      values = new TreeSet<>();

      if (this.isCaseSensitive) {
        isContains = token.contains(this.sequence);
      } else {
        isContains = token.toLowerCase().contains(this.sequence);
      }

      if (isContains) {
        values.add(this.featureValue);
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
  public Contains(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public Contains(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public Contains(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Integer.parseInt(params[2]),
            Boolean.parseBoolean(params[3]),
            params[4]);
  }


  @Override 
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.isCaseSensitive);
    // always compact
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.addFalseFeatureValues);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.sequence);
    return stringBuffer.toString();
  }
}
