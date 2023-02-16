package fun.with;

public class Triple<A, B, C> {
  private final A a;
  private final B b;
  private final C c;
  
  public Triple(A a, B b, C c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }
  
  public static  <X, Y, Z> Triple<X, Y, Z> of(X x, Y y, Z z) {
    return new Triple<>(x, y, z);
  }
  public static  <X> TripleOf<X> ofSameKind(X x, X y, X z) {
    return new TripleOf<>(x, y, z);
  }
  
  
  public A a() {
    return this.a;
  }
  
  public B b() {
    return this.b;
  }
  
  public C c() {
    return this.c;
  }
}
