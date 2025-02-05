---
title: MySQL必知必会21-26章
date: 2020-08-20 15:30:26
tags: MySQL
---

这些章讲解创建和操纵表，使用视图，使用存储过程，使用游标，使用触发器，管理事务处理。

<!--more-->

## 第二十一章，创建和操纵表

### 12.1.创建表

MySQL一般有两种创建表的方法。

1.使用具有交互式创建和管理表的工具(如第二章讨论的工具)。

2.表也可以直接用MySQL语句操纵。

为了用程序创建表，可使用SQL的CREATE TABLE语句。值得注意的是，在使用交互式工具时，实际上使用的是MySQL语句。但是，这些语句不是用户编写的，界面工具会自动生成并执行相应的MySQL语句(更改现有表时也是这样)。

#### 12.1.1.表创建基础

为利用CREATE TABLE创建表，必须给出下列信息。

1.新表的名字，在关键字CREATE TABLE之后给出。

2.表列的名字和定义，用逗号分隔。

CREATE TABLE语句也可能会包括其他关键字或选项，但至少要包括表的名字和列的细节。下面的MySQL语句创建本书中所用的customers表。

```mysql
输入：  CREATE TABLE customers
       (
           cust_id         int           NOT NULL AUTO_INCREMENT,
           cust_name       char(50)      NOT NULL,
           cust_city       char(50)      NULL,
           cust_state      char(5)       NULL,
           cust_zip        char(10)      NULL,
           cust_country    char(50)      NULL,
           cust_contact    char(50)      NULL,
           cust_email      char(255)     NULL,
           PRIMARY KEY(cust_id)
       )ENGINE=InnoDB;
```

分析：表名紧跟在CREATE TABLE关键字后面。实际的表定义(所有列)括在圆括号之中。各列之间用逗号分隔。这个表由9列组成。每列的定义以列名(它在表中)开始，后跟列的数据类型。表的主键可以创建表时用PRIMARY KEY关键字指定。整条语句由右括号后的分号结束。

**处理现有的表：在创建新表时，指定的表名必须不存在，否则将出错。如果要防止意外覆盖已有的表，SQL要求首先手工删除该表，然后再重建它，而不是简单的用穿那个创建表语句覆盖它。如果你仅想在一个表不存在时创建它，因你刚刚在表名后给出IF ONT EXISTS。这样做不检查已有表的模式是否与你打算创建的表模式相匹配。它只是查看表名是否存在，并且仅在表名不存在时创建它。**

#### 21.1.2.使用NULL值

在第六章说过，NULL值就是没有值或缺值。允许NULL值的列也允许在插入行时不给出该列的值。不允许NULL值的列不接受该列没有值的行，换句话说，在插入或更新行时，该列必须有值。

每个表列或者是NULL列，或者说NOT NULL列，这种状态在创建时由表的定义规定。看下面的例子。

```mysql
输入： CREATE TABLE orders
      (
          order_num       int          NOT NULL TUTO_INCREMENT,
          order_date      datetime     NOT NULL,
          cust_id         int   
          PRIMARY KEY(order_num);
      )ENGINE=InnoDB;
```

分析：这条语句创建本书所用的orders表。orders包含3个列，分别是订单号，订单日期和客户ID。所有3个列都需要值，因此每个列的定义都含有关键字NOT NULL。这将会阻止插入没有值的列。如果视图插入没有值的列，将返回错误，且插入失败。

如果列可以为空值，那么将NOT NULL改为NULL即可，或者什么都不加，因为NULL为默认设置。

#### 21.1.3.主键再介绍

主键值必须唯一。即，表中的每个行必须具有唯一的主键值。如果主键使用单个列，则它的值必须唯一。如果使用多个列，则这些列的组合值必须唯一。

迄今为止我们看到的例子都是用单个列作为主键。其中主键用以下的类似的语句定义。

PRIMARY KEY(order_id)

为创建由多个列组成的主键，应该以逗号分隔的列表给出各列名，如下所示：

```mysql
输入：   CREATE TABLE orderitems
       (
         order_num        int                NOT NULL,
         order_item       int                NOT NULL,
         prod_id          char(10)           NOT NULL,
         quantity         int                NOT NULL,
         item_price       decimal(8,2)       NOT NULL,
         PRIMARY KEY(order_num, order_item)
        )ENGINE=InnoDB;
```

分析：orderitems表包含orders表中每个订单的细节。每个订单有多项物品，但每个订单任何时候都只有1个第一项物品，1个第二项物品，如此等等。因此订单号(order_num列)和订单物品(order_item列)的组合是唯一的，从而适合作为主键，其定义为：

PRIMARY KEY(order_num, order_item)

主键可以在创建表时定义，或者在创建表后定义。

#### 21.1.4.使用VUTO_INCREMENT

请看下面的代码行(用来创建customers表的CREATE TABLE语句的组成部分)：

```mysql
 cust_id         int           NOT NULL AUTO_INCREMENT,
```

AUTO_INCREMENT告诉MySQL,本列每当增加一行时自动增量。每次执行一个INSERT操作时，MySQL自动对该列增量，给该列赋予一个可用的值。这样给每个行分配一个唯一的cust_id，从而可用用作主键值。

每个表只允许一个AUTO_INCREMENT列，而且它必须被索引(如，通过它成为主键)。

确定AUTO_INCREMENT的值：可使用last_insert_id()函数获得这个值。如下所示：

```mysql
SELECT last_insert_id()
```

此语句返回最后一个AUTO_INCREMENT值，然后可以将它用于后续的MySQL语句。

#### 21.1.5.指定默认值

如果在插入行时没有给出值，MySQL允许指定此时使用的默认值。默认值用CREATE TABLE语句的列定义的DEFAULT关键字指定。

