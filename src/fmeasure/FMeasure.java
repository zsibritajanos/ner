package fmeasure;

import java.util.*;

public class FMeasure {

  private Map<String, Map<CLASS_TYPE, Integer>> statistics = null;
  private static final String AGGREGATED_LABEL = "AGG";

  private static final boolean IS_VERBOSE = false;

  public enum CLASS_TYPE {
    TP, FP, FN
  }

  public FMeasure() {
    // init
    this.statistics = new TreeMap<>();
  }

  public void increment(String label, CLASS_TYPE classType) {

    initLabel(label);

    // increment class
    this.getStatistics().get(label)
            .put(classType, this.getStatistics().get(label).get(classType) + 1);
  }

  public void initLabel(String label) {
    // add label
    if (!this.getStatistics().containsKey(label)) {
      this.getStatistics().put(label, new TreeMap<>());
    }

    for (CLASS_TYPE classType : CLASS_TYPE.values()) {
      if (!this.getStatistics().get(label).containsKey(classType)) {
        this.getStatistics().get(label).put(classType, 0);
      }
    }
  }

  public static double getPrecision(Map<CLASS_TYPE, Integer> values) {

    try {
      return (double) values.get(CLASS_TYPE.TP)
              / (values.get(CLASS_TYPE.TP) + values.get(CLASS_TYPE.FP));
    } catch (NullPointerException e) {
      return 0.0;
    }
  }

  public static double getRecall(Map<CLASS_TYPE, Integer> values) {
    try {
      return (double) values.get(CLASS_TYPE.TP)
              / (values.get(CLASS_TYPE.TP) + values.get(CLASS_TYPE.FN));
    } catch (NullPointerException e) {
      return 0.0;
    }
  }

  public static double getF(Map<CLASS_TYPE, Integer> values) {
    return 2 * ((getPrecision(values) * getRecall(values)) / ((getPrecision(values) + getRecall(values))));
  }

  /**
   * @param tokens
   * @param goldStandardPhrases
   * @param predicatedPhrases
   * @return
   */
  private static List<String> getFalseNegativePhrases(String[] tokens, Set<Phrase> goldStandardPhrases, Set<Phrase> predicatedPhrases) {
    List<String> phrases = new LinkedList<>();

    for (Phrase goldStandardPhrase : goldStandardPhrases) {
      if (!predicatedPhrases.contains(goldStandardPhrase)) {
        if (IS_VERBOSE) {
          System.out.println("FN " + goldStandardPhrase);
        }
        phrases.add(Arrays.toString(Arrays.copyOfRange(tokens, goldStandardPhrase.begin, goldStandardPhrase.end + 1)));
      }
    }
    return phrases;
  }

  /**
   * @param tokens
   * @param goldStandardPhrases
   * @param predicatedPhrases
   */
  private static List<String> getFalsePositivePhrases(String[] tokens, Set<Phrase> goldStandardPhrases, Set<Phrase> predicatedPhrases) {

    List<String> phrases = new LinkedList<>();

    for (Phrase predicatedPhrase : predicatedPhrases) {
      if (!goldStandardPhrases.contains(predicatedPhrase)) {
        if (IS_VERBOSE) {
          System.out.println("FP " + predicatedPhrase);
        }
        phrases.add(Arrays.toString(Arrays.copyOfRange(tokens, predicatedPhrase.begin, predicatedPhrase.end + 1)));
      }
    }

    return phrases;
  }

  private static boolean isPhraseStart(String previousLabel, String currentLabel) {

    // p null
    // c (I|B)-PER
    if (previousLabel == null) {
      // p null
      // c O
      return !currentLabel.equals("O");
    }

    // B-PER
    // B-PER
    if (previousLabel.equals(currentLabel) && previousLabel.startsWith("B-")) {
      return true;
    }

    // p B-PER
    // v I-PER
    if (previousLabel.replace("B-", "I-").equals(currentLabel)) {
      return false;
    }

    // O|B-PER|I-LOC
    // I-PER

    // the current label is not "O" and different from the previous label
    if (!currentLabel.equals("O") && !currentLabel.equals(previousLabel)) {
      return true;
    }

    return false;
  }

  private static boolean isPhraseEnd(String currentLabel, String nextLabel) {

    // the first label is not "O"
    if (nextLabel == null && !currentLabel.equals("O")) {
      return true;
    }

    // B-PER
    // I-PER
    if (currentLabel.replace("B-", "I-").equals(nextLabel)) {
      return false;
    }

    // the current label is not "O" and different from the previous label
    if (!currentLabel.equals("O") && !nextLabel.equals(currentLabel)) {
      return true;
    }

    return false;
  }


  /**
   * @param labels
   * @return
   */
  public static Set<Phrase> getPhrases(String[] labels) {

    Set<Phrase> phrases = new TreeSet<>();

    String prevLabel;
    String currentLabel;
    String nextLabel;

    int begingIndex = -1;
    int endIndex = -1;

    for (int i = 0; i < labels.length; ++i) {
      if (i < 1) {
        prevLabel = null;
      } else {
        prevLabel = labels[i - 1];
      }

      currentLabel = labels[i];

      if (i >= labels.length - 1) {
        nextLabel = null;
      } else {
        nextLabel = labels[i + 1];
      }

      if (isPhraseStart(prevLabel, currentLabel)) {
        begingIndex = i;
      }

      if (isPhraseEnd(currentLabel, nextLabel)) {
        endIndex = i;
        if (begingIndex > -1 && endIndex > -1 && !currentLabel.equals(null)) {
          phrases.add(new Phrase(begingIndex, endIndex, currentLabel.replace(
                  "I-", "").replace("B-", "")));
          begingIndex = -1;
          endIndex = -1;
          currentLabel = null;
        } else {
          // TODO:
        }
      }
    }

    return phrases;
  }


