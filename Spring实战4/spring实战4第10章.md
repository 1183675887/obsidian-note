---
title: spring实战4第10章
date: 2020-07-08 10:00:26
tags: spring
---

## 第十章，通过Spring和JDBC征服数据库

此章讲解配置数据源，在Spring中使用JDBC。

<!--more-->

### 10.0.各种注解

#### 1.@Repository

@Repository注解，在类上使用，表明它将会在组件扫描的时候自动创建。

```java
@Repository               //表明它会在组件扫描的时候自动创建。
public   class  JdbcSpitterRepository  implements  SpitterRepository{
   private  JdbcOperations   jdbcOperations;
    @Autowired    //JdbcOperations是一个接口，定义了jdbcTemplate所实现的操作。
    public  JdbcSpitterRepository(JdbcOperations   JdbcOperations){
      this.JdbcOperations=JdbcOperations;
                             }
    --------
                         }
```



### 10.1.Spring的数据访问哲学

很多应用需要从某种类型的数据库中读取和写入数据，为了避免持久化的逻辑分散到应用的各个组件中，最好将数据访问的功能放在一个或多个专注于此项任务的组件中。这样的组件通常称为数据访问对象（DAO），或Repository。

为了避免应用于特定的数据访问策略耦合在一起，编写良好的Repository应该以接口的方式暴露功能。

但这并不是强制的，可以使用Spring将bean（DAO或其他类型）直接装配到另一个bean的属性中，而不需要一定通过接口注入。

#### 10.1.2.数据访问模块化

Spring将数据访问过程中固定的和可变的部分明确划分为两个不同的类：模板(template)和回调(callback)。模板管理过程中固定的部分，而回调处理自定义的数据访问代码。

Repository模板负责1.2.5.6步。Repository负责3.4步。

1.准备资源   2.开始事务

3.在事务中执行  4.返回数据

5.提交/回滚事务  6.关闭资源和处理错误

Spring的模板类负责数据访问的固定部分--事务控制，管理资源，以及处理异常。同时应用程序相关的数据访问-语句，参数绑定以及整理结果集在回调的实现中处理。

Spring所支持的大多数持久化功能都依赖于数据源。因此，在声明模板和Repository之前，我们需要在Spring中配置一个数据源用来连接数据库。

### 10.2.配置数据源

Spring提供了在Spring上下文中配置数据源bean的多种方式。

1.通过JDBC驱动定义的数据源。

2.通过JNDI查找的数据源。

3.连接池的数据源。

对于即将发布到生产环境中的应用程序，建议使用从连接池获取连接的数据源。如果可能的话，，更倾向于通过应用服务器的JNDI来获取数据源。

#### 10.2.1.使用JNDI数据源

Spring应用程序经常部署在java EE应用服务器中，如Tomcat这样的Web容器。这些服务器允许你配置通过JNDI获取数据源。这种配置的好处在于数据源完全可以在应用程序之外进行管理。在应用服务器中管理的数据源通常以池的方式组织。

利用Spring，我们可以像使用Spring bean那样配置JNDI中数据源的引用将其装配到需要的类中。可以使用<Jee:jndi-lookup>元素将其装配到Spring中。

```xml
<jee:jndi-lookup  id="dataScoure"
     jndi-name="/jdbc/SpitterDS"    //jndi-name属性用于指定JNDI中资源的名称。
     resource-ref="true" />         //运行在java应用服务器将resource-ref设置为true，
                                    //这样给定的jndi-name会自动添加"java:comp/env/"前缀。
```

如果想使用java配置的话，可以借助JndiObjectFactoryBean从JNDI中查找DataSource。

```java
 @Bean
 public  JndiObjectFactoryBean    dataScoure(){
    JndiObjectFactoryBean  jndiObjectFB  =  new  JndiObjectFactoryBean();
    JndiObjectFB.setJndiName("jdbc/SpitterDS");
    JndiObjectFB.setResourceRef(ture);
    JndiObjectFB.setProxyInterface(javax.sql.DataSource.class);
       return   jndiObjectFB;
     }
```

#### 10.2.2.使用数据源连接池

如果不能从JNDI中查找数据源，那么下一个选择就是直接在Spring中配置数据源连接池。有如下开源的实现

