package ast.cmp;

import java.util.HashSet;

import ast.AST;
import ast.lists.Map;
import ast.patterns.Pattern;
import ast.types.Type;
import ast.types.TypeError;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "pattern", "exp" })

public class BQual extends Qualifier {

  public Pattern pattern;
  public AST     exp;

  public BQual() {
  }

  public BQual(int lineStart, int lineEnd) {
    super(lineStart, lineEnd);
  }

  public String toString() {
    return "BQual(" + pattern + "," + exp + ")";
  }

  public void FV(HashSet<String> vars) {
    exp.FV(vars);
  }

  public void vars(HashSet<String> vars) {
    pattern.vars(vars);
  }

  public AST desugar(AST value) {
    return new Map(getLineStart(), getLineEnd(), "", pattern, exp, value);
  }

  public void setPath(String path) {
    exp.setPath(path);
  }

  public Env<String, Type> bind(Env<String, Type> env) {
    Type type = exp.type(env);
    if (type instanceof ast.types.List) {
      ast.types.List list = (ast.types.List) type;
      env = pattern.bind(env, Type.eval(list.getType(),env));
      return env;
    } else throw new TypeError(exp.getLineStart(), exp.getLineEnd(), "expecting a list type " + type);
  }

}
