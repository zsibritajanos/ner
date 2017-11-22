package ner;

import java.util.Arrays;
import java.util.List;

//import cc.mallet.fst.CRF;
import cc.mallet.types.*;
//import crf.CRFTools;
//import crf.Model;
import extractor.FeatureDescriptor;
import extractor.FeatureExtractor;
import memm.MEMM;
import memm.MEMMModel;
import memm.MaxEntTools;
import memm.InstanceListTools;
import util.InstanceListUtil;

public class NamedEntityRecognizer {

  private FeatureDescriptor featureDescriptor = null;
//  private CRF crf = null;
  private MEMM memm;
  private FeatureExtractor featureExtractor = null;
  private Alphabet labelAlphabet = null;
  private Alphabet featureAlphabet = null;

  /**
   * @param model
   */
  public NamedEntityRecognizer(MEMMModel memm) {

	this.setMemm(memm.getMemm());
    this.setFeatureAlphabet(memm.getFeatureAlphabet());
    this.getFeatureAlphabet().stopGrowth();

    this.setLabelAlphabet(memm.getLabelAlphabet());
    this.getLabelAlphabet().stopGrowth();

    this.setFeatureExtractor(memm.getFeatureExtractor());
  }

  /**
   * @param file
   */
  public NamedEntityRecognizer(String file) {
    this(MEMMModel.restore(file));
    System.err.println("model loaded " + file);
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

/*    FeatureVectorSequence featureVectorSequence = new FeatureVectorSequence(
            CRFTools.featuresToAugmentableFeatureVectorArray(this
                    .getFeatureExtractor().extractFeatures(sentence), this
                    .getFeatureAlphabet(), false));
*/
	  
	FeatureVectorSequence featureVectorSequence = 
			InstanceListTools.featuresToFeatureVectorSequence(
					this.getFeatureExtractor().extractFeatures(sentence),
					this.getFeatureAlphabet(), false);
			
	  
//    Instance instance = new Instance(featureVectorSequence, null, null, sentence);
	InstanceList instanceList = InstanceListTools.doInstanceList(this.memm, featureVectorSequence, Arrays.asList(sentence));
	
	List<String> output = MaxEntTools.predict(instanceList, this.memm);
    // Sequence<String> output = memm.transduce((Sequence<String>) instance.getData());

    String[] predicatedLabels = new String[output.size()];
    for (int i = 0; i < output.size(); i++) {
      predicatedLabels[i] = output.get(i);
    }

    if (instanceLogFile != null && goldLabels != null) {
      InstanceListUtil.writeInstanceList(instanceList, instanceLogFile);
    }

    return predicatedLabels;
  }

  public FeatureDescriptor getFeatureDescriptor() {
    return featureDescriptor;
  }

  public void setFeatureDescriptor(FeatureDescriptor featureDescriptor) {
    this.featureDescriptor = featureDescriptor;
  }

/*  public CRF getCrf() {
    return crf;
  }

  public void setCrf(CRF crf) {
    this.crf = crf;
  }
*/
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

  public MEMM getMemm() {
	return memm;
  }

  public void setMemm(MEMM memm) {
	this.memm = memm;
  }
}
