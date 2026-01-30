#!/bin/bash

# 量化 SDK 发布到 GitHub Packages 脚本
# 使用方法: ./deploy-to-github-packages.sh

set -e

# GitHub Packages 配置
GITHUB_REPO="arkmsg/ark-nexus"
DEPLOY_REPO="githubarkmsg::https://maven.pkg.github.com/${GITHUB_REPO}"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Maven settings.xml 配置
check_maven_config() {
    print_info "检查 Maven settings.xml 配置..."
    
    if [ ! -f ~/.m2/settings.xml ]; then
        print_warn "未找到 ~/.m2/settings.xml，将创建基本配置"
        mkdir -p ~/.m2
        cat > ~/.m2/settings.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>githubarkmsg</id>
            <username>arkmsg</username>
            <password>\${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>
EOF
        print_warn "请设置环境变量 GITHUB_TOKEN 或在 settings.xml 中配置 GitHub Token"
    else
        if ! grep -q "githubarkmsg" ~/.m2/settings.xml; then
            print_warn "settings.xml 中未找到 githubarkmsg 配置，请手动添加"
        else
            print_info "Maven settings.xml 配置检查通过"
        fi
    fi
}

# 发布单个项目
deploy_project() {
    local project_dir=$1
    local project_name=$2
    
    if [ ! -d "$project_dir" ]; then
        print_warn "项目目录不存在: $project_dir，跳过"
        return 1
    fi
    
    print_info "========================================="
    print_info "发布项目: $project_name"
    print_info "目录: $project_dir"
    print_info "========================================="
    
    cd "$project_dir"
    
    # 清理并发布
    print_info "执行: mvn clean deploy -DaltDeploymentRepository=${DEPLOY_REPO}"
    if mvn clean deploy -DaltDeploymentRepository="${DEPLOY_REPO}" -DskipTests; then
        print_info "✅ $project_name 发布成功"
        cd ..
        return 0
    else
        print_error "❌ $project_name 发布失败"
        cd ..
        return 1
    fi
}

# 主函数
main() {
    print_info "开始发布量化 SDK 到 GitHub Packages"
    print_info "目标仓库: ${GITHUB_REPO}"
    print_info "部署仓库 ID: ${DEPLOY_REPO}"
    echo ""
    
    # 检查 Maven 配置
    check_maven_config
    echo ""
    
    # 检查 GITHUB_TOKEN
    if [ -z "$GITHUB_TOKEN" ]; then
        print_warn "未设置 GITHUB_TOKEN 环境变量"
        print_warn "请设置: export GITHUB_TOKEN=your_github_token"
        print_warn "或在 ~/.m2/settings.xml 中直接配置密码"
        echo ""
        read -p "是否继续? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_error "已取消发布"
            exit 1
        fi
    fi
    
    # 按依赖顺序发布
    # 1. 基础 SDK（无依赖）
    print_info "========== 第一阶段: 基础 SDK =========="
    deploy_project "trading-calendar-sdk" "Trading Calendar SDK"
    deploy_project "alpha4j" "Alpha4j"
    
    # 2. 依赖基础 SDK 的项目
    print_info "========== 第二阶段: 依赖基础 SDK =========="
    deploy_project "stocks-indicator-sdk" "Stocks Indicator SDK"
    
    # 3. 依赖多个 SDK 的项目
    print_info "========== 第三阶段: 依赖多个 SDK =========="
    deploy_project "stocks-sdk" "Stocks SDK"
    
    # 4. 依赖 stocks-sdk 的项目
    print_info "========== 第四阶段: 依赖 Stocks SDK =========="
    deploy_project "stocks-data-sdk" "Stocks Data SDK"
    deploy_project "longport-sdk" "Longport SDK"
    deploy_project "binance-sdk" "Binance SDK"
    deploy_project "stocks-strategy-sdk" "Stocks Strategy SDK"
    
    # 5. BOM（最后发布）
    print_info "========== 第五阶段: BOM =========="
    deploy_project "quant-bom" "Quant BOM"
    
    print_info ""
    print_info "========================================="
    print_info "✅ 所有 SDK 发布完成！"
    print_info "========================================="
    print_info "查看已发布的包: https://github.com/${GITHUB_REPO}/packages"
}

# 运行主函数
main

