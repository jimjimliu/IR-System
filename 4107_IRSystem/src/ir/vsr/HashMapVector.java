package ir.vsr;

import java.util.*;

import ir.utilities.*;

/**
 * A data structure for a term vector for a document stored
 * as a HashMap that maps tokens to Weight's that store the
 * weight of that token in the document.
 * <p/>
 * Needed as an efficient, indexed representation of sparse
 * document vectors.
 *
 * @author Ray Mooney
 */

public class HashMapVector {
  /**
   * The HashMap that stores the mapping of tokens to Weights
   */
  public Map<String, Weight> hashMap = new HashMap<String, Weight>();

  /**
   * Returns the number of tokens in the vector.
   */
  public int size() {
    return hashMap.size();
  }

  /**
   * Clears the vector back to all zeros
   */
  public void clear() {
    hashMap.clear();
  }

  /**
   * Returns the Set of MapEntries in the hashMap
   */
  public Set<Map.Entry<String, Weight>> entrySet() {
    return hashMap.entrySet();
  }

  /**
   * Increment the weight for the given token in the vector by the given amount.
   */
  public double increment(String token, double amount) {
    Weight weight = hashMap.get(token);
    if (weight == null) {
      // If there is no current Weight for this token, create one
      weight = new Weight();
      hashMap.put(token, weight);
    }
    // Increment the weight of this token in the bag.
    weight.increment(amount);
    return weight.getValue();
  }

  /**
   * Return the weight of the given token in the vector
   */
  public double getWeight(String token) {
    Weight weight = hashMap.get(token);
    if (weight == null)
      return 0.0;
    else
      return weight.getValue();
  }

  /**
   * Increment the weight for the given token in the vector by 1.
   */
  public double increment(String token) {
    return increment(token, 1.0);
  }

  /**
   * Increment the weight for the given token in the vector by the given int
   */
  public double increment(String token, int amount) {
    return increment(token, (double) amount);
  }

  /**
   * Destructively add the given vector to the current vector
   */
  public void add(HashMapVector vector) {
    for (Map.Entry<String, Weight> entry : vector.entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      increment(token, weight);
    }
  }

  /**
   * Destructively add a scaled version of the given vector to the current vector
   */
  public void addScaled(HashMapVector vector, double scalingFactor) {
    for (Map.Entry<String, Weight> entry : vector.entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      increment(token, scalingFactor * weight);
    }
  }

  /**
   * Destructively subtract the given vector from the current vector
   */
  public void subtract(HashMapVector vector) {
    for (Map.Entry<String, Weight> entry : vector.entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      increment(token, -weight);
    }
  }


  /**
   * Destructively multiply the vector by a constant
   */
  public void multiply(double factor) {
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      Weight weight = entry.getValue();
      weight.setValue(factor * weight.getValue());
    }
  }


  /**
   * Produce a copy of this HashMapVector with a new HashMap and new
   * Weight's
   */
  public HashMapVector copy() {
    HashMapVector result = new HashMapVector();
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      result.increment(token, weight);
    }
    return result;
  }

  /**
   * Returns the maximum weight of any token in the vector.
   */
  public double maxWeight() {
    double maxWeight = Double.NEGATIVE_INFINITY;
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      if (weight > maxWeight)
        maxWeight = weight;
    }
    return maxWeight;
  }


  /**
   * Print out the vector showing the tokens and their weights
   */
  public void print() {
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // Print the term and its weight, where the value of the map entry is a Weight
      // and then you need to get the value of the Weight as the weight.
      System.out.println(entry.getKey() + ":" + entry.getValue().getValue());
    }
  }

  /**
   * Return String of the vector showing the tokens and their weights
   */
  public String toString() {
    String ret = "";
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // Print the term and its weight, where the value of the map entry is a Weight
      // and then you need to get the value of the Weight as the weight.
      ret += entry.getKey() + ": " + entry.getValue().getValue() + " ";
    }
    return ret;
  }

  /**
   * Computes cosine of angle to otherVector.
   */
  public double cosineTo(HashMapVector otherVector) {
    return cosineTo(otherVector, otherVector.length());
  }

  /**
   * Computes cosine of angle to otherVector when also given otherVector's Euclidian length
   * (Allows saving computation if length already known.  more efficient when
   * current vector is shorter than otherVector)
   */
  public double cosineTo(HashMapVector otherVector, double length) {
    // Stores sum of squares of current vector elements
    double sum = 0;
    // Stores running sum for dot product of two vectors
    double dotProd = 0;
    // iterate through elements in current vector
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The weight for the token is in the value of the Weight
      double weight = entry.getValue().getValue();
      double otherWeight = otherVector.getWeight(token);
      // Update dot product sum and sum of squares
      dotProd += weight * otherWeight;
      sum += weight * weight;
    }
    // cosine is dot product over product of lengths
    return (dotProd / (Math.sqrt(sum) * length));
  }

  /**
   * Compute Euclidian length (sqrt of sum of squares) of vector
   */
  public double length() {
    // Stores running sum of squares
    double sum = 0;
    for (Map.Entry<String, Weight> entry : entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      double weight = entry.getValue().getValue();
      sum += weight * weight;
    }
    return Math.sqrt(sum);
  }

}





