import java.lang.*;
import java.util.*;
import syntaxtree.*;
import visitor.*;



public class J2V extends GJNoArguDepthFirst<String>{
	public static void main(String [] args){
		try{

			HashMap<String, HashMap<String, LinkedList<String>>> map = new HashMap<>();
			FirstVisit fv = new FirstVisit(map);
			Goal g = new MiniJavaParser(System.in).Goal();
			g.accept(fv);

			for(String s : map.keySet()){
				System.out.println("const vmt_" + s);
				HashMap<String, LinkedList<String>> tmp = map.get(s);
				LinkedList<String> tmp_method = tmp.get(s + "virtual table");
				for(String st: tmp_method)
					System.out.println(" "+ ":" + s + "." + st);
			}

			SecondVisit sv = new SecondVisit(map);
			g.accept(sv);

			System.out.println("func AllocArray(size)");
			System.out.println("bytes = MulS(size 4)");
			System.out.println("bytes = Add(bytes 4)");
			System.out.println("v = HeapAllocZ(bytes)");
			System.out.println("[v] = size");
			System.out.println("ret v");
		}

		catch(Exception e){
			e.printStackTrace();
    		System.out.println("error");
		}
	}
}