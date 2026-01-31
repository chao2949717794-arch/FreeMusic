# 快速构建脚本
# 使用GitHub Actions自动构建APK

Write-Host "=== 自由音 - 快速构建工具 ===" -ForegroundColor Green
Write-Host ""

# 检查Git
if (!(Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "❌ 未检测到Git，正在安装..." -ForegroundColor Red
    winget install --id Git.Git -e --source winget
    Write-Host "✅ Git安装完成，请重启终端后重新运行此脚本" -ForegroundColor Green
    exit
}

Write-Host "✅ Git已就绪" -ForegroundColor Green

# 进入项目目录
$projectPath = "d:\worktask\qian-test\FreeMusic"
Set-Location $projectPath

Write-Host "`n📦 初始化Git仓库..." -ForegroundColor Yellow
git init
git add .
git commit -m "Initial commit: FreeMusic Android App"

Write-Host "`n✅ 本地仓库已准备就绪！" -ForegroundColor Green
Write-Host ""
Write-Host "=== 下一步操作 ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "1️⃣  访问 https://github.com/new 创建新仓库" -ForegroundColor White
Write-Host "    - 仓库名称: FreeMusic" -ForegroundColor Gray
Write-Host "    - 选择 Public（免费构建额度更多）" -ForegroundColor Gray
Write-Host ""
Write-Host "2️⃣  创建后，复制仓库地址" -ForegroundColor White
Write-Host ""
Write-Host "3️⃣  执行以下命令（替换YOUR_USERNAME）：" -ForegroundColor White
Write-Host ""
Write-Host "    git remote add origin https://github.com/YOUR_USERNAME/FreeMusic.git" -ForegroundColor Yellow
Write-Host "    git branch -M main" -ForegroundColor Yellow
Write-Host "    git push -u origin main" -ForegroundColor Yellow
Write-Host ""
Write-Host "4️⃣  推送后，GitHub会自动构建APK（约5分钟）" -ForegroundColor White
Write-Host ""
Write-Host "5️⃣  构建完成后，前往 Actions 标签下载 APK" -ForegroundColor White
Write-Host ""
Write-Host "🎉 完成！" -ForegroundColor Green
