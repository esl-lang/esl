package ast.types;

import java.util.HashSet;

import env.Env;
import exp.BoaConstructor;

@BoaConstructor(fields = { "keyType","valueType" })
public class Table extends Type {

	public Type								keyType;
	public Type								valueType;

	public Table() {
	}

	@Override
	public void check(Env<String, Type> env) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void FV(HashSet<String> vars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type substType(Type type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
