package memm;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.Alphabet;
import cc.mallet.types.LabelAlphabet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to store the machine learned MEMM models.
 */
@SuppressWarnings("serial")
public class MEMM implements Serializable {
    private final Map<String, MaxEnt> models = new HashMap<String, MaxEnt>();

    public Map<String, MaxEnt> getModels() {
        return models;
    }

    public void setModel(String c, MaxEnt model) {
        this.models.put(c, model);
    }

    public LabelAlphabet getLabelAlphabet() {
        for (MaxEnt m : models.values()) {
            return m.getLabelAlphabet();
        }
        return null;
    }

    public Alphabet getAlphabet() {
        for (MaxEnt m : models.values()) {
            return m.getAlphabet();
        }
        return null;
    }
}
