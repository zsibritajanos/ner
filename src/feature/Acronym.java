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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Acronym extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  private static Pattern ACRONYM = Pattern
          .compile(".*[A-ZÁÉÍÓÖŐÜŰ]{2,}.*|[A-ZÁÉÍÓÖŐÜŰ]{2,}.*[0-9].*|[A-ZÁÉÍÓÖŐÜŰ]+[0-9]+");

  /**
   * @param name
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param addFalseFeatureValues
   */
  public Acronym(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean addFalseFeatureValues) {
    super(name, isCaseSensitive, isCompact, windowSize, addFalseFeatureValues);

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  protected static final List<String> EXCEPTIONS = Arrays.asList(new String[]{
          "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
          "NOT", "BUT", "YES", "NO",
          "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE",
          "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY",
          "MON", "TUE", "WED", "THU", "FRI", "SAT",
          "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER",
          "JAN", "FEB", "APR", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
          "IGAZ", "HAMIS",
          "JANUÁR", "FEBRUÁR", "MÁRCIUS", "ÁPRILIS", "MÁJUS", "JÚNIUS", "JÚLIUS", "AUGUSZTUS", "SZEPTEMBER", "OKTÓBER", "NOVEMBER", "DECEMBER"});

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    boolean isAllUpper = true;
    for (String word : sentence) {
      if (!word.toUpperCase().equals(word)) {
        isAllUpper = false;
        break;
      }
    }

    for (int i = 0; i < sentence.length; ++i) {
      features.add(new TreeSet<>());
    }

    for (int i = 0; i < sentence.length; ++i) {
      String acronym = getAcronym(sentence[i]);
      if (acronym != null && !isAllUpper) {
        if (this.isCompact) {
          features.get(i).add(this.featureValue);
        } else {
          features.get(i).add(this.featureValue + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + acronym);
        }
      }
    }

    if (this.addFalseFeatureValues) {
      for (int i = 0; i < sentence.length; ++i) {
        if (features.get(i).isEmpty()) {
          features.get(i).add(this.falseFeatureValue);
        }
      }
    }
    return features;
  }

  /**
   * @param token
   * @return
   */
  public static String getAcronym(String token) {
    if (token.length() > 1
            && !EXCEPTIONS.contains(token)) {
      Matcher matcher = ACRONYM.matcher(token);
      if (matcher.matches()) {
        return matcher.group(0);
      }
      return null;
    }
    return null;
  }

  /**
   * @param params
   */
  public Acronym(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public Acronym(String[] params) {
    this(params[0], Boolean.parseBoolean(params[1]), Boolean.parseBoolean(params[2]), Integer.parseInt(params[3]), Boolean.parseBoolean(params[4]));
  }
}