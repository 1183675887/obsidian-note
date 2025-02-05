package reflection;
import java.util.*;
import java.lang.reflect.*;
//该程序使用反射来打印类的所有功能
public class ReflectionTest {
	public static void main(String[] args) 
	   throws ReflectiveOperationException
	{    //从命令行参数或用户输入中读取类名
		String name;
		if(args.length > 0) name = args[0];
		else {
			var in = new Scanner(System.in);
			System.out.println("Enter class name (e.g. java.util.Date): ");
			name = in.next();
		} //打印类名和超类名（如果！= Object）
		Class cl = Class.forName(name);
		Class supercl = cl.getSuperclass();
		String modifiers = Modifier.toString(cl.getModifiers());
		if(modifiers.length() > 0) System.out.print(modifiers + " ");
		System.out.print("class " + name);
		if(supercl != null && supercl != Object.class) System.out.print(" extends "
				+ supercl.getName());
		System.out.print("\n{\n");
		printConstructors(cl);
		System.out.println();
		printMethods(cl);
		System.out.println();
		printFields(cl);
		System.out.println("}");		
	}    //打印一个类的所有构造函数
	public static void printConstructors(Class cl) {
		Constructor[] constructors = cl.getDeclaredConstructors();
		for(Constructor c : constructors) {
			String name = c.getName();
			System.out.println(" ");
			String modifiers = Modifier.toString(c.getModifiers());
			if(modifiers.length() > 0 ) System.out.print(modifiers + " ");
			System.out.print(name + "(");
			//打印参数类型
			Class[] paramTypes = c.getParameterTypes();
			for(int j = 0; j < paramTypes.length; j++) {
				if(j > 0) System.out.print(","); 
					System.out.print(paramTypes[j].getName());
			}
			System.out.println(");");
		}
	}   //打印类的所有方法
	public static void printMethods(Class cl) {
		Method[] methods = cl.getDeclaredMethods();
		for(Method m : methods) {
			Class retType = m.getReturnType();
			String name = m.getName();
			System.out.print(" ");
			//打印修饰符，返回类型和方法名称
			String modifiers = Modifier.toString(m.getModifiers());
			if(modifiers.length() > 0) System.out.print(modifiers + " ");
			System.out.print(retType.getName() + " " + name + "(");
			//打印参数类型
			Class[] paramTypes = m.getParameterTypes();
			for(int j = 0; j < paramTypes.length; j++) {
				if(j > 0) System.out.print(paramTypes[j].getName());
			}
			System.out.println(");");
		}
	}        //打印类的所有字段
	public static void printFields(Class cl) {
		Field[] fields = cl.getDeclaredFields();
		for(Field f : fields) {
			Class type = f.getType();
			String name = f.getName();
			System.out.println("  ");
			String modifiers = Modifier.toString(f.getModifiers());
			if(modifiers.length() > 0) System.out.print(modifiers + " ");
			System.out.println(type.getName() + " " +name + ";");
		}
	}
}
