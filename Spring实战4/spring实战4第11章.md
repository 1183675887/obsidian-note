---
title: spring实战4第11章
date: 2020-07-08 17:34:42
tags: spring
---

## 第十一章，使用对象-关系映射持久化数据

此章讲解在Spring中集成Hibernate,Spring与java持久化API。

<!--more-->

随着应用程序变得越来越复杂，对持久化的需求也变得更复杂。我们需要将对象的属性映射到数据库的列上，并且需要自动生成语句和查询，这样我们就能从无休止的问号字符串中解脱出来。此外，我们还需要一些更复杂的特效。

延迟加载：随着我们的对象关系变得越来越复杂，有时候我们并不需要立即获取完整的对象间关系。

预先抓取：这与延迟加载是相对的。借助于预先抓取，我们可以使用一个查询获取完整的关联对象。

级联：有时，更改数据库中的表会同事修改其他表。

一些可用的框架提供了这样的服务，这些服务的通用名称是对象/关系映射(ORM)。在持久层使用ORM工具，，可以节省数千行的代码和大量的开发时间。

Spring对多个持久化框架都提供了支持，包括Hibernate,iBatis，java数据对象（JDO）以及java持久化API（JPA）。Spring对ORM框架的支持提供了与这些框架的集成点以及一些附加的服务。

1.支持集成Spring声明式事务。

2.透明的异常处理。

3.现成安全的，轻量级的模板类。

4.DAO支持类。

5.资源管理。

因为Spring对不同ORM解决方案的支持是很相似的。一旦掌握了Spring对某种ORM框架的支持后，你可以轻松地切换到另一个框架。

### 11.0.各种注解

#### 1.@PersistenceUnit

@PersistenceUnit注解会将EntityManagerFactory注入到Repository之中。

```java
 @PersistenceUnit
   private  EntityManagerFactory  emf;          //将EntityManagerFactory注入到Reposity中
```

#### 2.@Transactional

@Transactional注解表明这个Repository中持久化方法是在事务上下文中执行的。

```java
@Transactional
public  class  JpaSpitterRepository  implements    SpitterRepository{
```

#### 3.@Repository

@Repository注解，它的作用与开发Hibernate上下文Session版本的Repository时是一致的。

### 11.1.在Spring中集成Hibernate

Hibernate不仅提供了基本的对象关系映射，还提供了ORM工具所应有的所有复杂功能，比如缓存，延迟加载，预先抓取以及分布式缓存。

#### 11.1.1.声明Hibernate的Session的工厂

使用Hibrernate的主要接口是org.hibernate.Session。Session接口提供了基本的数据访问功能，如保存，更新，删除以及从数据库加载对象的功能。通过Hibernate的Session接口，应用程序的Repository能够满足所有的持久化需求。

获取Hibernate Session对象的标准方式是借助于Hibernate SessionFactory接口的实现类。SessionFactory主要负责Hibernate Session的打开，关闭和管理。

在Spring中，我们要通过Spring的某一个Hibernate Session工厂bean来获取Hibernate SessionFactory。

Spring提供了三个Session工厂bean供我们选择。

1.org.springframework.orm.hibernate3.LocalSessionFactoryBean

2.org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean

3.org.springframework.rom.hibernate4.LocalSessionFactoryBean

这些bean工厂都是Factorybean接口的实现，它们会产生一个Hibernate SessionFactory，它能够装配进任何SessionFactory类型的属性中。这样的话，就能够在应用的Spring上下文中，与其他的bean一起配置Hibernate Session工厂。

如果使用Hibernate3.2或更高版本（直接4.0，但是不包括），并且使用XML定义映射的话，那么你需要定义Spring的中的org.springframework.orm.hibernate3包中的LocalSessionFactoryBean。

```java
@Bean
public LocalSessionFactoryBean sessionFactory(DataSource dataSource){
    LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
    sfb.setDataSource(dataSource);  //Datasource属性装配了一个DataSource bean 的引用。
    sfb.setMappingResources(new String[]  {"Spitter.hbm.xml"}); //属性MappingResources列出了一个或多个的                                     //Hibernate映射文件,在这些文件中定义了应用程序的持久化策略。
    Properties props = new Properties();
    props.setProperty("dialect","org.hibernate.dialect.H2Dialect");  //配置Hibernata使用H2数据库并且要按照
                                                                //H2Dialect来构建SQL。 
    sfb.setHibernateProperties(props);  //hibernateProperties属性配置了Hibernate如何进行操作的细节。
    return sfb;
}
```

