# custom countdown progressbar
实现带有倒计时功能的进度条。

  基于view的绘制流程，通过onDraw方法去绘制相关的progressbar，除此之外融合了倒计时Timer，实现自动倒计时的功能，考虑到countdowntimer在应用的各个生命周期有内存泄漏的问题，用户需要在对应的生命周期调用封装过的countdowntimer对应的生命周期方法。

> 1.圆形进度条带有文字倒计时提示

> 2.水平进度条没有文字倒计时，可自行改进

> 3.有自定义属性去设置进度条颜色，宽度，文字颜色，大小等
