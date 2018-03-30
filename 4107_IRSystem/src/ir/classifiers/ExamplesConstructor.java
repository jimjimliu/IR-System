package ir.classifiers;

import java.util.*;

import ir.vsr.*;

/**
 * Creates a list of Examples from data files
 * Specializations handle various ways of storing
 * examples.
 *
 * @author Ray Mooney
 */

public abstract class ExamplesConstructor {

  /**
   * Return the list of examples for this dataset
   */
  public abstract List<Example> getExamples();
}














