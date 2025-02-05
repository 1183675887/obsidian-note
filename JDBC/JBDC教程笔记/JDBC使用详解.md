# Java中JDBC的使用详解

## 一 环境介绍

1.mysql中创建一个库jdbcTest，并创建user表和插入表的数据。

2.新建一个java工程jdbc，并导入数据驱动。

## 二 详细步骤

### 1 加载数据库驱动，使用加载驱动类的方式(开发推荐的方式)

```java
//1.Mysql库
Class.forName("com.mysql.jdbc.Driver");
//2.postgresql库
Class.forName("org.postgresql.Driver");
//3.Oracle库
Class.forName("oracle.jdbc.OracleDriver");
//4.sqlserver库
Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
```

### 2 建立连接

#### 2.1 数据库URL

URL用于标识数据库的位置，程序员通过URL地址告诉JDBC程序连接哪个数据库，URL的写法为：

```java
//1.Mysql库
jdbc:mysql://{ip}:{port}/{库名}?useUnicode=true&characterEncoding=UTF-8
//2.postgresql库
jdbc:postgresql://{ip}:{port}/{库名}
//3.Oracle库
jdbc:oracle:thin:@//{ip}:{port}/{库名}
//4.sqlserver库
jdbc:microsoft:sqlserver://{ip}:{port};DatabaseName={库名}
```

#### 2.2. Connection

  Jdbc程序中的Connection，它用于代表数据库的链接，Collection是数据库编程中最重要的一个对象，客户端与数据库所有交互都是通过connection对象完成的，创建方法为：

```java
Connection connection = DriverManager.getConnection(url,userName,password);  //数据库URL，用户名，密码
```

这个对象的常用方法：

|               方法                |                      描述                      |
| :-------------------------------: | :--------------------------------------------: |
|         createStatement()         |       创建向数据库发送sql的statement对象       |
|       prepareStatement(sql)       | 创建向数据库发送预编译sql的PrepareSatement对象 |
|         prepareCall(sql)          |    创建执行存储过程的callableStatement对象     |
| setAutoCommit(boolean autoCommit) |              设置事务是否自动提交              |
|             commit()              |                在链接上提交事务                |
|            rollback()             |               在此链接上回滚事务               |

```java
String url = "jdbc:mysql://localhost:3306/test";   //自行更改
String username = "root";                          //自行更改
String password = "root";                          //自行更改

//.获取与数据库的链接
Connection connection = DriverManager.getConnection(url, username, password);
```

### 3 执行SQL语句

#### 3.1 Statement

   Jdbc程序中的Statement对象用于向数据库发送SQL语句，创建方法为：

```java
Statement statement = connection.createStatement();
```

Statement对象常用方法：

|           方法            |                    描述                    |
| :-----------------------: | :----------------------------------------: |
| executeQuery(String sql)  |           用于向数据发送查询语句           |
| executeUpdate(String sql) | 用于向数据库发送insert、update或delete语句 |
|    execute(String sql)    |        用于向数据库发送任意sql语句         |
|   addBatch(String sql)    |       把多条sql语句放到一个批处理中        |
|      executeBatch()       |        向数据库发送一批sql语句执行         |

```java
//获取用于向数据库发送sql语句的statement
Statement statement = connection.createStatement();
//向数据库发sql
String sql = "select id,name,password,email,birthday from users";
st.executeQuery(sql);
```

#### 3.2 PreperedStatement

 PreperedStatement是Statement的孩子，它的实例对象可以通过调用：

```java
PreperedStatement statement = connection.preparedStatement();
```

```java
String sql = "select * from users where name=? and password=?";
 
//获取用于向数据库发送sql语句的Preperedstatement
PreperedStatement statement = connection.preparedStatement(sql);  //在此次传入，进行预编译
st.setString(1, username);
st.setString(2, password);
//向数据库发sql
st.executeQuery();  //在这里不需要传入sql
```

#### 3.3 PreperedStatement与Statement的比较

1.PreperedStatement可以避免SQL注入的问题。

2.Statement会使数据库频繁编译SQL，可能造成数据库缓冲区溢出。PreparedStatement 可对SQL进行预编译，从而提高数据库的执行效率。

3.并且PreperedStatement对于sql中的参数，允许使用占位符的形式进行替换，简化sql语句的编写。

### 4 获取结果

Jdbc程序中的ResultSet用于代表Sql语句的执行结果。Resultset封装执行结果时，采用的类似于表格的方式，ResultSet 对象维护了一个指向表格数据行的游标，初始的时候，游标在第一行之前，调用ResultSet.next() 方法，可以使游标指向具体的数据行，进行调用方法获取该行的数据。

#### 4.1 获取行

ResultSet提供了对结果集进行滚动的方法：

