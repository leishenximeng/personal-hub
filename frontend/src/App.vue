
<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app">

    <!-- 所有页面共享的顶部导航栏 -->
    <header class="site-header">
      <RouterLink to="/" class="logo">
        Personal Hub
      </RouterLink>

      <nav class="site-nav">
        <RouterLink to="/" class="nav-link">
          Home
        </RouterLink>

          <RouterLink to="/projects" class="nav-link">
    Projects
  </RouterLink>

    <RouterLink to="/articles" class="nav-link">
    Articles
  </RouterLink>

    <RouterLink to="/about" class="nav-link">
    About
  </RouterLink>

        <RouterLink to="/chat" class="nav-link">
          AI Chat
        </RouterLink>
      </nav>
    </header>

    <!-- 根据当前网址显示不同页面 -->
    <RouterView />

  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
}

.site-header {
  height: 72px;
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  background: white;
  border-bottom: 1px solid #eaeaea;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #202124;
  text-decoration: none;
}

.site-nav {
  display: flex;
  align-items: center;
  gap: 28px;
}

.nav-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.nav-link:hover {
  color: #111;
}

/* 当前页面对应的导航链接 */
.nav-link.router-link-exact-active {
  color: #111;
  font-weight: 600;
}
</style>
