<script setup>
import { onMounted, ref } from 'vue'

const message = ref('')
const messages = ref([])
const loading = ref(false)

// 暂时固定使用一个 Conversation
// 后面做“左侧聊天列表”之后，这个 ID 会动态切换
const activeConversationId = ref(3)

async function loadMessages() {
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
  }
}

onMounted(() => {
  loadMessages()
})

async function sendMessage() {
  const userMessage = message.value.trim()

  if (!userMessage || loading.value) {
    return
  }

  loading.value = true

  // 1. 先把用户消息显示到页面
  messages.value.push({
    role: 'user',
    content: userMessage,
  })

  // 2. 清空输入框
  message.value = ''

  // 3. 先创建一个空的 AI 消息
  // 后面的流式内容会不断追加到这里
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

      /*
       * 以前：
       *
       * {
       *   messages: [...]
       * }
       *
       * 现在：
       *
       * {
       *   conversationId: 1,
       *   message: "你好"
       * }
       *
       * 历史消息由后端从 PostgreSQL 查询。
       */
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

    // 获取 HTTP 流读取器
    const reader = response.body.getReader()

    // UTF-8 字节 → JavaScript 字符串
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

      /*
       * SSE 事件之间由空行分隔：
       *
       * data: 你好
       *
       * data: ，很高兴
       *
       */
      const events = buffer.split(/\r?\n\r?\n/)

      // 最后一段可能还没有接收完整
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

        // 每收到一个 chunk，就立即追加到 AI 消息
        messages.value[assistantIndex].content += data
      }
    }
  } catch (error) {
    console.error('请求失败：', error)

    messages.value[assistantIndex].content =
      '请求失败，请查看浏览器控制台'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="page">
    <div class="container">
      <h1>Personal Hub</h1>

      <p class="description">
        My personal digital space.
      </p>

      <div class="messages">
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
        <input v-model="message" type="text" placeholder="输入一句话..." :disabled="loading" @keyup.enter="sendMessage" />

        <button :disabled="loading" @click="sendMessage">
          {{ loading ? '生成中...' : '发送' }}
        </button>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding-top: 120px;
  background: #f7f7f8;
  font-family: Arial, sans-serif;
}

.container {
  width: 600px;
}

h1 {
  margin-bottom: 8px;
  font-size: 36px;
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

input:disabled {
  opacity: 0.7;
}
</style>
