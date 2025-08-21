import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import BudgetProgress from '../../../src/components/insights/BudgetProgress'

describe('components/insights/BudgetProgress', () => {
  it('shows remaining budget when under budget', () => {
    render(<BudgetProgress totalExpense={50} monthlyBudget={100} />)
    expect(screen.getByText('Remaining Budget')).toBeInTheDocument()
    expect(screen.getAllByText('₹50.00').length).toBeGreaterThan(0)
  })

  it('shows over budget message when exceeded', () => {
    render(<BudgetProgress totalExpense={150} monthlyBudget={100} />)
    expect(screen.getByText('Over Budget')).toBeInTheDocument()
    expect(screen.getByText('₹50.00')).toBeInTheDocument()
  })

  it('handles no budget defined', () => {
    render(<BudgetProgress totalExpense={0} monthlyBudget={0} />)
    expect(screen.getByText('No budget defined')).toBeInTheDocument()
  })
})
