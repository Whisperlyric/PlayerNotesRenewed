# Player Notes Renewed

## Features

### Multi-List Management
- Create, edit, and delete multiple player lists
- Each list can have its own prefix with custom colors and styles
- Players can be in multiple lists simultaneously
- Prefix display order follows the list order in the GUI

### GUI Interface
- Access via **keybind** (configurable in Controls settings) or **ModMenu**
- Add/remove players to lists even when not connected to a server
- View player online status: **[Online]**, **[Offline]**, or **[Undefined]**
- Refresh player status manually or auto-update when joining a world

### Prefix Formatting
- Use `&` instead of `§` for formatting codes
- Example: `&2&l[VIP]` = Dark green bold `[VIP]`
- Common colors: `&0`-`&f`, `&a`-`&f` for bright colors
- Styles: `&l` (bold), `&o` (italic), `&n` (underline), `&m` (strikethrough), `&k` (obfuscated), `&r` (reset)

### Display Options
- Show prefix above player **name tags** (toggleable in config)
- Show prefix in **tab list** with priority sorting (toggleable in config)

## Usage

1. Press the configured keybind (default: none) or open via ModMenu
2. Click **"Add List"** to create a new player list
3. Enter list name and prefix (e.g., `&c&l[Admin]`)
4. Click **"Edit"** to manage players in the list
5. Add players by name (UUID will be resolved when possible)
6. Use **"Refresh Status"** to update online status

## Privacy

Your player lists are stored locally and not sent to any server.

## Dependencies

- [Fabric API](https://modrinth.com/mod/fabric-api) (required)
- [ModMenu](https://modrinth.com/mod/modmenu) (optional, for config GUI access)

---

# Player Notes Renewed (中文)

## 功能

### 多列表管理
- 创建、编辑和删除多个玩家列表
- 每个列表可以有自己的前缀，支持自定义颜色和样式
- 同一玩家可以同时存在于多个列表中
- 前缀显示顺序遵循界面中的列表顺序

### GUI 界面
- 通过**快捷键**（可在控制设置中配置）或 **ModMenu** 访问
- 即使未连接服务器也可以添加/移除玩家
- 查看玩家在线状态：**[在线]**、**[离线]** 或 **[未定义]**
- 手动刷新玩家状态或加入世界时自动更新

### 前缀格式
- 使用 `&` 代替 `§` 作为格式化代码
- 示例：`&2&l[VIP]` = 深绿色粗体 `[VIP]`
- 常用颜色：`&0`-`&f`，`&a`-`&f` 为亮色系
- 样式：`&l`（粗体）、`&o`（斜体）、`&n`（下划线）、`&m`（删除线）、`&k`（随机字符）、`&r`（重置）

### 显示选项
- 在玩家**名称标签**上方显示前缀（可在配置中切换）
- 在**Tab 列表**中显示前缀并按优先级排序（可在配置中切换）

## 使用方法

1. 按下配置的快捷键（默认：无）或通过 ModMenu 打开
2. 点击**"添加列表"**创建新的玩家列表
3. 输入列表名称和前缀（如 `&c&l[管理员]`）
4. 点击**"编辑"**管理列表中的玩家
5. 通过名称添加玩家（尽可能解析 UUID）
6. 使用**"刷新状态"**更新在线状态

## 隐私

你的玩家列表存储在本地，不会发送到任何服务器。

## 依赖

- [Fabric API](https://modrinth.com/mod/fabric-api)（必需）
- [ModMenu](https://modrinth.com/mod/modmenu)（可选，用于访问配置界面）

---

## Original Project / 原项目

This project is a renewed version of [Player Notes](https://modrinth.com/project/player-notes) by LucasEDVK.

本项目是 LucasEDVK 开发的 [Player Notes](https://modrinth.com/project/player-notes) 的重制版本。

**Original Author / 原作者:** LucasEDVK (aka LucimannHD)
- Discord: https://www.lucasedvk.de/discord
- Modrinth: https://modrinth.com/project/player-notes

**Special Thanks / 特别感谢:**
- Motschen for providing the awesome config library (MidnightLib)
- https://modrinth.com/user/Motschen

---

## License / 许可证

This project is licensed under [CC BY-SA 4.0](LICENSE.txt).

本项目采用 [CC BY-SA 4.0](LICENSE.txt) 许可证。

**You are free to / 你可以自由地：**
- **Share** — copy and redistribute the material in any medium or format / **分享** — 以任何媒介或格式复制、发行本作品
- **Adapt** — remix, transform, and build upon the material for any purpose / **改编** — 依据任何目的，混合、转换或基于本作品进行创作

**Under the following terms / 惟须遵守下列条件：**
- **Attribution** — You must give appropriate credit to the original author / **署名** — 您必须给出适当的署名，注明原作者
- **ShareAlike** — If you remix, transform, or build upon the material, you must distribute your contributions under the same license / **相同方式共享** — 如果您再混合、转换或基于本作品进行创作，您必须以相同的许可证分发您的贡献

See [LICENSE.txt](LICENSE.txt) for full license text. / 完整许可证文本请见 [LICENSE.txt](LICENSE.txt)。
