package extractor;

import feature.Feature;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zsibritajanos on 2016.05.04..
 */
public class FeatureDescriptor implements Serializable {

  private static final long serialVersionUID = 1L;
  protected List<Feature> features = null;

  protected FeatureDescriptor() {

  }

  public FeatureDescriptor(List<Feature> features) {
    this.features = features;
  }

  public FeatureDescriptor(String file) {
    this.features = FeatureDescriptorReader.read(file);
  }

  public List<Feature> getFeatures() {
    return this.features;
  }
}
