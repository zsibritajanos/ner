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
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Feature that looks for the current token in the specified map, witch
 *         contains string key and integer value pairs. If the current token is
 *         in the map (as key), the value of the feature will be the value of
 *         the found map item. If the feature isn't compact, than the feature
 *         value will contain the value of the map element.
 */
public class MapWord extends Feature {
  private static final long serialVersionUID = 1L;
  private String file;
  private String encoding;
  private String separator;
  private Normalization normalization;

  // map from file
  private Map<String, Double> map;
  // map with normalized values
  public Map<Double, Integer> normalized;
  // distribution
  private int distribution;

  public enum Normalization {
    NORMAL, HARMONIC, INACTIVE
  }

  public MapWord(String name, boolean isCaseSensitive, boolean isCompact,
                 int windowSize, boolean isAddNegativeFeatureValues, String file,
                 String encoding, String separator, Normalization normalization, int distribution) {

    super(name, isCaseSensitive, isCompact, windowSize,
            isAddNegativeFeatureValues);

    this.file = file;
    this.encoding = encoding;
    this.separator = separator;

    if (normalization == null) {
      this.normalization = Normalization.INACTIVE;
    } else {
      this.normalization = normalization;
    }
    this.distribution = distribution;

    init();

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }


  private void init() {
    // hash map
    map = Reader.readMap(this.file, this.encoding, this.separator);
    normalized = normalizeValues(map.values(), this.normalization, this.distribution);
  }


  /**
   * Sort and unique the given Double array.
   *
   * @param array
   * @return
   */
  private static double[] sortAndUnique(Double[] array) {
    // SORTED and UNIQUE values
    Set<Double> unique = new TreeSet<>(Arrays.asList(array));

    // as array of double values
    double[] sortedUniqueArray = new double[unique.size()];
    int index = 0;
    for (Double value : unique) {
      sortedUniqueArray[index] = value;
      ++index;
    }

    return sortedUniqueArray;
  }

  /**
   * Normalize the values.
   *
   * @param array
   * @param distribution
   * @return
   */
  private static Map<Double, Integer> normallyNormalizeValues(Double[] array, int distribution) {

    // sorted and unique array
    double[] sortedUniqueArray = sortAndUnique(array);

    // min value int the array
    double min = sortedUniqueArray[0];
    // max value int the array
    double max = sortedUniqueArray[sortedUniqueArray.length - 1];

    Map<Double, Integer> normalizedValues = new TreeMap<>();
    for (double i : sortedUniqueArray) {
      // scaled and distributed
      int normalizedValue = (int) (Math.round((i - min) / (max - min) * distribution));
      normalizedValues.put(i, normalizedValue);
    }

    return normalizedValues;
  }

  /**
   * Normalize harmonically the values.
   *
   * @param array
   * @param distribution
   * @return
   */
  private static Map<Double, Integer> harmonicallyNormalizeValues(Double[] array, int distribution) {
    // sorted and unique array
    double[] sortedUniqueArray = sortAndUnique(array);

    Map<Double, Integer> normalizedValues = new TreeMap<>();
    for (double i : sortedUniqueArray) {
      int normalizedValue = (int) Math.round((Arrays.binarySearch(sortedUniqueArray, i)) / (double) (sortedUniqueArray.length) * distribution);
      normalizedValues.put(i, normalizedValue);
    }

    return normalizedValues;
  }

  /**
   * Normalize harmonically the values.
   *
   * @param array
   * @return
   */
  private static Map<Double, Integer> inactiveNormalizeValues(Double[] array) {

    Map<Double, Integer> normalizedValues = new TreeMap<>();
    for (double i : array) {
      int normalizedValue = (int) Math.round(i);
      normalizedValues.put(i, normalizedValue);
    }

    return normalizedValues;
  }

  /**
   * Normalize harmonically the values.
   *
   * @param array
   * @param normalization
   * @param distribution
   * @return
   */
  private static Map<Double, Integer> normalizeValues(Double[] array, Normalization normalization, int distribution) {

    Map<Double, Integer> normalizedValues = null;
    switch (normalization) {
      case NORMAL:
        normalizedValues = normallyNormalizeValues(array, distribution);
        break;
      case HARMONIC:
        normalizedValues = harmonicallyNormalizeValues(array, distribution);
        break;
      case INACTIVE:
        normalizedValues = inactiveNormalizeValues(array);
    }
    return normalizedValues;
  }


  private static Map<Double, Integer> normalizeValues(Collection<Double> values, Normalization normalization, int distribution) {
    return normalizeValues(values.toArray(new Double[values.size()]), normalization, distribution);
  }


  @Override
  public List<Set<String>> process(String[] sentence) {

    // features of the sentence
    List<Set<String>> features = new ArrayList<>();

    // features of the token
    Set<String> values;

    for (String token : sentence) {

      // lowerize if case insensitive
      String tmpToken = this.isCaseSensitive ? token : token.toLowerCase();

      values = new TreeSet<>();

      // get the value from the map
      if (this.map.containsKey(tmpToken)) {
        String value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + normalized.get(map.get(tmpToken));
        values.add(value);
      } else {
        // false feature values
        if (this.addFalseFeatureValues && values.isEmpty()) {
          values.add(this.falseFeatureValue);
        }
      }
      features.add(values);
    }

    return features;
  }


  /**
   * @param params
   * @param separator
   */
  public MapWord(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public MapWord(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public MapWord(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Boolean.parseBoolean(params[2]),
            Integer.parseInt(params[3]),
            Boolean.parseBoolean(params[4]),
            params[5],
            params[6],
            // [9 for tabulator | 32 for space | etc]
            String.valueOf((char) Integer.parseInt(params[7])),
            Normalization.valueOf(params[8]),
            Integer.parseInt(params[9]));
  }

  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.file);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.encoding);
    stringBuffer.append(Settings.PARAM_SEPARATOR + (int) this.separator.charAt(0));
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.normalization);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.distribution);
    return stringBuffer.toString();
  }
}
