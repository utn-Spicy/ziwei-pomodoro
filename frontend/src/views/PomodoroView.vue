<template>
  <div class="page-wrapper">
    <div class="focus-card">
      <h2 class="page-title">{{ isRunning && !isPaused ? '专注进行中' : '静心专注' }}</h2>

      <div
        class="timer-display-box"
        :class="{ 'timer-breathing': isRunning && !isPaused }"
      >
        <span class="timer-text">{{ formattedTime }}</span>
      </div>

      <div class="controls-area">
        <button
          v-if="!isRunning"
          class="btn-action btn-gold"
          @click="handleStartSession"
        >
          开 始
        </button>

        <div v-else class="timer-operating">
          <button
            class="btn-action"
            :class="isPaused ? 'btn-gold' : 'btn-outline'"
            @mousedown="triggerLongPressStart"
            @mouseup="triggerLongPressEnd"
            @mouseleave="triggerLongPressEnd"
            @touchstart="triggerLongPressStart"
            @touchend="triggerLongPressEnd"
            @click="handleTogglePause"
          >
            {{ isPaused ? '继 续' : '暂 停' }}
          </button>

          <p class="hold-tip" :class="{ 'hold-active': isLongPressing }">
            {{ isLongPressing ? '正在中断...' : '长按「暂停」2 秒以中断专注' }}
          </p>
        </div>
      </div>

      <div class="records-section">
        <h3 class="records-title">今日专注印记</h3>
        <div v-if="todayRecords.length === 0" class="records-empty">
          今日尚未开始计时，静候您的初次专注
        </div>
        <ul v-else class="records-list">
          <li v-for="record in todayRecords" :key="record.id" class="record-item">
            <span class="record-time">专注 {{ record.duration }} 分钟</span>
            <span
              class="record-status"
              :class="record.status === 1 ? 'status-done' : 'status-failed'"
            >
              {{ record.status === 1 ? '已达成' : '已中断' }}
            </span>
          </li>
        </ul>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  startPomodoro,
  completePomodoro,
  interruptPomodoro,
  getRunningPomodoro,
  getTodayRecords,
  getSuggestion
} from '../api/index.js'

const router = useRouter()

const timeLeft = ref(0)
const isRunning = ref(false)
const isPaused = ref(false)
const currentPomodoroId = ref(null)
const todayRecords = ref([])

const showOverlay = ref(false)
const quoteMessage = ref('')

let timerInterval = null
const defaultDuration = history.state?.duration || 25

const isLongPressing = ref(false)
const isLongPressedTriggered = ref(false)
let longPressTimer = null

const formattedTime = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60)
  const seconds = timeLeft.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

onMounted(async () => {
  timeLeft.value = defaultDuration * 60
  let hasRecovered = false

  try {
    const runningRes = await getRunningPomodoro()
    if (runningRes && runningRes.data && runningRes.data.status === 0) {
      const runData = runningRes.data
      currentPomodoroId.value = runData.id
      const startTime = new Date(runData.startTime || runData.createdAt).getTime()
      const totalSeconds = runData.duration * 60
      const elapsedSeconds = Math.floor((Date.now() - startTime) / 1000)

      if (elapsedSeconds < totalSeconds) {
        timeLeft.value = totalSeconds - elapsedSeconds
        startCountdown()
        hasRecovered = true
      } else {
        await completePomodoro(runData.id, runData.duration)
      }
    }
  } catch (err) {
    console.error('检查运行中任务失败：', err)
  }

  if (!hasRecovered && history.state?.duration) {
    handleStartSession()
  }
  loadRecords()
})

onUnmounted(() => {
  stopCountdown()
})

const startCountdown = () => {
  isRunning.value = true
  isPaused.value = false
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    if (timeLeft.value > 0) timeLeft.value--
    else handleComplete(currentPomodoroId.value)
  }, 1000)
}

const stopCountdown = () => {
  if (timerInterval) { clearInterval(timerInterval); timerInterval = null }
}

const handleStartSession = async () => {
  try {
    const res = await startPomodoro(defaultDuration)
    if (res && res.data) {
      currentPomodoroId.value = res.data.id
      timeLeft.value = defaultDuration * 60
      startCountdown()
      loadRecords()
    }
  } catch (err) {
    console.error('开始番茄钟失败：', err)
  }
}

