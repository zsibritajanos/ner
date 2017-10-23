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
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         This class describes a feature, that describes the method of the
 *         information extraction from the tokens of the sentence.
 */
public abstract class Feature implements Serializable, Comparable<Feature> {

  public int windowSize = 0;

  private static final long serialVersionUID = 1L;

  protected String name = null;
  protected boolean isCaseSensitive = false;
  protected boolean isCompact = false;
  protected boolean addFalseFeatureValues = false;

  protected String featureValue = null;
  protected String falseFeatureValue = null;

  /**
   * Processes the sentence, which represented by an array of string. Each
   * string in the is a word (token).
   */
  public abstract List<Set<String>> process(String[] sentence);

  /**
   * Constructs the feature with the necessary values.
   *
   * @param name                  the name of the feature, witch will be the part of the feature
   *                              value
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param addFalseFeatureValues
   */
  public Feature(String name, boolean isCaseSensitive, boolean isCompact,
                 int windowSize, boolean addFalseFeatureValues) {
    this.name = name;
    this.isCaseSensitive = isCaseSensitive;
    this.isCompact = isCompact;
    this.windowSize = windowSize;
    this.addFalseFeatureValues = addFalseFeatureValues;

    this.featureValue = this.getClass().getSimpleName() + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.name;
    this.falseFeatureValue = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + Settings.FALSE_SUFFIX;
  }

  public int compareTo(Feature feature) {
    return this.toString().compareTo(feature.toString());
  }

  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.name);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.isCaseSensitive);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.isCompact);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.windowSize);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.addFalseFeatureValues);
    return stringBuffer.toString();
  }
}
