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
import util.HungarianGuesser;

import java.util.*;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Feature that tries to suit tokens and phrases from the specified
 *         lexicon to the tokens of the given sentence. If it suits, than the
 *         value of the feature will be the name of the feature. If the feature
 *         isn't compact, than the feature value will contain the content of the
 *         lexicon element.
 */
public class Lexicon extends Feature {

  private static final long serialVersionUID = 1L;

  private String file;
  private String encoding;
  private boolean canUseTokenGuesser;
  private boolean canUsePhraseGuesser;

  private Map<String, List<List<String>>> lexicon;
  private HungarianGuesser hungarianGuesser;

  private Map<String, Integer> suitLog;

  /**
   * @param name
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param isAddFalseFeatureValues
   * @param file
   * @param encoding
   * @param canUseGuesser
   */
  public Lexicon(String name, boolean isCaseSensitive, boolean isCompact,
                 int windowSize, boolean isAddFalseFeatureValues, String file,
                 String encoding, boolean canUseGuesser) {
    this(name, isCaseSensitive, isCompact,
            windowSize, isAddFalseFeatureValues, file,
            encoding, canUseGuesser, false);
  }

  /**
   * @param name
   * @param isCaseSensitive
   * @param isCompact
   * @param windowSize
   * @param isAddFalseFeatureValues
   * @param file
   * @param encoding
   * @param canUseTokenGuesser
   * @param canUsePhraseGuesser
   */
  public Lexicon(String name, boolean isCaseSensitive, boolean isCompact,
                 int windowSize, boolean isAddFalseFeatureValues, String file,
                 String encoding, boolean canUseTokenGuesser, boolean canUsePhraseGuesser) {

    super(name, isCaseSensitive, isCompact, windowSize, isAddFalseFeatureValues);

    this.file = file;
    this.encoding = encoding;
    this.canUseTokenGuesser = canUseTokenGuesser;
    this.canUsePhraseGuesser = canUsePhraseGuesser;

    this.lexicon = readLexicon(this.file, this.encoding, !this.isCaseSensitive, true);
    this.hungarianGuesser = new HungarianGuesser("./data/suffix/hu.suffix");

    suitLog = new TreeMap<>();

    if (Settings.IS_VERBOSE){
      System.out.println(this);
    }
  }

  /**
   * @param tokens
   * @param phrase
   * @param isCaseSensitive
   * @param canUseGuesser
   * @return
   */
  private boolean suitPhrase(List<String> tokens, List<String> phrase, boolean isCaseSensitive, boolean canUseGuesser) {

    if (phrase.size() > tokens.size()) {
      return false;
    }

    // except last token
    for (int i = 0; i < phrase.size() - 1; i++) {
      if ((isCaseSensitive && !phrase.get(i).equals(tokens.get(i)))
              || (!isCaseSensitive && !phrase.get(i).equalsIgnoreCase(tokens.get(i)))) {
        return false;
      }
    }

    // last token from subsentence
    String lastToken = tokens.get(tokens.size() - 1);

    // last token from subsentence
    String lastPhraseToken = phrase.get(phrase.size() - 1);

    // last token
    if (suitToken(lastToken, lastPhraseToken, isCaseSensitive, canUseGuesser)) {
      return true;
    }
    return false;
  }