```mysql
输入：   CREATE TABLE orderitems
       (
         order_num        int                NOT NULL,
         order_item       int                NOT NULL,
         prod_id          char(10)           NOT NULL,
         quantity         int                NOT NULL    DEFAULT 1,
         item_price       decimal(8,2)       NOT NULL,
         PRIMARY KEY(order_num, order_item)
        )ENGINE=InnoDB;
```

分析：这条语句创建包含组成订单的各物品的orderitems表(订单本身存储在oeders表中)。quantity列包含订单中每项物品的数量。在此例子中，给该列的描述添加文本DEFAULT 1指示MySQL,在未给出数量的情况下使用数量1,。

**不允许函数：与大多数DBMS不一样，MySQL不允许使用函数作为默认值，它只支持常量。**

**使用默认值而不是NULL值：许多数据库开发人员使用默认值而不是NULL列，特别是对用于计算或数据分组的列更是如此。**

#### 21.1.6.引擎类型

MySQL有一个具体管理和处理数据的内部引擎。在你使用CREATE TABLE语句时，该引擎具体创建表，而你在使用SELECT语句或进行其他数据库处理时，该引擎在内部处理你的请求。多数时候，此引擎都隐藏在DBMS内，不需要过多的关注它。

但MySQL与其他DBMS不一样，它具有多种引擎。它打包多个引擎，这些引擎都隐藏在MySQL服务器内，全部能执行CREATE TABLE和SELECT等命令。

如果忽略ENGINE=语句，则使用默认引擎(很可能是MyISAM)，多数SQL语句都会默认使用它。以下是几个需要知道的引擎。

1.InnoDB是一个可靠的事务处理引擎，它不支持全文本搜索。

2.MEMORY在功能等同于MyISAM，但由于数据存储在内存(不是磁盘)中，速度很快(特别适合于临时表)；

3.MyISAM是一个性能极高的引擎，它支持全文本搜索，但不支持事务处理。

### 21.2.更新表

为更新表定义，可使用ALTER TABLE语句。但是，在理想状态下，当表中存储数据以后，该表就不应该再被更新。在表的设计过程中需要花费大量时间来考虑，以便后期不对该表进行大的改动。

为了使用ALTER TABLE更改表结构，必须给出下面的信息。

1.在ALTER TABLE之后给出要更改的表名(该表必须存在，否则将出错)。

2.所做更改的列表。

下面的例子给表添加一个列。

```mysql
输出： ALTER TABLE vendors
      ADD vend_phone CHAR(20);
```

分析：这条语句给vendors表增加一个名为vend_phone的列，必须明确其数据类型。

删除刚刚添加的列，可以这样做。

```mysql
输入： ALTER TABLE vendors
      DROP COLUMN vend_phone;
```

ALTER TABLE的一种常见用途是定义外键。下面是用来定义本书中的表所用的外键的代码。

```mysql
ALTER TABLE orderitems
ADD CONSTRAINT fk_orderitems_orders (order_num);
FOREIGN KEY (order_num) REFERENCES orders (order_num);

ALTER TABLE orderitems
ADD CONSTRAINT fk_orderitems_products  DOREIGN KEY (prod_id)
REFERENCES products (prod_id);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_customers FOREIGN KEY (cust_id)
REFERENCES customers (cust_id);

ALTER TABLE products
ADD CONSTRAINT fk_products_vendors
FOREIGN KEY (vend_id) REFERENCES vendors (vend_id);

```

这里，由于要更改4个不同的表，使用了4条ALTER TABLE语句。为了对单个表进行多个更改，可以使用单条ALTER TABLE 语句，每个更改用逗号分隔。

复杂的表结构更改一般需要手动删除过程，它涉及以下步骤。

1.用新的列布局创建一个新表。

2.使用INSERT SELECT语句从旧表复制数据到新表。如果有必要，可使用转换函数和计算字段。

3.检验包含所需数据的新表。

4.重命名旧表(如果确定，可以删除它)。

5.用旧表原来的名字重命名新表。

6.根据需要，重新创建触发器，存储过程，索引和外键。

**小心使用ALTER TABLE ：使用ALTER TABLE要极为小心，应该在进行改动前做一个完整的备份(模式和数据的备份)。数据库表的更改不能撤销，如果增加了不需要的列，可能不能删除它们，类似地，如果删除了不应该删除的列，可能会丢失该列中的所有数据。**

### 21.3.删除表

删除表使用DBOP TABLE语句即可。

```mysql
输入： DROP TABLE customers2;
```

分析：这条语句删除customers2表。删除表没有确定，也不能撤销，执行这条语句将永远删除该表。

### 21.4.重命名表

使用RENAME TABLE语句可以重命名一个表。

```mysql
输入： RENAME TABLE customers2 TO customers;
```

分析：RENAME TABLE所做的仅是重命名一个表。可以使用下面的语句对多个表重命名。

```mysql
RENAME TABLE back_customers TO customers,
             back_vendors TO vendors,
             back_products TO products;
```

## 第二十二章，使用视图

MySQL5添加了对视图的支持。因此，本章内容适用于MySQL5及以后的版本。

视图是虚拟的表。与包含数据的表不一样，视图只包含使用时动态检索数据的查询。

理解视图的最好办法是看一个例子，第十五章中用下面的SELECT语句从3个表中检索数据。

```mysql
输入：  SELECT  cust_name, cust_contact
       FROM   customers, orders, orderitems
       WHERE  customers.cust_id = orders.cust_id
       AND    orderitems.order_num = orders.order_num
       AND    prod_id = 'TNT2';
```

此查询用来检索订购了某个特定产品的客户。任何需要这个数据的人都必须理解相关表的结构，并且知道如何创建查询和对表进行联结。

为了检索其他产品(或多个产品)的相同数据，必须修改最后的WHERE子句。现在，假如可以把整个查询包装成一个名为productcustomers的虚拟表，则可以如下轻松地检索出相同的数据。

