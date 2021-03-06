package compiler;

import ast.refs.Ref;
import instrs.apply.ApplyDynamicVar;
import instrs.tests.DynamicVarIsCons;
import instrs.tests.DynamicVarIsTerm;
import instrs.vars.SetDynamic;
import instrs.vars.SubDynamic;
import list.List;
import runtime.functions.CodeBox;

public class DynamicVar extends Local {

  public DynamicVar(String name, int frameIndex) {
    super(name, frameIndex);
  }

  public static DynamicVar incDynamic(DynamicVar var) {
    return new DynamicVar(var.name, var.getIndex() + 1);
  }

  public void compile(int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new instrs.vars.Dynamic(line, getIndex()), locals, dynamics);
  }

  public void compilePush(int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new instrs.vars.PushDynamic(line, getIndex()), locals, dynamics);
  }

  public void update(int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new SetDynamic(line, getIndex()), locals, dynamics);
  }

  public String toString() {
    return "DynamicVar(" + getName() + "," + getIndex() + ")";
  }

  public void sub1(int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new SubDynamic(line, getIndex(), 1), locals, dynamics);
  }

  public void apply(int arity, int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics, boolean isLast) {
    code.add(new ApplyDynamicVar(line, getIndex(), arity), locals, dynamics);
  }

  public void isCons(int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new DynamicVarIsCons(line, getIndex()), locals, dynamics);
  }

  public void isTerm(String name, int arity, int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new DynamicVarIsTerm(line, name, arity, getIndex()), locals, dynamics);
  }

  public void bind(Ref ref, int line, CodeBox code, List<FrameVar> locals, List<DynamicVar> dynamics) {
    code.add(new instrs.patterns.SetDynamic(line, ref, getIndex()), locals, dynamics);
  }

}
