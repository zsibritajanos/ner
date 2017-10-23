package memm;

import cc.mallet.types.*;
import extractor.FeatureExtractor;

import java.util.Arrays;
import java.util.List;

//import relatedworks.LineFeatureExtractor;
//import relatedworks.Train;
//import util.Util;
//import environmnet.Settings;
//import extractor.FeatureExtractor;
//import util.InstanceListUtil;

/**
 * Class for triaining MEMM via the given train file. One documentum will be one
 * instance.
 */
public class MEMMTrain {

	// labels
	private Alphabet labelAlphabet = null;
	// features
	private Alphabet featureAlphabet = null;
	// train instances
	private InstanceList instanceList = null;

	// feature extactor
	private FeatureExtractor featureExtractor = null;

	/**
	 * Constuctor, initilizes the alphabets, the feature extactor and the
	 * instance collector.
	 */
	public MEMMTrain(FeatureExtractor featureExtractor) {
		this.setFeatureAlphabet(new Alphabet());
		this.getFeatureAlphabet().startGrowth();

		this.setLabelAlphabet(new LabelAlphabet());
		this.getLabelAlphabet().startGrowth();

		this.setFeatureExtractor(featureExtractor);

		this.setInstanceList(new InstanceList(this.getFeatureAlphabet(), this
				.getLabelAlphabet()));
	}

	/**
	 * Adds a document to the train set.
	 *
	 * @param lines lines of the document
	 * @param labels labels of the lines
	 * @param formattings formattings (extracted from the pdf)
	 */
	private void addDocument(String[] lines, String[] labels, String[] formattings) {
		if (lines.length > 0 && labels.length > 0 && labels.length == lines.length) {

			LabelSequence labelSequence = null;
			FeatureVectorSequence featureVectorSequence = null;

			// extracted features
			List<List<String>> features = featureExtractor.extractFeatures(lines);
//			List<List<String>> features = featureExtractor.extract(lines,
//					formattings);

			// features to fvs
			featureVectorSequence = InstaceListTools.featuresToFeatureVectorSequence(
					features, featureAlphabet, true);

			// labels
			labelSequence = new LabelSequence(labelAlphabet);
			for (String label : labels) {
				labelSequence.add(label);
			}

			// instance via the given document
			Instance instance = new Instance(featureVectorSequence, labelSequence,
					null, Arrays.asList(lines));

			this.getInstanceList().add(instance);
		}
	}

	public void addSentence(String[] sentence, String[] labels) {
		if (sentence.length > 0 && labels.length > 0 && labels.length == sentence.length) {
			FeatureVectorSequence featureVectorSequence = null;
			LabelSequence labelSequene = null;

			List<List<String>> features = featureExtractor.extractFeatures(sentence);

			featureVectorSequence = InstaceListTools.featuresToFeatureVectorSequence(features, featureAlphabet, true);

			labelSequene = new LabelSequence(labelAlphabet);
			for (String label : labels) {
				labelSequene.add(label);
			}

			Instance instance = null;
			instance = new Instance(featureVectorSequence, labelSequene, null, Arrays.asList(sentence));

			this.getInstanceList().add(instance);
		}
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

	public InstanceList getInstanceList() {
		return instanceList;
	}

	public void setInstanceList(InstanceList instanceList) {
		this.instanceList = instanceList;
	}

//	public void trainTest(String trainFile)
//	{
//		List<List<String>> train = Train.read(trainFile);
//
//		String[] labels = null;
//		String[] formattings = null;
//		String[] lines = null;
//
//		int docCounter = 0;
//		for (List<String> document : train) {
//			System.err.println("\n" + ++docCounter + "/" + train.size());
//			labels = Train.getLabels(document);
//			formattings = Train.getFormattings(document);
//			lines = Train.getLinesWithoutAnnotation(document);
//			this.addDocument(lines, labels, formattings);
//		}
//
//		MEMM memm = MaxEntTools.trainMEMM(this.getInstanceList());
//
//		InstaceListTools.writePlainInstanceList(this.getInstanceList(), trainFile
//				+ ".instanceList", Settings.DEFAULT_ENCODING);
//
//		// log
//		System.err.println("train instanceList saved: " + trainFile
//				+ ".instanceList");
//
//		MEMMModel memmModel = new MEMMModel(memm, this.getFeatureAlphabet(),
//				this.getLabelAlphabet(), this.getLineFeatureExtractor());
//
//		Util.serialize(memmModel, trainFile + ".memm.model");
//
//		System.err.println("MEMM model saved: " + trainFile + ".memm.model");
//		
//	}
	
	public void train(String trainFile) {

//		List<List<String>> train = Train.read(trainFile);
//
//		String[] labels = null;
//		String[] formattings = null;
//		String[] lines = null;
//
//		int docCounter = 0;
//		for (List<String> document : train) {
//			System.err.println("\n" + ++docCounter + "/" + train.size());
//			labels = Train.getLabels(document);
//			formattings = Train.getFormattings(document);
//			lines = Train.getLinesWithoutAnnotation(document);
//			this.addDocument(lines, labels, formattings);
//		}
//
//		MEMM memm = MaxEntTools.trainMEMM(this.getInstanceList());
//
//		InstaceListTools.writePlainInstanceList(this.getInstanceList(), trainFile
//				+ ".instanceList", Settings.DEFAULT_ENCODING);
//
//		// log
//		System.err.println("train instanceList saved: " + trainFile
//				+ ".instanceList");
//
//		MEMMModel memmModel = new MEMMModel(memm, this.getFeatureAlphabet(),
//				this.getLabelAlphabet(), this.getLineFeatureExtractor());
//
//		Util.serialize(memmModel, trainFile + ".memm.model");
//
//		System.err.println("MEMM model saved: " + trainFile + ".memm.model");
	}

	public void train(List<List<String[]>> trainSentences, String output) {
		int c = 0;

		long start = System.currentTimeMillis();
		System.err.println("feature extractor started");
		for (List<String[]> sentence : trainSentences) {
			String[] tokens = new String[sentence.size()];
			String[] labels = new String[sentence.size()];

			for (int i = 0; i < sentence.size(); ++i) {
				tokens[i] = sentence.get(i)[0];
				labels[i] = sentence.get(i)[1];
			}

			this.addSentence(tokens, labels);

			if (++c % 1000 == 0) {
				System.err.println(c + "/" + trainSentences.size() + " in "
						+ ((System.currentTimeMillis() - start) / (long) 60000) + " min");
			}
		}
		
//		InstanceListUtil.writeInstanceList(this.getInstanceList(),
//				"./instanceList.txt");
		
		start = System.currentTimeMillis();
		System.err.println("train started");

		MEMM memm = MaxEntTools.trainMEMM(this.getInstanceList());
		
		MEMMModel memmModel = new  MEMMModel(memm, this.getFeatureAlphabet(),
				this.getLabelAlphabet(), this.getFeatureExtractor());
		
//		Util.serialize(memmModel, output + ".memm.model");

		
		System.err.println(("\ntrained in " + (System.currentTimeMillis() - start)
				/ (long) 60000)
				+ " min");		
	}
}
