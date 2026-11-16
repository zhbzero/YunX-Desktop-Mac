# macOS 构建与使用

这是 YunX-Desktop 的 macOS 社区移植版。基线为上游提交 `00a030f778a65840e9be65baecb15497a94b04ec`（1.3.4），版权与 AGPL-3.0 许可保持不变。

## 安装

从本仓库 Releases 获取与芯片匹配的 DMG，打开后将 `YunX-Desktop.app` 拖入「应用程序」。应用自带 Java，无需安装开发环境。

本地构建使用临时签名，没有 Apple Developer ID 签名和公证。macOS 首次打开时可能要求用户在「系统设置 → 隐私与安全性」中允许打开；请仅在确认来源后操作。

第一次使用内嵌登录浏览器会把随包携带的 Chromium 解压到应用缓存目录，可能需要数秒。

## 开发与打包

安装对应架构的 JDK 17 后，在仓库根目录运行：

```sh
./run-macos.sh run
./run-macos.sh test
./run-macos.sh app
./run-macos.sh dmg
```

`.app` 位于 `desktop/build/compose/binaries/main/app/`，原始 DMG 位于 `desktop/build/compose/binaries/main/dmg/`；脚本会在 `release/` 中生成带架构名称的安装包，便于发布和自动更新识别。Intel 版本必须使用 Intel JDK 构建；本次交付只验证 Apple Silicon 产物。

Gradle Wrapper 使用官方 Gradle 8.13，并固定发行包 SHA-256。首次构建需要联网下载依赖。

## 数据和可选引擎

默认数据目录为 `~/Library/Application Support/YunX-Desktop`。`YUNX_DESKTOP_DATA_DIR` 可用于指定独立测试数据目录。凭证密钥文件随数据库保存；跨机器迁移请用应用中的认证备份功能。

Gopeed 在「设置 → 下载引擎」中按需获取当前平台的官方内核，也可以导入官方 `gopeed-web-版本-macos-arm64.zip` 或 `macos-amd64.zip`。

FFmpeg 为可选项。应用会识别 Homebrew 或 PATH 中的 `ffplay`、`ffmpeg` 和 `ffprobe`，Mac 版使用独立播放窗口，不使用 Win32 窗口嵌入。

```sh
brew install ffmpeg
```

系统代理支持读取 macOS 的 HTTP / HTTPS 静态代理。PAC、自动发现和绕过列表暂不支持。

## 功能边界

Mac 版不提供 Windows DPAPI 凭证保护档位、浏览器 Cookie 自动导入、Windows 通知中心进度和下载完成关机。账号可通过内嵌 Chromium 登录，或手动粘贴 Cookie。原作者的版权与贡献说明保留。

不提供 Apple Developer ID 签名和公证。本次验证包括自动化测试、Mac 原生依赖初始化与应用窗口启动。未使用个人网盘账号执行第三方平台的完整登录下载测试。
