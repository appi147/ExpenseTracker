import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import MonthlyExpenseTrends from '../../src/pages/MonthlyTrendsPage'

vi.mock('html-to-image', () => ({ toPng: vi.fn().mockResolvedValue('data:image/png;base64,abc') }))
vi.mock('../../src/services/insight-service', () => ({
  getMonthlyTrends: vi.fn().mockResolvedValue([]),
}))

describe('MonthlyTrendsPage.tsx', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders empty state and disables export when no data', async () => {
    render(<MonthlyExpenseTrends />)
    expect(await screen.findByText('No expense trends available.')).toBeInTheDocument()
    const btn = screen.getByRole('button', { name: /export png/i })
    expect(btn).toBeDisabled()
  })
})
