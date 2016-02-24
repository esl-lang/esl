package instrs;

import actors.Actor;

public class SkipTrue extends Instr {

  int count;

  public SkipTrue(int count) {
    super();
    this.count = count;
  }

  public void perform(Actor actor) {
    // Instruction counter has already been advanced...
    if ((boolean) actor.popStack()) actor.incCodePtr(count-1);
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String toString() {
    return "SkipTrue(" + count + ")";
  }

}
