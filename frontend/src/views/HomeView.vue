<script setup>
import { onMounted, ref } from 'vue'

const message = ref('')
const messages = ref([])
const conversations = ref([])

const activeConversationId = ref(null)

const loading = ref(false)
const loadingMessages = ref(false)

/**
 * 查询所有历史对话
 */
async function loadConversations() {
  try {
    const response = await fetch('/api/conversations')

    if (!response.ok) {
      throw new Error(`加载对话列表失败：${response.status}`)
    }

    conversations.value = await response.json()
  } catch (error) {
    console.error('加载对话列表失败：', error)
  }
}

async function createConversation() {
  if (loading.value) {
    return
  }

  try {
    const response = await fetch('/api/conversations', {
      method: 'POST',

      headers: {
        'Content-Type': 'application/json',
      },

      body: JSON.stringify({
        title: '新对话',
      }),
    })

    if (!response.ok) {
      throw new Error(`创建对话失败：${response.status}`)
    }

    const conversation = await response.json()

    // 把新对话加到左侧列表
    conversations.value.push(conversation)

    // 自动切换到新对话
    activeConversationId.value = conversation.id

    // 新对话目前没有任何消息
    messages.value = []
  } catch (error) {
    console.error('创建对话失败：', error)
  }
}

/**
 * 根据当前 conversationId
 * 从数据库加载聊天记录
 */
async function loadMessages() {
  if (!activeConversationId.value) {
    messages.value = []
    return
  }

  loadingMessages.value = true

  try {
    const response = await fetch(
      `/api/conversations/${activeConversationId.value}/messages`,
    )

    if (!response.ok) {
      throw new Error(`加载聊天记录失败：${response.status}`)
    }

    const data = await response.json()

    messages.value = data.map((item) => ({
      role: item.role.toLowerCase(),
      content: item.content,
    }))
  } catch (error) {
    console.error('加载聊天记录失败：', error)
  } finally {
    loadingMessages.value = false
  }
}

/**
 * 点击左侧某一场聊天
 */
async function switchConversation(conversationId) {
  if (loading.value) {
    return
  }

  if (activeConversationId.value === conversationId) {
    return
  }

  activeConversationId.value = conversationId

  await loadMessages()
}

/**
 * 发送消息
 */
async function sendMessage() {
  const userMessage = message.value.trim()

  if (!userMessage || loading.value) {
    return
  }

  if (!activeConversationId.value) {
    console.error('当前没有选中的对话')
    return
  }

  loading.value = true

  // 先显示用户消息
  messages.value.push({
    role: 'user',
    content: userMessage,
  })

  message.value = ''

  // 创建一个空的 AI 消息
  messages.value.push({
    role: 'assistant',
    content: '',
  })

  const assistantIndex = messages.value.length - 1

  try {
    const response = await fetch('/api/chat/stream', {
      method: 'POST',

      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
      },

      body: JSON.stringify({
        conversationId: activeConversationId.value,
        message: userMessage,
      }),
    })

    if (!response.ok) {
      throw new Error(`请求失败：${response.status}`)
    }

    if (!response.body) {
      throw new Error('浏览器无法读取流式响应')
    }

    const reader = response.body.getReader()

    const decoder = new TextDecoder('utf-8')

    let buffer = ''

    while (true) {
      const { value, done } = await reader.read()

      if (done) {
        break
      }

      buffer += decoder.decode(value, {
        stream: true,
      })

      const events = buffer.split(/\r?\n\r?\n/)

      buffer = events.pop() ?? ''

      for (const event of events) {
        const lines = event.split(/\r?\n/)

        const dataLines = lines
          .filter((line) => line.startsWith('data:'))
          .map((line) => {
            let data = line.slice(5)

            if (data.startsWith(' ')) {
              data = data.slice(1)
            }

            return data
          })

        if (dataLines.length === 0) {
          continue
        }

        const data = dataLines.join('\n')

        if (data === '[DONE]') {
          continue
        }

        messages.value[assistantIndex].content += data
      }
    }
    // ↓ 新增这一句
    // AI回答完成后，重新获取左侧对话列表
    await loadConversations()
  } catch (error) {
    console.error('请求失败：', error)

    messages.value[assistantIndex].content =
      '请求失败，请查看浏览器控制台'
  } finally {
    loading.value = false
  }
}

