package util;

import reader.Reader;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class HungarianGuesser implements Serializable {

  private static final long serialVersionUID = 1L;
  private Set<String> suffices = null;
  public final static String TOKEN_SEPARATORS = " ,.;:_()[]/!?\\\t\n\"'+=€$&#{}";

  public HungarianGuesser(String file) {
    this.suffices = Reader.readSet(file, "utf-8", true);
  }

  public Set<String> getPossibleLemmas(String word) {
    Set<String> lemmas = new TreeSet<>();
    lemmas.add(word);

    if (word.lastIndexOf("-") > 0
            && suffices.contains((word.substring(word.lastIndexOf("-") + 1)
            .toLowerCase()))) {
      lemmas.add(word.substring(0, word.lastIndexOf("-")));
      return lemmas;
    }

    for (String suffix : suffices) {
      if (word.toLowerCase().endsWith(suffix)) {
        String lemma;
        if (NERUtil.hungarianTokenLength(lemma = NERUtil.starterSubstring(word,
                suffix.length())) > 2) {
          lemmas.add(lemma);
          String mod = getModifiedLemmas(lemma);
          if (mod != null)
            lemmas.add(mod);
        }
      }
    }

    return lemmas;
  }

  protected static String getModifiedLemmas(String lemma) {
    // hasonulások kezelése
    if (lemma.endsWith("nny"))
      return NERUtil.starterSubstring(lemma, 3) + "ny";
    else if (lemma.endsWith("nny"))
      return NERUtil.starterSubstring(lemma, 3) + "ny";
    else if (lemma.endsWith("ggy"))
      return NERUtil.starterSubstring(lemma, 3) + "gy";
    else if (lemma.endsWith("tty"))
      return NERUtil.starterSubstring(lemma, 3) + "ty";
    else if (lemma.endsWith("ssz"))
      return NERUtil.starterSubstring(lemma, 3) + "sz";
    else if (lemma.endsWith("ccs"))
      return NERUtil.starterSubstring(lemma, 3) + "cs";
    else if (lemma.endsWith("zzs"))
      return NERUtil.starterSubstring(lemma, 3) + "zs";
    else if (lemma.endsWith("ddz"))
      return NERUtil.starterSubstring(lemma, 3) + "dz";
    else if (lemma.endsWith("ddzs"))
      return NERUtil.starterSubstring(lemma, 4) + "dzs";
      // Oslóban -> Oslo
    else if (lemma.endsWith("ó"))
      return NERUtil.starterSubstring(lemma, 1) + "o";
    else if (lemma.endsWith("ú"))
      return NERUtil.starterSubstring(lemma, 1) + "u";
    else if (lemma.endsWith("í"))
      return NERUtil.starterSubstring(lemma, 1) + "i";
    else if (lemma.endsWith("ő"))
      return NERUtil.starterSubstring(lemma, 1) + "ö";
    else if (lemma.endsWith("ű"))
      return NERUtil.starterSubstring(lemma, 1) + "ü";
    else if (lemma.endsWith("á"))
      return NERUtil.starterSubstring(lemma, 1) + "a";
    else if (lemma.endsWith("é"))
      return NERUtil.starterSubstring(lemma, 1) + "e";

    if (lemma.length() > 3) {
      String ending = lemma.substring(lemma.length() - 3);
      if (ending.matches(".*[eiaüuoö].*")) {
        ending = ending.replace('e', 'é');
        ending = ending.replace('i', 'í');
        ending = ending.replace('a', 'á');
        ending = ending.replace('ü', 'ű');
        ending = ending.replace('u', 'ú');
        ending = ending.replace('ö', 'ő');
        ending = ending.replace('o', 'ó');
        return NERUtil.starterSubstring(lemma, 3) + ending;
      }
    }
    return null;
  }

  protected static String getModifiedForm(String text) {
    // hasonulások kezelése
    if (text.endsWith("ny"))
      return NERUtil.starterSubstring(text, 2) + "nny";
    if (text.endsWith("gy"))
      return NERUtil.starterSubstring(text, 2) + "ggy";
    if (text.endsWith("ty"))
      return NERUtil.starterSubstring(text, 2) + "tty";
    if (text.endsWith("cs"))
      return NERUtil.starterSubstring(text, 2) + "ccs";
    if (text.endsWith("sz"))
      return NERUtil.starterSubstring(text, 2) + "ssz";
    if (text.endsWith("zs"))
      return NERUtil.starterSubstring(text, 2) + "zzs";
    if (text.endsWith("dz"))
      return NERUtil.starterSubstring(text, 2) + "ddz";
    if (text.endsWith("dzs"))
      return NERUtil.starterSubstring(text, 3) + "ddzs";

    if (text.endsWith("o"))
      return NERUtil.starterSubstring(text, 1) + "ó";
    if (text.endsWith("ö"))
      return NERUtil.starterSubstring(text, 1) + "ő";
    if (text.endsWith("u"))
      return NERUtil.starterSubstring(text, 1) + "ú";
    if (text.endsWith("ü"))
      return NERUtil.starterSubstring(text, 1) + "ű";
    if (text.endsWith("a"))
      return NERUtil.starterSubstring(text, 1) + "á";
    if (text.endsWith("i"))
      return NERUtil.starterSubstring(text, 1) + "í";
    if (text.endsWith("e"))
      return NERUtil.starterSubstring(text, 1) + "é";

    if ((text.length() > 3)
            && (text.substring(text.length() - 3).matches("[éíáűúőó]"))) {
      String ending = text.substring(text.length() - 3);
      ending.replace('é', 'e');
      ending.replace('í', 'i');
      ending.replace('á', 'a');
      ending.replace('ű', 'ü');
      ending.replace('ú', 'u');
      ending.replace('ő', 'ö');
      ending.replace('ó', 'o');
      return NERUtil.starterSubstring(text, 3) + ending;
    }
    return null;
  }

  public static void main(String[] args) {

    HungarianGuesser hungarianGuesser = null;
    hungarianGuesser = new HungarianGuesser("./data/suffix/hu.suffix");

    System.err.println(hungarianGuesser.getPossibleLemmas("EU-hoz"));
    System.err.println(hungarianGuesser.getPossibleLemmas("Kína"));
  }
}
