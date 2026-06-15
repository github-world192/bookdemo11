
---

## 如何啟動網站

### 事前準備

電腦需已安裝：

- **Java**（17 或以上）
- **Maven**（建置工具）

若不確定是否已安裝，可請開發人員協助確認。

### 啟動步驟

1. 開啟「終端機」（Terminal）或「命令提示字元」
2. 進入專案資料夾，例如：
   ```bash
   cd ~/bookdemo11
   ```
3. 執行啟動指令：
   ```bash
   mvn spring-boot:run
   ```
4. 等待畫面出現類似 `Started Bookdemo11Application` 的訊息，代表啟動成功
5. 打開瀏覽器（Chrome、Safari、Edge 皆可），在網址列輸入：

   **http://localhost:8081**

   看到飯店首頁即表示網站已正常運作。

### 如何關閉

在執行啟動指令的那個視窗，按 `Ctrl + C` 即可停止網站。

### 若網址打不開

- 確認啟動指令是否還在執行中（視窗沒有關掉）
- 若出現「Port 8081 was already in use」，代表網站**可能已在背景執行**，直接開瀏覽器試 **http://localhost:8081** 即可
- 若仍無法連線，請請開發人員協助

---

## 網站網址一覽

所有頁面都使用同一個網址開頭：**http://localhost:8081**  
差別在於後面的路徑（可以想成「不同房間的門牌」）。

### 前台（給旅客 / 會員）

| 功能 | 網址 |
|------|------|
| 飯店首頁 | http://localhost:8081 |
| 關於我們 | http://localhost:8081/about |
| 設施介紹 | http://localhost:8081/facilities |
| 常見問題 | http://localhost:8081/faq |
| 房型列表 | http://localhost:8081/rooms |
| 會員登入 | http://localhost:8081/member/login |
| 會員註冊 | http://localhost:8081/member/register |
| 會員中心 | http://localhost:8081/member/center（需先登入） |
| 空房查詢 / 訂房 | http://localhost:8081/booking/search（需先登入） |
| 我的訂房紀錄 | http://localhost:8081/booking/records（需先登入） |
| 飯店商城 | http://localhost:8081/shop |
| 購物車 | http://localhost:8081/shop/cart（需先登入） |
| 購物訂單 | http://localhost:8081/shop/orders（需先登入） |

### 後台（給飯店員工）

| 功能 | 網址 |
|------|------|
| **員工登入** | **http://localhost:8081/admin/login** |
| 後台儀表板 | http://localhost:8081/admin/dashboard（登入後自動導向） |
| 訂房管理 | http://localhost:8081/admin/orders |
| 房型管理 | http://localhost:8081/admin/room-types |
| 房間管理 | http://localhost:8081/admin/rooms |
| 會員管理 | http://localhost:8081/admin/members |
| 員工管理 | http://localhost:8081/admin/employees |
| 權限 / 部門管理 | http://localhost:8081/admin/org |

> **重點整理**
> - 前台入口：http://localhost:8081
> - 會員登入：http://localhost:8081/member/login
> - 後台員工登入：http://localhost:8081/admin/login
> - 網站使用的埠號（port）為 **8081**

---

## 測試帳號

### 會員（前台）

| 帳號（Email） | 密碼 |
|---------------|------|
| member@test.com | 123456 |

也可用「會員註冊」頁面自行建立新帳號測試。

### 後台員工

| 角色 | 帳號 | 密碼 | 說明 |
|------|------|------|------|
| 超級管理員 | admin | admin123 | 可使用所有後台功能 |
| 櫃檯人員 | staff | staff123 | 依職務權限顯示部分選單 |
| 房務主管 | housekeeping | hk123456 | 依職務權限顯示部分選單 |

不同角色的後台選單可能不同，這是權限設計的正常現象。

### 優惠券

訂房時可輸入優惠碼：**STAR500**

- 折抵 NT$500
- 訂單金額需滿 NT$3000 才可使用

---

## 建議測試流程

### 前台測試

1. 開啟首頁 http://localhost:8081，瀏覽關於我們、設施、FAQ
2. 到 http://localhost:8081/rooms 查看房型
3. 用會員帳號登入 http://localhost:8081/member/login
4. 進行空房查詢與訂房（可試用優惠券 STAR500）
5. 到商城 http://localhost:8081/shop 加入購物車並結帳
6. 到會員中心與訂房紀錄，確認資料是否正確

### 後台測試

1. 開啟 http://localhost:8081/admin/login
2. 用 `admin` / `admin123` 登入
3. 依序查看儀表板、訂房管理、房型管理、會員管理等頁面
4. 改用 `staff` 帳號登入，比較選單與權限差異

### 線上付款（選用）

若要測試信用卡線上付款，需由開發人員設定 Stripe 測試金鑰。設定完成後，訂房或購物結帳時可選擇「Stripe 信用卡付款」，會跳轉到 Stripe 測試付款頁面。

一般功能測試選擇「臨櫃付款」即可，不需額外設定。

---

## 常見問題

**Q：重新啟動後，之前的訂單不見了？**  
A：示範版使用暫存資料庫，關閉程式後資料會清空，這是正常現象。

**Q：前台某些功能進不去？**  
A：訂房、購物車、會員中心等需先以會員身分登入。

**Q：後台登入後只看到部分功能？**  
A：不同員工角色權限不同，請改用 `admin` 帳號測試完整功能。

**Q：`localhost` 是什麼？**  
A：代表「您自己這台電腦」。只有本機能開啟，其他人無法透過網際網路連進來。

---

## 需要協助時

若啟動失敗、畫面異常或帳號無法登入，請將終端機的錯誤訊息截圖，並告知您正在測試的網址與帳號，交給開發人員協助排查。
