package reflection;
import java.util.*;
import java.lang.reflect.*;
//�ó���ʹ�÷�������ӡ������й���
public class ReflectionTest {
	public static void main(String[] args) 
	   throws ReflectiveOperationException
	{    //�������в������û������ж�ȡ����
		String name;
		if(args.length > 0) name = args[0];
		else {
			var in = new Scanner(System.in);
			System.out.println("Enter class name (e.g. java.util.Date): ");
			name = in.next();
		} //��ӡ�����ͳ������������= Object��
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
	}    //��ӡһ��������й��캯��
	public static void printConstructors(Class cl) {
		Constructor[] constructors = cl.getDeclaredConstructors();
		for(Constructor c : constructors) {
			String name = c.getName();
			System.out.println(" ");
			String modifiers = Modifier.toString(c.getModifiers());
			if(modifiers.length() > 0 ) System.out.print(modifiers + " ");
			System.out.print(name + "(");
			//��ӡ��������
			Class[] paramTypes = c.getParameterTypes();
			for(int j = 0; j < paramTypes.length; j++) {
				if(j > 0) System.out.print(","); 
					System.out.print(paramTypes[j].getName());
			}
			System.out.println(");");
		}
	}   //��ӡ������з���
	public static void printMethods(Class cl) {
		Method[] methods = cl.getDeclaredMethods();
		for(Method m : methods) {
			Class retType = m.getReturnType();
			String name = m.getName();
			System.out.print(" ");
			//��ӡ���η����������ͺͷ�������
			String modifiers = Modifier.toString(m.getModifiers());
			if(modifiers.length() > 0) System.out.print(modifiers + " ");
			System.out.print(retType.getName() + " " + name + "(");
			//��ӡ��������
			Class[] paramTypes = m.getParameterTypes();
			for(int j = 0; j < paramTypes.length; j++) {
				if(j > 0) System.out.print(paramTypes[j].getName());
			}
			System.out.println(");");
		}
	}        //��ӡ��������ֶ�
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
