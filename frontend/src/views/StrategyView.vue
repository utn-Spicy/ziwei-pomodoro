<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const rawData = ref(null)
const rawDuration = ref(25)

const strategy = computed(() => {
  const d = rawData.value
  if (!d) return { element: '—', personalityType: '未知', personalityDesc: '暂无数据', recommendDuration: 25, studyAdvice: '' }
  if (typeof d === 'string') return { element: '', personalityType: '命盘策略', personalityDesc: d, recommendDuration: rawDuration.value, studyAdvice: '' }
  return {
    element: d.element,
    personalityType: d.personalityType,
    personalityDesc: d.personalityDesc || d.studyAdvice || '',
    recommendDuration: d.recommendDuration || d.strategy?.recommendDuration || 25,
    studyAdvice: d.studyAdvice || '',
  }
})

onMounted(() => {
  // 优先从路由 state 读取（从登录页过来）
  const state = history.state || {}
  if (state.strategy) {
    rawData.value = state.strategy
    rawDuration.value = state.duration || 25
    // 存入 localStorage，供从休息页返回时使用
    localStorage.setItem('cached_strategy', JSON.stringify(state.strategy))
    localStorage.setItem('cached_duration', String(state.duration || 25))
  } else {
    // 从 localStorage 恢复（从休息页返回）
    const cached = localStorage.getItem('cached_strategy')
    if (cached) {
      rawData.value = JSON.parse(cached)
      rawDuration.value = Number(localStorage.getItem('cached_duration')) || 25
    }
  }
})

function goToPomodoro() {
  router.push({ name: 'pomodoro', state: { duration: strategy.value.recommendDuration } })
}
</script>

<template>
  <div class="page-wrapper">
    <div class="strategy-card">
      <h2 class="page-title">专属专注方案</h2>

      <div class="section-block">
        <div class="section-header">
          <span class="decor-dot"></span>
          <span class="section-label">命盘类型</span>
        </div>
        <div class="fate-box">
          <div class="fate-tag">{{ strategy.personalityType || '未载入命盘' }}</div>
          <p class="fate-intro">{{ strategy.personalityDesc || '暂无命盘解析信息' }}</p>
        </div>
      </div>

      <div class="section-block">
        <div class="section-header">
          <span class="decor-dot"></span>
          <span class="section-label">番茄钟策略</span>
        </div>
        <div class="strategy-box">
          <h3 class="strategy-name">「 {{ strategy.personalityType || '默认' }}专注流 」</h3>

          <div class="time-metrics">
            <div class="metric-item">
              <span class="metric-num">{{ strategy.recommendDuration ?? '--' }}</span>
              <span class="metric-lbl">分钟专注</span>
            </div>
            <div class="metric-divider"></div>
            <div class="metric-item">
              <span class="metric-num">5</span>
              <span class="metric-lbl">分钟休息</span>
            </div>
          </div>

          <p class="strategy-tip">{{ strategy.studyAdvice || '暂无定制专注建议' }}</p>
        </div>
      </div>

      <button class="btn-start" @click="goToPomodoro">开 始</button>
    </div>
  </div>
</template>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FAF9F6;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  padding: 24px;
  box-sizing: border-box;
}
.strategy-card {
  width: 100%;
  max-width: 520px;
  background-color: #FFFFFF;
  padding: 40px;
  border-radius: 24px;
  box-shadow: 0 10px 25px -5px rgba(44, 26, 77, 0.05), 0 8px 10px -6px rgba(44, 26, 77, 0.05);
  border: 1px solid rgba(44, 26, 77, 0.04);
  box-sizing: border-box;
}
.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #2C1A4D;
  margin: 0 0 32px 0;
  text-align: center;
  letter-spacing: 0.05em;
}
.section-block { margin-bottom: 28px; text-align: left; }
.section-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.decor-dot { width: 6px; height: 6px; background-color: #E09F53; border-radius: 50%; }
.section-label { font-size: 13px; font-weight: 600; color: #9CA3AF; text-transform: uppercase; letter-spacing: 0.1em; }
.fate-box { background-color: rgba(44, 26, 77, 0.02); border: 1px solid rgba(44, 26, 77, 0.06); padding: 20px; border-radius: 12px; }
.fate-tag { display: inline-block; background-color: #EAE6F3; color: #2C1A4D; font-weight: 600; font-size: 14px; padding: 6px 16px; border-radius: 20px; margin-bottom: 12px; }
.fate-intro { font-size: 13px; color: #6B7280; line-height: 1.6; margin: 0; }
.strategy-box { background-color: #FAF7F2; border: 1px solid rgba(224, 159, 83, 0.15); padding: 24px; border-radius: 16px; text-align: center; }
.strategy-name { font-size: 18px; font-weight: 700; color: #2C1A4D; margin: 0 0 20px 0; }
.time-metrics { display: flex; align-items: center; justify-content: center; gap: 24px; margin-bottom: 20px; }
.metric-item { display: flex; flex-direction: column; align-items: center; }
.metric-num { font-size: 40px; font-weight: 800; color: #2C1A4D; font-family: 'Helvetica Neue', Arial, sans-serif; line-height: 1; }
.metric-lbl { font-size: 12px; color: #6B7280; margin-top: 6px; }
.metric-divider { width: 1px; height: 36px; background-color: rgba(44, 26, 77, 0.1); }
.strategy-tip { font-size: 13px; color: #6B7280; line-height: 1.6; margin: 0; text-align: left; }
.btn-start { width: 100%; height: 52px; background: linear-gradient(135deg, #E09F53 0%, #E9B87F 100%); color: #FFFFFF; border: none; border-radius: 26px; font-size: 16px; font-weight: 600; cursor: pointer; box-shadow: 0 4px 10px -1px rgba(224, 159, 83, 0.25); transition: all 0.2s ease-in-out; margin-top: 8px; letter-spacing: 0.2em; }
.btn-start:hover { transform: translateY(-1px); box-shadow: 0 6px 14px -1px rgba(224, 159, 83, 0.35); }
.btn-start:active { transform: translateY(1px); filter: brightness(0.95); }
@media (max-width: 480px) { .strategy-card { padding: 24px 20px; } .time-metrics { gap: 16px; } .metric-num { font-size: 32px; } }
</style>
