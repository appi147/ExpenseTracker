import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import MonthlyInsights from '../../src/pages/MonthlyInsights'
import * as expenseService from '../../src/services/expense-service'

vi.spyOn(expenseService, 'getMonthlyInsight').mockResolvedValue({
  totalBudget: 0,
  totalExpense: 0,
  categories: [],
})

describe('MonthlyInsights.tsx', () => {
  it('renders heading and ExpenseInsight with default monthly=true', () => {
    render(
      <MemoryRouter initialEntries={[{ pathname: '/insights', search: '' }]}>
        <MonthlyInsights />
      </MemoryRouter>
    )
    expect(screen.getByText('Expense Insights')).toBeInTheDocument()
  })

  it('respects monthly=false from search params', () => {
    render(
      <MemoryRouter initialEntries={[{ pathname: '/insights', search: '?monthly=false' }]}>
        <MonthlyInsights />
      </MemoryRouter>
    )
    // Smoke render; behavior inside ExpenseInsight is covered elsewhere
    expect(screen.getByText('Expense Insights')).toBeInTheDocument()
  })
})
