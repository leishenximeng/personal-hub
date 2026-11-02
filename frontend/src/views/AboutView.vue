
<script setup>
const skills = [
  'Java',
  'Spring Boot',
  'Python',
  'Vue 3',
  'PostgreSQL',
  'MySQL',
  'Docker',
  'Git',
]

const interests = [
  {
    title: 'Backend Development',
    description: '使用 Java 和 Spring Boot 构建后端服务，探索系统设计与数据管理。',
  },
  {
    title: 'AI Applications',
    description: '探索大模型 API、RAG、Agent，以及 AI 与实际业务的结合。',
  },
  {
    title: 'Frontend Development',
    description: '使用 Vue 3 开发现代 Web 应用，持续改善产品交互与用户体验。',
  },
]
</script>

<template>
  <main class="about-page">
    <div class="container">

      <section class="intro">
        <p class="eyebrow">A LITTLE ABOUT ME</p>

        <h1>About Me</h1>

        <p class="intro-text">
          你好！欢迎来到我的个人网站。
        </p>

        <p class="intro-text">
          我对软件开发和人工智能应用充满兴趣，
          正在通过实际项目不断学习后端开发、前端技术与 AI 工程。
        </p>

        <p class="intro-text">
          这个网站既是我的个人数字空间，
          也是记录技术成长、展示项目和探索新想法的地方。
        </p>
      </section>

      <section class="section">
        <h2>Tech Stack</h2>

        <div class="skill-list">
          <span
            v-for="skill in skills"
            :key="skill"
            class="skill-tag"
          >
            {{ skill }}
          </span>
        </div>
      </section>

      <section class="section">
        <h2>What I'm Exploring</h2>

        <div class="interest-list">
          <article
            v-for="interest in interests"
            :key="interest.title"
            class="interest-card"
          >
            <h3>{{ interest.title }}</h3>
            <p>{{ interest.description }}</p>
          </article>
        </div>
      </section>

    </div>
  </main>
</template>

<style scoped>
.about-page {
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

.intro {
  margin-bottom: 70px;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 3px;
  color: #888;
}

h1 {
  font-size: 48px;
  margin: 16px 0 32px;
}

.intro-text {
  font-size: 16px;
  line-height: 2;
  color: #666;
  margin-bottom: 16px;
}

.section {
  margin-bottom: 70px;
}

.section h2 {
  font-size: 26px;
  margin-bottom: 24px;
}

.skill-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.skill-tag {
  padding: 10px 16px;
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  font-size: 14px;
}

.interest-list {
  display: grid;
  gap: 16px;
}

.interest-card {
  padding: 24px;
  background: white;
  border: 1px solid #eaeaea;
  border-radius: 12px;
}

.interest-card h3 {
  font-size: 18px;
  margin: 0 0 12px;
}

.interest-card p {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
  margin: 0;
}

@media (max-width: 600px) {
  .about-page {
    padding: 48px 20px;
  }

  h1 {
    font-size: 36px;
  }
}
</style>
