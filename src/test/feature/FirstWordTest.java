package test.feature;

import feature.Feature;
import feature.FirstWord;

import java.util.List;
import java.util.Set;

/**
 * Created by zsibritajanos on 2016.05.25..
 */
public class FirstWordTest {

  public static void main(String[] args) {
    Feature feature = new FirstWord("simple true false 2 true");
    String[] sentence = "This sentence is the example .".split(" ");

    List<Set<String>> values = feature.process(sentence);

    for (int i = 0; i < sentence.length; ++i) {
      System.out.println(sentence[i] + '\t' + values.get(i));
    }
  }
}
