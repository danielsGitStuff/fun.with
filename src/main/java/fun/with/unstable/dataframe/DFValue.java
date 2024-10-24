package fun.with.unstable.dataframe;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class DFValue {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(340);
    }

    private Object object;
    private final DFRow row;
    private final int columnIdx;

    public DFValue(DFRow row, int columnIdx, Object object) {
        this.object = object;
        this.row = row;
        this.columnIdx = columnIdx;
    }

    public Object getObject() {
        return object;
    }

    public Number getNumber() {
        return (Number) this.object;
    }

    public Double getDouble() {
        return (Double) this.object;
    }

    public Integer getInt() {
        return (Integer) this.object;
    }

    public Long getLong() {
        return (Long) this.object;
    }

    public Boolean getBoolean() {
        return (Boolean) this.object;
    }

    public String getString() {
        return (String) this.object;
    }

    public boolean isNull() {
        return this.object == null;
    }

    public boolean isNotNull() {
        return this.object != null;
    }

    public <X> X getCast(Class<X> type) {
        return (X) this.object;
    }

    @Override
    public String toString() {
        if (this.object instanceof Double) {
            return DECIMAL_FORMAT.format(this.object);
        }
        return this.object == null ? "null" : this.object.toString();
    }

    public DFValue setValue(Object object) {
        this.object = object;
        this.row.set(this.columnIdx, object);
        return this;
    }

    public boolean eq(Object o) {
        return Objects.equals(this.object, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(object);
    }
}
