---
title: MySQL必知必会15-20章
date: 2020-08-17 14:23:53
tags: MySQL
---

这些章讲解联结表，组合查询，全文本搜索，插入数据，更新和删除数据。

<!--more-->

## 第十五章，联结表

### 15.1.联结

联结是利用SELECT能执行的最重要的操作，在有效地使用联结前，必须了解关系表以及关系数据库设计的一些基础知识。

#### 15.1.1.关系表

理解关系表的最好办法是来看一个现实世界中的例子。

假如有一个包含产品目录的数据库表，其中每种类别的物品占一行。对于每种物品要存储的信息包括产品描述和价格，以及生产该产品的供应商信息。

现在，假如有同一供应商生产的多种物品，那么在何处存储供应商信息(如，供应商名，地址，联系方法等)呢？将这些数据与产品信息分开存储的理由如下：

1.因为同一个供应商生产的每个产品的供应商信息都是相同的，对每个产品重复此信息即浪费时间又浪费存储空间。

2.如果供应商信息改变(例如，供应商搬家或电话号码变动)，值需改动一次即可。

3.如果有重复数据(即每种产品都存储供应商信息)，很难保证每次输入该数据的方式都相同。不一致的数据在报表中很难利用。

关键是，相同数据出现多次绝不是一件好事，此因素是关系数据库设计的基础。关系表的设计就是要保证把信息分解成多个表，一类数据一个表。各表通过某些常用的值(即关系设计中的关系)互相关联。

在这个例子中，可建立两个表，一个存储供应商信息，另一个存储产品信息。vendors表包含所有供应商信息，每个供应商占一行，每个供应商具有唯一的标识。此标识称为主键，可以是供应商ID或任何其他唯一值。

products表只存储产品信息，它除了存储供应商ID(vendors的主键)外不存其他供应商信息。vendors表又叫作products表的外键，它将vendors表和products表关联，利用供应商ID能从vendors表中找出相应供应商的详细信息。

**外键(foregin key)：外键为某个表中的一列，它包含另一个表的主键值，定义了两个表之间的关系。**

这样做的好处如下：

1.供应商信息不重复，从而不浪费时间和空间。

2.如果供应商信息变动，可以只更新vendors表中的单个记录，相关表中的数据不用改动

3.由于数据无重复，显然数据是一致的，这使得处理数据更简单。

总之，关系数据库的可伸缩性远比非关系数据库要好。

**可伸缩性：能够适应不断增加的工作量而不失败。设计良好的数据库或应用程序称之为可伸缩性好。**

#### 15.1.2.为什么要使用联结

分解数据更能有效地存储，更方便处理，并且有着更大的可伸缩性。但这些好处是有代价的。

如果数据存储在多个表中，怎样用单条SELECT语句检索出数据？答案是使用联结。简单的说，联结是一种机制，用来在一条SELLECT语句中关联表，因此称之为联结。使用特殊的语法，可以联结多个表返回一组输出，联结在运行时关联表中正确的行。

### 15.2.创建联结

联结的创建非常简单，规定要联结的所有表以及它们如何关联即可。看下面的例子。

```mysql
输入：  SELECT  vend_name, prod_name,prod_price
       FROM   vendors, products
       WHERE  vendors.vend_id = products.vend_id
       ORDER BY vend_name, prod_name
```

输出：

| vend_name | prod_name | prod_price |
| --------- | --------- | ---------- |
| ACME      | Bird      | 10.00      |
| ACME      | Carr      | 2.50       |

分析：这里的语句与前面的大致相同。这里，最大的差别是所指定的两个列(prod_name和prod_price)在一个表中，而另一个列(_name)在另一个表中。

现在来看FROM子句。与之前的SELECT子句不一样，这条语句的FROM子句列出了两个表，它们就是这条SELECT语句联结的两个表的名字。这两个表用WHERE子句正确联结，WHERE子句指示MySQL匹配vendors表中的vend_id和products表中的vend_id。

可以看到要匹配的两个列以vendors.vend_id和products.vend_id指定。这里需要这种完全限定列名，因为如果只给出vend_id，则MySQL不知道指的是哪一个(它们有两个，一个表一个)。

