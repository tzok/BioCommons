package pl.poznan.put.common;

import org.biojava.bio.structure.Group;

public interface ResidueBondRule {
    boolean areConnected(Group r1, Group r2);
}
