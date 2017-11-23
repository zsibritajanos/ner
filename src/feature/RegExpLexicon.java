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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Feature that tries to fit the elements of a lexicon of regular
 *         expressions to the tokens of the given sentence.
 */
public class RegExpLexicon extends Feature {

  private static final long serialVersionUID = 1L;
  private String file = null;
  private String encoding = null;
  private Pattern[] patterns = null;

  private String prefix = null;
  private String postfix = null;

  public RegExpLexicon(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean isAddNegativeFeatureValues, String file, String encoding, String prefix, String postfix) {

    super(name, isCaseSensitive, isCompact, windowSize, isAddNegativeFeatureValues);

    this.file = file;
    this.encoding = encoding;
    if (prefix != null) {
      this.prefix = prefix;
    }

    if (postfix != null) {
      this.postfix = postfix;
    }

    this.patterns = getPatters(this.prefix, Reader.readSet(this.file, this.encoding, this.isCaseSensitive), this.postfix);

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      Set<String> values = new TreeSet<>();
      for (Pattern pattern : this.patterns) {
        Matcher matcher = pattern.matcher(sentence[i]);
        if (matcher.matches()) {
          String value = this.featureValue;

          if (!this.isCompact) {
            value += Settings.FEATURE_VALUE_TOKEN_SEPARATOR + pattern;
          }

          values.add(value);
        }
      }
      features.add(values);
    }

    for (int i = 0; i < features.size(); ++i) {
      // add negative values
      if (this.addFalseFeatureValues && features.get(i).isEmpty()) {
        features.get(i).add(this.falseFeatureValue);
      }
    }

    return features;
  }

  private Pattern[] getPatters(String prefix, Set<String> expressions, String postfix) {
    List<Pattern> patterns = new ArrayList<>();

    // iterate over the expressions and compile them
    for (String expression : expressions) {

      if (prefix != null) {
        expression = prefix + expression;
      }
      if (postfix != null) {
        expression = expression + postfix;
      }

      if (!this.isCaseSensitive) {
        patterns.add(Pattern.compile(expression, Pattern.CASE_INSENSITIVE));
      } else {
        patterns.add(Pattern.compile(expression));
      }
    }

    return patterns.toArray(new Pattern[patterns.size()]);
  }

  /**
   * @param params
   * @param separator
   */
  public RegExpLexicon(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public RegExpLexicon(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public RegExpLexicon(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Boolean.parseBoolean(params[2]),
            Integer.parseInt(params[3]),
            Boolean.parseBoolean(params[4]),
            params[5],
            params[6],
            params[7],
            params[8]);
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.file);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.encoding);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.prefix);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.postfix);
    return stringBuffer.toString();
  }
}
