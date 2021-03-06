package instrs.tests;

import instrs.Instr;
import runtime.actors.Actor;

public class IsBool extends Instr {

  boolean value;

  public IsBool(int line, boolean value) {
    super(line);
    this.value = value;
  }

  public void perform(Actor actor) {
    actor.pushStack((boolean)actor.popStack() == value);
  }

  public String toString() {
    return pprint("ISBOOL",value);
  }

}