**笛卡儿积：由没有联结条件的表关系返回的结果为笛卡儿积。检索出的行的数目将是第一个表中的行数乘以第二个表中的行数。**

**不要忘了WHERE子句：应该保证所有联结都有WHERE子句，否则MySQL将返回比想要的数据多得多的数据。同理，应该保证WHERE子句的正确性。不正确的过滤条件将导致MySQL返回不正确的数据。**

#### 15.2.2.内部联结

目前为止所用的联结称为等值联结，它基于两个表之间的相等测试。这种联结也称为内部联结。其实，对于这种联结可以使用稍微不同的语法来明确指定联结的类型。下面的SELECT语句返回与前面例子完全相同的数据。

```mysql
输入：  SELECT  vend_name, prod_name,prod_price
       FROM   vendors INNER  JOIN products
       ON vendors.vend_id = products.vend_id;
```

分析：此语句中的SELECT与前面的SELECT语句相同，但FROM子句不同。这里，两个表之间的关系是FROM子句的组成部分，以INNER JOIN指定。在使用这种语法时，联结条件用特定的ON子句而不是WHERE子句给出。传递给ON的实际条件与传递给WHERE的相同。

**使用哪种语言：SQL规范首选INNER JOIN语法。此外，尽管使用WHERE子句定义联结的确比较简单，但是使用明确的联结语法能够确保不会忘记联结条件，有时候这样做也能影响性能。**

#### 15.2.3.联结多个表

SQL对SELECT语句可以联结的表的数目没有限制。创建联结的基本规则也相同。首先列出所有表，然后定义表之间的关系。

## 第十六章，创建高级联结

### 16.1.使用表别名

别名除了用于列名和计算字段外，SQL还允许给表名起别名。这样做有两个主要理由：

1.缩短SQL语句

2.允许在单条SELECT语句中多次使用相同的表。

看如下例子：

```mysql
输入：  SELECT  cust_name, cust_contact
       FROM   customers AS c, orders AS o, orderitems AS oi
       WHERE c.cust_id = o.cust_id
       AND oi.order_num =o.order_num
       AND prod_id = 'TNT2';
```

分析：可以看到，FROM的3个表都有别名。这样在WHERE子句中可以使用别名。应该注意，表别名只在查询执行中使用。与列别名不同，表别名不返回客户机。

### 16.2.使用不同类型的联结

现在来看3种其他联结，它们分别是自联结，自然联结和外部联结。

#### 16.2.1.自联结

如前所述，使用表别名的主要原因之一是能在单条SELECT语句中不止一次引用相同的表。下面举个例子。

假如你发现某物品(其ID为DTNTR)存在问题，因此想知道生产该物品的供应商生产的其他物品是否也存在这些问题。此查询要求首先找到生产ID为DTNTR的物品的供应商，然后找出这个供应商生产的其他物品。下面是解决方法之一：

```mysql
输入：  SELECT  prod_id, prod_name
       FROM   products
       WHERE  vend_id = (SELECT vend_id
                         FROM products
                         WHERE prod_id = 'DTNTR')
```

现在来看看使用联结的相同查询：

```mysql
输入：  SELECT  p1.prod_id, p1.prod_name
       FROM   products AS p1, products AS p2
       WHERE p1.vend_id = p2.vend_id
       AND p2.prod_id = 'DTNTR';
```

输出：

| prod_id | prod_name |
| ------- | --------- |
| DTNTR   | Deton     |
| FB      | Bird      |

分析：此查询需要的两个表实际是相同的表，因此products表在FROM子句中出现了两次。虽然这是完全合法的，但对products的引用具有二义性，因为MySQL不知道你引用的是products表中的哪个实例。为解决这问题，使用了表别名。

**用自联结而不用子查询：自联结通常作为外部语句用来替代从相同表中检索数据时使用的子查询语句。虽然最终的结果是相同的，但有时候处理联结远比处理子查询快得多。应该试一下两种方法，以确定哪一种的性能更好。**

#### 16.2.2.自然联结

无论何时对表进行联结，应该至少有一个列出现在不止一个表中(被联结的表)。标准的联结(前一章介绍的内部联结)返回所有数据，甚至相同的列多次出现。自然联结排除多次出现，使每个列只返回一次。

