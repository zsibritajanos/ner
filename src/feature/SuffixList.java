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
import reader.Reader;

import java.util.*;

/**
 * This feature looks for the suffix of the current token in a predefined list.
 * If the list contains the suffix of the current token, the value if the
 * feature will be the name of the feature. If the feature isn't compact, the
 * value will contain the suffix.
 *
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 */
public class SuffixList extends Feature {

  private static final long serialVersionUID = 1L;
  private String file;
  private String encoding;
  private Set<String> suffices;

  public SuffixList(String name, boolean isCaseSensitive, boolean isCompact,
                    int windowSize, boolean isAddFalseFeatureValues, String file,
                    String encoding) {

    super(name, isCaseSensitive, isCompact, windowSize, isAddFalseFeatureValues);

    this.file = file;
    this.encoding = encoding;

    this.suffices = readSuffices(this.file, this.encoding, !this.isCaseSensitive, true);

    if (Settings.IS_VERBOSE) {
      System.out.println(this);
    }
  }

  /**
   * @param file
   * @param encoding
   * @param canLowerize
   * @param canTrim
   * @return
   */
  public static Set<String> readSuffices(String file, String encoding, boolean canLowerize, boolean canTrim) {

    Set<String> suffices = new HashSet<>();
    List<String> lines = Reader.readLines(file, encoding);

    for (String line : lines) {

      String temp = line;
      if (canTrim) {
        temp = temp.trim();
      }

      if (temp.length() == 0) {
        continue;
      }

      if (canLowerize) {
        temp = temp.toLowerCase();
      }

      suffices.add(temp);
    }

    return suffices;
  }

  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new ArrayList<>();

    for (String token : sentence) {
      Set<String> values = new TreeSet<>();

      if (!this.isCaseSensitive) {
        token = token.toLowerCase();
      }

      // multiple suit
      for (String suffix : this.suffices) {
        if (token.endsWith(suffix)) {
          String value = this.featureValue;
          if (!this.isCompact) {
            value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR + suffix;
          }
          values.add(value);
        }
      }

      // negative feature values
      if (this.addFalseFeatureValues && values.isEmpty()) {
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
  public SuffixList(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public SuffixList(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public SuffixList(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Boolean.parseBoolean(params[4]), params[5], params[6]);
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.file);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.encoding);
    return stringBuffer.toString();
  }
}
