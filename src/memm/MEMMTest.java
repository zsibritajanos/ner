package memm;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.InstanceList;
import extractor.FeatureExtractor;

import java.util.Arrays;
import java.util.List;

/**
 * Predicates the given test file via the specified trained MEMM model.
 */
public class MEMMTest {

    private Alphabet labelAlphabet = null;
    private Alphabet featureAlphabet = null;
    private MEMM memm = null;
    private FeatureExtractor featureExtractor = null;

    public MEMMTest(MEMMModel memmModel) {
        this.setFeatureAlphabet(memmModel.getFeatureAlphabet());
        this.setLabelAlphabet(memmModel.getLabelAlphabet());
        this.setFeatureExtractor(memmModel.getFeatureExtractor());
        this.setMemm(memmModel.getMemm());
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


    public InstanceList sentenceToInstanceList(String[] sentence) {
        InstanceList instanceList = null;

        FeatureVectorSequence featureVectorSequence = null;

        List<List<String>> features = featureExtractor.extractFeatures(sentence);

        featureVectorSequence = InstanceListTools.featuresToFeatureVectorSequence(
                features, this.getFeatureAlphabet(), false);

        instanceList = InstanceListTools.doInstanceList(this.getMemm(),
                featureVectorSequence, Arrays.asList(sentence));

        return instanceList;
    }

    public List<String[]> getPrediction(String[] sentence, int k) {
        InstanceList instanceList = sentenceToInstanceList(sentence);

        return MaxEntTools.predictNBest(instanceList, this.getMemm(), k);
    }

}
