# PlayerNotesRenewed TODO

## v1.0.7 - 聊天前后缀支持

### 功能描述
在聊天消息中显示玩家的前缀和后缀

### 当前状态
- [ ] 聊天前后缀显示功能未完成
- [ ] 需要研究正确的 Mixin 注入方式

### 已尝试的方案
1. 注入 `ChatComponent.addMessage(Component)` - 未生效
2. 注入 `ChatComponent.addMessage(Component, MessageSignature, GuiMessageTag)` - 未生效
3. 注入 `ChatListener.method_45745` - 未生效
4. 使用 `@ModifyVariable` - 未生效
5. 使用 `@Inject` + `cancellable` - 未生效

### 技术难点
- chat-heads 模组已经注入了聊天消息处理
- 需要在 chat-heads 之后执行修改
- Mixin 优先级设置可能不够

### 相关文件
- `src/main/java/dev/wsplrc/playernotesrenewed/client/mixin/ChangeChat.java`
- `src/main/java/dev/wsplrc/playernotesrenewed/client/config/Config.java` (showPrefixInChat, showSuffixInChat, styleAffectPlayerName)

### 参考
- chat-heads 的 ChatListenerMixin.java 实现方式

---

## 已完成版本

### v1.0.6
- [x] 把新增内容补充进帮助中
- [x] 为优先级输入框添加文字提示

### v1.0.5
- [x] 复制玩家列表功能
- [x] 后缀功能
- [x] 前后缀末尾重置代码切换
- [x] 优先级配置
- [ ] 聊天栏显示前后缀 (移至 v1.0.7)
- [ ] 前后缀样式影响玩家名字 (移至 v1.0.7)

### v1.0.4
- [x] 每15秒自动更新玩家在线状态
- [x] 玩家重复添加检测
- [x] 列表名称重复检测
- [x] 性能优化（仅在状态变化时保存）

### v1.0.3
- [x] 修复新列表取消后仍创建的问题
- [x] 修复帮助界面返回后输入框内容丢失的问题

### v1.0.2
- [x] 修复界面尺寸过大问题
- [x] 修复滚动条无法拖动问题
- [x] 修复多前缀格式冲突问题

### v1.0.1
- [x] 修复 Velocity 群组服服务器前缀显示顺序问题