自然联结是这样一种联结，其他你只能选择那些唯一的列。这一般是通过对表使用通配符(SELECT*)，对所有其他表的列使用明确的子集来完成的。举个例子：

```mysql
输入：  SELECT c.*, o.order_num, o.order_date,oi.prod_id, oi.quantiry, oi.item_price
       FROM  customers AS c, orders AS o, orderitems AS oi
       WHERE c.cust_id = o.cust_id
       AND oi.order_num = o.order_num
       AND prod_id = 'FB';
```

分析：在这个例子中，通配符只对第一个表使用。所有其他列明确列出，所有没有重复的列被检索出来。

事实上，迄今为止我们建立的每个内部联结都是自然联结，很可能我们永远都不会用到不是自然联结的内部联结。

#### 16.2.3.外部联结

许多联结将一个表中的行与另一个表中的行相关联。但有时候会需要包含没有关联行的那些行。例如，可能需要使用联结来完成以下工作。

1.对每个客户下了多少订单进行计数，包括没有人订购的产品。

2.列出所有产品以及订购数量，包括那些没有人订购的产品。

3.计算平均销售规模，包括那些至今未下订单的客户。

在上述例子中，联结包含了那些在相关表中没有关联的行。这种类型的联结称为外部联结。

下面的语句给出了一个简单的内部联结，它检索所有客户以及其订单。

```mysql
输入：  SELECT  customers.cust_id, orders.order_num
       FROM  customers LEFT OUTER JOIN orders
        ON customers.cust_id = orders.cust_id;
```

输出：

| cust_id | order_num |
| ------- | --------- |
| 10001   | 20005     |
| 10002   | 20009     |

分析：这条语句使用了关键字OUTER JOIN来指定联结的类型(而不是在WHERE子句中指定)。但是，与内部联结关联两个表中的行不同的是，外部联结还包括没有关联行的行。在使用OUTER JOIN语法时，必须使用RIGHT或LEFT关键字指定包括其所有行的表(RIGHT指出的是OUTER JOIN右边的表，而LEFT关键字指出的是OUTER JOIN左边的表)。上面的例子使用LEFT OUTER JOIN从FROM子句的左边表(customers表)中选择所有行。为了从右边的表中选择所有行，应该使用RIGHT OUTER JOIN。如下例所示。

```mysql
输入：  SELECT  customers.cust_id, orders.order_num
       FROM  customers RIGHT OUTER JOIN orders
        ON orders.cust_id = customers.cust_id;
```

**外部联结的类型：存在两种基本的外部联结形式：左外部联结和右外联结。它们之间的唯一差别是所关联的表的顺序不同。**

### 16.3.使用带聚集函数的联结

函数也可以与联结一起使用。为说明这一点，看一个例子。如果要检索所有客户及每个客户所下的订单数，下面使用了COUNT()函数的代码可完成此工作。

```mysql
输入：  SELECT  customers.cust_name, customers.cust_id, COUNT(orders.order_num) AS num_ord
       FROM  customers INNER JOIN orders
        ON customers.cust_id = orders.cust_id
       GROUP BY customers.cust_id;
```

分析：此语句使用INNER JOIN将customers和orders表互相关联。GROUP BY子句按客户分组数据，因此函数调用COUNT(orders.order_num)对客户的订单计算，将它作为num_ord返回。

### 16.4.使用联结和联结条件

在总结关于联结的这两章前，有必要汇总一下关于联结及其使用的某些要点。

1.注意所使用的联结类型。一般我们使用内部联结，但使用外部联结也是有效的。

2.保证使用正确的联结条件，否则将返回不正确的数据。

3.应该总是提供联结条件，否则会得出笛卡儿积。

4.在一个联结中可以包含多个表，甚至对于每个联结可以采用不同的联结类型。虽然这样做是合法的，一般也很有用，但应该在一起测试它们前，分别测试每个联结。这将使故障排除更为简单。

## 第十七章，组合查询

### 17.1.组合查询

MySQL允许执行多个查询(多条SELECT语句)，并将结果作为单个查询结果集返回。这些组合查询通常称为并或复合查询。

有两种基本情况，其中需要使用组合查询。

1.在单个查询中从不同的表返回类似结构的数据。

