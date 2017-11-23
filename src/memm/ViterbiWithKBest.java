/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memm;

import cc.mallet.types.InstanceList;
import splitter.utils.SortedArrayList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Alex Sliz-Nagy<sliz-nagy.alex@stud.u-szeged.hu>
 */
public class ViterbiWithKBest extends Viterbi {
    private KState[][] Lattice;

    private class State implements Comparable<State> {

        private double score;
        private State backPointer;
        private int labelIndex;

        public State(double score, State backPointer, int labelIndex) {
            this.setScore(score);
            this.setBackPointer(backPointer);
            this.setLabelIndex(labelIndex);
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public State getBackPointer() {
            return backPointer;
        }

        public void setBackPointer(State backPointer) {
            this.backPointer = backPointer;
        }

        public int getLabelIndex() {
            return labelIndex;
        }

        public void setLabelIndex(int labelIndex) {
            this.labelIndex = labelIndex;
        }

        @Override
        public int compareTo(State o) {
            if ((this.getScore() - o.getScore()) > 0) {
                return -1;
            } else if ((this.getScore() - o.getScore()) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private class KState {

        public final int K;

        private List<State> row;

        public KState(int K) {
            this.K = K;
            row = new LinkedList<>();
        }

        public int getStateCount() {
            return row.size();
        }

        private int searchPosition(double score) {
            int i = 0;
            for (State s : row) {
                if (score > s.getScore()) {
                    return i;
                }
                i++;
            }
            return row.size();
        }

        public void pushScore(double score, State backPointer, int labelInd) {
            int position = searchPosition(score);

            if (position <= K) {
                row.add(position, new State(score, backPointer, labelInd));

                if (row.size() > K) {
                    row.remove(K);
                }
            }
        }

        public List<State> getRow() {
            return row;
        }

        public void setRow(List<State> row) {
            this.row = row;
        }

    }

    public ViterbiWithKBest(InstanceList instances, MEMM model) {
        super(instances, model);
    }


    private State[] getKBestFinalState(int K) {
        List<State> finals = new SortedArrayList<>();

        int instanseLength = Lattice[0].length - 1;
        for (int i = 0; i < Lattice.length; ++i) {
            for (int ii = 0; ii < K; ++ii) {
                finals.add(Lattice[i][instanseLength].getRow().get(ii));
            }
        }

        List<State> kBest = new ArrayList<>();
        for (int i = 0; i < K; ++i) {
            State s = finals.get(i);
            if (s == null) {
                break;
            }
            kBest.add(s);
        }

        return kBest.toArray(new State[kBest.size()]);
    }

    private void initLattice(int K) {
        double[] startProbs = getClassificationScoresWithPrevState(instances.get(0), "START");

        for (int i = 0; i < Lattice.length; ++i) {
            Lattice[i][0] = new KState(K);
            Lattice[i][0].pushScore(startProbs[i], null, i);
        }
    }

    public int[][] getKbestPath(int K) {
        int numLabel = model.getLabelAlphabet().size();
        int[][] paths = new int[K][instances.size()];

        Lattice = new KState[numLabel][instances.size()];
        initLattice(K);

        for (int i = 1; i < instances.size(); ++i) {

            // calculate transitionScores
            double[][] transitionScores = new double[numLabel][numLabel];
            for (int k = 0; k < numLabel; k++) {
                transitionScores[k] = getClassificationScoresWithPrevState(
                        instances.get(i), model.getLabelAlphabet().lookupObject(k)
                                .toString());
            }

            for (int ii = 0; ii < numLabel; ++ii) {
                Lattice[ii][i] = new KState(K);
                double score;
                for (int iii = 0; iii < numLabel; ++iii) {
                    for (int k = 0; k < Lattice[iii][i - 1].getStateCount(); ++k) {
                        score = transitionScores[iii][ii] * Lattice[iii][i - 1].getRow().get(k).getScore();
                        Lattice[ii][i].pushScore(score, Lattice[iii][i - 1].getRow().get(k), ii);
                    }
                }
            }
        }

        State[] kBestFinalState = getKBestFinalState(K);

        for (int i = 0; i < K; ++i) {
            paths[i][instances.size() - 1] = kBestFinalState[i].labelIndex;
            State backPointer = kBestFinalState[i].backPointer;
            for (int ii = instances.size() - 2; ii >= 0; --ii) {
                paths[i][ii] = backPointer.labelIndex;
                backPointer = backPointer.backPointer;
            }
        }

        return paths;
    }
}