```mysql
输入：  SELECT  cust_name, cust_contact
       FROM   productcustomers
       WHERE prod_id = 'TNT2';
```

这就是视图的作用。productcustomers是一个视图，作为视图，它不包含表中应该有的任何列或数据，它包含的是一个SQL查询(与上面用以正确联结表的相同的查询)。

#### 22.1.1.为什么使用视图

下面是视图的一些常见应用。

1.重用SQL语句。

2.简化复杂的SQL操作。在编写查询后，可以方便地重用它而不必知道它的基本查询细节。

3.使用表的组成部分而不是整个表。

4.保护数据。可以给用户授予表的特定部分的访问权限而不是整个表的访问权限。

5.更改数据格式和表示。视图可返回与底层表的表示和格式不同的数据。

在视图创建之后，可以用与表基本相同的方式利用它们。可以对视图执行SELECT操作，过滤和排序数据，将视图联结到其他视图或表，甚至能添加和更新数据。重要的是指定视图仅仅是用来查看存储在别处的数据的一种设施。视图本身不包含数据，因此它们返回的数据是从其他表中检索出来的。在添加或更改这些表中的数据时，视图将返回改变过的数据。

#### 22.1.2.视图的规则和限制

下面是视图创建和使用的一些常见的规则和限制。

1.与表一样，视图必须唯一命名(不能给视图取与别的视图或表相同的名字)。

2.对于可以创建的视图数目没有限制。

3.为了创建视图，必须具有足够的访问权限。这些限制通常由数据库管理人员授予。

4.视图可以嵌套，即可以利用从其他视图中检索数据的查询来构造一个视图。

5.ORDER BY可以用在视图中，但如果从该视图检索数据的SELECT语句中也含有ORDER BY，那么该视图的ORDER BY将被覆盖。

6.视图不能索引，也不能有关联的触发器或默认值。

7.视图可以和表一起使用。例如，编写一条联结表和视图的SELECT语句。

### 22.2.使用视图

在理解什么是视图后，我们来看一下视图的创建。

1.视图用CREATE VIEW语句来创建。

2.使用SHOW CREATE VIEW viewname;来查看创建视图的语句。

3.用DROP删除视图，其语法为DROP VIEW viewname;

4.更新视图时，可以先用DROP再用CREATE,也可以直接用CREATE OR REPLACE VIEW。如果要更新的视图不存在，则第二条语句会创建一个视图。如果要更新的视图存在，则第二条更新语句会替换原有视图。

#### 22.2.1.利用视图简化复杂的联结

视图最常见的应用之一是隐藏复杂的SQL，这通常都会涉及联结。请看下面的例子。

```mysql
输入：     CREATE VIEW productcustomers AS
          SELECT cust_name, cust_contact, prod_id
          FROM  customers, orders, orderitems
          WHERE customers.cust_id = orders.cust_id
          AND   orderitems.order_num = orders.order_num
```

分析：这条语句创建一个名为productcustomers的视图，它联结三个表，以返回已订购了任意产品的所有客户的列表。如果执行SELECT * FROM productcustomers,将列出订购了任意产品的客户。

可以出，视图极大地简化了复杂SQL语句的使用。利用视图，可一次性编写基础的SQL。然后根据需要多次利用。

**创建可重用的视图：创建不受特定数据限制的视图是一种好办法。**

#### 22.2.2.用视图重新格式化检索出的数据

视图的另一常见用途是重新格式化检索出的数据。下面的SELECT语句在打那个组合计算列中返回供应商名和位置。

```mysql
输入： SELECT Concat(RTrim(vend_name), '(', RTrim(vend_country, ')')
      FROM  vendors
      ORDER BY vend_name;
```

现在，假如经常需要这个格式的结果。不必在每次需要时执行联结，创建一个视图，每次需要时使用它即可。为把词语句转换为视图，可按如下进行。

```mysql
输入： CREATE VIEW vendorlocations AS
      SELECT Concat(RTrim(vend_name), '(', RTrim(vend_country, ')')
      FROM  vendors
      ORDER BY vend_name;
```

分析：这条语句使用之前的SELECT语句相同的查询创建视图。为了检索出以创建所有邮件标签的数据，可如下进行。

```mysql
输入：  SELECT *
       FROM vendorlocations;
```

#### 22.2.3.用视图过滤不想要的数据

视图对于应用普通的SELECT子句也很有用。例如，可以定义customeremaillist视图，它过滤没有电子邮件地址的客户。为此目的，可使用下面的语句。

```mysql
输入： CREATE VIEW customeremaillist AS
      SELECT cust_id, cust_name, cust_eamil
      FROM customers
      WHERE cust_email IS NOT NULL;
```

现在，可以使用其他表一样使用视图customeremaillist。

```mysql
输入：  SELECT *
       FROM customeremaillist;
```

#### 22.2.4.使用视图与计算字段

视图对于简化字段的使用特别有用。方法也是先建视图再使用。

##### 22.2.5.更新视图

通常，视图是可更新的(即，可以对它们使用INSERT， UPDATE,和DELETE)。更新一个视图将更新其基表(可以回忆一下，视图本身没有数据)。如果你对视图增加或删除行，实际上是对其基表或删除行。但是如果MySQL不能正确地确定被更新的基数据，则不允许更新(包括插入和删除)。这实际意味着，如果视图定义中有以下操作，则不能进行视图的更新。

1.分组(使用GROUP BY和HAVING)。

2.联结。3.子查询。4.并。5.聚集函数。6.DISTINCT。7.导出(计算)列。

将视图用于检索：一般，应该将视图用于检索(SELECT语句)而不用于更新(INSERT,UPDATE和DELETE)。

## 第二十三章，使用存储过程

### 23.1.存储过程

存储过程简单来说，就是为以后的使用而保存的一条或多条MySQL语句的集合。可将其视为批文件，虽然它们的作用不仅限于批处理。

