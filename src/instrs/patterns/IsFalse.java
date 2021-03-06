package instrs.patterns;

import ast.refs.Ref;
import instrs.Instr;
import runtime.actors.Actor;

public class IsFalse extends Instr {

  Ref ref;

  public IsFalse(int line, Ref ref) {
    super(line);
    this.ref = ref;
  }

  public void perform(Actor actor) {
    Object o = ref.ref(actor);
    if (o instanceof Boolean) {
      if (o.equals(Boolean.TRUE)) actor.fail();
    } else actor.fail();
  }

  public String toString() {
    return pprint("ISFALSE",ref);
  }

}
