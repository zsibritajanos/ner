package memm;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.InstanceList;
import extractor.FeatureExtractor;

import java.util.Arrays;
import java.util.List;

//import environmnet.Env;
//import environmnet.Settings;
//import util.DevSet;
//import util.NERUtil;
//import util.RankerUtils;
//import util.io.SpecIOBReader;

/**
 * Predicates the given test file via the specified trained MEMM model.
 */
public class MEMMTest {

	private Alphabet labelAlphabet = null;
	private Alphabet featureAlphabet = null;
	private MEMM memm = null;
	private FeatureExtractor featureExtractor = null;

//	public MEMMTest(InstanceList trainInstanceList,
//			LineFeatureExtractor lineFeatureExtractor, MEMM memm) {
//
//		this.setFeatureExtractor(featureExtractor);
//		this.setMemm(memm);
//	}

	public MEMMTest(MEMMModel memmModel) {
		this.setFeatureAlphabet(memmModel.getFeatureAlphabet());
		this.setLabelAlphabet(memmModel.getLabelAlphabet());
		this.setFeatureExtractor(memmModel.getFeatureExtractor());
		this.setMemm(memmModel.getMemm());
	}

//	public MEMMTest(String memmModel) {
////		this((MEMMModel) Util.restore(memmModel));
//
//		this((MEMMModel) NERUtil.restore(memmModel));
//	}

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

//  public InstanceList documentToInstaceList(String[] lines, String[] formattings) {
//
//    InstanceList instanceList = null;
//
//    FeatureVectorSequence featureVectorSequence = null;
//
//    // extracted features
//    List<List<String>> features = featureExtractor.extractFeatures(lines);
//
//    featureVectorSequence = InstaceListTools.featuresToFeatureVectorSequence(
//        features, this.getFeatureAlphabet(), false);
//
//    instanceList = InstaceListTools.doInstanceList(this.getMemm(),
//        featureVectorSequence, Arrays.asList(lines));
//
//    return instanceList;
//  }
	public InstanceList sentenceToInstanceList(String[] sentence) {
		InstanceList instanceList = null;

		FeatureVectorSequence featureVectorSequence = null;
//		String[] sent = RankerUtils.getColumn(sentence, 0);
		List<List<String>> features = featureExtractor.extractFeatures(sentence);

		featureVectorSequence = InstaceListTools.featuresToFeatureVectorSequence(
				features, this.getFeatureAlphabet(), false);

		instanceList = InstaceListTools.doInstanceList(this.getMemm(),
				featureVectorSequence, Arrays.asList(sentence));

		return instanceList;
	}

//	public void fscore(String testFile, String output) {
//		List<List<String[]>> test = SpecIOBReader.readIOB(testFile, Settings.DEFAULT_ENCODING, Settings.SEPARATOR);
//		HivFMeasure fm = new HivFMeasure();
//
////		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output+".wil", false)))) {
//		for (List<String[]> sentence : test) {
//			String[] tokens = RankerUtils.getColumn(sentence, 0);
//			String[] etalon = RankerUtils.getColumn(sentence, 1);
//
//			InstanceList instanceList = sentenceToInstanceList(tokens);
//
//			List<String[]> kBestPrediction = MaxEntTools.predictNBest(instanceList, memm, Env.K);
//
////				for (int i = 0; i < instanceList.size(); ++i) {
////					instanceList.get(i).unLock();
////					instanceList.get(i).setTarget(labels.get(i));
////				}
////
////				InstaceListTools.writePlainInstanceListWithFeatureWeights(instanceList, this.getMemm(), writer);
//			fm.addBestSentence(tokens, etalon, kBestPrediction);
//		}
//
////			writer.close();
////		} catch (IOException ex) {
////			Logger.getLogger(MEMMTest.class.getName()).log(Level.SEVERE, null, ex);
////		}
////		System.err.println("\nweighted instanceList saved: " + output
////				+ ".weightedInstanceList");
//		fm.printFMeasureToFile(output + "-" + Env.K);
//	}

//	/**
//	 * Evalutes the the given testFile.
//	 */
//	public void eval(String testFile, String output) {
//		List<List<String[]>> test = SpecIOBReader.readIOB(testFile, Settings.DEFAULT_ENCODING, Settings.SEPARATOR);
//		HivFMeasure fm = new HivFMeasure();
//
//		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output + ".wil", false)))) {
//			for (List<String[]> sentence : test) {
//				String[] tokens = RankerUtils.getColumn(sentence, 0);
//				String[] etalon = RankerUtils.getColumn(sentence, 1);
//
//				InstanceList instanceList = sentenceToInstanceList(tokens);
//
//				List<String> labels = MaxEntTools.predict(instanceList, this.getMemm());
//				String[] labelsS = labels.toArray(new String[labels.size()]);
//
//				for (int i = 0; i < instanceList.size(); ++i) {
//					instanceList.get(i).unLock();
//					instanceList.get(i).setTarget(labels.get(i));
//				}
//
//				InstaceListTools.writePlainInstanceListWithFeatureWeights(instanceList, this.getMemm(), writer);
//
//				fm.addSentence(tokens, etalon, labelsS);
//			}
//
//			writer.close();
//		} catch (IOException ex) {
//			Logger.getLogger(MEMMTest.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		System.err.println("\nweighted instanceList saved: " + output
//				+ ".weightedInstanceList");
//
//		fm.printFMeasureToFile(output);
//	}
//    List<List<String>> test = Train.read(testFile);
//
//    String[] goldLabels = null;
//    String[] formattings = null;
//    String[] lines = null;
//
//    Writer writer = null;
//
//    try {
//      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
//          testFile + ".weightedInstanceList"), "utf-8"));
//    } catch (UnsupportedEncodingException | FileNotFoundException e) {
//      e.printStackTrace();
//    }
//
//    FMeasure fMeasure = new FMeasure();
//    FMeasure docFMeasure = null;
//
//    HTMLWriter htmlWriter = new HTMLWriter(testFile + ".html");
//    int docCounter = 0;
//    for (List<String> document : test) {
//      docFMeasure = new FMeasure();
//      System.err.println("\n" + ++docCounter + "/" + test.size());
//
//      lines = Train.getLinesWithoutAnnotation(document);
//      goldLabels = Train.getLabels(document);
//      formattings = Train.getFormattings(document);
//
//      InstanceList documentInstanceList = documentToInstaceList(lines,
//          formattings);
//
//      // predicated
//      List<String> labels = MaxEntTools.predict(documentInstanceList,
//          this.getMemm());
//
//      fMeasure.addSentence(lines, goldLabels,
//          labels.toArray(new String[labels.size()]));
//
//      docFMeasure.addSentence(lines, goldLabels,
//          labels.toArray(new String[labels.size()]));
//
//      // set the predicated values
//      for (int i = 0; i < documentInstanceList.size(); i++) {
//        documentInstanceList.get(i).unLock();
//        documentInstanceList.get(i).setTarget(labels.get(i));
//      }
//
//      // writes the weighted list
//      InstaceListTools.writePlainInstanceListWithFeatureWeights(
//          documentInstanceList, memm, writer);
//
//      htmlWriter.write(null, goldLabels,
//          labels.toArray(new String[labels.size()]), lines,
//          docFMeasure.toString());
//    }
//
//    try {
//      writer.close();
//      htmlWriter.close(fMeasure.toString());
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    System.err.println(fMeasure.toString());
//
//    // log
//    System.err.println("\nweighted instanceList saved: " + testFile
//        + ".weightedInstanceList");
//    // log
//    System.err.println("HTML saved: " + testFile + ".html");
//	}

