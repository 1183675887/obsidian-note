package methods;
import java.lang.reflect.*;
public class MethodTableTest {

	public static void main(String[] args) 
			throws ReflectiveOperationException{
		//��ȡָ��square��sqrt�����ķ���ָ��
			Method square = MethodTableTest.class.getMethod("square", double.class);
			Method sqrt = Math.class.getMethod("sqrt", double.class);
			//��ӡx��yֵ��
			printTable(1, 10, 10, square);
			printTable(1, 10, 10, sqrt);
		}
	//�������ֵ�ƽ��
	public static double square(double x) {
		return x * x;
	}
	//��ӡ���з�����x��yֵ�ı�
	public static void printTable(double from, double to, int n, Method f)
	        throws ReflectiveOperationException{
		//��ӡ�÷�����Ϊ��ͷ
		System.out.println(f);
		double dx = (to - from) / (n - 1);
		for(double x = from; x <= to; x += dx) {
			double y = (Double) f.invoke(null, x);
			System.out.printf("%10.4f | %10.4f%n", x, y);
		}
	}
	}

