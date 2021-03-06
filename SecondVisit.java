import java.lang.*;
import java.util.*;
import syntaxtree.*;
import visitor.*;

public class SecondVisit extends GJNoArguDepthFirst<Integer>{

	HashMap<String, HashMap<String, LinkedList<String>>> class_list;
	HashMap<String, LinkedList<String>> class_map;
	LinkedList<String> method_list;
	LinkedList<String> parameter_list;

	HashMap<String, HashMap<String, LinkedList<String>>> inner_class_list;
	HashMap<String, LinkedList<String>> inner_class_map;

	HashMap<String, String> base_map;
	HashMap<String, String> function_map;
	boolean fields_define;
	boolean add_map;


	String curr_classname;
	String curr_parameter_name;
	Integer lable;
	Integer null_lable;
	Integer if_lable;
	Integer while_lable;
	Integer ss_lable;
	Integer bound_lable;

	boolean call_function;
	boolean inner_call;
	boolean inner_classt;

	public SecondVisit(HashMap<String, HashMap<String, LinkedList<String>>> class_list){
		this.class_list = class_list;
		this.inner_class_list = class_list;
		null_lable = 0;
		if_lable = 0;
		while_lable = 0;
		ss_lable = 0;
		bound_lable = 0;
		call_function = false;
		inner_call = false;
		add_map = false;
		fields_define = false;
	}

	public Integer visit(Goal g){

		g.f0.accept(this);
		g.f1.accept(this);

		return null;
	}

	public Integer visit(TypeDeclaration td){

		td.f0.accept(this);

		return null;
	}

	public Integer visit(ClassDeclaration cd){


		curr_classname = cd.f1.f0.toString();

		inner_class_map = new HashMap<>();
		inner_class_map = inner_class_list.get(curr_classname);

		base_map = new HashMap<>();
		base_map.put("this", curr_classname);
		//method_list = class_map.get(class_name + "virtual table");
		fields_define = true;
		cd.f3.accept(this);
		fields_define = false;

		cd.f4.accept(this);
		//lable = 0;

		return null;

	}

	public Integer visit(MethodDeclaration md){

		function_map = new HashMap<>();

		lable = 0;
		System.out.print("func" + " " + curr_classname + "." + md.f2.f0.toString() + "(this ");
		md.f4.accept(this);
		System.out.print(")");
		System.out.println();

		add_map = true;
		md.f7.accept(this);
		add_map = false;

		md.f8.accept(this);

		System.out.println("ret" + " " + "t." + md.f10.accept(this).toString());

		return null;
	}

	public Integer visit(VarDeclaration vd){

		if(fields_define){
			curr_parameter_name = vd.f1.f0.toString();
			vd.f0.accept(this);
		}

		if(add_map){
			curr_parameter_name = vd.f1.f0.toString();
			vd.f0.accept(this);
		}

		return null;
	}

	public Integer visit(FormalParameterList fp){
		add_map = true;
		fp.f0.accept(this);
		fp.f1.accept(this);
		add_map = false;
		return null;
	}

	public Integer visit(FormalParameter fp){
		curr_parameter_name = fp.f1.f0.toString();
		fp.f0.accept(this);
		System.out.print(fp.f1.f0.toString() + " ");
		return null;
	}

	public Integer visit(FormalParameterRest fp){
		fp.f1.accept(this);
		return null;
	}


	public Integer visit(MainClass mc){

		System.out.println("func Main()");

		lable = 0;

		mc.f15.accept(this);

		System.out.println("ret");

		return null;

	}

	public Integer visit(Statement s){

		s.f0.accept(this);
		return null;

	}

	public Integer visit(PrintStatement ps){
		Integer tmp = ps.f2.accept(this);
		System.out.println("PrintIntS(t." + tmp.toString() + ")");
		return tmp;
	}

	public Integer visit(AssignmentStatement as){
		Integer tmp = as.f2.accept(this);

		if(!inner_class_map.isEmpty() && inner_class_map.containsKey(as.f0.f0.toString())){
			LinkedList<String> value_list = new LinkedList<>();
			value_list = inner_class_map.get(as.f0.f0.toString());
			String value = value_list.get(0);
			Integer index = Integer.valueOf(value)*4;
			System.out.println("[this + " + index.toString() + "] = t." + tmp.toString());
			return null;
		}
		System.out.println(as.f0.f0.toString() + " = " + "t." + tmp.toString());												//class may be wrong????

		return null;
	}

