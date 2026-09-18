<script setup>
import { ref } from 'vue'

const message = ref('')
const reply = ref('')
const loading = ref(false)

async function sendMessage() {
  if (!message.value.trim()) {
    return
  }

  loading.value = true
  reply.value = ''

  try {
    const response = await fetch('/api/chat/stream', {
      method: 'POST',

      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
      },

      body: JSON.stringify({
        message: message.value,
      }),
    })

    if (!response.ok) {
      throw new Error(`请求失败：${response.status}`)
    }

    if (!response.body) {
      throw new Error('浏览器无法读取流式响应')
    }

    // 从 HTTP Response 中取得“数据流读取器”
    const reader = response.body.getReader()

    // 把 UTF-8 字节转换成 JavaScript 字符串
    const decoder = new TextDecoder('utf-8')

    let buffer = ''

    while (true) {
      const { value, done } = await reader.read()

      if (done) {
        break
      }

      // 一批新的字节到达
      buffer += decoder.decode(value, {
        stream: true,
      })

      /*
       * SSE 每一个事件之间有一个空行：
       *
       * data: xxx
       *
       * data: xxx
       *
       */
      const events = buffer.split(/\r?\n\r?\n/)

      // 最后一部分可能还没传完整
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

        // 最关键的一行：
        // 每收到一段，就立即加到页面上
        reply.value += data
      }
    }
  } catch (error) {
    console.error('请求失败：', error)

    reply.value =
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

      <div class="chat-box">
        <input v-model="message" type="text" placeholder="输入一句话..." @keyup.enter="sendMessage" />

        <button :disabled="loading" @click="sendMessage">
          {{ loading ? '发送中...' : '发送' }}
        </button>
      </div>

      <div v-if="reply" class="reply">
        {{ reply }}
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

.reply {
  margin-top: 24px;
  padding: 16px;
  background: white;
  border-radius: 8px;
}
</style>
