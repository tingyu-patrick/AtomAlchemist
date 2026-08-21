# CLAUDE.md — Alchemist

個人 side project。NeoForge mod，核心玩法是「物品 → 化學原子 → 物品」的雙向轉換：分解機（Decomposer）把物品拆成原子，合成機（Synthesizer）用原子合成回物品。重點在自訂配方系統與機台邏輯，不是內容堆疊型 mod。

規劃與里程碑見 `PLAN.md`，目前進度見 `README.md`。

## Stack

- Minecraft 1.21.1 / NeoForge 21.1.248 / Java 21（無 Kotlin）
- Build: Gradle + ModDevGradle 2.0.143
- Mappings: Mojang official + Parchment 2024.11.17
- 沒有任何額外 mod 依賴（`dependencies {}` 是空的），沒有 Mixin，沒有 datagen，沒有測試

## 指令

```bash
./gradlew compileJava   # 最快的編譯檢查，改完 code 先跑這個
./gradlew build         # 完整建置，CI 跑的就是這個
./gradlew runClient     # 開發用 client
./gradlew runServer     # 開發用 dedicated server（--nogui）
```

`runGameTestServer` 和 `runData` 的 run config 存在，但沒有對應程式碼，跑了會 crash，不要用。

**完成的定義**：`./gradlew build` 通過。改到遊戲內行為（GUI、機台邏輯、配方）要 `runClient` 實際進遊戲確認。改到 BlockEntity / Menu / 網路相關的東西，額外跑一次 `runServer` 確認不會因為 client class 而 crash。

## 結構

```
src/main/java/com/tingyu/alchemist/
  Alchemist.java          # 進入點，所有 DeferredRegister 在建構子統一 .register(modEventBus)
  AlchemistClient.java    # client-only：@Mod(dist = Dist.CLIENT)，註冊 Screen 與 item color handler
  registry/               # ModBlocks / ModItems / ModBlockEntities / ModMenuTypes /
                          # ModRecipeTypes / ModRecipeSerializers / ModCreativeTabs
  block/                  # DecomposerBlock, SynthesizerBlock
  block/entity/           # 對應 BlockEntity，含 tick、燃料、處理時間邏輯
  item/                   # AtomItem
  menu/                   # AbstractContainerMenu 實作
  recipe/                 # DecomposingRecipe / SynthesizingRecipe 及 Serializer、AtomRecipeInput
  chemistry/              # Element record、Elements 常數表
  client/screen/          # GUI Screen，只能被 AlchemistClient 參照
src/main/resources/
  assets/alchemist/       # blockstates, models, textures, lang
  data/alchemist/         # recipe/, loot_table/
src/main/templates/META-INF/neoforge.mods.toml   # 有 ${} 佔位符，build 時展開
```

Package 是按技術角色分，不是按功能分。新增一台機器會同時動到 block/、block/entity/、menu/、client/screen/、recipe/、registry/ 和 resources。照 Decomposer 或 Synthesizer 的現有模式複製。

## 規則

**註冊**
- 所有 Block / Item / BlockEntity / MenuType / RecipeType / RecipeSerializer 都在 `registry/` 用 DeferredRegister 註冊，不要在別處註冊。
- `ModCreativeTabs` 用 `ModItems.ITEMS.getEntries()` 自動列出所有物品，加物品不需要手動改 creative tab。
- MenuType 用 `IMenuTypeExtension.create(...)` 搭配靜態 `create(...)` 工廠從 client 讀 BlockPos，照現有寫法。

**Client / Server 分離**
- 任何 Screen、render、color handler 相關的 class 只能放在 `client/` 下，且只能被 `AlchemistClient` 參照。
- `Alchemist.java`、Block、BlockEntity、Menu、Recipe 裡不能 import `client/` 下的任何東西，也不能 import `net.minecraft.client.*`。違反會讓 dedicated server crash。

**新增 Block 的完整清單**（缺一個都算沒做完）
1. `assets/alchemist/blockstates/<name>.json`
2. `assets/alchemist/models/block/<name>.json`
3. `assets/alchemist/models/item/<name>.json`（`registerSimpleBlockItem` 不會自動生成，要手寫）
4. `assets/alchemist/textures/block/<name>.png`（16×16）
5. `data/alchemist/loot_table/blocks/<name>.json`（注意路徑是單數 `loot_table`，1.21 之後改的）
6. `assets/alchemist/lang/en_us.json` 加 `block.alchemist.<name>`
7. 有 GUI 的話：`assets/alchemist/textures/gui/<name>.png`

新增 Item 同理：item model、texture、lang key。

**資源與字串**
- 所有玩家看得到的字串走翻譯 key，不 hardcode。
- 目前只有 `en_us.json`。`zh_tw.json` 是 PLAN.md 列的 MVP 品質底線，之後要補；新增 lang key 時如果 `zh_tw.json` 已存在，兩邊一起加。

**Mixin**
- 目前沒有，也不要加。NeoForge 的 event 與 API 足夠。真的遇到非 Mixin 不可的需求，先停下來跟我討論。

**Mappings**
- 用 Mojang official 名稱（`Level`、`BlockPos`、`ItemStack`…）。不確定 vanilla 的 class / method 名稱時，去 IDE 反編譯的原始碼查，不要憑記憶或 Fabric / Yarn 的名字猜。

## 踩過的坑

- **GUI 材質必須是 256×256 畫布，內容放左上角。** vanilla 的簡易 `blit` 假設來源材質是 256×256，尺寸不對會座標錯亂。
- **Item color handler 的回傳值要 `| 0xFF000000`**，否則 alpha 為 0，圖示整個透明。
- **BlockEntity 的 `ItemStackHandler.isItemValid` 不要限制輸出槽**，限制了會連內部塞產物的邏輯都被擋住。
- 改了 `neoforge.mods.toml` 樣板之後要重新 `build`，hot reload 不會生效。

## 工作方式

- 這是 side project，優先簡單直接。不要加「未來可能用到」的抽象層。
- 只做被要求的事。看到其他可以改進或重構的地方，列出來問我，不要直接動。
- 不要自行新增依賴（包含 JEI、Cloth Config 之類的常見 mod）。需要時先說明理由。
- 改動超過一兩個檔案的功能，先說明計畫再動手。
- 非直覺的遊戲機制、workaround 要加註解說明原因；顯而易見的 code 不要加註解。
- Commit message：英文祈使句標題 + 空行 + 說明段落，不加 `feat:` 類前綴。例：

  ```
  Add synthesizer machine with fuel and processing time

  Mirrors the decomposer but consumes atoms to produce items. ...
  ```

## 目前狀態（2026-08 更新）

- **Git 歷史落後於工作目錄。** repo 只有 1 個 commit（分解機、瞬間反應、無燃料），但 working tree 裡已經有整組 Synthesizer、兩台機器的燃料槽與處理時間系統。動手前先 `git status` 看清楚現況，不要以 git log 為準。
- 已完成（未 commit）：Decomposer、Synthesizer、自訂配方型別、燃料/處理時間機制、兩個 GUI。
- 尚未做：`zh_tw.json`、測試、datagen、tags、燒杯等 PLAN.md 裡列的其他物品。
- 下一步與優先順序看 `PLAN.md`。