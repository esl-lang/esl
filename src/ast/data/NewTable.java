package ast.data;

import java.util.HashSet;

import ast.general.AST;
import ast.types.Array;
import ast.types.Type;
import ast.types.TypeError;
import compiler.DynamicVar;
import compiler.FrameVar;
import env.Env;
import exp.BoaConstructor;
import list.List;
import runtime.functions.CodeBox;

@BoaConstructor(fields = { "keyType", "valueType" })
public class NewTable extends AST {

	public Type	keyType;
	public Type	valueType;

	public NewTable() {
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compile(List<FrameVar> locals, List<DynamicVar> dynamics, CodeBox code, boolean isLast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DV(HashSet<String> vars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void FV(HashSet<String> vars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int maxLocals() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AST subst(AST ast, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type type(Env<String, Type> env) {
		// TODO Auto-generated method stub
		return null;
	}

}