### 23.2.为什么要使用存储过程

下面列出一些主要的理由：

1.通过把处理封装在容易使用的单元中，简化复杂的操作。

2.由于不要求反复建立一系列处理步骤，这保证了数据的完整性。如果所有开发人员和应用程序都使用同一(实验和测试)存储过程，则所使用的代码都是相同的。这一点的延迟就是防止错误。需要执行的步骤越多，出错的可能性就越大。防止错误保证了数据的一致性。

3.简化对变动的管理。如果表名，列名或业务逻辑(或别的内容)有变化，只需要更改存储过程的代码。使用它的人员甚至不需要知道这些变化。

这一点延伸的就是安全性。通过存储过程限制对基础数据的访问减少了数据错误的机会。

1.提高性能。因为使用存储过程比使用单独的SQL语句要快。

2.存在一些只能用在单个请求中的MySQL元素和特性，存储过程可以使用它们来编写功能更强更灵活的代码。

换句话说，使用存储过程有3个主要的好处，即简单，安全，高性能。显然，它们都很重要。不过，在将SQL代码转换为存储过程前，也必须知道它的一些缺陷。

1.一般来说，存储过程的编写比基本SQL语句复杂，编写存储过程需要更多的技能，更丰富的经验。

2.你可能没有创建存储过程的安全访问权限。许多数据库管理员限制存储过程的创建权限，允许用户使用存储过程，但不允许他们创建存储过程。

### 23.3.使用存储过程

使用存储过程需要知道如何执行它们。存储过程的执行远比其定义更容易遇到，因此，我们将从执行存储过程开始介绍。然后再创建和使用存储过程。

#### 23.3.1.执行存储过程

MySQL称存储过程的执行调用，因此MySQL执行存储过程的语句为CALL。CALL接受存储过程的名字以及需要传递给它的任意参数。看如下例子：

```mysql
输入：  CALL productpricing(@pricelow,
                           @pricehigh,
                           @priceaverage);
```

分析：其中，执行名为productpricing的存储过程，它计算并返回产品的最低，最高和平均价格。

#### 23.2.创建存储过程

看一个例子，一个返回产品平均价格的存储过程。以下是代码：

```mysql
输入：   CREATE PROCEDURE productpricing()
        BEGIN
           SELECT Avg(prod_price) AS priceaverage
           FROM products;
        END;
```

分析：此存储过程名为productpricing，用CREATE PROCEDURE productpricing()语句定义。如果存储过程接受参数，它们将在()中列举出来。此存储过程没有参数，但后跟的()仍然需要。BEGIN和END语句用来限定存储过程体，过程体本身仅是一个简单的SELECT语句。

在MySQL处理这段代码时，它创建一个新的存储过程productpricing。没有返回数据，因为这段代码并未调用存储过程，这里只是为以后使用创建它。

那么，如何使用这个存储过程？如下所示：

```mysql
输入：   CALL productpricing()
```

输出：

| priceaverage |
| ------------ |
| 16.133571    |

分析：CALL productpricing()；执行刚创建的存储过程并显示返回的结果。因为存储过程实际上是一种函数，所有存储过程后需要有()符号。

#### 23.3.删除存储过程

存储过程在创建之后，被保存在服务器上以供使用，直至被删除。为删除刚创建的存储过程，可使用以下语句。

```mysql
输入：   DROP PROCEDURE productpricing;
```

分析：这条语句删除刚创建的存储过程。请注意没有使用后面的()，只给出存储过程名。

**仅当存在时删除：如果指定的过程不存在，则DROP PROCEDURE将产生一个错误。当过程存在想删除它时(如果过程不存在也不产生错误)可使用DROP PROCEDURE IF EXISTS。**

#### 23.3.4.使用参数

一般，存储过程并不显示结果，而是把结果返回给你指定的变量。

**变量：内存中一个特定的位置，用来临时存储数据。**

以下是producpricing的修改版本(如果不先删除此存储过程，则不能再次创建它)。

```mysql
输入：    CREATE PROCEDURE productpricing(
           OUT p1 DECIMAL(8,2),
           OUT ph DECIMAL(8,2),
           OUT pa DECIMAL(8,2)
          )
          BEGIN
              SELECT  Min(prod_price)
              INTO p1
              FROM products;
              SELECT Max(prod_price)
              INTO ph
              FROM products;
              SELECT Ava(prod_price)
              INTO pa
              FROM products;
          END;
```

分析：此存储过程接受3个参数：p1存储产品最低价格，ph存储产品最高价值，pa存储产品平均价格。每个参数必须具有指定的类型，这里使用十进制值。关键字OUT指出相应的参数用来从存储过程传出一个值(返回给调用者)。MySQL支持IN(传递给存储过程)，OUT(从存储过程传出)和INOUT(对存储过程传入和传出)类型的参数。存储过程的代码位于BEGIN和END语句内，如前所见，它们是一系列SELECT语句，用来检索值，然后保存到相应的变量(通过指定INTO关键字)。

为调用此修改过的存储过程，必须指定3个变量名，如下所示。

```mysql
输入：  CALL productpricing(@pricelow,
                           @pricehigh,
                           @priceaverage);
```

分析：由于存存储过程要求3个参数，因此必须正好传递3个参数，不多也不少。所以，这条CALL语句给出3个参数。它们是存储过程将保存结果3个变量的名字。

**变量名：所有MySQL变量都必须以@开始。**

在调用时，这条语句并不显示任何数据。它返回以后可以显示(或在其他处理中使用)的变量。

为了显示检索出的产品平均价格，可如下进行。

```mysql
输入：  SELECT @Priceaverage;
```

输出：

| @Priceaverage |
| ------------- |
| 16.155        |

下面是另一个例子，这次使用IN和OUT参数。ordertotal接受订单号并返回该订单的合计。

