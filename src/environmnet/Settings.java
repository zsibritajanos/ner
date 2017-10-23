/**
 * Developed by Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 *
 * @see <a href="http://www.inf.u-szeged.hu/rgai/">Research Group on Artificial Intelligence of the Hungarian Academy of Sciences</a>
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * @see <a href="http://creativecommons.org/licenses/by-sa/3.0/legalcode">http://creativecommons.org/licenses/by-sa/3.0/legalcode</a>
 */
package environmnet;

/**
 * @author <a href="mailto:zsibrita@inf.u-szeged.hu">Janos Zsibrita</a>
 *         <p>
 *         Class to set up the base attributes for the system.
 */
public class Settings {

  public static final String DEFAULT_ENCODING = "utf-8";
  public static final String DEFAULT_SEPARATOR = "\t";
  public static final String PARAM_SEPARATOR = " ";

  public static final String FEATURE_PACKAGE = "feature";

  public static final String FEATURE_VALUE_TOKEN_SEPARATOR = "_";
  public static final String FALSE_SUFFIX = "FALSE";
  public static final String DOC_SEPARATOR = "-DOCSTART-";

  public static final boolean IS_VERBOSE = true;

  // default HU model
  public static final String MODEL_FILE = "hu.model";
}
