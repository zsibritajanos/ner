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

public class WordNGram extends Feature {

  private static final long serialVersionUID = 1L;
  private int n = 1;
  private boolean useBagOfWords = true;

  private static final String BEEGIN_PREFIX = "BEGIN";
  private static final String END_PREFIX = "END";
  private static final String INNER_PREFIX = "INNER";

  public WordNGram(String name, boolean isCaseSensitive, boolean isCompact, int windowSize, boolean addFalseFeatureValues, int n, boolean useBagOfWords) {
    super(name, isCaseSensitive, isCompact, windowSize, addFalseFeatureValues);
    this.n = n;
    this.useBagOfWords = useBagOfWords;
  }

  public static String getBefore(String[] sentence, int index, int n) {
    StringBuffer nGram = new StringBuffer();
    if (n < 2 || index - n + 1 < 0) {
      return null;
    }
    for (int i = index - n + 1; i <= index; ++i) {
      nGram.append(Settings.FEATURE_VALUE_TOKEN_SEPARATOR + sentence[i]);
    }

    return nGram.toString();
  }

  public static String getAfter(String[] sentence, int index, int n) {
    StringBuffer nGram = new StringBuffer();
    if (n < 2 || index + n > sentence.length) {
      return null;
    }
    for (int i = index; i <= index + n - 1; ++i) {
      nGram.append(Settings.FEATURE_VALUE_TOKEN_SEPARATOR + sentence[i]);
    }

    return nGram.toString();
  }


  public static Set<String> getInners(String[] sentence, int index, int n) {

    Set<String> nGrams = new TreeSet<>();
    for (int begin = index + 1 - n + 1; begin < index; ++begin) {
      if (begin < 0) {
        continue;
      }

      int end = begin + n - 1;
      if (end > sentence.length - 1) {
        continue;
      }
      StringBuffer nGram = new StringBuffer();
      for (int i = begin; i <= end; ++i) {
        nGram.append(Settings.FEATURE_VALUE_TOKEN_SEPARATOR + sentence[i]);
      }
      nGrams.add(nGram.toString());
    }

    return nGrams;
  }


  @Override
  public List<Set<String>> process(String[] sentence) {
    List<Set<String>> features = new LinkedList<>();

    for (int i = 0; i < sentence.length; ++i) {
      Set<String> values = new TreeSet<>();

      for (int j = 2; j <= this.n; ++j) {
        /**
         * before
         */
        String before = getBefore(sentence, i, j);
        if (before != null) {
          if (!this.isCaseSensitive) {
            before = before.toLowerCase();
          }

          values.add(this.getClass().getSimpleName() + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.name
                  + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.END_PREFIX + before);
        }


        /**
         * after
         */
        String after = getAfter(sentence, i, j);
        if (after != null) {
          if (!this.isCaseSensitive) {
            after = after.toLowerCase();
          }
          values.add(this.getClass().getSimpleName() + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.name
                  + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.BEEGIN_PREFIX + after);
        }

        /**
         * inner
         */
        for (String inner : getInners(sentence, i, j)) {
          if (!this.isCaseSensitive) {
            inner = inner.toLowerCase();
          }
          if (after != null) {
            values.add(this.getClass().getSimpleName() + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.name
                    + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + this.INNER_PREFIX + inner);
          }
        }
      }

      features.add(values);
    }
    return features;
  }
}
