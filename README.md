# 病院向けリネン発注数計算アプリ（Spring Boot + MySQL）

病院内で行っているリネン発注数の計算をWebアプリ上で自動化するためのアプリです。  
従来は担当者が在庫数を確認し、手計算した結果を所定の用紙へ記入していましたが、本アプリにより計算ミスの削減・作業時間の短縮を狙います。

---

## 解決したい課題
- 紙運用＋手計算のため、転記ミス・計算ミスが起きやすい
- 施設ごとに取扱商品や基準在庫（定数）が異なり、管理が手間
- 次回納品数を前回発注実績から自動入力したい（初回は0）

---

## 主な機能
- ログイン（施設ID・パスワード）
- ダッシュボード
- 基準在庫（定数）設定（施設×アイテム単位で変更）
- 発注計算（現在庫入力 → 発注数自動計算）
  - 次回納品数は前回発注履歴から自動入力（初回は0）
  - 例外的に納品数が変動する場合に備え、画面上で編集可能
- 発注確定（DB保存）
- 発注履歴一覧／発注詳細
- 発注データCSV出力

---

## 画面遷移（使い方）
1. `/login` でログイン（施設ID・パスワード）
2. `/dashboard` から各機能へ遷移
3. 必要に応じて `/facility/settings` で基準在庫（定数）を設定
4. `/order` で現在庫数を入力し「一括計算」
5. 計算結果確認後「確定」で発注データを保存
6. `/orders/history` で履歴を確認（詳細・CSV出力も可能）

---

## URL一覧（ルーティング）
- `GET  /login`：ログイン画面
- `POST /login`：ログイン処理
- `GET  /dashboard`：ダッシュボード
- `GET  /facility/settings`：基準在庫（定数）設定画面
- `POST /facility/settings`：基準在庫更新
- `GET  /order`：発注計算画面
- `POST /order/calculate`：発注数計算
- `POST /order/confirm`：発注確定（DB保存）
- `GET  /orders/history`：発注履歴一覧
- `GET  /orders/{orderId}`：発注詳細
- `GET  /orders/{orderId}/csv`：CSV出力

---

## 計算ロジック（概要）
入力：
- 現在庫数
- 次回納品数（初期値は前回発注数の半分、必要に応じて編集可）
- 基準在庫（定数）

出力：
- 発注数（上限あり）

※実装上は orderQuantities = min(Y * 2, 定数) のように上限を持たせています。  
（発注数が定数を超える場合は定数で頭打ち）

---

## DBテーブル
- `facility`（施設）
- `users`（ユーザー）
- `linen_item`（アイテム）
- `facility_linen`（施設×アイテムの基準在庫）
- `order_header`（発注ヘッダ）
- `order_detail`（発注明細）

---

## 使用技術
- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Thymeleaf
- MySQL

---

## 起動方法（ローカル）
### 1) MySQLを起動しDB作成
```sql
CREATE DATABASE linen_order_app;
