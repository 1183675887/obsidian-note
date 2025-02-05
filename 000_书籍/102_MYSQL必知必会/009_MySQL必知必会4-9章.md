---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags: MySQL
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

## 第四章，检索数据

### 4.1.SELECT语句

SELECT语句，它的用途是从一个或多个表中检索信息。

### 4.2.检索单个列

我们将从简单的SQL  SELECT语句开始介绍。此语句如下所示。

```mysql
输入:   SELECT  prod_name
        FROM  products;
```

分析：上述语句利用SELECT语句从products表中检索一个名为prod_name的列。所需的列名在SELECT关键字之后给出，REOM关键字之处从其中检索数据的表名。此语句的输出如下所示：

| prod_name      |
| -------------- |
| .5  ton  anvil |
| 1  ton   anvil |
| 2  ton   anvil |

如上一条的一个简单SELECT语句将返回表中所有行。数据没有过滤(过滤将得到结果集的一个子集)，也没有排序。

**结束SQL语句：多条SQL语句必须以分号(;)分隔。MySQL如同多数DBMS一样，不需要在单条SQL语句后加分号。但特定的DBMS可能必须在单条SQL语句后加上分号。当然，如果愿意可以总是加上分号。事实上，即使不一定需要，但加上分号肯定没有坏处。如果你使用的是mysql命令行，必须加上分号来结束SQL语句。**

**SQL语句和大小写：SQL语句不区分大小写，因此SELECT与select是相同的。许多开发人员喜欢对所有SQL关键字使用大写，而对所有列和表名使用小写，这样做使代码更易于阅读和调试。最佳方式是按照大小写的惯例，切使用时保持一致。**

**使用空格：在处理SQL语句时，其中所有空格都会忽略。SQL语句可以在一行上给出，也可以分成许多行。多数开发人员认为将SQL语句**

**分成多行更容易阅读和调试。**

### 4.3.检索多个列

想从一个表中检索多个列，使用相同的SELECT语句，唯一的不同是必须在SELECT关键字后面给出多个列名，列名之间必须以逗号分隔。

下面的SELECT语句从products表中选择3列。

```mysql
输入：  SELECT  prod_id, prod_name, prod_price
       FROM   products;
```

分析：与前一个例子一样，这条语句使用SELECT语句从表products中选择数据。在这个例子中，指定了3个列名，列名之间用逗号分隔。

此语句的输出如下：

| prod_id | prod_name      | prod_price |
| ------- | -------------- | ---------- |
| ANVO1   | .5  ton  anvil | 5.99       |
| ANVO2   | 1   ton  anvil | 9.99       |
| ANVO3   | 2   ton  anvil | 14.99      |
| OL1     | Oil  can       | 8.99       |

**数据表示：从上述输出可以看到，SQL语句一般返回原始的，无格式的数据。数据的格式化是一个表示问题，而不是一个检索问题。**

### 4.4.检索所有列

SELECT语句还可以检索所有的列而不必逐个列出它们。这可以通过在实际列名的位置使用星号(*)通配符来达到。如下所示。

```mysql
输入：  SELECT  *
       FROM   products;
```

分析：如果给定一个通配符(*)，则返回表中所有列。列的顺序一般是列在表定义中出现的顺序。

**使用通配符：一般，除非你确实需要表中的每个列，否则最好别使用*通配符。虽然使用通配符可能会使你自己省事，不用明确列出所需列，但检索不需要的列通常会降低检索和应用程序的性能。**

### 4.5.检索不同的行

如果你不想要每个值都出现。例如，加入你想得出products表中产品的所有供应商ID。

```mysql
输入： SELECT  vend_id
      FROM  products;
```

输出

| vend_id |
| ------- |
| 1001    |
| 1001    |
| 1002    |
| 1002    |

解决办法是使用DISTINCT关键字，顾名思义，此关键字指示MySQL只返回不同的值。

```
输入： SELECT  DISTINCT   vend_id
      FROM  porducts;
```

分析：SELECT  DISTINCT  vend_id告诉MySQL只返回不同(唯一)的vend_id行。输入如下。

| vend_id |
| ------- |
| 1001    |
| 1002    |

**不能部分使用DISTINCT：DISTINCT关键字应用于所有列而不仅是前置它的列。如果给出SELECT  DISTINCT  vend_id，prod_price，除非指定的两个列都不同，否则所有行都将被检索出来。**

### 4.6.限制结果

SELECT语句返回所有匹配的行，它们可能是指定表中的每个行。为了返回第一行或前几行，可使用LIMIT子句。下面举个例子：