|       方法        |          描述           |
| :---------------: | :---------------------: |
|      next()       |      移动到下一行       |
|    Previous()     |      移动到前一行       |
| absolute(int row) |      移动到指定行       |
|   beforeFirst()   |  移动resultSet的最前面  |
|    afterLast()    | 移动到resultSet的最后面 |

#### 4.2 获取值

ResultSet既然用于封装执行结果的，所以该对象提供的都是用于获取数据的get方法。

|             方法             |        描述        |
| :--------------------------: | :----------------: |
|     getObject(int index)     | 获取任意类型的数据 |
| getObject(string columnName) | 获取任意类型的数据 |
|     getString(int index)     | 获取指定类型的数据 |
| getString(String columnName) | 获取指定类型的数据 |

常用类型转化

|         SQL类型          |      JDBC对应方法      |      返回类型      |
| :----------------------: | :--------------------: | :----------------: |
|      bit(1)，bit(n)      | getBoolean，getBytes() |  Boolean，byte[]   |
|         tinyint          |       getByte()        |        Byte        |
|         smallint         |       getShort()       |       Short        |
|           int            |         getInt         |        Int         |
|          bigint          |       getLong()        |        Long        |
| char,varchar,longvarchar |       getString        |       String       |
|     text(clob) blob      |  getClob()，getblob()  |     Clob，blob     |
|           date           |       getDate()        |   java.sql.Date    |
|           time           |       getTime()        |   java.sql.Time    |
|        timestamp         |      getTimestamp      | java.sql.Timestamp |

```java
//向数据库发sql,并获取代表结果集的resultset
String sql = "select id,name,password,email,birthday from users";
ResultSet responseSet = statement.executeQuery(sql);
			
//取出结果集的数据
responseSet.afterLast();
responseSet.previous();
System.out.println("id=" + responseSet.getObject("id"));
System.out.println("name=" + responseSet.getObject("name"));
System.out.println("password=" + responseSet.getObject("password"));
System.out.println("email=" + responseSet.getObject("email"));
System.out.println("birthday=" + responseSet.getObject("birthday"));
//或者
//循环取出(id)
while(rs.next())
{
	String id=responseSet.getString(1); //1代表数据库中表的列数，id在第一列也可以("id")
	System.out.println(id+" ");
}
```

### 5 释放资源

Jdbc程序运行完后，切记要释放程序在运行过程中，创建的那些与数据库进行交互的对象，这些对象通常是ResultSet, Statement和Connection对象。

注意：为确保资源释放代码能运行，资源释放代码也一定要放在finally语句中

```java
//6.关闭链接，释放资源
	if(responseSet!=null){
		try{
			responseSet.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		rs = null;
	
	}
	if(statement!=null){
		try{
			statement.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
				
	}	
	if(connection!=null){
		try{
			connection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
}
```

## 三 基本操作

### 1 DDL

数据库模式定义语言DDL(Data Definition Language)，是用于描述数据库中要存储的现实世界实体的语言。主要由create（添加）、alter（修改）、drop（删除）和 truncate（删除） 四个关键字完成。

```java
    /**
     * 在java中使用ddl语句（credate,drop,backup...）
     */
    public class Test1 {

        public static void main(String[] args) {
            Test1 test = new Test1();
        }

        public Test1() {
            this.lianjie();
        }

        public void lianjie() {
            //定义需要的对象
            PreparedStatement statement = null;
            Connection connection = null;
            ResultSet responseSet = null;
            try {
                //初始化对象
                //1.加载驱动
                Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
                //2.得到连接(1433表示sql server的默认端口)
                connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://localhost:1433;databaseName=shen", "sa", "Anxin062039");
                //3.创建Preparestatement,创建数据
                statement = connection.prepareStatement("create database vvv");
//			statement=connection.prepareStatement("create table xxx");//创建表
//			statement=connection.prepareStatement("backup database shen to disk='F:/123.bak'");//备份数据库

                //如果执行的是ddl语句
                boolean b = statement.execute();  //用于向数据库发送任意sql语句
                if(b) {
                    System.out.println("创建成功！");
                }else {
                    System.out.println("失败");
                }
            }catch(Exception e) {
                // TODO: handle exception
            }finally {
                //关闭资源
                try {
                    //为了程序健壮
                    if(statement != null)
                        statement.close();
                    if(connection != null)
                        connection.close();
                }catch(Exception e2) {
                    // TODO: handle exception
                }
            }

        }
    }
```

### 2 CRUD

1.先创建一个实体类

```java
public class User {
	private int id;
	private String name;
	private String password;
	private String email;
	private Date birthday;
        
        //相关的get和set
}
```

2.编写jbdc连接工具

