
<script setup>
// 项目展示数据
const projects = [
  {
    id: 1,
    name: 'Personal Hub',
    description: '基于 Spring Boot、Vue 和 PostgreSQL 的个人网站，集成 AI 对话与知识管理功能。',
    techStack: ['Java', 'Spring Boot', 'Vue 3', 'PostgreSQL'],
    status: '开发中',
  },
  {
    id: 2,
    name: 'COVID Dashboard',
    description: '使用 Python 和 Streamlit 构建的全球疫情数据分析与可视化项目。',
    techStack: ['Python', 'Pandas', 'Streamlit'],
    status: '已完成',
  },
  {
    id: 3,
    name: 'Blog',
    description: '基于 Spring Boot、Vue 和 MySQL 的前后端分离博客项目。',
    techStack: ['Java', 'Vue 3', 'MySQL'],
    status: '基础功能完成',
  },
]
</script>

<template>
  <main class="projects-page">
    <div class="container">
      <header class="page-header">
        <p class="eyebrow">MY WORK</p>
        <h1>Projects</h1>
        <p>这里展示我的开发项目、技术实践与学习成果。</p>
      </header>

      <div class="project-grid">
        <article
          v-for="project in projects"
          :key="project.id"
          class="project-card"
        >
          <span class="status">{{ project.status }}</span>

          <h2>{{ project.name }}</h2>

          <p class="description">
            {{ project.description }}
          </p>

          <div class="tech-stack">
            <span
              v-for="tech in project.techStack"
              :key="tech"
              class="tech-tag"
            >
              {{ tech }}
            </span>
          </div>
        </article>
      </div>
    </div>
  </main>
</template>

<style scoped>
.projects-page {
  min-height: 100vh;
  padding: 80px 24px;
  background: #fafafa;
  color: #202124;
  font-family: Arial, sans-serif;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 48px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 3px;
  color: #888;
}

h1 {
  font-size: 48px;
  margin: 16px 0;
}

.page-header > p:last-child {
  color: #666;
  line-height: 1.8;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.project-card {
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 14px;
  padding: 28px;
}

.project-card h2 {
  margin: 24px 0 12px;
  font-size: 22px;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
  min-height: 100px;
}

.status {
  font-size: 12px;
  color: #888;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 24px;
}

.tech-tag {
  padding: 6px 10px;
  background: #f2f2f2;
  border-radius: 6px;
  font-size: 12px;
  color: #555;
}
</style>