1.Apache Commons DBCP

2.c3p0

3.BoneCP

这些连接池中的大多数都能配置为Spring的数据源，在一定程度上与Spring自带的DriverManagerDataSource或SingleConnectionDataSource很类似。例如，如下就是配置DBCP basicDataSource的方式。

```xml
<bean id="dataSource"   class="org.apache.dbcp.BasicDataSource"  
  p:driverClassName="org.h2.Driver"       //指定JDBC驱动类的全限定类名     
  p:url="jdbc:h2:tcp//localhost/~/spitter"        //设置了JDBC的URL。
  p:username="sa"     //用于认证
  p:password=" "       //用于认证
  p:initialSize="5"     //池子启动时创建的连接数量
  p:maxActive="10"  />   //同一时间可从池里分配的最多连接数


```

如果喜欢java配置的话，连接池形式的DataSource bean可以声明如下。

```java
@Bean
public   BasicDataSource  dataSource(){
  BasicDataSource  ds =  new   BasicDataSource();
     ds.setDriverClassName("org.h2.Driver");           //指定JDBC驱动类的全限定类名。
     ds.setUrl("jdbc:h2:tcp//localhost/~/spitter") ;   //设置了JDBC的URL。
     ds.setUsername("sa");          //用于认证
     ds.setPassword(" ");           //用于认证
     ds.setInitialSize(5);          //池子启动时创建的连接数量
     ds.setMaxActive(10);           //同一时间可从池里分配的最多连接数
         return  ds;
                   }

```

前四个属性是配置BasicDataSource所必需的。属性driverClassName指定了JDBC驱动类的全限定类名。属性url用于设置数据库的JDBC URL。最后，username和password用于在连接数据库时进行认证。

#### 10.2.3.基于JDBC驱动的数据源

在spring中，基于JDBC驱动的定义数据源是最简单的配置方式。spring提供了3个这样的数据源类。

1.DriverMangerDataSource:   在每个连接请求时都会返回一个新建的连接。与DBCP的BasicDataSource不同，DriverMangerDataSource提供的连接池并没有进行池化管理。
 2..SimpleDriverDataSource:  与DriverMangerDataSource的工作方法类似，但是它直接使用JDBC驱动，来解决在特定环境下的类加载问题，这样的环境包括OSGi容器；
3.SingleConnectionDataSource:  在每个连接请求时都会返回一个同一个的连接。 尽管SingleConnectionDataSource不是严格意义上的连接池数据源，但是你可以将其视为只有一个连接的池。

以上这些数据源的配置与DBCP BasicDataSource的配置类似。例如如下就是配置DriverMangerDataSource的方法。

```java
@Bean
public  DataSource  dataSource(){
   DriverManagerDataSource  ds  =  new  DriverManagerDataSource();
      ds.setDriverClassName("org.h2.Driver");           //指定JDBC驱动类的全限定类名。
      ds.setUrl("jdbc:h2:tcp//localhost/~/spitter") ;   //设置了JDBC的URL。
      ds.setUsername("sa");     //用于认证
      ds.setPassword(" ");      //用于认证
        return  ds;
    }
```

如果使用xml的话，DriverManagerDataSource可以按如下的方式配置。

```xml
<bean id="dataSource"   class="org.springframework.jdbc.datasource.DriverMangerDataSource"
    p:driverClassName="org.h2.Driver"      //指定JDBC驱动类的全限定类名     
    p:url="jdbc:h2:tcp//localhost/~/spitter"      //设置了JDBC的URL。
    p:username="sa"         //用于认证
    p:password=" "  />      //用于认证
```

以上都有限制，因此强烈建议使用数据源连接池。

#### 10.2.4.使用嵌入式的数据源

嵌入式在生产环境中，没什么作用，但是对于开发和测试来讲，是个好方案。因为每次重启应用或运行测试的时候，都能够
重新填充测试数据。spring的jdbc命名空间能简化嵌入式数据库的配置。

```xml
<beans ---->  
  <jdbc:embedded-database  id="dataSource"  type="H2">   //type为H2，表明是嵌入式H2数据库
  <jdbc:script  location="com/habuma/spitter/db/jdbc/schema.sql" />   //引用数据库中创建表的SQL
  <jdbc:script  location="com/habuma/spitter/db/jdbc/test-data.sql" /> //将测试数据填充到数据库中
  </jdbc:embedded-database>
</beans>

```

