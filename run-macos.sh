#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ "$(uname -s)" != Darwin ]]; then
  echo "请在 macOS 上运行此脚本。" >&2
  exit 1
fi
if ! command -v java >/dev/null 2>&1; then
  echo "开发构建需要 JDK 17；安装后的应用无需另装 Java。" >&2
  exit 1
fi
case "${1:-run}" in
  run) exec ./gradlew :desktop:run ;;
  build) exec ./gradlew :desktop:build ;;
  test) exec ./gradlew :desktop:test ;;
  app) exec ./gradlew :desktop:createDistributable ;;
  dmg)
    ./gradlew :desktop:packageDmg
    build_arch="$(java -XshowSettings:properties -version 2>&1 | awk '/os.arch =/{print $3}')"
    case "$build_arch" in
      aarch64|arm64) build_arch=arm64 ;;
      x86_64|amd64) build_arch=amd64 ;;
      *) echo "无法识别 JDK 架构：$build_arch" >&2; exit 1 ;;
    esac
    app_version="$(head -n 1 version.txt | tr -d '\r\n')"
    mkdir -p release
    cp "desktop/build/compose/binaries/main/dmg/YunX-Desktop-$app_version.dmg" "release/YunX-Desktop-macOS-$build_arch-$app_version.dmg"
    echo "安装包：release/YunX-Desktop-macOS-$build_arch-$app_version.dmg"
    ;;
  *) echo "用法：./run-macos.sh {run|build|test|app|dmg}" >&2; exit 2 ;;
esac