```mysql
输入：  SELECT prod_name
       FROM   products
       LIMIT  2;
```

 分析：此语句使用SELECT语句检索单个列。LIMIT 2指示MySQL返回不多于2行。此语句的输入如下所示：

| prod_name      |
| -------------- |
| .5  ton  anvil |
| 1  ton  anvil  |

为得出下一个2行，可指定检索的开始行和行数，如下所示：

```mysql
输入：  SELECT prod_name
       FROM   products
       LIMIT  2,2;
```

分析：LIMIT  2,2指示MySQL返回从行2开始的2行。第一个数为开始位置，第二个数为要检索的行数。此语句的输出如下。

| prod_name      |
| -------------- |
| 2   ton  anvil |
| Oil  can       |

所以，带一个值的LIMIT总数从第一行开始，给出的数为返回的行数。带二个值的LIMIT可以指定从行号为第一个值的位置开始。

**行0：检索出来的第一行为行0而不是行1.因此LIMIT 1,1将检索出第二行而不是第一行**

### 4.7.使用完全限定的表名

迄今为止使用的SQL例子只通过列名引用列。可能会使用完全限定的名字来引用列(同时使用表名和列字)。看以下例子：

```mysql
输入：  SELECT  products.prod_name
       FROM  products;
```

这里指定了一个完全限定的列名。

表名也可以是完全限定的。如下所示：

```mysql
输入：  SELECT  products.prod_name
       FROM  crashcourse.products
```

有一些情形需要完全限定名。现在，需要注意这个语法，以便在遇到时知道它的作用。

## 第五章，排序检索数据

### 5.1.排序检索数据

**子句：SQL语句由子句构成的，有些子句是必须的，而有的是可选的。一个子句通常由一个关键字和所提供的数据组成。**

为了明确地排序用SELECT语句检索出的数据，可使用ORDER  BY子句。ORDER  BY子句取一个或多个列的名字，据此对输出进行排序。如下面的例子。

```mysql
输入：  SELECT prod_name
       FROM   products
       ORDER BY  prod_name;
```

分析：这条语句除了指示MySQL对prod_name列以字母顺序排序数据的ORDER BY子句外，与前面的语句一样。结果如下：

| prod_name    |
| ------------ |
| .5 ton anvil |
| Bird seed    |
| Carrots      |

通过非选择列进行排序：通常，ORDER  BY子句中使用的列将是为显示所选择的列。但是，实际上并不一定要这样，用非建设的列排序数据是完全合法的。

### 5.2.按多个列排序

为了按多个列排序，只需要指定列名，列名之间用逗号分开即可。

下面的代码检索3个列，并按照其中两个列对结果进行排序。首先是价格，然后在按名称排序。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price,prod_name
```

输出：

| prod_id | prod_price | prod_name    |
| ------- | ---------- | ------------ |
| FC      | 2.50       | Carrots      |
| TNT1    | 2.50       | TNT(1 stick) |
| FU1     | 3.42       | Fuses        |

重要的是理解在按多个列排序时，排序完全按所规定的顺序进行。换句话说，对于上述例子中的输出，仅在多个行具有相同的prod_price值时才对产品按prod_name进行排序。如果prod_price列中所有的值都是唯一的，则不会按prod_name排序。

### 5.3.指定排序方向

数据排序不限于升序排序。这只是默认的排序顺序，还可以使用ORDER  BY子句以降序顺序排序。为了进行降序排序，必须指定DESC关键字。下面的例子按价格以降序排序产品(最贵的排在最前面)。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price DESC;
```

输出：

| prod_id | prod_price | prod_name |
| ------- | ---------- | --------- |
| JP2000  | 55.00      | Jetpack   |
| SAFE    | 50.00      | Safe      |
| ANV03   | 35.00      | Jepack    |

还可以使用多个列排序。下面的例子以降序排序产品，然后再对产品名排序。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       ORDER BY prod_price DESC,prod_name
```

输出：

| prod_id | prod_price | prod_name |
| ------- | ---------- | --------- |
| JP2000  | 55.00      | Jetpack   |
| FB      | 10.00      | Bird      |
| TNT2    | 10.00      | TNT       |

DESC关键字只应用到直接位于前面的列名。在上例中，只对prod_price列指定DESC，对prod_name列不指定。因此，prod_price列以降序排序，而prod_name仍然按标准的升序排序。

**在多个列上降序排序：如果想在多个列上进行降序排序，必须对每个列指定DESC关键字。**

使用ORDER BY和LIMIT的组合，能够找到一个列中最高或最低的值。下面的例子演示了如何找到最昂贵物品的值。

```mysql
输入：  SELECT prod_price
       FROM   products;
       ORDER BY prod_price DESC
       LIMIT 1;