const handleTogglePause = () => {
  if (isLongPressedTriggered.value) {
    isLongPressedTriggered.value = false
    return
  }
  if (isPaused.value) startCountdown()
  else { stopCountdown(); isPaused.value = true }
}

const handleComplete = async (id) => {
  stopCountdown()
  try {
    await completePomodoro(id, defaultDuration)
    await triggerQuote(1, '/break')
  } catch (err) {
    console.error('完成番茄钟异常：', err)
    router.push('/break')
  }
}

const triggerLongPressStart = () => {
  if (!isRunning.value) return
  isLongPressing.value = true
  isLongPressedTriggered.value = false
  longPressTimer = setTimeout(async () => {
    isLongPressedTriggered.value = true
    await handleInterrupt()
  }, 2000)
}

const triggerLongPressEnd = () => {
  isLongPressing.value = false
  if (longPressTimer) { clearTimeout(longPressTimer); longPressTimer = null }
}

const handleInterrupt = async () => {
  stopCountdown()
  triggerLongPressEnd()
  if (!currentPomodoroId.value) return
  try {
    await interruptPomodoro(currentPomodoroId.value)
    await triggerQuote(2, '/strategy')
  } catch (err) {
    console.error('中断异常：', err)
    router.push('/strategy')
  }
}

const triggerQuote = async (status, targetRoute) => {
  const birthday = localStorage.getItem('birthday') || '1995-01-01'
  try {
    const res = await getSuggestion({ birthday, status })
    quoteMessage.value = res.data || (status === 1 ? '专注达成，适当小憩一下吧。' : '中途退离，调整状态再出发。')
  } catch (err) {
    quoteMessage.value = status === 1 ? '专注达成，适当小憩一下吧。' : '中途退离，调整状态再出发。'
  }
  showOverlay.value = true
  setTimeout(() => {
    showOverlay.value = false
    router.push(targetRoute)
  }, 3000)
}

const loadRecords = async () => {
  try {
    const res = await getTodayRecords()
    if (res && res.data) todayRecords.value = res.data
  } catch (err) {
    console.error(err)
  }
}
</script>

<style scoped>
.page-wrapper { position: relative; min-height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #FAF9F6; font-family: -apple-system, BlinkMacSystemFont, sans-serif; padding: 24px; }
.focus-card { width: 100%; max-width: 480px; background-color: #FFFFFF; padding: 40px; border-radius: 24px; box-shadow: 0 10px 25px -5px rgba(44, 26, 77, 0.05); border: 1px solid rgba(44, 26, 77, 0.04); text-align: center; }
.page-title { font-size: 20px; font-weight: 700; color: #2C1A4D; margin-bottom: 28px; }
.timer-display-box { background-color: #F8F7F4; border: 1px solid rgba(224, 159, 83, 0.15); border-radius: 24px; padding: 36px; margin-bottom: 28px; }
.timer-breathing { animation: auraGlow 3s infinite ease-in-out; }
@keyframes auraGlow { 0%, 100% { box-shadow: 0 0 4px 0px rgba(224, 159, 83, 0.1); } 50% { box-shadow: 0 0 20px 4px rgba(224, 159, 83, 0.25); } }
.timer-text { font-size: 64px; font-weight: 800; color: #2C1A4D; }
.controls-area { margin-bottom: 40px; }
.btn-action { width: 100%; max-width: 280px; height: 52px; border-radius: 26px; font-size: 16px; font-weight: 600; cursor: pointer; }
.btn-gold { background: linear-gradient(135deg, #E09F53 0%, #E9B87F 100%); color: #FFFFFF; border: none; }
.btn-outline { background-color: #F9FAFB; border: 2px solid #2C1A4D; color: #2C1A4D; }
.hold-tip { margin-top: 12px; font-size: 12px; color: #9CA3AF; }
.hold-active { color: #DC2626; font-weight: bold; }
.records-section { border-top: 1px dashed rgba(44, 26, 77, 0.1); padding-top: 24px; text-align: left; }
.records-title { font-size: 14px; font-weight: 700; color: #2C1A4D; margin-bottom: 12px; }
.records-empty { font-size: 12px; color: #9CA3AF; text-align: center; }
.records-list { list-style: none; padding: 0; max-height: 180px; overflow-y: auto; }
.record-item { display: flex; justify-content: space-between; padding: 8px 12px; background-color: rgba(44, 26, 77, 0.02); border-radius: 6px; margin-bottom: 6px; font-size: 13px; }
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
