// frontend/src/components/ui/__test__/DateSlider.test.tsx
import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import DateSlider from '../DateSlider'

describe('DateSlider', () => {
  it('отображает 7 дат', () => {
    render(<DateSlider selectedDate="2024-01-15" onDateChange={vi.fn()} />)
    const buttons = screen.getAllByRole('button')
    expect(buttons).toHaveLength(7)
  })

  it('выделяет выбранную дату через data-selected', () => {
    render(<DateSlider selectedDate="2024-01-15" onDateChange={vi.fn()} />)
    const selectedButton = screen.getByText('15').closest('button')
    expect(selectedButton).toHaveAttribute('data-selected', 'true')
  })

  it('вызывает onDateChange при клике', () => {
    const onDateChange = vi.fn()
    render(<DateSlider selectedDate="2024-01-15" onDateChange={onDateChange} />)
    
    const nextDayButton = screen.getByText('16').closest('button')
    fireEvent.click(nextDayButton!)
    
    expect(onDateChange).toHaveBeenCalledWith('2024-01-16')
  })
})