正如你所知，spark实现了多种shuffle方法，通过 spark.shuffle.manager来确定。暂时总共有三种：hash shuffle、sort shuffle和tungsten-sort shuffle，从1.2.0开始默认为sort shuffle。本节主要介绍hash shuffle。

