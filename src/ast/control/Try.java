package ast.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import ast.AST;
import ast.binding.Var;
import ast.data.Fun;
import ast.tests.BArm;
import ast.tests.Case;
import compiler.DynamicVar;
import compiler.FrameVar;
import exp.BoaConstructor;
import instrs.Instr;
import list.List;

@BoaConstructor(fields = { "body", "arms" })

public class Try extends AST {

  public AST    body;
  public BArm[] arms = new BArm[] {};

  public Try() {
  }

  public Try(AST body, BArm[] arms) {
    this.body = body;
    this.arms = arms;
  }

  public String toString() {
    return "Try(" + body + "," + Arrays.toString(arms) + ")";
  }

  public void compile(List<FrameVar> locals, List<DynamicVar> dynamics, Vector<Instr> code) {

    // try e catch p1 -> e1; ...
    // is implemented as a closure in a new frame for the scope of e.
    // It is equivalent to a call of fun() e where the new stack frame
    // includes fun(v) case v p1 -> e1 ... as the catch closure. If
    // a value is ever thrown then the stack is unwound up to the
    // most recent catch closure whose stack frame is popped and the
    // catch closure is called.

    desugarCatch().compile(locals, dynamics, code);
    desugarBody().compile(locals, dynamics, code);
    code.add(new instrs.Try());
  }

  public AST desugarBody() {
    return new Fun("try-body", new String[] {}, body);
  }

  public AST desugarCatch() {
    return new Fun("catch", new String[] { "$1" }, new Case(new AST[] { new Var("$1") }, arms));
  }

  public void FV(HashSet<String> vars) {
    desugarBody().FV(vars);
    desugarCatch().FV(vars);
  }

  public void DV(HashSet<String> vars) {
    desugarBody().DV(vars);
    desugarCatch().DV(vars);
  }

  public int maxLocals() {
    return 0;
  }

  public AST subst(AST ast, String name) {
    return new Try(body.subst(ast, name), substArms(ast, name));
  }

  private BArm[] substArms(AST ast, String name) {
    BArm[] as = new BArm[arms.length];
    for (int i = 0; i < arms.length; i++)
      as[i] = arms[i].subst(ast, name);
    return as;
  }

}