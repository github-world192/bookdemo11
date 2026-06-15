# bookdemo11 - The Star 東方之星度假飯店

參考 CJA101G1 專案架構與第二組期中報告規格，以 **Spring Boot 3.5 + Spring Security + Thymeleaf + JPA** 實作的訂房網站 Demo。

## 功能模組

### 前台
- 首頁、關於我們、設施介紹、FAQ
- 會員註冊 / 登入（Spring Security）
- 房型展示、空房查詢、訂房表單、訂單確認
- 會員中心、訂房紀錄、優惠券折抵、Stripe 線上付款
- 飯店商城、購物車、購物訂單、Stripe 結帳

### 後台
- **員工登入**：Spring Security 表單登入，依角色載入功能權限
- **員工管理**：新增 / 編輯 / 停用 / 刪除員工，指派部門角色
- **員工權限**：角色（部門）管理，勾選功能權限（儀表板、員工、房型、訂房等）
- 房型管理、房間管理、訂房管理、會員管理（依權限顯示選單）

## 測試帳號

| 角色 | 帳號 | 密碼 |
|------|------|------|
| 會員 | member@test.com | 123456 |
| 超級管理員 | admin | admin123 |
| 櫃檯人員 | staff | staff123 |
| 房務主管 | housekeeping | hk123456 |

優惠券代碼：`STAR500`（折抵 NT$500，最低消費 NT$3000）

## Stripe 金流設定

1. 複製環境變數範本：
   ```bash
   cp .env.example .env
   ```
2. 至 [Stripe Dashboard](https://dashboard.stripe.com/apikeys) 取得 Test 金鑰，填入 `.env`：
   ```
   STRIPE_SECRET_KEY=sk_test_...
   STRIPE_PUBLISHABLE_KEY=pk_test_...
   APP_BASE_URL=http://localhost:8081
   ```
3. 重啟應用後，訂房與購物結帳可選擇 **Stripe 信用卡（線上付款）**，將導向 Stripe Checkout 完成付款。

付款代碼：`payMethod=0` 臨櫃、`payMethod=1` Stripe

## 啟動方式

```bash
cd ~/bookdemo11
mvn spring-boot:run
```

瀏覽器開啟：http://localhost:8081

H2 資料庫控制台：http://localhost:8081/h2-console

## 專案架構

```
com.bookdemo11
├── config/          # Security、資料初始化
├── member/          # 會員模組
├── employee/        # 員工 / 後台模組
├── room/            # 房型、房間
├── booking/         # 訂房訂單
├── coupon/          # 優惠券
└── controller/    # 首頁等共用 Controller
```

## 技術棧

- Java 17
- Spring Boot 3.5.0
- Spring Security 6（雙 SecurityFilterChain：會員 / 後台）
- Spring Data JPA + H2（可切換 MySQL）
- Thymeleaf + Thymeleaf Security Extras