2.对单个表执行多个查询，按单个查询返回数据。

**组合查询和多个WHERE条件：多数情况下，组合相同表的两个查询完成的工作与具有多个WHERE子句条件的单条查询完成的工作相同。换句话说，任何具有多个WHERE子句的SELECT语句都可以作为一个组合查询给出，在以下段落中可以看到这一点。这两种技术在不同的查询中性能也不同。因此，应该试一下这两种技术，以确定对特定的查询哪一种性能更好。**

### 17.2.创建组合查询

可以UNION操作符来组合数条SQL查询。利用UNION，可给出多条SELECT语句，将它们的结果组合成单个结果集。

#### 17.2.1.使用UNION

UNION的使用很简单。所需做的只是给出每条SELECT语句，在各条语句之间放上关键字UNION。

举一个例子，假如需要价格小于等于5的所有物品的一个列表，而且还想包括供应商1001和1002生产的所有物品(不考虑价格)。当然，可以利用WHERE子句来完成此工作，不过这次我们将使用UNION。

```mysql
第一条输入： SELECT vend_id, prod_id, prod_price
           FROM products
           WHERE prod_price <= 5;
```

```mysql
第二条输出： SELECT vend_id, prod_id, prod_price
           FROM products
           WHERE vend_id IN (1001,1002);
```

为了组合这两条语句，可以使用UNION。

```mysql
输入：    SELECT vend_id, prod_id, prod_price
         FROM products
         WHERE prod_price <= 5
         UNION
         SELECT vend_id, prod_id, prod_price
         FROM products
         WHERE vend_id IN (1001,1002);
```

分析：这条语句由前面两条语句组成，语句中用UNION关键字分隔。UNION指示MySQL执行两个SELECT语句，并把输出组合成单个查询结果集。

#### 17.2.2.UNION规则

正如所见，并是非常容易使用的。但在进行并时有几条规则需要注意。

1.UNION必须由两条或两条以上的SELECT语句组成，语句之间用关键字UNION分隔(因此，如果组合4条SELECT语句，将要使用3个UNION关键字)。

2.UNION中的每个查询必须包含相同的列，表达式或聚集函数(不过各个列不需要以相同的次序列出)。

3.列数据类型必须兼容：类型不必完全相同，但必须是DBMS可以隐含地转换的类型(例如：不同的数值类型或不同的日期类型)。

4.如果遵守了这些基本规则或限制，则可以将并用于任何数据检索任务。

#### 17.2.3.包含或取消重复的行

UNION从查询结果集中自动去除了重复的行。这是UNION的默认行为，但是如果需要，可以改变它。事实上，如果想反回所有匹配行，可使用UNION ALL而不是UNION。

#### 17.2.4.组合查询结果排序

语句的输出用ORDER BY排序。在用UNION组合查询时，只能使用一条ORDER BY子句，它必须出现在最后一条SELECT语句之后。对于结果集，不存在用一种方式排序一部分，而又用另一种方式排序另一部分的情况，因此不允许使用多条ORDER BY子句。

下面的例子排序前面的UNION返回的结果。

```mysql
输入：    SELECT vend_id, prod_id, prod_price
         FROM products
         WHERE prod_price <= 5
         UNION
         SELECT vend_id, prod_id, prod_price
         FROM products
         WHERE vend_id IN (1001,1002)
         ORDER BY vend_id, prod_price;
```

输出：

| vend_id | prod_id | prod_price |
| ------- | ------- | ---------- |
| 1001    | ANV01   | 5.99       |
| 1001    | ANV02   | 9.99       |

分析：这条UNION在最后一条SELECT语句后使用了ORDER BY子句，虽然ORDER BY子句似乎只是最后一条SELECT语句的组成部分，但实际上MySQL将用它来排序所有SELECT语句返回的所有结果。

## 第十八掌，全文本搜索

### 18.1.理解全文本搜素

**并非所有引擎都支持全文本搜索：两个最常见引擎为MyISAM和InnoDB。前者支持全文本搜索，而后者不支持。**

第八章介绍了LIKE关键字，它利用通配操作符匹配文本(和部分文本)。利用LIKE，能够查找包含特殊值或部分值的行(不管这些值位于列内什么位置)。

