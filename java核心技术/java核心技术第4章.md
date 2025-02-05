---
title: java核心技术第4章
date: 2020-05-18 15:05:36
tags: java
---

这章讲解面向对象程序设计概述，使用预定义类，用户自定义类，静态字段和静态方法，方法参数，对象构造，包。

<!--more-->

## 第四章，对象与类

### 4.1.面向对象程序设计概述

面向对象程序设计(OOP)是当今主流的程序设计范型。OOP将数据放在第一位，然后才考虑操作数据的算法。

#### 4.1.1.类

类(class)是构造对象的模板或蓝图。我们可以将类想象成制作小甜饼的模具，将对象想象为小甜饼。由类构造对象的过程称为创建类的实例。

封装(有时称为数据隐藏)是处理对象的一个重要概念。对象中的数据称为实例字段，操作数据的过程称为方法。作为一个类的实例，特定对象都有一组特定的实例字段值。这些值的集合就是这个对象的当前状态。

实现封装的关键在于，绝对不能让类中的方法直接访问类的实例字段。程序只能通过对象的方法与对象数据进行交互。

#### 4.1.2.对象

要想使用OOP，一一定要清楚对象的三个主要特性:

1.对象的行为-----------可以对对象完成那些操作，或者可以对对象应用哪些方法？

2.对象的状态-----------当调用那些方法时，对象会如何响应？

3.对象的标识-----------如何区分具有相同行为与状态的不同对象？

#### 4.1.3.识别类

编写程序时，首先从识别类开始，然后再为各个类添加方法。一般是在分析问题的过程中寻找名词，而方法对应着动词。当然，这只是一种经验。

#### 4.1.4.类之间的关系

在类之间，最常见的关系有

1.依赖--------这个类的方法使用或操纵另一个类的对象，我们就说一个类依赖于另一个类。

2.聚合-------一个对象包含一些对象。

3.继承-------类A扩展B，很多方法来源于B。

### 4.2.使用预定义类

要想使用对象，首先必须构造对象，并指定其初始状态。然后对对象应用方法。

在java程序设计语言中，要使用构造器(或构造函数)构造新实例。构造器是一种特殊的方法，用来构造并初始化对象。

构造器的名字应该与类名相同。比如，Date类的构造器名为Date。想要构造一个Date对象，需要在构造器前面加上new操作符。

```java
new Date()        //这个表达式构造了一个新对象。这个对象被初始化为当前的日期和时间
```

如果需要的话，可以将这个对象传递给一个方法。

```java
System.out.println(new Date());
```

或者，也可以对刚刚创建的对象应用一个方法。比如，Date类中有一个toString方法。这个方法将返回日期的字符串描述。

```java
String s = new Date().toString();
```

通常，你会希望构造的对象可以多次使用，因此，需要将对象存放在一个变量中。

```java
Date birthday = new Date();
```

对象变量birthday，引用了新构造的对象。一定要认识到，对象变量并没有实际包含一个对象，它只是引用一个对象。

在java中，任何对象变量的值都是对存储在另一个地方的某个对象的引用。new操作符的返回值也是一个引用。

```java
Date deadline = new Date();
```

表达式new Date()构造了一个Date类型的对象，它的值是对新创建对象的一个引用。这个引用存储在变量deadline中。

#### 4.2.3.更改器方法与访问器方法

在调用更改器方法后，对象的状态会改变。

只访问对象而不修改对象的方法有时称为访问器方法。

### 4.3.用户自定义类

下面显示了一个Employee类的实际使用。

```java
import java.time.*;
public class EmployeeTest {

	public static void main(String[] args) {
		Employee[] staff = new Employee[3];
		//结合new来调用构造器
		staff[0] = new Employee("Carl Cracker", 75000, 1987, 12, 15);
		staff[1] = new Employee("Harry Hacker", 50000, 1989, 10, 11);
		staff[2] = new Employee("Tony Tester", 40000, 1990, 3, 15);
		
        for(Employee e : staff)   //打印各个员工的信息
			e.raiseSalary(5);
		for(Employee e : staff)
			System.out.println("name=" + e.getName() + ",salary=" + e.getSalary() +
					",hireDay="+ e.getHireDay());
	}
}
//剖析类
class Employee{
	private String name;           //privata确保只有Employee类能读写这些数据
	private double salary;
	private LocalDate hireDay;
	
	public Employee(String n,double s, int year, int month, int day) {//构造方法（构造器），与类名相同
		this.name = n;
		this.salary = s;
		this.hireDay = LocalDate.of(year, month, day);
	}
	//3个访问器
	public String getName() {
		return this.name;
	}
	public double getSalary() {
		return this.salary;
	}
	public LocalDate getHireDay() {
		return this.hireDay;
	}

                //更改器
	public void raiseSalary(double byPercent) {
		double raise = this.salary + byPercent / 100;
		this.salary += raise;
	}
}
```

