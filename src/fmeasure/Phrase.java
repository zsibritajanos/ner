package fmeasure;

public class Phrase implements Comparable<Phrase> {

  public int begin;
  public int end;
  public String label;

  /**
   * @param begin
   * @param end
   * @param label
   */
  public Phrase(int begin, int end, String label) {
    this.begin = begin;
    this.end = end;
    this.label = label;
  }

  /**
   * @return
   */
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(String.valueOf(this.begin));
    stringBuffer.append("\t" + this.end);
    stringBuffer.append("\t" + this.label);
    return stringBuffer.toString();
  }

  /**
   * Compares two phrases.
   */
  public int compareTo(Phrase phrase) {
    if (this.begin > phrase.begin) {
      return 1;
    }

    if (this.begin == phrase.begin && this.end == phrase.end
            && this.label.equals(phrase.label)) {
      return 0;
    }

    return -1;
  }
}