第九章中，用基于文本的搜索作为正则表达式匹配列值的更近一步的介绍。使用正则表达式，可以编写查找所需行的非常复杂的匹配模式。

虽然这些搜索机制非常有用，但存在几个重要的限制。

1.性能--通配符和正则表达式匹配通常要求MySQL尝试匹配表中所有行(而且这些搜索极少使用表索引)。因此，由于被搜索行数不断增加，这些搜索可能非常耗时。

2.明确控制----使用通配符和正则表达式匹配，很难(而且并不总是能)明确地控制匹配什么和不匹配什么。例如，指定一个词必须匹配，一个词必须不匹配，而一个词仅在第一个词确实匹配的情况下可以匹配或者才可以不匹配。

3.智能化的结果---虽然基于通配符和正则表达式的搜索结果的方法。例如，一个特殊词的搜索将会返回包含该词的所有行，而不区分包含单个匹配的行和包括多个匹配的行(按照可能是更好的匹配来排列它们)。类似，一个特殊词的搜索将不会找出不包含该词但包含其他相关词的行。

所有这些限制以及更多的限制都可以用全文本搜来解决。

### 18.2.使用全文本搜索

为了进行全文本搜索，必须索引被搜索的列，而且要随着数据的改变不断地重新索引。在对表列进行适当设计后，MySQL会自动进行所有的索引和重新索引。在索引之后，SELECT可与Match()和Against()一起使用以解决实际问题。

#### 18.2.1.启用全文本搜索支持

一般在创建表时启动全文本搜索。CREATE TABLE语句接受FULLTEXT子句，它给出被索引的一个逗号分隔的列表。

下面的CREATE语句演示了FULLTEXT子句的使用。

```mysql
CREATE TABLE productnotes
(
 note_id     int         NOT NULL, AUTO_INCREMENT
 prod_id     char(10)    NOT NULL,
 note_date   datetime    NOT NULL,
 note_text   text        NULL,
 PRIMARY  KEY(note_id),
 FULLTEXT(note_text)
)ENGINE=MyISAM;
```

分析：这些列中有一个名为note_text的列，为了进行全文本搜索，MySQL根据子句FULLTEXT(note_text)的指示对它进行索引。这里的FULLTEXT索引单个列，如果需要也可以指定多个列。

在定义之后，MySQL自动维护该索引。在增加，更新或删除行时，索引随之自动更新。可以在创建表时指定FULLTEXT，或者在稍后指定(在这种情况下所有已有数据必须立即索引)。

不要在导入数据时使用FULLTEXT：更新索引要花时间，虽然不是很多，但毕竟要花时间。如果正在导入数据到一个新表，此时不应该启动FULLTEXT索引。应该首先导入所有数据，然后再修改表，定义FULLTEXT。这样有助于更快地导入数据(而且使索引数据的总时间小于在导入每行时分别进行索引所需的总时间)。

#### 18.2.2.进行全文本搜索

在索引之后，使用两个函数Match()和Against()执行全文本搜索，其中Match()指定被搜索的列，Against()指定要使用的搜索表达式。

下面举一个例子：

```mysql
输入： SELECT note_text
      FROM productnotes
      WHERE Match(note_text) Against('rabbit');
```

输出：

| note_text              |
| ---------------------- |
| C-----------文本内容。 |

分析：此语句检索单个列note_text，由于WHERE子句，一个全文本搜索被执行。Match(note_text)指示MySQL针对指定的列进行搜索哦，Against('rabbit')指定rabbit作为搜索文本。由于有两行包含词rabbit，这两行被返回。

#### 18.2.3.使用查询扩展

查询扩展用来设法放宽所返回的全文本搜索结果的范围。考虑下面的情况。你想找出所有提到anvils的注释。只有一个注释包含词anvils。但你好像找出可能与你的搜索有关的所有其他行，即使它们不包含词anvils。这也是查询扩展的一项任务。在使用查询扩展时，MySQL对数据和索引进行两遍扫描来完成搜索。

1.首先，进行一个基本的全文本搜索，找出与搜索条件匹配的所有行。

2.其次，MySQL检查这些匹配行并选择所有有用的词(我们将会简单地解释MySQL如何断定什么有用，什么无用)。

