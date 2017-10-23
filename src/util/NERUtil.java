package util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for feature extraction.
 *
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 */
public class NERUtil {

  private static final ClassLoader CLASS_LOADER = NERUtil.class
          .getClassLoader();


  /**
   * Tries to suit a String array (phrase) on a String array (sentence).
   *
   * @param sentence        array of the tokens of the sentence
   * @param phrase          array of the tokens of the phrase
   * @param startPos        starting position of the suit to try
   * @param isCaseSensitive case sensitivity of the suit
   * @return the index of the beginning of the suit
   */
  public static boolean suit(String[] sentence, List<String> phrase,
                             int startPos, boolean isCaseSensitive) {
    if (sentence.length - startPos < phrase.size()) {
      return false;
    }

    for (int i = startPos, j = 0; j < phrase.size(); i++, j++) {
      if ((isCaseSensitive && !phrase.get(j).equals(sentence[i]))
              || (!isCaseSensitive && !phrase.get(j).equalsIgnoreCase(sentence[i]))) {
        return false;
      }
    }
    return true;
  }



  /**
   * String representation of a list of string, with the specified token
   * separator.
   *
   * @param list      list of the tokens
   * @param separator token separatore
   * @return String of the tokens
   */
  public static String listAsString(List<String> list, String separator) {
    if (list.size() == 0) {
      return null;
    }

    if (list.size() == 1) {
      return list.get(0);
    }

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer(list.get(0));
    for (int i = 1; i < list.size(); ++i) {
      stringBuffer.append(separator);
      stringBuffer.append(list.get(i));
    }

    return stringBuffer.toString();
  }


  /**
   * Determines if the given String is punctuation.
   *
   * @param token raw String token
   * @return true if the given token is punctuation, false otherwise
   */
  public static boolean isPunctuation(String token) {
    for (char c : token.toCharArray()) {
      if (Character.isLetterOrDigit(c)) {
        return false;
      }
    }
    return true;
  }

  // ==============================



  public static boolean isNumber(String token) {
    for (char character : token.toCharArray()) {
      if (!Character.isDigit(character)) {
        return false;
      }
    }
    return true;
  }

  /**
   * String representation of a string array.
   *
   * @param array
   * @return
   */
  public static String arrayAsString(String[] array, String separator) {
    if (array.length == 0)
      return "";
    if (array.length == 1)
      return array[0];

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer(array[0]);
    for (int i = 1; i < array.length; ++i) {
      stringBuffer.append(separator);
      stringBuffer.append(array[i]);
    }

    return stringBuffer.toString();
  }

  /**
   * Split and compile the pattern phrases from the given lexicon.
   *
   * @param expressions lines of the pattern lexicon
   * @return array of the pattern phrases
   */
  public static Pattern[] getPatterns(Set<String> expressions,
                                      boolean isCaseSensitive) {
    List<Pattern> patterns = null;
    patterns = new ArrayList<Pattern>();

    // iterate over the expressions and compile them
    for (String expression : expressions) {
      if (!isCaseSensitive) {
        patterns.add(Pattern.compile(expression, Pattern.CASE_INSENSITIVE));
      } else {
        patterns.add(Pattern.compile(expression));
      }
    }

    return patterns.toArray(new Pattern[patterns.size()]);
  }

  /**
   * Recover the original string.
   *
   * @param sentence
   * @return
   */
  public static String unSplit(String[] sentence) {
    if (sentence.length == 0)
      return new String();

    if (sentence.length == 1)
      return sentence[0];

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer(sentence[0]);

    for (int i = 1; i < sentence.length; ++i) {
      if (sentence[i].length() > 0) {
        if (Character.isLetterOrDigit(sentence[i].charAt(0))) {
          stringBuffer.append(' ');
        }
      }
      stringBuffer.append(sentence[i]);
    }

    return stringBuffer.toString();
  }

