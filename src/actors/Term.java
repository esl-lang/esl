package actors;

import java.util.Arrays;

public class Term {
  String   name;
  Object[] values;

  public Term(String name, Object... values) {
    super();
    this.name = name;
    this.values = values;
  }

  public String getName() {
    return name;
  }

  public Object[] getValues() {
    return values;
  }

  public String toString() {
    return name + ((values.length == 0) ? "" : "(" + separate(",", values) + ")");
  }

  public boolean equals(Object other) {
    if (other instanceof Term) {
      Term t = (Term) other;
      if (t.getName().equals(name) && t.getValues().length == values.length) {
        for (int i = 0; i < values.length; i++) {
          if (!values[i].equals(t.getValues()[i])) return false;
        }
        return true;
      } else return false;
    } else return false;
  }

  public static String separate(String sep, Object[] os) {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; os != null && i < os.length; i++) {
      sb.append(os[i]);
      if (i < os.length - 1) {
        sb.append(sep);
      }
    }
    return sb.toString();
  }
}
