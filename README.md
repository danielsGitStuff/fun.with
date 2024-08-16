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
Lists<House> houses2 = Lists.of(new House(2, 4), new House(3, 5));
// => l(3)[House{doors=2, windows=4},House{doors=3, windows=5},House{doors=4, windows=6}]// => l(2)[House{doors=2, windows=4},House{doors=3, windows=5}]
```

- add elements as you go (modifies the underlying list).
```java
Lists<House> additionalHouses = houses.copy().add(new House(10, 11)).add(new House(12, 13));
// => l(5)[House{doors=2, windows=4},House{doors=3, windows=5},House{doors=4, windows=6},House{doors=10, windows=11},House{doors=12, windows=13}]
```

- create a Map with all houses and their according amount of windows
```java
Maps<House, Integer> house2windows = houses.associateWith(house -> house.windows + 1);
// => M(3){House{doors=2, windows=4}->5,House{doors=3, windows=5}->6,House{doors=4, windows=6}->7}
```

- create a Map with all houses and their according amount of windows
```java
Maps<Integer, House> windows2house = houses.associateBy(house -> house.windows);
// => M(3){4->House{doors=2, windows=4},5->House{doors=3, windows=5},6->House{doors=4, windows=6}}
```

- have an indexed map method
```java
Lists<House> mapped = houses.mapIndexed((index, house) -> new House(index + 1, house.windows));
// => l(3)[House{doors=1, windows=4},House{doors=2, windows=5},House{doors=3, windows=6}]
```

- group by
```java
Maps<Integer, Lists<House>> doors2house = houses.copy().add(new House(2, 9)).groupBy(house -> house.doors);
// => M(3){2->l(2)[House{doors=2, windows=4},House{doors=2, windows=9}],3->l(1)[House{doors=3, windows=5}],4->l(1)[House{doors=4, windows=6}]}
```

- group by with value selector
```java
Maps<Integer, Lists<Integer>> doors2windows = houses.copy().add(new House(2, 9)).groupBy(h -> h.doors, house -> house.windows);
// => M(3){2->l(2)[4,9],3->l(1)[5],4->l(1)[6]}
```

- map function for Map values only
```java
Maps<Integer, Integer> door2firstWindows = doors2house.mapValues((integer, hs) -> hs.first().windows);
// => M(3){2->4,3->5,4->6}
```

- map the whole Map
```java
Maps<String, House> mappedMap = doors2house.map((doors, hs) -> Pair.of("S" + doors, hs.first()));
// => M(3){S3->House{doors=3, windows=5},S4->House{doors=4, windows=6},S2->House{doors=2, windows=4}}
```

- intersect collections
```java
Lists<House> intersection = houses.intersection(houses2);
// => l(2)[House{doors=2, windows=4},House{doors=3, windows=5}]
```

- subtract collections
```java
Lists<House> subtraction = houses.subtract(houses2);
// => l(1)[House{doors=4, windows=6}]
```

create Sets
```java
  Sets.of(1, 2, 3);
  Lists.of(1, 2, 3).sets();
  Sets.wrap(Arrays.asList(1, 2, 3))
```