```java
public class JdbcUtils {

    private static String driver = null;
    private static String url = null;
    private static String username = null;
    private static String password = null;

    static {
        try{
            InputStream inputStrem = JdbcUtils.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inputStrem);

            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

            Class.forName(driver);

        }catch(Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void release(Connection connection, Statement statement, ResultSet rs) {

        if(rs != null) {
            try {
                rs.close();
            }catch(Exception e) {
                e.printStackTrace();
            }
            rs = null;

        }
        if(statement != null) {
            try {
                statement.close();
            }catch(Exception e) {
                e.printStackTrace();
            }

        }

        if(connection != null) {
            try {
                connection.close();
            }catch(Exception e) {
                e.printStackTrace();
            }

        }

    }
}
```

3.资源文件properties

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/test
username=root
password=root
```

4.测试demo

```java
//使用jdbc对数据库增删改查
public class Demo {

    @Test
    public void insert() {
        Connection connection = null;
        Statement statement = null;
        ResultSet responseSet = null;
        try {
            connection = JdbcUtils.getConnection();
            statement = connection.createStatement();
            String sql = "insert into users(id,name,password,email,birthday)" 
                        +"values(4,'xxx','123','xx@sina.com',to_date('1980-09-09','YYYY-MM-DD'))";
            int num = statement.executeUpdate(sql);  //update
            if(num > 0) {
                System.out.println("插入成功！！");
            }

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

    @Test
    public void delete() {
        Connection connection = null;
        Statement statement = null;
        ResultSet responseSet = null;
        try {
            connection = JdbcUtils.getConnection();
            String sql = "delete from users where id=4";
            statement = connection.createStatement();
            int num = statement.executeUpdate(sql);
            if(num > 0) {
                System.out.println("删除成功！！");
            }
        }catch(Exception e) {


        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

    @Test
    public void update() {
        Connection connection = null;
        Statement statement = null;
        ResultSet responseSet = null;
        try {
            connection = JdbcUtils.getConnection();
            String sql = "update users set name='wuwang',email='wuwang@sina.com' where id=3";
            statement = connection.createStatement();
            int num = statement.executeUpdate(sql);
            if(num > 0) {
                System.out.println("更新成功！！");
            }
        }catch(Exception e) {


        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

    @Test
    public void find() {
        Connection connection = null;
        Statement statement = null;
        ResultSet responseSet = null;
        try {
            connection = JdbcUtils.getConnection();
            String sql = "select * from users where id=1";
            statement = connection.createStatement();
            responseSet = statement.executeQuery(sql);
            if(responseSet.next()) {
                System.out.println(responseSet.getString("name"));
            }
        }catch(Exception e) {

        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

}
```

## 四 常见操作

### 1 处理大数据

 如下内容都借助CRUD中的内容。

#### 1.1 存取文本文件（.txt文件）

clob用于存储大文本（mysql中无clob,存储大文本采用的是Text）

1.数据库

```sql
	 create table testclob
	 (
	 	id int primary key auto_increment,
	 	resume text
	 );
```

```java
public class Demo1 {
    @Test
    public void add() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql = "insert into testclob(resume) values(?)";
            statement = connection.prepareStatement(sql);

            //Reader reader = new InputStreamReader(Demo1.class.getClassLoader().getResourceAsStream("1.txt"));
            String path = Demo1.class.getClassLoader().getResource("1.txt").getPath();//1.txt文件在src下
            File file = new File(path);
            statement.setCharacterStream(1, new FileReader(file), (int) file.length());//将该文件添加
            int num = statement.executeUpdate();
            if(num > 0) {
                System.out.println("插入成功！！");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

    @Test
    public void read() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql = "select resume from testclob where id=1";
            statement = connection.prepareStatement(sql);
            responseSet = statement.executeQuery();
            if(responseSet.next()) {
                Reader reader = responseSet.getCharacterStream("resume");
                char buffer[] = new char[1024];
                int len = 0;
                FileWriter out = new FileWriter("c:\\1.txt");
                while((len = reader.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                reader.close();
            }

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }
}
```

#### 1.2 获取二进制文件(图片，视频)

1.数据库

```sql
	 create table testblob
	 (
	 	id int primary key auto_increment,
	 	image longblob
	 );
```

```java
public class Demo2 {
    @Test
    public void add() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql = "insert into testblob(image) values(?)";
            statement = connection.prepareStatement(sql);
            String path = Demo2.class.getClassLoader().getResource("01.jpg").getPath();
            statement.setBinaryStream(1, new FileInputStream(path), (int) new File(path).length());
            int num = statement.executeUpdate();
            if(num > 0) {
                System.out.println("插入成功！！");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }

    @Test
    public void read() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql = "select image from testblob where id=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, 1);
            responseSet = statement.executeQuery();
            if(responseSet.next()) {
                InputStream in = responseSet.getBinaryStream("image");
                int len = 0;
                byte buffer[] = new byte[1024];

                FileOutputStream out = new FileOutputStream("c:\\1.jpg");
                while((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }
}
```

### 2 批量处理

1.数据库

```sql
	 create table testbatch
	 (
	 	id int primary key,
	 	name varchar2(20)
	 );
```

2.通过Statement

```java
    @Test
    public void testbatch1() {

        Connection connection = null;
        Statement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql1 = "insert into testbatch(id,name) values(1,'aaa')";
            String sql2 = "insert into testbatch(id,name) values(2,'bbb')";
            String sql3 = "delete from testbatch where id=1";

            statement = connection.createStatement();
            statement.addBatch(sql1);
            statement.addBatch(sql2);
            statement.addBatch(sql3);

            statement.executeBatch();
            statement.clearBatch();
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }
    }
```

3.通过preparedstatement

```java
    @Test
    public void testbatch2() {

        long starttime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            String sql = "insert into testbatch(id,name) values(?,?)";
            statement = connection.prepareStatement(sql);

            for(int i = 1; i < 10000008; i++) {  //i=1000  2000
                statement.setInt(1, i);
                statement.setString(2, "aa" + i);
                statement.addBatch();

                if(i % 1000 == 0) {
                    statement.executeBatch();
                    statement.clearBatch();
                }
            }
            statement.executeBatch();

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }

        long endtime = System.currentTimeMillis();

        System.out.println("程序花费时间：" + (endtime - starttime) / 1000 + "秒！！");
    }
```

### 3 获取自动生成的主键

1.数据库

```sql
	 create table test1
	 (
	 	id int primary key auto_increment,//id自动生成
	 	name varchar(20)
	 );
```

```java
    /**
     * 获取自动生成的主键
     */
    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try{
            connection = JdbcUtils.getConnection();
            String sql = "insert into test1(name) values(?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "aaa");//在插入name时数据库会自动生成id
            statement.executeUpdate();

            responseSet = statement.getGeneratedKeys();//获取生成的主键
            if(responseSet.next()){
                System.out.println(responseSet.getInt(1));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            JdbcUtils.release(connection, statement, responseSet);
        }

    }
```

### 4 使用事务

1.普通事务的操作

```java
    /**
     * 模似转帐
     * <p>
     * create table account(
     * id int primary key auto_increment,
     * name varchar(40),
     * money float
     * )character set utf8 collate utf8_general_ci;
     * insert into account(name,money) values('aaa',1000);
     * insert into account(name,money) values('bbb',1000);
     * insert into account(name,money) values('ccc',1000);
     */
    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;

        try {
            connection = JdbcUtils.getConnection();
            connection.setAutoCommit(false);   //start transaction，开启事务

            String sql1 = "update account set money=money-100 where name='aaa'";
            statement = connection.prepareStatement(sql1);
            statement.executeUpdate();

            String sql2 = "update account set money=money+100 where name='bbb'";
            statement = connection.prepareStatement(sql2);
            statement.executeUpdate();
            
            connection.commit();//提交

            System.out.println("成功！！！");  //log4j

        }catch(Exception e) {
            e.printStackTrace();//如果程序出错手动回滚
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }

    }
```

2.设置事务回滚点

```java
    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet responseSet = null;
        Savepoint savePoint = null;

        try {
            connection = JdbcUtils.getConnection();
            connection.setAutoCommit(false);   //start transaction

            String sql1 = "update account set money=money-100 where name='aaa'";
            statement = connection.prepareStatement(sql1);
            statement.executeUpdate();

            savePoint = connection.setSavepoint();//在这里设置事务回滚点

            String sql2 = "update account set money=money+100 where name='bbb'";
            statement = connection.prepareStatement(sql2);
            statement.executeUpdate();

            int x = 1 / 0;

            String sql3 = "update account set money=money+100 where name='ccc'";
            statement = connection.prepareStatement(sql3);
            statement.executeUpdate();

            connection.commit();

        }catch(Exception e) {
            try {
                connection.rollback(savePoint);  //回滚到该事务点，即该点之前的会正常执行（sql1）
                connection.commit();             //回滚了要记得提交,如果没有提交sql1将会自动回滚
            }catch(SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JdbcUtils.release(connection, statement, responseSet);
        }

    }
```

3.设置事务级别

```java
conn = JdbcUtils.getConnection();
conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//避免脏读
conn.setAutoCommit(false);
/*
Serializable：可避免脏读、不可重复读、虚读情况的发生。（串行化）
Repeatable read：可避免脏读、不可重复读情况的发生。（可重复读）
Read committed：可避免脏读情况发生（读已提交）。
Read uncommitted：最低级别，以上情况均无法保证。(读未提交)
*/
```





