package memm;

import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntL1Trainer;
import cc.mallet.types.*;

import java.util.*;

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

        labels = new LinkedList<>();
        for (int p : path) {
            labels.add((String) maxent.getLabelAlphabet().lookupObject(p));
        }

        return labels;
    }

    public static List<String[]> predictNBest(InstanceList instance, MEMM maxent, int k) {
        return predictNBest(instance, maxent, k, "START");
    }

    public static List<String[]> predictNBest(InstanceList instance, MEMM maxent, int k, String startClassId) {
        ViterbiWithKBest vit = new ViterbiWithKBest(instance, maxent);

        int[][] paths = vit.getKbestPath(k);

        List<String[]> kBestLabels = new ArrayList<>();
        for (int i = 0; i < k; ++i) {
            List<String> toAdd = new ArrayList<>();

            for (int p : paths[i]) {
                toAdd.add((String) maxent.getLabelAlphabet().lookupObject(p));
            }
            kBestLabels.add(toAdd.toArray(new String[toAdd.size()]));
        }

        return kBestLabels;
    }


    @SuppressWarnings("unchecked")
    protected static Map<String, InstanceList> createMaxEntDatasets(InstanceList trainingData, MEMM orig) {
        LabelAlphabet labelAlphabet = (orig == null ? new LabelAlphabet() : orig.getLabelAlphabet());
        labelAlphabet.startGrowth();

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

                Label lab = labelAlphabet.lookupLabel(ls.get(i).toString(), true);
                Instance bininst = new Instance(fv, lab, null,
                        ((List<String>) inst.getSource()).get(i));

                if (!datasets.containsKey(prevlab)) {
                    datasets.put(prevlab, new InstanceList(
                            trainingData.getDataAlphabet(), labelAlphabet));
                }
                datasets.get(prevlab).add(bininst);
            }
        }
        return datasets;
    }

    public static MEMM trainMEMM(InstanceList trainingData) {
        Map<String, InstanceList> datasets = createMaxEntDatasets(trainingData, null);
        MEMM memm = new MEMM();
        for (String lab : datasets.keySet()) {
            memm.setModel(lab, new MaxEntL1Trainer().train(datasets.get(lab), 200));
        }
        return memm;
    }

    public static MEMM trainMEMMIncremental(MEMM orig, InstanceList trainingData, int orig_numfeature) {
        int orig_numlabel = orig.getLabelAlphabet().size();
        Map<String, InstanceList> datasets = createMaxEntDatasets(trainingData, orig);

        Set<String> labelsToTrain = new HashSet<String>(datasets.keySet());
        for (int i = 0; i < orig.getLabelAlphabet().size(); ++i) {
            labelsToTrain.add(orig.getLabelAlphabet().lookupLabel(i).toString());
        }

        for (String lab : labelsToTrain) {
            MaxEnt orig_lab = orig.getModels().get(lab);
            if (orig_lab != null && (orig_lab.getNumParameters() != orig_lab.getParameters().length)) {
                fixMaxentParameters(orig_lab, orig_numlabel, orig_numfeature);
                orig.setModel(lab, orig_lab);
            }
            if (!datasets.containsKey(lab) || datasets.get(lab).size() < 10)
                continue;
            MaxEntL1Trainer trainer = orig_lab != null ? new MaxEntL1Trainer(orig_lab) : new MaxEntL1Trainer();
            orig.setModel(lab, trainer.train(datasets.get(lab), 200));
        }
        return orig;
    }

    private static void fixMaxentParameters(MaxEnt orig, int orig_numlabel, int orig_numfeature) {
        double p[] = new double[orig.getNumParameters()];
        Arrays.fill(p, 0f);
        int s = orig.getAlphabet().size() + 1;
        for (int l = 0; l < orig_numlabel; ++l)
            for (int f = 0; f < orig_numfeature; ++f) {
                if (orig.getParameters().length <= l * (orig_numfeature + 1) + f)
                    break;
                p[l * s + f] = orig.getParameters()[l * (orig_numfeature + 1) + f];
            }
        orig.setParameters(p);
        orig.setDefaultFeatureIndex(orig.getAlphabet().size());
    }

/*	public static List<NamedEntity> testFromStringTokens(InstanceList testData, String text, int[] offsets, MEMM memm) {
        LinkedList NEList = new LinkedList();
        new Vector();
        NamedEntity ne = null;
        String prevLabel = "";
        String currLabel = "";
        Vector testWords = (Vector) ((Instance) testData.get(0)).getSource();
        Sequence input = (Sequence) ((Instance) testData.get(0)).getData();

        //Sequence output = memm.transduce(input);


        List<String> myWords = new LinkedList<>();
        for (int j = 0; j < testWords.size(); ++j) {
            myWords.add(testWords.get(j).toString());
        }


        List output = predict(InstaceListTools.doInstanceList(memm, (FeatureVectorSequence) input, myWords), memm);


        for (int j = 0; j < output.size(); ++j) {
            currLabel = output.get(j).toString();
            if (!currLabel.equals("O")) {
                if (currLabel.equals(prevLabel)) {
                    ne.setEntity((text.substring(ne.getBegin(), offsets[j]) + ((String[]) ((String[]) testWords.get(j)))[0].toString()).trim());
                    ne.setEnd(ne.getBegin() + ne.getEntity().length());
                } else {
                    if (ne != null) {
                        NEList.add(ne);
                        ne = null;
                    }

                    if (ne != null) {
                        NEList.add(ne);
                        ne = null;
                    }

                    ne = new NamedEntity();
                    ne.setEntity(((String[]) ((String[]) testWords.get(j)))[0].toString().trim());
                    ne.setAnnotation(output.get(j).toString());
                    ne.setBegin(offsets[j]);
                    ne.setEnd(offsets[j] + ne.getEntity().length());
                }
            }

            prevLabel = currLabel;
        }

        if (ne != null) {
            NEList.add(ne);
        }

        return NEList;
    }
*/
}