/**
 * 页面第一次打开时：
 *
 * 1. 查询所有 Conversation
 * 2. 自动选中第一条
 * 3. 加载第一条 Conversation 的消息
 */
onMounted(async () => {
  await loadConversations()

  if (conversations.value.length > 0) {
    activeConversationId.value =
      conversations.value[0].id

    await loadMessages()
  }
})
</script>

<template>
  <main class="page">

    <!-- 左侧对话列表 -->
    <aside class="sidebar">
      <h2>Personal Hub</h2>

      <button class="new-chat-button" :disabled="loading" @click="createConversation">
        + 新建对话
      </button>

      <div class="conversation-list">

        <button v-for="conversation in conversations" :key="conversation.id" class="conversation-item" :class="{
          active:
            conversation.id === activeConversationId,
        }" @click="switchConversation(conversation.id)">
          {{ conversation.title }}
        </button>

      </div>
    </aside>

    <!-- 右侧聊天区域 -->
    <section class="chat-area">

      <div class="chat-container">

        <h1>AI Assistant</h1>

        <p class="description">
          My personal digital space.
        </p>

        <div v-if="loadingMessages" class="empty-state">
          正在加载聊天记录...
        </div>

        <div v-else-if="messages.length === 0" class="empty-state">
          暂无聊天记录
        </div>

        <div v-else class="messages">
          <div v-for="(item, index) in messages" :key="index" class="message" :class="item.role">

            <strong>
              {{ item.role === 'user' ? 'You' : 'AI' }}
            </strong>

            <div>
              {{ item.content }}
            </div>

          </div>
        </div>

        <div class="chat-box">

          <input v-model="message" type="text" placeholder="输入一句话..." :disabled="loading || !activeConversationId"
            @keyup.enter="sendMessage" />

          <button :disabled="loading ||
            !activeConversationId
            " @click="sendMessage">
            {{ loading ? '生成中...' : '发送' }}
          </button>

        </div>

      </div>

    </section>

  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  background: #f7f7f8;
  font-family: Arial, sans-serif;
}

/* 左侧 */

.sidebar {
  width: 260px;
  padding: 24px 16px;
  background: white;
  border-right: 1px solid #e5e5e5;
}

.sidebar h2 {
  margin: 0 0 24px;
  padding: 0 10px;
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.conversation-item {
  width: 100%;
  padding: 12px 14px;
  border: none;
  border-radius: 8px;
  background: transparent;
  text-align: left;
  cursor: pointer;
}

.conversation-item:hover {
  background: #f3f3f3;
}

.conversation-item.active {
  background: #e9e9ea;
}

/* 右侧 */

.chat-area {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 60px 40px;
}

.chat-container {
  width: 700px;
}

h1 {
  margin-bottom: 8px;
  font-size: 32px;
}

.description {
  margin-bottom: 32px;
  color: #666;
}

.messages {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.message {
  padding: 14px 16px;
  border-radius: 10px;
  line-height: 1.6;
}

.message.user {
  background: #e9e9ea;
}

.message.assistant {
  background: white;
}

.message strong {
  display: block;
  margin-bottom: 6px;
}

.empty-state {
  margin: 40px 0;
  color: #999;
  text-align: center;
}

.chat-box {
  display: flex;
  gap: 12px;
}

input {
  flex: 1;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

button {
  padding: 0 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>

.new-chat-button {
width: 100%;
margin-bottom: 18px;
padding: 12px 14px;
border: 1px solid #ddd;
border-radius: 8px;
background: white;
text-align: left;
cursor: pointer;
font-size: 14px;
}

.new-chat-button:hover {
background: #f5f5f5;
}
