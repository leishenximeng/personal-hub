<script setup>
import { ref } from 'vue'

const message = ref('')
const reply = ref('')
const loading = ref(false)

async function sendMessage() {
    // 防止用户发送空消息
    if (!message.value.trim()) {
        return
    }

    loading.value = true
    reply.value = ''

    try {
        const response = await fetch('/api/chat', {
            method: 'POST',

            headers: {
                'Content-Type': 'application/json',
            },

            body: JSON.stringify({
                message: message.value,
            }),
        })

        const data = await response.json()

        reply.value = data.reply
    } catch (error) {
        console.error('请求失败：', error)
        reply.value = '请求失败，请查看浏览器控制台'
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