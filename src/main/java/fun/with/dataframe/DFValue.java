package fun.with.dataframe;

import fun.with.misc.NumberTransformer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DFValue {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(340);
    }

    private final Object object;

    public DFValue(Object object) {
        this.object = object;
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

    public Boolean getBoolean() {
        return (Boolean) this.object;
    }

    public String getString() {
        return (String) this.object;
    }

    public boolean isNull() {
        return this.object == null;
    }

    @Override
    public String toString() {
        if (this.object instanceof Double) {
            return DECIMAL_FORMAT.format(this.object);
        }
        return this.object == null ? "null" : this.object.toString();
    }
}
