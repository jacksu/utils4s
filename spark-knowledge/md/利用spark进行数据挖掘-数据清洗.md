数据清洗是数据分析的第一步，也是最重要的一步。但是很多数据分析师不会做，因为相对于使用高深的机器学习算法进行数据挖掘得到最终结果来说，太单调乏味而且还不会产生出结果。大家多听过“garbage in，garbage out”,但是很多是通过得出偏差的结果后再回去进行数据清洗。应该在数据的整个生命周期都应该发现有意思的和有意义的结果，技巧和精力应用的越早，对产品的结果就越有信心。

##spark编程模型

* > 在输入数据集上定义transformations
* > 在transformated的数据集调用actions，把结果保存或者返回给driver memory
* > 本地执行模仿分布式执行，帮助确定transformations和actions

##记录关联

记录关联（record linkage）包括实体解析、去重、合并分拆等。我们收集的数据大部分表示一个实体，比如用户、病人、商业地址和事件，他们有很多属性，例如name、address、phone等，我们需要通过这些属性来确定记录表示的是同一个实体，但是这些属性没有那么好，值可能表示形式不一样，类型不一样，甚至会缺失。如下表：

| *Name*	| *Address* | City | State | Phone|
| :-------------: |:-------------------:| :-----:| :-----:| :-----:|
|Josh’s Co ee Shop|1234 Sunset Boulevard |West Hollywood|CA|(213)-555-1212
|Josh Cofee|1234 Sunset Blvd West |Hollywood|CA|555-1212||Coffee Chain #1234|1400 Sunset Blvd #2|Hollywood|CA|206-555-1212|
|Coffee Chain Regional Office| 1400 Sunset Blvd Suite 2|Hollywood|CA|206-555-1212|

第一个实体和第二个是同一个，虽然看起来他们好像处在不同的城市。三表示咖啡店，四表示办公地点，但两个同时都给了公司总部的电话号码。因此进行记录关联是比较困难。

##例子

以病人的信息为例，处理数据的流程为：

1. 创建RDD,有两种方式：（1）通过外部数据源；（2）别的RDD通过transformation
2. 数据简单过滤（比如去掉第一行）
3. 用case class表示，这样每个字段都有名字
4. 如果数据后面会多次处理，那么最好调用cache
5. 做一些简单统计，比如个数，均值，方差等
6. 创建通用统计代码