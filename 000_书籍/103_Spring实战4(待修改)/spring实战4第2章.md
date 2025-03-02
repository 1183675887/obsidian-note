---
title: spring实战4第2章
date: 2020-07-04 18:12:20
tags:
  - spring
  - spring实战4
---

## 第二章，装配Bean
此章讲解自动化装配bean,通过java装配bean，通过XML装配bean。

<!--more-->

### 2.0.各种注解

#### 1.@Component

@component注解，表明该类会作为组件类，并告知Spring要为这个类创建bean，不过组件扫描默认是不启动的。
并且bean的默认ID为首字母变为小写的类名。

```java
@component
public  class  sgtppers  implements  compactDisac{-- }  //sgtppers就是此类
```

#### 2.@ComponentScan

@componentScan注解，表明在spring中启动组件扫描，如果没有其他配置的话，默认扫描与配置类相同的包。

```java
 @componentScan
 public  class  CDplayerConfig{  --}     //  扫描CDplayerConfig类位于的包和包下的所有子包 
```

#### 3.@contextConfiguration

@contextConfiguration注解，会告诉你它需要在类中加载配置，因为类中包含@componentScan注解。

```java
 @contextConfiguration（"classes=CDPlayerConfig.class"）//classes代表那个类
 public  class  CDplayerTest{--}

```

#### 4.@Autwired

@Autowired注解，表明当spring创建类的bean的时候，会通过这个构造器来进行实例化，并且会传入一个可设置给类的类型的bean。

```java
 @Autowired
 public  CDplayer（CompactDisc   cd）{ --}       //创建的是CDplayer类。 将CompactDisc bean传入类中 
```

不仅能够用在构造器上，还可以用在属性的setter方法上。

```java
@Autowired
public  void  setCompactDisc（CompactDisc   cd）{--}   //类的setCompactDisc方法。 将CompactDisc bean传入类中
```

#### 5.@Configuration

@configuration注解，表明这个类是一个配置类。

```java
@configuration
public  class  CDplayerConfig{--}                
```

#### 6.@Bean

在javaConfig中声明bean，需要编写方法，这个方法会创建所需类型的实例，然后给这个方法添加@Bean注解。

```java
@Bean
public  CompactDisc  sgtPeppers{        //声明了CompactDisc bean
  return  new  sgtPeppers();           //返回sgtPeppers对象,将其注册为spring应用上下文的bean
}
```



### 2.1.Spring配置的可行性方案

提供了3种主要的配置方案：

1.在xml中显示配置

2.在java中进行显示配置

3.隐式的bean发现机制和自动装配

**尽可能地使用自动配置的机制。显示配置越少越好。必须显示配置bean的时候，推荐javaConfig而不是XML。**

### 2.2.自动化配置bean

Spring从两个角度来实现自动化配置：

1.组件扫描：Spring会自动发现应用上下文中所创建的bean。

2.自动装配：Spring自动满足bean之间的依赖。

#### 2.2.2.为组件扫描的bean命名

Spring默认会给类名为其指定一个ID，默认是将类名的第一个字母变为小写。

如果想设置不同ID，则需要将ID作为值传递给**@Component注解**。

```java
@component（"lonelyHeartsClub"）     // lonelyHeartsClub 为 bean ID
public  class  sgtppers  implements  compactDisac{ -- } //sgtppers就是此类
```

或者使用java依赖规范提供的**@Name**注解替代**@Compoent**为bean设置ID,但是不推荐。

#### 2.2.3.设置组件扫描的包

指定不同的包，在**@ComponentScan**的中指定包的名称。

```java
@componentScan（basePackage="Soundsystem"）  //basePackage后为包名
public  class  CDplayerConfig{ -- }  
```

也可以指定为包所含的类或接口。

```java
 @componentScan（basePackageClasses={CDplayer.class,DVDplayer.class}）  //basePackageClasses后为类名或接口名
 public  class  CDplayerConfig{---}

```

#### 2.2.4.通过为bean添加注解实现自动装配

@Autowired注解，表明当spring创建类的bean的时候，会通过这个构造器来进行实例化，并且会传入一个可设置给类的类型的bean。

```java
 @Autowired
 public  CDplayer（CompactDisc   cd）{ --}       //创建的是CDplayer类。 将CompactDisc bean传入类中 
```

不仅能够用在构造器上，还可以用在属性的setter方法上。

```java
@Autowired
public  void  setCompactDisc（CompactDisc   cd）{--}   //类的setCompactDisc方法。 将CompactDisc bean传入类中
```

### 2.3.通过java代码装配bean

#### 2.3.1.创建配置类

1.@Configuration注解，表明这个类是一个配置类。

```java
@configuration
public  class  CDplayerConfig{--}  
```

#### 2.3.2.声明简单的bean

在javaConfig中声明bean，需要编写方法，这个方法会创建所需类型的实例，然后给这个方法添加@Bean注解。

