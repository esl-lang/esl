package ast.types;

import java.util.Arrays;
import java.util.HashSet;

import env.Env;
import exp.BoaConstructor;

public class Cnstr extends Type {

  Term  type;
  Union union;

  public Cnstr(int lineStart, int lineEnd, Term type, Union union) {
    super(lineStart, lineEnd);
    this.type = type;
    this.union = union;
  }

  public Term getType() {
    return type;
  }

  public void setType(Term type) {
    this.type = type;
  }

  public Union getUnion() {
    return union;
  }

  public void setUnion(Union union) {
    this.union = union;
  }

  public String toString() {
    return "<cnstr " + type.toString() + " " + union + ">";
  }

  public Type substType(Type t, String name) {
    return new Cnstr(getLineStart(), getLineEnd(), (Term) type.substType(t, name), (Union) union.substType(t, name));
  }

  public Type[] getTypes() {
    return type.getTypes();
  }

  public void check(Env<String, Type> env) {
    type.check(env);
  }

  public void FV(HashSet<String> vars) {
    type.FV(vars);
  }

}
