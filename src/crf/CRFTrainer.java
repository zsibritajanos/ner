package crf;

import cc.mallet.types.*;
import extractor.FeatureExtractor;

public class CRFTrainer {

  private Alphabet labelAlphabet = null;
  private Alphabet featureAlphabet = null;
  private InstanceList instanceList = null;
  private FeatureExtractor featureExtractor = null;

  /**
   *
   * @param featureExtractor
   */
  public CRFTrainer(FeatureExtractor featureExtractor) {
    this.setFeatureAlphabet(new Alphabet());
    this.getFeatureAlphabet().startGrowth();

    this.setLabelAlphabet(new LabelAlphabet());
    this.getLabelAlphabet().startGrowth();

    this.setInstanceList(new InstanceList(this.getFeatureAlphabet(), this
            .getLabelAlphabet()));

    this.setFeatureExtractor(featureExtractor);
  }

  /**
   *
   * @param sentence
   * @param labels
   */
  public void addSentence(String[] sentence, String[] labels) {
    if (sentence.length > 0 && labels.length > 0 && labels.length == sentence.length) {

      FeatureVectorSequence featureVectorSequence = new FeatureVectorSequence(
              CRFTools.featuresToAugmentableFeatureVectorArray(this
                      .getFeatureExtractor().extractFeatures(sentence), this
                      .getFeatureAlphabet(), true));

      LabelSequence labelSequence = new LabelSequence(this.getLabelAlphabet());

      for (String label : labels) {
        labelSequence.add(label);
      }

      Instance instance = new Instance(featureVectorSequence, labelSequence, null, sentence);

      this.getInstanceList().add(instance);
    }
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

  public InstanceList getInstanceList() {
    return instanceList;
  }

  public void setInstanceList(InstanceList instanceList) {
    this.instanceList = instanceList;
  }

  public FeatureExtractor getFeatureExtractor() {
    return featureExtractor;
  }

  public void setFeatureExtractor(FeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }
}
