# MultilevelFeedbackQueue

[![MultilevelFeedbackQueueSimulation](https://img.shields.io/badge/MultilevelFeedbackQueueSimulation-v1.1.0-brightgreen.svg)](
https://github.com/Yuziquan/MultilevelFeedbackQueueSimulation)
[![license](https://img.shields.io/packagist/l/doctrine/orm.svg)](https://github.com/somelou/MultilevelFeedbackQueueSimulation/blob/master/LICENSE)

### 更新
> * 更正了多级反馈队列调度算法(太懒了不想写...)
> * 去除了几个不重要的按钮动作(可参照原项目添加回去)
> * 不知道怎么请求合并


*以下为原项目说明*
因为不会再原项目基础上改...
> 原项目地址: https://github.com/Yuziquan/MultilevelFeedbackQueueSimulation
> 原项目博客：https://blog.csdn.net/wuchangi/article/details/80432794

### 一、项目功能

**采用多级反馈队列调度算法进行进程调度的模拟。**

<br/>

*具体要求如下：*

> * 每个进程对应一个 PCB。在 PCB 中包括进程标识符 pid、进程的状态标识 status、进程优先级 priority、表示进程生命周期的数据项 life（在实际系统中不包括该项）。
> * 创建进程时即创建一个 PCB，各个进程的 pid 都是唯一的，pid 是在 1 到 100 范围内的一个整数。
> * 可以创建一个下标为 1 到 100 的布尔数组， “假”表示下标对应的进程标识号是空闲的，“真”表示下标对应的进程标识号已分配给某个进程。
> * 进程状态 status 的取值为“就绪 ready”或“运行 run”，刚创建时，状态为“ready”。被进程调度程序选中后变为“run”。
> * 进程优先级 priority 是 0（最低） 到 49（最高） 范围内的一个随机整数。
> * 进程生命周期 life 是 1 到 5 范围内的一个随机整数。
> * 初始化时，创建 50 个就绪队列，各就绪队列的进程优先级 priority 分别是 0 到 49。
> * 为了模拟用户动态提交任务的过程，要求动态创建进程。进入进程调度循环后，每次按 ctrl+f 即动态创建一个进程，然后将该 PCB 插入就绪队列中。
> * 在进程调度循环中，每次选择优先级大的就绪进程来执行。将其状态从就绪变为运行，通过延时一段时间来模拟该进程执行一个时间片 的过程，然后优先级减半，生命周期减一。
> * 如果将该运行进程的生命周期不为 0，则重新把它变为就绪状态，插入就绪队列中；否则该进程执行完成，撤消其 PCB。以上为一次进程调度循环。
> * 设计图形用户界面 GUI，在窗口中显示该进程和其他所有进程的 PCB 内容。

<br/>

***

### 二、项目运行效果
![1](https://github.com/Yuziquan/MultilevelFeedbackQueueSimulation/blob/master/Screenshots/MFQ.gif)
