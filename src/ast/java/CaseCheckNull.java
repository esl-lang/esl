package ast.java;

import java.io.PrintWriter;

import ast.java.types.Type;

public class CaseCheckNull extends CaseArm {

  Exp     exp;
  Type    result;
  CaseArm cont;

  public CaseCheckNull(Exp exp, Type result, CaseArm cont) {
    this.exp = exp;
    this.result = result;
    this.cont = cont;
  }

  public void write(PrintWriter out) {
    out.print("new Fun1<" + result.toArgString() + "," + "Fail<" + result.toArgString() + ">>() { public " + result.toArgString() + " apply(");
    out.print("Fail<" + result.toArgString() + "> fail) {");

    out.print("if(");
    exp.writeExp(out);
    out.print("==null)");

    out.print("return ");
    cont.write(out);
    out.print(".apply(fail);");

    out.print("else return fail.fail();");
    out.print("}}");
  }

  public void toMessageHandler() {
    result = Type.OBJ;
    cont.toMessageHandler();
  }

}