  /**
   * Get the offsets of the splitted tokens.
   *
   * @param sentence
   * @return
   */
  public static int[][] getOffsets(String[] sentence) {
    if (sentence.length == 0)
      return new int[0][0];

    if (sentence.length == 1)
      return new int[][]{{0, sentence[0].length()}};

    int[][] offsets = new int[sentence.length][2];
    int offset = 0;
    int length = 0;

    offsets[0][0] = 0;
    length = sentence[0].length();
    offset += sentence[0].length();
    offsets[0][1] = offset;

    for (int i = 1; i < sentence.length; ++i) {

      if (sentence[i].length() > 0
              && Character.isLetterOrDigit(sentence[i].charAt(0))) {
        ++length;
        offsets[i][0] = offset + 1;
      } else {
        offsets[i][0] = offset;
      }
      length += sentence[i].length();
      offset = length;
      offsets[i][1] = offset;
    }

    return offsets;
  }

  /**
   * Distribute the integer values to the given distribution.
   *
   * @param values       values to distribute
   * @param distribution distribution
   * @return distributed values
   */
  public static int[] normalize(int[] values, int distribution) {
    int[] sorted = null;

    sorted = Arrays.copyOf(values, values.length);
    Arrays.sort(sorted);

    // offset
    for (int i = 0; i < values.length; ++i) {
      values[i] -= sorted[0];
    }

    // scale
    for (int i = 0; i < values.length; ++i) {
      values[i] = (int) (values[i] * distribution / sorted[sorted.length - 1]);
    }

    return values;
  }

  /**
   * Distribute the double values to the given distribution.
   *
   * @param values       values to distribute
   * @param distribution distribution
   * @return distributed values
   */
  public static int[] distribute(double[] values, int distribution) {
    double[] sorted = null;

    sorted = Arrays.copyOf(values, values.length);
    Arrays.sort(sorted);

    // offset
    for (int i = 0; i < values.length; ++i) {
      values[i] -= sorted[0];
    }

    // scale and cast to integer
    int[] distributed = null;
    distributed = new int[values.length];

    for (int i = 0; i < values.length; ++i) {
      distributed[i] = (int) (values[i] * (double) distribution / sorted[sorted.length - 1]);
    }

    return distributed;
  }

  /**
   * Simple distribution
   *
   * @param map
   * @param distribution
   * @return
   */
  public static Map<String, Integer> distribution(Map<String, Double> map,
                                                  int distribution) {

    Double[] sorted = null;
    sorted = map.values().toArray(new Double[map.values().size()]);

    Arrays.sort(sorted);

    Map<String, Integer> normalized = null;
    normalized = new HashMap<String, Integer>();

    for (Entry<String, Double> entry : map.entrySet()) {
      normalized.put(entry.getKey(), (int) ((entry.getValue() - sorted[0])
              * (double) distribution / sorted[sorted.length - 1]));

    }

    return normalized;

  }

  /**
   * Harmonic distribution
   *
   * @param map
   * @param distribution
   * @return
   */
  public static Map<String, Integer> harmonicDistribution(
          Map<String, Double> map, int distribution) {

    Double[] sorted = null;
    sorted = map.values().toArray(new Double[map.values().size()]);

    Arrays.sort(sorted);

    Map<String, Integer> normalized = null;
    normalized = new HashMap<String, Integer>();

    for (Entry<String, Double> entry : map.entrySet()) {
      normalized.put(entry.getKey(),
              (int) ((double) Arrays.binarySearch(sorted, entry.getValue())
                      / (sorted.length) * distribution));
    }

    return normalized;
  }

