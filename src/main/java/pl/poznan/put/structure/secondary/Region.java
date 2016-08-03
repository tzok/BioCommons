package pl.poznan.put.structure.secondary;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.structure.secondary.formats.BpSeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Region {
    private final List<BpSeq.Entry> entries;
    private boolean isRemoved;

    public Region(final List<BpSeq.Entry> pairs) {
        super();
        this.entries = new ArrayList<>(pairs);
        isRemoved = false;
    }

    public final List<BpSeq.Entry> getEntries() {
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
