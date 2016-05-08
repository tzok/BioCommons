package pl.poznan.put.structure.secondary;


public class Pair {
  private int ST;
  private int ND;

  public Pair(int _ST, int _ND) {
    ST   = _ST;
    ND = _ND;
  }

  public Pair(Pair rhs) { ST = rhs.ST; ND = rhs.ND;}

  public int getFirst() { return ST; }
  public int getSecond() { return ND; }
}
