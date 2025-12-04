# 天气应用（Demo3）

一个基于 Android 的城市天气查询与温度趋势展示应用。集成了高德开放平台天气 Web 服务 API，支持快速切换城市、查看当天白天/夜间天气详情以及未来一周的温度趋势。

## 功能概览

- 实时天气：显示当前城市的天气现象、实时温度，以及当天最高/最低温度范围。
- 白天/夜间卡片：分区展示当天白天和夜间的天气、温度、风力。
- 城市快速切换：北京、上海、广州、深圳四个城市，胶囊按钮横向滚动，选中项自动居中。
- 温度趋势：单独页面展示最近几天的白天/夜间温度曲线，支持网格线、平滑曲线、触点高亮与数值提示。
- 视觉样式：渐变背景、圆角卡片、底部容器渐变+阴影的层次化设计。

## 技术栈与实现

- UI：`Activity` + 约束布局/线性布局，胶囊按钮与卡片样式使用 `drawable` 形状资源。
- 网络：`HttpURLConnection` 同步请求，`org.json` 解析响应。
- 交互：`runOnUiThread` 回传 UI，横向滚动城市栏自动居中选中项。
- 自定义视图：`TempChartView` 绘制白天/夜间折线图，支持触摸选点与网格显示。

## 目录与关键代码

- Java 入口与页面：
  - `app/src/main/java/com/example/demo3/MainActivity.java`
    - 读取 API Key：`BuildConfig.AMAP_API_KEY`（`MainActivity.java:119`）
    - 拉取并解析天气：`fetchWeather`（`MainActivity.java:118`）
    - 城市胶囊选中态与居中滚动：`updateCityTab`（`MainActivity.java:93`）
  - `app/src/main/java/com/example/demo3/TrendActivity.java`
    - 未来几天数据截取与渲染列表/折线图
  - `app/src/main/java/com/example/demo3/TempChartView.java`
    - 折线图绘制、网格、平滑曲线与触点高亮
- 布局与样式：
  - `app/src/main/res/layout/activity_main.xml`（首页）
  - `app/src/main/res/layout/activity_trend.xml`（趋势页）
  - `app/src/main/res/drawable/capsule_selected.xml`、`capsule_unselected.xml`（胶囊按钮）
  - `app/src/main/res/drawable/bottom_bg.xml`（底部容器渐变）
  - `app/src/main/res/drawable/bg_gradient.xml`、`card_bg.xml`（背景与卡片）

## 开发环境

- Android Studio（任意近期版本）
- Gradle 构建，`compileSdk`/`targetSdk` 为 36
- 运行：真机或模拟器（需联网）

## 集成高德天气 API（Web 服务）

1. 在高德开放平台创建应用：
   - https://lbs.amap.com/
   - 创建应用后，新增 Key 类型选择“Web 服务”
2. 将 Key 写入本地配置：
   - 在项目根目录的 `local.properties` 中新增：
     ```
     AMAP_API_KEY=你的Web服务Key
     ```
   - 项目已通过 Gradle 注入该值为 `BuildConfig.AMAP_API_KEY`，无需其他改动。
3. 代码读取 Key：
   - `MainActivity` 中通过 `BuildConfig.AMAP_API_KEY` 获取（`app/src/main/java/com/example/demo3/MainActivity.java:119`）。
4. 请求接口（已内置）：
   - 实况天气：`https://restapi.amap.com/v3/weather/weatherInfo?extensions=base&city=<CITY_CODE>&key=<KEY>`
   - 预报天气：`https://restapi.amap.com/v3/weather/weatherInfo?extensions=all&city=<CITY_CODE>&key=<KEY>`
   - 城市代码示例：北京 `110000`、上海 `310000`、广州 `440100`、深圳 `440300`

## 运行与构建

- Android Studio：打开项目 → 同步 Gradle → 选择设备运行
- 命令行：
  ```
  ./gradlew.bat assembleDebug
  ```

## 截图

首页与趋势页示例：

![首页](docs/screenshots/home.png)

![趋势页](docs/screenshots/trend.png)

将你的截图文件命名为 `home.png`、`trend.png` 放到 `docs/screenshots/` 目录即可在 README 中显示。