  /**
   * @param tokens
   * @param goldStandardLabels
   * @param predicatedLabels
   */
  public void addSentence(String[] tokens, String[] goldStandardLabels, String[] predicatedLabels) {

    Set<Phrase> goldStandardPhrases = getPhrases(goldStandardLabels);
    Set<Phrase> predicatedPhrases = getPhrases(predicatedLabels);

    List<String> fnp = getFalseNegativePhrases(tokens, goldStandardPhrases, predicatedPhrases);

    // FN log
    if (IS_VERBOSE) {
      if (fnp.size() > 0) {
        for (String phrase : fnp) {
          System.out.println("FN: " + phrase);
        }
      }
    }

    // FP log
    List<String> fpp = getFalsePositivePhrases(tokens, goldStandardPhrases, predicatedPhrases);
    if (IS_VERBOSE) {
      if (fpp.size() > 0) {
        for (String phrase : fpp) {
          System.out.println("FP: " + phrase);
        }
      }
    }

    for (Phrase goldStandardPhrase : goldStandardPhrases) {
      if (predicatedPhrases.contains(goldStandardPhrase)) {
        // TP
        increment(goldStandardPhrase.label, CLASS_TYPE.TP);
        increment(AGGREGATED_LABEL, CLASS_TYPE.TP);
      } else {
        // FN
        increment(goldStandardPhrase.label, CLASS_TYPE.FN);
        increment(AGGREGATED_LABEL, CLASS_TYPE.FN);
      }
    }
    for (Phrase predicatedPhrase : predicatedPhrases) {
      if (!goldStandardPhrases.contains(predicatedPhrase)) {
        // FP
        increment(predicatedPhrase.label, CLASS_TYPE.FP);
        increment(AGGREGATED_LABEL, CLASS_TYPE.FP);
      }
    }
  }

  public String entryToString(Map.Entry<String, Map<CLASS_TYPE, Integer>> entry) {
    StringBuffer stringBuffer = new StringBuffer();

    stringBuffer.append("P(");
    stringBuffer.append(entry.getKey());
    stringBuffer.append(") (");
    stringBuffer.append(entry.getValue().get(CLASS_TYPE.TP));
    stringBuffer.append(") ");
    stringBuffer.append(String.format("%.4f",
            (double) (getPrecision(entry.getValue()) * 100)));

    stringBuffer.append('\t');

    stringBuffer.append("R(");
    stringBuffer.append(entry.getKey());
    stringBuffer.append(") (");

    int tp = 0;
    int fn = 0;
    if (entry.getValue().containsKey(CLASS_TYPE.TP)) {
      tp = entry.getValue().get(CLASS_TYPE.TP);
    }
    if (entry.getValue().containsKey(CLASS_TYPE.FN)) {
      fn = entry.getValue().get(CLASS_TYPE.FN);
    }

    stringBuffer.append(tp + fn);

    stringBuffer.append(") ");
    stringBuffer.append(String.format("%.4f",
            (double) (getRecall(entry.getValue()) * 100)));

    stringBuffer.append('\t');

    stringBuffer.append("F(");
    stringBuffer.append(entry.getKey());
    stringBuffer.append(") ");
    stringBuffer.append(String.format("%.4f",
            (double) (getF(entry.getValue()) * 100)));

    stringBuffer.append('\n');

    return stringBuffer.toString();
  }

  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();

    for (Map.Entry<String, Map<CLASS_TYPE, Integer>> entry : this
            .getStatistics().entrySet()) {
      if (!entry.getKey().equals(AGGREGATED_LABEL))
        stringBuffer.append(this.getStatistics().get(entry.getKey()) + "\t"
                + entryToString(entry));
    }

    stringBuffer
            .append("---------------------------------------------------------------\n");

    for (Map.Entry<String, Map<CLASS_TYPE, Integer>> entry : this
            .getStatistics().entrySet()) {
      if (entry.getKey().equals(AGGREGATED_LABEL))
        stringBuffer.append(entryToString(entry));
    }

    return stringBuffer.toString();
  }

  public double getAggregatedF() {

    String[] lines = this.toString().trim().split("\n");
    String[] tokens = lines[lines.length - 1].split(" ");

    String f = tokens[tokens.length - 1];

    if (f.equalsIgnoreCase("nan")) {
      return 0;
    } else {
      return Double.parseDouble(f.replace(",", "."));
    }
  }

  public Map<String, Map<CLASS_TYPE, Integer>> getStatistics() {
    return statistics;
  }

  public static void main(String[] args) {
    FMeasure fMeasure = new FMeasure();
    fMeasure.addSentence(new String[]{"A", "B", "C", "D"}, new String[]{"I-TITLE", "O", "O", "I-TITLE"}, new String[]{"I-TITLE", "O", "I-TITLE", "O"});
    System.out.println(fMeasure);
  }
}