如果你更倾向于使用注解的方式来定义持久化，并且你还没用使用Hibernate4的话，那么需要使用AnnotationSessionFactorybean来代替LocalSessionFactoryBean。

```java
@Bean
public AnnotationSessionFactoryBean sessionFactory(DataSource dataSource){
 AnnotationSessionFactoryBean sfb = new AnnotationSessionFactoryBean();
       sfb.setDataSource(dataSource); 
            //PackagesToScan属性告诉SPring扫描一个或多个包以查找域类
       sfb.setPackagesToScan(new String[] {"com.habuma.spitter.domain"})
       Properties props = new Properties();
       props.setProperty("dialect","org.hibernate.dialect.H2Dialect");                                              sfb.setHibernateProperties(props);
      return sfb;
}          
```

如果使用Jibernate4的话，那么久应该使用org.springframework.rom.hibernate4中的LocalSessionFactoryBean。它有很多相同的属性

，能够支持基于XML的映射和基于注解的映射。

```java
@Bean
public LocalSessionFactoryBean sessionFactory(DataSource dataSource){
 LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
       sfb.setDataSource(dataSource); 
       sfb.setPackagesToScan(new String[] {"com.habuma.spitter.domain"})
       Properties props = new Properties();
       props.setProperty("dialect","org.hibernate.dialect.H2Dialect");                                              sfb.setHibernateProperties(props);
      return sfb;
}  
```

#### 11.1.2.构建不依赖于Spring的Hibernate代码

现在是使用上下文Session(Contextual session)。通过这种方式，会直接将Hibernate SessionFactory 装配到Repository中，并使用它来获取Session。

```java
@Autowired
public HibernateSpitterRepository(SessionFactory sessionFactory){  //注入SessionFactory
    this.sessionFactory = sessionFactory;
}
private Session currentSession(){     //从SessionFactory中获取当前事务的Session
    return sessionFactory.getCurrentSession();
}
public long count(){
    return findAll().size();
}
public Spitter save(Spitter spitter){
    Serializable id = currentSession().save(spitter);   //使用当前Session
    return new Spitter(Long) id,
             spitter.getUsername(),
             spitter.getPassword(),
             spitter.getFullName(),
             spitter.getEmail(),
             spitter.isUpdateByEmail());
}
public Spitter findOne(long id){
    return (Spitter)  currentSession().get(Spitter.class,id);
}
public Spitter findByUsername(String username){
    return (Spitter) currentSession()
             .createCriteria(Spitter.class)
             .add(Restriteria(Spitter.class)
             .list().get(0);
}
 public List<Spitter> findAll(){
     return (List<Spitter>) currentSession()
              .createCriteria(Spitter.class).list();
 }
 }

```

### 11.2.Sprin与java持久化API(JPA)

在Spring中使用JPA的第一步是要在Spring应用上下文中将实体管理器工厂按照bean的形式来进行配置。

#### 11.2.1.配置实体类管理器工厂

基于JPA的应用程序需要使用EntityManagerFactory的实现类来获取EntityManager实例。JPA定义了两种类型的实体管理器。

 1：应用程序管理类型：当应用程序向实体管理器工厂直接请求实体管理器时，工厂会创建一个实体管理器。 在这种模式下，程序要负责打开或关闭实体管理器并在事务中对其进行控制。这种方式的实例管理器适合于不运行在javaEE容器中的独立应用程序。

2：容器管理类型：实体管理器由JavaEE创建和管理。应用程序根本不与实体管理器工厂打交道。相反，实体管理器通过注入或JNDI获取。容器负责配置实体管理工厂打交道。这种类型的实体管理器  适合用于javaEE容器，在这种情况下会希望在persistence.xml指定的JPA配置之外保持一些自己对JPA的控制。

以上的两种实体管理器实现了同一个EntityManager接口，关键的区别不在于EntityManager本身，而在于它的创建和管理方式。

应用程序管理类型的EntityManager是由EntityManagerFactory创建的。而后者是通过PersistenceProvider的createEntityManagerFactory()方法得到的。

于此相对，容器管理类型的EntityManagerFactorys是通过PersistenceProvider的createContainerEntityManagerFactory()方法得到的。

