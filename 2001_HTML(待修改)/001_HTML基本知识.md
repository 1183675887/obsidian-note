---
title: HTML和CSS
date: 2020-12-06 11:05:29
tags:
  - HTML
---

本文主要讲述HTML

<!--more-->

## 1.HTML

```html
<head>
    <!--网页头部 -->
</head>
<meta> <!--描述型标签，它用来描述我们网站的一些信息-->
<title> 
    <!--网页标题-->
</title>

<body>
    <!--网页主体内容，所有显现的都在这个body标签内-->
</body>

<h1>
    <!--标题标签，123456就是不同的标签-->
</h1>

<p>
    <!--段落标签，每一个就是一行-->
</p>

<br>或者<br/>  <!--这是换行标签，两种都可以，只需要写一个就会换行了-->

<hr>或者<hr/>  <!--水平线标签-->

<Strong>
<!--，粗体标签将里面的字体加粗-->
</Strong>

<em>
<!--斜体标签-->
</em

<!--特殊符号-->
空格 &nbsp;
大于号 &gt;
小于号 &lt;
版权符号 &copy;

<!-- 图像标签-->
<!--img学习
src:图片地址； ../代表上一级目录，/代表下一级目录 
alt：当图片加载失败时显示的东西，一般是中文
title:悬停时产生的文字
width:图像宽度
height:图像高度-->
<img src="" alt="" width="" height="">
    
<!--链接标签-->
<!--a中的超链接学习，与img类似
href:链接路径，表示要跳转到哪个路径，可以嵌套图片等内容当做点击样式
target:表示链接在哪个窗口打开:_blank可以在新页面打开，默认_self,在自己的网页中打开，或者自己定义-->    
<a href="" target="" >链接名字</a>
<a href="" target="" >
    <img src="" alt="" width="" height=""></a> 
<!--a的锚链接学习
1.需要一个锚标记，2.跳转到标记
name:做标记，href中的就是name中的东西，点击就会跳转到name的位置-->
<a name="">锚链接名字</a>
<a href="name属性中的东西"></a>
<!--a的功能性链接，如邮件mailto:qq链接，也在href中填写-->
    
<!--有序列表，即顺序1.2.3.4.5.应用范围试卷，问答。ol为标签，li为显示内容-->
<ol>
  <li></li>
</ol>
<!--无序列表,即全是*，没顺序，应用范围导航，侧边栏，ul为标签，li为显示内容-->
<ul>
  <li></li>
</ul>
<!--自定义列表，dl为标签，dt为列表名称，dd为列表内容-->
<dl>
    <dt></dt>
    
    <dd></dd>
</dl>
    
<!--表格标签，table为标签，tr为行标签，td为列标签。注意td在tr内.border为边框；colspan为列的占据，rowspan为行的占据。-->
<table border="">
    <tr>
        <td colspan=""或者rowspan=""></td>
    </tr>
</table>
    
<!--音频audio标签和视频video标签,src:资源路径，controls:控制条。autoplay：自动播放-->
<video src="" controls autoplay></video>

<!--网页基本结构-->
<header></header>   //标题头部区域的内容
<footer></footer>   //标记脚部区域的内容
<section></section> //Web页面中的一块独立区域
<article></article> //独立的文章内容
<aside></aside>     //相关内容或应用(或用于侧边栏)
<nav></nav>         //导航类辅助内容
    
<!--iframe内联框架-->
<iframe src="" name="" width="" height=""></iframe>
    
<!--form为表单标签，method为用什么方式发送表单，action为向何处发送表单数据
input标签为文本输入框，name为名字，type为框内填写的方法，有很多种，以下都是。 value为初始默认值-->
<form method="" action="">
    <p>名字:<input name="name" type="text" value="" /></p>
    <p>密码:<input name="pass" type="password" /></p>
    <p>
        <input type="submit" name="Button" value="提交"/>
        <input type="reset" name="Reset" value="重填"/>
    </p>
</form>
    
<!--单选框标签name要一致。type中的radio就代表单选框，check为默认-->
<p>
    <input type="radio" value="boy" name="sex" checked />男
    <input type="radio" value="girl" name="sex" />女
</p>

<!--多选框标签name要一致。type的checkbox就代表多选框，checked为默认-->
<p>爱好：
    <input type="checkbox" value="sleep" name="hobby" checked>睡觉
    <input type="checkbox" value="code" name="hobby">敲代码
</p>

<!--按钮,type的button就代表普通按钮，image代表图像按钮，submit代表提交按钮，reser代表设置按钮。value代表按钮上的值-->
<p>
    <input type="button" name="btn" value="提交">
</p>

<!--select为下拉列表框，name为提交的列表名称，value为选项提交的值。如选择中国则会提交value中的值。selected为默认-->
<p>
    <select name="提交的列表名称">
        <option value="选项提交的值" selected>中国</option>
        <option value="选项的提交值">美国</option>
    </select>
</p>
    
<!--表单中的input各种属性。hidden为隐藏域，disabled为禁用。readonly只读-->
<input-----   hidden disabled readonly>   //只能选择其中一个，其中隐藏域经常用。
<!--placeholder为框内提示,required表示框内不能为空，pattern为正则表达式验证-->
<input placeholder=""  required   pattern=""/>
```
