package extractor;

import environmnet.Settings;
import feature.Feature;
import reader.Reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by zsibritajanos on 2016.05.04..
 */
public class FeatureDescriptorReader {

  /**
   * @param file
   * @return
   */
  public static List<Feature> read(String file) {
    return read(file, "utf-8", "#", "$", Settings.PARAM_SEPARATOR);
  }

  /**
   * @param file
   * @param commentSign
   * @param separator
   * @return
   */
  public static List<Feature> read(String file, String encoding, String commentSign, String propSign, String separator) {

    /**
     *  prop
     */
    Properties properties = getProperties(file, encoding, propSign);
    // features
    List<Feature> features = new LinkedList<>();

    for (String line : Reader.readLines(file, encoding)) {
      // comment line
      if (line.trim().startsWith(commentSign) || line.trim().startsWith(propSign) || line.trim().length() == 0) {
        continue;
      }

      try {
        String className = Settings.FEATURE_PACKAGE + "." + line.substring(0, line.indexOf(separator)).trim();

        String params = line.substring(line.indexOf(separator)).trim();

        for (String property : properties.stringPropertyNames()) {
          params = params.replaceAll(Settings.PARAM_SEPARATOR + property + Settings.PARAM_SEPARATOR, Settings.PARAM_SEPARATOR + properties.getProperty(property) + Settings.PARAM_SEPARATOR);
        }

        for (String property : properties.stringPropertyNames()) {
          params = params.replaceAll(Settings.PARAM_SEPARATOR + property, Settings.PARAM_SEPARATOR + properties.getProperty(property));
        }

        Feature feature = (Feature) Class.forName(className).getConstructor(String.class).newInstance(params);

        if (feature != null) {
          features.add(feature);
        }

      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    Collections.sort(features);
    return features;
  }

  private static Properties getProperties(String file, String encoding, String propSign) {
    Properties properties = new Properties();

    //
    List<String> lines = Reader.readLines(file, encoding);

    StringBuffer stringBuffer = new StringBuffer();
    for (String line : lines) {
      line = line.trim();
      if (line.startsWith(propSign)) {
        stringBuffer.append(line.trim().substring(1) + '\n');
      }
    }

    try {
      properties.load(new ByteArrayInputStream(stringBuffer.toString().getBytes(encoding)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return properties;
  }

  public static void main(String[] args) {
    //read("./data/param/1.param");
    read("./data/param/rev.txt");
  }
}
