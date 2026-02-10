package fun.with.unstable.dataframe;

import java.util.ArrayList;
import java.util.List;

public class DFColumn {
    private String name;
    private final int index;
    private ColumnCast cast;
    private final List<DFColumnListener> listeners = new ArrayList<>();

    public DFColumn(String name, int index, ColumnCast cast) {
        this.name = name;
        this.index = index;
        this.cast = cast;
    }

    public DFColumn setCast(ColumnCast cast) {
        this.cast = cast;
        return this;
    }

    public String getName() {
        return name;
    }

    public DFColumn setName(String name) {
        this.name = name;
        for (DFColumnListener listener : listeners) {
            listener.onColumnChanged(this);
        }
        return this;
    }

    public void addListener(DFColumnListener listener) {
        this.listeners.add(listener);
    }

    public int getIndex() {
        return index;
    }

    public ColumnCast getCast() {
        return cast;
    }

    public DFColumn withName(String name) {
        return new DFColumn(name, this.index, this.cast);
    }

    public DFColumn withIndex(int index) {
        return new DFColumn(this.name, index, this.cast);
    }

    public DFColumn withCast(ColumnCast cast) {
        return new DFColumn(this.name, this.index, cast);
    }

    @Override
    public String toString() {
        return "DFColumn{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", cast=" + cast +
                '}';
    }
}