	public Integer visit(ArrayAssignmentStatement as){
		Integer tmp = as.f2.accept(this);
		Integer tmp1 = as.f5.accept(this);

		if(!inner_class_map.isEmpty() && inner_class_map.containsKey(as.f0.f0.toString())){
			LinkedList<String> value_list = new LinkedList<>();
			value_list = inner_class_map.get(as.f0.f0.toString());
			String value = value_list.get(0);
			Integer index = Integer.valueOf(value)*4;

			Integer tmp_array = lable;
			System.out.println("t." + tmp_array.toString() + " = " + "[this + " + index.toString() + "]");
			lable = lable + 1;
			System.out.println("t." + lable.toString() + " = " + "[t." + tmp_array.toString() + "]");
			System.out.println("t." + lable.toString() + " = " + "Lt(t." + tmp.toString() + " t." + lable.toString() + ")");
			System.out.println("if t." + lable.toString() + " goto :bounds" + bound_lable.toString());
			System.out.println("Error(\"array index out of bounds\")");
			System.out.println("bounds" + bound_lable.toString() + ":");
			bound_lable = bound_lable + 1;
			System.out.println("t." + lable.toString() + " = MulS(t." + tmp.toString() + " 4)");
			System.out.println("t." + lable.toString() + " = Add(t." + lable.toString() + " t." + tmp_array.toString() + ")");
			System.out.println("[t." + lable.toString() + "+4] = t." + tmp1.toString());
			lable = lable + 1;

			return null;
		}

		//System.out.println(as.f0.f0.toString() + " = " + "t." + tmp.toString());
		Integer tmp_array = lable;
		System.out.println("t." + tmp_array.toString() + " = " + as.f0.f0.toString());
		lable = lable + 1;
		System.out.println("t." + lable.toString() + " = " + "[t." + tmp_array.toString() + "]");
		System.out.println("t." + lable.toString() + " = " + "Lt(t." + tmp.toString() + " t." + lable.toString() + ")");
		System.out.println("if t." + lable.toString() + " goto :bounds" + bound_lable.toString());
		System.out.println("Error(\"array index out of bounds\")");
		System.out.println("bounds" + bound_lable.toString() + ":");
		bound_lable = bound_lable + 1;
		System.out.println("t." + lable.toString() + " = MulS(t." + tmp.toString() + " 4)");
		System.out.println("t." + lable.toString() + " = Add(t." + lable.toString() + " t." + tmp_array.toString() + ")");
		System.out.println("[t." + lable.toString() + "+4] = t." + tmp1.toString());
		lable = lable + 1;
		return null;
	}

	public Integer visit(IfStatement is){
		Integer tmp = is.f2.accept(this);
		Integer curr_lable = if_lable;
		if_lable = if_lable + 1;

		System.out.println("if0" + " t." + tmp.toString() + " goto :if" + curr_lable.toString() + "_else");
		//System.out.print("	");
		is.f4.accept(this);
		System.out.println("goto :if" + curr_lable.toString() + "_end");
		System.out.println("if" + curr_lable.toString() + "_else:");
		is.f6.accept(this);
		System.out.println("if" + curr_lable.toString() + "_end:");

		return null;
	}

	public Integer visit(WhileStatement ws){

		Integer curr_lable = while_lable;
		while_lable = while_lable + 1;

		System.out.println("while" + curr_lable.toString() + "_top:");
		Integer tmp = ws.f2.accept(this);
		System.out.println("if0 t." + tmp.toString() + " goto :while" + curr_lable.toString() + "_end");
		ws.f4.accept(this);
		System.out.println("goto :while" + curr_lable.toString() + "_top");
		System.out.println("while" + curr_lable.toString() + "_end:");

		return null;
	}

	public Integer visit(CompareExpression ce){
		Integer tmp1 = ce.f0.accept(this);
		Integer tmp2 = ce.f2.accept(this);

		System.out.println("t." + lable.toString() + " = " + "LtS(t." + tmp1.toString() + " " + "t." + tmp2.toString() + ")");

		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}

	public Integer visit(ArrayAllocationExpression ae){
		Integer tmp1 = ae.f3.accept(this);
		System.out.println("t." + lable.toString() + " = call :AllocArray(t." + tmp1.toString() + ")");
		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}



	public Integer visit(Expression e){

		Integer tmp = e.f0.accept(this);
		return tmp;

	}


