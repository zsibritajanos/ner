package crf;

import cc.mallet.fst.CRF;
import extractor.FeatureDescriptor;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Model implements Serializable {

  private static final long serialVersionUID = 1L;
  private CRF crf = null;
  private FeatureDescriptor featureDescriptor = null;

  public Model(CRF crf, FeatureDescriptor featureDescriptor) {
    this.setCrf(crf);
    this.setFeatureDescriptor(featureDescriptor);
  }

  /**
   * Serialize a model to an output file.
   */
  public void serialize(String file) {

    try {
      ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
              new FileOutputStream(file)));
      oos.writeObject(this);
      oos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Restore model from an input file.
   */
  public static Model restore(String file) {
    ObjectInputStream objectInputStream = null;
    Model model = null;

    try {
      objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
      model = (Model) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        objectInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return model;
  }

  public CRF getCrf() {
    return crf;
  }

  public void setCrf(CRF crf) {
    this.crf = crf;
  }

  public FeatureDescriptor getFeatureDescriptor() {
    return featureDescriptor;
  }

  public void setFeatureDescriptor(FeatureDescriptor featureDescriptor) {
    this.featureDescriptor = featureDescriptor;
  }
}
