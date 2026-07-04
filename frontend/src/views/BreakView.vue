<template>
  <div class="page-wrapper">
    <div class="break-card">
      <h2 class="page-title">{{ isRunning ? '安心小憩中...' : '稍作休息' }}</h2>

      <div class="timer-display-box" :class="{ 'timer-active': isRunning }">
        <span class="timer-text">{{ formattedTime }}</span>
      </div>

      <div class="actions-row">
        <button class="btn-action btn-outline" @click="handleSkip">跳 过</button>
        <button class="btn-action btn-gold" :disabled="isRunning" @click="handleStartRest">
          {{ isRunning ? '休息中' : '休 息' }}
        </button>
      </div>

      <div class="records-wrapper">
        <button class="btn-toggle-records" @click="handleToggleRecords">
          <span>今日记录</span>
          <span class="arrow-icon" :class="{ 'arrow-up': showRecords }">▼</span>
        </button>

        <Transition name="slide-fade">
          <div v-if="showRecords" class="records-content">
            <div v-if="todayRecords.length === 0" class="records-empty">
              今日尚未有完成的专注记录
            </div>
            <ul v-else class="records-list">
              <li v-for="record in todayRecords" :key="record.id" class="record-item">
                <span class="record-time">专注 {{ record.duration }} 分钟</span>
                <span class="record-status" :class="record.status === 1 ? 'status-done' : 'status-failed'">
                  {{ record.status === 1 ? '已达成' : '已中断' }}
                </span>
              </li>
            </ul>
          </div>
        </Transition>
      </div>
    </div>

    <Transition name="fade">
      <div v-if="showOverlay" class="quote-overlay">
        <div class="quote-card">
          <p class="quote-text">{{ quoteMessage }}</p>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTodayRecords, getSuggestion } from '../api/index.js'

const router = useRouter()

const defaultRestTime = 5 * 60
const timeLeft = ref(defaultRestTime)
const isRunning = ref(false)
const showRecords = ref(false)
const todayRecords = ref([])

const showOverlay = ref(false)
const quoteMessage = ref('')

let timerInterval = null

const formattedTime = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60)
  const seconds = timeLeft.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

const handleStartRest = () => {
  if (isRunning.value) return
  isRunning.value = true
  timerInterval = setInterval(() => {
    if (timeLeft.value > 0) timeLeft.value--
    else { clearTimer(); handleRedirect() }
  }, 1000)
}

const handleSkip = () => {
  clearTimer()
  handleRedirect()
}

const handleRedirect = async () => {
  const birthday = localStorage.getItem('birthday') || '1995-01-01'
  try {
    const res = await getSuggestion({ birthday, status: 3 })
    quoteMessage.value = res.data || '小憩结束，带着饱满的精力开始新一轮专注吧！'
  } catch (err) {
    quoteMessage.value = '小憩结束，带着饱满的精力开始新一轮专注吧！'
  }
  showOverlay.value = true
  setTimeout(() => {
    showOverlay.value = false
    router.push('/strategy')
  }, 3000)
}

const handleToggleRecords = async () => {
  showRecords.value = !showRecords.value
  if (showRecords.value) {
    try {
      const res = await getTodayRecords()
      if (res && res.data) todayRecords.value = res.data
    } catch (err) { console.error(err) }
  }
}

const clearTimer = () => {
  if (timerInterval) { clearInterval(timerInterval); timerInterval = null }
}

onUnmounted(() => { clearTimer() })
</script>

<style scoped>
.page-wrapper { position: relative; min-height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #FAF9F6; font-family: -apple-system, BlinkMacSystemFont, sans-serif; padding: 24px; }
.break-card { width: 100%; max-width: 440px; background-color: #FFFFFF; padding: 40px 32px; border-radius: 24px; box-shadow: 0 10px 25px -5px rgba(44, 26, 77, 0.05); border: 1px solid rgba(44, 26, 77, 0.04); text-align: center; }
.page-title { font-size: 20px; font-weight: 700; color: #2C1A4D; margin-bottom: 28px; }
.timer-display-box { background-color: #F8F7F4; border-radius: 20px; padding: 40px; margin-bottom: 32px; border: 1px solid rgba(44, 26, 77, 0.03); }
.timer-active { box-shadow: 0 0 15px rgba(224, 159, 83, 0.15); border-color: rgba(224, 159, 83, 0.2); }
.timer-text { font-size: 64px; font-weight: 700; color: #2C1A4D; }
.actions-row { display: flex; gap: 16px; justify-content: center; margin-bottom: 32px; }
.btn-action { flex: 1; max-width: 160px; height: 48px; border-radius: 24px; font-size: 15px; font-weight: 600; cursor: pointer; }
.btn-gold { background: linear-gradient(135deg, #E09F53 0%, #E9B87F 100%); color: #FFFFFF; border: none; }
.btn-gold:disabled { background: #E5E7EB; color: #9CA3AF; cursor: not-allowed; }
.btn-outline { background-color: #FFFFFF; border: 2px solid #2C1A4D; color: #2C1A4D; }
.records-wrapper { border-top: 1px dashed rgba(44, 26, 77, 0.1); padding-top: 20px; text-align: left; }
.btn-toggle-records { background: none; border: none; width: 100%; display: flex; justify-content: space-between; align-items: center; font-size: 14px; font-weight: 700; color: #2C1A4D; cursor: pointer; }
.arrow-icon { font-size: 10px; color: #9CA3AF; transition: transform 0.2s ease; }
.arrow-up { transform: rotate(180deg); }
.records-empty { font-size: 12px; color: #9CA3AF; text-align: center; padding: 16px 0; }
.records-list { list-style: none; padding: 0; max-height: 160px; overflow-y: auto; }
.record-item { display: flex; justify-content: space-between; padding: 8px 12px; margin-bottom: 6px; background-color: rgba(44, 26, 77, 0.02); border-radius: 8px; font-size: 13px; }
.record-status { padding: 2px 8px; border-radius: 12px; font-size: 11px; }
.status-done { background-color: rgba(220, 252, 231, 0.8); color: #16A34A; }
.status-failed { background-color: rgba(254, 226, 226, 0.8); color: #DC2626; }
.quote-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 999; }
.quote-card { width: 85%; max-width: 380px; background-color: #FFFFFF; padding: 32px 24px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15); animation: scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); text-align: center; }
.quote-text { font-size: 17px; color: #2C1A4D; line-height: 1.6; margin: 0; font-weight: 500; }
@keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
