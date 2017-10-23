/**
 * Developed by Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 *
 * @see <a href="http://www.inf.u-szeged.hu/rgai/">Research Group on Artificial Intelligence of the Hungarian Academy of Sciences</a>
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * @see <a href="http://creativecommons.org/licenses/by-sa/3.0/legalcode">http://creativecommons.org/licenses/by-sa/3.0/legalcode</a>
 */
package extractor;

import feature.Feature;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Class that extract the features via the defined list of the features
 *         from the tokens.
 */
public class SentenceFeatureExtractor implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Feature> features = null;

  private boolean useBagOfWords = false;

  /**
   * Constructor to set the features and the bag-of-words flag.
   *
   * @param features      fetures extract to
   * @param useBagOfWords bag-of-words flag, if it 'false', the windowsize of the current
   *                      feature will be applied as a number from -windowsize to
   *                      +windowsize, is it 'true' only the BEFORE and AFTER flag will be
   *                      given instead of the exact postition
   */
  public SentenceFeatureExtractor(List<Feature> features, boolean useBagOfWords) {
    this.setFeatures(features);
    this.setUseBagOfWords(useBagOfWords);
  }

  /**
   * Apply the feature values via the given window size, from the current
   * feature vector.
   *
   * @param featureValues actual feature values
   * @param windowSize    size of the window
   * @return the copied feature values
   */
  private List<Set<String>> applyWindowSize(List<Set<String>> featureValues,
                                            int windowSize) {

    StringBuffer featureName;

    List<Set<String>> applied = new ArrayList<>();

    for (int i = 0; i < featureValues.size(); i++) {
      applied.add(i, new TreeSet<>());
    }

    for (int i = 0; i < featureValues.size(); i++) {
      for (String featureValue : featureValues.get(i)) {

        for (int j = -windowSize; j <= windowSize; j++) {
          if ((i + j > -1) && (i + j < featureValues.size())) {
            featureName = new StringBuffer(featureValue);

            // use bag of words
            if (this.isUseBagOfWords()) {
              if (j * (-1) < 0) {
                featureName.append("_BEFORE");
              }
              if (j * (-1) > 0) {
                featureName.append("_AFTER");
              }
            }
            // don't use bag of words, exact positions
            else {
              featureName.append("_").append(j * (-1));
            }
            applied.get(i + j).add(featureName.toString());
          }
        }
      }
    }

    return applied;
  }

  /**
   * Extracts all of the features from the tokens of the sentence. All token
   * will get a list of values of the features.
   *
   * @param sentence array of the tokens
   * @return list of the list of the features
   */
  public List<List<String>> extractFeatures(String[] sentence) {
    List<List<String>> features = new LinkedList<>();

    for (int i = 0; i < sentence.length; ++i) {
      features.add(i, new ArrayList<>());
    }

    List<Set<String>> featureValues;
    for (Feature feature : this.getFeatures()) {
      // feature values
      featureValues = feature.process(sentence);
      // apply window size
      featureValues = applyWindowSize(featureValues, feature.windowSize);

      for (int i = 0; i < featureValues.size(); ++i) {
        features.get(i).addAll(featureValues.get(i));
      }
    }
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public List<Feature> getFeatures() {
    return this.features;
  }

  public boolean isUseBagOfWords() {
    return useBagOfWords;
  }

  public void setUseBagOfWords(boolean useBagOfWords) {
    this.useBagOfWords = useBagOfWords;
  }
}
