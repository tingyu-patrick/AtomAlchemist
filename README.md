# Alchemist

一個 Minecraft mod:把物品分解成「原子」(依真實元素週期表),再依真實化學組合把原子重組成其他物品。長期目標是加入化學實驗器材(燒杯、NMR 等)作為機台外觀與功能載體。

- **平台**:NeoForge
- **版本**:Minecraft 1.21.1
- **開發模式**:單人開發,先做可玩雛形(MVP)再逐步打磨

完整的專案規劃(MVP 範圍、任務拆解、里程碑時程、風險評估)見 [PLAN.md](PLAN.md)。

## 目前進度

MVP 開發中,「鐵錠 → 分解機 → 9×Fe 原子 → 合成機 → 鐵錠」這條最短完整循環的前半段已打通。

- [x] **B1** 註冊骨架 — DeferredRegister(Block/Item/BlockEntity/Menu/RecipeType)
- [x] **B2** 原子物品 — H、C、O、Fe 四種元素,程序化上色材質(依 Jmol/CPK 配色),tooltip 顯示符號與原子序
- [x] **B3** 分解機 — 方塊 + BlockEntity + Menu + Screen,含存讀檔與 GUI 同步
- [x] **B4** 分解配方系統 — 自訂 `RecipeType`/`RecipeSerializer`,配方走 datapack JSON(`data/alchemist/recipe/decomposing/`),已有鐵錠、糖兩筆配方
- [ ] **B5** 合成機 — 重用分解機架構,原子 → 物品(進行中)
- [ ] **B6** 打通完整循環並驗證(M1 里程碑)

## 已實作的核心機制

- **原子系統**:元素定義(符號/原子序/顏色)採資料驅動(`chemistry/Elements.java`),擴充新元素不需改動機台邏輯
- **分解機**(`Decomposer`):1 個輸入槽 + 4 個輸出槽,放入支援的物品後依配方瞬間分解成對應原子;輸出槽對玩家唯讀(僅機器可寫入),配方比對透過 `RecipeManager` 查詢
- **配方格式**:

  ```json
  {
    "type": "alchemist:decomposing",
    "input": { "item": "minecraft:sugar" },
    "outputs": [
      { "id": "alchemist:atom_carbon", "count": 12 },
      { "id": "alchemist:atom_hydrogen", "count": 22 },
      { "id": "alchemist:atom_oxygen", "count": 11 }
    ]
  }
  ```

## 開發環境

- JDK 21
- IntelliJ IDEA(或其他支援 Gradle 的 IDE)
- NeoForge MDK(本專案基於官方 MDK 範本建立)

### 建置與執行

```bash
./gradlew compileJava   # 編譯檢查
./gradlew runClient     # 啟動開發用 client
```

## 專案結構

```
src/main/java/com/tingyu/alchemist/
├── chemistry/       元素資料(Element、Elements)
├── item/             AtomItem(原子物品)
├── block/            DecomposerBlock
│   └── entity/        DecomposerBlockEntity
├── menu/             DecomposerMenu
├── recipe/           DecomposingRecipe / DecomposingRecipeSerializer
├── registry/         各類 DeferredRegister(Block/Item/BlockEntity/Menu/RecipeType/RecipeSerializer/CreativeTab)
├── client/screen/     DecomposerScreen
├── Alchemist.java     mod 主類
├── AlchemistClient.java  client-only 事件(材質上色、GUI 螢幕註冊)
└── Config.java        mod 設定(目前為空殼,待後續擴充)

src/main/resources/
├── assets/alchemist/  材質、模型、語言檔
└── data/alchemist/    配方、戰利品表
```