```

输出：

| prod_price |
| ---------- |
| 55.00      |

ORDER BY子句的位置：在给出ORDER  BY子句时，应该保证它位于FROM子句之后。如果使用LIMIT，它必须位于ORDER BY之后。使用子句的次序不对将产生错误消息。

## 第六章，过滤数据

### 6.1.使用WHRER子句

数据库表一般包含大量的数据，很少需要检索表中所有行。通常只会根据特定操作或报告的需要提取数据的子集。只检索所需数据需要制定搜索条件，搜索条件也称为过滤条件。

在SELECT语句中，数据根据WHERE子句中指定的搜索条件进行过滤。WHERE子句在表名(FROM子句)之后给出。如下所示：

```
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price = 2.50;
```

分析：这条语句从products表中检索两个列，但不返回所有行，只返回prod_price值为2.50的行，，如下所示：

| prod_name | prod_price |
| --------- | ---------- |
| Carrots   | 2.50       |
| TNT       | 2.50       |

**WHERE子句的位置：在同时使用ORDER BY和WHERE子句时，应该让ORDER BY位于WHERE之后，否则将会产生错误。**

### 6.2.WHERE子句操作符

MySQL支持以下的条件操作符。

| 操作符  | 说明               |
| ------- | ------------------ |
| =       | 等于               |
| <>      | 不等于             |
| !=      | 不等于             |
| <       | 小于               |
| <=      | 小于等于           |
| >       | 大于               |
| >=      | 大于等于           |
| BETWEEN | 在指定的两个值之间 |

#### 6.2.1.检查单个值

我们已经看到了测试相等的例子。再来看一个类似的例子。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_name = 'fuses';
```

输出：

| prod_name | prod_price |
| --------- | ---------- |
| fuses     | 3.42       |

分析：检查WHERE  prod_name = 'fuses'语句，它返回prod_name的值为Fuses的一行。MySQL在执行匹配时默认不区分大小写，所以fuses与Fuses匹配。

再来看其他例子。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price < 5;
```

输出：

| prod_name      | prod_price |
| -------------- | ---------- |
| .5  ton  anvil | 4.49       |
| 1  ton  anvil  | 2.50       |

这个例子是列出价格小于5美元的所有产品。

#### 6.2.3.范围值检查

为了检查某个范围的值，可使用BETWEEN操作符。其语法与其他WHERE子句的操作符稍有不同，因为它要两个值，即范围的开始值和结束值。

下面的例子说明如何使用BETWEEN操作符，它检索价格在9美元和10美元之间的所有产品。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE prod_price BETWEEN 9 AND 10;
```

输出：

| prod_name    | prod_price |
| ------------ | ---------- |
| 1  ton anvil | 9.99       |
| Bird         | 10.00      |

在使用BETWEEB时，必须指定两个值-----所需范围的底端值和高端值。这两个值必须用AND关键字分隔。BETWEEN匹配范围中所有的值，包括指定的开始值和结束值。

#### 6.2.4.空值检查

在创建表时，表设计人员可以指定其其中的列是否可以不包含值。在一个列不包含值时，称其为包含空值NULL。

**NULL  无值(no value)：它与字段包含0，空字符串或仅仅包含空格不同。**

SELECT语句由一个特殊的WHERE语句，可用来检查具有NULL值的列。这个WHERE子句就是IS NULL子句。语法如下：

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE prod_price IS NULL;
```

因为prod_price值没有一个为NULL，所以不返回。

## 第七章，数据过滤

### 7.1.组合WHERE子句

为了进行更强的过滤控制，MySQL允许给出多个WHERE子句。这些子句可以两种方式使用：以AND子句的方式或OR子句的方式使用。

**操作符(oprerator)：用来联结或改变WHERE子句中的子句的关键字。也称为逻辑操作符。**

#### 7.1.1.AND操作符

为了通过不止一个列进行过滤，可使用AND操作符给WHERE子句附加条件。下面的代码给了一个例子。

```mysql
输入：  SELECT  prod_id, prod_price,prod_name
       FROM   products;
       WHERE  vend_id = 1003 AND prod_price <= 10;
```

分析：此SQL语句检索由供应商1003制造切价格小于等于10美元的所有产品的名称和价格。AND关键字联结它们的条件。输出如下。

| prod_id | prod_price | prod_name |
| ------- | ---------- | --------- |
| FB      | 10.00      | Bird      |
| FC      | 2.50       | Carrots   |

**AND：用在WHERE子句中的关键字，用来指示检索满足所有给定条件的行。**

我们还可以添加多个过滤条件，每添加一条就要使用一个AND。

#### 7.1.2.OR操作符

OR操作符与AND操作符不同，它指示MySQL检索匹配任一条件的行。

看如下的SELECT语句。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE  vend_id = 1003 OR vend_id = 1002;
```