```mysql
输入：  CREATE PROCEDURE ordertotal(
         IN onumber INT,
         OUT ototal DECIMAL(8,2)
       )
       BEGIN
          SELECT Sum(item_price*quantity)
          FROM orderitems
          WHERE order_num = onumber
          INTO ototal;
       END;
```

分析：onumber定义为IN，因为订单号被传入存储过程。ototal定义为OUT，是因为要从存储过程返回合计。SELECT语句使用这两个参数，WHERE子句使用onumber选择正确的行，INTO使用ototal存储计算出来的合计。

##### 23.3.5.建立智能存储过程

只有在存储过程内包含业务规则和智能处理时，它们的威力才真正显现出来。

考虑这个场景。你需要获得与以前一样的订单合计，但需要对合计增加营业税，不过只针对某些顾客(或许是你所在州中那些顾客)。那么，你需要做下面几件事情。

1.获得合计。2.把营业税有条件地添加到合计。3.返回合计(带或不带税)。

存储过程的完整工作如下。

```mysql
输入：           CREATE PROCEDURE ordertotal(
                   IN onumber INT,
                   IN taxable BOOLEAN,
                   OUT ototal DECIMAL(8,2)    
               )COMMENT 'Obtain order total,optionally adding tax'
                BEGIN
               
                   DECLARE total DECIMAL(8,2);
               
                   DECLARE taxrate INT DEFAULT 6;
               
                   SELECT Sum(item_price*quantity)
                   FROM orderitems
                   WHERE order_num = onumber
                   INTO total;
               
               IF taxable THEN
               
                  SELECT total+(total/100*taxrate) INFO total;
               END IF;
                  
                  SELECT total INTO ototal;
               
               END;
```

分析：此存储过程有很大的变动。首先，增加了注释。在存储过程复杂性增加时，这样做特别重要。添加了另外一个参数taxable，它是一个布尔值(如果要增加税为真，否则为假)。在存储过程体中，用DECLARE语句定义了两个局部变量。DECLARE要求指定变量名和数据类型，它也支持可选的默认值(这个例子中的taxrate被默认设置为6%)。SELECT语句以及改变，因此其结果存储到total(局部变量)而不是ototal。IF语句检查taxable是否为真。如果为真，则用另一SELECT语句增加营业税到局部变量total。最后，用另一SELECT语句将total(它增加或许不增加营业税)保存到ototal。

这显然是一个更高级，功能更强的存储过程。可以用以下例子来实验：

```mysql
输入：  CALL ordertotal(20005, 0, @total);
       SELECT @total;
```

| total  |
| ------ |
| 149.87 |



#### 23.3.6.检查存储过程

为显示用来创建一个存储过程的CREATE语句，使用SHOW CREATE PROCEDURE语句。

```
输入：   SHOW CREATE PROCEDURE ordertotal;
```

为了获得包括何时，由谁创建等详细信息的存储过程列表，使用SHOW PROCEDURE STATUS.

限制过程状态结果：SHOW CREATE STATUS列出所有存储过程。为限制其输出，可使用LIKE指定一个过滤模式，例如：

SHOW PROCEDURE STATUS LIKE 'ordertotal';

## 第二十四章，使用游标

### 24.1.游标

有时，需要在检索出来的行中前进或后退一行。这就是使用游标的原因。游标是一个存储在MySQL服务器上的数据库查询，它不是一条SELECT语句，而是被该语句检索出来的结果集。在存储了游标之后，应用程序可以根据需要滚动或浏览其中的数据。

游标主要用于交互式应用，其中用户需要滚动屏幕上的数据，并对数据进行浏览或作做出更改。

**只能用于存储过程：MySQL游标只能用于存储过程(和函数)。**

### 24.2.使用游标

使用游标设计几个明确的步骤。

1.在能使用游标前，必须声明(定义)它。这个过程实际上没有检索数据，它只是定义要使用的SELECT语句。

2.一旦声明后，必须打开游标以供使用。这个过程用前面定义的SELECT语句把数据实际检索出来。

3.对于填有数据的游标，根据需要取出(检索)各行。

4.在结束游标使用时，必须关闭游标。

在声明游标后，可根据需要频繁地打开和关闭游标。在游标打开后，可根据需要频繁地执行取操作。

#### 24.2.1.创建游标

游标用DECLARE语句创建。DECLARE命名游标，并定义相关的SELECT语句，根据需要带WHERE和其他子句。例如，下面的语句定义了名为ordernumbers的游标，使用了可以检索所有订单的SELECT语句。

```mysql
输入：  CREATE PROCEDURE processorders()
       BEGIN
          DECLARE ordernumbers CURSOR
          FOR
          SELECT order_num FROM orders;
       END;
```

分析：这个存储过程并没有做很多事情，DELECT语句用来定义和命令游标，这里为ordernumbers。存储过程处理完成后，游标就消失(因为它局限于存储过程)。

在定义游标之后，可以打开它。

#### 24.2.2.打开和关闭游标

游标用OPEN CURSOR语句来打开。

```mysql
输入：  OPEN ordernumbers;
```

分析： 在处理OPEN语句是执行查询，存储检索出的数据以供浏览和滚动。

游标处理完成后，应当使用如下语句关闭游标。

```mysql
输入：   CLOSE ordernumbers;
```

分析：CLOSE释放游标使用的所有内部内存和资源，因此在每个游标不再需要时都应该关闭。

在一个游标关闭后，如果没有重新打开，则不能使用它。但是，使用声明过的游标不需要再次声明，用OPEN语句打开它就可以了。

**隐含关闭：如果你不明确关闭游标，MySQL将会在到达END语句时自动关闭它。**

下面是前面例子的修改版本。

