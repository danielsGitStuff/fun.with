# fun.with

Bring a bit of Kotlin like goodness to Java to make Lists etc more fun.
Just the basic stuff.

## Examples

Have a `House` class:

```java
public class House {
    public int doors;
    public int windows;
}
```

```java
Lists<House> houses = Lists.of(new House(2, 4), new House(3, 5), new House(4, 6));
// => [(H(2,4)), H(3,5), H(4,6)]
```

- create a Map with all houses and their according amount of windows
- ```java
  houses.associateWith(house -> house.windows + 1);
  // => {(H(2,4):5), H(3,5):6, H(4,6):7}
  ```

- have an indexed map method
- ```java
  houses.mapIndexed((index, house) -> new House(index + 1, house.windows));
  // => [(H(1,4)), H(2,5), H(3,6)]
  ```
  
- create Sets
- ```java
  Sets.of(1, 2, 3);
  Lists.of(1, 2, 3).sets();
  Sets.wrap(Arrays.asList(1, 2, 3))
  ```