3.再其次，MySQL再次进行全文本搜索，这次不仅使用原来的条件，而且还使用所有有用的词。

下面举个例子：

```mysql
输入： SELECT note_text
      FROM productnotes
      WHERE Match(note_text) Against('anvils' WITH QUERY EXPANSION);
```

输出：

| note_text     |
| ------------- |
| M----文本内容 |

分析：这次返回了7行。第一行包含词anvils，因此等级最高。第二行与anvils无关，但因为它包含第一行中的两个词(customer和recommend)，所以也被检索了出来。后面的行也如此。

#### 18.2.4.布尔文本搜索

以布尔方式，可以提供如下内容的细节。

1.要匹配的值。2.要排斥的词。3.排列提示。4.表达式分组。5.另外一些内容

即使没有FULLTEXT索引也可以使用：布尔方式不同于迄今为止使用的全文本搜索语法的地方在于，即使没有定义FULLTEXT索引，也可以使用它，但这是一种非常缓慢的操作(其性能将随着数据量的增加而降低)。

为演示IN BOOLEAN MOOE的作用，举一个简单的例子。

```mysql
输入： SELECT note_text
      FROM productnotes
      WHERE Match(note_text) Against('heavy -rope*' IN BOOLEAN MOOE);
```

输出：

| note_text         |
| ----------------- |
| C--------文本内容 |

分析：这次只返回一行。这一次匹配词heavy，但-rope*明确地指示MySQL排除包含rope*(任何以rope开始的词)，包括(ropes)的行。

我们已经看到了布尔操作符-和*。下面列出常见的几种操作符。

| 布尔操作符 | 说明                                                         |
| ---------- | ------------------------------------------------------------ |
| +          | 包含，词必须存在                                             |
| -          | 排除，词必须不出现                                           |
| >          | 包含，而且增加等级值                                         |
| <          | 包含，且减少等级值                                           |
| ()         | 把词组成子表达式(允许这些子表达式作为一个组被包含，排除，排列等) |
| ~          | 取消一个词的排序值                                           |
| *          | 词尾的通配符                                                 |
| " "        | 定义一个短语(与单个词的列表不一样，它匹配整个短语以便包含或排除这个短语) |

#### 18.2.5.全文本搜索的使用说明

给出全文本搜索的某些重要的说明。

1.在索引全文本数据时，短词被忽略且从索引中排除。短词定义为那些具有3个或3个以下字符的词(如果需要，这个数目可以更改)。

2.MySQL带有一个内建的非用词(stopword)列表，这些词在索引全文本数据时总是被忽略、如果需要，可以覆盖这个列表(请参考MySQL文档以了解如何完成此工作)。

3.许多词出现的频率很高，搜索它们没有用处。

4.如果表中的行数小于3行，则全文本搜索不返回结果

5.不具有词分隔符(包括日语和汉语)的语言不能恰当地返回全文本搜索结果。

6.如前所述，仅在MyISAM数据库引擎中支持全文本搜索。

## 第十九章，插入数据

### 19.1.数据插入

INSERT是用来插入(或添加)行到数据库表的。插入可以用几种方式使用：

1.插入完整的行。2.插入行的一部分。3.插入多行。4.插入某些查询的结果。

### 19.2.插入完整的行

把数据插入表中的最简单的方法是使用基本INSERT语法，它要求指定表名和被插入到新行中的值。下面举一个例子：

```mysql
输入： INSERT INTO customers
      VALUES(NULL, 'Pep E. LaPew', '100 Main Street', 'Los Angeles', 'CA', '90046', 'USA', NULL, NULL);
```

**没有输出：INSERT语句一般不会产生输出。**

分析：此例子插入一个新客户到customers表。存储在每个表列中的数据在VALUE子句中给出。对每个列必须提供一个值。如果某个列没有值，应该使用NULL值(假定表允许对该列指定空值)。各个列必须以它们在表定义中出现的次序填充。第一列也为NULL，因为每次插入一个新行时，该列由MySQL自动增量。你不想给出一个值，又不能省略此列，所以指定一个NULL值。

虽然这种语法很简单，但并不安全，应该尽量避免使用。应该使用下面的语法。

