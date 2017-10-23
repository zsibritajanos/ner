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
import java.util.List;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Class that extract features from a String sequence via the given
 *         description of a feature set.
 */
public class FeatureExtractor implements Serializable {

  private static final long serialVersionUID = 1L;

  private SentenceFeatureExtractor featureExtractor = null;

  public FeatureExtractor(String file) {

    List<Feature> features = FeatureDescriptorReader.read(file);

    this.setFeatureExtractor(new SentenceFeatureExtractor(features, false));
  }

  public FeatureExtractor(List<Feature> sentenceFeature) {
    this.setFeatureExtractor(new SentenceFeatureExtractor(sentenceFeature,
            false));
  }

  public List<List<String>> extractFeatures(String[] sentence) {

    List<List<String>> features = this.getFeatureExtractor().extractFeatures(sentence);

    return features;
  }

  public void setFeatureExtractor(SentenceFeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }

  public SentenceFeatureExtractor getFeatureExtractor() {
    return featureExtractor;
  }

}
