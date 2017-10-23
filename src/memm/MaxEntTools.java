package memm;

import cc.mallet.classify.MaxEntL1Trainer;
import cc.mallet.types.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MaxEntTools {

    public static List<String> predict(InstanceList instance, MEMM maxent) {
        return predict(instance, maxent, "START");
    }

    /**
     * Predicates the labels of the given instance via the given trained CRF.
     *
     * @param instance instance to predicate
     * @param maxent   trained CRF
     * @return sequence of the predicated labels
     */
    public static List<String> predict(InstanceList instance, MEMM maxent,
                                       String startClassId) {

        Viterbi vit = new Viterbi(instance, maxent);

        int[] path = vit.traverse(startClassId);

        List<String> labels;

        labels = new LinkedList<String>();
        for (int p : path) {
            labels.add((String) maxent.getLabelAlphabet().lookupObject(p));
        }

        return labels;
    }


    @SuppressWarnings("unchecked")
    public static MEMM trainMEMM(InstanceList trainingData) {

        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Map<String, InstanceList> datasets = new HashMap<String, InstanceList>();

        for (int instanceIdx = 0; instanceIdx < trainingData.size(); instanceIdx++) {

            Instance inst = trainingData.get(instanceIdx);

            Instance prev = (instanceIdx > 1) ? trainingData.get(instanceIdx - 1)
                    : null;

            LabelSequence prevls = (prev != null) ? (LabelSequence) prev.getTarget()
                    : null;
            FeatureVectorSequence fvs = (FeatureVectorSequence) inst.getData();

            LabelSequence ls = (LabelSequence) inst.getTarget();

            for (int i = 0; i < fvs.size(); ++i) {
                AugmentableFeatureVector fv = (AugmentableFeatureVector) (fvs.get(i));

                //String defLabel = (prev != null) ? prevls.get(prevls.size() - 1)
                //        .toString() : "START";
                String prevlab;
                if (i > 0) {
                    prevlab = ls.get(i - 1).toString();
                } else {
                    prevlab = "START";
                }

                Label lab = labelAlphabet.lookupLabel(ls.get(i).toString());
                Instance bininst = new Instance(fv, lab, null,
                        ((List<String>) inst.getSource()).get(i));

                if (!datasets.containsKey(prevlab)) {
                    datasets.put(prevlab, new InstanceList(
                            trainingData.getDataAlphabet(), labelAlphabet));
                }
                datasets.get(prevlab).add(bininst);
            }
        }

        MEMM memm = new MEMM();
        for (String lab : datasets.keySet()) {
            memm.setModel(lab, new MaxEntL1Trainer().train(datasets.get(lab), 200));
        }

        return memm;
    }
}
