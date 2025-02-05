---
title: ssm整合文档
date: 2020-09-31 10:10:12
tags: SSM
---

## SSM整合项目文档

<!--more-->

### 1.开发环境配置

- IDEA

- MySQL 5.7.19

- Tomcat 9

- Maven 3.6

  也可以参考https://juejin.cn/post/6847902223637331975#heading-9。

### 2.数据库创建

> 数据库环境

创建一个存放书籍数据的数据库表

```mysql
CREATE DATABASE `ssmbuild`;

USE `ssmbuild`;

DROP TABLE IF EXISTS `books`;

CREATE TABLE `books` (
`bookID` INT(10) NOT NULL AUTO_INCREMENT COMMENT '书id',
`bookName` VARCHAR(100) NOT NULL COMMENT '书名',
`bookCounts` INT(11) NOT NULL COMMENT '数量',
`detail` VARCHAR(200) NOT NULL COMMENT '描述',
KEY `bookID` (`bookID`)
) ENGINE=INNODB DEFAULT CHARSET=utf8

INSERT  INTO `books`(`bookID`,`bookName`,`bookCounts`,`detail`)VALUES
(1,'Java',1,'从入门到放弃'),
(2,'MySQL',10,'从删库到跑路'),
(3,'Linux',5,'从进门到进牢');
```

### 3.搭配基本项目构造

> 基本环境搭建

1、新建一Maven项目！ssmbuild ， 添加web的支持

2、导入相关的pom依赖！在pom.xml中导入。pom.xml建立项目就有。

```xml
<!--依赖Juint,数据库驱动，连接池，servlet,jsp,mybatis,mybatis-spring,spring-->
<dependencies>
    
   <!--Junit单元测试-->
   <dependency>
       <groupId>junit</groupId>
       <artifactId>junit</artifactId>
       <version>4.12</version>
   </dependency>
    
   <!--mysql数据库驱动-->
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>5.1.47</version>              //主要注意mysql对应安装的版本
   </dependency>
    
   <!-- 数据库连接池 --> 
   <dependency>
       <groupId>com.mchange</groupId>
       <artifactId>c3p0</artifactId>
       <version>0.9.5.2</version>
   </dependency>

     <!-- lombok依赖导入 -->
     <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.16.10</version>
        
    </dependency>
    
   <!--Servlet - JSP -->
   <dependency>
       <groupId>javax.servlet</groupId>
       <artifactId>servlet-api</artifactId>
       <version>2.5</version>
   </dependency>
    
   <dependency>
       <groupId>javax.servlet.jsp</groupId>
       <artifactId>jsp-api</artifactId>
       <version>2.2</version>
   </dependency>
    
   <dependency>
       <groupId>javax.servlet</groupId>
       <artifactId>jstl</artifactId>
       <version>1.2</version>
   </dependency>

   <!--Mybatis-->
   <dependency>
       <groupId>org.mybatis</groupId>
       <artifactId>mybatis</artifactId>
       <version>3.5.2</version>
   </dependency>
    <!--mybatis整合spring的包-->
   <dependency>
       <groupId>org.mybatis</groupId>
       <artifactId>mybatis-spring</artifactId>
       <version>2.0.2</version>
   </dependency>

   <!--Spring依赖-->
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-webmvc</artifactId>
       <version>5.1.9.RELEASE</version>
   </dependency>
    <!--spring操作数据库需要的包-->
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-jdbc</artifactId>
       <version>5.1.9.RELEASE</version>
   </dependency>
    <!--aop织入包 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.4</version>
    </dependency>
    
</dependencies>
```

3、Maven资源过滤设置,也在pom.xml中写入。接着上面代码

```xml
<build>
   <resources>
       <resource>
           <directory>src/main/java</directory>
           <includes>
               <include>**/*.properties</include>
               <include>**/*.xml</include>
           </includes>
           <filtering>false</filtering>
       </resource>
       <resource>
           <directory>src/main/resources</directory>
           <includes>
               <include>**/*.properties</include>
               <include>**/*.xml</include>
           </includes>
           <filtering>false</filtering>
       </resource>
   </resources>
</build>
```