```mysql
输入： INSERT INTO customers(cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country, cust_contact, cust_email)
      VALUES(NULL, 'Pep E. LaPew', '100 Main Street', 'Los Angeles', 'CA', '90046', 'USA', NULL, NULL);
```

分析：这个例子在表名后的括号中明确地给出了列名。在插入行的时候，MySQL将用VALUES列表中的相应值填入列表中的对应项。VALUES中的第一个值对应第一个指定的列名。第二个值对应第二个指定的列名，如此等等。

**总是使用列的列表：一般不要使用没有明确给出列的列表的INSERT语句。使用列的列表能使SQL代码继续发挥作用，即使表结构发生了变化。**

**仔细的给出值：不管使用哪种INSERT语法，都必须给出VALUES的正确数目，如果不提供列名，则必须给每个表列提供一个值。如果提供列名，则必须对每个列出的列给出一个值。如果不这样，将产生一条错误信息，相应的行插入不成功。**

**提高整体性能：数据库经常被多个客户访问，对处理什么请求以及用什么次序处理进行管理是MySQL的任务，INSERT操作可能很耗时(特别是由很多索引需要更新时)，而且它可能降低等待处理的SELECT语句的性能。如果数据数据检索是最重要的(通常是这样)，则你可以通过在INSERT和INTO之间添加关键字LOW_PRIORITY，指示MySQL降低INSERT的优先级，如下所示：**

**INSERT LOW_PRIORITY INTO       。这也适用于UPDATE和DELETE。**

### 19.3.插入多个行

INSERT可以插入一行到一个表中。但如果你想插入多个行可以使用多条SELECT语句，甚至一次提交它们，每条语句用一个分号结束，如下所示：

```mysql
输入： INSERT INTO customers(cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country)
      VALUES(xxx, 'xxx', 'xxx', 'xxx', 'xxx', 'xxx', 'xxx',xxx,xxx);
      INSERT INTO customers(cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country)
      VALUES(xxx, 'xxx', 'xxx', 'xxx', 'xxx', 'xxx', 'xxx',xxx,xxx);
      INSERT INTO customers(cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country)
      VALUES(xxx, 'xxx', 'xxx', 'xxx', 'xxx', 'xxx', 'xxx',xxx,xxx);
```

或者，只要每条INSERT语句中的列名(和次序)相同，可以如下组合各语句。

```mysql
输入： INSERT INTO customers(cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country )
      VALUES(xxx, 'xxx', 'xxx', 'xxx', 'xxx', 'xxx', 'xxx',xxx,xxx),
            (xxx, 'xxx', 'xxx', 'xxx', 'xxx', 'xxx', 'xxx',xxx,xxx);
```

分析：其中，单条INSERT语句有多组值，每组值用一对圆括号括起来，用逗号分隔。

**提高INSERT的性能：此技术可以提高数据库处理的性能，因为MySQL用单条INSERT语句处理多个插入比使用多余INSERT语句快。**

### 19.4.插入检索出的数据

INSERT一般用来给表插入一个指定列值的行。但是，INSERT还存在另一种形式，可以利用它将一条SELECT语句的结果插入表中。这就是所谓的INSERT SELECT，顾名思义，它是由一条INSERT语句和一条SELECT语句组成的。

假如你想从另一个表中合并用户列表到你的customers表。不需要每次读取一行，然后再将它用INSERT插入，可以如下进行：

```mysql
输入： INSERT INTO customers(cust_id, cust_contact, cust_email, cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country)
      SELECT cust_id, cust_contact, cust_email, cust_name, cust_address, cust_city, cust_state, cust_zip, cust_country
      FROM custnew;
```

分析：这个例子使用INSERT SELECT从custnew中检索出要插入的值，而不是列出它们。SELECT中列出的每个列对应于customers表名后跟的列表中的每个列。这条语句将插入多少行有赖于custnew表中有多少行。如果这个表为空，则没有行被插入(也不产生错误，因为操作是合法的)。如果这个确实含有数据，则所有数据将被插入到customers。

**INSERT SELECT中的列名：为简单起见，这个例子在INSERT和SELECT语句中使用了相同的列名。但是，不一定要求列名匹配。事实上，MySQL甚至不关心SELECT返回的列名。它使用的是表列中指定的第一个列，第二个列将用来填充表列中指定的第二个列，如此等等。这对于从使用不同列名的表中导入数据是非常有用的。**

