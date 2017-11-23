package memm;

import cc.mallet.types.Alphabet;
import extractor.FeatureExtractor;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public MEMMModel(MEMM memm, FeatureExtractor featureExtractor) {
        if (memm != null) {
            this.setMemm(memm);
            this.setFeatureAlphabet(memm.getAlphabet());
            this.setLabelAlphabet(memm.getLabelAlphabet());
        }
        this.setFeatureExtractor(featureExtractor);
    }

    public void serialize(String file) {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
                    new FileOutputStream(file)));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restore model from an input file.
     */
    public static MEMMModel restore(String file) {
        ObjectInputStream objectInputStream = null;
        MEMMModel model = null;

        try {
            objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            model = (MEMMModel) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return model;
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
