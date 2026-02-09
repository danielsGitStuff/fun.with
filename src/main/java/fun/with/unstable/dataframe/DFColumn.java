package fun.with.unstable.dataframe;

public class DFColumn {
    private final String name;
    private final int index;
    private final ColumnCast cast;

    public DFColumn(String name, int index, ColumnCast cast) {
        this.name = name;
        this.index = index;
        this.cast = cast;
    }

    public String getName() {
        return name;
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
