高仿微信6.5.7（融云版）
============

## 目录
* [一、简述](#一、简述)
* [二、功能](#二、功能)
* [三、效果图](#三、效果图)
* [四、其他相关](#四、其他相关)
* [五、打赏支持](#五、打赏支持)

# 一、简述

>本项目由 CSDN_LQR 个人独立开发。
>
>项目博客地址：[高仿微信6.5.7（融云版）](http://www.jianshu.com/p/f119810520e4)
>
>项目源码地址：[码云：LQRWeChatRongCloud](https://git.oschina.net/CSDNLQR/lqrwechatrongcloud)
>
>项目DemoApp下载：[Demo](https://git.oschina.net/CSDNLQR/lqrwechatrongcloud/blob/master/app-debug.apk?dir=0&filepath=app-debug.apk&oid=80f630859d9894c62406951929448f68ec7205e3&sha=1c65a0feab5c134a4c87889f8a5e21c7910a9a8f)
	
## 1、简单介绍一下：
这个项目是本人独立开发的第二个高仿微信项目，仿最新版微信6.5.7（除图片选择器外）。本项目基于融云SDK，使用目前较火的 Rxjava+Retrofit+MVP+Glide 技术开发。相比上个版本，加入发送位置消息，红包消息等功能。本项目由码云平台托管，欢迎start和fork~~

## 2、制作该开源项目的原因有：

1. 熟练使用 Rxjava+Retrofit+MVP+lambda 等新安卓技术。
2. 熟悉融云等SDK的使用。
3. 向高手进阶过渡。

## 3、统一回复下网友的问题：
有网友说看我上一个项目有别人提出的很多问题，而且我都没有回复并解决，实际是有的，只不过那时已经在着手准备开发这个新的高仿微信，而且因为上一个版本使用的是网易云SDK，开发上比较简单，同时该SDK的封装实在是太好了，所以没地方可以施展Retrofit，达不到我预计的提升要求，于是便选用了融云SDK干脆做了一个新的，上版中存在的一些问题已经在这个版本中基本解决，同时制作并更新了几个自己的库（如：表情库和语音库等）。

# 二、功能

## 1、好友

1. 查询好友
1. 发起添加好友请求
1. 查看好友个人信息
1. 设置备注
1. 删除好友
1. 扫码加好友
1. 查看新加朋友

## 2、群组

1. 拉人进群
1. 踢人去群
1. 修改群昵称
1. 查看群二级码
1. 扫码加入群组
1. 解散群（群主）
1. 退出群（群成员）

## 3、个人

1. 查看头像
1. 上传更新头像
1. 修改个人昵称
1. 查看个人二维码

## 4、会话

1. 会话置顶
1. 取消置顶
1. 删除会话
1. 撤回消息
1. 发送文本消息
1. 发送图片消息
1. 发送视频消息
1. 发送语音消息
1. 发送贴图消息
1. 发送位置消息
1. 发送红包消息

## 5、系统

1. 登录
1. 注册
1. 退出当前账号
1. 退出APP

## 6、尚未完成

1. 消息通知
1. @功能
1. 对方输入状态提示

# 三、效果图

![主界面](screenshots/1.gif)
![会话控制](screenshots/2.gif)
![录制、发送语音](screenshots/3.gif)
![发送表情文字](screenshots/4.gif)
![发送红包](screenshots/5.gif)
![抢红包](screenshots/6.gif)
![发送位置](screenshots/7.gif)
![录制、发送小视频](screenshots/8.gif)
![选择、发送图片](screenshots/9.gif)
![查看、撤回消息](screenshots/10.gif)
![拉人入群](screenshots/11.gif)
![踢人出群](screenshots/12.gif)
![修改群昵称](screenshots/13.gif)
![发起群聊](screenshots/14.gif)


# 四、其他相关

## 1、该项目使用到的技术有：

1. Rxjava 2.0
1. Retrofit 2.0
1. MVP 
1. Glide
1. lambda
1. ...

## 2、用到的主要库有：

### 主要的大神库：

1. [鸿神的AutoLayout](https://github.com/hongyangAndroid/AndroidAutoLayout)
1. [郭神的LitePal](https://github.com/LitePalFramework/LitePal)
1. [bingoogolapple的万能刷新控件](https://github.com/bingoogolapple/BGARefreshLayout-Android)
1. [bingoogolapple的二维码控件扫描库](https://github.com/bingoogolapple/BGAQRCode-Android)
1. [CJT2325的仿微信拍照Android控件](https://github.com/CJT2325/CameraView)
1. ...

### 自己(CSDN_LQR)做的库：

1. [万能适配器](https://github.com/GitLqr/LQRAdapterLibrary)
1. [包装过的RecyclerView](https://github.com/GitLqr/LQRRecyclerViewLibrary)
1. [高仿微信表情库](https://github.com/GitLqr/LQREmojiLibrary)
1. [高仿微信主意库](https://github.com/GitLqr/LQRAudioRecord)
1. [高仿微信图片选择器](https://github.com/GitLqr/LQRImagePicker)
1. [高仿微信九宫格控件](https://github.com/GitLqr/LQRNineGridImageView)
1. [常用选项条目库](https://github.com/GitLqr/LQROptionItemView)

## 3、说明与鸣谢：

不提供测试号，请使用自己手机注册后登录，因为本人手机号有限，测试上很有局限，可能存在一些我不知道的bug，请多包涵，可在项目中提出issue。本人做这个项目只为提升个人安卓开发能力，故依赖融云官方给出的server端做为本项目的后台服务，该server源码使用Node.js开发，目前本人只会用java开发后端，所以如果要搞点别的功能的话，目前是不可能啦，有兴趣的同学可以看看这个[嗨豹 IM 应用服务器](https://github.com/sealtalk/sealtalk-server)，当然融云也有它的坑，特别是红包module，我干脆不用它的了，希望该项目可以帮到那些正在踩坑的人（至少我已经踩了一次了，嘿嘿），此外，很感谢很多网友对我的支持，还有专门跑到CSDN跟我私信给我鼓励的，真的很感动，谢谢。

# 五、打赏支持

最后，如果觉得本项目对您有用，请随意打赏，鼓励我继续创作，谢谢啦。

![wechat](screenshots/wechat_pay.png)
![alipay](screenshots/alipay.png)


