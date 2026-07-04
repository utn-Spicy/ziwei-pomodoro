# ziwei-pomodoro
紫微番茄钟 · AI 个性化学习教练
 # 紫微番茄钟 · AI 个性化学习教练

  > 结合紫微斗数与番茄工作法，打造 AI 驱动的个性化时间管理与学习辅助系统。

## 致谢

  - 紫微斗数排盘算法参考了 [iztro](https://github.com/SylarLong/iztro) 开源项目

  ## 技术栈

  **后端：** Java 17 · Spring Boot · MyBatis · MySQL · Redis · RESTful API
  
  **前端：** Vue 3 · Vite · Axios
  
  **AI：** DeepSeek API · 命理语录生成
  
  **开发方式：**  
  - 后端架构与业务代码：独立开发，AI辅助代码优化
  - 前端页面实现：AI 主力生成，作者主导接口联调与交互逻辑
  - AI 能力集成：后端统一封装 DeepSeek API，支撑命理语录场景

  ## 功能演示

  | 页面 | 截图 |
  |------|------|
  | 生辰录入页 | <img width="1590" height="862" alt="image" src="https://github.com/user-attachments/assets/53068940-3bd6-4c33-a557-46161cdfeaa2" /> |
  | 命盘策略页 | <img width="1528" height="1103" alt="image" src="https://github.com/user-attachments/assets/4cbb18c1-59d8-45a6-8bb9-ff2cd06c1f91" /> |
  | 专注计时页 | <img width="1807" height="1129" alt="image" src="https://github.com/user-attachments/assets/5fdc3d46-7bf4-4822-901f-dc8569b12dbf" /> |
  | 休息页 | <img width="1604" height="912" alt="image" src="https://github.com/user-attachments/assets/60761212-2d82-458f-a05c-1af683103764" /> |
  | AI语录页 | <img width="1735" height="952" alt="image" src="https://github.com/user-attachments/assets/007f72b5-8e2f-4349-82cb-ba32774c3a27" /> |


  ## 项目架构

    用户请求 → 前端（Vue 3） → 后端 API（Spring Boot） → 数据库（MySQL）
                                          ↓
                                    AI 服务（DeepSeek） 

  ## 核心功能

  - 生辰输入 → 排盘引擎 → 命盘分析 + 番茄钟策略推荐
  - AI 驱动的专注语录（完成/中断/休息三种场景，贴合命理风格）
  - 番茄钟计时（自动开始/暂停/长按中断/页面刷新恢复）
  - 今日专注记录看板
  - 新国风 UI 设计（暖沙色调 + 深黛紫 + 古铜金点缀）

  ## 快速启动

  ### 后端

  ```bash
  # 1. 导入项目到 IDEA，等待 Maven 依赖下载
  # 2. 在 MySQL 中创建数据库 ziwei_pomodoro，执行项目中的建表 SQL
  # 3. 设置环境变量
  export DEEPSEEK_API_KEY=你的key
  # 4. 运行 PomodoroApplication.java
  # 5. 访问 http://localhost:8080/doc.html 查看 Swagger 接口文档

  前端

  cd frontend
  npm install
  npm run dev
  # 访问 http://localhost:5173

  Vibe Coding 说明


  - 前端页面由 AI 根据设计稿生成 Vue 组件，作者负责接口联调与数据流校验
  - AI API 集成部分由后端服务统一封装
  - 开发过程中充分利用 AI 工具解决实际问题，同时对 AI 生成代码进行人工复查与优化

  项目规划

  - [x] MVP 核心闭环（登录 → 策略 → 专注 → 休息）
  - [x] AI 命理语录
  - [ ] Agent 调度机制（动态调整专注策略）
  - [ ] 多用户支持
  - [ ] 数据统计看板