#### 4.3.5.用var声明局部变量

在java10中，如果可以从变量的初始值推导出它们的类型，那么可以用var关键字声明局部变量，而无须指定类型。

```java
var harry = new Employee("Harry Hacker", 50000, 1989, 10, 11);
```

如果无须了解任何java API就能从等号右边明显看出类型，在这种情况下可以使用var表示法。

注意关键字var只能用于方法中的局部变量。参数和字段的类型必须声明。

#### 4.3.7.隐式参数和显式参数

方法用于操作对象以及存取它们的实例字段。

```java
public void raiseSalary(double byPercent) {
		double raise = salary + byPercent / 100;
		salary += raise;
	}
```

调用这个方法的对象的salary实例字段设置为一个新值。考虑下面这个调用。

```java
number007.raiseSalary(5);   //结果是将number007.salary字段的值增加百分之5。
```

具体的讲，这个调用将执行以下指令。

```java
double raise = number007.salary*5/100;
number007.salary += raise;
```

raiseSalary方法有两个参数，第一个参数称为隐式参数，是出现在方法名前的Employee类型的对象。第二个参数是位于方法名后面括号中的数值，这是一个显式参数。

可以看到，显式参数显式的列在方法声明中，例如double byPercent。隐式参数没有出现在方法声明中。

在每一个方法中，关键字this指示隐式参数。如果喜欢的话，可以改写方法。

```java
public void raiseSalary(double byPercent) {
		double raise = this.salary + byPercent / 100;
		this.salary += raise;
	}
```

这样可以将实例字段与局部变量明显的区分开来。

#### 4.3.9.基于类的访问权限

一个方法可以访问所属类的所有对象的私有数据。

#### 4.3.10.私有方法

大多数时候方法都被设计为公共的，但有时候需要设计为私有的，只需要把public改为private即可。

#### 4.3.11.final实例字段

可以将实例字段定义为final。这样的字段必须在构造对象时初始化。也就是说，必须确保在每一个构造器执行之后，这个字段的值以及设置，并且以后不能再修改这个字段。例如，可以将Employee类中的name字段声明为final，因为在对象构造之后，这个值不会再改变，即没有setName方法。

```java
class Employee
{
    private final String name;
}
```

final修饰符对于类型为基本类型或者不可变类的字段尤其有用。

### 4.4.静态字段与静态方法

#### 4.4.1.静态字段

如果将一个字段定义为static，每个类只有一个这样的字段。而对于非静态的实例字段，每个对象都有自己的一个副本。

例如，假设需要给每一个员工赋予唯一的标识码。这里给Employee类添加一个实例字段id和一个静态字段nextId。

```java
class Employee
{
  private static int nextId = 1;
  private int id;
}
```

现在，每一个Employee对象都有一个自己的id字段，但这个类的所有实例将共享一个nextId字段。换句话说，如果有1000个Employe对象，则有1000个实例字段id，分别对应每一个对象。但是，只有一个静态字段nextId。即使没有Employee对象，静态字段nextId也存在。它属于类，而不属于任何单个的对象。

#### 4.4.2.静态常量

静态变量使用得比较少，但静态常量却很常用。

```java
public class Math{
    public static final double Pi = 3.1415
}
```

#### 4.4.3.静态方法

静态方法是不再对象上执行的方法。例如，Math类的pow方法就是一个静态方法。

```java
Math.pow(x,a);
```

可以认为静态方法是没有this参数的方法。静态方法可以访问静态字段。

下面两种情况下可以使用静态方法。

1.方法不需要访问对象状态，因为它需要的所有参数都通过显式参数提供(例如，Math.pow)。

2.方法只需要访问类的静态字段(例如，Employee.getNextId)。

#### 4.4.4.工厂方法

静态方法还有另外一种常见的用途。就是使用静态工厂方法类构造对象。

```java
NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
NumberFormat percentFormatter = NumberFormat.getPercentInstance();
doublc x = 0.1;
System.out.println(currencyFormatter.format(x));    //prints $0.10
System.out.println(percentFormatter.format(x));     //print 10% 
```

### 4.5.方法参数

首先回顾一下在程序设计语言中关于如何将参数传递给方法(或函数)的一些专业术语。按值调用表示方法接受的是调用者提供提供的值。而按引用调用表示方法接收的是调用者提供的变量地址。

