package memm;

import extractor.DefaultFeatureDescriptor;
import extractor.FeatureDescriptor;
import extractor.FeatureExtractor;
import util.io.IOBReader;

import java.util.*;

/**
 * Created by zsjanos on 2017.05.09..
 */
public class MEMMMultiTrain {


    /**
     * @param serializedFileName
     * @param tempDir
     */
    public void run(String trainFile, String serializedFileName, int labelIndex, String trainFileEncoding, String separator) {
        FeatureDescriptor new_fd = new DefaultFeatureDescriptor();
        MEMMModel memmMultiModel = new MEMMModel(null, new FeatureExtractor(new_fd.getFeatures()));
        incremental(memmMultiModel, trainFile, serializedFileName, labelIndex, trainFileEncoding, separator);
    }

    /**
     * @param memmMultiModel
     * @param serializedFileName
     * @param tempDir
     */
    public void incremental(MEMMModel memmModel, String trainFile, String serializedFileName, int labelIndex, String trainFileEncoding, String separator) {

        extractor.FeatureExtractor featureExtractor = memmModel.getFeatureExtractor();        

        List<List<String[]>> trainSentences = IOBReader.readIOB(trainFile, trainFileEncoding, separator);
        
        MEMMTrain memmTrain = new MEMMTrain(featureExtractor);
        int orig_numfeature = 0;
        MEMM orig = memmModel.getMemm();
        if(orig != null){
        	memmTrain.setFeatureAlphabet(orig.getAlphabet());
    		orig_numfeature = memmTrain.getFeatureAlphabet().size();
        }
        
        for (List<String[]> sentence : trainSentences) {
            String[] tokens = new String[sentence.size()];
            String[] labels = new String[sentence.size()];

            for (int i = 0; i < sentence.size(); ++i) {
                tokens[i] = sentence.get(i)[0];
                labels[i] = sentence.get(i)[labelIndex];
            }



            memmTrain.addSentence(tokens, labels);

        }
        
        if (memmTrain.getInstanceList().size() > 5) {
            MEMM memm = null;
            if (orig == null)
                memm = MaxEntTools.trainMEMM(memmTrain.getInstanceList());
            else
                memm = MaxEntTools.trainMEMMIncremental(orig, memmTrain.getInstanceList(),orig_numfeature);
            memmModel.setMemm(memm);
            memmModel.setFeatureAlphabet(memm.getAlphabet());
            memmModel.setLabelAlphabet(memm.getLabelAlphabet());
        }
        memmModel.serialize(serializedFileName);
        
//    	MEMMMultiSerializer.compressAndSerialize(memmMultiModel, serializedFileName);
    }

}