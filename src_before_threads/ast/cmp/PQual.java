package ast.cmp;

import java.util.HashSet;

import ast.AST;
import ast.binding.Var;
import ast.data.Apply;
import ast.lists.List;
import ast.tests.If;
import ast.types.Type;
import ast.types.TypeError;
import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "exp" })

public class PQual extends Qualifier {

  public AST exp;

  public PQual() {
  }

  public String toString() {
    return "PQual(" + exp + ")";
  }

  public void FV(HashSet<String> vars) {
    exp.FV(vars);
  }

  public void vars(HashSet<String> vars) {
  }

  public AST desugar(AST value) {
    return new Apply(getLineStart(), getLineEnd(), "", new Var(getLineStart(), getLineEnd(), "flatten", null), new If(getLineStart(), getLineEnd(), exp, new List(getLineStart(), getLineEnd(), new List(getLineStart(), getLineEnd(), value)), new List(getLineStart(), getLineEnd())));
  }

  public void setPath(String path) {
    exp.setPath(path);
  }

  public Env<String, Type> bind(Env<String, Type> env) {
    Type type = exp.type(env);
    if (type instanceof ast.types.Bool)
      return env;
    else throw new TypeError(exp.getLineStart(), exp.getLineEnd(), "predicate qualifier expects a boolean valued expression " + type);
  }

}