不管希望使用哪种EntityManagerFactory，Spring都会负责管理EntityManagerFactory。这两种实体管理器工厂分别由对应的Spring工厂bean创建。

1.LocalEntityManagerFactoryBean生成应用程序管理类型的EntityManagerFactory;

2.LocalContainerEntityManagerFactoryBean生成容器管理类型的EntityManagerFactory。

应用程序管理类型和容器管理类型的实体管理器工厂之间唯一值得关注的区别是在Spring应用上下文中如何进行配置。

##### 配置应用程序管理类型的JPA

对于应用程序管理类型的实体管理器工厂来说，它绝大部分配置都来源于一个名为persistence.xml的配置文件。这个文件需要位于类路径下的META-INF目录下。persistence.xml的作用在于定义一个或多个持久化单元。简单的说，persistence.xml列出了一个或多个的持久化类以及一些其他的配置如数据源和基于XML的配置文件。

因为persistence.xml文件中包含了大量的配置信息，所以在Spring中需要配置的就很少了。可以通过以下的@Bean注解方法在Spring声明LocalEntityManagerFactoryBean。

```java
@bean
public   LocalEntityManagerFactoryBean    entityManagerFactoryBean(){
   LocalEntiryManagerFactoryBean  emfb = new  LocalEntityManagerFactoryBean();
       emfb.setPersistenceUnitName("spitterPU");     //PersistenceUnitName属性的值就是持久化单元的名称
           return   emfb;
                      } 
```

借助于Spring对JPA的支持，我们不再需要直接处理PersistenceProvider了。因此，再将配置信息放在persistence.xml就不好了。所以，让我们关注一下容器管理的JPA。

##### 使用容器管理类型的JPA

容器管理的JPA采取了一个不同的方式。当运行容器时，可以使用容器（如Spring）提供的信息来生成EntityManagerFactory。

可以将数据源信息配置在Spring应用上下文中，而不是在persistence.xml中。

```java
@bean
 public   LocalContainerEntityManagerFactoryBean   entityManagerFactory(
               DataSource  dataSource, JpaVendorAdapter jpaVendorAdapter)  {
       LocalContainerEntityManagerFactory  emb = new  LocalContainerEntityManagerFactory ();
       emfb.setDataSource(dataSource);      //设置dataSource属性
       emfb.setJpaVendorAdpter(jpaVendorAdapter);   //JpaVendorAdpter属性指明所使用的是哪一个厂商的JPA实现。
            return  emfb;
                       }
```

Spring提供了多个JPA厂商适配器。

1：EclipseLinkJpaVendorAdapter
2 ：HibernateJpaVendorAdapter
3：OpenJpaVendorAdapter
4:  TopLinkJpaVendorAdapter(在Spring3.1版本中，已经将其废弃了)

在这个例子中，我们使用了Hibernate作为JPA实现，所以将其配置为HibernateJpaVendorAdapter。

```java
@bean
 public  JpaVendorAdapter   jpaVendorAdapter(){
    HibernateJpaVendorAdapter   adapter  =  new  HibernateJpaVendorAdapter();
       adapter.setDatabase("HSQL");              //说明是Hypersonic数据库平台
       adapter.setShowSql(true);
       adapter.setGenerateDdl(false);
       adapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
          return  adapter;
                   }


```

选择哪一个实体管理工厂取决于如何使用它。有一个小技巧可能会让你倾向于LocalContainerEntityManagerFactoryBean。

persistence.xml文件的主要作用就在于识别持久化单元的实体类，但是我们能够在LocalContainerEntityManagerFactoryBean中直接设置packagesToScan属性。

```java
@bean
 public   LocalContainerEntityManagerFactoryBean   entityManagerFactory(
               DataSource  dataSource, JpaVendorAdapter jpaVendorAdapter)  {
       LocalContainerEntityManagerFactory  emb = new  LocalContainerEntityManagerFactory ();
       emfb.setDataSource(dataSource);      //设置dataSource属性
       emfb.setJpaVendorAdpter(jpaVendorAdapter);   //JpaVendorAdpter属性指明所使用的是哪一个厂商的JPA实现。
       emfb.setPackagesToScan("com.habuma.spittr.domain")； //设置包,会扫描这个包
            return  emfb;
                       }

```

##### 从JNDI获取实体管理器工厂

