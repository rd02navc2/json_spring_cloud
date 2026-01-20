<template>
  <div>
    <h1>Vue3 + Spring Cloud Example</h1>
    <input v-model="name" placeholder="Enter your name" />
    <button @click="callBackend">Say Hello</button>
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const name = ref('')
const message = ref('')

const callBackend = async () => {
  try {
    const res = await axios.get('http://localhost:8081/api/hello', { 
      params: { name: name.value } 
    })
    message.value = res.data.message
  } catch (err) {
    message.value = 'Error: ' + err.message
  }
}
</script>