	public Integer visit(PlusExpression pe){

		Integer tmp1 = pe.f0.accept(this);
		Integer tmp2 = pe.f2.accept(this);

		System.out.println("t." + lable.toString() + " = " + "Add(" + "t." + tmp1.toString() + " " + "t." + tmp2.toString() + ")");

		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}

	public Integer visit(MinusExpression me){

		Integer tmp1 = me.f0.accept(this);
		Integer tmp2 = me.f2.accept(this);

		System.out.println("t." + lable.toString() + " = " + "Sub(" + "t." + tmp1.toString() + " " + "t." + tmp2.toString() + ")");

		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}

	public Integer visit(TimesExpression te){

		Integer tmp1 = te.f0.accept(this);
		Integer tmp2 = te.f2.accept(this);

		System.out.println("t." + lable.toString() + " = " + "MulS(" + "t." + tmp1.toString() + " " + "t." + tmp2.toString() + ")");

		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}

	public Integer visit(BracketExpression be){

		Integer tmp = be.f1.accept(this);
		return tmp;
	}

	public Integer visit(AndExpression ae){

		Integer tmp1 = ae.f0.accept(this);

		Integer curr_lable = ss_lable;

		ss_lable = ss_lable + 1;

		System.out.println("if0 t." + tmp1.toString() + " goto :ss" + curr_lable.toString() + "_else");

		Integer tmp2 = ae.f2.accept(this);

		System.out.println("goto :ss" + curr_lable.toString() + "_end");

		System.out.println("ss" + curr_lable.toString() + "_else:");

		System.out.println("t." + tmp2.toString() + " = 0");

		System.out.println("ss" + curr_lable.toString() + "_end:");

		return tmp2;
	}

	public Integer visit(NotExpression ne){

		Integer tmp1 = ne.f1.accept(this);

		System.out.println("t." + lable.toString() + " = " + "Sub(1 " + "t." + tmp1.toString() + ")");
		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
	}

	public Integer visit(MessageSend ms){
		
		call_function = true;

		parameter_list = new LinkedList<>();
		ms.f4.accept(this);		//read parameter

		Integer tmp1 = ms.f0.accept(this);

		if(inner_call){
			String method = ms.f2.f0.toString();

			int index = method_list.indexOf(method);
			

			System.out.println("t." + lable.toString() + " = " + "[this]");
			System.out.println("t." + lable.toString() + " = " + "[" + "t." + lable.toString() + "+" + Integer.toString(index*4) + "]");
			Integer method_lable = lable;
			lable = lable + 1;

								
			

			System.out.print("t." + lable.toString() + " = " + "call t." + method_lable.toString() + "(this ");
			for(String s : parameter_list) System.out.print(s + " ");
			System.out.print(")");
			System.out.println();

			call_function = false;
			inner_call = false;
			Integer tmp = lable;
			lable = lable + 1;
			return tmp;
		}

		//Integer tmp1 = ms.f0.accept(this);								//t.0
		String method = ms.f2.f0.toString();

		int index = method_list.indexOf(method);

		//System.out.println("........." + index);

		System.out.println("t." + lable.toString() + " = " + "[" + "t." + tmp1.toString() + "]");
		System.out.println("t." + lable.toString() + " = " + "[" + "t." + lable.toString() + "+" + Integer.toString(index*4) + "]");
		Integer method_lable = lable;
		lable = lable + 1;

		//parameter_list = new LinkedList<>();
		//ms.f4.accept(this);							//read parameter
		
		System.out.print("t." + lable.toString() + " = " + "call t." + method_lable.toString() + "(t." + tmp1.toString() + " ");
		for(String s : parameter_list) System.out.print(s + " ");
		System.out.print(")");
		System.out.println();

		call_function = false;
		Integer tmp = lable;
		lable = lable + 1;
		return tmp;
		
		//Integer tmp2 = ms.f2.accept(this);
	}

	public Integer visit(ExpressionList el){

		//if(call_function){
			Integer tmp1 = el.f0.accept(this);
			parameter_list.add("t." + tmp1.toString());
			el.f1.accept(this);
			return null;
		//}
	}

	public Integer visit(ExpressionRest er){

		Integer tmp1 = er.f1.accept(this);
		parameter_list.add("t." + tmp1.toString());
		return null;
	}



	public Integer visit(PrimaryExpression pe){

		Integer tmp = pe.f0.accept(this);
		return tmp;
	}

