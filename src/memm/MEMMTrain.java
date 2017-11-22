package memm;

import cc.mallet.types.*;
import extractor.FeatureExtractor;

import java.util.Arrays;
import java.util.List;


/**
 * Class for triaining MEMM via the given train file. One documentum will be one
 * instance.
 */
public class MEMMTrain {

    // labels
    private Alphabet labelAlphabet = null;
    // features
    private Alphabet featureAlphabet = null;
    // train instances
    private InstanceList instanceList = null;

    // feature extactor
    private FeatureExtractor featureExtractor = null;

    /**
     * Constuctor, initilizes the alphabets, the feature extactor and the
     * instance collector.
     */
    public MEMMTrain(FeatureExtractor featureExtractor) {
        this.setFeatureAlphabet(new Alphabet());
        this.getFeatureAlphabet().startGrowth();

        this.setLabelAlphabet(new LabelAlphabet());
        this.getLabelAlphabet().startGrowth();

        this.setFeatureExtractor(featureExtractor);

        this.setInstanceList(new InstanceList(this.getFeatureAlphabet(), this
                .getLabelAlphabet()));
    }


    public void addSentence(String[] sentence, String[] labels) {
        if (sentence.length > 0 && labels.length > 0 && labels.length == sentence.length) {
            FeatureVectorSequence featureVectorSequence = null;
            LabelSequence labelSequene = null;

            List<List<String>> features = featureExtractor.extractFeatures(sentence);

            featureVectorSequence = InstanceListTools.featuresToFeatureVectorSequence(features, featureAlphabet, true);

            labelSequene = new LabelSequence(labelAlphabet);
            for (String label : labels) {
                labelSequene.add(label);
            }

            Instance instance = null;
            instance = new Instance(featureVectorSequence, labelSequene, null, Arrays.asList(sentence));

            this.getInstanceList().add(instance);
        }
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
        featureAlphabet.startGrowth();
        this.instanceList = new InstanceList(featureAlphabet, this.getLabelAlphabet());
    }

    public InstanceList getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(InstanceList instanceList) {
        this.instanceList = instanceList;
    }

    public MEMMModel train(List<List<String[]>> trainSentences, String output) {
        for (List<String[]> sentence : trainSentences) {
            String[] tokens = new String[sentence.size()];
            String[] labels = new String[sentence.size()];

            for (int i = 0; i < sentence.size(); ++i) {
                tokens[i] = sentence.get(i)[0];
                labels[i] = sentence.get(i)[1];
            }

            this.addSentence(tokens, labels);

        }

        MEMM memm = MaxEntTools.trainMEMM(this.getInstanceList());

        MEMMModel memmModel = new MEMMModel(memm, this.getFeatureExtractor());
        return memmModel;
    }
}
