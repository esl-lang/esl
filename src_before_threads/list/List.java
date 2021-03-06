package list;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public abstract class List<Element> implements Serializable {

  public static <Element> List<Element> asList(Element[] a) {
    List<Element> l = new Nil<Element>();
    for (int i = a.length - 1; i >= 0; i--)
      l = l.cons(a[i]);
    return l;
  }

  public static <T> List<T> flatten(List<List<T>> ls) {
    if (ls.isNil())
      return new Nil<T>();
    else return ls.getHead().append(flatten(ls.getTail()));
  }

  public static <Element> void shuffle(Element[] a) {
    Random rnd = ThreadLocalRandom.current();
    for (int i = 0; i < a.length * 2; i++) {
      int m = rnd.nextInt(a.length);
      int n = rnd.nextInt(a.length);
      Element e = a[m];
      a[m] = a[n];
      a[n] = e;
    }
  }

  public abstract List<Element> append(Element object);

  public List<Element> append(List<Element> list) {
    if (isNil())
      return list;
    else if (list.isNil())
      return this;
    else return getTail().append(list).cons(getHead());
  }

  public Element[] asArray() {
    Element[] a = (Element[]) Array.newInstance(Object.class, length());
    List<Element> l = this;
    for (int i = 0; i < length(); i++)
      a[i] = nth(i);
    return a;
  }

  public Cons<Element> cons(Element head) {
    return new Cons<Element>(head, this);
  }

  public List<Element> drop(int elements) {
    if (elements == 0)
      return this;
    else return getTail().drop(elements - 1);
  }

  public abstract Element getHead();

  public abstract List<Element> getTail();

  public abstract Object getTailMaybe();

  public abstract boolean isNil();

  public abstract boolean isRealList();

  public Element last() {
    return nth(length() - 1);
  }

  public abstract int length();

  public abstract <T> List<T> map(Function<Element, T> f);

  private List<Element> mkRealList() {
    if (isNil())
      return this;
    else {
      List<Element> l = (List<Element>)getTailMaybe();
      return new Cons<Element>(getHead(), l.mkRealList());
    }
  }

  public Element nth(int i) {
    if (i <= 0)
      return getHead();
    else return getTail().nth(i - 1);
  }

  public List<List<Element>> product(List<Element> l2) {
    List<Element> l1 = this;
    List<List<Element>> product = new Nil<List<Element>>();
    while (!l1.isNil()) {
      while (!l2.isNil()) {
        product = product.cons(new Nil<Element>().cons(l2.getHead()).cons(l1.getHead()));
        l2 = l2.getTail();
      }
      l1 = l1.getTail();
    }
    return product;
  }

  public List<Element> remove(Element e) {
    if (isNil())
      return this;
    else if (getHead().equals(e))
      return getTail().remove(e);
    else return getTail().remove(e).cons(getHead());
  }

  public List<Element> reverse() {
    List<Element> list = this;
    List<Element> newList = new Nil<Element>();
    while (!list.isNil()) {
      newList = newList.cons(list.getHead());
      list = list.getTail();
    }
    return newList;
  }

  public List<Element> shuffle() {
    // Return a randomly generated list...
    Element[] a = asArray();
    shuffle(a);
    return asList(a);
  }

  public List<Element[]> split() {
    return split(new Nil<Element>());
  }

  public abstract List<Element[]> split(List<Element> prefix);

  public List<Element> take(int elements) {
    if (elements == 0)
      return new Nil<Element>();
    else return getTail().take(elements - 1).cons(getHead());
  }

  public String toString() {
    if (isRealList()) {
      List<Element> l = mkRealList();
      String s = "[";
      for (int i = 0; i < l.length() - 1; i++)
        s = s + l.nth(i) + ",";
      if (l.isNil())
        return s + "]";
      else return s + l.last() + "]";
    }
    return super.toString();
  }

  public List<Element> update(int i, Element v) {
    if (i == 0)
      return getTail().cons(v);
    else return getTail().update(i - 1, v).cons(getHead());
  }

}