```mysql
输入：  CREATE PROCEDURE processorders()
       BEGIN
          DECLARE ordernumbers CURSOR
          FOR
          SELECT order_num FROM orders;
           
          OPEN ordernumbers;
          CLOSE ordernumbers;
      END;
```

分析：这个存储过程声明，打开和关闭一个游标。但对检索出的数据什么也没做。

#### 24.2.3.使用游标数据

在一个游标被打开后，可以使用FETCH语句分别访问它的每一行。FETCH指定检索什么数据(所需的列)，检索出来的数据存储在什么地方。它还向前移动游标中的内部行指针。使下一条FETCH语句检索下一行(不重复读取同一行)。

第一个例子从游标中检索单个行(第一行)：

```mysql
输入：   CREATE PROCEDURE processorders()
        BEGIN
           
           DECLARE o INT;
           
           DECLARE ordernumbers CURSOR
           FOR
           SELECT order_num FROM orders;
           
           OPEN ordernumbers;
           
           FETCH ordernumbers INTO o;
           
           CLOSE ordernumbers;
        
        END;
```

分析：其中FETCH用来检索当前行的order_num列(将自动从第一行开始)到一个名为o的局部变量的声明中。对检索出的数据不做任何处理。

在下一个例子中，循环检索数据，从第一行到最后一行：

```mysql
输入：   CREATE PROCEDURE processorders()
        BEGIN
        
           DECLARE done BOOLEAN DEFAULT 0;
           DECLARE o INT;
           
           DECLARE ordernumbers CURSOR
           FOR
           SELECT order_num FROM orders;
           
           DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1;
           
           OPEN ordernumbers;
        
        REPEAT
          
           FETCH ordernumbers INTO o;
          
           UNTIL done END REPEAT;
          
           CLOSE ordernumbers;
        
        END;
```

分析：这个例子使用FETCH检错当前order_num到声明的名为o的变量中。但和前一个例子不一样的是，这个例子中的FETCH是在REPEAT内，因此它反复执行直到done为真(由UNTIL done END REPEAT;规定)。为使它起作用，用一个DEFAULT 0(假，不结束)定义变量done。那么，done怎样才能在结束时被设置为真呢？答案是用以下语句：

​     DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1;

这条语句定义了一个CONTINUE HANDLER，它是在条件出现时被执行的代码。这里，它指出当SQLSTATE '02000'出现时，SET done=1。SQLSTATE '02000'是一个未找到条件，当REPEAT由于没有更多的行供循环而不能继续时，出现这个条件。

**DECLARE语句的次序：DECLARE语句的发布存在特定的次序，用DECLARE语句定义的局部变量必须在定义任何游标或句柄之前定义，而句柄必须在游标之后定义。不遵循此顺序将产生错误消息。**    

如果调用这个存储过程，它将定义几个表量和一个CONTINUE HANDLER，定义并打开一个游标，重复读取所有行，然后关闭游标。

如果一切正常，你可以在循环内放入任意需要的处理(在FETCH语句之后，循环结束之前)。

为了把这些内容组织起来，下面给出我们的游标存储过程样例的更一步修改的版本，这次对取出的数据进行某种实际的处理。

```mysql
输入：   CREATE PROCEDURE processorders()
        BEGIN
        
           DECLARE done BOOLEAN DEFAULT 0;
           DECLARE o INT;
           DECLARE t DECIMAL(8,2);
           
           DECLARE ordernumbers CURSOR
           FOR
           SELECT order_num FROM orders;
           
           DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1;
           
           CREATE TABLE IF NOT EXISTS ordertotals
             (order_num INT,total DECIMAL(8,2));
             
           OPEN ordernumbers;
        
        REPEAT
          
           FETCH ordernumbers INTO o;
          
           CALL ordertotal(o,1,t);
          
           INSERT TINTO ordertotals(order_num, total)
           VALUES(o, t);
           
           UNTIL done END REPEAT;
          
           CLOSE ordernumbers;
        
        END;
```

分析：在这个例子中，我们增加了另一个名为t的变量(存储每个订单的合计)。此存储过程还在允许中创建一个新表(如果它不存在的话)，名为ordertotals。这个表将保存存储过程生成的结果。FETCH像以前一样取每个order_num，然后用CALL执行另一个存储过程(我们在前一章中创建)来计算每个订单的带税的合计(结果存储到t)。最后，用INSERT保存每个订单的订单号和合计。

此存储过程不返回数据，但它能够创建和填充另一个表，可以用一条简单的SELECT语句查看该表：

```mysql
输入：  SELECT *
       FROM ordertotals;
```

| order_num | total   |
| --------- | ------- |
| 20005     | 1588.86 |
| 20006     | 58.30   |

## 第二十五章，使用触发器

### 25.1.触发器

如果你想要某条语句(或某些语句)在事件发生时自动执行，怎么办呢？例如：

1.每当增加一个顾客到某个数据表时，都检查其电话号码格式是否正确，州的缩写是否为大小写。

2.每当订购一个产品时，都从库存数量中减去订购的数量。

3.无论何时删除一行，都在某个存档表中保留一个副本。

所有这些例子的共通之处是它们都需要在某个表发生更改时自动处理。这确切地说就是触发器。触发器是MySQL响应一下任意语句而自动执行的一条MySQL语句(或位于BEGIN和END语句之间的一组语句)。

1.DELECT。2.INSERT。3.UPDATE。

其他MySQL语句不支持触发器。

### 25.2.创建触发器

创建触发器时，需要给出4条信息。

1.唯一的触发器名。2.触发器关联的表。

3.触发器应该响应的活动(如DELETE,INSERT或UPDATE)；

4.触发器何时执行(处理之前或之后)。

**保持每个数据库的触发器唯一：在MySQL5中,触发器名必须在每个表中唯一，但不是在每个数据库中唯一。这表示同个数据库的两个表可具有相同名字的触发器。这在其他每个数据库触发器名必须唯一的DBMS中是不允许的，而且以后的MySQL版本很可能会使命名规则更为严格。因此，现在最好是在数据库范围内使用的触发器名。**

