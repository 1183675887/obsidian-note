---
title: MySQL必知必会4-9章
date: 2020-08-12 10:00:50
tags:
  - MySQL
  - MYSQL必知必会
---

这些章讲解检索数据，排序检索数据，过滤数据，数据过滤，用通配符过滤，用正则表达式进行搜索。

<!--more-->

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