---
title: spring实战4第3章
date: 2020-07-05 15:14:17
tags: spring
---

##  第三章，高级装配

此章讲解环境与profile，条件化的bean，处理自动装配的歧义性，bean的作用域，运行时值注入。

<!--more-->

### 3.0.各种注解

#### 1.@profile

@profile注解，在java配置中可以指定某个bean属于哪一个profile。

```java
@profile（"dev"）   //只有dev profile激活的时候，才会创建对应的bean
public   DataSource   dataSource(){--}    //DataSoure的bean

```

#### 2.@Conditional

@Conditional注解，它可以用在带有@bean注解的方法上，如果给点的条件为true，就创建，否则忽视这个bean。

```
@bean
@Conditional(MagicExistsCondition.class)           //在xx.class中找matches方法的某个属性是否存在。
 public MagicBean  magicBean(){                        //有则创建bean。
   return  new   MagicBean();
    }
```

#### 3.@Primary

@Primary注解，标志首选的bean，避免自动装配歧义性，与@compoent组合用在组件扫描的bean上，
也可以与@bean组合用在java配置的bean声明中。

```java
例子1：    @Compoent
          @Primary
          public  class   IceCream  implements  Dessert{---}

例子2：   @bean
         @Primary
         public   Dessert   iceCream(){---}

```

#### 4.@Qualifier

@Qualifier注解，是使用限定符的主要方式。可以与@Autowired和@Inject协同使用。

```java
 @Autowired
 @Qualifier("iceCream")                            //将IceCream类bean注入到setDessert（）中
 public   void   setDessert(Dessert   dessert)    {---}
```

#### 5.@Scope

单例是默认的作用域，对于易变的类，如果选择其他的作用域，要使用@Scope注解，
可以与@Component或@bean一起使用。

```java
@Component
@Scope(ConfigureableBeanFactory.SCOPE_PROTOTYPE)    //常量
public  class   Notepad(---)                        //将Notepad声明为原型bean
```



### 3.1.环境与profile

在开发软件的时候，需要从一个环境迁移到另一个环境。因此可以在同一个部署环境中配置各种bean在特地的机会用到。

#### 3.1.1.配置profile bean

要使用profile，要将所有的bean定义整理到一个或多个profile中，在使用时也要确保对应的profile处于激活状态。

在java配置中，可以使用@Profile注解在类上指定某个bean属于哪一个profile。

```java
@profile（"dev"）   //只有dev profile激活的时候，才会创建对应的bean
public   DataSource   dataSource(){--}    //DataSoure的bean
```

@profile注解可以与@bean注解一起使用在方法上，就能将这两个bean的声明放在同一个配置类中。

```java
@bean                                    //为dev profile装配的bean
@proflie("dev"）
public  DataSoure embeddedDataSoure(){--}

```

也可以通过<beans>元素的profile属性在XML中配置profile bean。

```xml
<beans ------------
     profile="dev">
    <jdbd----->        //指定数据库等
 </beans>

```

可以在<beans>元素中嵌套定义<beans>元素，而不是为每个环境都创建一个profile XML文件。

```xml
<beans--->
    <beans profile="dev">
       <jdbc------->
    </beans>
    <beans profile="qa">
           <----->
    </beans>
</beans>
```

#### 3.1.2.激活profile

确定那个profile处于激活，需要依赖两个独立的属性。

1.spring.profile.active：确定那个profile是激活的。

2.spring.profile.default：在active属性没设置时替代active来确定。

有多种方式来设置这两个属性：

1.作为DispatcherServlet的初始化参数。

2.作为Web应用的上下文参数。

3.作为JNDI条目。

4.作为环境变量。

5.作为JVM的系统属性。

6.在集成测试类上，使用@ActiveProfiles注解设置。

例如将default设置为开发环境的profile。

```xml
<context-param>
   <param-name>spring.profiles.default</param-name>      //为上下文设置默认的profile
   <param-value>dev</param-value>              //dev为默认的profile
   </context-param>  
        -----------
     <servlet>--
        <param-name>spring.profiles.default</param-name>      //为servlet设置默认的profile
         <param-value>dev</param-value>                   //dev为默认的profile
           </servlet>
```

在测试时，使用@ActiveProfile注解来指定运行测试时要激活哪个profile。

```java
@AutiveProfiles("dev")              //激活dev
 public   class   persistenTest(){--}
```

### 3.2.条件化的bean

有时希望bean在特地的路径下包含特定库才会创建，或者其他条件，@Conditional注解可以实现这种级别的条件化配置，它可以用在带有@bean注解的方法上，如果给点的条件为true，就创建，否则忽视这个bean。

```java
@bean
@Conditional(MagicExistsCondition.class)           //在xx.class中找matches方法的某个属性是否存在。
  public MagicBean  magicBean(){                        //有则创建bean。
    return  new   MagicBean();
 }
```

@profile可以还可以基于@Conditional和condition实现。

```java
@Conditional(profileCondition.class)
public  @interface   profile{--}    
```

### 3.3.处理自动装配的歧义性

