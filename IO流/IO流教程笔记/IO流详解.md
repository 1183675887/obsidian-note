# IO流详解

io流用到的地方很多，就比如上传下载，传输，设计模式等....基础打扎实了，才能玩更高端的。

每次在使用IO流之前都需要明确分析如下四点 ：

1.要操作的数据是数据源还是数据目的(也就是要读还是要写)
       2.要操作的设备上的数据是字节还是文本
       3.数据所在的具体设备
       4.是否需要额外功能（比如是否需要转换流、高效流等）

## 一 使用IO流的四点明确

1.明确要操作的数据是数据源还是数据目的(要读还是要写)

```
源：
InputStream　　Reader

目的：
OutputStream　　Writer
```

2.明确要操作的设备上的数据是字节还是文本

```
源：
字节： InputStream     文本： Reader

目的：
字节： OutputStream    文本： Writer
```

3.明确数据所在的具体设备

```
源设备：
硬盘：文件 File开头
内存：数组，字符串
键盘：System.in
网络：Socket

对应目的设备：
硬盘：文件 File开头
内存：数组，字符串
屏幕：System.out
网络：Socket
```

4.明确是否需要额外功能

```
需要转换—— 转换流 InputStreamReader 、OutputStreamWriter

需要高效—— 缓冲流Bufferedxxx

多个源—— 序列流 SequenceInputStream

对象序列化—— ObjectInputStream、ObjectOutputStream

保证数据的输出形式—— 打印流PrintStream 、Printwriter

操作基本数据，保证字节原样性——DataOutputStream、DataInputStream
```

然后，我们再来看下IO流的分类吧

