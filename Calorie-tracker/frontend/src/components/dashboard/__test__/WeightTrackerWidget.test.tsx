// frontend/src/components/dashboard/__tests__/WeightTrackerWidget.test.tsx
import { render, screen } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import WeightTrackerWidget from '../WeightTrackerWidget'
import { weightApi } from '../../../api/endpoints'

// Мокаем API
vi.mock('../../../api/endpoints', () => ({
  weightApi: {
    getEntries: vi.fn(),
    getStats: vi.fn(),
  }
}))

describe('WeightTrackerWidget', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Мокаем успешные ответы
    ;(weightApi.getEntries as any).mockResolvedValue({ data: [] })
    ;(weightApi.getStats as any).mockResolvedValue({ 
      data: { 
        current_weight: 75.0, 
        min_weight: 70.0, 
        max_weight: 80.0, 
        entries_count: 5 
      } 
    })
  })

  it('отображает заголовок виджета', async () => {
    render(<WeightTrackerWidget />)
    // findByText используется, так как данные загружаются асинхронно
    expect(await screen.findByText('Отслеживание веса')).toBeInTheDocument()
  })

  it('отображает кнопку добавления (плюсик)', async () => {
    render(<WeightTrackerWidget />)
    // Проверяем наличие кнопки с иконкой плюса (или title)
    const addButton = await screen.findByTitle('Добавить запись')
    expect(addButton).toBeInTheDocument()
  })
})