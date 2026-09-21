<script setup>
import { ref } from 'vue'

// const message = ref('')
// const reply = ref('')
const message = ref('')
const messages = ref([])
const loading = ref(false)

async function sendMessage() {
  const userMessage = message.value.trim()

  if (!userMessage || loading.value) {
    return
  }

  loading.value = true

  // 1. 先把用户消息加入聊天记录
  messages.value.push({
    role: 'user',
    content: userMessage,
  })

  // 2. 清空输入框
  message.value = ''

  /*
   * 3. 保存这一次真正发给后端的历史
   *
   * 注意：
   * 此时还没有加入空的 assistant 消息。
   */
  const requestMessages = messages.value.map((item) => ({
    role: item.role,
    content: item.content,
  }))

  /*
   * 4. 先在页面创建一个空的 AI 消息。
   *
   * 后面流式 chunk 会不断追加进去。
   */
  messages.value.push({
    role: 'assistant',
    content: '',
  })

  const assistantIndex =
    messages.value.length - 1

  try {
    const response = await fetch(
      '/api/chat/stream',
      {
        method: 'POST',

        headers: {
          'Content-Type': 'application/json',
          Accept: 'text/event-stream',
        },

        body: JSON.stringify({
          messages: requestMessages,
        }),
      },
    )

    if (!response.ok) {
      throw new Error(
        `请求失败：${response.status}`,
      )
    }

    if (!response.body) {
      throw new Error(
        '浏览器无法读取流式响应',
      )
    }

    const reader =
      response.body.getReader()

    const decoder =
      new TextDecoder('utf-8')

    let buffer = ''

    while (true) {
      const { value, done } =
        await reader.read()

      if (done) {
        break
      }

      buffer += decoder.decode(
        value,
        {
          stream: true,
        },
      )

      const events =
        buffer.split(/\r?\n\r?\n/)

      buffer = events.pop() ?? ''

      for (const event of events) {
        const lines =
          event.split(/\r?\n/)

        const dataLines = lines
          .filter((line) =>
            line.startsWith('data:'),
          )
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

        const data =
          dataLines.join('\n')

        if (data === '[DONE]') {
          continue
        }

        /*
         * 最关键：
         *
         * 把每一个 chunk
         * 加到当前 assistant 消息上。
         */
        messages.value[
          assistantIndex
        ].content += data
      }
    }
  } catch (error) {
    console.error(
      '请求失败：',
      error,
    )

    messages.value[
      assistantIndex
    ].content =
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
</style>
