<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createChart, analyzeChart } from '../api/index.js'

const router = useRouter()
const year = ref('')
const month = ref('')
const day = ref('')
const hour = ref('')
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  error.value = ''
  if (!year.value || !month.value || !day.value) {
    error.value = '请填写完整的出生日期'
    return
  }
  const birthday = `${year.value}-${String(month.value).padStart(2, '0')}-${String(day.value).padStart(2, '0')}`
  const params = { birthday }
  if (hour.value) params.birthTime = `${String(hour.value).padStart(2, '0')}:00`
  loading.value = true
  try {
    await createChart(params)
    const res = await analyzeChart(params)
    const strategy = res.data?.strategy
    const duration = strategy?.recommendDuration || 25
    localStorage.setItem('birthday', birthday)
    router.push({ name: 'strategy', state: { strategy, duration } })
  } catch (_e) {
    error.value = '提交失败，请检查数据后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrapper">
    <div class="login-card">
      <h1 class="logo-title">紫微番茄钟</h1>
      <p class="subtitle">您好，我们需要以下信息</p>
      <div class="form-container">
        <div class="input-row">
          <input type="number" placeholder="生年" v-model="year" class="input-item year-input" />
          <input type="number" placeholder="月" v-model="month" class="input-item month-input" />
          <input type="number" placeholder="日" v-model="day" class="input-item day-input" />
          <input type="text" placeholder="时(可选)" v-model="hour" class="input-item hour-input" />
        </div>
        <div v-if="error" class="error-msg">{{ error }}</div>
        <button class="btn-submit" :disabled="loading" @click="handleSubmit">{{ loading ? '提交中…' : '加 入' }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FAF9F6;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  padding: 20px;
  box-sizing: border-box;
}
.login-card {
  width: 100%;
  max-width: 480px;
  background-color: #FFFFFF;
  padding: 48px 40px;
  border-radius: 20px;
  box-shadow: 0 10px 25px -5px rgba(44, 26, 77, 0.05), 0 8px 10px -6px rgba(44, 26, 77, 0.05);
  border: 1px solid rgba(44, 26, 77, 0.04);
  text-align: center;
  box-sizing: border-box;
}
.logo-title {
  font-size: 28px;
  font-weight: 700;
  color: #2C1A4D;
  margin: 0 0 8px 0;
  letter-spacing: 0.1em;
}
.subtitle {
  font-size: 14px;
  color: #6B7280;
  margin: 0 0 36px 0;
}
.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  box-sizing: border-box;
}
.input-row {
  display: flex;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;
}
.input-item {
  flex: 1;
  min-width: 0;
  height: 48px;
  background-color: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  text-align: center;
  font-size: 14px;
  color: #1F2937;
  outline: none;
  transition: all 0.2s ease-in-out;
  box-sizing: border-box;
}
.year-input { flex: 1.4; }
.hour-input { flex: 1.4; }
.input-item:focus {
  border-color: #2C1A4D;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(44, 26, 77, 0.1);
}
.input-item::-webkit-outer-spin-button,
.input-item::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.input-item[type=number] { -moz-appearance: textfield; }
.error-msg {
  font-size: 13px;
  color: #DC2626;
  text-align: center;
  min-height: 18px;
}
.btn-submit {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #E09F53 0%, #E9B87F 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 6px -1px rgba(224, 159, 83, 0.2);
  transition: all 0.2s ease-in-out;
}
.btn-submit:hover { transform: translateY(-1px); box-shadow: 0 6px 12px -1px rgba(224, 159, 83, 0.3); }
.btn-submit:active { transform: translateY(1px); filter: brightness(0.95); }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
@media (max-width: 480px) {
  .login-card { padding: 32px 20px; }
  .input-row { gap: 8px; }
}
</style>
