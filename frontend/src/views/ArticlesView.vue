
<script setup>
// 暂时使用模拟数据，后续接入 Spring Boot
const articles = [
  {
    id: 1,
    title: '理解 Spring Boot 的三层架构',
    summary: '通过 Controller、Service、Repository 理解后端请求的完整流程。',
    category: 'Java',
    status: '待编写',
  },
  {
    id: 2,
    title: 'Vue 3 中的 ref 是什么？',
    summary: '理解 Vue 的响应式状态，以及数据变化如何驱动页面更新。',
    category: 'Vue',
    status: '待编写',
  },
  {
    id: 3,
    title: '从零搭建 AI 聊天应用',
    summary: '记录 Spring Boot、PostgreSQL 与大模型 API 的集成实践。',
    category: 'AI',
    status: '待编写',
  },
]
</script>

<template>
  <main class="articles-page">
    <div class="container">
      <header class="page-header">
        <p class="eyebrow">KNOWLEDGE & WRITING</p>
        <h1>Articles</h1>
        <p>记录学习、技术实践与个人思考。</p>
      </header>

      <div class="article-list">
        <article
          v-for="article in articles"
          :key="article.id"
          class="article-card"
        >
          <div class="article-meta">
            <span>{{ article.category }}</span>
            <span>{{ article.status }}</span>
          </div>

          <h2>{{ article.title }}</h2>
          <p>{{ article.summary }}</p>
        </article>
      </div>
    </div>
  </main>
</template>

<style scoped>
.articles-page {
  min-height: 100vh;
  padding: 80px 24px;
  background: #fafafa;
  color: #202124;
  font-family: Arial, sans-serif;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 48px;
}

.eyebrow {
  color: #888;
  font-size: 12px;
  letter-spacing: 3px;
}

h1 {
  font-size: 48px;
  margin: 16px 0;
}

.page-header > p:last-child {
  color: #666;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.article-card {
  padding: 28px;
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 14px;
}

.article-card h2 {
  margin: 16px 0 12px;
  font-size: 22px;
}

.article-card p {
  color: #666;
  line-height: 1.8;
  font-size: 14px;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  color: #888;
  font-size: 12px;
}
</style>
