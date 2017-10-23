package util.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import environmnet.Settings;

public class IOBWriter {
  public static void WriteIOB(List<List<String[]>> iob, String file,
      String encoding) {

    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), Settings.DEFAULT_ENCODING));

      for (List<String[]> sentence : iob) {
        for (String[] token : sentence) {

          writer.write(token[0]);
          for (int i = 1; i < token.length; ++i) {
            writer.write('\t' + token[i]);
          }
          writer.write('\n');
        }
        writer.write('\n');
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
}
