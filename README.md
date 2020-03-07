# custom countdown progressbar
实现带有倒计时功能的进度条。

  基于view的绘制流程，通过onDraw方法去绘制相关的progressbar，除此之外融合了倒计时Timer，实现自动倒计时的功能，考虑到countdowntimer在应用的各个生命周期有内存泄漏的问题，用户需要在对应的生命周期调用封装过的countdowntimer对应的生命周期方法。

> 1.圆形进度条带有文字倒计时提示

> 2.水平进度条没有文字倒计时，可自行改进

### 自定义属性介绍
属性 | 功能 | 备注
---|---|---
showCountText | 是否显示倒数文字(char) | 默认显示(true)/关闭(false)
countDownText | 倒计时(int) | 默认5s(可自定义时长)
countDownTextSize | 倒数字体(int) | 默认20sp(可自定义大小)
countDownTextColor | 倒计时文本颜色 | 默认黑色(可自定义颜色)
strokeWidth | 进度条宽度 | 默认5dp(可自定义大小)
progressColor | 进度条进度颜色| 默认蓝色(可自定义颜色)
progressBgColor | 背景颜色| 默认灰色(可自定义颜色)
progressShape | 进度条形状 | 默认round(圆形进度条)/可选linear(水平进度条)

### API介绍
- setCountDownTimerCallBack(CountDownTimerCallBack callBack)//设置倒计时结束监听
- countDownResume()     //界面恢复时继续倒计时
- countDownCancel() //界面暂停时调用取消倒计时
- getAngle() //获取圆形进度角度
- getProgress() //获取进度百分比

### 更新内容
> 使用ObjectAnimator进行进度更新，使倒计时更加平滑