用java来配置嵌入式数据库时，可以使用EmbeddedDatabaseBuilder来构造DataSource。

```java
@Bean
public  DataSource   dataSource(){
   return  new  EmbeddedDatabaseBuilder()
         .setType(EmbeddedDatabaseType.H2)
         .addScript("classpath:schema.sql")
         .addScript("classpath:test-data.sql")
         .build();
                   }
```

#### 10.2.2.使用profile选择数据源

我们很可能面临这样一种需求，那就是在某种环境下需要其中一种数据源，而在另外的环境中需要不同的数据源。例如，

对于开发期来说，<jdbc:embedded-database>元素是很合适的，

而在QA环境中，你可能希望使用DBCP的BasicDataSource，

在生产部署环境下，可能需要使用<jee:jndi-lookup>

```java
@Configuration
public class DataSourceConfiguration{
   
   @Profile("development")      //开发数据源
   @Bean
   public  DataSource  embeddedDataSource(){   
      return  new  EmbeddedDatabaseBuilder()
         .setType(EmbeddedDatabaseType.H2)
         .addScript("classpath:schema.sql")
         .addScript("classpath:test-data.sql")
         .build();
                   }
    
    @Profile("qa")                     //QA数据源
    @Bean
    public  DataSource  Data(){
       BasicDataSource  ds =  new BasicDataSource();
          ds.setDriverClassName("org.h2.Driver");           //指定JDBC驱动类的全限定类名。
          ds.setUrl("jdbc:h2:tcp//localhost/~/spitter") ;   //设置了JDBC的URL。
          ds.setUsername("sa");          //用于认证
          ds.setPassword(" ");           //用于认证
          ds.setInitialSize(5);          //池子启动时创建的连接数量
          ds.setMaxActive(10);           //同一时间可从池里分配的最多连接数
            return  ds;
                   }
    
    @Profile("production")          //生产环境的数据源
    @Bean
    public  DataSource    dataScoure(){
        JndiObjectFactoryBean  jndiObjectFB  =  new  JndiObjectFactoryBean();
        JndiObjectFB.setJndiName("jdbc/SpitterDS");
        JndiObjectFB.setResourceRef(ture);
        JndiObjectFB.setProxyInterface(javax.sql.DataSource.class);
           return   jndiObjectFB;
     }
}
```

还可以使用XML配置

```xml
<beans------>
 <beans profile="development">       //开发数据源       
   <jdbc:embedded-database  id="dataSource"  type="H2">   //type为H2，表明是嵌入式H2数据库
   <jdbc:script  location="com/habuma/spitter/db/jdbc/schema.sql" />   //引用数据库中创建表的SQL
   <jdbc:script  location="com/habuma/spitter/db/jdbc/test-data.sql" /> //将测试数据填充到数据库中
   </jdbc:embedded-database>
 </beans>
 <beans profile="qa">
   <bean id="dataSource"   class="org.springframework.jdbc.datasource.DriverMangerDataSource"
     p:driverClassName="org.h2.Driver"      //指定JDBC驱动类的全限定类名     
     p:url="jdbc:h2:tcp//localhost/~/spitter"      //设置了JDBC的URL。
     p:username="sa"         //用于认证
     p:password=" "  />      //用于认证
   </beans>
  <beans profile="production">
    <jee:jndi-lookup  id="dataScoure"
       jndi-name="/jdbc/SpitterDS"    //jndi-name属性用于指定JNDI中资源的名称。
       resource-ref="true" />         //运行在java应用服务器将resource-ref设置为true，
                                      //这样给定的jndi-name会自动添加"java:comp/env/"前缀。
    </beans>
</beans>
```

现在我们已经通过数据源建立了与数据库的连接，接下来要实际访问数据库了。Spring提供了多种数据库的方式包括JDBC,Hibernate,以及java持久化API（JPA）。

### 10.3.在Spring中使用JDBC

#### 10.3.1.应对时刻的JDBC代码

如果使用JDBC往数据库中插入数据，那么以下代码你肯定不陌生。使用JDBC在数据库里插入一行数据。

