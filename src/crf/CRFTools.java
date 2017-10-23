package crf;

import cc.mallet.classify.MaxEntOptimizableByLabelLikelihood;
import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByValueGradients;
import cc.mallet.optimize.Optimizable;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.MalletProgressMessageLogger;

import java.util.List;
import java.util.logging.Level;

public class CRFTools {

  /**
   *
   */
  private static final int ITERATIONS = 200;

  /**
   *
   * @param features
   * @param featureAlphabet
   * @param growth
   * @return
   */
  public static AugmentableFeatureVector[] featuresToAugmentableFeatureVectorArray(List<List<String>> features, Alphabet featureAlphabet, boolean growth) {

    AugmentableFeatureVector[] augmentableFeatureVectorArray = new AugmentableFeatureVector[features.size()];

    for (int i = 0; i < features.size(); i++) {
      augmentableFeatureVectorArray[i] = new AugmentableFeatureVector(featureAlphabet, 100, true);
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


  /**
   *
   * @param instanceList
   * @return
   */
  public static CRF train(InstanceList instanceList) {

    ((MalletLogger) MalletProgressMessageLogger
            .getLogger(MaxEntOptimizableByLabelLikelihood.class.getName()))
            .getRootLogger().setLevel(Level.OFF);

    CRF crf = new CRF(instanceList.getDataAlphabet(),
            instanceList.getTargetAlphabet());

    crf.addFullyConnectedStatesForLabels();

    crf.setWeightsDimensionAsIn(instanceList, false);

    CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(
            crf,
            new Optimizable.ByGradientValue[]{new CRFOptimizableByLabelLikelihood(
                    crf, instanceList)});

    for (int i = 1; i <= ITERATIONS; i++) {
      try {
        if (crfTrainer.train(instanceList, 1)) {
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return crf;
  }
}
