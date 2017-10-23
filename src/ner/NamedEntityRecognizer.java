package ner;

import cc.mallet.fst.CRF;
import cc.mallet.types.*;
import crf.CRFTools;
import crf.Model;
import extractor.FeatureDescriptor;
import extractor.FeatureExtractor;
import feature.Feature;
import util.InstanceListUtil;

import java.util.List;

public class NamedEntityRecognizer {

  private FeatureDescriptor featureDescriptor = null;
  private CRF crf = null;
  private FeatureExtractor featureExtractor = null;
  private Alphabet labelAlphabet = null;
  private Alphabet featureAlphabet = null;

  public NamedEntityRecognizer(List<Feature> features, CRF crf) {
    this.setCrf(crf);

    this.setFeatureAlphabet(this.getCrf().getInputAlphabet());
    this.getFeatureAlphabet().stopGrowth();

    this.setLabelAlphabet(this.getCrf().getOutputAlphabet());
    this.getLabelAlphabet().stopGrowth();

    this.setFeatureExtractor(new FeatureExtractor(features));
  }

  public NamedEntityRecognizer(FeatureDescriptor featureDescriptor, CRF crf) {
    this.setFeatureDescriptor(featureDescriptor);
    this.setCrf(crf);

    this.setFeatureAlphabet(this.getCrf().getInputAlphabet());
    this.getFeatureAlphabet().stopGrowth();

    this.setLabelAlphabet(this.getCrf().getOutputAlphabet());
    this.getLabelAlphabet().stopGrowth();

    this.setFeatureExtractor(new FeatureExtractor(this.getFeatureDescriptor()
            .getFeatures()));
  }

  /**
   * @param file
   */
  public NamedEntityRecognizer(String file) {
    this(Model.restore(file));
    System.err.println("model loaded " + file);
  }

  /**
   * @param model
   */
  public NamedEntityRecognizer(Model model) {
    this(model.getFeatureDescriptor(), model.getCrf());
  }

  /**
   * @param sentence
   * @return
   */
  public String[] predicateSentence(String[] sentence) {
    return predicateSentence(sentence, null, null);
  }

  /**
   * @param sentence
   * @param instanceLogFile
   * @return
   */
  public String[] predicateSentence(String[] sentence, String[] goldLabels, String instanceLogFile) {

    FeatureVectorSequence featureVectorSequence = new FeatureVectorSequence(
            CRFTools.featuresToAugmentableFeatureVectorArray(this
                    .getFeatureExtractor().extractFeatures(sentence), this
                    .getFeatureAlphabet(), false));

    Instance instance = new Instance(featureVectorSequence, null, null, sentence);

    Sequence<String> output = crf.transduce((Sequence<String>) instance.getData());

    String[] predicatedLabels = new String[output.size()];
    for (int i = 0; i < output.size(); i++) {
      predicatedLabels[i] = output.get(i);
    }

    if (instanceLogFile != null && goldLabels != null) {
      InstanceListUtil.writeInstanceList(goldLabels, predicatedLabels, instance, crf, instanceLogFile);
    }

    return predicatedLabels;
  }

  public FeatureDescriptor getFeatureDescriptor() {
    return featureDescriptor;
  }

  public void setFeatureDescriptor(FeatureDescriptor featureDescriptor) {
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

  public FeatureExtractor getFeatureExtractor() {
    return featureExtractor;
  }

  public void setFeatureExtractor(FeatureExtractor featureExtractor) {
    this.featureExtractor = featureExtractor;
  }
}
