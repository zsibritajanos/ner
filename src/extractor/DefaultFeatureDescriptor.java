/**
 * Developed by Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 *
 * @see <a href="http://www.inf.u-szeged.hu/rgai/">Research Group on Artificial Intelligence of the Hungarian Academy of Sciences</a>
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * @see <a href="http://creativecommons.org/licenses/by-sa/3.0/legalcode">http://creativecommons.org/licenses/by-sa/3.0/legalcode</a>
 */
package extractor;

import environmnet.Settings;
import feature.*;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Class that describes the list of the features, using for extract
 *         features from the tokens ot the text.
 */
public class DefaultFeatureDescriptor extends FeatureDescriptor implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final boolean ADD_FALSE = false;
  private static final int WINDOW_SIZE = 4;

  public DefaultFeatureDescriptor() {
    this.features = new LinkedList<>();

    // case //////////////////
    this.getFeatures().add(new Case("simple", WINDOW_SIZE, ADD_FALSE));
    // character n-gram
    this.getFeatures().add(new CharacterNGram("simple", false, WINDOW_SIZE, 2, 6));
    // first word //////////////////
    this.getFeatures().add(new FirstWord("simple", false, true, WINDOW_SIZE, ADD_FALSE));

    // vilagcegek
    this.getFeatures().add(new Lexicon("company", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/company.lexicon", Settings.DEFAULT_ENCODING, false));
    // ceg utotagok
    this.getFeatures().add(new Lexicon("company-suffix", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/company-suffix.lexicon", Settings.DEFAULT_ENCODING, false));
    // orszagok angolul
    this.getFeatures().add(new Lexicon("en.country", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/en/en.country.lexicon", Settings.DEFAULT_ENCODING, false));
    // algerian american
    this.getFeatures().add(new Lexicon("en.inhabitant", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/en/en.inhabitant.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("en.non-ne", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/en/en.non-ne.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("en.stopword", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/en/en.stopword.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("en.wword", false, false, WINDOW_SIZE, ADD_FALSE, "data/lexicon/en/en.wword.lexicon", Settings.DEFAULT_ENCODING, false));
    // keresztnevek
    this.getFeatures().add(new Lexicon("firstname", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/firstname.lexicon", Settings.DEFAULT_ENCODING, false));
    // vilagvarosok magyarul
    this.getFeatures().add(new Lexicon("hu.city", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.city.lexicon", Settings.DEFAULT_ENCODING, true));
    // orszagok magyarul
    this.getFeatures().add(new Lexicon("hu.country", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.country.lexicon", Settings.DEFAULT_ENCODING, true));
    // medence sivatag
    this.getFeatures().add(new Lexicon("hu.geo-place", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.geo-place.lexicon", Settings.DEFAULT_ENCODING, false));
    // magyar varosok magyararul
    this.getFeatures().add(new Lexicon("hu.hun-city", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.hun-city.lexicon", Settings.DEFAULT_ENCODING, true));
    // keresztnevek
    this.getFeatures().add(new Lexicon("hu.hun-firstname", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.hun-firstname.lexicon", Settings.DEFAULT_ENCODING, false));
    // vezeteknevek
    this.getFeatures().add(new Lexicon("hu.hun-lastname", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.hun-lastname.lexicon", Settings.DEFAULT_ENCODING, false));
    // hu ne
    this.getFeatures().add(new Lexicon("hu.hun-ne", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.hun-ne.lexicon", Settings.DEFAULT_ENCODING, true));
    // utca udvar
    this.getFeatures().add(new Lexicon("hu.hun-settlement", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.hun-settlement.lexicon", Settings.DEFAULT_ENCODING, false));
    // stop
    this.getFeatures().add(new Lexicon("hu.stopword", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.stopword.lexicon", Settings.DEFAULT_ENCODING, false));
    // wword
    this.getFeatures().add(new Lexicon("hu.wword", false, false, WINDOW_SIZE, ADD_FALSE, "data/lexicon/hu.wword.lexicon", Settings.DEFAULT_ENCODING, false));
    // loc misc per org
    this.getFeatures().add(new Lexicon("loc", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/loc.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("misc", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/misc.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("org", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/org.lexicon", Settings.DEFAULT_ENCODING, true));
    this.getFeatures().add(new Lexicon("per", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/per.lexicon", Settings.DEFAULT_ENCODING, false));
    // team
    this.getFeatures().add(new Lexicon("team", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/team.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("team-football", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/team-football.lexicon", Settings.DEFAULT_ENCODING, false));
    this.getFeatures().add(new Lexicon("team-nba", false, true, WINDOW_SIZE, ADD_FALSE, "data/lexicon/team-nba.lexicon", Settings.DEFAULT_ENCODING, false));
    // mapword
    this.getFeatures().add(new MapWord("hu-web-map-harmonic", false, true, WINDOW_SIZE, true, "data/mapword/hu.web.map", Settings.DEFAULT_ENCODING, "\t", MapWord.Normalization.HARMONIC, 10));
    this.getFeatures().add(new MapWord("hu-web-map-low-harmonic", true, true, WINDOW_SIZE, true, "data/mapword/hu.web-low.map", Settings.DEFAULT_ENCODING, "\t", MapWord.Normalization.HARMONIC, 10));
    this.getFeatures().add(new MapWord("hu-web-map-up-harmonic", true, true, WINDOW_SIZE, true, "data/mapword/hu.web-up.map", Settings.DEFAULT_ENCODING, "\t", MapWord.Normalization.HARMONIC, 10));
    // EN suffix
    this.getFeatures().add(new MapWord("en.suffix", false, true, WINDOW_SIZE, ADD_FALSE, "data/mapword/en.suffix.map", Settings.DEFAULT_ENCODING, "\t", null, 10));
    // position
    this.getFeatures().add(new Position("simple", WINDOW_SIZE, 5));
    // reg exp
    this.getFeatures().add(new RegExp("has-upcase", true, true, WINDOW_SIZE, ADD_FALSE, ".+[A-Z].*"));
    this.getFeatures().add(new RegExp("upcase", true, true, WINDOW_SIZE, ADD_FALSE, "[A-Z].*"));
    this.getFeatures().add(new RegExp("lowcase", true, true, WINDOW_SIZE, ADD_FALSE, "[a-z].*"));
    this.getFeatures().add(new RegExp("sign", true, true, WINDOW_SIZE, ADD_FALSE, "\\W.*"));
    this.getFeatures().add(new RegExp("has-digit", true, true, WINDOW_SIZE, ADD_FALSE, ".+[0-9].*"));
    this.getFeatures().add(new RegExp("has-upcase", true, true, WINDOW_SIZE, ADD_FALSE, ".+[A-Z].*"));
    this.getFeatures().add(new RegExp("is-upcase", true, true, WINDOW_SIZE, ADD_FALSE, "[^a-z]*"));
    this.getFeatures().add(new RegExp("has-punct-1", true, true, WINDOW_SIZE, ADD_FALSE, ".*[\\|/].*"));
    this.getFeatures().add(new RegExp("has-punct-2", true, true, WINDOW_SIZE, ADD_FALSE, ".*>.*"));
    this.getFeatures().add(new RegExp("has-punct-3", true, true, WINDOW_SIZE, ADD_FALSE, ".*-.*"));
    this.getFeatures().add(new RegExp("has-punct-4", true, true, WINDOW_SIZE, ADD_FALSE, ".+\\W.*"));
    this.getFeatures().add(new RegExp("number", true, true, WINDOW_SIZE, ADD_FALSE, "[0-9].*"));

    this.getFeatures().add(new RegExp("roman-number", true, true, 8, ADD_FALSE, "[IVXLCDM]*"));
    this.getFeatures().add(new RegExp("one-length", true, true, 8, ADD_FALSE, "."));
    this.getFeatures().add(new RegExp("two-length", true, true, 8, ADD_FALSE, ".."));
    this.getFeatures().add(new RegExp("three-length", true, true, 8, ADD_FALSE, "..."));
    this.getFeatures().add(new RegExp("five-length", true, true, 8, ADD_FALSE, ".{4,6}+"));
    this.getFeatures().add(new RegExp("ten-length", true, true, 8, ADD_FALSE, ".{7,10}+"));
    this.getFeatures().add(new RegExp("long-length", true, true, 8, ADD_FALSE, ".{11,}+"));

    // reg exp lexicon
    this.getFeatures().add(new RegExpLexicon("inside-c1", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/inside-c1.regexplexicon", Settings.DEFAULT_ENCODING, ".*", ".*"));
    this.getFeatures().add(new RegExpLexicon("inside-c2", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/inside-c2.regexplexicon", Settings.DEFAULT_ENCODING, ".*", ".*"));
    this.getFeatures().add(new RegExpLexicon("inside-c3", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/inside-c3.regexplexicon", Settings.DEFAULT_ENCODING, ".*", ".*"));
    this.getFeatures().add(new RegExpLexicon("inside-c4", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/inside-c4.regexplexicon", Settings.DEFAULT_ENCODING, ".*", ".*"));
    this.getFeatures().add(new RegExpLexicon("postfix-c1", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/postfix-c1.regexplexicon", Settings.DEFAULT_ENCODING, ".*", null));
    this.getFeatures().add(new RegExpLexicon("postfix-c2", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/postfix-c2.regexplexicon", Settings.DEFAULT_ENCODING, ".*", null));
    this.getFeatures().add(new RegExpLexicon("postfix-c3", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/postfix-c3.regexplexicon", Settings.DEFAULT_ENCODING, ".*", null));
    this.getFeatures().add(new RegExpLexicon("postfix-c4", false, true, WINDOW_SIZE, ADD_FALSE, "./data/regexplexicon/postfix-c4.regexplexicon", Settings.DEFAULT_ENCODING, ".*", null));

    // sentence length
    this.getFeatures().add(new SentenceLength("simple"));

    // hu suffix list
    this.getFeatures().add(new SuffixList("hu", false, false, WINDOW_SIZE, ADD_FALSE, "./data/suffix/hu.suffix", Settings.DEFAULT_ENCODING));

    // token length
    this.getFeatures().add(new TokenLength("simple", WINDOW_SIZE));

    // traininfo
    this.getFeatures().add(new TrainInfo("simple", false, true, WINDOW_SIZE, ADD_FALSE, "./data/hvg.conll.train", Settings.DEFAULT_ENCODING, " ", 0, 1, 2, 0.5));

    // word form
    this.getFeatures().add(new WordForm("simple", false, WINDOW_SIZE));

    // words left
    this.getFeatures().add(new WordsLeft("simple", WINDOW_SIZE));

    // word n gram //////////////////
    // this.getFeatures().add(new WordNGram("simple", false, true, 2, false, 3, false));
  }

}