分析：OR操作符告诉DBMS匹配任一条件而不是同时匹配两个条件，只要其中一个满足即可。输出如下。

| prod_name | prod_price |
| --------- | ---------- |
| Bird      | 13.00      |
| Carrots   | 2.50       |

**OR：WHERE子句中使用的关键字，用来表示检索任意给定条件的行**

#### 7.1.3.计算次序

WHERE可包含任意数目的AND和OP操作符。允许两者结合以进行复原和更高级的过滤。

SQL语句默认先执行AND操作符，再执行OR操作符，为了避免出现问题，做好是使用圆括号明确地分组相应的操作符。看如下语句。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE  (vend_id = 1003 OR vend_id = 1002) AND prod_price >= 10;
```

输出：

| prod_name | prod_price |
| --------- | ---------- |
| Bird      | 10.00      |
| Safe      | 50.00      |

**分析：这条语句中，前两个条件用圆括号括了起来。因为圆括号具有较AND或OR操作符高的计算次序，DBMS首先过滤圆括号内的OR条件。再执行AND操作符**

**在WHERE子句中使用圆括号：任何时候使用具有AND和OR操作符的WHERE子句，都应该使用圆括号明确地分组操作符。**

### 7.2.IN操作符

圆括号在WHERE子句中还有另外一种用法。IN操作符用来指定条件范围，范围中的每个条件都可以进行匹配。IN取合法值的由逗号分隔的清单，全都括在圆括号中。下面例子说明这个操作。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE  vend_id IN (1002,1003)
       ORDER BY  prod_name;
```

输出：

| prod_name | prod_price |
| --------- | ---------- |
| Bird      | 10.00      |
| Safe      | 50.00      |

IN与OR类似。为什么要使用IN操作符，其优点如下。

1.在使用长的合法选项清单时，IN操作符的语法更清楚且更直观。

2.在使用IN时，计算的次序更容易管理(因为使用的操作符更少)。

3.IN操作符一般比OR操作符清单执行更快。

4.IN的最大优点是可以包含其他SELECT语句，使得能够更动态地建立WHERE子句。第十四章将会对此进行详细介绍。

**IN：WHERE子句中用来指定要匹配值的清单的关键字，功能与OR相当。**

### 7.3.NOT操作符

WHERE子句中的NOT操作符有且只有一个功能，那就是否定它之后所跟的任何条件。

NOT：WHERE子句中用来否定后跟条件的关键字。下面列字说明NOT的使用。

```mysql
输入：  SELECT prod_name, prod_price
       FROM   products;
       WHERE  vend_id NOT IN (1002,1003)
       ORDER BY  prod_name;
```

输出：

| prod_name     | prod_price |
| ------------- | ---------- |
| .5  ton anvil | 5.99       |
| 1  ton anvil  | 9.99       |

**分析：这里的NOT否定跟在它之后的条件，因此MySQL不是匹配1002和1003的vend_id。**

为什么使用NOT？对于简单的WHERE子句，使用NOT确实没有优势。但在更复杂的子句中，NOT是非常有用的。例如，在于IN操作符联合使用时，NOT使找出与条件列表不匹配的行为非常简单。

**MySQL中的NOT：MySQL支持使用NOT对IN，BETWEEN和EXISTS子句取反，这与多数其他DBMS允许使用NOT对各种条件取反有很多的差别。**

## 第八章，用通配符进行过滤

### 8.1.LINE操作符

**通配符(wildcard)：用来匹配值的一部分的特殊字符。**

**搜索模式(search pattern)：由字母值，通配符或两者组合构成的搜索条件。**

通配符本身实际是SQL的WHERE子句中有特殊含义的字符，SQL支持几种通配符。为在搜索子句中使用通配符，必须使用LIKE操作符。LIKE指示MySQL，后跟的搜索模式利用通配符匹配而不是直接相等匹配进行比较。

**谓词：操作符何时不是操作符？答案是在它作为谓词时。从技术上来说，LIKE是谓词而不是操作符，虽然结果是一样的。**

#### 8.1.1.百分号(%)通配符

最常使用的通配符就是百分号(%)。在搜索串中，%表示任何字符出现任意次数。例如，为了找出所有以词jet起头的产品，可使用以下SELSET语句。

```mysql
输入：  SELECT prod_id, prod_name
       FROM   products;
       WHERE   prod_name LIKE 'jet%';
```

