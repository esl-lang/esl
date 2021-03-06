package ast.spec;

import java.util.Arrays;

import ast.TreeNode;
import ast.query.rules.Rule;
import exp.BoaConstructor;
import values.Located;

@BoaConstructor(fields = { "state", "rules", "behaviour" })
public class Spec implements Located, TreeNode {

  public State   state;
  public Rule[]  rules;
  public BRule[] behaviour;

  int            start = -1;
  int            end = -1;

  public Spec() {
  }

  public String getLabel() {
    return toString();
  }

  public int getLineStart() {
    return start;
  }

  public int getLineEnd() {
    return end;
  }

  public void setLineStart(int linePos) {
    start = linePos;
  }

  public void setLineEnd(int linePos) {
    end = linePos;
  }

  public State getState() {
    return state;
  }

  public Rule[] getRules() {
    return rules;
  }

  public BRule[] getBehaviour() {
    return behaviour;
  }

  public String toString() {
    return "Spec(" + state + "," + Arrays.toString(rules) + "," + Arrays.toString(behaviour) + ")";
  }

}
