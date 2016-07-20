package pl.poznan.put.structure.secondary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import pl.poznan.put.structure.secondary.formats.BpSeq;

public class RNAStructure {

  public ArrayList<Character> rnaSequence = new ArrayList<Character>();
  public ArrayList<Pair> connections = new ArrayList<Pair>();
  public ArrayList<Integer> originalConnections = new ArrayList<Integer>();
  public ArrayList<Integer> correctConnections = new ArrayList<Integer>();
  public ArrayList<Character> DotBracket = new ArrayList<Character>();
  public boolean DP = false;


  // Function to create a deep copy of RNAStructure class
  public RNAStructure(RNAStructure rhs) {
    for(int i = 0; i < rhs.connections.size(); i++) {
      connections.add(new Pair(rhs.connections.get(i)));
      rnaSequence.add(rhs.rnaSequence.get(i));
      DotBracket.add(rhs.DotBracket.get(i));
      originalConnections.add(rhs.originalConnections.get(i));
    }
    for(int i = rhs.connections.size(); i < rhs.rnaSequence.size(); i++){
      rnaSequence.add(rhs.rnaSequence.get(i));
      DotBracket.add(rhs.DotBracket.get(i));
      originalConnections.add(rhs.originalConnections.get(i));
    }
  }


  // Initial function that analyse input file and parse lines to make connections and RNA sequence
  public RNAStructure(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = reader.readLine()) != null) {
        parseLine(line);
      }
      reader.close();
    }
    catch (IOException e) {
      System.err.format("Exception occurred trying to read '%s'.", filename);
      e.printStackTrace();
    }
  }

  public RNAStructure(BpSeq bpSeq) {
    for (BpSeq.Entry e : bpSeq.getEntries()) {
      rnaSequence.add(e.getSeq());
      originalConnections.add(e.getPair() - 1);
      DotBracket.add('.');
      if (e.getIndex() < e.getPair())
        connections.add(new Pair(e.getIndex(), e.getPair()));
    }
  }

  public String getSequence() {
    String Seq = "";
    for (char c : rnaSequence)
      Seq += c;
    return Seq;
  }

  public String getStructure() {
    String Str = "";
    for (int i : originalConnections)
      Str += Integer.toString(i);
    return Str;
  }

  public String getDotBracketStructure() {
    String Str = "";
    for (char c : DotBracket)
      Str += c;
    return Str;
  }

  // Save optimal structure to output file
  public void saveFile(String filename) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      // check if Dynamic Programming algorithm was used. It returns a slightly different Protein Structure
      if(!DP) {
        int[] output = createOutput();

        for(int i = 0; i < rnaSequence.size(); i++) {
          writer.write(i+1 + " " + rnaSequence.get(i) + " " + output[i+1]);
          writer.newLine();
        }
      } else {
        for(int i = 0; i < rnaSequence.size(); i++) {
          writer.write(i+1 + " " + rnaSequence.get(i) + " " + (correctConnections.get(i)+1));
          writer.newLine();
        }
      }
      writer.close();
    } catch (IOException e) {
      System.err.format("Exception occurred trying to save '%s'.", filename);
      e.printStackTrace();
    }
  }

  // Parse line from .bpseq input to RNA sequence and connections
  private void parseLine(String line){
    int index = line.indexOf(' ');
    DotBracket.add('.');
    rnaSequence.add(line.charAt(index+1));
    int first = Integer.parseInt(line.substring(0, index)), second = Integer.parseInt(line.substring(index + 3));
    if(first < second)
      connections.add(new Pair(first, second));
    originalConnections.add(second-1);
  }

  // Create temporary array from pairs for output simplicity
  private int[] createOutput(){
    int size = rnaSequence.size();
    int[] output = new int[size+2];

    for(int i = 0; i < connections.size(); i++){
      output[connections.get(i).getFirst()] = connections.get(i).getSecond();
      output[connections.get(i).getSecond()] = connections.get(i).getFirst();
    }

    return output;
  }
}
