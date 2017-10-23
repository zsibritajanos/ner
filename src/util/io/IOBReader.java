package util.io;

import environmnet.Settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IOBReader {

  public static List<List<String[]>> readIOB(String file, String encoding, String separator) {
    BufferedReader reader = null;
    String line;

    List<String[]> sentence = new ArrayList<>();

    List<List<String[]>> sentences = new ArrayList<>();

    String[] split;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

      while ((line = reader.readLine()) != null) {
        if (!line.contains(Settings.DOC_SEPARATOR)) {
          split = line.split(separator);
          if (split.length > 1) {
            sentence.add(split);
          } else if (!sentence.isEmpty()) {
            sentences.add(sentence);
            sentence = new ArrayList<>();
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sentences;
  }
}