	public Integer visit(ThisExpression te){

		if(call_function){
			inner_call = true; 
			String name = base_map.get("this");
			class_map = class_list.get(name);
			method_list = class_map.get(name + "virtual table");
		}

		System.out.println("t." + lable.toString() + " = this");

		Integer tmp = lable;
		lable = lable + 1;

		return tmp;
	}

	public Integer visit(AllocationExpression ae){
		Integer tmp = lable;										//class lable
		String class_name = ae.f1.f0.toString();
		class_map = class_list.get(class_name);
		int size = class_map.size() * 4;

		method_list = class_map.get(class_name + "virtual table");

		System.out.println("t." + lable.toString() + " = " + "HeapAllocZ(" + Integer.toString(size) + ")");
		System.out.println("[t." + lable.toString() + "] = :" + "vmt_" + class_name);

		System.out.println("if t." + lable.toString() + " goto :null" + (null_lable).toString());							//if_lable

		System.out.println("	Error(\"null pointer\")");
		System.out.println("null" + (null_lable).toString() + ":");

		null_lable = null_lable + 1;

		lable = lable + 1;
		return tmp;
	}

	public Integer visit(IntegerLiteral il){

		//if(call_function){
		//	System.out.print(il.f0.toString() + " ");
		//	return null;
		//}

		//else{
		Integer tmp = lable;

		System.out.println("t." + lable.toString() + " = " + il.f0.toString());

		lable = lable + 1;

		return tmp;
		//}
	}

	public Integer visit(TrueLiteral tl){
		Integer tmp = lable;

		System.out.println("t." + lable.toString() + " = 1" );

		lable = lable + 1;

		return tmp;
	}

	public Integer visit(FalseLiteral fl){
		Integer tmp = lable;

		System.out.println("t." + lable.toString() + " = 0" );

		lable = lable + 1;

		return tmp;
	}

	public Integer visit(Identifier i){

		if(add_map){
			if(class_list.containsKey(i.f0.toString())){
				function_map.put(curr_parameter_name, i.f0.toString());
				return null;
			}
		}

		if(fields_define){
			if(class_list.containsKey(i.f0.toString())){
				base_map.put(curr_parameter_name, i.f0.toString());
				return null;
			}
		}

		if(call_function){
			if(base_map.containsKey(i.f0.toString())){
				String name = base_map.get(i.f0.toString());
				class_map = class_list.get(name);
				method_list = class_map.get(name + "virtual table");
			}
			else if(function_map.containsKey(i.f0.toString())){
				String name = function_map.get(i.f0.toString());
				class_map = class_list.get(name);
				method_list = class_map.get(name + "virtual table");
			}
		}
		/////////////////////
		//if map has identifier another treat
		//should be departed into classmap and method map////
		////////////////////
		//if(call_function){
		//	System.out.print(i.f0.toString() + " ");
		//	return null;
		//}

		//else{
		Integer tmp = lable;

		if(!inner_class_map.isEmpty() && inner_class_map.containsKey(i.f0.toString())){
			LinkedList<String> value_list = new LinkedList<>();
			value_list = inner_class_map.get(i.f0.toString());
			String value = value_list.get(0);
			Integer index = Integer.valueOf(value)*4;
			System.out.println("t." + lable.toString() + " = " + "[this + " + index.toString() + "]");

			lable = lable + 1;
			return tmp;
		}
			

			System.out.println("t." + lable.toString() + " = " + i.f0.toString());

			lable = lable + 1;
			return tmp;
		//}
	}

	public Integer visit(ArrayLookup al){
		Integer tmp1 = al.f0.accept(this);
		Integer tmp2 = al.f2.accept(this);

		System.out.println("t." + lable.toString() + " = " + "[t." + tmp1.toString() + "]");
		System.out.println("t." + lable.toString() + " = " + "Lt(t." + tmp2.toString() + " t." + lable.toString() + ")");
		System.out.println("if t." + lable.toString() + " goto :bounds" + bound_lable.toString());
		System.out.println("Error(\"array index out of bounds\")");
		System.out.println("bounds" + bound_lable.toString() + ":");

		bound_lable = bound_lable + 1;

		System.out.println("t." + lable.toString() + " = MulS(t." + tmp2.toString() + " 4)");
		System.out.println("t." + lable.toString() + " = Add(t." + lable.toString() + " t." + tmp1.toString() + ")");

		Integer tmp = lable;
		lable = lable + 1;

		System.out.println("t." + lable.toString() + " = [t." + tmp.toString() + "+4]");

		tmp = lable;
		lable = lable + 1;
		return tmp;
	}




}