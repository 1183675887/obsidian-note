package arrays;
import java.lang.reflect.*;
import java.util.*;
public class CopyOfTest {
//该程序演示了如何使用反射来处理数组
	public static void main(String[] args) {
		int[] a = {1,2,3};
		a = (int[]) goodCopyOf(a,10);
		System.out.println(Arrays.toString(a));
		String[] b = {"Tom", "Dick", "Harry"};
		b = (String[]) goodCopyOf(b, 10);
		System.out.println(Arrays.toString(b));
		System.out.println("以下调用将生成异常. ");
		b = (String[]) badCopyOf(b,10);
		}
	//此方法尝试通过分配新数组并复制所有元素来增大数组
	public static Object[] badCopyOf(Object[] a, int newLength) {
		var newArray = new Object[newLength];
		System.arraycopy(a, 0, newArray, 0, Math.min(a.length, newLength));
		return newArray;
		}
	//此方法通过分配相同类型的新数组并复制所有元素来生成数组
	public static Object goodCopyOf(Object a,int newLength) {
		Class cl = a.getClass();
		if(!cl.isArray()) return null;
		Class componentType = cl.getComponentType();
		int length = Array.getLength(a);
		Object newArray = Array.newInstance(componentType, newLength);
		System.arraycopy(a, 0, newArray, 0, Math.min(length, newLength));
		return newArray;
	}
}

