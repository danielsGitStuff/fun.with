package fun.with.lists.classes;

import fun.with.lists.AssociationTests;

import java.util.Objects;

public class House {
    public int doors;
    public int windows;

    public House(int doors, int windows) {
        this.doors = doors;
        this.windows = windows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return doors == house.doors && windows == house.windows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doors, windows);
    }

    @Override
    public String toString() {
        return "House{" +
                "doors=" + doors +
                ", windows=" + windows +
                '}';
    }
}
