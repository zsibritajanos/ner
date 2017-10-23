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
import util.io.IOBReader;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 */
public class TrainInfo extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  // train file
  private String trainFile;
  private String encoding;
  private String separator;
  private int wordFormIndex;
  private int labelIndex;

  // train token min occ
  private int minTokenOccurrence;

  // labeled min freq
  private double minLabeledFrequency;

  // label map
  private Map<String, String> filteredLabelMap;

  private final String NEUTER_LABEL = "O";

  /**
   * @param name
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param addFalseFeatureValues
   * @param trainFile
   * @param encoding
   * @param separator
   * @param wordFormIndex
   * @param labelIndex
   * @param minTokenOccurrence
   * @param minLabeledFrequency
   */
  public TrainInfo(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean addFalseFeatureValues, String trainFile, String encoding, String separator, int wordFormIndex, int labelIndex, int minTokenOccurrence, double minLabeledFrequency) {

    super(name, isCaseSensitive, isCompact, windowSize, addFalseFeatureValues);

    this.trainFile = trainFile;
    this.encoding = encoding;
    this.separator = separator;
    this.wordFormIndex = wordFormIndex;
    this.labelIndex = labelIndex;

    this.minTokenOccurrence = minTokenOccurrence;
    this.minLabeledFrequency = minLabeledFrequency;

    // reads the train file
    List<List<String[]>> sentences = IOBReader.readIOB(this.trainFile,
            this.encoding, this.separator);

    this.filteredLabelMap = getFilteredLabelMap(sentences);

    if (Settings.IS_VERBOSE) {
      System.out.println(this);
    }
  }

  /**
   * @param map
   * @param key
   * @return
   */
  private static double getLabelFrequency(Map<String, Integer> map, String key) {
    int tot = 0;
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      tot += entry.getValue();
    }
    return map.get(key) / (double) tot;
  }

  /**
   * @param map
   * @return
   */
  private static String getMaxOccurrenceLabel(Map<String, Integer> map) {

    int maxOccur = 0;
    String maxLabel = null;

    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      if (entry.getValue() > maxOccur) {
        maxOccur = entry.getValue();
        maxLabel = entry.getKey();
      }
    }

    return maxLabel;
  }

  private static int getOccurrence(Map<String, Integer> map) {
    int occur = 0;

    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      occur += entry.getValue();
    }

    return occur;
  }

  /**
   * @param sentences
   * @return
   */
  private Map<String, Map<String, Integer>> getLabelMap(List<List<String[]>> sentences) {
    Map<String, Map<String, Integer>> labelMap = new HashMap<>();

    /**
     * collects the token labels
     */
    for (List<String[]> sentence : sentences) {
      for (String[] token : sentence) {

        String tempToken = token[this.wordFormIndex];
        if (!this.isCaseSensitive) {
          tempToken = tempToken.toLowerCase();
        }

        if (!labelMap.containsKey(tempToken)) {
          labelMap.put(tempToken, new TreeMap<>());
        }

        String label = token[labelIndex];
        if (!labelMap.get(tempToken).containsKey(label)) {
          labelMap.get(tempToken).put(label, 0);
        }

        labelMap.get(tempToken).put(label, labelMap.get(tempToken).get(label) + 1);
      }
    }

    return labelMap;
  }

  /**
   * @param sentences
   * @return
   */
  private Map<String, String> getFilteredLabelMap(List<List<String[]>> sentences) {

    /**
     * get label map
     */
    Map<String, Map<String, Integer>> labelMap = getLabelMap(sentences);

    /**
     *  filter
     */
    Map<String, String> filteredLabelMap = new TreeMap<>();
    for (Map.Entry<String, Map<String, Integer>> entry : labelMap.entrySet()) {
      // a={I-MISC=20, I-ORG=3, O=9765}
      if (getOccurrence(entry.getValue()) > this.minTokenOccurrence) {
        String maxOccurLabel = getMaxOccurrenceLabel(entry.getValue());
        if (!maxOccurLabel.equals(NEUTER_LABEL)) {
          if (getLabelFrequency(entry.getValue(), maxOccurLabel) > this.minLabeledFrequency) {
            filteredLabelMap.put(entry.getKey(), maxOccurLabel);
          }
        }
      }
    }

    return filteredLabelMap;
  }

  private Set<String> processLabelMap(String token) {

    Set<String> values = new TreeSet<>();

    // get the value from the map
    if (this.filteredLabelMap.containsKey(token)) {

      String value = this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + filteredLabelMap.get(token);

      if (!this.isCompact) {
        value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR;
        if (this.isCaseSensitive) {
          value += token;
        } else {
          value += token.toLowerCase();
        }
      }
      values.add(value);
    } else {
      // false feature values
      if (this.addFalseFeatureValues && values.isEmpty()) {
        values.add(this.falseFeatureValue);
      }
    }

    return values;
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new LinkedList<>();

    for (String token : sentence) {
      String tempToken = token;

      if (!this.isCaseSensitive) {
        tempToken = tempToken.toLowerCase();
      }

      Set<String> values = new TreeSet<>();

      values.addAll(processLabelMap(tempToken));

      features.add(values);
    }
    return features;
  }

  /**
   * @param params
   * @param separator
   */
  public TrainInfo(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public TrainInfo(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public TrainInfo(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Boolean.parseBoolean(params[2]),
            Integer.parseInt(params[3]),
            Boolean.parseBoolean(params[4]),
            params[5],
            params[6],
            // [9 for tabulator | 32 for space | etc]
            String.valueOf((char) Integer.parseInt(params[7])),
            Integer.parseInt(params[8]),
            Integer.parseInt(params[9]),
            Integer.parseInt(params[10]),
            Double.parseDouble(params[11]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.trainFile);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.encoding);
    stringBuffer.append(Settings.PARAM_SEPARATOR + (int) this.separator.charAt(0));
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.wordFormIndex);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.labelIndex);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.minTokenOccurrence);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.minLabeledFrequency);

    return stringBuffer.toString();
  }
}