  /**
   * @param sentenceToken
   * @param lexiconToken
   * @param isCaseSensitive
   * @param canUseGuesser
   * @return
   */
  private boolean suitToken(String sentenceToken, String lexiconToken, boolean isCaseSensitive, boolean canUseGuesser) {
    if ((isCaseSensitive && sentenceToken.equals(lexiconToken))
            || (!isCaseSensitive && sentenceToken.equalsIgnoreCase(lexiconToken))) {
      return true;
    } else if (canUseGuesser) {
      for (String lemma : this.hungarianGuesser.getPossibleLemmas(sentenceToken)) {
        if (isCaseSensitive && lemma.equals(lexiconToken) || (!isCaseSensitive && lemma.equalsIgnoreCase(lexiconToken))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param file
   * @param encoding
   * @param canLowerize
   * @param canTrim
   * @return
   */
  public static Map<String, List<List<String>>> readLexicon(String file, String encoding, boolean canLowerize, boolean canTrim) {

    Map<String, List<List<String>>> lexicon = new HashMap<>();

    List<String> lines = reader.Reader.readLines(file, encoding);

    Set<String> uniqueLines = new LinkedHashSet<>(lines);

    for (String line : uniqueLines) {

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

      String[] tokens = temp.split(" ");

      // key in the map
      String key = tokens[0];

      // add key to map
      if (!lexicon.containsKey(key)) {
        lexicon.put(key, new LinkedList<>());
      }
      // build the phrase
      List<String> phrase = new LinkedList<>();
      for (int i = 1; i < tokens.length; ++i) {
        phrase.add(tokens[i]);
      }

      // add the phrase to the map
      lexicon.get(key).add(phrase);

    }

    return lexicon;
  }

  /**
   * @param features
   * @param begin
   * @param length
   * @param value
   */
  private void addFeatureValues(List<Set<String>> features, int begin, int length, String value, List<String> suitedPhrase) {

    String featureValue = this.isCompact ? value : value + Settings.FEATURE_VALUE_TOKEN_SEPARATOR + suitedPhrase;

    for (int i = begin; i < begin + length; ++i) {
      features.get(i).add(featureValue);
    }
  }

  /**
   *
   */
  private void addToLog(String[] sentence, List<Set<String>> features) {
    for (int i = 0; i < sentence.length; ++i) {
      if (features.get(i) != null && features.get(i).size() > 0) {

        if (!suitLog.containsKey(sentence[i])) {
          suitLog.put(sentence[i], 0);
        }
        suitLog.put(sentence[i], suitLog.get(sentence[i]) + 1);
      }
    }
  }

  private List<List<String>> getPossiblePhrases(Set<String> keys) {

    List<List<String>> possiblePhrases = new LinkedList<>();

    for (String key : keys) {
      possiblePhrases.addAll(getPossiblePhrases(key));
    }

    return possiblePhrases;
  }

  private List<List<String>> getPossiblePhrases(String key) {


    List<List<String>> possiblePhrases = new LinkedList<>();
    if (this.lexicon.containsKey(key)) {
      possiblePhrases.addAll(this.lexicon.get(key));
    }

    // add 'key' at 0 for full phrase
    List<List<String>> fullPhrases = new LinkedList<>();
    for (List<String> phrase : possiblePhrases) {
      List<String> fullPhrase = new LinkedList<>(phrase);
      fullPhrase.add(0, key);
      fullPhrases.add(fullPhrase);
    }

    return fullPhrases;
  }

  @Override
  public List<Set<String>> process(String[] sentence) {

    List<Set<String>> features = new ArrayList<>();

    for (int i = 0; i < sentence.length; ++i) {
      features.add(i, new TreeSet<>());
    }

    for (int i = 0; i < sentence.length; ++i) {

      int suitedLength = 0;
      List<String> suitedPhrase = new LinkedList<>();

      List<List<String>> lexiconPhrases = new LinkedList<>();

      /**
       * key
       */
      String key = sentence[i];
      if (!this.isCaseSensitive) {
        key = key.toLowerCase();
      }
      lexiconPhrases.addAll(getPossiblePhrases(key));


      Set<String> lemmas = null;
      if (this.canUseTokenGuesser) {
        lemmas = this.hungarianGuesser.getPossibleLemmas(key);
      }
      if (lemmas != null) {
        lemmas.remove(key);
        lexiconPhrases.addAll(getPossiblePhrases(lemmas));
      }

      if (lexiconPhrases.size() > 0) {
        for (List<String> lexiconPhrase : lexiconPhrases) {

          //real phrase
          if (sentence.length > (i + lexiconPhrase.size() - 1)) {

            String[] subSentence = Arrays.copyOfRange(sentence, i, i + lexiconPhrase.size());

            if (suitPhrase(Arrays.asList(subSentence), lexiconPhrase, this.isCaseSensitive, this.canUsePhraseGuesser)) {
              // longest suit
              if (lexiconPhrase.size() > suitedLength) {
                suitedLength = lexiconPhrase.size();
                suitedPhrase = lexiconPhrase;
              }
            }
          }
        }
      }

      if (suitedLength > 0) {
        addFeatureValues(features, i, suitedLength, this.featureValue, suitedPhrase);
      } else {
        if (this.addFalseFeatureValues) {
          features.get(i).add(this.falseFeatureValue);
        }
      }
    }
    addToLog(sentence, features);
    return features;
  }

  /**
   * @param params
   * @param separator
   */
  public Lexicon(String params, String separator) {
    this(params.split(separator));
  }

  /**
   * @param params
   */
  public Lexicon(String params) {
    this(params.split(Settings.PARAM_SEPARATOR));
  }

  /**
   * @param params
   */
  public Lexicon(String[] params) {
    this(params[0],
            Boolean.parseBoolean(params[1]),
            Boolean.parseBoolean(params[2]),
            Integer.parseInt(params[3]),
            Boolean.parseBoolean(params[4]),
            params[5],
            params[6],
            Boolean.parseBoolean(params[7]));
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.file);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.encoding);
    stringBuffer.append(Settings.PARAM_SEPARATOR + this.canUseTokenGuesser);
    return stringBuffer.toString();
  }
}
