mapWithState的延迟是updateStateByKey的6X，维持10X的keys的状态。导致这种情况的原因是：

* > 避免处理没有新数据的keys

* > 限制计算新数据keys的数量，这样可以减少每批次处理延迟