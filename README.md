# fun.with

Bring a bit of Kotlin like goodness to Java to make Lists etc more fun.
Just the basic stuff.

## Example

Have a `House` class:

```java
public class House {
    public int doors;
    public int windows;
}
```

```java
Lists<House> houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));
```

- create a Map with all houses and their according amount of windows
- ```java
  houses.associateWith(house -> house.windows + 1);
  // => {(H(2,4):5), H(3.5):6, H(4,6):7}
```