[![40zFns.png](https://z3.ax1x.com/2021/09/24/40zFns.png)](https://imgtu.com/i/40zFns)

## 二 File类

IO流，也就是输入输出流，从文件出发到文件结束，至始至终都离不开文件，所以IO流还得从文件File类讲起。

### 1 File概述

java.io.File类是专门对文件进行操作的类，只能对文件本身进行操作，不能对文件内容进行操作。

java.io.File类是文件和目录路径名的抽象表示，主要用于文件和目录的创建、查找和删除等操作。

第一句就是说File跟流无关，File类不能对文件进行读和写也就是输入和输出！
       第二句就是说File主要表示类似`D:\\文件目录1`与`D:\\文件目录1\\文件.txt`,前者是文件夹（Directory）后者则是文件(file)，而File类就是操作这两者的类。

### 2 构造方法

在java中，一切皆是对象，File类也不例外，不论是哪个对象都应该从该对象的构造说起，所以博主来分析分析`File`类的构造方法。首先从API开始着手。

[![40zCcQ.png](https://z3.ax1x.com/2021/09/24/40zCcQ.png)](https://imgtu.com/i/40zCcQ)

我们主要来学习一下比较常用的三个：

1.public File(String pathname)：通过将给定的**路径名字符串**转换为抽象路径名来创建新的 File实例。

2.public File(String parent, String child) ：从**父路径名字符串和子路径名字符串**创建新的 File实例。

3.public File(File parent, String child) ：从**父抽象路径名和子路径名字符串**创建新的 File实例。

看字描述不够生动不够形象不得劲？没得事，下面进行构造举例，马上就生动形象了，代码如下：

```java
//一个File对象代表硬盘中实际存在的一个文件或者目录。
//File类构造方法不会给你检验这个文件或文件夹是否真实存在，因此无论该路径下是否存在文件或者目录，都不影响File对象的创建。

// 文件路径名 
String path = "D:\\123.txt";
File file1 = new File(path); 

// 文件路径名
String path2 = "D:\\1\\2.txt";
File file2 = new File(path2);     -------------相当于D:\\1\\2.txt

// 通过父路径和子路径字符串
 String parent = "F:\\a";
 String child = "b.txt";
 File file3 = new File(parent, child);  --------相当于F:\\a\\b.txt

// 通过父级File对象和子路径字符串
File parentDir = new File("F:\\a");
String child = "b.txt";
File file4 = new File(parentDir, child); --------相当于F:\\a\\b.txt
```

File类的注意点：

1. 一个File对象代表硬盘中实际存在的一个文件或者目录。
2. File类构造方法不会给你检验这个文件或文件夹是否真实存在，因此无论该路径下是否存在文件或者目录，都不影响File对象的创建。

### 3 常用方法

File的常用方法主要分为获取功能、获取绝对路径和相对路径、判断功能、创建删除功能的方法

#### 3.1 获取功能的方法

|           方法           |                描述                |
| :----------------------: | :--------------------------------: |
| String getAbsolutePath() |    返回此File的绝对路径名字符串    |
|     String getPath()     |     将此File转换为路径名字符串     |
|     String getName()     | 返回由此File表示的文件或目录的名称 |
|      long length()       |    返回由此File表示的文件的长度    |

以上方法测试，代码如下【注意测试以自己的电脑文件夹为准】：注意：length()，表示文件的长度。但是File对象表示目录，则返回值未指定。

```java
public class FileGet {
    public static void main(String[] args) {
        File f = new File("d:/aaa/bbb.java");     
        System.out.println("文件绝对路径:"+f.getAbsolutePath());     //文件绝对路径:d:\aaa\bbb.java
        System.out.println("文件构造路径:"+f.getPath());             //文件构造路径:d:\aaa\bbb.java
        System.out.println("文件名称:"+f.getName());                //文件名称:bbb.java
        System.out.println("文件长度:"+f.length()+"字节");           //文件长度:2116字节
 
        File f2 = new File("d:/aaa");     
        System.out.println("目录绝对路径:"+f2.getAbsolutePath());   //目录绝对路径:d:\aaa
        System.out.println("目录构造路径:"+f2.getPath());           //目录构造路径:d:\aaa
        System.out.println("目录名称:"+f2.getName());              //目录名称:aaa
        System.out.println("目录长度:"+f2.length());               //目录长度:3236
    }
}
```

#### 3.2 创建删除功能的方法

|          方法           |                             描述                             |
| :---------------------: | :----------------------------------------------------------: |
| boolean createNewFile() | 文件不存在，创建一个新的空文件并返回true，文件存在，不创建文件并返回false |
|    boolean delete()     |                 删除由此File表示的文件或目录                 |
|     boolean mkdir()     |                    创建由此File表示的目录                    |
|    boolean mkdirs()     |     创建由此File表示的目录，包括任何必需但不存在的父目录     |

其中，mkdirs()和mkdir()方法类似，但mkdir()，只能创建一级目录，mkdirs()可以创建多级目录比如//a//b//c，所以开发中一般用mkdirs();

这些方法中值得注意的是createNewFile方法以及mkdir与mkdirs的区别。

```java
public class FileCreateDelete {
    public static void main(String[] args) throws IOException {
        // 文件的创建
        File f = new File("aaa.txt");
        System.out.println("是否存在:"+f.exists());         // false
        System.out.println("是否创建:"+f.createNewFile()); // true
        System.out.println("是否创建:"+f.createNewFile()); // 以及创建过了所以再使用createNewFile返回false
        System.out.println("是否存在:"+f.exists());        // true
		
     	// 目录的创建
      	File f2= new File("newDir");	
        System.out.println("是否存在:"+f2.exists());   // false
        System.out.println("是否创建:"+f2.mkdir());	  // true
        System.out.println("是否存在:"+f2.exists());   // true

		// 创建多级目录
      	File f3= new File("newDira\\newDirb");
        System.out.println(f3.mkdir());             // false
        File f4= new File("newDira\\newDirb");
        System.out.println(f4.mkdirs());            // true
      
      	// 文件的删除
       	System.out.println(f.delete());            // true
      
      	// 目录的删除
        System.out.println(f2.delete());           // true
        System.out.println(f4.delete());           // false
    }
}
```

注意：delete方法，如果此File表示目录，则目录必须为空才能删除。

#### 3.3 目录的遍历

|        方法        |                          描述                          |
| :----------------: | :----------------------------------------------------: |
|  String[] list()   | 返回一个String数组，表示该File目录中的所有子文件或目录 |
| File[] listFiles() | 返回一个File数组，表示该File目录中的所有的子文件或目录 |

```java
public class FileFor {
    public static void main(String[] args) {
        File dir = new File("G:\光标");
      
      	//获取当前目录下的文件以及文件夹的名称。
		String[] names = dir.list();
		for(String name : names){
			System.out.println(name);
		}
        //获取当前目录下的文件以及文件夹对象，只要拿到了文件对象，那么就可以获取更多信息
        File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println(file);
        }
    }
}
```

[![40zA7q.png](https://z3.ax1x.com/2021/09/24/40zA7q.png)](https://imgtu.com/i/40zA7q)

listFiles在获取指定目录下的文件或者文件夹时必须满足下面两个条件：

1.指定的目录必须存在

2.指定的必须是目录。否则容易引发返回数组为null，出现NullPointerException异常

#### 3.4 递归遍历文件夹下所有文件以及子文件

```java
//递归遍历文件夹下所有的文件
public class RecursionDirectory {
    
    public static void main(String[] args) {
      File file=new File("D:\\java专属IO测试");
        Recursion(file);
    }
    
    public static void Recursion(File file){
        //1、判断传入的是否是目录
        if(!file.isDirectory()){
            //不是目录直接退出
            return;
        }
        //已经确保了传入的file是目录
        File[] files = file.listFiles();
        //遍历files
        for(File f: files) {
            //如果该目录下文件还是个文件夹就再进行递归遍历其子目录
            if(f.isDirectory()){
                //递归
                Recursion(f);
            }else {
                //如果该目录下文件是个文件，则打印对应的名字
                System.out.println(f.getName());
            }

        }
    }
}
```

## 三 初探IO流

### 1 什么是IO流

我想在座各位肯定经历都过这样的场景。当你编辑一个文本文件也好用eclipse打代码也罢，忘记了ctrl+s ，在你关闭文件的哪一瞬间手残点了个不该点的按钮，但你反应过来，心早已拔凉拔凉的了。

我们把这种数据的传输，可以看做是一种数据的流动，按照流动的方向，以内存为基准，分为输入input和输出output，即流向内存是输入流，流出内存的输出流。Java中I/O操作主要是指使用java.io包下的内容，进行输入、输出操作。输入也叫做读取数据，输出也叫做作写出数据。

### 2 IO流的分类

根据数据的流向分为：输入流和输出流。

1.输入流：把数据从其他设备上读取到内存中的流。

2.输出流：把数据从内存中写出到其他设备上的流。

根据数据的类型分为：字节流和字符流。

1.字节流：以字节为单位，读写数据的流。

2.字符流：以字符为单位，读写数据的流。

分类之后对应的超类如下所示：

|        |         输入流         |         输出流          |
| :----: | :--------------------: | :---------------------: |
| 字节流 | 字节输入流 InputStream | 字节输出流 OutputStream |
| 字符流 |   字符输入流 Reader    |    字符输出流 Writer    |

由这四个类的子类名称基本都是以其父类名作为子类名的后缀。比如InputStream的子类FileInputStream。Reader的子类FileReader。

[![40zkBn.png](https://z3.ax1x.com/2021/09/24/40zkBn.png)](https://imgtu.com/i/40zkBn)

### 3 关于IO的分流向说明

[![40zPXj.png](https://z3.ax1x.com/2021/09/24/40zPXj.png)](https://imgtu.com/i/40zPXj)

[![40zZNV.png](https://z3.ax1x.com/2021/09/24/40zZNV.png)](https://imgtu.com/i/40zZNV)

## 四 字节流OutputStream与InputSteam的故事

OutputStream与InputStream的继承关系

[![40zehT.png](https://z3.ax1x.com/2021/09/24/40zehT.png)](https://imgtu.com/i/40zehT)

### 1 文件的世界里一切皆为字节

我们必须明确一点的是，一切文件数据(文本、图片、视频等)在存储时，都是以二进制数字的形式保存，都一个一个的字节，那么传输时一样如此。所以，字节流可以传输任意文件数据。在操作流的时候，我们要时刻明确，无论使用什么样的流对象，底层传输的始终为二进制数据。

### 2 字节输出流(OutputStream)

java.io.OutputStream抽象类是表示字节输出流的所有类的超类（父类），将指定的字节信息写出到目的地。它定义了字节输出流的基本共性功能方法。

以下是字节输出流的基本共性功能方法: 这五个方法则是字节输出流都具有的方法，由父类OutputStream定义提供，子类都会共享以上方法

|               方法                |                             描述                             |
| :-------------------------------: | :----------------------------------------------------------: |
|           void close()            |         关闭此输出流并释放与此流相关联的任何系统资源         |
|           void flush()            |          刷新此输出流并强制任何缓冲的输出字节被写出          |
|          write(byte[] b)          |        将 b.length个字节从指定的字节数组写入此输出流         |
| write(byte[] b, int off, int len) | 从指定的字节数组写入 len字节，从偏移量 off开始输出到此输出流。即 从off个字节数开始读取一直到len个字节结束 |
|         void write(int b)         |                      将指定的字节输出流                      |

#### 2.1 FileOutputStream类

OutputStream有很多子类，我们从最简单的一个子类FileOutputStream开始。看名字就知道是文件输出流，用于将数据写出到文件。

#### 2.2 FileOutputStream构造方法

不管学啥子，只有是对象，就从构造方法开始！

1.public FileOutputStream(File file)：根据File对象为参数创建对象。

2.public FileOutputStream(String name)：根据名称字符串为参数创建对象。

推荐第二种构造方法【开发常用】：

```java
FileOutputStream outputStream = new FileOutputStream("a.txt");
```

就以上面这句代码来讲，类似这样创建字节输出流对象都做了三件事情：

首先调用系统功能去创建文件（输出流对象才会自动创建），之后创建outputStream对象，最后把foutputStream对象指向这个文件

注意： 创建输出流对象的时候，系统会自动去对应位置创建对应文件，而创建输出流对象的时候，文件不存在则会报FileNotFoundException异常，也就是系统找不到指定的文件异常。

当你创建一个流对象时，必须直接或者间接传入一个文件路径。比如现在我们创建一个FileOutputStream流对象，在该路径下，如果没有这个文件，会创建该文件。如果有这个文件，会清空这个文件的数据。具体代码如下：

```java
public class FileOutputStreamConstructor throws IOException {
    public static void main(String[] args) {
   	 	// 使用File对象创建流对象
        File file = new File("G:\\自动创建的文件夹\\a.txt");
        FileOutputStream fos = new FileOutputStream(file);
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("G:\\b.txt");
    }
}
```

#### 2.3 FileOutputStream写出字节数据

使用FileOutputStream写出字节数据主要通过Write方法，而write方法分如下三种

```java
public void write(int b)
public void write(byte[] b)
public void write(byte[] b,int off,int len)  //从`off`索引开始，`len`个字节
```

##### 2.3.1 写出字节

write(int b)方法，每次可以写出一个字节数据，代码如下

```java
public class IoWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("fos.txt");     
      	// 写出数据
      	fos.write(97); // 写出第1个字节
      	fos.write(98); // 写出第2个字节
      	fos.write(99); // 写出第3个字节
      	// 关闭资源
        fos.close();  ///输出结果：abc
    }
}
```

虽然参数为int类型四个字节，但是只会保留一个字节的信息写出。流操作完毕后，必须释放系统资源，调用close方法，千万记得。

##### 2.3.2 写出字节数组

write(byte[] b)，每次可以写出数组中的数据，代码如下

```java
public class FOSWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("fos.txt");     
      	// 字符串转换为字节数组
      	byte[] b = "烤山药".getBytes();
      	// 写出字节数组数据
      	fos.write(b);
      	// 关闭资源
        fos.close();     //输出结果：烤山药
    }
}
```

##### 2.3.3 写出指定长度字节数组

 write(byte[] b, int off, int len)，每次写出从off索引开始，len个字节，代码如下

```java
public class FOSWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("fos.txt");     
      	// 字符串转换为字节数组
      	byte[] b = "abcde".getBytes();
		// 写出从索引2开始，2个字节。索引2是c，两个字节，也就是cd。
        fos.write(b,2,2);
      	// 关闭资源
        fos.close();             //输出结果：cd
    }
}
```

#### 2.4 FileOutputStream实现数据追加续写，换行

经过以上的代码测试，每次程序运行，每次创建输出流对象，都会清空目标文件中的数据。如何保留目标文件中数据，还能继续追加新数据呢？并且实现换行呢？其实很简单，这个时候我们又要再学习FileOutputStream的另外两个构造方法了，如下

1.public FileOutputStream(File file, boolean append)

2.public FileOutputStream(String name, boolean append)

这两个构造方法，第二个参数中都需要传入一个boolean类型的值，true 表示追加数据，false 表示不追加也就是清空原有数据。这样创建的输出流对象，就可以指定是否追加续写了，至于Windows换行则是 \n\r，下面将会详细讲到。

实现数据追加续写代码如下：

```java
public class FOSWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("fos.txt"，true);     
      	// 字符串转换为字节数组
      	byte[] b = "abcde".getBytes();
		// 写出从索引2开始，2个字节。索引2是c，两个字节，也就是cd。
        fos.write(b);
      	// 关闭资源
        fos.close();           //文件操作前，cd。文件操作后，cdabcde
    }
}
```

Windows系统里，换行符号是\r\n（其他的可以自行上网搜索）,具体代码如下：

```java
public class FOSWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream("fos.txt");   
      	// 定义字节数组
      	byte[] words = {97,98,99,100,101};
      	// 遍历数组
        for (int i = 0; i < words.length; i++) {
          	// 写出一个字节
            fos.write(words[i]);
          	// 写出一个换行, 换行符号转成数组写出                        //输出结果
            fos.write("\r\n".getBytes());                          //a
        }                                                          //b
      	// 关闭资源                                                 //c
        fos.close();                                              //d
    }                                                             //e
}
```

### 3 字节输入流(InputStream)

java.io.InputStream抽象类是表示字节输入流的所有类的超类（父类），可以读取字节信息到内存中。它定义了字节输入流的基本共性功能方法。

字节输入流的基本共性功能方法:

|        方法        |                             描述                             |
| :----------------: | :----------------------------------------------------------: |
|    void close()    |         关闭此输入流并释放与此流相关联的任何系统资源         |
|     int read()     |                 从输入流读取数据的下一个字节                 |
| int read(byte[] b) | 该方法返回的int值代表的是读取了多少个字节，读到几个返回几个，读取不到返回-1 |

#### 3.1 FileInputStream类

java.io.FileInputStream类是文件输入流，从文件中读取字节。

#### 3.2 FileInputStream的构造方法

1.FileInputStream(File file)： 通过打开与实际文件的连接来创建一个 FileInputStream ，该文件由文件系统中的 File对象 file命名。

2.FileInputStream(String name)： 通过打开与实际文件的连接来创建一个 FileInputStream ，该文件由文件系统中的路径名name命名

同样的，推荐使用第二种构造方法：

```java
FileInputStream inputStream = new FileInputStream("a.txt");
```

当你创建一个流对象时，必须传入一个文件路径。该路径下，如果没有该文件,会抛出FileNotFoundException。代码如下：

```java
public class FileInputStreamConstructor throws IOException{
    public static void main(String[] args) {
   	 	// 使用File对象创建流对象
        File file = new File("a.txt");
        FileInputStream fos = new FileInputStream(file);
      
        // 使用文件名称创建流对象
        FileInputStream fos = new FileInputStream("b.txt");
    }
}
```

#### 3.3 FileInputStream读取字节数据

##### 3.3.1 读取字节

read方法，每次可以读取一个字节的数据，提升为int类型，读取到文件末尾，返回`-1`，代码测试如下【read.txt文件中内容为abcde】：

```java
public class FISRead {
    public static void main(String[] args) throws IOException{
      	// 使用文件名称创建流对象
       	FileInputStream fis = new FileInputStream("read.txt");//read.txt文件中内容为abcde
      	// 读取数据，返回一个字节
        int read = fis.read();
        System.out.println((char) read);           //输出结果： a
        read = fis.read();
        System.out.println((char) read);           //输出结果： b
        read = fis.read();
        System.out.println((char) read);           //输出结果： e
        read = fis.read();
        System.out.println((char) read);           //输出结果： d
        read = fis.read();
        System.out.println((char) read);           //输出结果： e
      	// 读取到末尾,返回-1
       	read = fis.read();
        System.out.println( read);                //输出结果： -1
		// 关闭资源
        fis.close();             
    }
}
```

循环改进读取方式，代码如下：

```java
public class FISRead {
    public static void main(String[] args) throws IOException{
      	// 使用文件名称创建流对象
       	FileInputStream fis = new FileInputStream("read.txt");
      	// 定义变量，保存数据
        int b ；
        // 循环读取
        while ((b = fis.read())!=-1) {
            System.out.println((char)b);         //输出结果：  a  b  c  d  e  
        }
		// 关闭资源
        fis.close();
    }
}
```

##### 3.3.2 使用字节数组读取

read(byte[] b)，每次读取b的长度个字节到数组中，返回读取到的有效字节个数，读取到末尾时，返回-1，代码如下：

```java
public class FISRead {
    public static void main(String[] args) throws IOException{
      	// 使用文件名称创建流对象.
       	FileInputStream fis = new FileInputStream("read.txt"); // read.txt文件中内容为abcde
      	// 定义变量，作为有效个数
        int len ；
        // 定义字节数组，作为装字节数据的容器   
        byte[] b = new byte[2];
        // 循环读取
        while (( len= fis.read(b))!=-1) {
           	// 每次读取后,把数组变成字符串打印
            System.out.println(new String(b));      //输出结果： ab   cd      ed
        }
		// 关闭资源
        fis.close();
    }
}
```

由于read.txt文件中内容为abcde，而错误数据d，是由于最后一次读取时，只读取一个字节e，数组中，上次读取的数据没有被完全替换（注意是替换，看下图），所以要通过len，获取有效的字节。

[![40zVA0.png](https://z3.ax1x.com/2021/09/24/40zVA0.png)](https://imgtu.com/i/40zVA0)

代码如下：

```java
public class FISRead {
    public static void main(String[] args) throws IOException{
      	// 使用文件名称创建流对象.
       	FileInputStream fis = new FileInputStream("read.txt"); // 文件中为abcde
      	// 定义变量，作为有效个数
        int len ；
        // 定义字节数组，作为装字节数据的容器   
        byte[] b = new byte[2];
        // 循环读取
        while (( len= fis.read(b))!=-1) {  //len 每次读取的有效字节个数
           	// 每次读取后,把数组的有效字节部分，变成字符串打印
            System.out.println(new String(b，0，len));  //输出结果： ab   cd    e
        } 
		// 关闭资源
        fis.close();
    }
}
```

在开发中一般强烈推荐使用数组读取文件，代码如下：

```java
public class input2 {
    public static void main(String args[]){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("a.txt");
            int len = 0 ;
            byte[] bys = new byte[1024];
            while ((len = inputStream.read(bys)) != -1) {
                System.out.println(new String(bys,0,len));
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
```

#### 3.4 FileInputstream复制图片

复制图片原理

[![40zu3F.png](https://z3.ax1x.com/2021/09/24/40zu3F.png)](https://imgtu.com/i/40zu3F)

复制图片文件，代码如下：

```java
public class Copy {
    public static void main(String[] args) throws IOException {
        // 1.创建流对象
        // 1.1 指定数据源
        FileInputStream fis = new FileInputStream("D:\\test.jpg");
        // 1.2 指定目的地
        FileOutputStream fos = new FileOutputStream("test_copy.jpg");

        // 2.读写数据
        // 2.1 定义数组
        byte[] b = new byte[1024];
        // 2.2 定义长度
        int len;
        // 2.3 循环读取
        while ((len = fis.read(b))!=-1) {
            // 2.4 写出数据
            fos.write(b, 0 , len);
        }

        // 3.关闭资源
        fos.close();
        fis.close();
    }
}
```

复制文本、图片、mp3、视频等的方式一样。

## 五 字符流Reader和Writer的故事

字符流Reader和Writer的故事从它们的继承图开始，啥都不说了，直接看图

[![40zn9U.png](https://z3.ax1x.com/2021/09/24/40zn9U.png)](https://imgtu.com/i/40zn9U)

### 1 字符流

字符流的由来：因为数据编码的不同，因而有了对字符进行高效操作的流对象，字符流本质其实就是基于字节流读取时，去查了指定的码表，而字节流直接读取数据会有乱码的问题（读中文会乱码），这个时候小白同学就看不懂了，没事，咋们先来看个程序：

```java
public class CharaterStream {
    public static void main(String[] args) throws Exception {
        //FileInputStream为操作文件的字符输入流
        FileInputStream inputStream = new FileInputStream("a.txt");//内容为哥敢摸屎

        int len;
        while ((len=inputStream.read())!=-1){
           System.out.print((char)len);        //运行结果：   ??￥??￠????±
        }

    }
}
```

具体现状分析

[![40zKc4.png](https://z3.ax1x.com/2021/09/24/40zKc4.png)](https://imgtu.com/i/40zKc4)

话说，就是你哥我敢摸si,那你哥我肯定也不认识这玩意啊： ??￥??￠????±

字节流读取中文字符时，可能不会显示完整的字符，那是因为一个中文字符占用多个字节存储。那字节流就没办法了吗？不，字节流依旧有办法，只是麻烦了点，代码如下：

```java
public class CharaterStream {
    public static void main(String[] args) throws Exception {

        FileInputStream inputStream = new FileInputStream("a.txt");
        byte[] bytes = new byte[1024];
        int len;
        while ((len=inputStream.read(bytes))!=-1){
           System.out.print(new String(bytes,0,len));   //运行结果： 哥敢摸屎
        }
    }
}
```

这是为啥呢？没错解码的正是String，查看new String()的源码，String构造方法有解码功能，并且默认编码是utf-8，代码如下

```java
this.value = StringCoding.decode(bytes, offset, length);
 
 再点进decode，循序渐进发现，默认编码是UTF-8
```

尽管字节流也能有办法决绝乱码问题，但是还是比较麻烦，于是java就有了字符流，`字符为单位`读写数据，字符流`专门用于处理文本`文件。如果处理纯文本的数据优先考虑字符流，其他情况就只能用字节流了（图片、视频、等等`只文本`例外）。

从另一角度来说：字符流 = 字节流 + 编码表

### 2 字符输入流（Reader）

java.io.Reader抽象类是字符输入流的所有类的超类（父类），可以读取字符信息到内存中。它定义了字符输入流的基本共性功能方法。

字符输入流的共性方法：

|         方法          |                         描述                          |
| :-------------------: | :---------------------------------------------------: |
|     void close()      |       关闭此流并释放与此流相关联的任何系统资源        |
|      int read()       |                 从输入流读取一个字符                  |
| int read(char[] cbuf) | 从输入流中读取一些字符，并将它们存储到字符数组 cbuf中 |

#### 2.1 FileReader类

java.io.FileReader类是读取字符文件的便利类。构造时使用系统默认的字符编码和默认字节缓冲区。

#### 2.2 构造方法

1.FileReader(File file)：创建一个新的 FileReader，给定要读取的File对象。

2.FileReader(String fileName)：创建一个新的 FileReader，给定要读取的文件的字符串名称。

构造方法的使用就算不写应该都很熟悉了吧，代码如下：

```java
public class FileReaderConstructor throws IOException{
    public static void main(String[] args) {
   	 	// 使用File对象创建流对象
        File file = new File("a.txt");
        FileReader fr = new FileReader(file);
      
        // 使用文件名称创建流对象
        FileReader fr = new FileReader("b.txt");
    }
}
```

#### 2.3  FileReader读取字符数据

读取字符：read方法，每次可以读取一个字符的数据，提升为int类型，读取到文件末尾，返回-1，循环读取，代码如下：

```java
public class FRRead {
    public static void main(String[] args) throws IOException {
      	// 使用文件名称创建流对象
       	FileReader fr = new FileReader("a.txt");
      	// 定义变量，保存数据
        int b;
        // 循环读取
        while ((b = fr.read())!=-1) {
            System.out.println((char)b);
        }
		// 关闭资源
        fr.close();
    }
}
```

至于读取的写法类似字节流的写法，只是读取单位不同罢了。

### 3 字符输出流（Writer）

java.io.Writer抽象类是字符输出流的所有类的超类（父类），将指定的字符信息写出到目的地。它同样定义了字符输出流的基本共性功能方法。

字符输出流的基本共性功能方法：

|                   方法                    |                           描述                           |
| :---------------------------------------: | :------------------------------------------------------: |
|             void write(int c)             |                       写入单个字符                       |
|          void write(char[] cbuf)          |                       写入字符数组                       |
| void write(char[] cbuf, int off, int len) | 写入字符数组的某一部分,off数组的开始索引,len写的字符个数 |
|             write(String str)             |                        写入字符串                        |
|    write(String str, int off, int len)    | 写入字符串的某一部分,off字符串的开始索引,len写的字符个数 |
|                  flush()                  |                      刷新该流的缓冲                      |
|                  close()                  |                  关闭此流，但要先刷新它                  |

#### 3.1 FileWriter类

java.io.FileWriter类是写出字符到文件的便利类。构造时使用系统默认的字符编码和默认字节缓冲区。

#### 3.2 构造方法

1.FileWriter(File file)：创建一个新的 FileWriter，给定要读取的File对象。

2.FileWriter(String fileName)：创建一个新的 FileWriter，给定要读取的文件的名称。

依旧是熟悉的构造举例，代码如下：

```java
public class FileWriterConstructor {
    public static void main(String[] args) throws IOException {
   	 	// 第一种：使用File对象创建流对象
        File file = new File("a.txt");
        FileWriter fw = new FileWriter(file);
      
        // 第二种：使用文件名称创建流对象
        FileWriter fw = new FileWriter("b.txt");
    }
}
```

#### 3.3 FileWriter写出数据

写出字符：write(int b)方法，每次可以写出一个字符数据，代码如下：

```java
public class FWWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileWriter fw = new FileWriter("fw.txt");     
      	// 写出数据
      	fw.write(97); // 写出第1个字符
      	fw.write('b'); // 写出第2个字符
      	fw.write('C'); // 写出第3个字符

        // fw.close();   //输出结果： abC
    }                     
}
```

注意，关闭资源时,与FileOutputStream不同。 如果不关闭,数据只是保存到缓冲区，并未保存到文件。

#### 3.4 关闭close和刷新flush

因为内置缓冲区的原因，如果不关闭输出流，无法写出字符到文件中。但是关闭的流对象，是无法继续写出数据的。如果我们既想写出数据，又想继续使用流，就需要flush方法了。

flush：刷新缓冲区，流对象可以继续使用。

close：先刷新缓冲区，然后通知系统释放资源。流对象不可以再被使用了。

flush还是比较有趣的写个程序体会体会： 字符流

```java
public class FlushDemo {
    public static void main(String[] args) throws Exception {
        //源   也就是输入流【读取流】 读取a.txt文件
        FileReader fr=new FileReader("a.txt");  //必须要存在a.txt文件，否则报FileNotFoundException异常
        //目的地  也就是输出流
        FileWriter fw=new FileWriter("b.txt");  //系统会自动创建b.txt，因为它是输出流！
        int len;
        while((len=fr.read())!=-1){
           fw.write(len);
        }
              //注意这里是没有使用close关闭流，开发中不能这样做，但是为了更好的体会flush的作用
    }
}
```

[![40zlu9.png](https://z3.ax1x.com/2021/09/24/40zlu9.png)](https://imgtu.com/i/40zlu9)

运行效果是怎么样的呢？答案是b.txt文件中依旧是空的，是的并没有任何东西，为啥呢？熊dei啊，我在上面就用红色字体特别标注过了，就是这句话：注意关闭资源时,与FileOutputStream不同。如果不关闭,数据只是保存到缓冲区，并未保存到文件。这个时候反应过来了吧，可见实践例子的重要性，编程就是这样，不去敲，永远学不会！！！

所以，我们在以上的代码中再添加下面三句代码，就完美了，b.txt文件就能复制到源文件的数据了！

```java
fr.close();
fw.flush();
fw.close();
```

flush()这个函数是清空的意思，用于清空缓冲区的数据流，进行流的操作时，数据先被读到内存中，然后再用数据写到文件中，那么当你数据读完时，我们如果这时调用close()方法关闭读写流，这时就可能造成数据丢失，为什么呢？因为，读入数据完成时不代表写入数据完成，一部分数据可能会留在缓存区中，这个时候flush()方法就格外重要了。

好了，接下来close使用代码如下：

```java
public class FWWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileWriter fw = new FileWriter("fw.txt");
        // 写出数据，通过flush
        fw.write('刷'); // 写出第1个字符
        fw.flush();
        fw.write('新'); // 继续写出第2个字符，写出成功
        fw.flush();
      
      	// 写出数据，通过close
        fw.write('关'); // 写出第1个字符
        fw.close();
        fw.write('闭'); // 继续写出第2个字符,【报错】java.io.IOException: Stream closed
        fw.close();
    }
}
```

即便是flush方法写出了数据，操作的最后还是要调用close方法，释放系统资源。

#### 3.5 FileWriter的续写和换行

续写和换行：操作类似于FileOutputStream操作，直接上代码：

```java
public class FWWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象，可以续写数据
        FileWriter fw = new FileWriter("fw.txt"，true);     
      	// 写出字符串
        fw.write("测试");
      	// 写出换行
      	fw.write("\r\n");
      	// 写出字符串
  		fw.write("换行");
      	// 关闭资源
        fw.close();             //输出结果   测试      换行
    }
}

```

#### 3.6 FileReader和FileWriter类完成文本文件复制

```java
public class CopyFile {
    public static void main(String[] args) throws IOException {
        //创建输入流对象
        FileReader fr=new FileReader("F:\\新建文件夹\\aa.txt");//文件不存在会抛出java.io.FileNotFoundException
        //创建输出流对象
        FileWriter fw=new FileWriter("C:\\copyaa.txt");
        /*创建输出流做的工作：
         *      1、调用系统资源创建了一个文件
         *      2、创建输出流对象
         *      3、把输出流对象指向文件        
         * */
        //文本文件复制，一次读一个字符
        copyMethod1(fr, fw);
        //文本文件复制，一次读一个字符数组
        copyMethod2(fr, fw);
        
        fr.close();
        fw.close();
    }

    public static void copyMethod1(FileReader fr, FileWriter fw) throws IOException {
        int ch;
        while((ch=fr.read())!=-1) {//读数据
            fw.write(ch);//写数据
        }
        fw.flush();
    }

    public static void copyMethod2(FileReader fr, FileWriter fw) throws IOException {
        char chs[]=new char[1024];
        int len=0;
        while((len=fr.read(chs))!=-1) {//读数据
            fw.write(chs,0,len);//写数据
        }
        fw.flush();
    }
}
```

[![40z1BR.png](https://z3.ax1x.com/2021/09/24/40z1BR.png)](https://imgtu.com/i/40z1BR)

最后再次强调：字符流，只能操作文本文件，不能操作图片，视频等非文本文件。当我们单纯读或者写文本文件时使用字符流，其他情况使用字节流。

## 六 缓冲流

### 1 简要概述

首先认识一下缓冲流,也叫高效流，是对4个FileXxx流的“增强流”。

**缓冲流的基本原理**：

1.使用了底层流对象从具体设备上获取数据，并将数据存储到缓冲区的数组内。

2.通过缓冲区的read()方法从缓冲区获取具体的字符数据，这样就提高了效率。

3.如果用read方法读取字符数据，并存储到另一个容器中，直到读取到了换行符时，将另一个容器临时存储的数据转成字符串返回，就形成了readLine()功能。

也就是说在创建流对象时，会创建一个内置的默认大小的缓冲区数组，通过缓冲区读写，减少系统IO次数，从而提高读写的效率。

缓冲书写格式为BufferedXxx，按照数据类型分类：

1.字节缓冲流：BufferedInputStream，BufferedOutputStream

2.字符缓冲流：BufferedReader，BufferedWriter

### 2 字节缓冲流

构造方法如下：

|                     方法                      |                        描述                        |
| :-------------------------------------------: | :------------------------------------------------: |
|  public BufferedInputStream(InputStream in)   | 创建一个新的缓冲输入流，注意参数类型为InputStream  |
| public BufferedOutputStream(OutputStream out) | 创建一个新的缓冲输出流，注意参数类型为OutputStream |

构造举例代码如下：

```java
//构造方式一： 创建字节缓冲输入流（但是开发中一般常用下面的格式申明）
FileInputStream fps = new FileInputStream(b.txt);
BufferedInputStream bis = new BufferedInputStream(fps)

//构造方式一： 创建字节缓冲输入流
BufferedInputStream bis = new BufferedInputStream(new FileInputStream("b.txt"));

///构造方式二： 创建字节缓冲输出流
BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("b.txt"));
```

#### 2.1 缓冲流的高效

缓冲流读写方法与基本的流是一致的，我们通过复制370多MB的大文件，测试它的效率。

1.基本流，代码如下：

```java
public class BufferedDemo {
    public static void main(String[] args) throws FileNotFoundException {
        // 记录开始时间
      	long start = System.currentTimeMillis();
		// 创建流对象
        try (
        	FileInputStream fis = new FileInputStream("py.exe");//exe文件够大
        	FileOutputStream fos = new FileOutputStream("copyPy.exe")
        ){
        	// 读写数据
            int b;
            while ((b = fis.read()) != -1) {
                fos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		// 记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("普通流复制时间:"+(end - start)+" 毫秒");   //十分钟过去了还在玩命复制中...
    }
}
```

2.缓冲流，代码如下：

```java
public class BufferedDemo {
    public static void main(String[] args) throws FileNotFoundException {
        // 记录开始时间
      	long start = System.currentTimeMillis();
		// 创建流对象
        try (
         BufferedInputStream bis = new BufferedInputStream(new FileInputStream("py.exe"));
	     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("copyPy.exe"));
        ){
        // 读写数据
            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		// 记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("缓冲流复制时间:"+(end - start)+" 毫秒");    //缓冲流复制时间:8016 毫秒
    }
}
```

3.缓冲流更快的方法就是数组，代码如下：

```java
public class BufferedDemo {
    public static void main(String[] args) throws FileNotFoundException {
      	// 记录开始时间
        long start = System.currentTimeMillis();
		// 创建流对象
        try (
		 BufferedInputStream bis = new BufferedInputStream(new FileInputStream("py.exe"));
		 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("copyPy.exe"));
        ){
          	// 读写数据
            int len;
            byte[] bytes = new byte[8*1024];
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0 , len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		// 记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("缓冲流使用数组复制时间:"+(end - start)+" 毫秒");
    }
}
```

### 3 字符缓冲流

​	构造方法，其格式以及原理和字节缓冲流是一样一样的！

|               方法                |                      描述                      |
| :-------------------------------: | :--------------------------------------------: |
| public BufferedReader(Reader in)  | 创建一个新的缓冲输入流，注意参数类型为Reader。 |
| public BufferedWriter(Writer out) |  创建一个新的缓冲输出流，注意参数类型为Writer  |

构造举例，代码如下：

```java
// 创建字符缓冲输入流
BufferedReader br = new BufferedReader(new FileReader("b.txt"));
// 创建字符缓冲输出流
BufferedWriter bw = new BufferedWriter(new FileWriter("b.txt"));
```

#### 3.1 字符缓冲流特有方法

字符缓冲流的基本方法与普通字符流调用方式一致，这里不再阐述，我们来看字符缓冲流具备的特有方法。

|       流       |           方法           |              描述               |
| :------------: | :----------------------: | :-----------------------------: |
| BufferedReader | public String readLine() | 读一行数据。 读取到最后返回null |
| BufferedWriter |  public void newLine()   |     换行,由系统属性定义符号     |

readLine方法演示代码如下：

```java
public class BufferedReaderDemo {
    public static void main(String[] args) throws IOException {
      	 // 创建流对象
        BufferedReader br = new BufferedReader(new FileReader("a.txt"));
		// 定义字符串,保存读取的一行文字
        String line  = null;
      	// 循环读取,读取到最后返回null
        while ((line = br.readLine())!=null) {
            System.out.print(line);
            System.out.println("------");
        }
		// 释放资源
        br.close();
    }
}
```

newLine方法演示代码如下：

```java
public class BufferedWriterDemo throws IOException {
  public static void main(String[] args) throws IOException  {
    	// 创建流对象
  	BufferedWriter bw = new BufferedWriter(new FileWriter("b.txt"));
    	// 写出数据
      bw.write("1");
    	// 写出换行
      bw.newLine();
      bw.write("2");
      bw.newLine();
      bw.write("34");
      bw.newLine();
      bw.write("567");
      bw.newLine();
  	// 释放资源
      bw.close();       //输出结果： 1       2      34          567
  }
}
```

## 七 转换流

何谓转换流？为何由来？暂时带着问题让我们先来了解了解字符编码和字符集！

[![40zMjJ.png](https://z3.ax1x.com/2021/09/24/40zMjJ.png)](https://imgtu.com/i/40zMjJ)

### 1 字符编码与解码

众所周知，计算机中储存的信息都是用二进制数表示的，而我们在屏幕上看到的数字、英文、标点符号、汉字等字符是二进制数转换之后的结果。按照某种规则，将字符存储到计算机中，称为编码 。反之，将存储在计算机中的二进制数按照某种规则解析显示出来，称为解码 。比如说，按照A规则存储，同样按照A规则解析，那么就能显示正确的文本符号。反之，按照A规则存储，再按照B规则解析，就会导致乱码现象。

简单一点的说就是：

编码:字符(能看懂的)--字节(看不懂的)

解码:字节(看不懂的)-->字符(能看懂的)

代码解释就是：

```java
String(byte[] bytes, String charsetName):通过指定的字符集解码字节数组
byte[] getBytes(String charsetName):使用指定的字符集合把字符串编码为字节数组

编码:把看得懂的变成看不懂的
String -- byte[]

解码:把看不懂的变成看得懂的
byte[] -- String
```

字符编码Character Encoding: 就是一套自然语言的字符与二进制数之间的对应规则。而编码表则是生活中文字和计算机中二进制的对应规则。

字符集Charset：也叫编码表。是一个系统支持的所有字符的集合，包括各国家文字、标点符号、图形符号、数字等。

计算机要准确的存储和识别各种字符集符号，需要进行字符编码，一套字符集必然至少有一套字符编码。常见字符集有ASCII字符集、GBK字符集、Unicode字符集等。

[![40z3H1.png](https://z3.ax1x.com/2021/09/24/40z3H1.png)](https://imgtu.com/i/40z3H1)

可见，当指定了编码，它所对应的字符集自然就指定了，所以编码才是我们最终要关心的。

ASCII字符集：

1.ASCII（American Standard Code for Information Interchange，美国信息交换标准代码）是基于拉丁字母的一套电脑编码系统，用于显示现代英语，主要包括控制字符（回车键、退格、换行键等）和可显示字符（英文大小写字符、阿拉伯数字和西文符号）。

2.基本的ASCII字符集，使用7位（bits）表示一个字符，共128字符。ASCII的扩展字符集使用8位（bits）表示一个字符，共256字符，方便支持欧洲常用字符。

ISO-8859-1字符集：

1.拉丁码表，别名Latin-1，用于显示欧洲使用的语言，包括荷兰、丹麦、德语、意大利语、西班牙语等。

2.ISO-8859-1使用单字节编码，兼容ASCII编码。

GBxxx字符集：

1.GB就是国标的意思，是为了显示中文而设计的一套字符集。

2.GB2312：简体中文码表。一个小于127的字符的意义与原来相同。但两个大于127的字符连在一起时，就表示一个汉字，这样大约可以组合了包含7000多个简体汉字，此外数学符号、罗马希腊的字母、日文的假名们都编进去了，连在ASCII里本来就有的数字、标点、字母都统统重新编了两个字节长的编码，这就是常说的"全角"字符，而原来在127号以下的那些就叫"半角"字符了。

3.GBK：最常用的中文码表。是在GB2312标准基础上的扩展规范，使用了双字节编码方案，共收录了21003个汉字，完全兼容GB2312标准，同时支持繁体汉字以及日韩汉字等。

4.GB18030：最新的中文码表。收录汉字70244个，采用多字节编码，每个字可以由1个、2个或4个字节组成。支持中国国内少数民族的文字，同时支持繁体汉字以及日韩汉字等。

Unicode字符集：

1.Unicode编码系统为表达任意语言的任意字符而设计，是业界的一种标准，也称为统一码、标准万国码。

2.它最多使用4个字节的数字来表达每个字母、符号，或者文字。有三种编码方案，UTF-8、UTF-16和UTF-32。最为常用的UTF-8编码。

3.UTF-8编码，可以用来表示Unicode标准中任何字符，它是电子邮件、网页及其他存储或传送文字的应用中，优先采用的编码。互联网工程工作小组（IETF）要求所有互联网协议都必须支持UTF-8编码。所以，我们开发Web应用，也要使用UTF-8编码。它使用一至四个字节为每个字符编码，编码规则：

1. 128个US-ASCII字符，只需一个字节编码。
2. 拉丁文等字符，需要二个字节编码。
3. 大部分常用字（含中文），使用三个字节编码。
4. 其他极少使用的Unicode辅助字符，使用四字节编码。

### 2 编码问题导致乱码

在java开发工具IDEA中，使用FileReader读取项目中的文本文件。由于IDEA的设置，都是默认的UTF-8编码，所以没有任何问题。但是，当读取Windows系统中创建的文本文件时，由于Windows系统的默认是GBK编码，就会出现乱码。

```java
public class ReaderDemo {
    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader("C:\\a.txt");
        int read;
        while ((read = fileReader.read()) != -1) {
            System.out.print((char)read);     //输出结果：���
        }
        fileReader.close();    
    }
}
```

那么如何读取GBK编码的文件呢？ 这个时候就得讲讲转换流了！从另一角度来讲：字符流=字节流+编码表

### 3 InputStreamReader类-----(字节流到字符流的桥梁)

构造方法

|                         方法                          |               描述               |
| :---------------------------------------------------: | :------------------------------: |
|           InputStreamReader(InputStream in)           | 创建一个使用默认字符集的字符流。 |
| InputStreamReader(InputStream in, String charsetName) |   创建一个指定字符集的字符流。   |

构造代码如下：

```java
InputStreamReader isr = new InputStreamReader(new FileInputStream("in.txt"));
InputStreamReader isr2 = new InputStreamReader(new FileInputStream("in.txt") , "GBK");
```

使用转换流解决编码问题

```java
public class ReaderDemo2 {
    public static void main(String[] args) throws IOException {
      	// 定义文件路径,文件为gbk编码
        String FileName = "C:\\A.txt";
      	// 创建流对象,默认UTF8编码
        InputStreamReader isr = new InputStreamReader(new FileInputStream(FileName));
      	// 创建流对象,指定GBK编码
        InputStreamReader isr2 = new InputStreamReader(new FileInputStream(FileName) , "GBK");
		// 定义变量,保存字符
        int read;
      	// 使用默认编码字符流读取,乱码
        while ((read = isr.read()) != -1) {
            System.out.print((char)read);   // �����ʺ      
        }
        isr.close();
      
      	// 使用指定编码字符流读取,正常解析
        while ((read = isr2.read()) != -1) {
            System.out.print((char)read);    // 测试
        }
        isr2.close();
    }
}
```

### 4 OutputStreamWriter类-----(字符流到字节流的桥梁)

转换流java.io.OutputStreamWriter，是Writer的子类，字面看容易混淆会误以为是转为字符流，其实不然，OutputStreamWriter为从字符流到字节流的桥梁。使用指定的字符集将字符编码为字节。它的字符集可以由名称指定，也可以接受平台的默认字符集。

构造方法

|                          方法                           |               描述               |
| :-----------------------------------------------------: | :------------------------------: |
|           OutputStreamWriter(OutputStream in)           | 创建一个使用默认字符集的字符流。 |
| OutputStreamWriter(OutputStream in, String charsetName) |   创建一个指定字符集的字符流。   |

构造举例，代码如下：

```java
OutputStreamWriter isr = new OutputStreamWriter(new FileOutputStream("a.txt"));
OutputStreamWriter isr2 = new OutputStreamWriter(new FileOutputStream("b.txt") , "GBK");
```

指定编码构造对象

```java
public class OutputDemo {
    public static void main(String[] args) throws IOException {
      	// 定义文件路径
        String FileName = "C:\\s.txt";
      	// 创建流对象,默认UTF8编码
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(FileName));
        // 写出数据
      	osw.write("测试"); // 保存为6个字节
        osw.close();
      	
		// 定义文件路径
		String FileName2 = "D:\\A.txt";
     	// 创建流对象,指定GBK编码
        OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(FileName2),"GBK");
        // 写出数据
      	osw2.write("代码");// 保存为4个字节
        osw2.close();
    }
}
```

[![40zY4K.png](https://z3.ax1x.com/2021/09/24/40zY4K.png)](https://imgtu.com/i/40zY4K)

为了达到最高效率，可以考虑在 BufferedReader 内包装 InputStreamReader

```java
BufferedReader in = new BufferedReader(new InputStreamReader(System.in))；
```

## 八 序列化流

### 1 何为序列化

Java 提供了一种对象序列化的机制。用一个字节序列可以表示一个对象，该字节序列包含该对象的数据、对象的类型和对象中存储的属性`等信息。字节序列写出到文件之后，相当于文件中持久保存了一个对象的信息。

反之，该字节序列还可以从文件中读取回来，重构对象，对它进行反序列化。对象的数据、对象的类型和对象中存储的数据信息，都可以用来在内存中创建对象。看图理解序列化：

[![40zJN6.png](https://z3.ax1x.com/2021/09/24/40zJN6.png)](https://imgtu.com/i/40zJN6)

### 2 ObjectOutputStream类

java.io.ObjectOutputStream类，将Java对象的原始数据类型写出到文件,实现对象的持久存储。

#### 2.1 构造方法

```java
public ObjectOutputStream(OutputStream out): 创建一个指定OutputStream的ObjectOutputStream。
```

构造代码如下：

```java
FileOutputStream fileOut = new FileOutputStream("aa.txt");
ObjectOutputStream out = new ObjectOutputStream(fileOut);
```

#### 2.2 序列化操作

1.一个对象要想序列化，必须满足两个条件：

该类必须实现java.io.Serializable 接口，Serializable是一个标记接口，不实现此接口的类将不会使任何状态序列化或反序列化，会抛出NotSerializableException 。

该类的所有属性必须是可序列化的。如果有一个属性不需要可序列化的，则该属性必须注明是瞬态的，使用transient关键字修饰。

```java
public class Employee implements java.io.Serializable {
    public String name;
    public String address;
    public transient int age;    // transient瞬态修饰成员,不会被序列化
    public void addressCheck() {
      	System.out.println("Address  check : " + name + " -- " + address);
    }
}
```

2.写出对象方法

public final void writeObject (Object obj): 将指定的对象写出。

```java
public class SerializeDemo{
   	public static void main(String [] args)   {
    	Employee e = new Employee();
    	e.name = "zhangsan";
    	e.address = "beiqinglu";
    	e.age = 20; 
    	try {
      		// 创建序列化流对象
          ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("employee.txt"));
        	// 写出对象
        	out.writeObject(e);
        	// 释放资源
        	out.close();
        	fileOut.close();
        	System.out.println("Serialized data is saved"); // 姓名，地址被序列化，年龄没有被序列化。
        } catch(IOException i)   {
            i.printStackTrace();              //输出结果：Serialized data is saved
        }
   	}
}
```

### 3 ObjectInputStream类

ObjectInputStream反序列化流，将之前使用ObjectOutputStream序列化的原始数据恢复为对象。

#### 3.1 构造方法

public ObjectInputStream(InputStream in)：创建一个指定InputStream的ObjectInputStream。

#### 3.2 反序列化操作1

如果能找到一个对象的class文件，我们可以进行反序列化操作，调用ObjectInputStream读取对象的方法：

```java
public final Object readObject () : 读取一个对象。
```

```java
public class DeserializeDemo {
   public static void main(String [] args)   {
        Employee e = null;
        try {		
             // 创建反序列化流
             FileInputStream fileIn = new FileInputStream("employee.txt");
             ObjectInputStream in = new ObjectInputStream(fileIn);
             // 读取一个对象
             e = (Employee) in.readObject();
             // 释放资源
             in.close();
             fileIn.close();
        }catch(IOException i) {
             // 捕获其他异常
             i.printStackTrace();
             return;
        }catch(ClassNotFoundException c)  {
        	// 捕获类找不到异常
             System.out.println("Employee class not found");
             c.printStackTrace();
             return;
        }
        // 无异常,直接打印输出
        System.out.println("Name: " + e.name);	// zhangsan
        System.out.println("Address: " + e.address); // beiqinglu
        System.out.println("age: " + e.age); // 0
    }
}
```

对于JVM可以反序列化对象，它必须是能够找到class文件的类。如果找不到该类的class文件，则抛出一个 ClassNotFoundException异常。

#### 3.3 反序列化操作2

另外，当JVM反序列化对象时，能找到class文件，但是class文件在序列化对象之后发生了修改，那么反序列化操作也会失败，抛出一个InvalidClassException异常。发生这个异常的原因如下：

1.该类的序列版本号与从流中读取的类描述符的版本号不匹配

2.该类包含未知数据类型

3.该类没有可访问的无参数构造方法

Serializable接口给需要序列化的类，提供了一个序列版本号。serialVersionUID该版本号的目的在于验证序列化的对象和对应类是否版本匹配。

```java
public class Employee implements java.io.Serializable {
     // 加入序列版本号
     private static final long serialVersionUID = 1L;
     public String name;
     public String address;
     // 添加新的属性 ,重新编译, 可以反序列化,该属性赋为默认值.
     public int eid; 

     public void addressCheck() {
         System.out.println("Address  check : " + name + " -- " + address);
     }
}
```

#### 3.4 序列化集合练习

1. 将存有多个自定义对象的集合序列化操作，保存到list.txt文件中。
2. 反序列化list.txt，并遍历集合，打印对象信息。

##### 3.4.1 案例分析

1.把若干学生对象 ，保存到集合中。

2.把集合序列化。

3.反序列化读取时，只需要读取一次，转换为集合类型。

4.遍历集合，可以打印所有的学生信息

##### 3.4.2 案例代码实现

```java
public class SerTest {
	public static void main(String[] args) throws Exception {
		// 创建 学生对象
		Student student = new Student("老王", "laow");
		Student student2 = new Student("老张", "laoz");
		Student student3 = new Student("老李", "laol");

		ArrayList<Student> arrayList = new ArrayList<>();
		arrayList.add(student);
		arrayList.add(student2);
		arrayList.add(student3);
		// 序列化操作
		// serializ(arrayList);
		
		// 反序列化  
		ObjectInputStream ois  = new ObjectInputStream(new FileInputStream("list.txt"));
		// 读取对象,强转为ArrayList类型
		ArrayList<Student> list  = (ArrayList<Student>)ois.readObject();
		
      	for (int i = 0; i < list.size(); i++ ){
          	Student s = list.get(i);
        	System.out.println(s.getName()+"--"+ s.getPwd());
      	}
	}

	private static void serializ(ArrayList<Student> arrayList) throws Exception {
		// 创建 序列化流 
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("list.txt"));
		// 写出对象
		oos.writeObject(arrayList);
		// 释放资源
		oos.close();
	}
}
```

## 九 打印流

### 1 何谓打印流

平时我们在控制台打印输出，是调用print方法和println方法完成的，这两个方法都来自于java.io.PrintStream类。该类能够方便地打印各种数据类型的值，是一种便捷的输出方式。

#### 1.1 打印流分类

字节打印流PrintStream，字符打印流PrintWriter

#### 1.2 打印流特点

1.只操作目的地,不操作数据源

2.可以操作任意类型的数据

3.如果启用了自动刷新，在调用println()方法的时候，能够换行并刷新

4.可以直接操作文件

如果该流的构造方法能够同时接收File和String类型的参数，一般都是可以直接操作文件的！

PrintStream是OutputStream的子类，PrintWriter是Writer的子类，两者处于对等的位置上，所以它们的API是非常相似的。二者区别无非一个是字节打印流，一个是字符打印流。

#### 1.3 字节输出打印流PrintStream复制文本文件

```java
public class PrintStreamDemo {
    public static void main(String[] args) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader("copy.txt"));
        PrintStream ps=new PrintStream("printcopy.txt");
        String line;
        while((line=br.readLine())!=null) {
            ps.println(line);
        }
        br.close();
        ps.close();
    }
}
```

#### 1.4 字符输出打印流PrintWriter复制文本文件

```java
public class PrintWriterDemo {
    public static void main(String[] args) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader("aa.txt"));
        PrintWriter pw=new PrintWriter("printcopyaa.txt");
        String line;
        while((line=br.readLine())!=null) {
            pw.println(line);
        }
        br.close();
        pw.close();
    }
}
```

## 十 Properties属性类

### 1 Properties概述

java.util.Properties继承于Hashtable，来表示一个持久的属性集。它使用键值结构存储数据，每个键及其对应值都是一个字符串。该类也被许多Java类使用，比如获取系统属性时，System.getProperties方法就是返回一个Properties对象。

### 2 Properties类

#### 2.1构造方法

```java
public Properties():创建一个空的属性列表。
```

#### 2.2 基本的存储方法

|                    方法名                    |                描述                |
| :------------------------------------------: | :--------------------------------: |
| Object setProperty(String key, String value) |            保存一对属性            |
|        String getProperty(String key)        | 使用此属性列表中指定的键搜索属性值 |
|      Set<String> stringPropertyNames()       |         所有键的名称的集合         |

```java
public class ProDemo {
    public static void main(String[] args) throws FileNotFoundException {
        // 创建属性集对象
        Properties properties = new Properties();
        // 添加键值对元素
        properties.setProperty("filename", "a.txt");
        properties.setProperty("length", "209385038");
        properties.setProperty("location", "D:\\a.txt");
        // 打印属性集对象
        System.out.println(properties);          //输出结果：  {filename=a.txt, length=209385038, location=D:\a.txt}
        // 通过键,获取属性值
        System.out.println(properties.getProperty("filename"));   //输出结果： a.txt
        System.out.println(properties.getProperty("length"));     //输出结果： 209385038
        System.out.println(properties.getProperty("location"));   //输出结果： D:\a.txt

        // 遍历属性集,获取所有键的集合
        Set<String> strings = properties.stringPropertyNames();
        // 打印键值对
        for (String key : strings ) {                                      //输出结果
          	System.out.println(key+" -- "+properties.getProperty(key));   //filename -- a.txt
                                                                          //length -- 209385038
                                                                          //location -- D:\a.txt
        }
    }
}
```

#### 2.3 与流相关的方法

```java
public void load(InputStream inStream)： 从字节输入流中读取键值对。
```

参数中使用了字节输入流，通过流对象，可以关联到某文件上，这样就能够加载文本中的数据了。现在文本数据格式如下:

```java
filename=Properties.txt
length=123
location=C:\Properties.txt
```

代码演示：

```java
public class ProDemo {
    public static void main(String[] args) throws FileNotFoundException {
        // 创建属性集对象
        Properties pro = new Properties();
        // 加载文本中信息到属性集
        pro.load(new FileInputStream("Properties.txt"));
        // 遍历集合并打印
        Set<String> strings = pro.stringPropertyNames();
        for (String key : strings ) {                                         //输出结果
          	System.out.println(key+" -- "+pro.getProperty(key));              //filename -- Properties.txt
                                                                              //length -- 123
                                                                              //location -- C:\Properties.txt
        }
     }
}
```

文本中的数据，必须是键值对形式，可以使用空格、等号、冒号等符号分隔。

