

<script setup>
import { onMounted, ref } from 'vue'

// 保存后端返回的项目列表
const projects = ref([])

// 是否正在加载
const loading = ref(true)

// 错误提示
const errorMessage = ref('')



const editingProjectId = ref(null)

const editForm = ref({
  name: '',
  description: '',
  status: '',
})

const saving = ref(false)
const editError = ref('')

// 进入编辑模式
function startEdit(project) {
  editingProjectId.value = project.id

  editForm.value = {
    name: project.name,
    description: project.description,
    status: project.status,
  }

  editError.value = ''
}

// 取消编辑
function cancelEdit() {
  editingProjectId.value = null
  editError.value = ''
}

// 保存项目修改
async function saveProjectInfo(projectId) {
  if (saving.value) return

  const name = editForm.value.name.trim()
  const status = editForm.value.status.trim()

  if (!name || !status) {
    editError.value = '项目名称和状态不能为空'
    return
  }

  saving.value = true
  editError.value = ''

  try {
    const response = await fetch(
      `/api/projects/${projectId}/info`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name,
          description: editForm.value.description,
          status,
        }),
      },
    )

    if (!response.ok) {
      throw new Error(`保存失败：${response.status}`)
    }

    const updatedProject = await response.json()

    // 用后端返回的最新项目更新前端数组
    projects.value = projects.value.map((project) =>
      project.id === projectId ? updatedProject : project,
    )

    editingProjectId.value = null
  } catch (error) {
    console.error('修改项目失败：', error)
    editError.value = '保存失败，请检查后端服务'
  } finally {
    saving.value = false
  }
}



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
    <!-- 编辑模式 -->
    <template v-if="editingProjectId === project.id">
      <form
        class="edit-form"
        @submit.prevent="saveProjectInfo(project.id)"
      >
        <label>
          项目名称
          <input v-model="editForm.name" maxlength="120" />
        </label>

        <label>
          项目描述
          <textarea
            v-model="editForm.description"
            rows="3"
          ></textarea>
        </label>

        <label>
          项目状态
          <input v-model="editForm.status" maxlength="30" />
        </label>

        <p v-if="editError" class="edit-error">
          {{ editError }}
        </p>

        <div class="edit-actions">
          <button
            type="button"
            :disabled="saving"
            @click="cancelEdit"
          >
            取消
          </button>

          <button type="submit" :disabled="saving">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </form>
    </template>

    <!-- 普通展示模式 -->
    <template v-else>
      <span class="status">{{ project.status }}</span>

      <h2>{{ project.name }}</h2>

      <p class="description">
        {{ project.description }}
      </p>

      <button
        type="button"
        class="edit-button"
        @click="startEdit(project)"
      >
        编辑项目
      </button>
    </template>


<!-- 项目技术栈 -->
<div
  v-if="project.techStack && project.techStack.length > 0"
  class="tech-stack"
>
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

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-form label {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
}

.edit-form input,
.edit-form textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font: inherit;
}

.edit-actions {
  display: flex;
  gap: 10px;
}

.edit-actions button,
.edit-button {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
}

.edit-actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.edit-error {
  color: #c0392b;
  font-size: 13px;
}

.edit-button {
  margin-top: 12px;
}

</style>