触发器用CREATE TRIGGER语句创建。下面是一个简单的例子。

```mysql
输入：   CREATE TRIGGER newproduct AFTER INSERT ON products
        FOR EACH ROW SELECT 'product added'
```

分析：CREATE TRIGGER用来创建名为newproduct的新触发器。触发器可在一个操作发生之前或之后执行，这里给出了AFTER INSERT，所以此触发器将在INSERT语句成功执行后执行。这个触发器还指定FOR EACH ROW，因此代码对每个插入和执行。在这个例子中，文本product added将对每个插入的行显示一次。

为了测试这个触发器，使用INSERT语句添加一行多多个列到products中，你将看到对每个成功的插入，显示Product added消息。

**仅支持表：只有表才支持触发器，视图不支持(临时表也不支持)。**

触发器按每个表每个事件每次地定义，每个表每个事件每次只允许一个触发器。因此，每个表最多支持6个触发器(每条INSERT,UPDATE,DELETE的之前和之后)。单一触发器不能与多个事件或多个表关联，所以，如果你需要一个对INSERT和UPDATE操纵执行的触发器，则应该定义两个触发器。

**触发器失败：如果BEFORE触发器失败，则MySQL将不执行请求的操作。此外，如果BEFORE触发器或语句本身失败，MySQL将不执行AFTER触发器(如果有的话)。**

### 25.3.删除触发器

为了删除一个触发器，可使用DROP TRIGGER语句，如下所示：

```mysql
输入：  DROP TRIGGER newproduct;
```

分析：触发器不能更新或覆盖。为了修改一个触发器，必须先删除它，然后再重新创建。

### 25.4.使用触发器

#### 25.4.1.INSERT触发器

INSERT触发器在INSERT语句执行之前或之后进行。需要知道以下几点：

1.在INSERT触发器代码内，可引用一个名为NEW的虚拟表，访问被插入的行。

2.在BEFORE INSERT触发器中，NEW中的值也可以被更新(允许更改被插入的值)。

3.对于AUTO_INCREMENT列，NEW在INSERT执行之前包含0，在INSERT执行之后包含新的自动生成值。

下面举一个例子。AUTO_INCREMENT列具有MySQL自动赋予的值。

```mysql
输入：   CREATE TRIGGER neworder AFTER INSERT ON orders
        FOR EACH ROW SELECT NEW.order_num;
```

分析：此代码创建了一个名为neworder的触发器，它按照AFTER INSERT ON orders执行。在插入一个新订单到orders表时，MySQL生成一个新订单号并保持到order_num中。触发器从NEW.order_num取得这个值并返回它。此触发器必须按照AFTER INSERT执行，因为在BEFORE INSERT语句执行之前，新order_num还没有生成。对于orders的每次插入使用这个触发器总是返回新的订单号。

为测试这个触发器，试着插入一个新行，如下所示。

```mysql
输入：   INSERT INTO orders(order_date, cust_id)
        VALUES(Now(), 10001);
```

| order_num |
| --------- |
| 20010     |

分析：orders包含3个列。order_date, cust_id必须给出，order_num由MySQL自动生成，而现在order_num还自动被返回。

**BEFORE或AFTER？：通常，将BDFORE用于数据验证和精华(目的是保证插入表中的数据确实是需要的数据)。本提示也适用于UPDATE触发器。**

#### 25.4.2.DELETE触发器

DELECT触发器在DELETE语句执行之前或之后执行。需要知道以下两点：

1.在DELETE触发器代码内，你可以引用一个名为OLD的虚拟表，访问被删除的行。

2.OLD中的值全都是只读的，不能更新。

下面的例子演示使用OLD保存将要被删除的行到一个存档表中：

```mysql
输入：  CREATE TRIGGER deleteorder BEFORE DELETE ON orders
       FOR EACH ROW 
       BEGIN
           INSERT INTO archive_orders(order_num, order_date, cust_id)
           VALUES(OLD.order_NUM, OLD.order_date, LOD.cust_id);
       END;
```

分析：在任意订单被删除前执行此触发器。它使用一条INSERT语句将OLD中的值(要被删除的订单)保存到一个名为archive_orders的存档表中(为实际使用这个例子，你需要用与orders相同的列创建一个名为archive_orders的表)。

使用BEFORE DELCTE触发器的优点(相当于AFTER DELETE触发器来说)为，如果由于某种原因，订单不能存档，DELETE本身将被放弃。

多语句触发器：正如所见，触发器deleteoder使用BEGIN和END语句标记触发器体。这在此例子中并不是必需的，不过也没有害处。使用BEGIN END块的好处是触发器能容纳多条SQL语句(在BEGIN END块中一条挨着一条)。

#### 25.4.3.UPDATE触发器

UPDATE触发器在UPDATE语句执行之前或之后执行。需要知道以下几点。

1.在UPDATE触发器代码中，你可以引用一个名为OLD的虚拟表访问以前(UPDATE语句前)的值，引用一个名为NEW的虚拟表访问新更新的值。

2.在BEFORE UPDATE触发器中，NEW中的值可能也被更新(允许更改将要用于UPDATE语句语句中的值)。

3.OLD中的值全都是只读的，不能更新。

下面的例子保证州名缩写总是大写(不管UPDATE语句中给出的是大写还是小写)：

```mysql
输入：  CREATE TRIGGER updatevendor BEFORE ON vendors
       FOR EACH ROW SET NEW.vend_state = Upper(NEW.vend_stata);
```

分析：显然，任何数据精华都需要在UPDATE语句之前进行，就像这个例子中一样。每次更新一个行时，NEW.vend_state的值(将用来更新表行的值)都用Upper(NEW.vend_stata)替换。

