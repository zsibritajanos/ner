package test;

import cc.mallet.fst.CRF;
import crf.CRFTools;
import crf.CRFTrainer;
import crf.Model;
import environmnet.Settings;
import extractor.*;
import feature.Feature;
import fmeasure.FMeasure;
import mail.Mailer;
import ner.NamedEntityRecognizer;
import reader.Reader;
import util.InstanceListUtil;
import util.io.IOBReader;
import writer.Writer;

import java.util.List;

public class TestTrainer {

  private static final String TRAIN_FILE = "./data/hvg.conll.train";
  private static final String TEST_FILE = "./data/hvg.conll.test";

  private static double maxF = 0;

  private static void init(){

  }

  public static void run(String paramFile) {

    System.out.println(paramFile);

    // read the train file
    List<List<String[]>> trainSentences = IOBReader.readIOB(TRAIN_FILE,
            Settings.DEFAULT_ENCODING, " ");

    System.out.println("loading features from " + paramFile);
    List<Feature> features = extractor.FeatureDescriptorReader.read(paramFile);
    System.out.println("features loaded");

    System.out.println("loading featureDescriptor");
    FeatureExtractor featureExtractor = new FeatureExtractor(features);
    System.out.println("featureDescriptor loaded");

    CRFTrainer crfTrainer = new CRFTrainer(featureExtractor);
    long start = System.currentTimeMillis();
    System.err.println("feature extractor started");

    int c = 0;
    for (List<String[]> sentence : trainSentences) {
      String[] tokens = new String[sentence.size()];
      String[] labels = new String[sentence.size()];

      for (int i = 0; i < sentence.size(); ++i) {
        tokens[i] = sentence.get(i)[0];
        labels[i] = sentence.get(i)[1];
      }

      crfTrainer.addSentence(tokens, labels);

      if (++c % 1000 == 0) {
        System.err.println(c + "/" + trainSentences.size() + " in "
                + ((System.currentTimeMillis() - start) / (long) 60000) + " min");
      }
    }

    InstanceListUtil.writeInstanceList(crfTrainer.getInstanceList(),
            paramFile + ".instanceList.txt");

    start = System.currentTimeMillis();
    System.err.println("train started");
    CRF crf = CRFTools.train(crfTrainer.getInstanceList());
    System.err.println("trained in " + ((System.currentTimeMillis() - start) / (long) 60000) + " min");


    FeatureDescriptor featureDescriptor = new DefaultFeatureDescriptor();

    Model model = new Model(crf, featureDescriptor);

    model.serialize("hun.model");

    FMeasure fMeasure = new FMeasure();

    NamedEntityRecognizer ner = new NamedEntityRecognizer(features, crf);

    List<List<String[]>> testSentences = IOBReader.readIOB(TEST_FILE,
            Settings.DEFAULT_ENCODING, " ");

    for (List<String[]> sentence : testSentences) {

      String[] tokens = new String[sentence.size()];
      String[] labels = new String[sentence.size()];

      for (int i = 0; i < sentence.size(); ++i) {
        tokens[i] = sentence.get(i)[0];
        labels[i] = sentence.get(i)[1];
      }
      String[] predicated = ner.predicateSentence(tokens);
      fMeasure.addSentence(tokens, labels, predicated);
    }


    if (fMeasure.getAggregatedF() > maxF) {

      maxF = fMeasure.getAggregatedF();

      StringBuffer stringBuffer = new StringBuffer();

      for (String line : Reader.readLines(paramFile)) {
        stringBuffer.append(line + "\n");
      }

      Mailer.getInstance().send("zsibrita.janos@gmail.com", paramFile, stringBuffer.toString() + "\n\n" + fMeasure.toString());
    }

    Writer.writeStringToFile(fMeasure.toString(), paramFile + ".f");
  }

  public static void main(String[] args) {
    run("./data/descriptor/main.descriptor");
  }
}