4、建立基本结构和配置框架！

- com.kuang.pojo(实体类层，在java包下建立).与数据库表一一对应。

- com.kuang.dao(持久层，在java包下建立).

- com.kuang.service(业务层，在java包下建立).

- com.kuang.controller(表现层，在java包下建立).

- mybatis-config.xml(mybatis核心配置文件，在resource包下建立).

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
         PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
         "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  
  </configuration>
  ```

- applicationContext.xml(spring核心配置文件，在resource包下建立).

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
         http://www.springframework.org/schema/beans/spring-beans.xsd">
  
  </beans>
  ```

### 4.Mybatis层搭建

> Mybatis层编写

1、数据库配置文件 **database.properties**（在resource包下建立）。与数据库连接。

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/ssmbuild?useSSL=true&useUnicode=true&characterEncoding=utf8
&serverTimezone=Asia/Shanghai     //这是mysql8.0需要的配置
jdbc.username=root
jdbc.password=123456
```

2、IDEA关联数据库

3、编写MyBatis的核心配置文件。在mybatis-config.xml中编写。（在resource包下）。本来需要在这配置数据源，现在的话交给spring去做。现在一般绑定mapper接口。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
       PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
       "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    
    <setting>
    <setting name = "LogImpl" value="STDOUT_LOGGING"/>    //日志
    </setting>


    <!--  mybatis定义别名，别名是类名(首字母大写或小写都可以,一般用小写)  定义别名后，直接在resultType中使用类名，不然就需要使用全限定名来写-->  
   <typeAliases>
       <package name="com.kuang.pojo"/>  //pojo为实体类位置包
   </typeAliases>
         
    
    <!-- 标签中配置的是我们定义的 xml 文件,Mybatis 加载 mybatis-config.xml 中的 <mappers> 标签
   主要解析加载两大块内容：XML和class文件,XML和Class文件通过class的全类名进行关联。
   同时 class 中的方法定义会与 XML 中定义的 SQL 语句通过方法名进行关联。
   以上两大内容组成了调用 class 接口，实现 SQL 操作数据库的基础数据。-->
   <mappers>
       <mapper resource="com/kuang/dao/BookMapper.xml"/> //BookMapper.xml为dao层接口的具体实现。
   </mappers>

</configuration>
```

4、编写数据库对应的实体类 com.kuang.pojo.Books。使用lombok插件！(IDEA要自己下载)在pojo包下建立Books类。

```java
package com.kuang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Books {
   
   private int bookID;
   private String bookName;
   private int bookCounts;
   private String detail;
   
}
```

5、编写Dao层的 Mapper接口！（在dao包下建立）bookMapper接口。

```java
package com.kuang.dao;

import com.kuang.pojo.Books;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BookMapper {
    //增加一个Book
    int addBook(Books book);

    //根据id删除一个Book
    int deleteBookById(@Param("bookID") int id);

    //更新Book
    int updateBook(Books books);

    //根据id查询,返回一个Book
    Books queryBookById(@Param("bookID") int id);

    //查询全部Book,返回list集合
    List<Books> queryAllBook();
}
```

6、编写接口对应的 Mapper.xml 文件。需要导入MyBatis的包。（在dao包下建立）bookMapper.xml。每次写完xml文件都需要在mybatis核心配置文件中配置相应的导入。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
       PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
       "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--Mapper.xml文件中的namespace，就是对应的接口的类路径-->