```java
private static final String SQL_INSERT_SPITTER=
    "insert into spitter(username,password,fullname)  values(?,?,?)";
private DataSource dataSource;
public void addSpitter(Spitter spitter){
    Connection conn = null;
    PreparedStatement stmt = null;
    try{
        conn = dataSource.getConnection();  //获取连接
        stmt = conn.prepareStatement(SQL_INSERT_SPITTER);  //创建语句
        stmt.setString(1,spitter.getUsername());   //绑定参数，下面也是
        stmt.setString(1,spitter.getPassword());
        stmt.setString(1,spitter.getUsername());
        stmt.execute();   //执行语句
    }catch (SQLException e){
        --------          //处理异常
    }finally{
    -------清理资源
    }
}
```

使用JDBC来更新数据库中Spitter表的一行。

```java
private static final String SQL_UPDATE_SPITTER=
    "update spitter set username = ?,password = ?,fullname=?"+"where id = ?";
public void saveSpitter(Spitter spitter){
    Connection conn = null;
    PreparedStatement stmt = null;
    try{
        conn = dataSource.getConnection();  //获取连接
        stmt = conn.prepareStatement(SQL_UPDATA_SPITTER);  //创建语句
        stmt.setString(1,spitter.getUsername());   //绑定参数，下面也是
        stmt.setString(1,spitter.getPassword());
        stmt.setString(1,spitter.getUsername());
        stmt.execute();   //执行语句
    }catch (SQLException e){
        --------          //处理异常
    }finally{
    -------清理资源
    }
}
```

使用JDBC从数据库中查询一行数据。

```java
private static final String SQL_SELECT_SPITTER=
    "select id,username,fullname from spitter where id = ?";
public void findOne(long id){
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try{
        conn = dataSource.getConnection();  //获取连接
        stmt = conn.prepareStatement(SQL_SELECT_SPITTER);  //创建语句
        stmt.setLong(1,id)     //绑定参数
        rs=stmt.executeQuery();  //执行语句
        Spitter spitter = null;
        if(rs.next()){  //处理结果
            spitter = new Spitter();
            spitter.setId(rs.getLong("id"));
            spitter.setUsername(rs.getString("username"));
            spitter.setPassword(rs.getString("password"));
            spitter.setFullName(rs.getString("fullname"));
        }
        return spitter;
    }catch (SQLException e){
        --------          //处理异常
    }finally{
    -------清理资源
    }
}
```



#### 10.3.2.使用JDBC模板

Spring的JDBC框架承担了资源管理和异常处理的工作，从而简化了JDBC代码，让我们只需编写从数据库读写数据的必须代码。

Spring为JDBC提供了三个模板类供选择。

1.jdbcTemplate:  最基本的spring  JDBC模板，这个模板支持简单的JDBC数据库访问功能以及基于索引参数的查询。
2.NamedParameterJdbcTemplata:使用该模板类执行查询时可以将值以命名参数的形式绑定到SQL中，而不是简单的索引参数。
3.SimpleJdbcTemple:  该模板类利用java 5 的一些特性如自动装箱，泛型以及可变参数列表来简化JDBC模板的使用  

对于大部分的JDBC任务来讲1就是最好的选择方案。3已经被弃用了。只有在需要使用命名参数的时候，才使用2。 

##### 使用jdbcTemplate来插入数据

为了让jdbcTemplate正常工作，只需要为其设置DataSource就可以了。这使得在Spring中配置jdbcTemplate非常容易，如下@Bean方法所示。

```java
@bean
public  class  jdbcTemplate    jdbcTemplate(DataSource   dataSource){  //DataSource通过构造器注入进来
    return  new  JdbcTemplate(dataSource);   
      } 
```

这里引用的dataSource bean可以是java.sql.DatSource的任意实现，比如之前创建的数据源。现在我们可以将jdbcTemplate装配到Repository中并使用它来访问数据库。例如SpitterRepository使用了JdbcTemplate。

```java
@Repository               //表明它会在组件扫描的时候自动创建。
public   class  JdbcSpitterRepository  implements  SpitterRepository{
   private  JdbcOperations   jdbcOperations;
    @Autowired    //JdbcOperations是一个接口，定义了jdbcTemplate所实现的操作。
    public  JdbcSpitterRepository(JdbcOperations   JdbcOperations){
      this.JdbcOperations=JdbcOperations;
                             }
    --------
                         }
```

