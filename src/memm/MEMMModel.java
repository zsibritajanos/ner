package memm;

import cc.mallet.types.Alphabet;
import extractor.FeatureExtractor;

import java.io.Serializable;

//import relatedworks.LineFeatureExtractor;

public class MEMMModel implements Serializable {

    private static final long serialVersionUID = 1L;
    // trained MEMM
    private MEMM memm = null;
    // possible labels
    private Alphabet labelAlphabet = null;
    // possible features
    private Alphabet featureAlphabet = null;
    // feature extractor
    private FeatureExtractor featureExtractor = null;

    public MEMMModel(MEMM memm, Alphabet featureAlphabet, Alphabet labelAlphabet,
                     FeatureExtractor featureExtractor) {
        this.setMemm(memm);
        this.setFeatureAlphabet(featureAlphabet);
        this.setLabelAlphabet(labelAlphabet);
        this.setFeatureExtractor(featureExtractor);
    }

    public MEMM getMemm() {
        return memm;
    }

    public void setMemm(MEMM memm) {
        this.memm = memm;
    }

    public FeatureExtractor getFeatureExtractor() {
        return featureExtractor;
    }

    public void setFeatureExtractor(FeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
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
}
