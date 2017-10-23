package crf;

import cc.mallet.fst.CRF;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.Sequence;
import extractor.DefaultFeatureDescriptor;
import extractor.FeatureExtractor;

public class CRFTest {

  private DefaultFeatureDescriptor featureDescriptor = null;
  private FeatureExtractor featureExtractor = null;

  private CRF crf = null;
  private Alphabet labelAlphabet = null;
  private Alphabet featureAlphabet = null;

  public CRFTest(CRF crf, DefaultFeatureDescriptor featureDescriptor) {
    this.setCrf(crf);
    this.setFeatureDescriptor(featureDescriptor);

    this.setFeatureExtractor(new FeatureExtractor(featureDescriptor
            .getFeatures()));
  }

  public DefaultFeatureDescriptor getFeatureDescriptor() {
    return featureDescriptor;
  }

  public void setFeatureDescriptor(DefaultFeatureDescriptor featureDescriptor) {
    this.featureDescriptor = featureDescriptor;
  }

  public CRF getCrf() {
    return crf;
  }

  public void setCrf(CRF crf) {
    this.crf = crf;
  }

  public Alphabet getLabelAlphabet() {
    return labelAlphabet;
  }

  public void setLabelAlphabet(Alphabet labelAlphabet) {
    this.labelAlphabet = labelAlphabet;
  }

  public Alphabet getFeatureAlphabet() {
    return featureAlphabet;
  }

  public void setFeatureAlphabet(Alphabet featureAlphabet) {
    this.featureAlphabet = featureAlphabet;
  }

  public String[] predicate(String[] sentence) {

    FeatureVectorSequence featureVectorSequence = new FeatureVectorSequence(
            CRFTools.featuresToAugmentableFeatureVectorArray(this
                    .getFeatureExtractor().extractFeatures(sentence), this
                    .getFeatureAlphabet(), false));

    Instance instance = new Instance(featureVectorSequence, null, null, sentence);

    @SuppressWarnings("unchecked")
    Sequence<String> output = crf.transduce((Sequence<String>) instance
            .getData());

    String[] labels = new String[output.size()];
    for (int i = 0; i < output.size(); i++) {
      labels[i] = output.get(i);
    }

    return labels;
  }

  public FeatureExtractor getFeatureExtractor() {
    return featureExtractor;
  }

  public void setFeatureExtractor(FeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }
}