```java
@Bean
public  CompactDisc  sgtPeppers{        //声明了CompactDisc bean
  return  new  sgtPeppers();           //返回sgtPeppers对象,将其注册为spring应用上下文的bean
}
```

默认情况下Bean的ID与带有@Bean注解的方法名是一样的，如果想取别的名，通过name属性。

```java
@Bean（name="longlyHertsClubBand"）  //bean ID为longlyHertsClubBand
 public  CompactDisc  sgtPeppers{        
    return  new  sgtPeppers();       
     }
```

#### 2.3.3.借助JavaConfig实现注入

在声明后，需要装配。装配最简单方式就是引用创建bean的方法。

```java
 @Bean
 public  CDplayer    cdplayer(){         //cdplay方法会创建CDplayer bean
   return   new   CDplayer(sgtPeppers（）); //返回的sgtPeppers对象，将其注册为spring应用上下文的bean
     }
```

通过调用方法引用不太好，因此换构造器注入最好。

```java
@Bean
 public  CDplayer    cdplayer(CompactDisc  compactDisc){  //cdplay方法请求一个CompactDisc为参数。
   return   new   CDplayer(compactDisc);            //这时就会自动装配一个ConpactDisc到配置方法中。               
     }
```

这种方式引用其他的bean是最佳的选择，因为它不会要求将CompactDisc声明到同一配置类之中。

也可以通过setter方法。

```java
@Bean
public  CDplayer    cdplayer(CompactDisc  compactDisc){  //cdplay方法请求一个CompactDisc为参数。
    CDplayer   cdplayer  = new CDplayer(compactDisc);       
       cdplay.setCompactDisc(compactDisc);
         return  cdplay;                        
                 }  
```

### 2.4.通过XML装配bean

#### 2.4.2.声明一个简单的<bean

<bean>元素类似于JavaConfig中的@Bean注解。通过class属性来确定这个bean的类

```xml
<bean class= "soundsystem.SgtPeppers" /> soundsystem包名，SgtPeppers类名
```

因为没有明确ID,所以会是soundsystem.SgtPeppers#0，所以需要通过id属性自定义bean ID

```xml
<bean  id="compactDisc"  class ="soundsystem.sgtPeppers"/>   //compactDisc为ID名
```

#### 2.4.3.借助构造器注入初始化bean

在XML中，只有一种声明bean的方式，但是声明DI的时候，会有两种基本的配置方案选择。

1.<constructor-arg>元素。

2.使用Spring3.0所引用的c-命名空间。

<constructor-arg>元素,ref属性告知将一个ID为xx的bean引用传递到类构造器中

```XML
<bean  id="cdplay" class ="soundsystem.CDplayer">   //CDplayer为类
  <constructor-arg  ref="compactDisc"/>            //compactDisc为ID名
 </bean>                                           //compactDisc bean引用传递到CDplay的构造器中
```

也可以使用c-命名空间。

```xml
<bean  id="cdplay"    class ="soundsystem.CDplayer"
   c:cd-ref="compactDisc"/>
c://C-空间命名前缀，cd//构造器参数名，-ref//注入bean引用，“”//为要注入的bean ID
```

前面都是对象的引用装配，有时候，我们需要的只是一个字面量来配置对象。用value属性将字面量注入到构造器中。

```xml
<bean  id="cdplay"    class ="soundsystem.CDplayer">
   <constructor-arg  value="xxx"/>    //xxx为字面量
</bean> 
```

还可以装配集合。<list>元素装配集合。或者<set>元素，可替换。

```xml
<constructor-arg>
              <list>
                  <value>xxx<value/>
                  <value>xxx<value/>
               </list>
            </constructor-arg>
```

也可以用<ref>元素。

```xml
  <constructor-arg>
              <list>
                  <ref bean="xxx"/>
                  <ref bean="xxx"/>
               </list>
            </constructor-arg>
```

<property>元素name注入属性。

```xml
 <bean  id="cdplay"    class ="soundsystem.CDplayer">
   <property name="compactDisc"  ref="compactDisc"/>        //引用ref的ID的bean，
    </bean>             //注入compactDisc属性中

```

也可以用p-命名空间。

```xml
<bean  id="cdplay"    class ="soundsystem.CDplayer"
   p：compactDisc-ref="compactDisc"/>     
```

用value属性将字面量注入到属性中。

```xml
 <bean  id="cdplay"    class ="soundsystem.CDplayer">
                    <property name="compactDisc"  value="xxx"/>      //将xxx注入到compactDisc属性中

```

### 2.5.导入和混合配置。

只需要了解的事情就是自动装配时，它并不在意要装配的bean来自哪里。自动装配的时候会考虑到Spring容器所有的bean，

不管它是来自JavaConfig或XML中声明的还是通过组件扫描获取到的。