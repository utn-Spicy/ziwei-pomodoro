import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export const createChart = (params) =>
  api.get('/chart/create', { params })

export const analyzeChart = (params) =>
  api.get('/chart/analyze', { params })

export const startPomodoro = (duration) =>
  api.post('/pomodoro/start', null, { params: { duration } })

export const completePomodoro = (id, actualDuration) =>
  api.put(`/pomodoro/${id}/complete`, null, { params: { actualDuration } })

export const interruptPomodoro = (id) =>
  api.put(`/pomodoro/${id}/interrupt`)

export const getRunningPomodoro = () =>
  api.get('/pomodoro/running')

export const getTodayRecords = () =>
  api.get('/pomodoro/today')

export const getSuggestion = (params) =>
  api.get('/suggestion', { params })
