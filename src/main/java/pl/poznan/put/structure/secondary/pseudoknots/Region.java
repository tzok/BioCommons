package pl.poznan.put.structure.secondary.pseudoknots;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.BpSeq.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by tzok on 04.08.16.
 */
public class Region {
    // Create Array of Regions from connections stored in RNAStructure
    public static List<Region> createRegions(final BpSeq bpSeq) {
        List<Region> regions = new ArrayList<>();
        List<Entry> regionEntries = new ArrayList<>();
        Iterable<Entry> allEntries = new ArrayList<>(bpSeq.getPaired());

        for (Entry entry : allEntries) {
            if (regionEntries.isEmpty()) {
                regionEntries.add(entry);
                continue;
            }

            Entry last = regionEntries.get(regionEntries.size() - 1);
            if ((entry.getIndex() == (last.getIndex() + 1)) &&
                (entry.getPair() == (last.getPair() - 1))) {
                regionEntries.add(entry);
                continue;
            }

            regions.add(new Region(regionEntries));
            regionEntries.clear();
            regionEntries.add(entry);
        }

        if (!regionEntries.isEmpty()) {
            regions.add(new Region(regionEntries));
        }

        return regions;
    }

    private final List<Entry> entries;
    private boolean isRemoved;

    public Region(final List<Entry> pairs) {
        super();
        entries = new ArrayList<>(pairs);
        isRemoved = false;
    }


    public final List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public final boolean isRemoved() {
        return isRemoved;
    }

    public final void setRemoved(final boolean removed) {
        isRemoved = removed;
    }

    public final int getBegin() {
        return entries.get(0).getIndex();
    }

    public final int getEnd() {
        return entries.get(0).getPair();
    }

    public final int getLength() {
        return entries.size();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Region region = (Region) obj;
        return CollectionUtils.isEqualCollection(entries, region.entries);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(entries);
    }
}