	public List<String[]> getPrediction(String[] sentence, int k) {
		InstanceList instanceList = sentenceToInstanceList(sentence);

		return MaxEntTools.predictNBest(instanceList, this.getMemm(), k);
	}

//	/**
//	 * Evalutes the the given testFile.
//	 */
//	public void predict(String testFile, String output) {
//		List<List<String[]>> test = SpecIOBReader.readIOB(testFile, Settings.DEFAULT_ENCODING, Settings.SEPARATOR);
//
//		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output, false)))) {
//			for (List<String[]> sentence : test) {
//				String[] sent = RankerUtils.getColumn(sentence, 0);
//				InstanceList instanceList = sentenceToInstanceList(sent);
//
//				List<String> labels = MaxEntTools.predict(instanceList, this.getMemm());
//
//				for (int i = 0; i < instanceList.size(); ++i) {
//					instanceList.get(i).unLock();
//					instanceList.get(i).setTarget(labels.get(i));
//				}
//
//				InstaceListTools.writePlainInstanceListWithFeatureWeights(instanceList, this.getMemm(), writer);
//			}
//
//			writer.close();
//		} catch (IOException ex) {
//			Logger.getLogger(MEMMTest.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		System.err.println("\nweighted instanceList saved: " + testFile
//				+ ".weightedInstanceList");

//    List<List<String>> test = Train.read(testFile);
//
//    String[] formattings = null;
//    String[] lines = null;
//
//    Writer writer = null;
//
//    try {
//      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
//          testFile + ".weightedInstanceList"), "utf-8"));
//    } catch (UnsupportedEncodingException | FileNotFoundException e) {
//      e.printStackTrace();
//    }
//
//    int docCounter = 0;L
//    for (List<String> document : test) {
//      System.err.println("\n" + ++docCounter + "/" + test.size());
//
//      lines = Train.getLinesWithoutAnnotation(document);
//      formattings = Train.getFormattings(document);
//
//      InstanceList documentInstanceList = documentToInstaceList(lines,
//          formattings);
//
//      // predicated
//      List<String> labels = MaxEntTools.predict(documentInstanceList,
//          this.getMemm());
//
//      // set the predicated values
//      for (int i = 0; i < documentInstanceList.size(); i++) {
//        documentInstanceList.get(i).unLock();
//        documentInstanceList.get(i).setTarget(labels.get(i));
//      }
//
//      // writes the weighted list
//      InstaceListTools.writePlainInstanceListWithFeatureWeights(
//          documentInstanceList, memm, writer);
//    }
//
//    try {
//      writer.close();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    // log
//    System.err.println("\nweighted instanceList saved: " + testFile
//        + ".weightedInstanceList");
//	}
}
