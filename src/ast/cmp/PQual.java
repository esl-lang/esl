package ast.cmp;

import java.util.HashSet;

import ast.AST;
import ast.binding.Var;
import ast.data.Apply;
import ast.lists.List;
import ast.tests.If;
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
    return new Apply(new Var("flatten"), new If(exp, new List(new List(value)), new List()));
  }

}