package util;

import cc.mallet.types.*;
import environmnet.Settings;

import java.io.*;

public class InstanceListUtil {

  public static BufferedWriter out = null;

  /**
   * Writes the given (train) instances to the gicen file.
   *
   * @param instanceList
   * @param file
   */
  public static void writeInstanceList(InstanceList instanceList, String file) {

    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), Settings.DEFAULT_ENCODING));
      for (Instance instance : instanceList) {
        FeatureVectorSequence data = (FeatureVectorSequence) instance.getData();
        LabelSequence target = (LabelSequence) instance.getTarget();

        String[] source = (String[]) instance.getSource();

        for (int j = 0; j < data.size(); j++) {
          writer.write((String) source[j] + " ");
          writer.write(data.get(j).toString(true));
          writer.write((String) target.get(j));
          writer.write("\n");
        }
        writer.write("\n");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

/*  *//**
   * Writes the given (test) instace to the given file.
   *
   * @param goldLabels
   * @param predicatedLabels
   * @param instance
   * @param crf
   * @param file
   *//*
  
  public static void writeInstanceList(String[] goldLabels, String[] predicatedLabels, Instance instance,
                                       MEMM memm, String file, Alphabet labelAlphabet) {
    try {
      if (out == null) {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                file), Settings.DEFAULT_ENCODING));
      }


      // iterates over the tokens
      for (int i = 0; i < predicatedLabels.length; ++i) {

        String token = ((String[]) instance.getSource())[i];
        out.write(token);

        int sourceStateIndex;
        if (i == 0) {
          sourceStateIndex = 0;
        } else {
          sourceStateIndex = memm.getState(predicatedLabels[i - 1]).getIndex();
        }

        int destStateIndex = memm.getState(predicatedLabels[i]).getIndex();

        // iterates over the features
        SparseVector features = ((FeatureVectorSequence) instance.getData())
                .get(i);
        for (int j = 0; j < features.numLocations(); ++j) {
          int index = features.indexAtLocation(j);
          String field = (String) labelAlphabet.lookupObject(index);
          int fieldIndex = labelAlphabet.lookupIndex(field);

          double value = memm.getParameter(sourceStateIndex, destStateIndex,
                  fieldIndex);

          if (Math.abs(value) > 0.1) {
            String valueString = String.format("%.2f", value);

            if (valueString != null && token != null) {
              out.write("\t" + field);
              out.write(" " + valueString);
            }
          }
        }
        out.write("\t" + predicatedLabels[i]);
        out.write("\t" + goldLabels[i]);
        out.write("\n");
      }
      out.write("\n");
      out.flush();
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
*/
}