<mapper namespace="com.kuang.dao.BookMapper">

    <!--  Mapper接口中的方法名和Mapper.xml中定义的每个SQL的id相同-->    
   <!--增加一个Book-->
    <!--Mapper接口方法的输入参数类型和mapper.xml中定义的每个sql parameterType类型相同  -->
   <insert id="addBook" parameterType="Books">  //parameterType：指定输入参数类型
      insert into ssmbuild.books(bookName,bookCounts,detail)
      values (#{bookName}, #{bookCounts}, #{detail})
   </insert>  
    <!-- 在MyBatis中提供了读取参数的内容到SQL语句中,#{参数名} :将参数的内容添加到sql语句中指定位置，实体类对象或则 Map 集合读取内容 -->
   <!--根据id删除一个Book-->
   <delete id="deleteBookById" parameterType="int">
      delete from ssmbuild.books where bookID=#{bookID}
   </delete>

   <!--更新Book-->
   <update id="updateBook" parameterType="Books">
      update ssmbuild.books
      set bookName = #{bookName},bookCounts = #{bookCounts},detail = #{detail}
      where bookID = #{bookID}
   </update>  
    <!--   Mapper接口中的方法的输出参数类型和mapper.xml中定义的每个sql的resultType的类型相同  -->
   <!--根据id查询,返回一个Book-->
   <select id="queryBookById" resultType="Books">  //resultType：指定输出结果类型
      select * from ssmbuild.books
      where bookID = #{bookID}
   </select>

   <!--查询全部Book-->
   <select id="queryAllBook" resultType="Books">
      SELECT * from ssmbuild.books
   </select>

</mapper>
```

7、编写Service层的接口和实现类

编写接口BookService。(在service包下建立)。与DAO层的mapper接口一一对应。

```java
package com.kuang.service;

import com.kuang.pojo.Books;
import java.util.List;

//BookService:底下需要去实现,调用dao层
public interface BookService {
   //增加一个Book
   int addBook(Books book);
   //根据id删除一个Book
   int deleteBookById(int id);
   //更新Book
   int updateBook(Books books);
   //根据id查询,返回一个Book
   Books queryBookById(int id);
   //查询全部Book,返回list集合
   List<Books> queryAllBook();
}
```

编写实现类BookServiceImpl类。(在service包下建立)。

```java
package com.kuang.service;

import com.kuang.dao.BookMapper;
import com.kuang.pojo.Books;
import java.util.List;

public class BookServiceImpl implements BookService {

   //调用dao层的操作，设置一个set接口，方便Spring管理
   @Autowired
   private BookMapper bookMapper;
 
    
   public void setBookMapper(BookMapper bookMapper) {
       this.bookMapper = bookMapper;
  }
    
   //方法实现，返回DAO层的接口方法。
   
   public int addBook(Books book) {
       return bookMapper.addBook(book);
  }
   
   public int deleteBookById(int id) {
       return bookMapper.deleteBookById(id);
  }
   
   public int updateBook(Books books) {
       return bookMapper.updateBook(books);
  }
   
   public Books queryBookById(int id) {
       return bookMapper.queryBookById(id);
  }
   
   public List<Books> queryAllBook() {
       return bookMapper.queryAllBook();
  }
}
```

**OK，到此，底层需求操作编写完毕！**

### 5.Spring层搭建

> Spring层整合mybatis层和spring mvc层

- 重点：spring整合的所有文件都应该在一个modules中。其中的spring-mvc.xml是6中的配置。

  [![DoVCLt.md.png](https://s3.ax1x.com/2020/12/02/DoVCLt.md.png)](https://imgchr.com/i/DoVCLt)](https://imgchr.com/i/DoETMR)



1、配置**Spring整合MyBatis**，我们这里数据源使用c3p0连接池；

2、我们去编写**Spring整合Mybatis**的相关的配置文件；spring-dao.xml。(在recource包下建立)spring-dao.xml。

```xml
<!--1.关联数据库配置文件。2.连接池。3.sqlSessionFactory-->


<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:context="http://www.springframework.org/schema/context"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd">

   <!-- 配置整合mybatis -->
   <!-- 1.关联数据库文件 -->
   <!-- 我们在基于spring开发应用的时候,一般都会将数据库的配置放置在properties文件中.-->
<!-- 这里的location值为参数配置文件的位置，这样.properties文件就会被spring加载-->
   <context:property-placeholder location="classpath:database.properties"/>

   <!-- 2.数据库连接池 -->
   <!--数据库连接池
       dbcp 半自动化操作 不能自动连接
       c3p0 自动化操作（自动的加载配置文件 并且设置到对象里面）,使用的就是这种，注意导入依赖
   -->
    <!--  class就是数据源的位置  -->
   <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
       <!-- 配置连接池属性 -->
       
       <!-- Value的值：${property : default_value}，注入的是外部参数对应的property的对应-->
       <property name="driverClass" value="${jdbc.driver}"/>
       <property name="jdbcUrl" value="${jdbc.url}"/>
       <property name="user" value="${jdbc.username}"/>
       <property name="password" value="${jdbc.password}"/>

       <!-- c3p0连接池的私有属性，一般是没有的，看实际情况编写 -->
       <!-- 最大连接池和最小连接池-->
       <property name="maxPoolSize" value="30"/>
       <property name="minPoolSize" value="10"/>
       <!-- 关闭连接后不自动commit -->
       <property name="autoCommitOnClose" value="false"/>
       <!-- 获取连接超时时间 -->
       <property name="checkoutTimeout" value="10000"/>
       <!-- 当获取连接失败重试次数 -->
       <property name="acquireRetryAttempts" value="2"/>
   </bean>

   <!-- 3.配置SqlSessionFactory对象，这是mybatis需要的映射 -->
   <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">    
       <!-- 注入数据库连接池，ref就是上面对应的id -->
       <property name="dataSource" ref="dataSource"/>
       
       <!-- 配置MyBaties全局配置文件:mybatis-config.xml -->
       <!--属性configLocation，它用来指定 MyBatis 的 XML 配置文件路径 -->
       <property name="configLocation" value="classpath:mybatis-config.xml"/>
   </bean>

   
    <!-- 4.配置扫描Dao接口包，动态实现Dao层的接口注入到spring容器中，这时就不需要再写配置类扫描dao层了-->
   <!--解释 ：https://www.cnblogs.com/jpfss/p/7799806.html-->
   <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
       <!-- 注入sqlSessionFactory，value属性对应上方的bean id -->
       <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
       <!-- 给出需要扫描Dao接口包，value属性为需要扫描的包 -->
       <property name="basePackage" value="com.kuang.dao"/>   
   </bean>

    
</beans>
```

3、**Spring整合service层**的配置文件spring-service.xml。(在recource包下建立)spring-service.xml。

```xml
<!--1.扫描service包。2.将service下的业务类注入到Spring中，可以通过配置或者注解实现。3.声明式事务配置-->

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:context="http://www.springframework.org/schema/context"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
   http://www.springframework.org/schema/beans/spring-beans.xsd
   http://www.springframework.org/schema/context
   http://www.springframework.org/schema/context/spring-context.xsd">

   <!-- 1.扫描service层下的bean -->
    <!--扫描到有@Component、@Controller、@Service 、@Repository等注解修饰的Java类，则将这些类注册为spring容器中的bean。-->
   <context:component-scan base-package="com.kuang.service" />   //service层的包位置

   <!--2.BookServiceImpl注入到IOC容器中，这步是扫描实现类如果有注解可以不写-->
   <bean id="BookServiceImpl" class="com.kuang.service.BookServiceImpl"> //service层的接口的实现类
       <property name="bookMapper" ref="bookMapper"/> //引入bookMapper接口
   </bean>

   <!-- 3.配置声明式事务管理器 -->
   <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
       <!-- 注入数据库连接池 -->
       <property name="dataSource" ref="dataSource" />
   </bean>
    
    <!--配置声明式事务通知，这是增删改查注解必须要配置的-->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
   <tx:attributes>
       <!--配置哪些方法使用什么样的事务,配置事务的传播特性-->
       <tx:method name="add" />
       <tx:method name="delete" />
       <tx:method name="update" />
       <tx:method name="search*" />
       <tx:method name="get" />
       <tx:method name="*" />
   </tx:attributes>
</tx:advice>

    <!--4.配置aop织入事务，看情况是否加，一般是dao层-->
<aop:config>
   <aop:pointcut id="txPointcut" expression="execution(* com.kuang.dao.*.*(..))"/>
   <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointcut"/>
</aop:config>
</beans>
```

Spring层搞定！再次理解一下，Spring就是一个大杂烩，一个容器！对吧！

### 6.SpringMVC层搭建

1、编写web配置文件**web.xml**。需要增加web支持让它成为Web项目（在WEB-INF包下建立jsp包，将web.xml放入jsp包中）需要IDEA添加web组件，这样就会自动创建WEB相关文件。创建方式为单机右键项目第二个选项勾上web。

```xml
<!--1.配置DispatcherServlet(前端控制器)。2.乱码过滤-->

<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
        version="4.0">
<!--spring mvc相关配置文件 -->
   <!--1.DispatcherServlet-->
   <!--注册servlet组件的必须的四个节点，<servlet-name>，<servlet-class>，<servlet-mapping>，<url-pattern>-->
   <servlet>
       <servlet-name>DispatcherServlet</servlet-name>  //注册servlet的名字
       <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class> //注册servlet的类地址
       <init-param>
           <param-name>contextConfigLocation</param-name>
           <!--一定要注意:我们这里加载的是总的配置文件，-->  
           <param-value>classpath:applicationContext.xml</param-value>
       </init-param>
       <load-on-startup>1</load-on-startup>    //添加这个会自动加载web.xml
   </servlet>
    
   <servlet-mapping>  //配置我们注册的组件的访问路径
       <servlet-name>DispatcherServlet</servlet-name> //与前面写的servlet名字一致
       <url-pattern>/</url-pattern>  //配置这个组件的访问路径
   </servlet-mapping>

   <!--2.配置encodingFilter过滤器，实现乱码过滤。这步有时候有问题，选择添加。-->
   <filter>
       <filter-name>encodingFilter</filter-name>
       <filter-class>
          org.springframework.web.filter.CharacterEncodingFilter
       </filter-class>
       <init-param>
           <param-name>encoding</param-name>
           <param-value>utf-8</param-value>
       </init-param>
   </filter>
    <!--这是实现过滤的操作-->
   <filter-mapping>
       <filter-name>encodingFilter</filter-name>
       <url-pattern>/*</url-pattern>
   </filter-mapping>
   
   <!--3.配置Session过期时间，可用可不用-->
   <session-config>
       <session-timeout>15</session-timeout>
   </session-config>
   
</web-app>
```

2、编写spring mvc配置文件**spring-mvc.xml**。(在recource包下建立)spring-mvc.xml。

```xml
<!--spring mvc3件套：1.映射器。2.视图解析器。3.文件解析器。-->
<!--这时需要1.注解驱动。2.静态资源过滤。3.扫描controller包，使它注入进来。4.视图解析器-->

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:context="http://www.springframework.org/schema/context"
      xmlns:mvc="http://www.springframework.org/schema/mvc"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
   http://www.springframework.org/schema/beans/spring-beans.xsd
   http://www.springframework.org/schema/context
   http://www.springframework.org/schema/context/spring-context.xsd
   http://www.springframework.org/schema/mvc
   https://www.springframework.org/schema/mvc/spring-mvc.xsd">

   <!-- 配置SpringMVC -->
   <!-- 1.开启SpringMVC注解驱动 -->
   <mvc:annotation-driven />
   <!-- 2.静态资源默认servlet配置-->
   <mvc:default-servlet-handler/>
    
    <!-- 3.扫描web相关的bean -->
   <context:component-scan base-package="com.kuang.controller" /> //这是controller位置的包

   <!-- 4.配置ViewResolver视图解析器 -->
   <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
       <property name="viewClass" value="org.springframework.web.servlet.view.JstlView" />
       <!-- 配置前缀-->
       <property name="prefix" value="/WEB-INF/jsp/" />
       <!--配置后缀-->
       <property name="suffix" value=".jsp" />
   </bean>

</beans>
```

- 此时，配置视图解析器后要在WEB-INF下创建jsp包与之对应。

  [![DoQTQU.png](https://s3.ax1x.com/2020/12/02/DoQTQU.png)](https://imgchr.com/i/DoQTQU)



3、**Spring的核心文件配置整合文件，applicationContext.xml**。(在recource包下建立)applicationContext.xml。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 将spring与dao,service,mvc层的配置依赖全部导入进来 -->
   <import resource="spring-dao.xml"/>
   <import resource="spring-service.xml"/>
   <import resource="spring-mvc.xml"/>
   
</beans>
```

配置文件，结束。此时还需要点击File,选择Project Settings中的Artifaces中的WEB-INF下建立lib包，点击+将依赖全部放入包中。

[![Do3UZ4.md.png](https://s3.ax1x.com/2020/12/02/Do3UZ4.md.png)](https://imgchr.com/i/Do3UZ4)

### 7.Controller 和 视图层搭建

1、编写BookController 类 ，（在controller包下建立）BookController类。 方法一：查询全部书籍。

```java
@Controller
@RequestMapping("/book")  //映射请求
public class BookController {

   @Autowired
   @Qualifier("BookServiceImpl") //指定要装配bean的名字，这是因为2个service可能会出现错误
   private BookService bookService; 
//查询全部的书籍，并且返回一个书籍展示页面
   @RequestMapping("/allBook")
   public String list(Model model) {
       List<Books> list = bookService.queryAllBook();   //调用方法获取所有书籍
       model.addAttribute("list", list);     //将所有书籍放入model中
       return "allBook";      //返回到allBook页面
  }
}
```

2、编写首页 **index.jsp**。(在WEB-INF包下)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE HTML>
<html>
<head>
   <title>首页</title>
   <style type="text/css">
       a {
           text-decoration: none;
           color: black;
           font-size: 18px;
      }
       h3 {
           width: 180px;
           height: 38px;
           margin: 100px auto;
           text-align: center;
           line-height: 38px;
           background: deepskyblue;
           border-radius: 4px;
      }
   </style>
</head>
<body>

<h3>
    <!--JSP取得绝对路径的方法    /book/allBook为controller层中类的对应的映射请求 -->
   <a href="${pageContext.request.contextPath}/book/allBook">点击进入列表页</a>
</h3>
</body>
</html>
```

3.如果出错了那么使用juint单元测试

```java
public class MyTest {
    @Test
    public void test(){
        //这时需要获取所有的配置文件，因此是application。
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //获得我们注册的bean
        BookService bookServiceImpl = (BookService) context.getBean("BookServiceImpl");
        for (Books books : bookServiceImpl.queryAllBook()) {
            System.out.println(books);
            
        }
    }
}
```



4、编写书籍列表页面 **allbook.jsp**。（在WEB-INF包下建立jsp包），在jsp包下建立allbook.jsp

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
   <title>书籍列表</title>
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <!-- 引入 Bootstrap -->
   <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">

   <div class="row clearfix">
       <div class="col-md-12 column">
           <div class="page-header">
               <h1>
                   <small>书籍列表 —— 显示所有书籍</small>
               </h1>
           </div>
       </div>
   </div>

   <div class="row">
       <div class="col-md-4 column">
           <a class="btn btn-primary" href="${pageContext.request.contextPath}/book/toAddBook">新增</a>
       </div>
   </div>

   <div class="row clearfix">
       <div class="col-md-12 column">
           <table class="table table-hover table-striped">
               <thead>
               <tr>
                   <th>书籍编号</th>
                   <th>书籍名字</th>
                   <th>书籍数量</th>
                   <th>书籍详情</th>
                   <th>操作</th>
               </tr>
               </thead>
<%--书籍从数据库中查询出来，之前model已经封装到list集合中了，需要从list遍历出来：for each--%>
<%--var是名字--%>
<%--对应后端model.addAttribute("list", list);   //返回前端展示，数据在list中，因为allBook页面需要展示数据--%>              
               <tbody>
               <c:forEach var="book" items="${requestScope.get('list')}">
                   <tr>
                       <td>${book.getBookID()}</td>
                       <td>${book.getBookName()}</td>
                       <td>${book.getBookCounts()}</td>
                       <td>${book.getDetail()}</td>
                       <td>
                           <a href="${pageContext.request.contextPath}/book/toUpdateBook?id=${book.getBookID()}">更改</a> |
                           <a href="${pageContext.request.contextPath}/book/del/${book.getBookID()}">删除</a>
                       </td>
                   </tr>
               </c:forEach>
               </tbody>
           </table>
       </div>
   </div>
</div>
```

4、BookController 类编写 ， 方法二：添加书籍。添加到controller包下的bookController类中。

```java
@RequestMapping("/toAddBook")
public String toAddPaper() {
   return "addBook";         //跳转到增加页面
}

//添加数据的请求
@RequestMapping("/addBook")
public String addPaper(Books books) {     //前端传入的是一个Books。
   bookService.addBook(books);
   return "redirect:/book/allBook";      //重定向到allBook请求
}
```

5、添加书籍页面：**addBook.jsp**。在WEB-INF包下建立jsp包，在jsp包下建立addBook.jsp。

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
   <title>新增书籍</title>
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <!-- 引入 Bootstrap -->
   <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">

   <div class="row clearfix">
       <div class="col-md-12 column">
           <div class="page-header">
               <h1>
                   <small>新增书籍</small>
               </h1>
           </div>
       </div>
   </div>
   <form action="${pageContext.request.contextPath}/book/addBook" method="post">
      书籍名称：<input type="text" name="bookName"><br><br><br>
      书籍数量：<input type="text" name="bookCounts"><br><br><br>
      书籍详情：<input type="text" name="detail"><br><br><br>
       <input type="submit" value="添加">
   </form>

</div>
```

6、BookController 类编写 ， 方法三：修改书籍。添加到controller包下的bookController类中。

```java
//添加书籍的请求
@RequestMapping("/toUpdateBook")
public String toUpdateBook(Model model, int id) {   //加Model是因为跳转到修改页面需要当前书籍的信息。
   Books books = bookService.queryBookById(id);
   model.addAttribute("book",books );
   return "updateBook";          //跳转到修改页面
}

@RequestMapping("/updateBook")
public String updateBook(Books book) {
   bookService.updateBook(book);
   return "redirect:/book/allBook";
}
```

7、修改书籍页面  **updateBook.jsp**。在WEB-INF包下建立jsp包，在jsp包下建立updateBook.jsp。

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
   <title>修改信息</title>
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <!-- 引入 Bootstrap -->
   <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">

   <div class="row clearfix">
       <div class="col-md-12 column">
           <div class="page-header">
               <h1>
                   <small>修改信息</small>
               </h1>
           </div>
       </div>
   </div>

   <form action="${pageContext.request.contextPath}/book/updateBook" method="post">
       <input type="hidden" name="bookID" value="${book.getBookID()}"/>  //前端ID隐藏域传递
      书籍名称：<input type="text" name="bookName" value="${book.getBookName()}"/>
      书籍数量：<input type="text" name="bookCounts" value="${book.getBookCounts()}"/>
      书籍详情：<input type="text" name="detail" value="${book.getDetail() }"/>
       <input type="submit" value="提交"/>
   </form>

</div>
```

8、BookController 类编写 ， 方法四：删除书籍

```java
//删除书籍，前端RESTFUL风格。/del/${book.getBookID()}
@RequestMapping("/del/{bookId}")
public String deleteBook(@PathVariable("bookId") int id) {    //说明传递是bookId,与前面对应
   bookService.deleteBookById(id);
   return "redirect:/book/allBook";
}
```

5.查询书籍

```java
@RequestMapping("/queryBook")
public String queryBook(String queryBookName Model model) {    //说明传递是bookId,与前面对应
   bookService.queryBookByName(queryBookName);
    List<Books> list = new ArrayLiat<Books>();
    model.addAttribute("list","list")
   return "allBook";
}
```

**配置Tomcat，进行运行！**

到目前为止，这个SSM项目整合已经完全的OK了，可以直接运行进行测试！

**项目结构图** 

[![BUAxde.png](https://s1.ax1x.com/2020/10/31/BUAxde.png)](https://imgchr.com/i/BUAxde)



[![BUEZdg.png](https://s1.ax1x.com/2020/10/31/BUEZdg.png)](https://imgchr.com/i/BUEZdg)

### SSM框架下web项目的运行流程

#### 1.SSM中各层作用及关系

##### 1.持久层：DAO层（mapper层）（属于mybatis模块）

- DAO层：主要负责与数据库进行交互设计，用来处理数据的持久化工作。
- DAO层的设计首先是设计DAO的接口，也就是项目中你看到的Dao包。
- 然后在Spring的xml配置文件中定义此接口的实现类，就可在其他模块中调用此接口来进行数据业务的处理，而不用关心接口的具体实现类是哪个类，这里往往用到的就是反射机制，DAO层的jdbc.properties数据源配置，以及有 关数据库连接的参数都在Spring的配置文件中进行配置。
- ps:（有的项目里面Dao层，写成mapper，当成一个意思理解。）

##### 2.业务层：Service层（属于spring模块）

- Service层：主要负责业务模块的逻辑应用设计。也就是项目中你看到的Service包。
- Service层的设计首先是设计接口，再设计其实现的类。也就是项目中你看到的service+impl包。
- 接着再在Spring的xml配置文件中配置其实现的关联。这样我们就可以在应用中调用Service接口来进行业务处理。
- 最后通过调用DAO层已定义的接口，去实现Service具体的实现类。
- ps:(Service层的业务实现，具体要调用到已定义的DAO层的接口.)

##### 3.控制层/表现层：Controller层（Handler层） （属于springMVC模块）

- Controller层：主要负责具体的业务模块流程控制，也就是你看到的controller包。
- Controller层通过要调用Service层的接口来控制业务流程，控制的配置也同样是在Spring的xml配置文件里面，针对具体的业务流程，会有不同的控制器。

##### 4.View层 （属于springMVC模块）

- 负责前台jsp页面的展示，此层需要与Controller层结合起来开发。
- Jsp发送请求，controller接收请求，处理，返回，jsp回显数据。

#### 2.三层架构运行流程

[![BUEnij.png](https://s1.ax1x.com/2020/10/31/BUEnij.png)](https://imgchr.com/i/BUEnij)

#### 3.各层之间的联系

- DAO层，Service层这两个层次可以单独开发，互相的耦合度很低。

- Controller，View层耦合度比较高，因而要结合在一起开发。也可以听当做两层来开发，这样，在层与层之前我们只需要知道接口的定义，调用接口即可完成所需要的逻辑单元应用，项目会显得清晰简单。

- 值得注意的是，Service逻辑层设计：

  - Service层是建立在DAO层之上的，在Controller层之下。因而Service层应该既调用DAO层的接口，又提供接口给Controller层的类来进行调用，它处于一个中间层的位置。每个模型都有一个Service接口，每个接口分别封装各自的业务处理方法。

    [![BUEMzq.png](https://s1.ax1x.com/2020/10/31/BUEMzq.png)](https://imgchr.com/i/BUEMzq)

#### 5.SSM框架实现一个web程序主要使用到如下三个技术：

1. Spring：用到注解和自动装配，就是Spring的两个精髓IOC(反向控制)和 AOP(面向切面编程)。
2. SpringMVC：用到了MVC模型，将流程控制代码放到Controller层处理，将业务逻辑代码放到Service层处理。
3. Mybatis：用到了与数据库打交道的层面，dao（mapper）层，放在所有的逻辑之后，处理与数据库的CRUD相关的操作。

比如你开发项目的时候，需要完成一个功能模块：

1. 先写实体类entity，定义对象的属性，（可以参照数据库中表的字段来设置，数据库的设计应该在所有编码开始之前）。
2. 写Mapper.xml（Mybatis），其中定义你的功能，对应要对数据库进行的那些操作，比如 insert、selectAll、selectByKey、delete、update等。
3. 写Mapper.java/Dao.java，将Mapper.xml中的操作按照id映射成Java函数。实际上就是Dao接口，二者选一即可。
4. 写Service.java，为控制层提供服务，接收控制层的参数，完成相应的功能，并返回给控制层。
5. 写Controller.java，连接页面请求和服务层，获取页面请求的参数，通过自动装配，映射不同的URL到相应的处理函数，并获取参数，对参数进行处理，之后传给服务层。
6. 写JSP页面调用，请求哪些参数，需要获取什么数据。