如果方法中有多种参数时，接口对应的不止一种，此时就会出现歧义，这时候就需要将可选bean的某一个设为首选（primary）的bean，或者使用限定符（qualifier）来帮助Spring将可选的bean的范围缩小到只有一个bean。

#### 3.1.1.标志首选的bean

在spring中，可以通过@Primary来表达最喜欢的方案，@Primary与@compoent组合用在组件扫描的bean上

```java
@Compoent
@Primary           //将@Component注解的IceCream bean声明为首选的bean
 public  class   IceCream  implements  Dessert{---}
```

也可以与@bean组合用在java配置的bean声明中。

```java
@bean
@Primary       
public   Dessert   iceCream(){
     return new IceCream()  };
      
```

如果使用XML配置bean的话，同样可以实现，使用<bean>元素的primary属性来指定首选的bean

```xml
<bean id ="iceCream"  class="com.desserteater.IceCream"
   primary="true"/>
```

#### 3.3.2.限定自动装配的bean

设置首选bean的局限性在于@Primary无法将可选方案的范围限定到唯一一个无歧义的选型中，它只能标示一个优先的可选方案。

因此需要限定符能够缩小范围，使用@Qualifier注解，将bean的 ID注入到参数中确定。可以与@Autowired和@Inject协同使用。

```java
@Autowired
@Qualifier("iceCream")                            //将IceCream bean注入到setDessert（）中
public   void   setDessert(Dessert   dessert)    {---}
```

还可以创建自定义的限定符，需要的就是在bean声明上添加@Qualifier注解。在注入时使用cold限定符就可以了。

```java
声明
@component
@Qualifer（"cold"）           //ID 为cold
public   class   IceCream    implements    Dessert(----)
    
注入
@Autowired
@Qualifier("cold")
public  class  setDessert(Dessert   dessert){--}
```

### 3.4.bean的作用域

spring定义了多种作用域，可以基于这些作用域创建bean。

1.单例：在整个应用中，只创建bean的一个实例。

2.原型：每次注入或者通过spring应用上下获取的时候，都会创建一个新的bean实例。

3.会话：在Web应用中，为每个会话创建一个bean的实例。

4.请求：在Web应用中，为每个请求创建一个bean的实例。

单例是默认的作用域，对于易变的类，如果选择其他的作用域，要使用@Scope注解，
可以与@Component或@bean一起使用。

```java
@Component
@Scope(ConfigureableBeanFactory.SCOPE_PROTOTYPE)    //常量，原型作用域
 public  class   Notepad(---)                        //将Notepad声明为原型bean
```

如果使用XML来配置bean的话，可以使用<bean>元素的scope属性来设置作用域。

```xml
<bean id="noted"  class="com.myapp.Notepad"
 scope="prototype"/>        //ConfigureableBeanFactory.SCOPE_PROTOTYPE类似
```

#### 3.4.1.使用会话和请求作用域

在Web应用中，如果能够实例化在会话和请求范围共享的bean，那是非常有价值的东西。

要指定会话作用域，我们可以使用@Scope注解，使用与原型作用域一样。

```java
@Component
@Scope(
     value=WebApplicationContext.SCOPE_SESSION,     //告诉spring为web应用的每一个会话,创建shoppingCart，会创建多个                                                          bean的实例。
             proxyMode=ScopedProxyMode.INTERFACES)    //解决将会话或请求作用域的bean注入到单例bean的问题
 public  shoppingCart  cart(){--}
```

#### 3.4.2.在XML中声明作用域代理

<bean>元素的scope属性能够设置bean的作用域，设置代理需要使用spring aop命名空间的一个新元素。

```xml
<bean id="cart"   class="com.myapp.ShopingCart"
  Scope="session">               //与@Scope注解相同
  <aop:scoped-proxy/>        //与proxyMode属性相同,它会告诉spring为bean创建一个作用域代理。
  <bean>
```

还可以将proxy--target-class属性设置为false，进而要求它生成基于接口的代理。

```xml
<aop:scoped-proxyproxy--target-class="false"/>
```

### 3.5.运行值注入

spring引入了spring表达式语言（SpEL）进行装配，它能够以一种简单的方式将值装配到bean属性和构造器参数中。

SpEL拥有很多特效，包括

1.使用bean 的ID来引用bean。

2.调用方法和访问对象的属性。

3.对值进行算术，关系和逻辑运算。

4.正则表达式匹配。

5.集合操作。

SpEL表达式要放在  #{....}之中，这和属性占位符类似，属性占位符需要放在  ${.....}中。

**表示字面值**

#{3.14}      //表示浮点值3.14

#{‘ hello’}     //表示String类型的字面量

#{false}      //表示Boolean类型的值

**引用bean，属性和方法**

#{sgtPeppers}    //引用bean ID为sgtPeppers。

#{sgtPeppers.artist}    //引用bean ID为sgtPeppers的artist属性。

#{sgtPeppers.selectArtist()}    //引用bean ID为sgtPeppers的selectArtist()方法

以上只是SpEL表达式简单的使用，具体可以自行上网查询其他。