如果将Spring应用程序部署在应用服务器中，EntityManagerFactory可能以及创建好了并且位于JNDI中等待查询使用。在这种情况下，可以使用spring jee命名空间下的<jee:jndi-lookup>元素来获取对EntityManagerFactory的引用。

```xml
<jee:jndi-lookup  id="emf"   jndi-name="persistence/spitterPU" />
```

也可以用如下的bean配置来获取EntityManagerFactory。

```java
@bean
public  JndiObjectFactoryBean  entityManagerFactory(){}
     JndiObjectFactoryBean  jndiObjectFB  =  new  JndiObjectFactoryBean();
          jndiObjectFB.setJndiName("jdbc/SpitterDS");
                return  jndiObjectFB;
                      }
```

#### 11.2.2.编写基于JPA的Repository

鉴于纯粹的JPA的方式远胜与基本模板的JPA，所以我们构建不依赖与Spring的JPA Repository。

如下的JpaSpitterRepository展现了如果开发不使用Spring JpaTemplate的JPA Repository。

```java
@Repository
@Transactional
public  class  JpaSpitterRepository  implements    SpitterRepository{
   @PersistenceUnit
   private  EntityManagerFactory  emf;          //将EntityManagerFactory注入到Reposity中
   public  void  addSpitter(Spitter  spitter){     //创建并使用EntityManager
       emf.createEntityManager().persist(spitter);   }
   public  Spitter  getSpitterById(long id){
        return  emf.createEntityManager().find(Spitter.class, id); }
   public  void  saveSpitter(Spitter  spitter){
        emf.createEntityManager().merge(spitter);  }
                   ---
                  }
```

在这个例子中，唯一的问题在于每个方法都会调用createEntityManager()。这意味着每次调用Repository的方法时，都会创建一个新的EntityManager。这种复杂性来源于事务。EntityManager并不是线程安全的，一般来讲并不适合注入到像Repository这样共享的单例bean中。我们可以借助@PersistentContext注解为JpaSpitterRepository设置EntityManager。

```java
@Repository
@Transactional
public  class  JpaSpitterRepository  implements    SpitterRepository{
@PersistenceContext
private  EntityManager  em;      //给它一个EntityManager代理,注入EntityManager                   
    public  void  addSpitter(Spitter  spitter){   
    em.persist(spitter);    }      //创建并使用EntityManager
    public  Spitter  getSpitterById(long id){
            return  em.find(Spitter.class, id);       }
     public  void  saveSpitter(Spitter  spitter){
           em.merge(spitter);   }
                   ---
                  }
```

### 11.3.借助Spring Data实现自动化的JPA Repository

Spring Data JPA能够终结样板式的行为，我们不需要一遍遍地编写相同的Repository实现。Spring Data能够让我们编写Repository接口就可以了。

```java
public interface SpitterRepository
     extends JpaRepository<Spitter,Long>{
    
}
```

看上去SpitterRepository没有什么作用，但是我们所需要做的就是提出要求，让Spring Data来为我们做这件事情。为了要求Spring Data创建SpitterRepository的实现，我们需要在Spring配置中添加一个元素，如下展现了在XML配置中启动Spring Data JPA所需要添加的内容。

```xml
<beans-------->
    <jpa:reposiitories base-package="com.habuma.spitter.db"/>  指定扫描的包。
</beans>
```

<jpa:reposiitories>元素掌握了Spring Data JPA的所有魔力，就像<context:component-scan>。

<context:component-scan>会扫描包（以及子包）来查找带有@Component注解的类，而<jpa:reposiitories>会扫描它的基础包来查找扩展自Spring Data JPA Repository接口的所有接口，它会自动生成（在应用启动的时候）这个接口的实现。

如果要使用java配置的话，要在java配置类上添加@EnableJpaRepositories注解。

```java
@Configuration
@EnableJpaRepositories(basePackages="com.habuma.spittr.db")
public class JpaConfiguration{
    
}
```

SpitterRepository接口扩展自JpaRepository，而JpaRepository又扩展自Repository标记接口（虽然是间接的）。因此SpitterRepository就传递性地扩展了Repository接口，也就是Repository扫描时要查找的接口。当Spring Data找到它后，就会创建SpitterRepository的实现类，其中包含了继承自JpaRepository，PagingAndSortingRepository和CrudRepository的18个方法。

