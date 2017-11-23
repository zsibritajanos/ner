package memm;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class InstanceListTools {

    /**
     * Writes the given instancelist to the specifed file, with the specified
     * encoding.
     *
     * @param instanceList
     * @param file
     * @param encoding
     */
    public static void writePlainInstanceList(InstanceList instanceList,
                                              String file, String encoding) {

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    file), encoding));

            for (Instance instance : instanceList) {

                FeatureVectorSequence data = (FeatureVectorSequence) instance.getData();
                LabelSequence target = (LabelSequence) instance.getTarget();

                @SuppressWarnings("unchecked")
                List<String> source = (List<String>) instance.getSource();

                for (int i = 0; i < data.size(); i++) {
                    writer.write(target.get(i).toString() + "\t");
                    writer.write(source.get(i) + "\t");
                    writer.write(data.get(i).toString(true));
                    writer.write("\n");
                }
                writer.write("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Writes the given instancelist with featureweights to the specified file,
     * with the specified encoding.
     *
     * @param instanceList
     * @param memm
     * @param writer
     */
    public static void writePlainInstanceListWithFeatureWeights(
            InstanceList instanceList, MEMM memm, Writer writer) {
        try {
            for (int i = 0; i < instanceList.size(); i++) {
                Instance instance = instanceList.get(i);

                AugmentableFeatureVector data = (AugmentableFeatureVector) instance
                        .getData();

                String source = (String) instance.getSource();

                writer.write(source + "\t");

                String prevClass = (i > 0) ? (String) instanceList.get(i - 1)
                        .getTarget() : "START";
                String currentClass = (String) instance.getTarget();
                double[] fv = getFeatureWeights(memm.getModels().get(prevClass),
                        currentClass);

                SortedSet<Entry<Double, String>> features = sortFeatures(data, fv);

                for (Entry<Double, String> sortedEntry : features) {
                    writer.write(sortedEntry.getValue() + " ("
                            + String.format("%.2f", sortedEntry.getKey()) + ") ");
                }

                SortedSet<Entry<Double, String>> postProbs = getPosterioriProbability(
                        memm, prevClass, instance);
                int k = 0;
                for (Entry<Double, String> postProbForClass : postProbs) {
                    if ((postProbForClass.getKey() - 0.01) < 0 && k >= 1) {
                        break;
                    }
                    writer.write(" " + postProbForClass.getValue() + "("
                            + String.format("%.2f", postProbForClass.getKey()) + ")");
                    if (++k >= 3) {
                        break;
                    }
                }
                writer.write('\n');
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Sorts the features by its values.
     *
     * @param features
     * @param fv
     * @return
     */
    public static SortedSet<Entry<Double, String>> sortFeatures(
            FeatureVector features, double[] fv) {

        SortedSet<Entry<Double, String>> ret = new TreeSet<>(
                getComparator());

        Alphabet featureAlphabet = features.getAlphabet();

        for (int j = 0; j < features.numLocations(); j++) {
            int featureIndex = features.indexAtLocation(j);
            String objectName = featureAlphabet.lookupObject(featureIndex).toString();
            if (Math.abs(fv[featureIndex]) - 0.001 > 0.0) {
                ret.add(new AbstractMap.SimpleEntry<>(fv[featureIndex],
                        objectName));
            }
        }
        return ret;
    }

    /**
     * Gets the featureWeights for the label from the given model.
     *
     * @param model
     * @param label
     * @return
     */
    public static double[] getFeatureWeights(MaxEnt model, String label) {
        final Alphabet dict = model.getAlphabet();
        final LabelAlphabet labelDict = model.getLabelAlphabet();

        dict.stopGrowth();
        labelDict.stopGrowth();

        int numFeatures = dict.size() + 1;

        double[] weights = new double[numFeatures - 1];
        Label l = labelDict.lookupLabel(label);
        int li = l.getIndex();
        for (int i = 0; i < model.getDefaultFeatureIndex(); i++) {
            double weight = model.getParameters()[li * numFeatures + i];
            weights[i] = weight;
        }
        return weights;
    }

    public static Comparator<Entry<Double, String>> getComparator() {
        return new Comparator<Entry<Double, String>>() {
            @Override
            public int compare(Entry<Double, String> e1, Entry<Double, String> e2) {
                int cf = e1.getKey().compareTo(e2.getKey());
                if (cf == -1) {
                    cf = 1;
                } else if (cf == 1) {
                    cf = -1;
                } else {
                    cf = e1.getValue().compareTo(e2.getValue());
                }
                return cf;
            }
        };
    }

    /**
     * Gets the Posteriori probability for the current instance.
     *
     * @param memm
     * @param prevClassID
     * @param currentInstance
     * @return
     */
    public static SortedSet<Entry<Double, String>> getPosterioriProbability(
            MEMM memm, String prevClassID, Instance currentInstance) {
        MaxEnt currentModel = memm.getModels().get(prevClassID);
        SortedSet<Entry<Double, String>> classes = new TreeSet<>(
                getComparator());
        double[] scores = new double[currentModel.getLabelAlphabet().size()];
        currentModel.getClassificationScores(currentInstance, scores);
        for (int i = 0; i < scores.length; i++) {
            String classID = (String) currentModel.getLabelAlphabet().lookupObject(i);
            classes.add(new AbstractMap.SimpleEntry<>(scores[i],
                    classID));
        }
        return classes;
    }

    /**
     * Creates an augmentable feature vector array from list of features.
     *
     * @param features
     * @param featureAlphabet
     * @param growth
     * @return
     */
    public static AugmentableFeatureVector[] featuresToAugmentableFeatureVectorArray(
            List<List<String>> features, Alphabet featureAlphabet, boolean growth) {

        AugmentableFeatureVector[] augmentableFeatureVectorArray = new AugmentableFeatureVector[features
                .size()];

        for (int i = 0; i < features.size(); i++) {
            augmentableFeatureVectorArray[i] = new AugmentableFeatureVector(
                    featureAlphabet, 100, true);
        }

        if (!growth) {
            featureAlphabet.stopGrowth();
        }

        int index;
        for (int i = 0; i < features.size(); ++i) {
            for (int j = 0; j < features.get(i).size(); ++j) {
                index = featureAlphabet.lookupIndex(features.get(i).get(j), growth);

                if (index != -1) {
                    augmentableFeatureVectorArray[i].add(index);
                }
            }
        }

        return augmentableFeatureVectorArray;
    }

    public static FeatureVectorSequence featuresToFeatureVectorSequence(
            List<List<String>> features, Alphabet featureAlphabet, boolean growth) {

        return new FeatureVectorSequence(featuresToAugmentableFeatureVectorArray(
                features, featureAlphabet, growth));
    }

    public static FeatureVectorSequence featuresToFeatureVectorSequence(
            List<List<String>> features, MEMM model, boolean growth) {
        return new FeatureVectorSequence(featuresToAugmentableFeatureVectorArray(
                features, model.getAlphabet(), growth));
    }

    /**
     * Creates an {@code InstanceList} from the given
     * {@code FeatureVectorSequence} and content.
     *
     * @param model
     * @param fvs
     * @param content
     * @return
     */
    public static InstanceList doInstanceList(MEMM model,
                                              FeatureVectorSequence fvs, List<String> content) {
        InstanceList instance = new InstanceList(model.getAlphabet(), null);
        instance.getDataAlphabet().stopGrowth();

        for (int i = 0; i < fvs.size(); i++) {
            Instance inst = new Instance(fvs.get(i), null, null, content.get(i));
            instance.add(inst);
        }

        return instance;
    }
}