方法可以修改按引用调用的变量的值，而不能修改按值传递的变量的值。java程序设计语言总是采用按值调用。也就是说，方法得到的是所有参数值的一个副本。具体来讲，方法不能修改传递给它的任何参数变量的内容。

```java
public class ParamTest {

	public static void main(String[] args) {
		//测试1:方法不能修改数值参数
		System.out.println("测试tripleValue:");
		double percent = 10;
		System.out.println("之前:percent=" + percent);
		tripleValue(percent);
		System.out.println("之后:percent=" + percent);
		//测试2:方法可以更改对象参数的状态
		System.out.println("\n测试tripleSalary:");
		var harry = new Employee("Harry", 50000);
		System.out.println("之前:salary=" + harry.getSalary());
		tripleSalary(harry);
		System.out.println("之后:salary=" + harry.getSalary());
		//测试3:方法无法将新对象附加到对象参数
		System.out.println("\n测试swap:");
		var a = new Employee("Alice", 70000);
		var b = new Employee("Bob", 60000);
		System.out.println("之前:a=" + a.getName());
		System.out.println("之前:b=" + b.getName());
		swap(a,b);
		System.out.println("之后:a=" + a.getName());
		System.out.println("之后:b=" + b.getName());
	}
	public static void tripleValue(double x) {
		x = 3 * x;
		System.out.println("方法结束:x="+ x);
	}
	public static void tripleSalary(Employee x) {
		x.raiseSalary(200);
		System.out.println("方法结束:salary="+ x.getSalary());
	}
	public static void swap(Employee x,Employee y) {
		Employee temp = x;
		x = y;
		y = temp;
		System.out.println("方法结束:x=" + x.getName());
		System.out.println("方法结束:y=" + y.getName());
	}
}
class Employee{
	private String name;          
	private double salary;
    
	public Employee(String name,double salary) {          //构造器
		this.name = name;
		this.salary = salary;
	}
                  //2个访问器
	public String getName() {
		return name;
	}
	public double getSalary() {
		return salary;
	}
                //更改器
	public void raiseSalary(double byPercent) {
		double raise = this.salary + byPercent / 100;
		this.salary += raise;	
	}
}

```

下面总结一下在java中对方法参数能做什么和不能做什么。

1.方法不能修改基本数据类型的参数(即数值型和布尔型)。

2.方法可以改变对象参数的状态。

3.方法不能让一个对象参数引用一个新的对象。

### 4.6.对象构造

#### 4.6.1.重载

有些类有多个构造器。例如，可以如下构造一个空的StringBuilder对象。

```java
var message = new StringBuilder();
```

或者，可以指定一个初始字符串。

```java
var todoList = new StringBuilder("To do:\n");
```

这种功能叫作重载。如果多个方法有相同的名字，不同的参数，便出现了重载。

#### 4.6.2.默认字段初始化

如果在构造器中没有显示地为字段设置初值，那么就会被自动地赋为默认值：数值为0，布尔值false,对象引用为null。

#### 4.6.3.无参数的构造器

很多类都包含一个无参数的构造器，由无参数构造器创建对象时，对象的状态会设置为适当的默认值。

```java
public Employee(){
    name = "";
    salary = 0;
    hireDay = LocalDate.now();
}
```

如果写一个类时没有编写构造器，就会为你提供一个无参数构造器。这个构造器将所有的实例字段设置为默认值。

如果类中提供了至少一个构造器，但是没有提供无参数的构造器，那么构造对象时如果不提供参数就是不合法的。

请记住，仅当类没有任何其他构造器的时候，你才会得到一个默认的无参数构造器。

#### 4.6.6.调用另一个构造器

关键字this指示一个方法的隐式参数。不过这个关键字还有另一个含义。

如果构造器的第一个语句形如this(......)，这个构造器将调用同一个类的另一个构造器。

```java
public Employee(double s){
    this("Employee #" + nextId, s);
    nextId++;
}
```

当调用new Employee(60000)时，Employee(double)构造器将调用Employee(String,double)构造器。

### 4.7.包

java允许使用包(package)将类组织在一个集合中。借助包名可以方便地组织自己的代码。

#### 4.7.1.包名

使用包的主要原因是确定类名的唯一性。

#### 4.7.2.类的导入

可以使用import语句导入一个特定的类或者整个包。import语句应该位于源文件的顶部(但位于package语句的后面)。

#### 4.10.类设计技巧

1.一定要保证数据私有。

2.一定要对数据进行初始化。

3.不要在类中使用过多的基本类型。

4.不是所有的字段都需要单独的字段访问器和字段更改器。

5.分解有过多职责的类。

6.类名和方法名要能够体现它们的职责。

7.优先使用不可变的类。

