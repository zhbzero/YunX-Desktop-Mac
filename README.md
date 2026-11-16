# YunX Desktop for macOS

基于 [tidain/YunX-Desktop](https://github.com/tidain/YunX-Desktop) 的 macOS 社区移植版，保留 Kotlin / Compose 界面、网盘分享解析、视频解析、多线程下载、断点续传、多账号与浏览器下载助手。

本仓库由 [zhbzero](https://github.com/zhbzero) 维护，属于独立的 macOS 适配项目。原作者与上游项目的署名、版权和 GNU AGPL-3.0 许可保留。

- 源码：[zhbzero/YunX-Desktop-Mac](https://github.com/zhbzero/YunX-Desktop-Mac)
- 安装包：[Releases](https://github.com/zhbzero/YunX-Desktop-Mac/releases)
- 问题反馈：[Issues](https://github.com/zhbzero/YunX-Desktop-Mac/issues)
- macOS 说明：[docs/MACOS.md](docs/MACOS.md)

## 在 Mac 上构建

需要 JDK 17。Gradle 会通过仓库自带的 Wrapper 下载；安装后的应用内置 Java 运行时。

```sh
./run-macos.sh run    # 开发运行
./run-macos.sh test   # 测试
./run-macos.sh app    # 生成 .app
./run-macos.sh dmg    # 生成 .dmg
```

构建时按当前 JDK 架构选择 Compose、JavaFX、SQLite、JCEF 的原生依赖。Apple Silicon 和 Intel 需要各自在相应架构的 Mac / JDK 上构建，产物不是通用二进制。本次在 Apple Silicon 上完成验证。

## macOS 适配

- Finder 打开目录和定位文件，原生文件 / 文件夹对话框。
- 菜单栏托盘、Dock 重新打开窗口、应用重启和下载期间防休眠。
- 数据默认保存在 `~/Library/Application Support/YunX-Desktop`。
- 账号凭证继续使用 AES-GCM 加密；普通档基于 Mac 的硬件标识保护主密钥，密钥文件仅当前用户可读写。
- Gopeed 选择当前 Mac 架构的官方 web 内核，并设置执行权限。
- FFmpeg 可从 `/opt/homebrew/bin`、`/usr/local/bin` 或 PATH 自动识别。可选安装：`brew install ffmpeg`；未安装时使用内置播放器与 Java 合轨。
- 夸克 / UC 分享图片直接预览，支持缩略图、缩放与左右切图。
- 应用内源码入口和更新地址指向本仓库，移除 QQ 群与捐赠入口。
- 修正直连代理选择与低速源频繁重连的问题。

Windows 专属的浏览器 Cookie 自动导入、DPAPI 档位、通知中心进度、下载完成关机暂不在 Mac 版提供。网盘可使用内嵌浏览器登录或手动粘贴 Cookie。第三方平台的实际登录和解析能力受其接口影响，需要使用自己的账号验证。

## 来源与许可

- Windows 上游：[tidain/YunX-Desktop](https://github.com/tidain/YunX-Desktop)，桌面端作者 tidain。
- Android 上游：[CYQawa/YunX](https://github.com/CYQawa/YunX)，作者 CYQawa。
- 许可：[GNU AGPL-3.0](LICENSE)。本移植继续以相同许可开放源码。
- 上游功能说明：[docs/FEATURES.md](docs/FEATURES.md)；Windows 构建说明：[docs/BUILD.md](docs/BUILD.md)。
