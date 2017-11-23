package main;


import environmnet.Settings;
import fmeasure.FMeasure;
import memm.MEMMMultiTrain;
import ner.NamedEntityRecognizer;
import splitter.MySplitter;
import util.io.IOBReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    /**
     * @param file
     * @param encoding
     * @param separator
     * @param wordIndex
     */
    public static FMeasure eval(String file, String encoding, String separator, int wordIndex, int goldLabelIndex, int predicatedLabelIndex) {

        FMeasure fMeasure = new FMeasure();

        List<List<String[]>> sentences = IOBReader.readIOB(file, encoding, separator);

        for (List<String[]> sentence : sentences) {

            String[] tokens = new String[sentence.size()];
            String[] goldLabels = new String[sentence.size()];
            String[] predicatedLabels = new String[sentence.size()];

            for (int i = 0; i < sentence.size(); ++i) {
                tokens[i] = sentence.get(i)[wordIndex];
                goldLabels[i] = sentence.get(i)[goldLabelIndex];
                predicatedLabels[i] = sentence.get(i)[predicatedLabelIndex];
            }

            fMeasure.addSentence(tokens, goldLabels, predicatedLabels);
        }

        return fMeasure;
    }

    /**
     * @param file
     * @param encoding
     * @param model
     * @param output
     */
    public static void parse(String file, String encoding, String model, String output) {

        String content = reader.Reader.read(file, encoding);

        List<List<String>> sentences = MySplitter.getInstance().split(content);

        NamedEntityRecognizer ner = new NamedEntityRecognizer(model);

        Writer writer = null;

        if (output != null) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), encoding));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (List<String> sentence : sentences) {

            String[] tokens = new String[sentence.size()];

            for (int i = 0; i < sentence.size(); ++i) {
                tokens[i] = sentence.get(i);
            }

            String[] predicatedLabels = ner.predicateSentence(tokens);

            if (output != null) {
                try {
                    for (int i = 0; i < tokens.length; ++i) {
                        writer.write(tokens[i] + Settings.DEFAULT_SEPARATOR + predicatedLabels[i] + '\n');
                    }
                    writer.write('\n');

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (output != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param model
     * @param file
     * @param encoding
     * @param separator
     * @param wordIndex
     * @param instance
     * @param output
     */

    public static void predicate(String model, String file, String encoding, String separator, int wordIndex, String instance, String output) {
        predicate(model, file, encoding, separator, wordIndex, -1, instance, output);
    }

    public static void predicate(String model, String file, String encoding, String separator, int wordIndex, int labelIndex, String instance, String output) {

        Writer writer = null;

        if (output != null) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), encoding));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NamedEntityRecognizer ner = new NamedEntityRecognizer(model);

        List<List<String[]>> testSentences = IOBReader.readIOB(file, encoding, separator);

        String[] tokens;

        String[] labels = null;

        String[] predicated;

        for (List<String[]> sentence : testSentences) {

            tokens = new String[sentence.size()];
            if (labelIndex != -1) {
                labels = new String[sentence.size()];
            }

            for (int i = 0; i < sentence.size(); ++i) {
                tokens[i] = sentence.get(i)[wordIndex];
                if (labels != null) {
                    labels[i] = sentence.get(i)[labelIndex];
                }
            }

            predicated = ner.predicateSentence(tokens, labels, instance);

            if (output != null) {
                try {
                    for (int i = 0; i < tokens.length; ++i) {
                        writer.write(tokens[i] + separator);
                        if (labels != null) {
                            writer.write(labels[i] + separator);
                        }
                        writer.write(predicated[i] + '\n');
                    }
                    writer.write('\n');

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (output != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param trainFile                input (train) file
     * @param trainFileEncoding        encoding of the input (train) file
     * @param trainFileColumnSeparator column separator in the input (train) file
     * @param trainTokenColumnIndex    index of the token column in the  input (train) file
     * @param trainLabelColumnIndex    index of the label column in the  input (train) file
     * @param modelFile                (trained) model file
     * @param instanceListFile         instance list (for debugging)
     */
    public static void train(String trainFile, String trainFileEncoding, String trainFileColumnSeparator, int trainTokenColumnIndex, int trainLabelColumnIndex, String paramFile, String instanceListFile, String modelFile) {

        // reads the train sentences

//    CRFTrainer crfTrainer = new CRFTrainer(featureExtractor);
        MEMMMultiTrain memmTrainer = new MEMMMultiTrain();

        long start = System.currentTimeMillis();

        memmTrainer.run(trainFile, modelFile, trainLabelColumnIndex, trainFileEncoding, trainFileColumnSeparator);

        System.out.println(("\ntrained in " + (System.currentTimeMillis() - start) / (long) 60000) + " min");
        System.out.println("model saved: " + modelFile);
    }

    /**
     * @param trainFile
     * @param trainFileEncoding
     * @param trainFileColumnSeparator
     * @param trainTokenColumnIndex
     * @param trainLabelColumnIndex
     * @param paramFile
     * @param modelFile
     */
    public static void train(String trainFile, String trainFileEncoding, String trainFileColumnSeparator, int trainTokenColumnIndex, int trainLabelColumnIndex, String paramFile, String modelFile) {
        train(trainFile, trainFileEncoding, trainFileColumnSeparator, trainTokenColumnIndex, trainLabelColumnIndex, paramFile, null, modelFile);
    }

    public static void main(String[] args) {

        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            params.put(args[i], args[i + 1]);
        }

        System.out.println(params);

        if (params.containsKey("-mode")) {
            switch (params.get("-mode")) {

                case "train":
                    if (params.containsKey("-t") && params.containsKey("-e") && params.containsKey("-s") &&
                            params.containsKey("-w") && params.containsKey("-l") && params.containsKey("-p") &&
                            params.containsKey("-m")) {
                        /**
                         * train instance log file
                         */
                        String instance = params.containsKey("-i") ? params.get("-i") : null;

                        Main.train(params.get("-t"), params.get("-e"), String.valueOf((char) Integer.parseInt(params.get("-s"))),
                                Integer.parseInt(params.get("-w")), Integer.parseInt(params.get("-l")), params.get("-p"), instance, params.get("-m"));
                    } else {
                        System.out.println("USAGE: -mode train -t train_file -e train_file_encoding -s separator_ASCII -w word_index -l label_index -p param_file [-i instance_log_file] -m model_file");
                    }
                    break;

                /**
                 * predicates tokenized file
                 */
                case "predicate":
                    if (params.containsKey("-m") && params.containsKey("-t") && params.containsKey("-e") && params.containsKey("-s")
                            && params.containsKey("-w") && params.containsKey("-p")) {

                        /**
                         * instance log file
                         */
                        String instance = params.containsKey("-i") ? params.get("-i") : null;

                        /**
                         * instance log file
                         */
                        int labelIndex = params.containsKey("-l") ? Integer.parseInt(params.get("-l")) : -1;

                        Main.predicate(params.get("-m"), params.get("-t"), params.get("-e"), String.valueOf((char) Integer.parseInt(params.get("-s"))),
                                Integer.parseInt(params.get("-w")), labelIndex, instance, params.get("-p"));

                    } else {
                        System.out.println("USAGE: -mode predicate -m model -t test_file -e encoding -s separator_ASCII [space: 32 tab: 9] -w word_index [-l label index] [-i instance_log_file] -p predicated_file");
                    }

                    break;

                case "eval":
                    if (params.containsKey("-p") && params.containsKey("-e") && params.containsKey("-s") && params.containsKey("-w") && params.containsKey("-g") && params.containsKey("-l")) {

                        /**
                         *result file
                         */
                        String resultFile = params.containsKey("-r") ? params.get("-r") : null;

                        FMeasure fMeasure = Main.eval(params.get("-p"), params.get("-e"), String.valueOf((char) Integer.parseInt(params.get("-s"))),
                                Integer.parseInt(params.get("-w")), Integer.parseInt(params.get("-g")), Integer.parseInt(params.get("-l")));

                        if (resultFile == null) {
                            System.out.println(fMeasure);
                        } else {
                            writer.Writer.writeStringToFile(fMeasure.toString(), resultFile, Settings.DEFAULT_ENCODING);
                        }

                    } else {
                        System.out.println("USAGE: -mode eval -p predicated_file -e encoding -s separator -w word_index -g gold_label_index -l predicated_label_index [-r result_file]");
                    }
                    break;

                case "parse":
                    if (params.containsKey("-f") && params.containsKey("-e") && params.containsKey("-o")) {
                        Main.parse(params.get("-f"), params.get("-e"), params.get("-m"), params.get("-o"));
                    } else {
                        System.out.println("USAGE: -mode parse -f file -e encoding -m model -o output");
                    }
                    break;
            }
        } else {
            System.out.println("USAGE: -mode [train|predicate|eval|parse]");
        }
    }
}
