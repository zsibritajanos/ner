package memm;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import java.util.Arrays;

public class Viterbi {

    protected final MEMM model;

    protected final InstanceList instances;

    protected double[][] decisionMatrix;

    protected int[][] backpointer;

    public Viterbi(InstanceList instances, MEMM model) {
        this.instances = instances;
        this.model = model;
    }

    protected int getMax(double[] scores) {
        double greatest = 0.0;
        int greatestValue = 0;

        for (int i = 0; i < scores.length; i++) {
            if (greatest < scores[i]) {
                greatest = scores[i];
                greatestValue = i;
            }
        }

        return greatestValue;
    }

    /**
     * Gets the classification score if the previous class is the given.
     *
     * @param inst      The current instance.
     * @param prevState The previous class.
     * @return An array scores.
     */
    public double[] getClassificationScoresWithPrevState(Instance inst,
                                                         String prevState) {
        double[] scores = new double[model.getLabelAlphabet().size()];
        if (!model.getModels().containsKey(prevState)) {
            Arrays.fill(scores, 0f);
            return scores;
        }
        model.getModels().get(prevState).getClassificationScores(inst, scores);

        return scores;
    }

    public int[] traverse() {
        return traverse("START");
    }

    /**
     * Calculates the best path.
     *
     * @return most likely hidden state sequence.
     */
    public int[] traverse(String startClass) {
        int numLabel = model.getLabelAlphabet().size();
        setDecisionMatrix(new double[instances.size()][numLabel]);
        backpointer = new int[instances.size()][numLabel];

        getDecisionMatrix()[0] = getClassificationScoresWithPrevState(
                instances.get(0), startClass);

        for (int i = 1; i < instances.size(); i++) {
            double[][] transitionScores = new double[numLabel][numLabel];
            for (int k = 0; k < numLabel; k++) {
                transitionScores[k] = getClassificationScoresWithPrevState(
                        instances.get(i), model.getLabelAlphabet().lookupObject(k)
                                .toString());
            }

            for (int j = 0; j < numLabel; j++) {

                double currentMaxScore = 0.0;
                for (int k = 0; k < numLabel; k++) {
                    if (currentMaxScore < getDecisionMatrix()[i - 1][k]
                            * transitionScores[k][j]) {
                        currentMaxScore = getDecisionMatrix()[i - 1][k]
                                * transitionScores[k][j];
                        backpointer[i][j] = k;
                    }
                }
                getDecisionMatrix()[i][j] = currentMaxScore;
            }
        }

        int[] path = new int[instances.size()];
        path[instances.size() - 1] = getMax(getDecisionMatrix()[instances.size() - 1]);
        for (int i = instances.size() - 2; i >= 0; i--) {
            path[i] = backpointer[i + 1][path[i + 1]];
        }

        return path;
    }


    public double[][] getDecisionMatrix() {
        return decisionMatrix;
    }

    public void setDecisionMatrix(double[][] decisionMatrix) {
        this.decisionMatrix = decisionMatrix;
    }

}
