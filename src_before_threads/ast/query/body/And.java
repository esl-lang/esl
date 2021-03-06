package ast.query.body;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import ast.binding.declarations.DeclaringLocation;
import ast.query.instrs.Instr;
import ast.query.value.Var;
import ast.types.Type;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "elements" })
public class And extends BodyElement {

  public BodyElement[] elements;

  public And() {
  }

  public And(int lineStart, int lineEnd, BodyElement[] elements) {
    super(lineStart, lineEnd);
    this.elements = elements;
  }

  public BodyElement subst(Type type, String typeName) {
    BodyElement[] newElements = new BodyElement[elements.length];
    for (int i = 0; i < elements.length; i++) {
      newElements[i] = elements[i].subst(type, typeName);
    }
    return new And(getLineStart(), getLineEnd(), newElements);
  }

  public void getContainedDecs(Hashtable<String, DeclaringLocation> decs) {
    for (BodyElement e : elements)
      e.getContainedDecs(decs);
  }

  public void compile(Vector<Instr> code, int length, Vector<String> vars, boolean isLast) {
    for (int i = 0; i < elements.length; i++) {
      BodyElement element = elements[i];
      isLast = isLast && i == elements.length - 1;
      element.compile(code, length, vars, isLast);
    }
  }

  public void vars(HashSet<String> vars) {
    vars(elements, vars);
  }

}
