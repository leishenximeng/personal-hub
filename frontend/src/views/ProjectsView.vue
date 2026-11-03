

<script setup>
import { onMounted, ref } from 'vue'

// 保存后端返回的项目列表
const projects = ref([])

// 是否正在加载
const loading = ref(true)

// 错误提示
const errorMessage = ref('')

/**
 * 从 Spring Boot 获取项目列表
 */
async function loadProjects() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await fetch('/api/projects')

    if (!response.ok) {
      throw new Error(`加载项目失败：${response.status}`)
    }

    // 后端 JSON → JavaScript 数组
    projects.value = await response.json()

  } catch (error) {
    console.error('加载项目失败：', error)
    errorMessage.value = '项目加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 页面加载后自动查询项目
onMounted(() => {
  loadProjects()
})
</script>


<template>
  <main class="projects-page">
    <div class="container">
      <header class="page-header">
        <p class="eyebrow">MY WORK</p>
        <h1>Projects</h1>
        <p>这里展示我的开发项目、技术实践与学习成果。</p>
      </header>


<!-- 正在加载 -->
<p v-if="loading">正在加载项目...</p>

<!-- 加载失败 -->
<p v-else-if="errorMessage">
  {{ errorMessage }}
</p>

<!-- 没有项目 -->
<p v-else-if="projects.length === 0">
  暂无项目
</p>

<!-- 项目列表 -->
<div v-else class="project-grid">
  <article
    v-for="project in projects"
    :key="project.id"
    class="project-card"
  >
    <span class="status">
      {{ project.status }}
    </span>

    <h2>{{ project.name }}</h2>

    <p class="description">
      {{ project.description }}
    </p>
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
