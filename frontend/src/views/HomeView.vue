
<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <main class="home">
    <div class="container">



      <!-- 首页主视觉 -->
      <section class="hero">
        <p class="eyebrow">MY DIGITAL SPACE</p>

        <h1>你好，欢迎来到我的数字空间。</h1>

        <p class="description">
          这里记录我的项目、学习笔记和 AI 应用探索。
          一个持续成长的个人网站。
        </p>

        <RouterLink to="/chat" class="primary-button">
          体验 AI 助手 →
        </RouterLink>
      </section>

      <!-- 功能模块 -->
      <section class="modules">
        <h2>Explore</h2>

        <div class="card-grid">
          <article class="card">
            <span class="card-label">01 / PROJECTS</span>
            <h3>项目作品</h3>
            <p>展示我的开发项目、技术实践与工程经验。</p>
            <span class="status">即将上线</span>
          </article>

          <article class="card">
            <span class="card-label">02 / ARTICLES</span>
            <h3>文章与笔记</h3>
            <p>记录学习过程，沉淀知识与思考。</p>
            <span class="status">即将上线</span>
          </article>

          <article class="card">
            <span class="card-label">03 / AI</span>
            <h3>AI Playground</h3>
            <p>探索大模型、RAG 和 Agent 应用。</p>

            <RouterLink to="/chat" class="card-link">
              进入 AI Chat →
            </RouterLink>
          </article>
        </div>
      </section>

      <footer class="footer">
        Personal Hub · Built with Vue & Spring Boot
      </footer>

    </div>
  </main>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: #fafafa;
  color: #202124;
  font-family: Arial, sans-serif;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 32px;
}



/* 首页主视觉 */
.hero {
  padding: 110px 0 120px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 3px;
  color: #777;
  font-weight: 600;
}

h1 {
  max-width: 800px;
  margin: 24px 0;
  font-size: clamp(32px, 5vw, 54px);
  line-height: 1.25;
}

.description {
  max-width: 600px;
  font-size: 17px;
  line-height: 1.9;
  color: #666;
  margin-bottom: 32px;
}

.primary-button {
  display: inline-block;
  padding: 14px 24px;
  background: #202124;
  color: white;
  border-radius: 9px;
  text-decoration: none;
  font-size: 14px;
}

.primary-button:hover {
  background: #444;
}

/* 模块卡片 */
.modules {
  padding-bottom: 100px;
}

.modules h2 {
  margin-bottom: 28px;
  font-size: 25px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.card {
  padding: 28px;
  border: 1px solid #eaeaea;
  border-radius: 14px;
  background: white;
}

.card-label {
  font-size: 11px;
  letter-spacing: 1px;
  color: #999;
}

.card h3 {
  margin: 24px 0 12px;
  font-size: 21px;
}

.card p {
  color: #777;
  line-height: 1.8;
  font-size: 14px;
  min-height: 76px;
}

.status {
  font-size: 12px;
  color: #aaa;
}

.card-link {
  color: #333;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
}

/* 底部 */
.footer {
  padding: 28px 0;
  border-top: 1px solid #eaeaea;
  color: #999;
  font-size: 12px;
}

@media (max-width: 700px) {
  .container {
    padding: 0 20px;
  }

  .hero {
    padding: 70px 0;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
