package instrs.tests;

import instrs.Instr;
import runtime.actors.Actor;

public class DynamicVarIsTerm extends Instr {

  String name;
  int    arity;

  int    index;

  public DynamicVarIsTerm(int line, String name, int arity, int index) {
    super(line);
    this.name = name;
    this.arity = arity;
    this.index = index;
  }

  public void perform(Actor actor) {
    Object o = actor.getDynamic(index);
    if (o instanceof runtime.data.Term) {
      runtime.data.Term t = (runtime.data.Term) o;
      if (t.getName().equals(name) && t.getValues().length == arity)
        actor.pushStack(true);
      else actor.pushStack(false);
    } else actor.pushStack(false);
  }

  public String toString() {
    return pprint("DYNAMICVARISTERM", name, arity, index);
  }

}