作为另外一种组件扫描和自动装配的方案，我们可以将jdbcSpitterRepository显示声明为Spring中的bean。

```java
@bean
public  SpitterRepository  spitterRepository(JdbcTemplate  jdbcTemplate){
   return new  JdbcSpitterRepository(jdbcTemplate)；
       }

```

在Repository中具备可用的jdbcTemplate后，我们可以极大的简化基于jdbcTemplate的addSpitter()方法。

```java
 public   void  addSpitter(Spitter  spitter){
       jdbcOperations.update(INSERT_SPITTER,
           spitter.getUsername(),
           spitter.getPassword(),
           spitter.getFullName().
           spitter.getEmail(),
           spitter.isUpdateByEmail()    )
        }

```

当updata()方法被调用的时候jdbcTemplate就会获取连接，创建语句并执行插入SQL。

```java
 public   class   findOne(long  id){
    return  jdbcOperations.queryForObject(              //查询Spitter，使用queryForObject方法。
         SELECT_SPITTER_BY_ID，new  SpitterRowMapper(),
                id );//将查结果映射到对象
              }
   private   static   final  class  SpitterRowMapper   implements     RowMapper<Spitter>{
          public   Spitter   mapRow(ResultSet   rs,  int   rowNum)    throws  SQLException{
               return  new   Spitter(                //绑定参数
                                    rs.getLong("id"),
                                    rs.getString("usename"),
                                    rs.getString("password"),
                                    rs.getString("fullName"),
                                    rs.getString("email"),
                                    rs.getString("updateByEmail")   );
                              } 
                                   }

```

在这个findOne()方法中使用了jdbcTemplate的queryForObject()方法来从数据库查询Spitter。queryForObject方法有三个参数：
 1.String对象，包含了要从数据库中查找数据的SQL；
 2.RowMapper对象，用来从ResultSet中提取数据并构建域对象（本例中为Spitter）；
 3.可变参数列表，列出了要绑定到查询上的索引参数值。

##### 使用命名参数

在之前的代码中，addSpitter()方法使用了索引参数，这意味着我们需要留意查询中参数的顺序，在将值传递给update()方法的时候要保证正确的顺序。

除了这种方法外，我们还可以使用命名参数，命名参数可以赋予SQL中的每个参数一个明确的名字，在绑定值到查询语句的时候就通过该名字来引用参数。假设SQL_INSERT_SPITTER查询语句是这样的。

```java
private static final String SQL_INSERT_SPITTER=
    "insert into spitter(username,password,fullname)" +
    "values(:username, :password, :fullname)";
```

使用命名参数查询，绑定值的顺序就不重要了，我们可以按照名字来绑定值。

NamedParameterJdbcTemplate是一个特殊的JDBC模板，它支持使用命名参数。在Spring中，NamedParameterJdbcTemplate的声明方式与常规的jdbcTemplate几乎完全相同。

```java
@Bean
public  NamedParameterJdbcTemplate(DataSource  dataSource){
    return  new  NamedParameterJdbcTemplate(dataSource);                                                                                                             }

```

在这里，我们将NamedParameterJdbcOperations(NamedParameterJdbcTemplate所实现的接口)注入到Repository中，用它来替代jdbcOperations。现在的addSpitter()方法如下。

```java
private  static  final  String INSERT_SPITTER =
   "insert  into  Spitter” +
    "(username,password,fullname,email,updateByEmail)" +
   "values"  +
     "(:username, :password, :funllname, :email,  :updateByEmail)";
                   
public   void  addSpitter(Spitter  spitter){
    Map<String,Object> paramMap = new  HashMap<String, Object>();
     paramMap.put("username", spitter.getUsername());        //绑定参数
     paramMap.put ("password", spitter.getPassword());
     paramMap.put("fullname", spitter.getFullName());
     paramMap.put ("email", spitter.getEmail());
     paramMap.put ("updateByEmail", spitter.isUpdateByEmail());  
    
     jdbcOperations.update(INSERT_SPITTER,paramMap);      //执行数据时插入
 }
```

