package ast.data;

import java.util.HashSet;

import ast.general.AST;
import ast.types.Type;
import ast.types.TypeError;
import compiler.DynamicVar;
import compiler.FrameVar;
import env.Env;
import list.List;
import runtime.functions.CodeBox;

public class TermRef extends AST {

  AST term;
  int index;

  public TermRef(int lineStart, int lineEnd, AST term, int index) {
    super(lineStart, lineEnd);
    this.term = term;
    this.index = index;
  }

  public void compile(List<FrameVar> locals, List<DynamicVar> dynamics, CodeBox code, boolean isLast) {
    term.compile(locals, dynamics, code, false);
    code.add(new instrs.ops.TermRef(getLineStart(), index), locals, dynamics);
  }

  public void FV(HashSet<String> vars) {
    term.FV(vars);
  }

  public void DV(HashSet<String> vars) {
    term.DV(vars);
  }

  public int maxLocals() {
    return term.maxLocals();
  }

  public AST subst(AST ast, String name) {
    return new TermRef(getLineStart(), getLineEnd(), term.subst(ast, name), index);
  }

  public String toString() {
    return "TermRef(" + term + "," + index + ")";
  }

  public void setPath(String path) {
    term.setPath(path);
  }

  public Type type(Env<String, Type> env) {
    Type type = term.type(env);
    if (type instanceof ast.types.Term) {
      ast.types.Term tt = (ast.types.Term) type;
      if (tt.getTypes().length > index) {
        setType(tt.getTypes()[index]);
        return getType();
      } else throw new TypeError(getLineStart(), getLineEnd(), "does not have element " + index);
    } else throw new TypeError(getLineStart(), getLineEnd(), "expecting a term, but value is of type " + type);
  }

  public String getLabel() {
    return "termref " + index + " :: " + getType();
  }

}