输出：

| prod_id | prod_name     |
| ------- | ------------- |
| JP1000  | JetPACK  1000 |
| JP2000  | JetPACK  2000 |

分析：此例子使用了搜索模式'jet%'。在执行这条子句时，将检索任意以jet起头的词。%告诉MySQL接受jet之后的任意字符。

通配符可以在搜索模式中任意位置使用，并且key使用多个通配符。下面的例子使用两个通配符，它们位于模式的两端。

```mysql
输入：  SELECT prod_id, prod_name
       FROM   products;
       WHERE   prod_name LIKE '%anvil%';
```

输出：

| prod_id | prod_name     |
| ------- | ------------- |
| ANV01   | .5 ton anvil  |
| ANV02   | 1  ton  anvil |

分析：搜索模式'%anvil%'表示匹配任何位置包含文本anvil的值，而不论它之前或之后出现什么字符。

通配符也可以出现在搜索模式的中间，虽然这样做不太有用。下面的例子找出以s起头以e结尾的所有产品。

```mysql
输入：  SELECT prod_id, prod_name
       FROM   products;
       WHERE   prod_name LIKE 's%e';
```

**注意尾空格：尾空格可能会干扰通配符匹配。**

**注意NULL：虽然似乎%通配符可以匹配任何东西，但有一个例外，即NULL。**

#### 8.1.2.下划线(_)通配符

另一个有用的通配符是下划线(_).下划线的用途与%一样，但下划线只匹配单个字符而不是多个字符。举一个例子。

```mysql
输入：  SELECT prod_id, prod_name
       FROM   products;
       WHERE   prod_name LIKE '_ ton anvil';
```

输出：

| prod_id | prod_name   |
| ------- | ----------- |
| ANV02   | 1 ton anvil |
| ANV03   | 2 ton anvil |

分析：此WHERE子句中的搜索模式给出了后面跟有文本的两个通配符。

### 8.2.使用通配符的技巧

这里给出一些使用通配符要记住的技巧。

1.不要过度使用通配符。如果其他操作符能达到相同的目的，应该使用其他操作符。

2.在确实需要使用通配符时，除非绝对有必要，否则不要把它们用在搜索模式的开始处。把通配符置于搜索模式的开始处，搜索起来是最慢的。

3.仔细注意通配符的位置。如果放错地方，可能不会反回想要的数据。

## 第九章，用正则表达式进行搜索

### 9.1.正则表达式介绍

正则表达式是用来匹配文本的特殊的串(字符集合)。

### 9.2.使用MySQL正则表达式

MySQL用WHERE子句对正则表达式提供了初步的支持，允许你指定正则表达式，过滤SELECT检索出的数据。

#### 9.2.1.基本字符匹配

我们个一个非常简单例子开始，下面的语句检索列prod_name包含文本1000的所有行。

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '.000'
       ORDER BY prod_name;
```

输出：

| prod_name    |
| ------------ |
| JetPack 1000 |

分析：REGEXP后跟的东西为正则表达式。这里使用了正则表达式'.000'，是正则表达式语言中一个特殊的字符。它表示匹配任一一个字符。

**LIKE与REGEXP：LIKE匹配整个列，REGEXP在列值内进行匹配。**

**匹配不区分大小写：MySQL中的正则表达式匹配不区分大小写。**

#### 9.2.2.进行OR匹配

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '1000|2000'
       ORDER BY prod_name;
```

输出：

| prod_name    |
| ------------ |
| JetPack 2000 |
| JetPack 1000 |

分析：使用正则表达式1000|2000。|为正则表达式的OR操作符。它表示匹配其中之一。

#### 9.2.3.匹配几个字符之一

匹配任何单一字符。如果你想匹配特定的字符，可通过指定一组用[和]括起来的字符来完成。如下所示。

```mysql
输入：  SELECT prod_name
       FROM   products;
       WHERE   prod_name REGEXP '[123] Ton'
       ORDER BY prod_name;
```

输出：

| prod_name     |
| ------------- |
| 1  ton anvil  |
| 2  ton  anvil |

分析：这里，使用了正则表达式[123] Ton，[123]定义一组字符，它的意思是匹配1或2或3。

#### 9.2.4.匹配范围

集合可用来定义要匹配的一个或多个字符。例如下面的集合将匹配数字0-9。[0-9]。

#### 9.2.5.匹配特殊字符

为了匹配特殊字符，必须用\\为前导，\\-表示查找-。

#### 9.2.6.匹配字符类

涉及太多，自行百度。

#### 9.2.8.定位符