#### 25.4.4.关于触发器的进一步介绍

1.与其他DBMS相比，MySQL5中支持的触发器相当初级。为了的MySQL版本中有一些改进和增强触发器支持的计划。

2.创建触发器可能需要特殊的安全访问权限，但是，触发器的执行是自动的。如果INSERT，UPDATE或DELETE语句能够执行，则相关的触发器也能执行。

3.应该用触发器来保证数据的一致性(大小写，格式等)。在触发器中执行这种类型的处理的优点是它总是进行这种处理，而且是透明地进行，与客户机应用无关。

4.触发器的一种非常有意义的使用是创建审计跟踪。使用触发器，把更改(如果需要，甚至还有之前和之后的状态)记录到另一个表非常容易。

5.遗憾的是，MySQL触发器中不支持CALL语句。这表示不能从触发器内调用存储过程。所需的存储过程代码需要复制到触发器内。

## 第二十六章，管理事务处理

### 26.1.事务处理

**并非所有引擎都支持事务处理：MyISAM和InnoDB是两种最常使用的引擎。前者不支持明确的事务处理管理，而后者支持。这就是为什么本书中使用的样例被创建来使用InnoDB而不是经常使用的MyISAM的原因。如果你的应用中需要事务处理功能，则一点要使用正确的引擎类型。**

事务处理可以用来维护数据库的完整性，它保证成批的MySQL操作要么完全执行，要么完全不执行。它们或者作为整体执行，或者完全不执行(除非明确指示)。如果没有错误发生，整条语句提交给(写到)数据库表。如果发生错误，则进行回退(撤销)以恢复数据库到某个已知且安全的状态。

下面是关于事务查理需要知道的几个术语。

1.事务(transaction)：指一组SQL语句。

2.回退(rollback)：指撤销指定SQL语句的过程。

3.提交(commit)：指将未存储的SQL语句结果写入数据库表。

4.保留点(savepoint)：指事务处理中设置的临时占位符(placeholder)，你可以对它发布回退(与回退整个事务处理不同)。

### 16.2.控制事务处理

管理事务处理的关键在于将SQL语句分解为逻辑块，并明确规定数据何时应该回退，何时不应该回退。

Mysql使用下面的语句来标识事务的开始。

```mysql
输入：   START TRANSACTION
```

#### 16.2.1.使用ROLLBACK

MySQL的ROLLBACK命令用来回退(撤销)MySQL语句，看下面的语句：

```mysql
输入：    SELECT * FROM ordertotals;
         START TRANSACTION;
         DELETE FROM ordertotals;
         SELECT * FROM ordertotals;
         ROLLBACK;
         SELECT * FROM ordertotals;
```

分析：这个例子从显示ordertotals表的内容开始。首先执行一条SELECT以显示该表不为空。然后开始一个事务处理，用一条DELECT语句删除ordertotals中的所有行。另一条SELECT语句验证ordertotals确实为空。这时用一条ROLLBACK语句回退START TRANSACTION之后的所有语句，最后一条SELECT语句显示该表不为空。

显然ROLLBACK只能在一个事务处理内使用(在执行一条START TRANSACTION命令之后)。

**哪些语句可以回退?：事务处理用来管理INSERT,DPDATE,DELECT语句。你不能回退SELECT语句。(这样做也没有什么意义)你不能回退CREATE或DROP操作。事务处理块中可以使用这两条语句，但如果你执行回退，它们不会被撤销。**

#### 26.2.2.使用COMMIT

一般的MySQL语句都是直接针对数据库表执行和编写的。这就是错位的隐含提交，即提交(写或保存)操作是自动进行的。

但是，在事务处理块中，提交不会隐含地进行。为进行明确的提交，使用COMMIT语句，如下所示：

```mysql
输入：   START TRANSACTION;
        DELETE FROM orderitems WHERE order_name = 20010;
        DELETE FROM orders WHERE order_num = 20010;
        COMMIT;
```

分析：在这个例子中，从系统中完全删除订单20010。因为涉及更新两个数据库表orders和orderitems，所以使用事务处理块来保证订单不被部分删除。最后的COMMIT语句仅在不出错时写出更改。如果第一条SELECT起作用，第二条失败，则DELETE不会提交(实际上，它是被自动撤销的)。

**隐含事务关闭：当COMMIT或ROLLBACK语句执行后，事务会自动关闭(将来的更改会隐含提交)。**

#### 26.2.3.使用保留点

为了支持回退部分事务处理，必须能在事务处理块中何时的位置放置占位符。这样，如果需要回退，可以回退到某个占位符。这些占位符称为保留点。为了创建占位符，可如下使用SAVEPOINT语句。

```mysql
输入：   SAVEPOINT  delete1;
```

每个保留点都取标识它的唯一名字，以便在回退时，MySQL知道要回退到何处。为了回退到本例给出的保留点，可如下进行。

```mysql
输入：  ROLLBACK TO delete1;
```

**保留点越多越好：可以在MySQL代码中设置任意多的保留点，越多越好。因为保留点越多，你就越能按自己的意愿灵活地进行回退。**

**释放保留点：保留点在事务处理完成(执行一条ROLLBACK或COMMIT)后自动释放。在MySQL5以来，也可以用RELEASE SAVEPOINT明确地释放保留点。**

#### 26.2.4.更改默认的提交行为

默认的MySQL行为是自动提交所有更改。换句话说，任何时候你执行一条MySQL语句，该语句实际上都是针对表执行的，而且所做的更改立即生效。为指示MySQL不自动提交，需要使用以下语句。

```mysql
输入：   SET autocommit = 0;
```

分析：autocommit标志决定是否自动提交更改，不管有没有COMMIT语句。设置autocommit为0(假)指示MySQL不自动提交更改(知道autocommit被设置为真为止)。

**标志为连接专用：autocommit标志是针对每个连接而不是服务器的。**



