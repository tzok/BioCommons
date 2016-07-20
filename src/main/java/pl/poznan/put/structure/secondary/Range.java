package pl.poznan.put.structure.secondary;


public class Range implements Comparable<Range> {

  public int left;
  public int right;


  public Range(int l, int r) {
    left = l;
    right = r;
  }

  public int size() {
    return Math.abs(left - right);
  }

  public boolean contains(int node) {
    return ((node <= right) && (node >= left));
  }

  @Override
  public int compareTo(Range rhs) {
    if (size() < rhs.size()) return -1;
    if (size() > rhs.size()) return 1;
    if (left < rhs.left) return -1;
    return 1;
  }

}
