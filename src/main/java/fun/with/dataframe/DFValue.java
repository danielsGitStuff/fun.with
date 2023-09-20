package fun.with.dataframe;

import fun.with.misc.NumberTransformer;

public class DFValue {
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
        return this.object == null ? "null" : this.object.toString();
    }
}