  /**
   * Reads a taxonomy from the specified file.
   *
   * @param file            name of the taxonomy file
   * @param isCaseSensitive case sensitivity
   * @param encoding        encoding if the file
   * @param separator       separator
   * @return taxonomy
   */
  public static Map<String, Set<String>> readTaxonomy(String file,
                                                      boolean isCaseSensitive, String encoding, String separator) {

    BufferedReader reader = null;
    String line = null;

    Map<String, Set<String>> taxonomy = null;
    taxonomy = new HashMap<String, Set<String>>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
              file), encoding));

      String[] splitted = null;
      while ((line = reader.readLine()) != null) {

        line = line.trim();

        if (!isCaseSensitive) {
          line = line.toLowerCase();
        }

        splitted = line.split(separator);

        // add key
        if (!taxonomy.containsKey(splitted[0])) {
          taxonomy.put(splitted[0], new HashSet<String>());
        }

        // collect the values
        Set<String> values = null;
        values = new HashSet<String>();
        for (int i = 1; i < splitted.length; ++i) {
          values.add(splitted[i]);
        }

        // add values
        taxonomy.get(splitted[0]).addAll(values);
      }

      reader.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return taxonomy;
  }

  public static boolean suitPhrase(String[] sentence, String[] phrase,
                                   int startPos) {

    if (startPos < 0) {
      return false;
    }

    if (sentence.length - startPos < phrase.length) {
      return false;
    }

    for (int i = startPos, j = 0; j < phrase.length; i++, j++) {
      if (!phrase[j].equals(sentence[i])) {
        return false;
      }
    }
    return true;
  }

  public static Map<String, List<String[]>> readPhrasseMap(String file,
                                                           String encoding, String separator, boolean isCaseSensitive) {

    BufferedReader reader = null;
    String line = null;

    Map<String, List<String[]>> phraseMap = null;
    phraseMap = new HashMap<String, List<String[]>>();

    String[] phrase = null;
    String[] value = null;

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
              file), encoding));

      String[] splitted = null;
      while ((line = reader.readLine()) != null) {

        line = line.trim();

        if (!isCaseSensitive) {
          line = line.toLowerCase();
        }

        splitted = line.split(separator);
        // TODO logger info if length < 2
        if (splitted.length > 1) {
          phrase = splitted[0].split(" ");
          // TODO try to parse + exception
          value = new String[phrase.length];
          for (int i = 1; i < phrase.length; ++i) {
            value[i - 1] = phrase[i];
          }
          value[phrase.length - 1] = splitted[1];

          if (!phraseMap.containsKey(phrase[0])) {
            phraseMap.put(phrase[0], new ArrayList<String[]>());
          }

          phraseMap.get(phrase[0]).add(value);
        }
      }

      reader.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return phraseMap;
  }

  @SuppressWarnings("unchecked")
  public static Set<String> phrasesToTokes(Set<String> phrases, int minTokenFreq) {

//    int minTokenLength = 3;
//    Map<String, Integer> map = null;
//    map = new HashMap<String, Integer>();
//
//    for (String phrase : phrases) {
//      for (List<String> sentence : ResourceHolder.getHunSplitter()
//              .split(phrase)) {
//        for (String token : sentence) {
//          if (token.length() >= minTokenLength) {
//            if (!map.containsKey(token)) {
//              map.put(token, 1);
//            } else {
//              map.put(token, map.get(token) + 1);
//            }
//          }
//        }
//      }
//    }
//
//    map = MapUtil.sortByValue(map);
//
//    for (Map.Entry<String, Integer> entry : map.entrySet()) {
//
//      System.err.println(entry.getKey());
//    }

    return null;
  }


  /**
   * Reads the specified file to a String with the specified encoding.
   *
   * @param file     name of the file
   * @param encoding encoding the file
   * @return content of the file
   */
  public static String readFileToString(String file, String encoding) {

    BufferedReader reader = null;

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    String line = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
              file), encoding));

      while ((line = reader.readLine()) != null) {
        stringBuffer.append(line + "\n");
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return stringBuffer.toString();
  }

  public static void writeSetToFile(Set<String> set, File file, String encoding) {
    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), encoding));
      for (String s : set) {
        writer.write(s + "\n");
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeStringToFile(String s, String file, String encoding) {
    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), encoding));
      writer.write(s);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int hungarianTokenLength(String possibleLemma) {
    int toSubstract = 0;
    Matcher m = Pattern.compile("([glnt]y|[ds]z|[cz]s|dzs)").matcher(
            possibleLemma.toLowerCase());
    while (m.find()) {
      toSubstract++;
    }
    return possibleLemma.length() - toSubstract;
  }

  public static String starterSubstring(String str, int n) {
    return str.substring(0, str.length() - n);
  }

  public static void writeMapToFile(Map<?, ?> map, String file,
                                    String separator, String encoding) {
    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), encoding));
      for (Entry<?, ?> entry : map.entrySet()) {
        writer.write(entry.getKey() + separator + entry.getValue() + "\n");
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
