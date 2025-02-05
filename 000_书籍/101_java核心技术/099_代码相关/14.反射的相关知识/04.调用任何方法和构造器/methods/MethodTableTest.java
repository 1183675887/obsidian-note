package methods;
import java.lang.reflect.*;
public class MethodTableTest {

	public static void main(String[] args) 
			throws ReflectiveOperationException{
		//获取指向square和sqrt方法的方法指针
			Method square = MethodTableTest.class.getMethod("square", double.class);
			Method sqrt = Math.class.getMethod("sqrt", double.class);
			//打印x和y值表
			printTable(1, 10, 10, square);
			printTable(1, 10, 10, sqrt);
		}
	//返回数字的平方
	public static double square(double x) {
		return x * x;
	}
	//打印具有方法的x和y值的表
	public static void printTable(double from, double to, int n, Method f)
	        throws ReflectiveOperationException{
		//打印该方法作为表头
		System.out.println(f);
		double dx = (to - from) / (n - 1);
		for(double x = from; x <= to; x += dx) {
			double y = (Double) f.invoke(null, x);
			System.out.printf("%10.4f | %10.4f%n", x, y);
		}
	}
	}

