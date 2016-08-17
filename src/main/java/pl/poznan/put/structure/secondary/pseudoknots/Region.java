package pl.poznan.put.structure.secondary.pseudoknots;

import pl.poznan.put.structure.secondary.formats.BpSeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A region is a collection of pairs (BPSEQ entries) which are consecutive in
 * sequence.
 */
public final class Region {
    private final int id;
    private final List<BpSeq.Entry> entries;
    private final int begin;
    private final int end;
    private boolean isRemoved;

    private Region(final int id, final List<BpSeq.Entry> entries,
                   final int begin, final int end) {
        super();
        this.id = id;
        this.entries = new ArrayList<>(entries);
        this.begin = begin;
        this.end = end;
        isRemoved = false;
    }

    // Create Array of Regions from connections stored in RNAStructure
    public static List<Region> createRegions(final BpSeq bpSeq) {
        List<Region> regions = new ArrayList<>();
        List<BpSeq.Entry> regionEntries = new ArrayList<>();
        Iterable<BpSeq.Entry> allEntries = new ArrayList<>(bpSeq.getPaired());
        int id = 0;

        for (BpSeq.Entry entry : allEntries) {
            if (regionEntries.isEmpty()) {
                regionEntries.add(entry);
                continue;
            }

            BpSeq.Entry last = regionEntries.get(regionEntries.size() - 1);
            if ((entry.getIndex() == (last.getIndex() + 1)) && (entry.getPair()
                                                                == (last.getPair()
                                                                    - 1))) {
                regionEntries.add(entry);
                continue;
            }

            BpSeq.Entry first = regionEntries.get(0);
            regions.add(new Region(id, regionEntries, first.getIndex(),
                                   first.getPair()));
            regionEntries.clear();
            regionEntries.add(entry);

            id++;
        }

        if (!regionEntries.isEmpty()) {
            BpSeq.Entry first = regionEntries.get(0);
            regions.add(new Region(id, regionEntries, first.getIndex(),
                                   first.getPair()));
        }

        return regions;
    }

    public static Region merge(final Region... regions) {
        List<BpSeq.Entry> entries = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (Region region : regions) {
            for (BpSeq.Entry entry : region.entries) {
                if (entry.getIndex() < min) {
                    min = entry.getIndex();
                }
                if (entry.getPair() > max) {
                    max = entry.getPair();
                }
                entries.add(entry);
            }
        }

        int id = regions[0].id;
        return new Region(id, entries, min, max);
    }

    public int getId() {
        return id;
    }

    public List<BpSeq.Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(final boolean removed) {
        isRemoved = removed;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return entries.size();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Region region = (Region) obj;
        return id == region.id;
    }
}