INSERT SELECT中SELECT语句可包含WHERE子句过滤插入的数据。

## 第二十章，更新和删除数据

### 20.1.更新数据

为了更新(修改)表中的数据，可使用UPDATE语句。可采用两种方式使用UPDATE：

1.更新表中特定行。2.更新表中所有行。

**不要省略WHERE子句：在使用UPDATE时一定要注意细心。因为稍不注意，就会更新表中所有行。**

UPDATE语句非常容易使用。基本的UPDATE语句由3部分组成，分别是：

1.要更新的表。2.列名和它们的新值。3.确定要更新行的过滤条件。

举一个例子。客户10005现在有了电子邮件地址，因此他的记录需要更新，语句如下：

```mysql
输入： UPDATE customers
      SET cust_email = 'elmer@fudd.com'
      WHERE cust_id = 10005;
```

分析：UPDATE语句以WHERE子句结束，它告诉MySQL更新哪一行。没有WHERE子句，MySQL将会用这个电子邮件地址更新customers表中所有行，这不是我们所希望的。

更新多个列的语法稍有不同。

```mysql
输入： UPDATE customers
      SET cust_name = 'THE Fudds',
          cust_email = 'elmer@fudd.com'
      WHERE cust_id = 10005；
```

分析：在更新多个列时，只需要使用单个SET命令，每个“列=值”对之间用哪个逗号分隔(最后一列之后不用逗号)。在这个例子中，更新客户10005的cust_name和cust_email列。

**在UPDATE语句中使用子查询：UPDATE语句中可以使用子查询，使得能用SELECT语句检索出的数据更新列数据。**

**IGNORE关键字：如果用UPDATE语句更新多行，并且在更新这些行中的一行或多行时出现错误，则整个UPDATE操作被取消(错误发生前更新的所有行被恢复到它们原来的值)。即使是发生错误，也继续进行更新，可使用IGNORE关键字。如下所示：**

**UPDATE IGNORE customers........**

为了删除某个列的值，可设置它为NULL(假如表定义允许NULL值)。如下进行：

```mysql
输入： UPDATE customers
      SET cust_email = NULL
      WHERE cust_id = 10005；
```

分析：其中NULL用来去除输入cust_email列中的值。

### 20.2.删除数据

为了从一个表删除(去掉)数据，使用DELETE语句。可以两种方式使用DELETE：

1.从表中删除特定的行。2.从表中删除所有行。

**不要省略WHERE子句：在使用DELECT时一定要注意细心。因为稍不注意，就会错误地删除表中所有行。**

前面说过，UPDATE非常容易使用，而DELECT更容易使用。下面的语句从customers表中删除一行：

```mysql
输入： DELECT FROM customers
      WHERE cust_id = 10006;
```

分析：这条语句很容易理解。DELECT FROM要求指定从中删除数据的表名。WHERE子句过滤要删除的行。在这个例子中，只删除客户10006.如果省略WHERE，它将删除表中每个客户。

DELECT不需要列名或通配符。DELECT删除整行而不是删除列。为了删除指定的列，请使用UPDATE语句。

**删除表的内容而不是表：DELECT语句从表中删除行，甚至是删除表中所有行。但是，DELECT不删除表本身。**

**更快的删除：如果想从表中删除所有行，不要使用DELECT。可使用TRUNCATE TABLE语句，它完成相同的工作，但速度更快(TRUNCATE实际是删除原来的表并重新创建一个表，而不是逐行删除表中的数据)。**

### 20.3.更新和删除的指导原则

下面是许多SQL程序员使用UPDATE或DELECT时所遵循的习惯。

1.除非确实打算更新和删除每一行，否则绝对不要使用不带WHERE子句的UPDATE和DELECT语句。

2.保证每个表都有主键，尽可能像WHERE子句那样使用它(可以指定各主键，多个值，或值的范围)。

3.在对UPDATE或DELECT语句使用WHERE子句前，应该先用SELECT进行测试，保证它过滤的是正确的记录，以防编写的WHERE子句不正确。

4.使用强制实施引用完整的数据库，这样MySQL将不允许删除具有与其他表相关联的数据的行。

