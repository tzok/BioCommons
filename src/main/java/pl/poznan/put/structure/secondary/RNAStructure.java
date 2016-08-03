package pl.poznan.put.structure.secondary;

import pl.poznan.put.structure.secondary.formats.BpSeq;

import java.util.ArrayList;
import java.util.List;

public class RNAStructure {
    public List<Character> rnaSequence = new ArrayList<Character>();
    public List<Pair> connections = new ArrayList<Pair>();
    public List<Integer> originalConnections = new ArrayList<Integer>();
    public List<Integer> correctConnections = new ArrayList<Integer>();
    public List<Character> DotBracket = new ArrayList<Character>();
    public boolean DP = false;

    // Function to create a deep copy of RNAStructure class
    public RNAStructure(final RNAStructure structure) {
        for (int i = 0; i < structure.connections.size(); i++) {
            connections.add(new Pair(structure.connections.get(i)));
            rnaSequence.add(structure.rnaSequence.get(i));
            DotBracket.add(structure.DotBracket.get(i));
            originalConnections.add(structure.originalConnections.get(i));
        }
        for (int i = structure.connections.size();
             i < structure.rnaSequence.size(); i++) {
            rnaSequence.add(structure.rnaSequence.get(i));
            DotBracket.add(structure.DotBracket.get(i));
            originalConnections.add(structure.originalConnections.get(i));
        }
    }

    public RNAStructure(final BpSeq bpSeq) {
        super();

        for (BpSeq.Entry e : bpSeq.getEntries()) {
            rnaSequence.add(e.getSeq());
            originalConnections.add(e.getPair() - 1);
            DotBracket.add('.');

            if (e.getIndex() < e.getPair()) {
                connections.add(new Pair(e.getIndex(), e.getPair()));
            }
        }
    }

    public final String getSequence() {
        String Seq = "";
        for (char c : rnaSequence) {
            Seq += c;
        }
        return Seq;
    }

    public String getStructure() {
        String Str = "";
        for (int i : originalConnections) {
            Str += Integer.toString(i);
        }
        return Str;
    }
}
